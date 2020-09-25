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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.NoSuchElementException;
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
            String subscriptionId, String url, String token, Long uniqueID, File report) {

        String result = null;

        try {
            String readLine = null;
            String post_params = null;
            HttpURLConnection postConnection = null;
            StringBuilder response = new StringBuilder();

            if (process.equals("token")) {
                post_params = "" + "BODY=x-www-form-urlencoded&\r" + "grant_type=password&\r" + "userName=" + userName + "&\r" + "password="
                        + password + "&\r";

                URL tokenurl = new URL(url);
                readLine = null;
                postConnection = (HttpURLConnection) tokenurl.openConnection();
                postConnection.setRequestMethod("POST");

            } else if (process.equals("NRC")) {

                post_params = "BODY=x-www-form-urlencoded&nrc=" + nrcID + "&";
                URL NrcURL = new URL(url + nrcID);
                postConnection = (HttpURLConnection) NrcURL.openConnection();
                postConnection.setRequestMethod("POST");

            } else if (process.equals("CreditReport")) {

                URL CreditReportURL = new URL(url + uniqueID);
                postConnection = (HttpURLConnection) CreditReportURL.openConnection();
                postConnection.setRequestMethod("GET");

            } else if (process.equals("UploadCreditReport")) {

                post_params = "BODY=formdata&" + report + "&" + "userName=" + userName + "&";
                LOG.info("post_params {}", post_params);
                LOG.info("report {}", report);
                URL addCreditReporturl = new URL(url);
                readLine = null;
                postConnection = (HttpURLConnection) addCreditReporturl.openConnection();
                postConnection.setRequestMethod("POST");

            }

            // common set of headers
            postConnection.setRequestProperty("mcix-subscription-key", subscriptionKey);
            postConnection.setRequestProperty("mcix-subscription-id", subscriptionId);
            postConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // token header not used when creating token i.e. when token will be null
            if (token != null) {
                postConnection.setRequestProperty("Authorization", "Bearer " + token);
            }

            // this set is required only for (POST METHOD)- fetching uniqueID from NRC/Creating Token/Add Credit report
            if (process.equals("NRC") || process.equals("token") || process.equals("UploadCreditReport")) {
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

            } else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {

                LOG.info("-----IP FORBIDDEN-----");

                this.handleIPIntegrityIssues();
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

        try {
            this.context.authenticatedUser();

            String creditBureauID = command.stringValueOfParameterNamed("creditBureauID");

            String creditBureauName = getCreditBureau(creditBureauID);

            CreditReportData reportobj = null;

            if (creditBureauName.equals(CreditBureaNames.THITSAWORKS.toString())) {
                reportobj = getCreditReportFromThitsaWorks(command);
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

    private String getCreditBureau(final String creditBureauID) {

        String creditBureauName = null;
        CreditBureau creditBureau = null;
        if (creditBureauID != null) {

            Long bureauID = Long.parseLong(creditBureauID);

            try {
                creditBureau = this.creditBureauRepository.findById(bureauID).get();
            } catch (NoSuchElementException dve) {

                handleCreditBureauNamesIntegrityIssues(dve);

            }

            creditBureauName = creditBureau.getName();
            LOG.info("Credit Bureau Name from CBID {}", creditBureauName);
        }

        return creditBureauName;
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

        String creditBureauID = command.stringValueOfParameterNamed("creditBureauID");
        Integer Id = Integer.parseInt(creditBureauID);
        CreditBureauConfiguration SubscriptionId = this.configDataRepository.getCreditBureauConfigData(Id, "SubscriptionId");
        CreditBureauConfiguration SubscriptionKey = this.configDataRepository.getCreditBureauConfigData(Id, "SubscriptionKey");
        CreditBureauConfiguration UserName = this.configDataRepository.getCreditBureauConfigData(Id, "Username");
        CreditBureauConfiguration Password = this.configDataRepository.getCreditBureauConfigData(Id, "Password");

        if (SubscriptionId != null || SubscriptionKey != null || UserName != null || Password != null) {

            subscriptionId = SubscriptionId.getValue();
            subscriptionKey = SubscriptionKey.getValue();
            userName = UserName.getValue();
            password = Password.getValue();

        } else {

            String configJson = "{ 'userName':'" + userName + "','password':'" + password + "','subscriptionId':'" + subscriptionId
                    + "','subscriptionKey':'" + subscriptionKey + "'}";

            this.fromApiJsonDeserializer.validateForUsingTokenConfig(configJson);
        }

        // if token is not available or previous token is expired then create a new token
        token = createToken(userName, password, subscriptionId, subscriptionKey, Id);

        // will use only "NRC" part of code from common http method to get data based on nrc
        process = "NRC";
        CreditBureauConfiguration SearchURL = this.configDataRepository.getCreditBureauConfigData(Id, "searchurl");
        String url = SearchURL.getValue();
        result = this.httpConnectionMethod(process, nrcId, userName, password, subscriptionKey, subscriptionId, url, token, uniqueID, null);

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

            // will use "CreditReport" part of code from common http method to fetch creditreport based on UniqueID
            process = "CreditReport";
            CreditBureauConfiguration creditReportURL = this.configDataRepository.getCreditBureauConfigData(Id, "creditreporturl");
            url = creditReportURL.getValue();
            result = this.httpConnectionMethod(process, nrcId, userName, password, subscriptionKey, subscriptionId, url, token, uniqueID,
                    null);
        }

        // after getting the result(creditreport) from httpconnection-response it will assign creditreport to generic
        // creditreportdata object

        JsonObject reportObject = JsonParser.parseString(result).getAsJsonObject();

        JsonObject borrowerInfos = null;
        String borrowerInfo = null;
        String CreditScore = null;
        String ActiveLoans = null;
        String PaidLoans = null;

        // Credit Reports Stored into Generic CreditReportData
        JsonObject data = null;
        JsonElement element = reportObject.get("Data");

        if (!(element instanceof JsonNull)) { // NOTE : "element instanceof JsonNull" is for handling empty values (and
                                              // assigning null) while fetching data from results
            data = (JsonObject) element;
        }

        borrowerInfo = null;
        element = data.get("BorrowerInfo");
        if (!(element instanceof JsonNull)) {
            borrowerInfos = (JsonObject) element;

            Gson gson = new Gson();
            borrowerInfo = gson.toJson(borrowerInfos);
        }

        Name = borrowerInfos.get("Name").toString();
        Gender = borrowerInfos.get("Gender").toString();
        Address = borrowerInfos.get("Address").toString();

        element = data.get("CreditScore");
        if (!(element instanceof JsonNull)) {
            JsonObject Score = (JsonObject) element;

            Gson gson = new Gson();
            CreditScore = gson.toJson(Score);
        }

        element = data.get("ActiveLoans");
        if (!(element instanceof JsonNull)) {
            JsonArray ActiveLoan = (JsonArray) element;

            Gson gson = new Gson();
            ActiveLoans = gson.toJson(ActiveLoan);
        }

        element = data.get("WriteOffLoans");
        if (!(element instanceof JsonNull)) {
            JsonArray PaidLoan = (JsonArray) element;

            Gson gson = new Gson();
            PaidLoans = gson.toJson(PaidLoan);
        }

        return CreditReportData.instance(Name, Gender, Address, CreditScore, borrowerInfo, ActiveLoans, PaidLoans);
    }

    @Override
    public String addCreditReport(File report, String bureauId) {

        String result = null;

        String creditBureauName = getCreditBureau(bureauId);
        LOG.info("Credit Bureau Name {}", creditBureauName);
        if (creditBureauName.equals(CreditBureaNames.THITSAWORKS.toString())) {

            String userName = "";
            String password = "";
            String subscriptionId = "";
            String subscriptionKey = "";

            Integer Id = Integer.parseInt(bureauId);

            CreditBureauConfiguration SubscriptionId = this.configDataRepository.getCreditBureauConfigData(Id, "SubscriptionId");
            CreditBureauConfiguration SubscriptionKey = this.configDataRepository.getCreditBureauConfigData(Id, "SubscriptionKey");
            CreditBureauConfiguration UserName = this.configDataRepository.getCreditBureauConfigData(Id, "Username");
            CreditBureauConfiguration Password = this.configDataRepository.getCreditBureauConfigData(Id, "Password");

            if (SubscriptionId != null || SubscriptionKey != null || UserName != null || Password != null) {

                subscriptionId = SubscriptionId.getValue();
                subscriptionKey = SubscriptionKey.getValue();
                userName = UserName.getValue();
                password = Password.getValue();

            } else {

                String configJson = "{ 'userName':'" + userName + "','password':'" + password + "','subscriptionId':'" + subscriptionId
                        + "','subscriptionKey':'" + subscriptionKey + "'}";

                this.fromApiJsonDeserializer.validateForUsingTokenConfig(configJson);
            }

            String token = createToken(userName, password, subscriptionId, subscriptionKey, Id);

            CreditBureauConfiguration addReportURL = this.configDataRepository.getCreditBureauConfigData(Id, "AddCreditReport");
            String url = addReportURL.getValue();

            String process = "UploadCreditReport";
            result = this.httpConnectionMethod(process, null, userName, password, subscriptionKey, subscriptionId, url, token, null,
                    report);

        }

        return result;
    }

    private String createToken(String userName, String password, String subscriptionId, String subscriptionKey, Integer creditBureauId) {

        String nrcId = null;
        String token = null;
        String result = null;
        Long uniqueID = 0L;

        CreditBureauToken creditbureautoken = this.tokenRepository.getToken();

        // check the expiry date of the previous token.
        if (creditbureautoken != null) {
            Date current = new Date();
            Date getExpiryDate = creditbureautoken.getTokenExpiryDate();

            if (getExpiryDate.before(current)) {
                this.tokenRepository.delete(creditbureautoken);
                creditbureautoken = null;
            }
        }
        // storing token if it is valid token(not expired)
        if (creditbureautoken != null) {
            token = creditbureautoken.getCurrentToken();
        }

        if (creditbureautoken == null) {
            CreditBureauConfiguration tokenURL = this.configDataRepository.getCreditBureauConfigData(creditBureauId, "tokenurl");
            String url = tokenURL.getValue();

            String process = "token";
            result = this.httpConnectionMethod(process, nrcId, userName, password, subscriptionKey, subscriptionId, url, token, uniqueID,
                    null);

            // created token will be storing it into database
            final CommandWrapper wrapper = new CommandWrapperBuilder().withJson(result).build();
            final String json = wrapper.getJson();

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
                this.tokenRepository.delete(credittoken);
            }

            this.tokenRepository.save(generatedtoken);

            creditbureautoken = this.tokenRepository.getToken();
            token = creditbureautoken.getCurrentToken();

        }
        return token;
    }

    private void handleTokenDataIntegrityIssues(final JsonCommand command, final Throwable realCause, final Exception dve) {

        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());

    }

    private void handleCreditBureauNamesIntegrityIssues(final Exception dve) {

        throw new PlatformDataIntegrityException("Credit Bureau not Found", "Credit Bureau not Found" + dve.getMessage());

    }

    private void handleIPIntegrityIssues() {

        throw new PlatformDataIntegrityException("IP FORBIDDEN", "IP FORBIDDEN");

    }

}
