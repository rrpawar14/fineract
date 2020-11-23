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
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.creditbureau.data.CreditBureauConfigurations;
import org.apache.fineract.infrastructure.creditbureau.data.CreditBureauReportData;
import org.apache.fineract.infrastructure.creditbureau.domain.CreditBureauConfiguration;
import org.apache.fineract.infrastructure.creditbureau.domain.CreditBureauConfigurationRepository;
import org.apache.fineract.infrastructure.creditbureau.domain.CreditBureauConfigurationRepositoryWrapper;
import org.apache.fineract.infrastructure.creditbureau.domain.CreditBureauToken;
import org.apache.fineract.infrastructure.creditbureau.domain.CreditReportRepository;
import org.apache.fineract.infrastructure.creditbureau.domain.TokenRepositoryWrapper;
import org.apache.fineract.infrastructure.creditbureau.serialization.CreditBureauTokenCommandFromApiJsonDeserializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
public class ThitsaWorksCreditBureauIntegrationWritePlatformServiceImpl implements ThitsaWorksCreditBureauIntegrationWritePlatformService {

    private final PlatformSecurityContext context;
    private final FromJsonHelper fromApiJsonHelper;
    private final TokenRepositoryWrapper tokenRepositoryWrapper;
    private final CreditBureauConfigurationRepositoryWrapper configDataRepository;
    private final CreditBureauTokenCommandFromApiJsonDeserializer fromApiJsonDeserializer;

    @Autowired
    public ThitsaWorksCreditBureauIntegrationWritePlatformServiceImpl(final PlatformSecurityContext context,
            final FromJsonHelper fromApiJsonHelper, final TokenRepositoryWrapper tokenRepositoryWrapper,
            final CreditBureauConfigurationRepositoryWrapper configDataRepository,
            final CreditBureauConfigurationRepository configurationDataRepository,
            final CreditBureauTokenCommandFromApiJsonDeserializer fromApiJsonDeserializer,
            final CreditReportRepository creditReportRepository) {
        this.context = context;
        this.tokenRepositoryWrapper = tokenRepositoryWrapper;
        this.configDataRepository = configDataRepository;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
    }

    private static final Logger LOG = LoggerFactory.getLogger(ThitsaWorksCreditBureauIntegrationWritePlatformServiceImpl.class);

    @Transactional
    @Override
    @SuppressWarnings("deprecation")
    public String okHttpConnectionMethod(String userName, String password, String subscriptionKey, String subscriptionId, String url,
            String token, File report, Long uniqueId, String nrcId, String process) {

        LOG.info("okHttpConnectionMethod");
        //
        String result = null;
        RequestBody body = null;
        OkHttpClient client = new OkHttpClient();

        if (process.equals("UploadCreditReport")) {

            final MediaType JSON = MediaType.parse("application/formdata");
            String json = "BODY=formdata&" + report + "&" + "userName=" + userName + "&";
            body = RequestBody.create(JSON, json);
        } else if (process.equals("token")) {

            final MediaType JSON = MediaType.parse("application/x-www-form-urlencoded");
            String json = "" + "BODY=x-www-form-urlencoded&\r" + "grant_type=password&\r" + "userName=" + userName + "&\r" + "password="
                    + password + "&\r";
            body = RequestBody.create(JSON, json);

        } else if (process.equals("NRC")) {

            final MediaType JSON = MediaType.parse("application/x-www-form-urlencoded");
            String json = "BODY=x-www-form-urlencoded&nrc=" + nrcId + "&";
            body = RequestBody.create(JSON, json);

        }

        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        String urlokhttp = urlBuilder.build().toString();
        Request request = null;
        if (token == null) {
            LOG.info("request token null");
            request = new Request.Builder().header("mcix-subscription-key", subscriptionKey).header("mcix-subscription-id", subscriptionId)
                    .header("Content-Type", "application/x-www-form-urlencoded").url(urlokhttp).post(body).build();
        }

        if (token != null) {

            if (process.equals("CreditReport")) { // GET method for fetching credit report
                LOG.info("request credit report");
                request = new Request.Builder().header("mcix-subscription-key", subscriptionKey)
                        .header("mcix-subscription-id", subscriptionId).header("Content-Type", "application/x-www-form-urlencoded")
                        .header("Authorization", "Bearer " + token).url(urlokhttp).get().build();
            } else { // POST method
                LOG.info("request post method");
                request = new Request.Builder().header("mcix-subscription-key", subscriptionKey)
                        .header("mcix-subscription-id", subscriptionId).header("Content-Type", "application/x-www-form-urlencoded")
                        .header("Authorization", "Bearer " + token).url(urlokhttp).post(body).build();
            }
        }

        Response response;
        Integer responseCode = 0;
        try {
            response = client.newCall(request).execute();
            responseCode = response.code();
            result = response.body().string();
        } catch (IOException e) {

            LOG.error("error.", e);
        }

        if (responseCode != HttpURLConnection.HTTP_OK) {
            LOG.info("----- RESPONSE NOT OK-----");
            this.httpResponse(responseCode);
        }
        LOG.error("Result.{}", result);

        return result;

    }

