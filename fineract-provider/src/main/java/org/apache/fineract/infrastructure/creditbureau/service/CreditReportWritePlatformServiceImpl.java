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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.util.NoSuchElementException;
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
    private final ThitsaWorksCreditBureauIntegrationWritePlatformService thitsaWorksCreditBureauIntegrationWritePlatformService;

    @Autowired
    public CreditReportWritePlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final FromJsonHelper fromApiJsonHelper, final TokenRepositoryWrapper tokenRepository,
            final CreditBureauConfigurationRepositoryWrapper configDataRepository,
            final CreditBureauTokenCommandFromApiJsonDeserializer fromApiJsonDeserializer,
            final CreditBureauLoanProductMappingRepository loanProductMappingRepository,
            final CreditBureauRepository creditBureauRepository,
            final ThitsaWorksCreditBureauIntegrationWritePlatformService thitsaWorksCreditBureauIntegrationWritePlatformService) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.tokenRepository = tokenRepository;
        this.configDataRepository = configDataRepository;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.loanProductMappingRepository = loanProductMappingRepository;
        this.creditBureauRepository = creditBureauRepository;
        this.thitsaWorksCreditBureauIntegrationWritePlatformService = thitsaWorksCreditBureauIntegrationWritePlatformService;
    }

    private static final Logger LOG = LoggerFactory.getLogger(CreditReportWritePlatformServiceImpl.class);

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
                reportobj = this.thitsaWorksCreditBureauIntegrationWritePlatformService.getCreditReportFromThitsaWorks(command);
            }
            else {
            	 this.handleCreditBureauNotmatchedIntegrityIssues();
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
            catch (NullPointerException e) {

                handleCreditBureauNamesIntegrityIssues(e);

            }

            creditBureauName = creditBureau.getName();
            LOG.info("Credit Bureau Name from CBID {}", creditBureauName);
        }

        return creditBureauName;
    }

    @Override
    public String addCreditReport(File report, String bureauId) {

        String result = null;

        String creditBureauName = getCreditBureau(bureauId);
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

            String token = this.thitsaWorksCreditBureauIntegrationWritePlatformService.createToken(userName, password, subscriptionId,
                    subscriptionKey, Id);

            CreditBureauConfiguration addReportURL = this.configDataRepository.getCreditBureauConfigData(Id, "AddCreditReport");
            String url = addReportURL.getValue();

            String process = "UploadCreditReport";
            result = this.thitsaWorksCreditBureauIntegrationWritePlatformService.httpConnectionMethod(process, null, userName, password,
                    subscriptionKey, subscriptionId, url, token, null, report);

        }
        JsonObject reportObject = JsonParser.parseString(result).getAsJsonObject();
        String responseMessage = reportObject.get("ResponseMessage").getAsString();

        return responseMessage;
    }

    @SuppressWarnings({ "CatchAndPrintStackTrace", "DefaultCharset" })
    @Override
    @Transactional
    public CommandProcessingResult saveCreditReport(Long organisationCreditBureauId, JsonCommand command) {

        try {
            this.context.authenticatedUser();

            String creditBureauID = command.stringValueOfParameterNamed("creditBureauID");

            String creditBureauName = getCreditBureau(creditBureauID);

            CreditReportData reportobj = null;

            // save the creditreport with the organisation creditbureauId in the database.

            if (creditBureauName.equals(CreditBureaNames.THITSAWORKS.toString())) {
                reportobj = this.thitsaWorksCreditBureauIntegrationWritePlatformService.getCreditReportFromThitsaWorks(command);
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

    private void handleTokenDataIntegrityIssues(final JsonCommand command, final Throwable realCause, final Exception dve) {

        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());

    }

    private void handleCreditBureauNamesIntegrityIssues(final Exception dve) {

        throw new PlatformDataIntegrityException("Credit Bureau not Found", "Credit Bureau not Found" + dve.getMessage());

    }
    
    private void handleCreditBureauNotmatchedIntegrityIssues() {

        throw new PlatformDataIntegrityException("Credit Bureau has not been Integrated", "Credit Bureau has not been Integrated");

    }

}
