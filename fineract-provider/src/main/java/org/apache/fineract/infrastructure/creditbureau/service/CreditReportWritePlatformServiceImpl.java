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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import javax.persistence.PersistenceException;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
import org.apache.fineract.infrastructure.creditbureau.domain.CreditReport;
import org.apache.fineract.infrastructure.creditbureau.domain.CreditReportRepository;
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
    private final CreditReportRepository creditReportRepository;
    private final ThitsaWorksCreditBureauIntegrationWritePlatformService thitsaWorksCreditBureauIntegrationWritePlatformService;

    @Autowired
    public CreditReportWritePlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final FromJsonHelper fromApiJsonHelper, final TokenRepositoryWrapper tokenRepository,
            final CreditBureauConfigurationRepositoryWrapper configDataRepository,
            final CreditBureauTokenCommandFromApiJsonDeserializer fromApiJsonDeserializer,
            final CreditBureauLoanProductMappingRepository loanProductMappingRepository,
            final CreditBureauRepository creditBureauRepository, final CreditReportRepository creditReportRepository,
            final ThitsaWorksCreditBureauIntegrationWritePlatformService thitsaWorksCreditBureauIntegrationWritePlatformService) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.tokenRepository = tokenRepository;
        this.configDataRepository = configDataRepository;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.loanProductMappingRepository = loanProductMappingRepository;
        this.creditBureauRepository = creditBureauRepository;
        this.creditReportRepository = creditReportRepository;
        this.thitsaWorksCreditBureauIntegrationWritePlatformService = thitsaWorksCreditBureauIntegrationWritePlatformService;
    }

    private static final Logger LOG = LoggerFactory.getLogger(CreditReportWritePlatformServiceImpl.class);

    @Override
    @Transactional
    public CommandProcessingResult getCreditReport(JsonCommand command) {

        try {
            this.context.authenticatedUser();

            Long creditBureauID = command.longValueOfParameterNamed("creditBureauID");

            Optional<String> creditBureauName = getCreditBureau(creditBureauID);

            if (creditBureauName.isEmpty()) {
                throw new PlatformDataIntegrityException("Credit Bureau has not been Integrated", "Credit Bureau has not been Integrated");
            }

            if (Objects.equals(creditBureauName.get(), CreditBureaNames.THITSAWORKS.toString())) {
                CreditReportData reportobj = this.thitsaWorksCreditBureauIntegrationWritePlatformService
                        .getCreditReportFromThitsaWorks(command);
                return new CommandProcessingResultBuilder().withCreditReport(reportobj).build();
            }

            throw new PlatformDataIntegrityException("Credit Bureau has not been Integrated", "Credit Bureau has not been Integrated");

        } catch (final DataIntegrityViolationException dve) {
            handleTokenDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return CommandProcessingResult.empty();
        } catch (final PersistenceException ee) {
            Throwable throwable = ExceptionUtils.getRootCause(ee.getCause());
            handleTokenDataIntegrityIssues(command, throwable, ee);
            return CommandProcessingResult.empty();
        }

    }

    private Optional<String> getCreditBureau(Long creditBureauID) {

        if (creditBureauID != null) {
            Optional<CreditBureau> creditBureau = this.creditBureauRepository.findById(creditBureauID);

            if (creditBureau.isEmpty()) {
                LOG.info("Credit Bureau Id {} not found", creditBureauID);
                return Optional.empty();
            }

            return Optional.of(creditBureau.get().getName());

        }

        return Optional.empty();
    }

    @Override
    public String addCreditReport(File report, Long bureauId) {

        Optional<String> creditBureauName = getCreditBureau(bureauId);

        if (Objects.equals(creditBureauName.get(), CreditBureaNames.THITSAWORKS.toString())) {

            Integer creditBureauId = bureauId.intValue();

            // make lower case
            CreditBureauConfiguration subscriptionIdData = this.configDataRepository.getCreditBureauConfigData(creditBureauId,
                    "SubscriptionId");
            CreditBureauConfiguration subscriptionKeyData = this.configDataRepository.getCreditBureauConfigData(creditBureauId,
                    "SubscriptionKey");
            CreditBureauConfiguration userNameData = this.configDataRepository.getCreditBureauConfigData(creditBureauId, "Username");
            CreditBureauConfiguration passwordData = this.configDataRepository.getCreditBureauConfigData(creditBureauId, "Password");

            String subscriptionId = "";
            String subscriptionKey = "";
            String userName = "";
            String password = "";

            try {
                subscriptionId = subscriptionIdData.getValue();
                subscriptionKey = subscriptionKeyData.getValue();
                userName = userNameData.getValue();
                password = passwordData.getValue();
            } catch (NullPointerException ex) {
                throw new PlatformDataIntegrityException("Credit Bureau Configuration is not available",
                        "Credit Bureau Configuration is not available" + ex);
            }

            LOG.info("subscriptionIdData {}", subscriptionIdData + "subscriptionId {}", subscriptionId);

            if (!"".equals(subscriptionId) && !"".equals(subscriptionKey) && !"".equals(userName) && !"".equals(password)) {

                String token = this.thitsaWorksCreditBureauIntegrationWritePlatformService.createToken(userName, password, subscriptionId,
                        subscriptionKey, creditBureauId);

                CreditBureauConfiguration addReportURL = this.configDataRepository.getCreditBureauConfigData(creditBureauId,
                        "addCreditReporturl");
                String url = addReportURL.getValue();

                String process = "UploadCreditReport";
                String result = this.thitsaWorksCreditBureauIntegrationWritePlatformService.httpConnectionMethod(process, null, userName,
                        password, subscriptionKey, subscriptionId, url, token, null, report);

                JsonObject reportObject = JsonParser.parseString(result).getAsJsonObject();
                String responseMessage = reportObject.get("ResponseMessage").getAsString();

                return responseMessage;
            }

            throw new PlatformDataIntegrityException("Credit Bureau Configuration is not available",
                    "Credit Bureau Configuration is not available");

        } else {

            throw new PlatformDataIntegrityException("Credit Bureau has not been Integrated", "Credit Bureau has not been Integrated");
        }

    }

    // saving the fetched creditreport into local database
    @Override
    @SuppressWarnings("DefaultCharset")
    @Transactional
    public CommandProcessingResult saveCreditReport(Long creditBureauId, JsonCommand command) {

        try {
            this.context.authenticatedUser();

            String reportData = command.stringValueOfParameterNamed("apiRequestBodyAsJson");

            LOG.info("reportData {}", reportData);

            // test it and assign that to specific thitsawork credit bureau
            JsonObject jsonObject = JsonParser.parseString(reportData).getAsJsonObject();
            JsonArray jArray = jsonObject.getAsJsonArray("Data");
            JsonObject jobject = jArray.get(0).getAsJsonObject();
            String nrc = jobject.get("NRC").toString();

            byte[] creditReportArray = reportData.getBytes(StandardCharsets.UTF_8);

            final CreditReport creditReports = CreditReport.instance(creditBureauId, nrc, creditReportArray);
            this.creditReportRepository.saveAndFlush(creditReports);

            return new CommandProcessingResultBuilder().withCreditReport(creditReports).build();
        } catch (final DataIntegrityViolationException dve) {
            handleTokenDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return CommandProcessingResult.empty();
        } catch (final PersistenceException ee) {
            Throwable throwable = ExceptionUtils.getRootCause(ee.getCause());
            handleTokenDataIntegrityIssues(command, throwable, ee);
            return CommandProcessingResult.empty();
        }
    }

    private void handleTokenDataIntegrityIssues(final JsonCommand command, final Throwable realCause, final Exception dve) {

        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());

    }

}
