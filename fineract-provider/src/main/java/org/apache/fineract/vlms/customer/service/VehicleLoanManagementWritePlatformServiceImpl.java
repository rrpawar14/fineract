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
package org.apache.fineract.vlms.customer.service;

import java.util.Map;
import javax.persistence.PersistenceException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.fineract.infrastructure.accountnumberformat.domain.AccountNumberFormatRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.address.domain.Address;
import org.apache.fineract.portfolio.address.domain.AddressRepository;
import org.apache.fineract.portfolio.client.domain.AccountNumberGenerator;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.NewVehicleLoanRepository;
import org.apache.fineract.portfolio.loanaccount.domain.VehicleDetails;
import org.apache.fineract.portfolio.loanaccount.domain.VehicleDetailsRepository;
import org.apache.fineract.portfolio.loanaccount.domain.VehicleLoan;
import org.apache.fineract.portfolio.loanaccount.service.LoanAssembler;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentType;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentTypeRepositoryWrapper;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.useradministration.domain.AppUserRepositoryWrapper;
import org.apache.fineract.vlms.branchmodule.domain.Employee;
import org.apache.fineract.vlms.branchmodule.domain.EmployeeRepository;
import org.apache.fineract.vlms.customer.domain.BankDetails;
import org.apache.fineract.vlms.customer.domain.BankDetailsRepository;
import org.apache.fineract.vlms.customer.domain.CustomerDetails;
import org.apache.fineract.vlms.customer.domain.CustomerDetailsRepository;
import org.apache.fineract.vlms.customer.domain.CustomerGuarantor;
import org.apache.fineract.vlms.customer.domain.CustomerGuarantorRepository;
import org.apache.fineract.vlms.customer.domain.Documents;
import org.apache.fineract.vlms.customer.domain.DocumentsRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FEEnroll;
import org.apache.fineract.vlms.fieldexecutive.domain.FEEnrollRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FELoanDetailsRepository;
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
    private final CustomerDetailsRepository customerDetailsRepository;
    private final VehicleDetailsRepository vehicleDetailsRepository;
    private final BankDetailsRepository bankDetailsRepository;
    private final PaymentTypeRepositoryWrapper paymentTyperepositoryWrapper;
    private final CustomerGuarantorRepository customerGuarantorRepository;
    private final AddressRepository addressRepository;
    private final FEEnrollRepository feEnrollRepository;
    private final EmployeeRepository employeeRepository;
    private final DocumentsRepository documentsRepository;
    private final AppUserRepositoryWrapper appUserRepositoryWrapper;
    private final FELoanDetailsRepository feLoanDetailsRepository;
    private final LoanAssembler loanAssembler;
    private final AccountNumberGenerator accountNumberGenerator;
    private final AccountNumberFormatRepositoryWrapper accountNumberFormatRepository;
    private final LoanRepositoryWrapper loanRepositoryWrapper;
    private static final Logger LOG = LoggerFactory.getLogger(VehicleLoanManagementWritePlatformServiceImpl.class);

    @Autowired
    public VehicleLoanManagementWritePlatformServiceImpl(final PlatformSecurityContext context, final FromJsonHelper fromJsonHelper,
            final NewVehicleLoanRepository newVehicleLoanRepository, final CustomerDetailsRepository customerDetailsRepository,
            final VehicleDetailsRepository vehicleDetailsRepository, final FELoanDetailsRepository feLoanDetailsRepository,
            final BankDetailsRepository bankDetailsRepository, final CustomerGuarantorRepository customerGuarantorRepository,
            final AddressRepository addressRepository, final FEEnrollRepository feEnrollRepository,
            final EmployeeRepository employeeRepository, final DocumentsRepository documentsRepository,
            final AppUserRepositoryWrapper appUserRepositoryWrapper, final LoanAssembler loanAssembler,
            final AccountNumberGenerator accountNumberGenerator, final AccountNumberFormatRepositoryWrapper accountNumberFormatRepository,
            final LoanRepositoryWrapper loanRepositoryWrapper, final PaymentTypeRepositoryWrapper paymentTyperepositoryWrapper) {
        this.context = context;
        this.fromJsonHelper = fromJsonHelper;
        this.newVehicleLoanRepository = newVehicleLoanRepository;
        this.customerDetailsRepository = customerDetailsRepository;
        this.vehicleDetailsRepository = vehicleDetailsRepository;
        this.bankDetailsRepository = bankDetailsRepository;
        this.feLoanDetailsRepository = feLoanDetailsRepository;
        this.customerGuarantorRepository = customerGuarantorRepository;
        this.addressRepository = addressRepository;
        this.feEnrollRepository = feEnrollRepository;
        this.employeeRepository = employeeRepository;
        this.documentsRepository = documentsRepository;
        this.appUserRepositoryWrapper = appUserRepositoryWrapper;
        this.loanAssembler = loanAssembler;
        this.accountNumberGenerator = accountNumberGenerator;
        this.accountNumberFormatRepository = accountNumberFormatRepository;
        this.loanRepositoryWrapper = loanRepositoryWrapper;
        this.paymentTyperepositoryWrapper = paymentTyperepositoryWrapper;
    }

    @Transactional
    @Override
    public CommandProcessingResult submitNewVehicleLoanApplication(final JsonCommand command) {

        // this.fromApiJsonDeserializer.validateForCreate(command.json());

        try {
            this.context.authenticatedUser();

            Address customerAdd = Address.fromJson(command, "customer_communicationAddress");
            this.addressRepository.save(customerAdd);
            Long addressid = customerAdd.getId();
            Address customerCommunicationAdd = this.addressRepository.getOne(addressid);

            customerAdd = Address.fromJson(command, "customer_permanentAddress");
            this.addressRepository.save(customerAdd);
            addressid = customerAdd.getId();
            Address customerPermanentAdd = this.addressRepository.getOne(addressid);

            customerAdd = Address.fromJson(command, "customer_officeAddress");
            this.addressRepository.save(customerAdd);
            addressid = customerAdd.getId();
            Address customerOfficeAdd = this.addressRepository.getOne(addressid);

            // createcustomerdetails
            final CustomerDetails customerDetails = CustomerDetails.fromJson(command, customerCommunicationAdd, customerPermanentAdd,
                    customerOfficeAdd); // add 3 address object

            this.customerDetailsRepository.save(customerDetails);

            final Long customerDetailsId = customerDetails.getId();

            final CustomerDetails customerDetailsObj = this.customerDetailsRepository.getOne(customerDetailsId);

            // createVehicleDetails
            final VehicleDetails vehicleDetails = VehicleDetails.fromJson(command);

            this.vehicleDetailsRepository.save(vehicleDetails);

            final Long vehicleDetailsId = vehicleDetails.getId();

            final VehicleDetails vehicleDetailsObj = this.vehicleDetailsRepository.getOne(vehicleDetailsId);

            // createguarantordetails

            Address add = Address.fromJson(command, "guarantor_communicationAddress");
            this.addressRepository.save(add);
            addressid = add.getId();
            Address guarantorCommunicationAdd = this.addressRepository.getOne(addressid);

            add = Address.fromJson(command, "guarantor_permanentAddress");
            this.addressRepository.save(add);
            addressid = add.getId();
            Address guarantorPermanentAdd = this.addressRepository.getOne(addressid);

            add = Address.fromJson(command, "guarantor_officeAddress");
            this.addressRepository.save(add);
            addressid = add.getId();
            Address guarantorOfficeAdd = this.addressRepository.getOne(addressid);

            final CustomerGuarantor customerGuarantor = CustomerGuarantor.fromJson(command, guarantorCommunicationAdd,
                    guarantorPermanentAdd, guarantorOfficeAdd); // add 3 address object

            this.customerGuarantorRepository.save(customerGuarantor);

            final Long customerGuarantorId = customerGuarantor.getId();

            final CustomerGuarantor customerGuarantorObj = this.customerGuarantorRepository.getOne(customerGuarantorId);

            // createbankdetails
            final Long paymentTypeId = command.longValueOfParameterNamed("paymentTypeId");
            PaymentType paymentType = null;

            if (paymentTypeId != null) {
                paymentType = this.paymentTyperepositoryWrapper.findOneWithNotFoundDetection(paymentTypeId);
            }

            /*
             * final BankDetails bankDetails = BankDetails.fromJson(paymentType, command);
             *
             * this.bankDetailsRepository.save(bankDetails);
             *
             * final Long bankDetailsId = bankDetails.getId();
             *
             * final BankDetails bankDetailsObj = this.bankDetailsRepository.getOne(bankDetailsId);
             */

            final Long userId = command.longValueOfParameterNamed("userId");

            AppUser appuser = null;
            if (userId != null) {
                appuser = this.appUserRepositoryWrapper.findOneWithNotFoundDetection(userId);
            }

            Loan loanDetails = null;
            String productIdParamName = "productId";

            /*
             * if (command.hasParameter(productIdParamName)) { // final Loan loanDetails = Loan.fromJson(command);
             *
             * loanDetails = this.loanAssembler.assembleFrom(command, appuser);
             * this.loanRepositoryWrapper.save(loanDetails);
             *
             * // this.feLoanDetailsRepository.save(loanDetails);
             *
             * final AccountNumberFormat accountNumberFormat = this.accountNumberFormatRepository
             * .findByAccountType(EntityAccountType.LOAN);
             *
             * String accountNumber = this.accountNumberGenerator.generate(loanDetails, accountNumberFormat);
             * loanDetails.updateAccountNo(accountNumber + "1"); } else {
             *
             * loanDetails = this.loanRepositoryWrapper.findOneWithNotFoundDetection(1L); }
             */

            final VehicleLoan newVehicleLoan = VehicleLoan.fromJson(command, customerDetailsObj, vehicleDetailsObj, customerGuarantorObj,
                    appuser);

            // final NewVehicleLoan newVehicleLoan = NewVehicleLoan.fromJson(command);

            this.newVehicleLoanRepository.save(newVehicleLoan);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(newVehicleLoan.getId()) //
                    // .withAddressId(addobj.getId()) //
                    .withCustomerDetailsId(customerDetailsObj.getId()) //
                    .withVehicleDetailsId(vehicleDetailsObj.getId()) //
                    .withCustomerGuarantorId(customerGuarantorObj.getId())// .withBankDetailsId(bankDetailsObj.getId())//
                    // .withLoanId(loanDetails.getId()) //
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
    public CommandProcessingResult submitBankDetails(final JsonCommand command) {

        try {

            this.context.authenticatedUser();

            final Long paymentTypeId = command.longValueOfParameterNamed("paymentTypeId");
            PaymentType paymentType = null;

            if (paymentTypeId != null) {
                paymentType = this.paymentTyperepositoryWrapper.findOneWithNotFoundDetection(paymentTypeId);
            }

            final Long loanId = command.longValueOfParameterNamed("loanId");
            Loan loan = null;

            if (loanId != null) {
                loan = this.loanRepositoryWrapper.findOneWithNotFoundDetection(loanId);
            }

            BankDetails bankDetails = BankDetails.fromJson(loan, null, paymentType, command);

            this.bankDetailsRepository.save(bankDetails);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withBankDetailsId(bankDetails.getId()) //
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
    public CommandProcessingResult updateCustomerDetails(Long customerDetailsId, final JsonCommand command) {

        // this.fromApiJsonDeserializer.validateForCreate(command.json());

        try {
            this.context.authenticatedUser();

            /*
             * final FETask feTask = retrieveTaskBy(taskId); final Map<String, Object> changes = feTask.update(command);
             *
             * if (!changes.isEmpty()) { this.feTaskRepository.save(feTask); }
             */

            // createcustomerdetails
            CustomerDetails customerDetails = this.customerDetailsRepository.getOne(customerDetailsId);
            final Map<String, Object> changes = customerDetails.update(command);

            if (!changes.isEmpty()) {
                this.customerDetailsRepository.save(customerDetails);
            }

            // final Long customerDetailsId = customerDetails.getId();

            // createVehicleDetails
            /*
             * final VehicleDetails vehicleDetails = VehicleDetails.fromJson(command);
             * System.out.println("vehicleDetails " + vehicleDetails);
             * this.vehicleDetailsRepository.save(vehicleDetails); System.out.println("vehicleDetailsRepository" +
             * vehicleDetailsRepository);
             *
             * final Long vehicleDetailsId = vehicleDetails.getId(); System.out.println("vehicleDetailsId" +
             * vehicleDetailsId); final VehicleDetails vehicleDetailsObj =
             * this.vehicleDetailsRepository.getOne(vehicleDetailsId); System.out.println("vehicleDetailsObj" +
             * vehicleDetailsObj);
             *
             * // createguarantordetails
             *
             * final CustomerGuarantor customerGuarantor = CustomerGuarantor.fromJson(command,
             * guarantorCommunicationAdd, guarantorPermanentAdd, guarantorOfficeAdd); // add 3 address object
             * System.out.println("customerGuarantor" + customerGuarantor);
             * this.customerGuarantorRepository.save(customerGuarantor);
             * System.out.println("customerGuarantorRepository" + customerGuarantorRepository);
             *
             * final Long customerGuarantorId = customerGuarantor.getId(); System.out.println("customerGuarantorId" +
             * customerGuarantorId); final CustomerGuarantor customerGuarantorObj =
             * this.customerGuarantorRepository.getOne(customerGuarantorId); System.out.println("customerGuarantorObj" +
             * customerGuarantorObj);
             *
             * // createbankdetails final BankDetails bankDetails = BankDetails.fromJson(command);
             * System.out.println("bankDetails" + bankDetails); this.bankDetailsRepository.save(bankDetails);
             * System.out.println("bankDetailsRepository" + bankDetailsRepository);
             *
             * final Long bankDetailsId = bankDetails.getId(); System.out.println("bankDetailsId" + bankDetailsId);
             * final BankDetails bankDetailsObj = this.bankDetailsRepository.getOne(bankDetailsId);
             * System.out.println("bankDetailsObj" + bankDetailsObj);
             *
             * final FELoanDetails loanDetails = FELoanDetails.fromJson(command); System.out.println("loanDetails" +
             * loanDetails); this.feLoanDetailsRepository.save(loanDetails);
             *
             * final Long userId = command.longValueOfParameterNamed("userId"); System.out.println("userId" + userId);
             * final AppUser appuser = this.appUserRepositoryWrapper.findOneWithNotFoundDetection(userId);
             * System.out.println("appuser" + appuser);
             *
             * final NewVehicleLoan newVehicleLoan = NewVehicleLoan.fromJson(command, customerDetailsObj,
             * vehicleDetailsObj, customerGuarantorObj, loanDetails, bankDetailsObj, appuser);
             * System.out.println("newVehicleLoan" + newVehicleLoan);
             *
             * // final NewVehicleLoan newVehicleLoan = NewVehicleLoan.fromJson(command);
             *
             * this.newVehicleLoanRepository.save(newVehicleLoan); System.out.println("newVehicleLoanRepository" +
             * newVehicleLoanRepository);
             */

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withCustomerDetailsId(customerDetails.getId()) //
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
    public CommandProcessingResult submitCustomerDocumentsData(final JsonCommand command) {

        // this.fromApiJsonDeserializer.validateForCreate(command.json());

        try {
            this.context.authenticatedUser();
            Loan loanDetails = null;
            CustomerDetails customerDetails = null;
            CustomerGuarantor customerGuarantor = null;
            FEEnroll enroll = null;
            BankDetails bankDetails = null;
            Employee employee = null;
            Loan loan = null;
            VehicleDetails vehicleDetails = null;

            final Long customerDetailsId = command.longValueOfParameterNamed("customerId");
            final Long guarantorId = command.longValueOfParameterNamed("guarantorId");
            final Long enrollId = command.longValueOfParameterNamed("enrollId");
            final Long bankDetailsId = command.longValueOfParameterNamed("bankId");
            final Long employeeId = command.longValueOfParameterNamed("employeeId");
            final Long loanId = command.longValueOfParameterNamed("loanId");
            final Long vehicleDetailsId = command.longValueOfParameterNamed("vehicleDetailsId");

            if (customerDetailsId != null) {
                customerDetails = this.customerDetailsRepository.getOne(customerDetailsId);
            }

            if (guarantorId != null) {
                customerGuarantor = this.customerGuarantorRepository.getOne(guarantorId);
            }

            if (employeeId != null) {
                employee = this.employeeRepository.getOne(employeeId);
            }

            if (bankDetailsId != null) {
                bankDetails = this.bankDetailsRepository.getOne(bankDetailsId);
            }

            if (enrollId != null) {
                enroll = this.feEnrollRepository.getOne(enrollId);
            }

            if (loanId != null) {
                loan = this.loanRepositoryWrapper.findOneWithNotFoundDetection(loanId);
            }

            if (vehicleDetailsId != null) {
                vehicleDetails = this.vehicleDetailsRepository.getOne(vehicleDetailsId);
            }

            final Documents documents = Documents.fromJson(command, customerDetails, customerGuarantor, employee, bankDetails, enroll,
                    loanDetails, vehicleDetails);

            this.documentsRepository.save(documents);
            /*
             * final VehicleDetails vehicleDetails = this.vehicleDetailsRepository.getOne(vehicleDetailsId);
             *
             * final Map<String, Object> changes = vehicleDetails.update(command);
             *
             * if (!changes.isEmpty()) { this.vehicleDetailsRepository.save(vehicleDetails); }
             */

            return new CommandProcessingResultBuilder() //
                    .withCommandId(documents.getId()) //
                    // .withVehicleDetailsId(vehicleDetails.getId()) //
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
    public CommandProcessingResult updateVehicleDetails(Long vehicleDetailsId, final JsonCommand command) {

        // this.fromApiJsonDeserializer.validateForCreate(command.json());

        try {
            this.context.authenticatedUser();

            final VehicleDetails vehicleDetails = this.vehicleDetailsRepository.getOne(vehicleDetailsId);

            final Map<String, Object> changes = vehicleDetails.update(command);

            if (!changes.isEmpty()) {
                this.vehicleDetailsRepository.save(vehicleDetails);
            }

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withVehicleDetailsId(vehicleDetails.getId()) //
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
    public CommandProcessingResult updateGuarantorDetails(Long guarantorId, final JsonCommand command) {

        // this.fromApiJsonDeserializer.validateForCreate(command.json());

        try {
            this.context.authenticatedUser();

            CustomerGuarantor customerGuarantor = this.customerGuarantorRepository.getOne(guarantorId);

            final Map<String, Object> changes = customerGuarantor.update(command);

            if (!changes.isEmpty()) {
                this.customerGuarantorRepository.save(customerGuarantor);
            }

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withCustomerGuarantorId(customerGuarantor.getId()) //
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
    public CommandProcessingResult updateBankDetails(Long bankDetailsId, final JsonCommand command) {

        // this.fromApiJsonDeserializer.validateForCreate(command.json());

        try {
            this.context.authenticatedUser();

            final BankDetails bankDetails = this.bankDetailsRepository.getOne(bankDetailsId);

            final Map<String, Object> changes = bankDetails.update(command);

            if (!changes.isEmpty()) {
                this.bankDetailsRepository.save(bankDetails);
            }

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withBankDetailsId(bankDetails.getId()) //
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

    /*
     * @Transactional
     *
     * @Override public CommandProcessingResult updateLoanDetails(Long loanDetailId, final JsonCommand command) {
     *
     * // this.fromApiJsonDeserializer.validateForCreate(command.json());
     *
     * try { this.context.authenticatedUser();
     *
     * final FELoanDetails loanDetails = this.feLoanDetailsRepository.getOne(loanDetailId); // CustomerGuarantor
     * customerGuarantor = this.customerGuarantorRepository.getOne(guarantorId);
     *
     * final Map<String, Object> changes = loanDetails.update(command);
     *
     * if (!changes.isEmpty()) { this.feLoanDetailsRepository.save(loanDetails); }
     *
     * return new CommandProcessingResultBuilder() // .withCommandId(command.commandId()) //
     * .withLoanId(loanDetails.getId()) // .build(); } catch (final JpaSystemException | DataIntegrityViolationException
     * dve) { handleDataIntegrityIssues(command, dve.getMostSpecificCause(), dve); return new
     * CommandProcessingResultBuilder() // .withCommandId(command.commandId()) // .build(); } catch (final
     * PersistenceException dve) { Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
     * handleDataIntegrityIssues(command, throwable, dve); return new CommandProcessingResultBuilder() //
     * .withCommandId(command.commandId()) // .build(); } }
     */

    /*
     * @Transactional
     *
     * @Override public CommandProcessingResult submitUsedVehicleLoanApplication(final JsonCommand command) {
     *
     * // this.fromApiJsonDeserializer.validateForCreate(command.json());
     *
     * try { this.context.authenticatedUser();
     *
     * // create address Address customerAdd = Address.fromJson(command, "customer_communicationAddress");
     * this.addressRepository.save(customerAdd); Long addressid = customerAdd.getId(); Address customerCommunicationAdd
     * = this.addressRepository.getOne(addressid);
     *
     * customerAdd = Address.fromJson(command, "customer_permanentAddress"); this.addressRepository.save(customerAdd);
     * addressid = customerAdd.getId(); Address customerPermanentAdd = this.addressRepository.getOne(addressid);
     *
     * customerAdd = Address.fromJson(command, "customer_officeAddress"); this.addressRepository.save(customerAdd);
     * addressid = customerAdd.getId(); Address customerOfficeAdd = this.addressRepository.getOne(addressid);
     *
     * // createcustomerdetails final CustomerDetails customerDetails = CustomerDetails.fromJson(command,
     * customerCommunicationAdd, customerPermanentAdd, customerOfficeAdd); System.out.println("customerDetails");
     * this.customerDetailsRepository.save(customerDetails); System.out.println("customerDetailsRepository");
     *
     * final Long customerDetailsId = customerDetails.getId(); System.out.println("customerDetailsId"); final
     * CustomerDetails customerDetailsObj = this.customerDetailsRepository.getOne(customerDetailsId);
     * System.out.println("customerDetailsObj");
     *
     * // createVehicleDetails final VehicleDetails vehicleDetails = VehicleDetails.fromJson(command);
     * System.out.println("vehicleDetails"); this.vehicleDetailsRepository.save(vehicleDetails);
     * System.out.println("vehicleDetailsRepository");
     *
     * final Long vehicleDetailsId = vehicleDetails.getId(); System.out.println("vehicleDetailsId"); final
     * VehicleDetails vehicleDetailsObj = this.vehicleDetailsRepository.getOne(vehicleDetailsId);
     * System.out.println("vehicleDetailsObj");
     *
     * Address add = Address.fromJson(command, "guarantor_commucationAddress"); this.addressRepository.save(add);
     * addressid = add.getId(); Address guarantorCommunicationAdd = this.addressRepository.getOne(addressid);
     *
     * add = Address.fromJson(command, "guarantor_permanentAddress"); this.addressRepository.save(add); addressid =
     * add.getId(); Address guarantorPermanentAdd = this.addressRepository.getOne(addressid);
     *
     * add = Address.fromJson(command, "guarantor_officeAddress"); this.addressRepository.save(add); addressid =
     * add.getId(); Address guarantorOfficeAdd = this.addressRepository.getOne(addressid);
     *
     * // createguarantordetails
     *
     * final CustomerGuarantor customerGuarantor = CustomerGuarantor.fromJson(command, guarantorCommunicationAdd,
     * guarantorPermanentAdd, guarantorOfficeAdd); System.out.println("customerGuarantor");
     * this.customerGuarantorRepository.save(customerGuarantor); System.out.println("customerGuarantorRepository");
     *
     * final Long customerGuarantorId = customerGuarantor.getId(); System.out.println("customerGuarantorId"); final
     * CustomerGuarantor customerGuarantorObj = this.customerGuarantorRepository.getOne(customerGuarantorId);
     * System.out.println("customerGuarantorObj");
     *
     * // createbankdetails final BankDetails bankDetails = BankDetails.fromJson(command);
     * System.out.println("bankDetails"); this.bankDetailsRepository.save(bankDetails);
     * System.out.println("bankDetailsRepository");
     *
     * final Long bankDetailsId = bankDetails.getId(); System.out.println("bankDetailsId"); final BankDetails
     * bankDetailsObj = this.bankDetailsRepository.getOne(bankDetailsId); System.out.println("bankDetailsObj");
     *
     * // store all in usedvehicle like addresswriteplatformservice
     *
     * final UsedVehicleLoan usedVehicleLoan = UsedVehicleLoan.fromJson(command, customerDetailsObj, vehicleDetailsObj,
     * customerGuarantorObj, bankDetailsObj);
     *
     * this.usedVehicleLoanRepository.save(usedVehicleLoan);
     *
     * return new CommandProcessingResultBuilder() // .withCommandId(command.commandId()) //
     * .withEntityId(usedVehicleLoan.getId()) // .withCustomerDetailsId(customerDetailsObj.getId()) //
     * .withVehicleDetailsId(vehicleDetailsObj.getId()) //
     * .withCustomerGuarantorId(customerGuarantorObj.getId()).withBankDetailsId(bankDetailsObj.getId())// //
     * .withOfficeId(loan.getOfficeId()) // // .withClientId(loan.getClientId()) // // .withGroupId(loan.getGroupId())
     * // // .withLoanId(loanId) // // .with(changes) // .build(); } catch (final JpaSystemException |
     * DataIntegrityViolationException dve) { handleDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
     * return new CommandProcessingResultBuilder() // .withCommandId(command.commandId()) // .build(); } catch (final
     * PersistenceException dve) { Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
     * handleDataIntegrityIssues(command, throwable, dve); return new CommandProcessingResultBuilder() //
     * .withCommandId(command.commandId()) // .build(); }
     *
     * }
     */
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
