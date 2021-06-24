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
package org.apache.fineract.vlms.branchmodule.service;

import java.util.Map;
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
import org.apache.fineract.vlms.branchmodule.domain.EducationQualification;
import org.apache.fineract.vlms.branchmodule.domain.EducationQualificationRepository;
import org.apache.fineract.vlms.branchmodule.domain.Employee;
import org.apache.fineract.vlms.branchmodule.domain.EmployeeRepository;
import org.apache.fineract.vlms.branchmodule.domain.InsuranceDetails;
import org.apache.fineract.vlms.branchmodule.domain.InsuranceRepository;
import org.apache.fineract.vlms.branchmodule.domain.LoanApprovalLimit;
import org.apache.fineract.vlms.branchmodule.domain.LoanApprovalLimitRepository;
import org.apache.fineract.vlms.branchmodule.domain.LoanDisbursalLimit;
import org.apache.fineract.vlms.branchmodule.domain.LoanDisbursalLimitRepository;
import org.apache.fineract.vlms.customer.domain.BankDetails;
import org.apache.fineract.vlms.customer.domain.BankDetailsRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FEApplicantDetailsRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FECoApplicantDetailsRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FEEnquiryRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FEEnrollRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FELoanDetailsRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FETaskRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FETransferDetailsRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FEUsedVehicleLoanRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FEVehicleDetailsRepository;
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
public class BranchModuleWritePlatformServiceImpl implements BranchModuleWritePlatformService {

    private static final Logger LOG = LoggerFactory.getLogger(BranchModuleWritePlatformServiceImpl.class);

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
    private final FETaskRepository feTaskRepository;
    private final InsuranceRepository insuranceRepository;
    private final EducationQualificationRepository educationQualificationRepository;
    private final EmployeeRepository employeeRepository;
    private final BankDetailsRepository bankDetailsRepository;
    private final LoanApprovalLimitRepository loanApprovalLimitRepository;
    private final LoanDisbursalLimitRepository loanDisbursalLimitRepository;

    @Autowired
    public BranchModuleWritePlatformServiceImpl(final PlatformSecurityContext context, final FEEnquiryRepository feEnquiryRepository,
            final CodeCommandFromApiJsonDeserializer fromApiJsonDeserializer, final NewLoanRepository newLoanRepository,
            final AddressRepository addressRepository, final FEUsedVehicleLoanRepository feUsedVehicleLoanRepository,
            final FEApplicantDetailsRepository feApplicantDetailsRepository,
            final FECoApplicantDetailsRepository feCoApplicantDetailsRepository,
            final FEVehicleDetailsRepository feVehicleDetailsRepository, final FELoanDetailsRepository feLoanDetailsRepository,
            final FETransferDetailsRepository feTransferDetailsRepository, final FEEnrollRepository feEnrollRepository,
            final FETaskRepository feTaskRepository, final InsuranceRepository insuranceRepository,
            final EducationQualificationRepository educationQualificationRepository, final EmployeeRepository employeeRepository,
            final BankDetailsRepository bankDetailsRepository, final LoanApprovalLimitRepository loanApprovalLimitRepository,
            final LoanDisbursalLimitRepository loanDisbursalLimitRepository) {
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
        this.insuranceRepository = insuranceRepository;
        this.educationQualificationRepository = educationQualificationRepository;
        this.employeeRepository = employeeRepository;
        this.bankDetailsRepository = bankDetailsRepository;
        this.loanApprovalLimitRepository = loanApprovalLimitRepository;
        this.loanDisbursalLimitRepository = loanDisbursalLimitRepository;
    }

