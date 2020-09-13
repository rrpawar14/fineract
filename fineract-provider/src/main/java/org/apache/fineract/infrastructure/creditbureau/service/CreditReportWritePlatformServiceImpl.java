/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.infrastructure.creditbureau.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.persistence.PersistenceException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.creditbureau.data.CreditBureaNames;
import org.apache.fineract.infrastructure.creditbureau.data.CreditReportData;
import org.apache.fineract.infrastructure.creditbureau.domain.CreditBureau;
import org.apache.fineract.infrastructure.creditbureau.domain.CreditBureauConfiguration;
import org.apache.fineract.infrastructure.creditbureau.domain.CreditBureauConfigurationRepositoryWrapper;
import org.apache.fineract.infrastructure.creditbureau.domain.CreditBureauLoanProductMapping;
import org.apache.fineract.infrastructure.creditbureau.domain.CreditBureauLoanProductMappingRepository;
import org.apache.fineract.infrastructure.creditbureau.domain.CreditBureauRepository;
import org.apache.fineract.infrastructure.creditbureau.domain.CreditBureauToken;
import org.apache.fineract.infrastructure.creditbureau.domain.TokenRepositoryWrapper;
import org.apache.fineract.infrastructure.creditbureau.serialization.CreditBureauTokenCommandFromApiJsonDeserializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreditReportWritePlatformServiceImpl implements CreditReportWritePlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final FromJsonHelper fromApiJsonHelper;
    private final TokenRepositoryWrapper tokenRepository;
    private final CreditBureauConfigurationRepositoryWrapper configDataRepository;
    private final CreditBureauTokenCommandFromApiJsonDeserializer fromApiJsonDeserializer;
    private final CreditBureauLoanProductMappingRepository loanProductMappingRepository;
    private final CreditBureauRepository creditBureauRepository;
    private String creditBureauID = null;

    @Autowired
    public CreditReportWritePlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final FromJsonHelper fromApiJsonHelper, final TokenRepositoryWrapper tokenRepository,
            final CreditBureauConfigurationRepositoryWrapper configDataRepository,
            final CreditBureauTokenCommandFromApiJsonDeserializer fromApiJsonDeserializer,
            final CreditBureauLoanProductMappingRepository loanProductMappingRepository,
            final CreditBureauRepository creditBureauRepository) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.tokenRepository = tokenRepository;
        this.configDataRepository = configDataRepository;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.loanProductMappingRepository = loanProductMappingRepository;
        this.creditBureauRepository = creditBureauRepository;
    }

    private static final Logger LOG = LoggerFactory.getLogger(CreditReportWritePlatformServiceImpl.class);

    public String httpConnectionMethod(String process, String nrcID, String userName, String password, String subscriptionKey,
            String subscriptionId, String url, String token, Long uniqueID) {

        String result = null;

        try {
            String readLine = null;
            String post_params = null;
            HttpURLConnection postConnection = null;
            StringBuilder response = new StringBuilder();

            if (process.equals("token")) {
                LOG.info("-----creating new token-----");
                post_params = "" + "BODY=x-www-form-urlencoded&\r" + "grant_type=password&\r" + "userName=" + userName + "&\r" + "password="
                        + password + "&\r";

                URL tokenurl = new URL(url);
                readLine = null;
                postConnection = (HttpURLConnection) tokenurl.openConnection();
                postConnection.setRequestMethod("POST");

            } else if (process.equals("NRC")) {
                // Search Methods
                LOG.info("-----Search by NRC-----");
                post_params = "BODY=x-www-form-urlencoded&nrc=" + nrcID + "&";

                URL NrcURL = new URL(url + nrcID);
                postConnection = (HttpURLConnection) NrcURL.openConnection();
                postConnection.setRequestMethod("POST");
            } else if (process.equals("CREDITREPORT")) {
                LOG.info("-----Search by CREDIT_REPORT-----");
                URL CreditReportURL = new URL(url + uniqueID);
                postConnection = (HttpURLConnection) CreditReportURL.openConnection();
                postConnection.setRequestMethod("GET");

            }

            // common set of headers
            postConnection.setRequestProperty("mcix-subscription-key", subscriptionKey);
            postConnection.setRequestProperty("mcix-subscription-id", subscriptionId);
            postConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // token header not used when creating token i.e. when token will be null
            if (token != null) {
                postConnection.setRequestProperty("Authorization", "Bearer " + token);
            }

            // this set of code only required for fetching uniqueID from Nrc fetched data (POST-SimpleSearch)
            if (process.equals("NRC") || process.equals("token")) {
                LOG.info("-----NRC & CREDITREPORT -----");
                postConnection.setDoOutput(true);
                OutputStream os = postConnection.getOutputStream();
                os.write(post_params.getBytes(StandardCharsets.UTF_8));
                os.flush();
                os.close();

            }

            // common part of code in http connection method
            int responseCode = postConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                response = new StringBuilder();

                LOG.info("----- RESPONSE OK-----");
                BufferedReader in = new BufferedReader(new InputStreamReader(postConnection.getInputStream(), StandardCharsets.UTF_8));
                while ((readLine = in.readLine()) != null) {
                    response.append(readLine);
                }
                in.close();
                result = response.toString();
                LOG.info("----- result-----{}", result);
                // results = result;
            } else {
                LOG.info("Request is Invalid");
            }

        } catch (IOException e) {
            LOG.error("Error occured.", e);
        }
        return result;

    }

    @SuppressWarnings({ "CatchAndPrintStackTrace", "DefaultCharset" })
    @Override
    @Transactional
    public CommandProcessingResult getCreditReport(JsonCommand command) {
        // TODO Auto-generated method stub

        try {
            LOG.info("----command --{}", command);
            this.context.authenticatedUser();
            // creditBureauID = null;
            String loanProductID = null;

            try {
                creditBureauID = command.stringValueOfParameterNamed("creditBureauID");
                loanProductID = command.stringValueOfParameterNamed("loanProductID");
            } catch (NullPointerException exception) {
                LOG.error("Caught Null Pointer Exception {}", exception);
            }

            LOG.info("-----params ID-----{} NRC : {}", creditBureauID, loanProductID);

            // Map<String, Object> params = params.compute(key, remappingFunction)

            // Map<String, Object> params = null;

            // Map<String, Object> params = command.mapObjectValueOfParameterNamed("param");

            String creditBureauName = getCreditBureau(loanProductID, creditBureauID);

            // CreditReportData creditReportData = new CreditReportData(name, gender, address, creditScore,
            // borrowerInfo, activeLoans, paidLoans);

            CreditReportData reportobj = null;
            // LOG.info("-----params 3-----{}", params);
            // once we have creditBureaName, invoke appropriate credit Bureau and get CreditReport
            if (creditBureauName.equals(CreditBureaNames.THITSAWORKS.toString())) {
                reportobj = getCreditReportFromThitsaWorks(command);
                // reportobj = creditReportData;
                LOG.info("creditReportData {}", reportobj);

                // CreditReportData creditReportDatas = new CreditReportData.getReport();

                // LOG.info("instance {}",
                // creditReportData.instance(name, gender, address, creditScore, borrowerInfo, activeLoans, paidLoans));
                // LOG.info("getCreditReport {}",
                // creditReportData.getReport());

                // reportobj=
                // LOG.info("creditReportDatas {}",
                // creditReportDatas);
            }
            return new CommandProcessingResultBuilder().withCreditReport(reportobj).build();
        } catch (final DataIntegrityViolationException dve) {
            handleTokenDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return CommandProcessingResult.empty();
        } catch (final PersistenceException ee) {
            Throwable throwable = ExceptionUtils.getRootCause(ee.getCause());
            handleTokenDataIntegrityIssues(command, throwable, ee);
            return CommandProcessingResult.empty();
        }

    }

    @SuppressWarnings("StringSplitter")
    public CreditReportData getCreditReportFromThitsaWorks(final JsonCommand command) {
        String Name;
        String Gender;
        String nrcId = command.stringValueOfParameterNamed("NRC");
        String Address = null;
        String userName = "";
        String password = "";
        String subscriptionId = "";
        String subscriptionKey = "";
        String token = null;
        String process = null;
        String result = null;
        Long uniqueID = 0L;

        this.context.authenticatedUser();

        Integer Id = Integer.parseInt(creditBureauID);
        CreditBureauConfiguration SubscriptionId = this.configDataRepository.getCreditBureauConfigData(Id, "SubscriptionId");
        CreditBureauConfiguration SubscriptionKey = this.configDataRepository.getCreditBureauConfigData(Id, "SubscriptionKey");
        CreditBureauConfiguration UserName = this.configDataRepository.getCreditBureauConfigData(Id, "Username");
        CreditBureauConfiguration Password = this.configDataRepository.getCreditBureauConfigData(Id, "Password");

        if (SubscriptionId != null || SubscriptionKey != null || UserName != null || Password != null) {

            /*
             * userName = credittokendata.getUserName(); password = credittokendata.getPassword(); subscriptionId =
             * credittokendata.getSubscriptionId(); subscriptionKey = credittokendata.getSubscriptionKey();
             */
            subscriptionId = SubscriptionId.getValue();
            subscriptionKey = SubscriptionKey.getValue();
            userName = UserName.getValue();
            password = Password.getValue();
            LOG.info("-----SubscriptionId----{} SubscriptionKey: {} userName: {} password: {}", subscriptionId, subscriptionKey, userName,
                    password);
            // validation required, incase when configuration is not stored in database while fetching data.
        } else {
            LOG.info("-----CreditBureauTokenCredential is empty----");
            String configJson = "{ 'userName':'" + userName + "','password':'" + password + "','subscriptionId':'" + subscriptionId
                    + "','subscriptionKey':'" + subscriptionKey + "'}";
            LOG.info("-----SubscriptionId----{} SubscriptionKey: {} userName: {} password: {}", subscriptionId, subscriptionKey, userName,
                    password);
            this.fromApiJsonDeserializer.validateForUsingTokenConfig(configJson);
        }

        CreditBureauToken creditbureautoken = this.tokenRepository.getToken();
        LOG.info("creditbureautoken : {} ", creditbureautoken);

        // check the expiry date of the previous token.
        if (creditbureautoken != null) {
            Date current = new Date();
            Date getExpiryDate = creditbureautoken.getTokenExpiryDate();

            LOG.info("current date : {} ", current);
            LOG.info("getExpiryDate : {} ", getExpiryDate);

            if (getExpiryDate.before(current)) {
                LOG.info("The token is expired");
                this.tokenRepository.delete(creditbureautoken);
                creditbureautoken = null;
            }
        }
        // storing token if it is valid token(not expired)
        if (creditbureautoken != null) {
            token = creditbureautoken.getCurrentToken();
        }

        // if token is not available or previous token is expired then create a new token
        if (creditbureautoken == null) {

            // using common http connection method for creating token
            // String process, String nrcID, String userName, String password
            process = "token";

            CreditBureauConfiguration tokenURL = this.configDataRepository.getCreditBureauConfigData(Id, "tokenurl");
            String url = tokenURL.getValue();
            // try {
            result = this.httpConnectionMethod(process, nrcId, userName, password, subscriptionKey, subscriptionId, url, token, uniqueID);
            // } catch (java.lang.NullPointerException exception) {
            // LOG.error("The expirydate {}", exception);
            // }

            JsonObject tokenObject = JsonParser.parseString(result).getAsJsonObject();
            String expiresextra = tokenObject.get(".expires").toString();
            String expires = expiresextra.substring(1, expiresextra.length() - 1);
            SimpleDateFormat dateformat = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss zzz", Locale.ENGLISH);
            try {
                Date getExpiryDate = dateformat.parse(expires);
                LOG.info("The expirydate {}", getExpiryDate);
            } catch (ParseException Ex) {
                LOG.error("Error occured.", Ex);
            }

            // created token will be storing it into database
            final CommandWrapper wrapper = new CommandWrapperBuilder().withJson(result).build();
            final String json = wrapper.getJson();
            result = null;
            JsonCommand apicommand = null;
            boolean isApprovedByChecker = false;
            final JsonElement parsedCommand = this.fromApiJsonHelper.parse(json);

            apicommand = JsonCommand.from(json, parsedCommand, this.fromApiJsonHelper, wrapper.getEntityName(), wrapper.getEntityId(),
                    wrapper.getSubentityId(), wrapper.getGroupId(), wrapper.getClientId(), wrapper.getLoanId(), wrapper.getSavingsId(),
                    wrapper.getTransactionId(), wrapper.getHref(), wrapper.getProductId(), wrapper.getCreditBureauId(),
                    wrapper.getOrganisationCreditBureauId());

            this.fromApiJsonDeserializer.validateForCreate(apicommand.json());

            final CreditBureauToken generatedtoken = CreditBureauToken.fromJson(apicommand);

            final CreditBureauToken credittoken = this.tokenRepository.getToken();
            if (credittoken != null) {
                // String fetoken = credittoken.getToken();
                this.tokenRepository.delete(credittoken);
            }

            // saved new token
            this.tokenRepository.save(generatedtoken);

            // fetched new token
            creditbureautoken = this.tokenRepository.getToken();
            token = creditbureautoken.getCurrentToken();

            // at this stage token is available for all cases i.e.(deleted expired token and saved new token)
        }

        // will use only "NRC" part of code from common http method to get data based on nrc
        process = "NRC";
        CreditBureauConfiguration SearchURL = this.configDataRepository.getCreditBureauConfigData(Id, "searchurl");
        String url = SearchURL.getValue();
        result = this.httpConnectionMethod(process, nrcId, userName, password, subscriptionKey, subscriptionId, url, token, uniqueID);

        // after fetching the data from httpconnection it will be come back here for fetching UniqueID from data
        if (process.equals("NRC")) {

            // to fetch the Unique ID from Result
            JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();
            JsonArray jArray = jsonObject.getAsJsonArray("Data");
            JsonObject jobject = jArray.get(0).getAsJsonObject();
            String uniqueIdString = jobject.get("UniqueID").toString();

            // cleaned the uniqueID value. Example id: "123" to 123
            String TrimUniqueId = uniqueIdString.substring(1, uniqueIdString.length() - 1);

            // unique ID is stored
            uniqueID = Long.parseLong(TrimUniqueId);

            // will use "CREDITREPORT" part of code from common http method to fetch creditreport based on UniqueID
            process = "CREDITREPORT";
            CreditBureauConfiguration creditReportURL = this.configDataRepository.getCreditBureauConfigData(Id, "creditreporturl");
            url = creditReportURL.getValue();
            result = this.httpConnectionMethod(process, nrcId, userName, password, subscriptionKey, subscriptionId, url, token, uniqueID);

            LOG.info("result : {} ", result);

        }

        // after fetching the data from httpconnection it will be come back here to assign data(result) to generic
        // creditreportdata object

        // results

        JsonObject reportObject = JsonParser.parseString(result).getAsJsonObject();

        // LOG.info("result_jsonObject : {} ", result);

        JsonObject borrowerInfos = null;
        String borrowerInfo = null;
        String CreditScore = null;
        String ActiveLoans = null;
        String PaidLoans = null;

        // Credit Reports Stored into Generic CreditReportData
        JsonObject data = null;
        JsonElement element = reportObject.get("Data");

        LOG.info("result_data : {} ", element);

        if (!(element instanceof JsonNull)) { // NOTE : "element instanceof JsonNull" is for handling empty values (and
                                              // assigning null) while fetching data from results
            data = (JsonObject) element;
        }

        borrowerInfo = null;
        element = data.get("BorrowerInfo");
        if (!(element instanceof JsonNull)) {
            borrowerInfos = (JsonObject) element;
            // LOG.info("borrowerInfo : {} ", borrowerInfos);
            Gson gson = new Gson();

            borrowerInfo = gson.toJson(borrowerInfos);
        }

        Name = borrowerInfos.get("Name").toString();
        Gender = borrowerInfos.get("Gender").toString();
        Address = borrowerInfos.get("Address").toString();

        element = data.get("CreditScore");
        if (!(element instanceof JsonNull)) {
            JsonObject Score = (JsonObject) element;
            // LOG.info("CreditScore : {} ", CreditScore);
            Gson gson = new Gson();

            CreditScore = gson.toJson(Score);
        }

        element = data.get("ActiveLoans");
        if (!(element instanceof JsonNull)) {
            JsonArray ActiveLoan = (JsonArray) element;
            // LOG.info("ActiveLoans : {} ", ActiveLoans);
            Gson gson = new Gson();

            ActiveLoans = gson.toJson(ActiveLoan);
        }

        element = data.get("WriteOffLoans");
        if (!(element instanceof JsonNull)) {
            JsonArray PaidLoan = (JsonArray) element;
            // LOG.info("PaidLoans : {} ", PaidLoans);

            Gson gson = new Gson();
            PaidLoans = gson.toJson(PaidLoan);
        }

        return CreditReportData.instance(Name, Gender, Address, CreditScore, borrowerInfo, ActiveLoans, PaidLoans);
    }

    public String getCreditBureau(final String ProductID, final String CBID) {
        String creditBureauName = null;

        String PID = "";
        PID = ProductID;
        LOG.info("-----------getCreditBureau-------------");
        LOG.info("-----------ProductID {}", ProductID);
        // loanProductID can be passed from LoanApplicationScreen

        if (CBID != null) {

            // this methodology can be used from Client Screen
            LOG.info("-----------CBID {}", CBID);

            Long creditBureauID = Long.parseLong(CBID);
            LOG.info("----------- Long CBID {}", CBID);
            CreditBureau creditBureau = this.creditBureauRepository.findById(creditBureauID).get();
            LOG.info("creditBureau : {}", creditBureau);
            if (creditBureau == null) {
                // throw exception
            }

            creditBureauName = creditBureau.getName();
            LOG.info("Credit Bureau Name from CBID {}", creditBureauName);
        } else { // invoke some readplatform service to get credit bureau from loan product.
            if (PID.equals("")) {
                LOG.info("throwing error for credit bureau Id and loan product Id is empty");
            }
            Long loanProductID = Long.parseLong(PID);
            CreditBureauLoanProductMapping mapping = this.loanProductMappingRepository.findOneByLoanProductId(loanProductID);

            if (mapping == null) {
                LOG.info("-----------mapping is null-------------");// throw exception and indicate that loanProduct is
                                                                    // not configured for credit check }
            }

            creditBureauName = mapping.getOrganisationCreditbureau().getCreditBureau().getName();
            LOG.info("-----------creditBureauName by Loan Product-------------{}", creditBureauName);
        }

        return creditBureauName;
    }

    private void handleTokenDataIntegrityIssues(final JsonCommand command, final Throwable realCause, final Exception dve) {
        /*
         * if (realCause.getMessage().contains("userNames")) { final String username =
         * command.stringValueOfParameterNamed("username"); throw new
         * PlatformDataIntegrityException("error.msg.token.duplicate.username", "A token with username '" + username +
         * "' already exists", "username", username); }
         */

        LOG.error("Error occured.", dve);
        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());

    }

}