    public void httpResponse(Integer responseCode) {
        LOG.error("httpResponse.");
        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            LOG.info("-----IP FORBIDDEN-----");
            String httpResponse = "HTTP_UNAUTHORIZED";
            this.handleAPIIntegrityIssues(httpResponse);

        } else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
            LOG.info("-----IP FORBIDDEN-----");
            String httpResponse = "HTTP_FORBIDDEN";
            this.handleAPIIntegrityIssues(httpResponse);

        }
        if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
            LOG.info("-----HTTP_INTERNAL_ERROR-----");
            String httpResponse = "HTTP_INTERNAL_ERROR";
            this.handleAPIIntegrityIssues(httpResponse);

        } else {
            LOG.info("Request is Invalid");
        }

    }

    @Transactional
    @Override
    public CreditBureauReportData getCreditReportFromThitsaWorks(final JsonCommand command) {

        this.context.authenticatedUser();
        String nrcId = command.stringValueOfParameterNamed("NRC");
        String bureauID = command.stringValueOfParameterNamed("creditBureauID");
        Integer creditBureauId = Integer.parseInt(bureauID);
        LOG.info("nrcId: {} creditBureauId: {} ", nrcId, creditBureauId);

        String token = null;

        String userName = getCreditBureauConfiguration(creditBureauId, CreditBureauConfigurations.userName.toString());
        String password = getCreditBureauConfiguration(creditBureauId, CreditBureauConfigurations.password.toString());
        String subscriptionId = getCreditBureauConfiguration(creditBureauId, CreditBureauConfigurations.subscriptionId.toString());
        String subscriptionKey = getCreditBureauConfiguration(creditBureauId, CreditBureauConfigurations.subscriptionKey.toString());

        CreditBureauToken creditbureautoken = createToken(creditBureauId.longValue());
        token = creditbureautoken.getCurrentToken();

        LOG.info("token processed done {}", token);
        // will use only "NRC" part of code from common http method to get data based on nrc
        String process = "NRC";
        CreditBureauConfiguration searchURL = this.configDataRepository.getCreditBureauConfigData(creditBureauId, "searchurl");
        String url = searchURL.getValue();
        String nrcUrl = url + nrcId;
        LOG.info("searchURL: {} ", nrcUrl);

        String searchResult = this.okHttpConnectionMethod(userName, password, subscriptionKey, subscriptionId, nrcUrl, token, null, 0L,
                nrcId, process);

        if (process.equals("NRC")) {
            Long uniqueID = this.extractUniqueId(searchResult);
            LOG.info("uniqueID : {} ", uniqueID);
            process = "CreditReport";
            CreditBureauConfiguration creditReportURL = this.configDataRepository.getCreditBureauConfigData(creditBureauId,
                    "creditReporturl");
            url = creditReportURL.getValue();
            String creditReportUrl = url + uniqueID;
            LOG.info("creditReporturl: {} ", creditReportUrl);

            searchResult = this.okHttpConnectionMethod(userName, password, subscriptionKey, subscriptionId, creditReportUrl, token, null,
                    uniqueID, null, process);

        }

        // after getting the result(creditreport) from httpconnection-response it will assign creditreport to generic
        // creditreportdata object

        JsonObject reportObject = JsonParser.parseString(searchResult).getAsJsonObject();
        LOG.info("CreditReport reportObject : {} ", reportObject);

        // Credit Reports Stored into Generic CreditReportData
        JsonObject jsonData = null;
        JsonElement element = reportObject.get("Data");
        LOG.info("CreditReport Data : {} ", element);

        if (!(element instanceof JsonNull)) { // NOTE : "element instanceof JsonNull" is for handling empty values (and
                                              // assigning null) while fetching data from results
            jsonData = (JsonObject) element;
            LOG.info("CreditReport jsonData : {} ", jsonData);
        }

        JsonObject borrowerInfos = null;
        String borrowerInfo = null;

        element = jsonData.get("BorrowerInfo");
        LOG.info("CreditReport BorrowerInfo : {} ", element);
        if (!(element instanceof JsonNull)) {
            borrowerInfos = (JsonObject) element;
            Gson gson = new Gson();
            borrowerInfo = gson.toJson(borrowerInfos);
        }
        LOG.info("borrowerInfo : {} ", borrowerInfo);

        String Name = borrowerInfos.get("Name").toString();
        String Gender = borrowerInfos.get("Gender").toString();
        String Address = borrowerInfos.get("Address").toString();
        LOG.info("Name : {} Gender: {} Address: {}", Name, Gender, Address);

        String creditScore = "CreditScore";
        creditScore = getJsonObjectToString(creditScore, element, jsonData);
        LOG.info("creditScore : {}", creditScore);

        String activeLoans = "ActiveLoans";
        activeLoans = getJsonArrayToString(activeLoans, element, jsonData);
        LOG.info("activeLoans : {}", activeLoans);

        String writeOffLoans = "WriteOffLoans";
        writeOffLoans = getJsonArrayToString(writeOffLoans, element, jsonData);
        LOG.info("writeOffLoans : {}", writeOffLoans);

        return CreditBureauReportData.instance(Name, Gender, Address, creditScore, borrowerInfo, activeLoans, writeOffLoans);
    }

    @Override
    @Transactional
    public String addCreditReport(File report, Long bureauId) {

        Integer creditBureauId = bureauId.intValue();

        String userName = this.getCreditBureauConfiguration(creditBureauId, CreditBureauConfigurations.userName.toString());
        String password = this.getCreditBureauConfiguration(creditBureauId, CreditBureauConfigurations.password.toString());
        String subscriptionId = this.getCreditBureauConfiguration(creditBureauId, CreditBureauConfigurations.subscriptionId.toString());
        String subscriptionKey = this.getCreditBureauConfiguration(creditBureauId, CreditBureauConfigurations.subscriptionKey.toString());

        LOG.info("Credentials are not empty {} {} {} {}", subscriptionId, subscriptionKey, userName, password);

        CreditBureauToken creditbureautoken = this.createToken(creditBureauId.longValue());
        String token = creditbureautoken.getCurrentToken();

        LOG.info("token: {}", token);

        CreditBureauConfiguration addReportURL = this.configDataRepository.getCreditBureauConfigData(creditBureauId, "addCreditReporturl");
        String url = addReportURL.getValue();

        String process = "UploadCreditReport";

        String result = this.okHttpConnectionMethod(userName, password, subscriptionKey, subscriptionId, url, token, report, 0L, null,
                process);

        JsonObject reportObject = JsonParser.parseString(result).getAsJsonObject();
        String responseMessage = reportObject.get("ResponseMessage").getAsString();

        LOG.info("responseMessage: {}", responseMessage);
        return responseMessage;
    }

    private Long extractUniqueId(String jsonResult) {
        LOG.info("extractUniqueId method");

        JsonObject reportObject = JsonParser.parseString(jsonResult).getAsJsonObject();

        JsonElement element = reportObject.get("Data");
        LOG.info("extractUniqueId element {}", element);
        if (element.isJsonNull()) {
            LOG.info("checking jsonnull");
            String ResponseMessage = reportObject.get("ResponseMessage").getAsString();
            LOG.info("ResponseMessage {}", ResponseMessage);
            handleAPIIntegrityIssues(ResponseMessage);
        }

        // to fetch the Unique ID from Result
        JsonObject jsonObject = JsonParser.parseString(jsonResult).getAsJsonObject();
        LOG.info("jsonObject: {}", jsonObject);

        Long uniqueID = 0L;
        try {
            JsonArray dataArray = jsonObject.getAsJsonArray("Data");
            LOG.info("Data: {}", dataArray);

            LOG.info("Dataarray size: {}", dataArray.size());
            if (dataArray.size() == 1) {
                LOG.info("data size = 1 ");
                JsonObject jobject = dataArray.get(0).getAsJsonObject();
                LOG.info("jobject {}", jobject);
                String uniqueIdString = jobject.get("UniqueID").toString();
                String TrimUniqueId = uniqueIdString.substring(1, uniqueIdString.length() - 1);

                uniqueID = Long.parseLong(TrimUniqueId);
                LOG.info("uniqueID : {}", uniqueID);
            } else if (dataArray.size() == 0) {
                LOG.info("data size = 0");
                String ResponseMessage = reportObject.get("ResponseMessage").getAsString();
                LOG.info("ResponseMessage {}", ResponseMessage);
                handleAPIIntegrityIssues(ResponseMessage);
            } else {
                LOG.info("data size > 1 ");
                String nrc = null;

                List<String> arrlist = new ArrayList<String>();

                for (int i = 0; i < dataArray.size(); ++i) {
                    JsonObject data = dataArray.get(i).getAsJsonObject();
                    nrc = data.get("NRC").toString();
                    // str = new String[] { nrc };
                    arrlist.add(nrc);
                    LOG.info("i : {} ", i);
                }
                LOG.info("arrlist : {} ", arrlist);

                String listString = String.join(", ", arrlist);

                LOG.info("listString : {} ", listString);

                this.handleMultipleNRC(listString);
            }

        } catch (IndexOutOfBoundsException e) {
            String ResponseMessage = jsonObject.get("ResponseMessage").getAsString();
            handleAPIIntegrityIssues(ResponseMessage);
        }
        return uniqueID;
    }

    private String getJsonObjectToString(String fetchData, JsonElement element, JsonObject jsonData) {
        LOG.info("getJsonToString method");
        String jsonString = null;
        element = jsonData.get(fetchData);
        if (!(element instanceof JsonNull)) {
            JsonObject fetchJson = (JsonObject) element;
            Gson gson = new Gson();
            jsonString = gson.toJson(fetchJson);
        }
        return jsonString;
    }

    private String getJsonArrayToString(String fetchData, JsonElement element, JsonObject jsonData) {
        LOG.info("getJsonToString method");
        String jsonString = null;
        element = jsonData.get(fetchData);
        if (!(element instanceof JsonNull)) {
            JsonArray fetchJson = (JsonArray) element;
            Gson gson = new Gson();
            jsonString = gson.toJson(fetchJson);
        }
        return jsonString;
    }

    @Transactional
    @Override
    public CreditBureauToken createToken(Long bureauID) {

        LOG.info("token creation");

        CreditBureauToken creditBureauToken = this.tokenRepositoryWrapper.getToken();

        // check the expiry date of the previous token.
        if (creditBureauToken != null) {
            Date current = new Date();
            Date getExpiryDate = creditBureauToken.getTokenExpiryDate();

            if (getExpiryDate.before(current)) {
                this.tokenRepositoryWrapper.delete(creditBureauToken);
                creditBureauToken = null;
            }
        }
        // storing token if it is valid token(not expired)

        if (creditBureauToken != null) {
            creditBureauToken = this.tokenRepositoryWrapper.getToken();
        }

        String userName = getCreditBureauConfiguration(bureauID.intValue(), CreditBureauConfigurations.userName.toString());
        String password = getCreditBureauConfiguration(bureauID.intValue(), CreditBureauConfigurations.password.toString());
        String subscriptionId = getCreditBureauConfiguration(bureauID.intValue(), CreditBureauConfigurations.subscriptionId.toString());
        String subscriptionKey = getCreditBureauConfiguration(bureauID.intValue(), CreditBureauConfigurations.subscriptionKey.toString());

        LOG.info("token Credentials are not empty {} {} {} {}", subscriptionId, subscriptionKey, userName, password);

        if (creditBureauToken == null) {
            CreditBureauConfiguration tokenURL = this.configDataRepository.getCreditBureauConfigData(bureauID.intValue(), "tokenurl");
            String url = tokenURL.getValue();

            String process = "token";
            String nrcId = null;
            Long uniqueID = 0L;
            String result = this.okHttpConnectionMethod(userName, password, subscriptionKey, subscriptionId, url, null, null, uniqueID,
                    nrcId, process);

            // created token will be storing it into database
            final CommandWrapper wrapper = new CommandWrapperBuilder().withJson(result).build();
            final String json = wrapper.getJson();

            JsonCommand apicommand = null;
            final JsonElement parsedCommand = this.fromApiJsonHelper.parse(json);

            apicommand = JsonCommand.from(json, parsedCommand, this.fromApiJsonHelper, wrapper.getEntityName(), wrapper.getEntityId(),
                    wrapper.getSubentityId(), wrapper.getGroupId(), wrapper.getClientId(), wrapper.getLoanId(), wrapper.getSavingsId(),
                    wrapper.getTransactionId(), wrapper.getHref(), wrapper.getProductId(), wrapper.getCreditBureauId(),
                    wrapper.getOrganisationCreditBureauId());

            this.fromApiJsonDeserializer.validateForCreate(apicommand.json());

            final CreditBureauToken generatedtoken = CreditBureauToken.fromJson(apicommand);

            final CreditBureauToken credittoken = this.tokenRepositoryWrapper.getToken();
            if (credittoken != null) {
                this.tokenRepositoryWrapper.delete(credittoken);
            }

            this.tokenRepositoryWrapper.save(generatedtoken);

            creditBureauToken = this.tokenRepositoryWrapper.getToken();

        }

        return creditBureauToken;
    }

    public String getCreditBureauConfiguration(Integer creditBureauId, String configurationParameterName) {

        String creditBureauConfigurationValue = null;

        try {

            CreditBureauConfiguration configurationParameterValue = this.configDataRepository.getCreditBureauConfigData(creditBureauId,
                    configurationParameterName);

            creditBureauConfigurationValue = configurationParameterValue.getValue();
            if (creditBureauConfigurationValue.isEmpty()) {
                throw new PlatformDataIntegrityException("Credit Bureau Configuration is not available",
                        "Credit Bureau Configuration is not available");
            }
        } catch (NullPointerException ex) {
            throw new PlatformDataIntegrityException("Credit Bureau Configuration is not available",
                    "Credit Bureau Configuration is not available" + ex);
        }

        return creditBureauConfigurationValue;
    }

    private void handleAPIIntegrityIssues(String httpResponse) {

        throw new PlatformDataIntegrityException(httpResponse, httpResponse);

    }

    private void handleMultipleNRC(String nrc) {
        String showMessageForMultipleNRC = "Found Multiple NRC's, Enter one from the given:" + nrc + "." + "";

        throw new PlatformDataIntegrityException(showMessageForMultipleNRC, showMessageForMultipleNRC);

    }
}
