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
package org.apache.fineract.vlms.fieldexecutive.service;

import javax.persistence.PersistenceException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.fineract.infrastructure.codes.serialization.CodeCommandFromApiJsonDeserializer;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.address.domain.Address;
import org.apache.fineract.portfolio.address.domain.AddressRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FEApplicantDetails;
import org.apache.fineract.vlms.fieldexecutive.domain.FEApplicantDetailsRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FECoApplicantDetails;
import org.apache.fineract.vlms.fieldexecutive.domain.FECoApplicantDetailsRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FEEnquiry;
import org.apache.fineract.vlms.fieldexecutive.domain.FEEnquiryRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FEEnroll;
import org.apache.fineract.vlms.fieldexecutive.domain.FEEnrollRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FELoanDetails;
import org.apache.fineract.vlms.fieldexecutive.domain.FELoanDetailsRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FETransferDetails;
import org.apache.fineract.vlms.fieldexecutive.domain.FETransferDetailsRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FEUsedVehicleLoan;
import org.apache.fineract.vlms.fieldexecutive.domain.FEUsedVehicleLoanRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FEVehicleDetails;
import org.apache.fineract.vlms.fieldexecutive.domain.FEVehicleDetailsRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.NewLoan;
import org.apache.fineract.vlms.fieldexecutive.domain.NewLoanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FEWritePlatformServiceJpaRepositoryImpl implements FEWritePlatformService {

    private static final Logger LOG = LoggerFactory.getLogger(FEWritePlatformServiceJpaRepositoryImpl.class);

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
    private final FEEnrollRepository feEnrollRepository;
    private final CodeCommandFromApiJsonDeserializer fromApiJsonDeserializer;

    @Autowired
    public FEWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context, final FEEnquiryRepository feEnquiryRepository,
            final CodeCommandFromApiJsonDeserializer fromApiJsonDeserializer, final NewLoanRepository newLoanRepository,
            final AddressRepository addressRepository, final FEUsedVehicleLoanRepository feUsedVehicleLoanRepository,
            final FEApplicantDetailsRepository feApplicantDetailsRepository,
            final FECoApplicantDetailsRepository feCoApplicantDetailsRepository,
            final FEVehicleDetailsRepository feVehicleDetailsRepository, final FELoanDetailsRepository feLoanDetailsRepository,
            final FETransferDetailsRepository feTransferDetailsRepository, final FEEnrollRepository feEnrollRepository) {
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
    }

    @Transactional
    @Override
    @CacheEvict(value = "enquiry", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('cv')")
    public CommandProcessingResult createFEEnquiry(final JsonCommand command) {

        try {
            this.context.authenticatedUser();

            // this.fromApiJsonDeserializer.validateForCreate(command.json());

            final FEEnquiry feEnquiry = FEEnquiry.fromJson(command);
            this.feEnquiryRepository.save(feEnquiry);

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(feEnquiry.getId()).build();
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
    @CacheEvict(value = "enroll", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('cv')")
    public CommandProcessingResult createFEEnroll(final JsonCommand command) {

        try {
            this.context.authenticatedUser();

            // this.fromApiJsonDeserializer.validateForCreate(command.json());

            final FEEnroll feEnroll = FEEnroll.fromJson(command);
            this.feEnrollRepository.save(feEnroll);

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(feEnroll.getId()).build();
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
    @CacheEvict(value = "newloan", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('cv')")
    public CommandProcessingResult createNewLoan(final JsonCommand command) {

        try {
            this.context.authenticatedUser();

            // this.fromApiJsonDeserializer.validateForCreate(command.json());

            final NewLoan newLoan = NewLoan.fromJson(command);
            this.newLoanRepository.save(newLoan);

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(newLoan.getId()).build();
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
    @CacheEvict(value = "feUsedVehicleLoan", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('cv')")
    public CommandProcessingResult createFEUsedVehicleLoan(final JsonCommand command) {

        try {
            this.context.authenticatedUser();

            // this.fromApiJsonDeserializer.validateForCreate(command.json());

            // create the objects final FEApplicantDetails applicantDetails, final FECoApplicantDetails
            // coApplicantDetails,
            // final FEVehicleDetails vehicleDetails, final FELoanDetails loanDetails, final FETransferDetails
            // transferDetails

            /*
             * final FEApplicantDetails applicantDetails = FEApplicantDetails.fromJson(command);
             * System.out.println("bankDetails" + applicantDetails);
             * this.feApplicantDetailsRepository.save(applicantDetails);
             */

            /*
             * final FECoApplicantDetails feCoApplicantDetails = FECoApplicantDetails.fromJson(command);
             * System.out.println("bankDetails" + feCoApplicantDetails);
             * this.feCoApplicantDetailsRepository.save(feCoApplicantDetails);
             */

            // applicant-address part
            Address customerAdd = Address.fromJson(command, "applicant_communicationAddress");
            this.addressRepository.save(customerAdd);
            Long addressid = customerAdd.getId();
            Address customerCommunicationAdd = this.addressRepository.getOne(addressid);

            customerAdd = Address.fromJson(command, "applicant_permanentAddress");
            this.addressRepository.save(customerAdd);
            addressid = customerAdd.getId();
            Address customerPermanentAdd = this.addressRepository.getOne(addressid);

            customerAdd = Address.fromJson(command, "applicant_officeAddress");
            this.addressRepository.save(customerAdd);
            addressid = customerAdd.getId();
            Address customerOfficeAdd = this.addressRepository.getOne(addressid);

            final FEApplicantDetails feApplicantDetails = FEApplicantDetails.fromJson(command, customerCommunicationAdd,
                    customerPermanentAdd, customerOfficeAdd); // add 3 address object
            System.out.println("feApplicantDetails" + feApplicantDetails);
            this.feApplicantDetailsRepository.save(feApplicantDetails);

            // coapplicant-address part
            customerAdd = Address.fromJson(command, "coapplicant_communicationAddress");
            this.addressRepository.save(customerAdd);
            addressid = customerAdd.getId();
            Address coAppCommunicationAdd = this.addressRepository.getOne(addressid);

            customerAdd = Address.fromJson(command, "coapplicant_permanentAddress");
            this.addressRepository.save(customerAdd);
            addressid = customerAdd.getId();
            Address coAppPermanentAdd = this.addressRepository.getOne(addressid);

            customerAdd = Address.fromJson(command, "coapplicant_officeAddress");
            this.addressRepository.save(customerAdd);
            addressid = customerAdd.getId();
            Address coAppOfficeAdd = this.addressRepository.getOne(addressid);

            final FECoApplicantDetails feCoApplicant = FECoApplicantDetails.fromJson(command, coAppCommunicationAdd, coAppPermanentAdd,
                    coAppOfficeAdd); // add 3 address object
            System.out.println("feCoApplicant" + feCoApplicant);
            this.feCoApplicantDetailsRepository.save(feCoApplicant);

            final FEVehicleDetails feVehicleDetails = FEVehicleDetails.fromJson(command);
            System.out.println("bankDetails" + feVehicleDetails);
            this.feVehicleDetailsRepository.save(feVehicleDetails);

            final FELoanDetails loanDetails = FELoanDetails.fromJson(command);
            System.out.println("loanDetails" + loanDetails);
            this.feLoanDetailsRepository.save(loanDetails);

            final FETransferDetails transferDetails = FETransferDetails.fromJson(command);
            System.out.println("bankDetails" + transferDetails);
            this.feTransferDetailsRepository.save(transferDetails);

            final FEUsedVehicleLoan feUsedVehicleLoan = FEUsedVehicleLoan.fromJson(command, feApplicantDetails, feCoApplicant,
                    feVehicleDetails, loanDetails, transferDetails);

            this.feUsedVehicleLoanRepository.save(feUsedVehicleLoan);

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(feUsedVehicleLoan.getId())
                    .withCustomerDetailsId(feApplicantDetails.getId()).withCustomerGuarantorId(feCoApplicant.getId())
                    .withLoanId(loanDetails.getId()).withBankDetailsId(transferDetails.getId()).build(); // transferdetails
                                                                                                         // are
                                                                                                         // bankaccount
                                                                                                         // details
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
