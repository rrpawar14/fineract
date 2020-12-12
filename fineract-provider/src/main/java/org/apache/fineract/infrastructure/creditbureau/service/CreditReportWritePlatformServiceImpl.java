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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.persistence.PersistenceException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.creditbureau.data.CreditBureauConfigurations;
import org.apache.fineract.infrastructure.creditbureau.data.CreditBureauReportData;
import org.apache.fineract.infrastructure.creditbureau.domain.CreditBureau;
import org.apache.fineract.infrastructure.creditbureau.domain.CreditBureauConfigurationRepositoryWrapper;
import org.apache.fineract.infrastructure.creditbureau.domain.CreditBureauLoanProductMappingRepository;
import org.apache.fineract.infrastructure.creditbureau.domain.CreditBureauRepository;
import org.apache.fineract.infrastructure.creditbureau.domain.CreditBureauToken;
import org.apache.fineract.infrastructure.creditbureau.domain.CreditReport;
import org.apache.fineract.infrastructure.creditbureau.domain.CreditReportRepository;
import org.apache.fineract.infrastructure.creditbureau.domain.TokenRepositoryWrapper;
import org.apache.fineract.infrastructure.creditbureau.serialization.CreditBureauTokenCommandFromApiJsonDeserializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreditReportWritePlatformServiceImpl implements CreditReportWritePlatformService {

    private final PlatformSecurityContext context;
    private final CreditBureauConfigurationRepositoryWrapper configDataRepository;
    private final CreditBureauRepository creditBureauRepository;
    private final CreditReportRepository creditReportRepository;
    private final ThitsaWorksCreditBureauIntegrationWritePlatformService thitsaWorksCreditBureauIntegrationWritePlatformService;
    private final ThitsaWorksCreditBureauIntegrationWritePlatformServiceImpl thitsaWorksCreditBureauIntegrationWritePlatformServiceImpl;
    private final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
    private final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
            .resource("creditBureauIntegration");

    @Autowired
    public CreditReportWritePlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final FromJsonHelper fromApiJsonHelper, final TokenRepositoryWrapper tokenRepository,
            final CreditBureauConfigurationRepositoryWrapper configDataRepository,
            final CreditBureauTokenCommandFromApiJsonDeserializer fromApiJsonDeserializer,
            final CreditBureauLoanProductMappingRepository loanProductMappingRepository,
            final CreditBureauRepository creditBureauRepository, final CreditReportRepository creditReportRepository,
            final ThitsaWorksCreditBureauIntegrationWritePlatformService thitsaWorksCreditBureauIntegrationWritePlatformService,
            final ThitsaWorksCreditBureauIntegrationWritePlatformServiceImpl thitsaWorksCreditBureauIntegrationWritePlatformServiceImpl) {
        this.context = context;
        this.configDataRepository = configDataRepository;
        this.creditBureauRepository = creditBureauRepository;
        this.creditReportRepository = creditReportRepository;
        this.thitsaWorksCreditBureauIntegrationWritePlatformService = thitsaWorksCreditBureauIntegrationWritePlatformService;
        this.thitsaWorksCreditBureauIntegrationWritePlatformServiceImpl = thitsaWorksCreditBureauIntegrationWritePlatformServiceImpl;
    }

    private static final Logger LOG = LoggerFactory.getLogger(CreditReportWritePlatformServiceImpl.class);

    @Override
    @Transactional
    public CommandProcessingResult getCreditReport(JsonCommand command) {

        try {
            this.context.authenticatedUser();

            Long creditBureauID = command.longValueOfParameterNamed("creditBureauID");
            LOG.info("Credit Bureau Id: {}", creditBureauID);

            Optional<String> creditBureauName = getCreditBureau(creditBureauID);
            LOG.info("creditBureauName: {}", creditBureauName);

            if (creditBureauName.isEmpty()) {
                baseDataValidator.reset().failWithCode("creditBureau.has.not.been.Integrated");
                throw new PlatformApiDataValidationException("creditBureau.has.not.been.Integrated", "creditBureau.has.not.been.Integrated",
                        dataValidationErrors);
            }

            if (Objects.equals(creditBureauName.get(), CreditBureauConfigurations.THITSAWORKS.toString())) {
                LOG.info("ThitsaWork:");
                CreditBureauToken creditBureauToken = this.thitsaWorksCreditBureauIntegrationWritePlatformService
                        .createToken(creditBureauID);
                LOG.info("creditbureautoken {}", creditBureauToken);
                CreditBureauReportData reportobj = this.thitsaWorksCreditBureauIntegrationWritePlatformService
                        .getCreditReportFromThitsaWorks(command);
                LOG.info("reportobj: {}", reportobj);
                return new CommandProcessingResultBuilder().withCreditReport(reportobj).withCreditBureauToken(creditBureauToken).build();
            }

            baseDataValidator.reset().failWithCode("creditBureau.has.not.been.Integrated");
            throw new PlatformApiDataValidationException("creditBureau.has.not.been.Integrated", "creditBureau.has.not.been.Integrated",
                    dataValidationErrors);

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
    @Transactional
    public String addCreditReport(File report, Long bureauId) {

        Optional<String> creditBureauName = getCreditBureau(bureauId);
        String responseMessage = null;

        if (Objects.equals(creditBureauName.get(), CreditBureauConfigurations.THITSAWORKS.toString())) {
            responseMessage = this.thitsaWorksCreditBureauIntegrationWritePlatformService.addCreditReport(report, bureauId);
        } else {

            baseDataValidator.reset().failWithCode("creditBureau.has.not.been.Integrated");
            throw new PlatformApiDataValidationException("creditBureau.has.not.been.Integrated", "creditBureau.has.not.been.Integrated",
                    dataValidationErrors);
        }

        return responseMessage;

    }

    // saving the fetched creditreport in database
    @Override
    @Transactional
    public CommandProcessingResult saveCreditReport(Long creditBureauId, String creditReportNumber, JsonCommand command) {

        try {
            this.context.authenticatedUser();

            Optional<String> creditBureauName = getCreditBureau(creditBureauId);
            CreditReport creditReport = null;

            if (Objects.equals(creditBureauName.get(), CreditBureauConfigurations.THITSAWORKS.toString())) {

                creditReport = creditReportRepository.getThitsaWorksCreditReport(creditBureauId, creditReportNumber);

                // checks whether the creditReport for same NRC was saved before. if yes, then deletes it and replaces
                // it with new one.
                if (creditReport != null) {
                    this.creditReportRepository.delete(creditReport);
                    creditReport = null;
                }

                if (creditReport == null) {

                    String reportData = command.stringValueOfParameterNamed("apiRequestBodyAsJson");
                    LOG.info("reportData {}", reportData);
                    String nrc = creditReportNumber; // for thitsaworks CreditReportNumber is a NRC number
                    byte[] creditReportArray = reportData.getBytes(StandardCharsets.UTF_8);
                    creditReport = CreditReport.instance(creditBureauId, nrc, creditReportArray);
                    this.creditReportRepository.saveAndFlush(creditReport);

                }

            }

            return new CommandProcessingResultBuilder().withCreditReport(creditReport).build();
        } catch (final DataIntegrityViolationException dve) {
            handleTokenDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return CommandProcessingResult.empty();
        } catch (final PersistenceException ee) {
            Throwable throwable = ExceptionUtils.getRootCause(ee.getCause());
            handleTokenDataIntegrityIssues(command, throwable, ee);
            return CommandProcessingResult.empty();
        }
    }

    // under working: download saved creditreports from database
    @Override
    @Transactional
    public Response downloadCreditReport(Long creditBureauId, String creditReportNumber) {

        Optional<String> creditBureauName = getCreditBureau(creditBureauId);
        CreditReport creditReport = null;
        byte[] creditReportByte = null;

        if (Objects.equals(creditBureauName.get(), CreditBureauConfigurations.THITSAWORKS.toString())) {
            creditReport = creditReportRepository.getThitsaWorksCreditReport(creditBureauId, creditReportNumber);
            creditReportByte = creditReport.getCreditReport();
            LOG.info("creditreport {}", creditReport);
        }

        String filename = creditReportNumber + DateUtils.getLocalDateOfTenant().toString() + ".xls";
        LOG.info("filename {}", filename);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(creditReportByte.length);

        baos.write(creditReportByte, 0, creditReportByte.length);

        LOG.info("baos {}", baos);

        final ResponseBuilder response = Response.ok(baos.toByteArray());
        response.header("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.header("Content-Type", "application/vnd.ms-excel");
        LOG.info("response {}", response);
        return response.build();

    }

    @Transactional
    @Override
    public CommandProcessingResult deleteCreditReport(Long creditBureauId, JsonCommand command) {

        this.context.authenticatedUser();

        Optional<String> creditBureauName = getCreditBureau(creditBureauId);
        CreditReport creditReport = null;

        if (Objects.equals(creditBureauName.get(), CreditBureauConfigurations.THITSAWORKS.toString())) {

            String creditReportNumber = command.stringValueOfParameterNamed("creditReportNumber");
            LOG.info("delete cbid{} nrc {}", creditBureauId, creditReportNumber);
            creditReport = creditReportRepository.getThitsaWorksCreditReport(creditBureauId, creditReportNumber);
            try {
                this.creditReportRepository.delete(creditReport);
                this.creditReportRepository.flush();
            } catch (final JpaSystemException | DataIntegrityViolationException dve) {
                throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
                        "Unknown data integrity issue with resource: " + dve.getMostSpecificCause(), dve);
            }
        }
        return new CommandProcessingResultBuilder().withCreditReport(creditReport).build();
    }

    private void handleTokenDataIntegrityIssues(final JsonCommand command, final Throwable realCause, final Exception dve) {

        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());

    }

    /*
     * private void handleCreditReportResponseMessages(String ResponseMessage) {
     *
     * throw new PlatformDataIntegrityException(ResponseMessage, ResponseMessage); }
     */

}
