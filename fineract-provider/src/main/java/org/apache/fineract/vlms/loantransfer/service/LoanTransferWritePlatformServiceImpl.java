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
package org.apache.fineract.vlms.loantransfer.service;

import java.util.Map;
import javax.persistence.PersistenceException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.fineract.infrastructure.accountnumberformat.domain.AccountNumberFormatRepositoryWrapper;
import org.apache.fineract.infrastructure.codes.exception.CodeNotFoundException;
import org.apache.fineract.infrastructure.codes.serialization.CodeCommandFromApiJsonDeserializer;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.address.domain.AddressRepository;
import org.apache.fineract.portfolio.client.domain.AccountNumberGenerator;
import org.apache.fineract.vlms.fieldexecutive.domain.FEApplicantDetailsRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FECoApplicantDetailsRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FEEnquiryRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FEEnrollRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FELoanDetailsRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FETaskRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FETransferDetailsRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FEUsedVehicleLoanRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FEVehicleDetailsRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FeCashLimitRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FieldExecutiveRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.LoanChangeRequestRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.NewLoanRepository;
import org.apache.fineract.vlms.loantransfer.domain.LoanTransferTask;
import org.apache.fineract.vlms.loantransfer.domain.LoanTransferTeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoanTransferWritePlatformServiceImpl implements LoanTransferWritePlatformService {

    private final PlatformSecurityContext context;
    private final FEEnquiryRepository feEnquiryRepository;
    private final NewLoanRepository newLoanRepository;
    private final AddressRepository addressRepository;
    private final FEUsedVehicleLoanRepository feUsedVehicleLoanRepository;
    private final FEApplicantDetailsRepository feApplicantDetailsRepository;
    private final FECoApplicantDetailsRepository feCoApplicantDetailsRepository;
    private final FEVehicleDetailsRepository feVehicleDetailsRepository;
    private final FELoanDetailsRepository feLoanDetailsRepository;
    private final FETransferDetailsRepository feTransferDetailsRepository;
    private final LoanTransferTeamRepository loanTransferTeamRepository;
    private final FEEnrollRepository feEnrollRepository;
    private final CodeCommandFromApiJsonDeserializer fromApiJsonDeserializer;
    private final FETaskRepository feTaskRepository;
    private final FeCashLimitRepository feCashLimitRepository;
    private final LoanChangeRequestRepository loanChangeRequestRepository;
    private final FieldExecutiveRepository fieldExecutiveRepository;
    private final AccountNumberGenerator accountNumberGenerator;
    private final AccountNumberFormatRepositoryWrapper accountNumberFormatRepository;

    @Autowired
    public LoanTransferWritePlatformServiceImpl(final PlatformSecurityContext context, final FEEnquiryRepository feEnquiryRepository,
            final CodeCommandFromApiJsonDeserializer fromApiJsonDeserializer, final NewLoanRepository newLoanRepository,
            final AddressRepository addressRepository, final FEUsedVehicleLoanRepository feUsedVehicleLoanRepository,
            final FEApplicantDetailsRepository feApplicantDetailsRepository,
            final FECoApplicantDetailsRepository feCoApplicantDetailsRepository,
            final FEVehicleDetailsRepository feVehicleDetailsRepository, final FELoanDetailsRepository feLoanDetailsRepository,
            final FETransferDetailsRepository feTransferDetailsRepository, final FEEnrollRepository feEnrollRepository,
            final FETaskRepository feTaskRepository, final FeCashLimitRepository feCashLimitRepository,
            final LoanChangeRequestRepository loanChangeRequestRepository, final FieldExecutiveRepository fieldExecutiveRepository,
            final AccountNumberGenerator accountNumberGenerator, final AccountNumberFormatRepositoryWrapper accountNumberFormatRepository,
            final LoanTransferTeamRepository loanTransferTeamRepository) {
        this.context = context;
        this.feEnquiryRepository = feEnquiryRepository;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.feUsedVehicleLoanRepository = feUsedVehicleLoanRepository;
        this.newLoanRepository = newLoanRepository;
        this.addressRepository = addressRepository;
        this.feApplicantDetailsRepository = feApplicantDetailsRepository;
        this.feCoApplicantDetailsRepository = feCoApplicantDetailsRepository;
        this.feVehicleDetailsRepository = feVehicleDetailsRepository;
        this.feLoanDetailsRepository = feLoanDetailsRepository;
        this.feTransferDetailsRepository = feTransferDetailsRepository;
        this.feEnrollRepository = feEnrollRepository;
        this.feTaskRepository = feTaskRepository;
        this.feCashLimitRepository = feCashLimitRepository;
        this.loanChangeRequestRepository = loanChangeRequestRepository;
        this.fieldExecutiveRepository = fieldExecutiveRepository;
        this.accountNumberGenerator = accountNumberGenerator;
        this.accountNumberFormatRepository = accountNumberFormatRepository;
        this.loanTransferTeamRepository = loanTransferTeamRepository;
    }

    @Transactional
    @Override
    @CacheEvict(value = "fetask", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('cv')")
    public CommandProcessingResult createLoanTransferTeamTask(final JsonCommand command) {

        try {
            this.context.authenticatedUser();

            // this.fromApiJsonDeserializer.validateForCreate(command.json());

            final LoanTransferTask loanTransferTask = LoanTransferTask.fromJson(command);
            this.loanTransferTeamRepository.save(loanTransferTask);

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(loanTransferTask.getId()).build();
            // .withEntityId(code.getId())
        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return CommandProcessingResult.empty();
        } catch (final PersistenceException ee) {
            Throwable throwable = ExceptionUtils.getRootCause(ee.getCause());
            handleDataIntegrityIssues(command, throwable, ee);
            return CommandProcessingResult.empty();
        }

    }

    @Transactional
    @Override
    @CacheEvict(value = "fetask", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('cv')")
    public CommandProcessingResult editLoanTransferTask(final Long taskId, final JsonCommand command) {

        try {
            this.context.authenticatedUser();

            // this.fromApiJsonDeserializer.validateForCreate(command.json());

            /*
             * final Code code = retrieveCodeBy(codeId); final Map<String, Object> changes = code.update(command);
             */

            final LoanTransferTask loanTransfer = retrieveTaskBy(taskId);
            final Map<String, Object> changes = loanTransfer.update(command);

            if (!changes.isEmpty()) {
                this.loanTransferTeamRepository.save(loanTransfer);
            }

            /*
             * final FETask feTask = FETask.fromJson(command); this.feTaskRepository.save(feTask);
             */

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(taskId).build();
            // .withEntityId(code.getId())
        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return CommandProcessingResult.empty();
        } catch (final PersistenceException ee) {
            Throwable throwable = ExceptionUtils.getRootCause(ee.getCause());
            handleDataIntegrityIssues(command, throwable, ee);
            return CommandProcessingResult.empty();
        }

    }

    @Transactional
    @Override
    @CacheEvict(value = "fetask", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('cv')")
    public CommandProcessingResult deleteLoanTransferTask(final Long taskId) {

        this.context.authenticatedUser();

        final LoanTransferTask loanTransfer = retrieveTaskBy(taskId);
        try {
            this.loanTransferTeamRepository.delete(loanTransfer);
            this.loanTransferTeamRepository.flush();
        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
                    "Unknown data integrity issue with resource: " + dve.getMostSpecificCause(), dve);
        }
        return new CommandProcessingResultBuilder().withEntityId(loanTransfer.getId()).build();
        // .withEntityId(code.getId())

    }

    private LoanTransferTask retrieveTaskBy(final Long taskId) {
        return this.loanTransferTeamRepository.findById(taskId).orElseThrow(() -> new CodeNotFoundException(taskId.toString()));
    }

    private void handleDataIntegrityIssues(final JsonCommand command, final Throwable realCause, final Exception dve) {

        if (realCause.getMessage().contains("mobile_no")) {
            final String mobileNo = command.stringValueOfParameterNamed("mobileNo");
            throw new PlatformDataIntegrityException("error.msg.client.duplicate.mobileNo",
                    "Client with mobileNo `" + mobileNo + "` already exists", "mobileNo", mobileNo);
        }

        throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

}