    @Transactional
    @Override
    @CacheEvict(value = "employee", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('cv')")
    public CommandProcessingResult createEmployee(final JsonCommand command) {

        try {
            this.context.authenticatedUser();

            // this.fromApiJsonDeserializer.validateForCreate(command.json());

            final BankDetails bankDetails = BankDetails.fromJson(command);
            this.bankDetailsRepository.save(bankDetails);

            Address customerAdd = Address.fromJson(command, "employee_communicationAddress");
            this.addressRepository.save(customerAdd);

            Address permanentAdd = Address.fromJson(command, "employee_permanentAddress");
            this.addressRepository.save(permanentAdd);

            final InsuranceDetails insuranceDetails = InsuranceDetails.fromJson(command, "general_insurance");
            this.insuranceRepository.save(insuranceDetails);

            final InsuranceDetails accidentalInsuranceDetails = InsuranceDetails.fromJson(command, "accidental_insurance");
            this.insuranceRepository.save(accidentalInsuranceDetails);

            final EducationQualification schoolQualification = EducationQualification.fromJson(command, "school_qualification");
            this.educationQualificationRepository.save(schoolQualification);

            final EducationQualification collegeQualification = EducationQualification.fromJson(command, "college_qualification");
            this.educationQualificationRepository.save(collegeQualification);

            final EducationQualification graduateQualification = EducationQualification.fromJson(command, "graduate_qualification");
            this.educationQualificationRepository.save(graduateQualification);

            final EducationQualification postGraduateQualification = EducationQualification.fromJson(command, "postgraduate_qualification");
            this.educationQualificationRepository.save(postGraduateQualification);

            final Employee employee = Employee.fromJson(command, customerAdd, permanentAdd, bankDetails, insuranceDetails,
                    accidentalInsuranceDetails, schoolQualification, collegeQualification, graduateQualification,
                    postGraduateQualification);

            this.employeeRepository.save(employee);

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(employee.getId()).build();
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
    @CacheEvict(value = "employee", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('cv')")
    public CommandProcessingResult createLoanApprovalLimit(final JsonCommand command) {

        try {

            LoanApprovalLimit loanApprovalLimit = LoanApprovalLimit.fromJson(command);
            this.loanApprovalLimitRepository.save(loanApprovalLimit);

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(loanApprovalLimit.getId()).build();
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
    @CacheEvict(value = "employee", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('cv')")
    public CommandProcessingResult createLoanDisbursalLimit(final JsonCommand command) {

        try {
            LoanDisbursalLimit loanDisbursalLimit = LoanDisbursalLimit.fromJson(command);
            this.loanDisbursalLimitRepository.save(loanDisbursalLimit);

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(loanDisbursalLimit.getId()).build();
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
    /*
     * @Transactional
     *
     * @Override
     *
     * @CacheEvict(value = "employee", key =
     * "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('cv')")
     * public CommandProcessingResult editEmployee(final JsonCommand command) {
     *
     * try { this.context.authenticatedUser();
     *
     * // this.fromApiJsonDeserializer.validateForCreate(command.json());
     *
     *
     *
     * /* final InsuranceDetails insuranceDetails = InsuranceDetails.fromJson(command, "general_insurance");
     * this.insuranceRepository.save(insuranceDetails);
     *
     * final InsuranceDetails accidentalInsuranceDetails = InsuranceDetails.fromJson(command, "accidental_insurance");
     * this.insuranceRepository.save(accidentalInsuranceDetails);
     *
     * final EducationQualification schoolQualification = EducationQualification.fromJson(command,
     * "school_qualification"); this.educationQualificationRepository.save(schoolQualification);
     *
     * final EducationQualification collegeQualification = EducationQualification.fromJson(command,
     * "college_qualification"); this.educationQualificationRepository.save(collegeQualification);
     *
     * final EducationQualification graduateQualification = EducationQualification.fromJson(command,
     * "graduate_qualification"); this.educationQualificationRepository.save(graduateQualification);
     *
     * final EducationQualification postGraduateQualification = EducationQualification.fromJson(command,
     * "postgraduate_qualification"); this.educationQualificationRepository.save(postGraduateQualification);
     *
     * final Employee employee = Employee.fromJson(command, customerAdd, permanentAdd, bankDetails, insuranceDetails,
     * accidentalInsuranceDetails, schoolQualification, collegeQualification, graduateQualification,
     * postGraduateQualification);
     *
     * this.employeeRepository.save(employee);
     *
     * return new
     * CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(employee.getId()).build(); //
     * .withEntityId(code.getId()) } catch (final JpaSystemException | DataIntegrityViolationException dve) {
     * handleDataIntegrityIssues(command, dve.getMostSpecificCause(), dve); return CommandProcessingResult.empty(); }
     * catch (final PersistenceException ee) { Throwable throwable = ExceptionUtils.getRootCause(ee.getCause());
     * handleDataIntegrityIssues(command, throwable, ee); return CommandProcessingResult.empty(); }
     *
     * }
     */

    @Transactional
    @Override
    public CommandProcessingResult updateEmployeeDetail(Long employeeDetailsId, final JsonCommand command) {

        // this.fromApiJsonDeserializer.validateForCreate(command.json());

        try {
            this.context.authenticatedUser();

            /*
             * final FETask feTask = retrieveTaskBy(taskId); final Map<String, Object> changes = feTask.update(command);
             *
             * if (!changes.isEmpty()) { this.feTaskRepository.save(feTask); }
             */

            // createcustomerdetails
            Employee employee = this.employeeRepository.getOne(employeeDetailsId);
            final Map<String, Object> changes = employee.update(command);

            if (!changes.isEmpty()) {
                this.employeeRepository.save(employee);
            }
            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(employee.getId()).build();
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
    public CommandProcessingResult updateInsuranceDetail(Long insuranceDetailsId, final JsonCommand command) {

        // this.fromApiJsonDeserializer.validateForCreate(command.json());

        try {
            this.context.authenticatedUser();

            /*
             * final FETask feTask = retrieveTaskBy(taskId); final Map<String, Object> changes = feTask.update(command);
             *
             * if (!changes.isEmpty()) { this.feTaskRepository.save(feTask); }
             */

            // createcustomerdetails
            InsuranceDetails insuranceDetails = this.insuranceRepository.getOne(insuranceDetailsId);
            final Map<String, Object> changes = insuranceDetails.update(command);

            if (!changes.isEmpty()) {
                this.insuranceRepository.save(insuranceDetails);
            }
            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(insuranceDetails.getId()).build();
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
    public CommandProcessingResult updateQualificationDetail(Long qualficationId, final JsonCommand command) {

        // this.fromApiJsonDeserializer.validateForCreate(command.json());

        try {
            this.context.authenticatedUser();

            /*
             * final FETask feTask = retrieveTaskBy(taskId); final Map<String, Object> changes = feTask.update(command);
             *
             * if (!changes.isEmpty()) { this.feTaskRepository.save(feTask); }
             */

            // createcustomerdetails

            final EducationQualification qualification = this.educationQualificationRepository.getOne(qualficationId);

            // EducationQualification.fromJson(command, "graduate_qualification");
            final Map<String, Object> changes = qualification.update(command);

            if (!changes.isEmpty()) {
                this.educationQualificationRepository.save(qualification);
            }

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(qualification.getId()).build();
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
