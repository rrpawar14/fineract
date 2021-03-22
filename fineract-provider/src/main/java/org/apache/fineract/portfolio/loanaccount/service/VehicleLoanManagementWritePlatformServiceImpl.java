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
package org.apache.fineract.portfolio.loanaccount.service;

import javax.persistence.PersistenceException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.address.domain.Address;
import org.apache.fineract.portfolio.address.domain.AddressRepository;
import org.apache.fineract.portfolio.loanaccount.domain.BankDetails;
import org.apache.fineract.portfolio.loanaccount.domain.BankDetailsRepository;
import org.apache.fineract.portfolio.loanaccount.domain.CustomerDetails;
import org.apache.fineract.portfolio.loanaccount.domain.CustomerDetailsRepository;
import org.apache.fineract.portfolio.loanaccount.domain.CustomerGuarantor;
import org.apache.fineract.portfolio.loanaccount.domain.CustomerGuarantorRepository;
import org.apache.fineract.portfolio.loanaccount.domain.NewVehicleLoan;
import org.apache.fineract.portfolio.loanaccount.domain.NewVehicleLoanRepository;
import org.apache.fineract.portfolio.loanaccount.domain.UsedVehicleLoan;
import org.apache.fineract.portfolio.loanaccount.domain.UsedVehicleLoanRepository;
import org.apache.fineract.portfolio.loanaccount.domain.VehicleDetails;
import org.apache.fineract.portfolio.loanaccount.domain.VehicleDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VehicleLoanManagementWritePlatformServiceImpl implements VehicleLoanManagementWritePlatformService {

    private final PlatformSecurityContext context;
    private final FromJsonHelper fromJsonHelper;
    private final NewVehicleLoanRepository newVehicleLoanRepository;
    private final UsedVehicleLoanRepository usedVehicleLoanRepository;
    private final CustomerDetailsRepository customerDetailsRepository;
    private final VehicleDetailsRepository vehicleDetailsRepository;
    private final BankDetailsRepository bankDetailsRepository;
    private final CustomerGuarantorRepository customerGuarantorRepository;
    private final AddressRepository addressRepository;
    private static final Logger LOG = LoggerFactory.getLogger(VehicleLoanManagementWritePlatformServiceImpl.class);

    @Autowired
    public VehicleLoanManagementWritePlatformServiceImpl(final PlatformSecurityContext context, final FromJsonHelper fromJsonHelper,
            final NewVehicleLoanRepository newVehicleLoanRepository, final UsedVehicleLoanRepository usedVehicleLoanRepository,
            final CustomerDetailsRepository customerDetailsRepository, final VehicleDetailsRepository vehicleDetailsRepository,
            final BankDetailsRepository bankDetailsRepository, final CustomerGuarantorRepository customerGuarantorRepository,
            final AddressRepository addressRepository) {
        this.context = context;
        this.fromJsonHelper = fromJsonHelper;
        this.newVehicleLoanRepository = newVehicleLoanRepository;
        this.usedVehicleLoanRepository = usedVehicleLoanRepository;
        this.customerDetailsRepository = customerDetailsRepository;
        this.vehicleDetailsRepository = vehicleDetailsRepository;
        this.bankDetailsRepository = bankDetailsRepository;
        this.customerGuarantorRepository = customerGuarantorRepository;
        this.addressRepository = addressRepository;
    }

    @Transactional
    @Override
    public CommandProcessingResult submitNewVehicleLoanApplication(final JsonCommand command) {

        // this.fromApiJsonDeserializer.validateForCreate(command.json());

        try {
            this.context.authenticatedUser();

            final Address add = Address.fromJson(command);
            this.addressRepository.save(add);

            final Long addressid = add.getId();
            final Address addobj = this.addressRepository.getOne(addressid);

            // createcustomerdetails
            final CustomerDetails customerDetails = CustomerDetails.fromJson(command);
            this.customerDetailsRepository.save(customerDetails);

            final Long customerDetailsId = customerDetails.getId();
            final CustomerDetails customerDetailsObj = this.customerDetailsRepository.getOne(customerDetailsId);

            // createVehicleDetails
            final VehicleDetails vehicleDetails = VehicleDetails.fromJson(command);
            this.vehicleDetailsRepository.save(vehicleDetails);

            final Long vehicleDetailsId = vehicleDetails.getId();
            final VehicleDetails vehicleDetailsObj = this.vehicleDetailsRepository.getOne(vehicleDetailsId);

            // createguarantordetails

            final CustomerGuarantor customerGuarantor = CustomerGuarantor.fromJson(command);
            this.customerGuarantorRepository.save(customerGuarantor);

            final Long customerGuarantorId = customerGuarantor.getId();
            final CustomerGuarantor customerGuarantorObj = this.customerGuarantorRepository.getOne(customerGuarantorId);

            // createbankdetails
            final BankDetails bankDetails = BankDetails.fromJson(command);
            this.bankDetailsRepository.save(bankDetails);

            final Long bankDetailsId = bankDetails.getId();
            final BankDetails bankDetailsObj = this.bankDetailsRepository.getOne(bankDetailsId);

            final NewVehicleLoan newVehicleLoan = NewVehicleLoan.fromJson(command, addobj, customerDetailsObj, vehicleDetailsObj,
                    customerGuarantorObj, bankDetailsObj);

            // final NewVehicleLoan newVehicleLoan = NewVehicleLoan.fromJson(command);

            this.newVehicleLoanRepository.save(newVehicleLoan);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(newVehicleLoan.getId()) //
                    // .withOfficeId(loan.getOfficeId()) //
                    // .withClientId(loan.getClientId()) //
                    // .withGroupId(loan.getGroupId()) //
                    // .withLoanId(loanId) //
                    // .with(changes) //
                    .build();
        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .build();
        } catch (final PersistenceException dve) {
            Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
            handleDataIntegrityIssues(command, throwable, dve);
            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .build();
        }

    }

    @Transactional
    @Override
    public CommandProcessingResult submitUsedVehicleLoanApplication(final JsonCommand command) {

        // this.fromApiJsonDeserializer.validateForCreate(command.json());

        try {
            this.context.authenticatedUser();

            // create address
            final Address add = Address.fromJson(command);
            this.addressRepository.save(add);

            final Long addressid = add.getId();
            final Address addobj = this.addressRepository.getOne(addressid);

            // createcustomerdetails
            final CustomerDetails customerDetails = CustomerDetails.fromJson(command);
            this.customerDetailsRepository.save(customerDetails);

            final Long customerDetailsId = customerDetails.getId();
            final CustomerDetails customerDetailsObj = this.customerDetailsRepository.getOne(customerDetailsId);

            // createVehicleDetails
            final VehicleDetails vehicleDetails = VehicleDetails.fromJson(command);
            this.vehicleDetailsRepository.save(vehicleDetails);

            final Long vehicleDetailsId = vehicleDetails.getId();
            final VehicleDetails vehicleDetailsObj = this.vehicleDetailsRepository.getOne(vehicleDetailsId);

            // createguarantordetails

            final CustomerGuarantor customerGuarantor = CustomerGuarantor.fromJson(command);
            this.customerGuarantorRepository.save(customerGuarantor);

            final Long customerGuarantorId = customerGuarantor.getId();
            final CustomerGuarantor customerGuarantorObj = this.customerGuarantorRepository.getOne(customerGuarantorId);

            // createbankdetails
            final BankDetails bankDetails = BankDetails.fromJson(command);
            this.bankDetailsRepository.save(bankDetails);

            final Long bankDetailsId = bankDetails.getId();
            final BankDetails bankDetailsObj = this.bankDetailsRepository.getOne(bankDetailsId);

            // store all in usedvehicle like addresswriteplatformservice

            final UsedVehicleLoan usedVehicleLoan = UsedVehicleLoan.fromJson(command, addobj, customerDetailsObj, vehicleDetailsObj,
                    customerGuarantorObj, bankDetailsObj);

            this.usedVehicleLoanRepository.save(usedVehicleLoan);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(usedVehicleLoan.getId()) //
                    // .withOfficeId(loan.getOfficeId()) //
                    // .withClientId(loan.getClientId()) //
                    // .withGroupId(loan.getGroupId()) //
                    // .withLoanId(loanId) //
                    // .with(changes) //
                    .build();
        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .build();
        } catch (final PersistenceException dve) {
            Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
            handleDataIntegrityIssues(command, throwable, dve);
            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .build();
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
