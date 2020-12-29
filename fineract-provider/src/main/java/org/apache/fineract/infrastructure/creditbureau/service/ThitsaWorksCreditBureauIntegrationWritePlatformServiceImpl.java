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
import com.sun.jersey.core.header.FormDataContentDisposition;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
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
    private final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
    private final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
            .resource("ThitsaWorksCreditBureauIntegration");

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
            String token, File file, FormDataContentDisposition fileData, Long uniqueId, String nrcId, String process) {

        String reponseMessage = null;
        RequestBody requestBody = null;
        OkHttpClient client = new OkHttpClient();

        String fileName = fileData.getFileName();

        if (process.equals("UploadCreditReport")) {
            requestBody = RequestBody.create(file, MediaType.parse("multipart/form-data"));

            requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", fileName, requestBody)
                    .addFormDataPart("BODY", "formdata").addFormDataPart("userName", userName).build();

        } else if (process.equals("token")) {

            final MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            String jsonBody = "" + "BODY=x-www-form-urlencoded&\r" + "grant_type=password&\r" + "userName=" + userName + "&\r" + "password="
                    + password + "&\r";
            requestBody = RequestBody.create(jsonBody, mediaType);

        } else if (process.equals("NRC")) {

            final MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            String jsonBody = "BODY=x-www-form-urlencoded&nrc=" + nrcId + "&";
            requestBody = RequestBody.create(jsonBody, mediaType);

        }

        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        String urlokhttp = urlBuilder.build().toString();
        Request request = null;
        if (token == null) {

            request = new Request.Builder().header("mcix-subscription-key", subscriptionKey).header("mcix-subscription-id", subscriptionId)
                    .header("Content-Type", "application/x-www-form-urlencoded").url(urlokhttp).post(requestBody).build();
        }

        if (token != null) {

            if (process.equals("CreditReport")) { // GET method for fetching credit report
                request = new Request.Builder().header("mcix-subscription-key", subscriptionKey)
                        .header("mcix-subscription-id", subscriptionId).header("Content-Type", "application/x-www-form-urlencoded")
                        .header("Authorization", "Bearer " + token).url(urlokhttp).get().build();
            } else if (process.equals("UploadCreditReport")) { // POST for uploading Credit-Report(multipart/form-data)
                                                               // To ThitsaWork
                request = new Request.Builder().header("mcix-subscription-key", subscriptionKey)
                        .header("mcix-subscription-id", subscriptionId).header("Content-Type", "multipart/form-data")
                        .header("Authorization", "Bearer " + token).url(urlokhttp).post(requestBody).build();

            } else { // POST method for application/x-www-form-urlencoded

                request = new Request.Builder().header("mcix-subscription-key", subscriptionKey)
                        .header("mcix-subscription-id", subscriptionId).header("Content-Type", "application/x-www-form-urlencoded")
                        .header("Authorization", "Bearer " + token).url(urlokhttp).post(requestBody).build();
            }
        }

        Response response;
        Integer responseCode = 0;
        try {
            response = client.newCall(request).execute();
            responseCode = response.code();
            reponseMessage = response.body().string();
        } catch (IOException e) {

            LOG.error("error.", e);
        }

        if (responseCode != HttpURLConnection.HTTP_OK) {
            this.httpResponse(responseCode, reponseMessage);
        }
        return reponseMessage;

    }

    private void httpResponse(Integer responseCode, String responseMessage) {

        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {

            String httpResponse = "HTTP_UNAUTHORIZED";
            this.handleAPIIntegrityIssues(httpResponse);

        } else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {

            String httpResponse = "HTTP_FORBIDDEN";
            this.handleAPIIntegrityIssues(httpResponse);

        } else {
            String responseResult = "HTTP Response Code: " + responseCode + "/" + "Response Message: " + responseMessage;
            this.handleAPIIntegrityIssues(responseResult);
        }

    }

    @Transactional
    @Override
    public CreditBureauReportData getCreditReportFromThitsaWorks(final JsonCommand command) {

        this.context.authenticatedUser();
        String nrcId = command.stringValueOfParameterNamed("NRC");
        String bureauID = command.stringValueOfParameterNamed("creditBureauID");
        Integer creditBureauId = Integer.parseInt(bureauID);

        String token = null;

        String userName = getCreditBureauConfiguration(creditBureauId, CreditBureauConfigurations.userName.toString());
        String password = getCreditBureauConfiguration(creditBureauId, CreditBureauConfigurations.password.toString());
        String subscriptionId = getCreditBureauConfiguration(creditBureauId, CreditBureauConfigurations.subscriptionId.toString());
        String subscriptionKey = getCreditBureauConfiguration(creditBureauId, CreditBureauConfigurations.subscriptionKey.toString());

        CreditBureauToken creditbureautoken = createToken(creditBureauId.longValue());
        token = creditbureautoken.getCurrentToken();

        // will use only "NRC" part of code from common http method to get data based on nrc
        String process = "NRC";
        CreditBureauConfiguration searchURL = this.configDataRepository.getCreditBureauConfigData(creditBureauId, "searchurl");
        String url = searchURL.getValue();
        String nrcUrl = url + nrcId;

        String searchResult = this.okHttpConnectionMethod(userName, password, subscriptionKey, subscriptionId, nrcUrl, token, null, null,
                0L, nrcId, process);

        if (process.equals("NRC")) {
            Long uniqueID = this.extractUniqueId(searchResult);

            process = "CreditReport";
            CreditBureauConfiguration creditReportURL = this.configDataRepository.getCreditBureauConfigData(creditBureauId,
                    "creditReporturl");
            url = creditReportURL.getValue();
            String creditReportUrl = url + uniqueID;

            searchResult = this.okHttpConnectionMethod(userName, password, subscriptionKey, subscriptionId, creditReportUrl, token, null,
                    null, uniqueID, null, process);

        }

        // after getting the result(creditreport) from httpconnection-response it will assign creditreport to generic
        // creditreportdata object

        JsonObject reportObject = JsonParser.parseString(searchResult).getAsJsonObject();
        LOG.error("reportObject. {}", reportObject);

        // Credit Reports Stored into Generic CreditReportData
        JsonObject jsonData = null;
        JsonElement element = reportObject.get("Data");

        if (!(element instanceof JsonNull)) { // NOTE : "element instanceof JsonNull" is for handling empty values (and
                                              // assigning null) while fetching data from results
            jsonData = (JsonObject) element;

        }

        JsonObject borrowerInfos = null;
        String borrowerInfo = null;

        element = jsonData.get("BorrowerInfo");

        if (!(element instanceof JsonNull)) {
            borrowerInfos = (JsonObject) element;
            Gson gson = new Gson();
            borrowerInfo = gson.toJson(borrowerInfos);
        }

        String Name = borrowerInfos.get("Name").toString();
        String Gender = borrowerInfos.get("Gender").toString();
        String Address = borrowerInfos.get("Address").toString();

        String creditScore = "CreditScore";
        creditScore = getJsonObjectToString(creditScore, element, jsonData);

        String activeLoans = "ActiveLoans";
        activeLoans = getJsonArrayToString(activeLoans, element, jsonData);

        String writeOffLoans = "WriteOffLoans";
        writeOffLoans = getJsonArrayToString(writeOffLoans, element, jsonData);

        return CreditBureauReportData.instance(Name, Gender, Address, creditScore, borrowerInfo, activeLoans, writeOffLoans);
    }

    @Override
    @Transactional
    public String addCreditReport(Long bureauId, File creditReport, FormDataContentDisposition fileDetail) {

        Integer creditBureauId = bureauId.intValue();

        String userName = this.getCreditBureauConfiguration(creditBureauId, CreditBureauConfigurations.userName.toString());
        String password = this.getCreditBureauConfiguration(creditBureauId, CreditBureauConfigurations.password.toString());
        String subscriptionId = this.getCreditBureauConfiguration(creditBureauId, CreditBureauConfigurations.subscriptionId.toString());
        String subscriptionKey = this.getCreditBureauConfiguration(creditBureauId, CreditBureauConfigurations.subscriptionKey.toString());

        CreditBureauToken creditbureautoken = this.createToken(creditBureauId.longValue());
        String token = creditbureautoken.getCurrentToken();

        CreditBureauConfiguration addReportURL = this.configDataRepository.getCreditBureauConfigData(creditBureauId, "addCreditReporturl");
        String url = addReportURL.getValue();

        String process = "UploadCreditReport";

        String responseMessage = this.okHttpConnectionMethod(userName, password, subscriptionKey, subscriptionId, url, token, creditReport,
                fileDetail, 0L, null, process);
        LOG.info("responseMessage: " + responseMessage);

        return responseMessage;
    }

    @Override
    @Transactional
    public Long extractUniqueId(String jsonResult) {

        JsonObject reportObject = JsonParser.parseString(jsonResult).getAsJsonObject();

        JsonElement element = reportObject.get("Data");

        if (element.isJsonNull()) {
            String ResponseMessage = reportObject.get("ResponseMessage").getAsString();
            handleAPIIntegrityIssues(ResponseMessage);
        }

        // to fetch the Unique ID from Result
        JsonObject jsonObject = JsonParser.parseString(jsonResult).getAsJsonObject();

        Long uniqueID = 0L;
        try {
            JsonArray dataArray = jsonObject.getAsJsonArray("Data");

            if (dataArray.size() == 1) {

                JsonObject jobject = dataArray.get(0).getAsJsonObject();

                String uniqueIdString = jobject.get("UniqueID").toString();
                String TrimUniqueId = uniqueIdString.substring(1, uniqueIdString.length() - 1);

                uniqueID = Long.parseLong(TrimUniqueId);

            } else if (dataArray.size() == 0) {
                String ResponseMessage = reportObject.get("ResponseMessage").getAsString();
                handleAPIIntegrityIssues(ResponseMessage);
            } else {
                String nrc = null;
                List<String> arrlist = new ArrayList<String>();

                for (int i = 0; i < dataArray.size(); ++i) {
                    JsonObject data = dataArray.get(i).getAsJsonObject();
                    nrc = data.get("NRC").toString();
                    arrlist.add(nrc);
                }

                String listString = String.join(", ", arrlist);

                this.handleMultipleNRC(listString);
            }

        } catch (IndexOutOfBoundsException e) {
            String ResponseMessage = jsonObject.get("ResponseMessage").getAsString();
            handleAPIIntegrityIssues(ResponseMessage);
        }
        return uniqueID;
    }

    private String getJsonObjectToString(String fetchData, JsonElement element, JsonObject jsonData) {

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

        if (creditBureauToken == null) {
            CreditBureauConfiguration tokenURL = this.configDataRepository.getCreditBureauConfigData(bureauID.intValue(), "tokenurl");
            String url = tokenURL.getValue();

            String process = "token";
            String nrcId = null;
            Long uniqueID = 0L;
            String result = this.okHttpConnectionMethod(userName, password, subscriptionKey, subscriptionId, url, null, null, null,
                    uniqueID, nrcId, process);

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
                baseDataValidator.reset().failWithCode("creditBureau.configuration.is.not.available");
                throw new PlatformApiDataValidationException("creditBureau.Configuration.is.not.available",
                        "creditBureau.Configuration.is.not.available", dataValidationErrors);

            }
        } catch (NullPointerException ex) {
            baseDataValidator.reset().failWithCode("creditBureau.configuration.is.not.available");
            throw new PlatformApiDataValidationException("creditBureau.Configuration.is.not.available" + ex,
                    "creditBureau.Configuration.is.not.available", dataValidationErrors);

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
