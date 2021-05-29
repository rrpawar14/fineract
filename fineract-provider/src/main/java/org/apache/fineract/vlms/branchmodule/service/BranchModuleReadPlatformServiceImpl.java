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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.vlms.branchmodule.data.BranchAnalyticsAllData;
import org.apache.fineract.vlms.branchmodule.data.EducationQualificationData;
import org.apache.fineract.vlms.branchmodule.data.EmployeeData;
import org.apache.fineract.vlms.branchmodule.data.InsuranceDetailsData;
import org.apache.fineract.vlms.branchmodule.data.LoanApprovalLimitData;
import org.apache.fineract.vlms.branchmodule.data.LoanDisbursalLimitData;
import org.apache.fineract.vlms.customer.data.AddressData;
import org.apache.fineract.vlms.customer.data.BankDetailsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class BranchModuleReadPlatformServiceImpl implements BranchModuleReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    @Autowired
    public BranchModuleReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class EmployeeMapper implements RowMapper<EmployeeData> {

        public String schema() {
            return " emp.id as empId, emp.name as empName, emp.calledname as calledName, emp.surname as surName, emp.mobilenumber as mobileNumber,"
                    + " emp.alternatenumber as alternateNumber, emp.officialnumber as officialNumber, emp.dob as dob, emp.gender as gender, emp.age as age,"
                    + " emp.maritalstatus as maritalstatus, emp.designation as designation, emp.spousename as spouseName, emp.bloodgroup as bloodgroup,"
                    + " emp.fathername as fathername, emp.vehiclenumber as vehiclenumber, emp.vehicleType as vehicleType,"
                    + " emp.doj as doj, emp.agtnumber as agtnumber, emp.status as status, "

                    + " commadd.id as commaddId, commadd.address_line_1 as commAddressLine1, commadd.address_line_2 as commAddressLine2, commadd.city as comcity, "
                    + " commadd.postal_code as comPostalCode, commadd.landmark as comlandmark, commadd.area as comarea, commadd.state as comstate, "
                    + " permadd.id as permaddId, permadd.address_line_1 as permAddressLine1, permadd.address_line_2 as permAddressLine2, permadd.city as permcity, "
                    + " permadd.postal_code as permpostalCode, permadd.landmark as permlandmark, permadd.area as permarea, permadd.state as permstate, "

                    + " bnk.id as bankId, bnk.loan_eligible_amount as eligibleloan, bnk.account_type as accountType, bnk.disbursal_type as disbursalType, "
                    + " bnk.account_number as accountNumber, bnk.account_holder_name as accountHolderName, bnk.bank_name as bankName, bnk.branch_name as branchName,"
                    + " bnk.IFSC as IFSC, "

                    + " genisn.id as genisnId, genisn.policynumber as genPolicyNumber,  genisn.companycoverage as genCompanyCoverage, genisn.policycoverage as genPolicyCoverage, "
                    + " accisn.id as accisnId, accisn.policynumber as accPolicyNumber,  accisn.companycoverage as accCompanyCoverage, accisn.policycoverage as accPolicyCoverage, "

                    + " sc.id as scId, sc.school_university as scUniversity, sc.qualification as scQualification, sc.percentage as scPercentage, sc.passingyear as scPassingYear, "
                    + " cl.id as clId, cl.school_university as clUniversity, cl.qualification as clQualification, cl.percentage as clPercentage, cl.passingyear as clPassingYear, "
                    + " gd.id as gdId, gd.school_university as gdUniversity, gd.qualification as gdQualification, gd.percentage as gdPercentage, gd.passingyear as gdPassingYear, "
                    + " pg.id as pgId, pg.school_university as pgUniversity, pg.qualification as pgQualification, pg.percentage as pgPercentage, pg.passingyear as pgPassingYear ";

        }

        public String schemaJoin() {
            return " join m_address commadd on emp.communicationadd_id = commadd.id  "
                    + " left join m_address permadd on emp.permanentadd_id = permadd.id "
                    + " left join m_customer_bank_details bnk on emp.bankdetails_id = bnk.id  "
                    + " left join m_insurancedetails genisn on emp.insurancedetails_id = genisn.id  "
                    + " left join m_insurancedetails accisn on emp.accidentalinsurancedetails_id = accisn.id "
                    + " left join m_education_qualification sc on emp.schoolqualification_id = sc.id "
                    + " left join m_education_qualification cl on emp.collegequalification_id = cl.id "
                    + " left join m_education_qualification gd on emp.graduatequalification_id = gd.id "
                    + " left join m_education_qualification pg on emp.postgraduatequalification_id = pg.id ";
        }

        @Override
        public EmployeeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long empId = rs.getLong("empId");
            final String empName = rs.getString("empName");
            final String calledName = rs.getString("calledName");
            final String surName = rs.getString("surName");
            final String mobileNumber = rs.getString("mobileNumber");
            final String alternateNumber = rs.getString("alternateNumber");
            final String officialNumber = rs.getString("officialNumber");
            final LocalDate dob = JdbcSupport.getLocalDate(rs, "dob");
            final String gender = rs.getString("gender");
            final Long age = rs.getLong("age");

            final String maritalstatus = rs.getString("maritalstatus");
            final String designation = rs.getString("designation");
            final String spouseName = rs.getString("spouseName");

            final String bloodgroup = rs.getString("bloodgroup");
            final String fathername = rs.getString("fathername");
            final String vehiclenumber = rs.getString("vehiclenumber");

            final String vehicleType = rs.getString("vehicleType");
            final LocalDate doj = JdbcSupport.getLocalDate(rs, "doj");

            final String agtnumber = rs.getString("agtnumber");
            final String status = rs.getString("status");

            final Long commaddId = rs.getLong("commaddId");
            final String commAddressLine1 = rs.getString("commAddressLine1");
            final String commAddressLine2 = rs.getString("commAddressLine2");
            final String comcity = rs.getString("comcity");
            final String comPostalCode = rs.getString("comPostalCode");
            final String comlandmark = rs.getString("comlandmark");
            final String comstate = rs.getString("comstate");
            final String comarea = rs.getString("comarea");
            final AddressData communicationAddressData = new AddressData(commaddId, commAddressLine1, commAddressLine2, comcity,
                    comPostalCode, comlandmark, comarea, comstate);

            final Long permaddId = rs.getLong("permaddId");
            final String permAddressLine1 = rs.getString("permAddressLine1");
            final String permAddressLine2 = rs.getString("permAddressLine2");
            final String permcity = rs.getString("permcity");
            final String permpostalCode = rs.getString("permpostalCode");
            final String permlandmark = rs.getString("permlandmark");
            final String permarea = rs.getString("permarea");
            final String permstate = rs.getString("permstate");

            final AddressData permanentAddressData = new AddressData(permaddId, permAddressLine1, permAddressLine2, permcity,
                    permpostalCode, permlandmark, permarea, permstate);

            final Long bankId = rs.getLong("bankId");
            final Long eligibleloan = rs.getLong("eligibleloan");
            final String accountType = rs.getString("accountType");
            final String disbursalType = rs.getString("disbursalType");
            final String accountNumber = rs.getString("accountNumber");
            final String accountHolderName = rs.getString("accountHolderName");
            final String bankName = rs.getString("bankName");
            final String branchName = rs.getString("branchName");
            final String IFSC = rs.getString("IFSC");

            BankDetailsData bankDetailsData = new BankDetailsData(bankId, eligibleloan, accountType, disbursalType, accountNumber,
                    accountHolderName, bankName, branchName, IFSC);

            final Long genInsuranceId = rs.getLong("genisnId");
            final String genPolicyNumber = rs.getString("genPolicyNumber");
            final String genCompanyCoverage = rs.getString("genCompanyCoverage");
            final String genPolicyCoverage = rs.getString("genPolicyCoverage");

            InsuranceDetailsData genInsuranceDetailsData = new InsuranceDetailsData(genInsuranceId, genPolicyNumber, genCompanyCoverage,
                    genPolicyCoverage);

            final Long accInsuranceId = rs.getLong("accisnId");
            final String accPolicyNumber = rs.getString("accPolicyNumber");
            final String accCompanyCoverage = rs.getString("accCompanyCoverage");
            final String accPolicyCoverage = rs.getString("accPolicyCoverage");

            InsuranceDetailsData accInsuranceDetailsData = new InsuranceDetailsData(accInsuranceId, accPolicyNumber, accCompanyCoverage,
                    accPolicyCoverage);

            final Long scId = rs.getLong("scId");
            final String scUniversity = rs.getString("scUniversity");
            final String scQualification = rs.getString("scQualification");
            final String scPercentage = rs.getString("scPercentage");
            final String scPassingYear = rs.getString("scPassingYear");

            EducationQualificationData scEducationQualificationData = new EducationQualificationData(scId, scUniversity, scQualification,
                    scPercentage, scPassingYear);

            final Long clId = rs.getLong("clId");
            final String clUniversity = rs.getString("clUniversity");
            final String clQualification = rs.getString("clQualification");
            final String clPercentage = rs.getString("clPercentage");
            final String clPassingYear = rs.getString("clPassingYear");

            EducationQualificationData clEducationQualificationData = new EducationQualificationData(clId, clUniversity, clQualification,
                    clPercentage, clPassingYear);

            final Long gdId = rs.getLong("gdId");
            final String gdUniversity = rs.getString("gdUniversity");
            final String gdQualification = rs.getString("gdQualification");
            final String gdPercentage = rs.getString("gdPercentage");
            final String gdPassingYear = rs.getString("gdPassingYear");

            EducationQualificationData gdEducationQualificationData = new EducationQualificationData(gdId, gdUniversity, gdQualification,
                    gdPercentage, gdPassingYear);

            final Long pgId = rs.getLong("pgId");
            final String pgUniversity = rs.getString("pgUniversity");
            final String pgQualification = rs.getString("pgQualification");
            final String pgPercentage = rs.getString("pgPercentage");
            final String pgPassingYear = rs.getString("pgPassingYear");

            EducationQualificationData pgEducationQualificationData = new EducationQualificationData(pgId, pgUniversity, pgQualification,
                    pgPercentage, pgPassingYear);

            return EmployeeData.instance(empId, empName, calledName, surName, mobileNumber, alternateNumber, officialNumber, dob, gender,
                    age, maritalstatus, designation, spouseName, bloodgroup, fathername, vehiclenumber, vehicleType, doj, agtnumber, status,
                    communicationAddressData, permanentAddressData, bankDetailsData, genInsuranceDetailsData, accInsuranceDetailsData,
                    scEducationQualificationData, clEducationQualificationData, gdEducationQualificationData, pgEducationQualificationData);

        }
    }

    @Override
    @Cacheable(value = "EmployeeData", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('CD')")
    public Collection<EmployeeData> retrieveAllEmployee() {
        this.context.authenticatedUser();

        final EmployeeMapper rm = new EmployeeMapper();
        final String sql = "select " + rm.schema() + "from m_employee emp " + rm.schemaJoin() + " ; ";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

    @Override
    @Cacheable(value = "EmployeeData", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('CD')")
    public EmployeeData retrieveEmployeeById(Long employeeId) {
        this.context.authenticatedUser();

        final EmployeeMapper rm = new EmployeeMapper();
        final String sql = "select " + rm.schema() + " from m_employee emp " + rm.schemaJoin() + " where emp.id = ? ";

        return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { employeeId });
    }

    private static final class LoanApprovalLimitMapper implements RowMapper<LoanApprovalLimitData> {

        public String schema() {
            return " lnapp.id as id, lnapp.approve_limit as approvalLimit, lnapp.branch_name as branchName, lnapp.requested_on as requestDate, lnapp.requested_loan_amount as requestAmount, "
                    + " lnapp.approve_amount as approveAmount, lnapp.status as status ";
        }

        @Override
        public LoanApprovalLimitData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final Long approvalLimit = rs.getLong("approvalLimit");
            final String branchName = rs.getString("branchName");
            final LocalDate requestDate = JdbcSupport.getLocalDate(rs, "requestDate");
            final Long requestAmount = rs.getLong("requestAmount");
            final Long approveAmount = rs.getLong("approveAmount");
            final String status = rs.getString("status");

            return LoanApprovalLimitData.instance(id, approvalLimit, branchName, requestDate, requestAmount, approveAmount, status);
        }
    }

    private static final class BranchAnalyticsAllDataMapper implements RowMapper<BranchAnalyticsAllData> {

        public String schema() {
            return " ba.cashDemand as cashDemand, ba.cashCollection as cashCollection, ba.loanAmountBankCollection as loanAmountBankCollection,"
                    + " ba.loanAmountCashCollection as loanAmountCashCollection, ba.feAssigned as feAssigned, ba.feFollowup as feFollowup,"
                    + " ba.feCompleted as feCompleted, ba.insuranceExpired as insuranceExpired,  ba.insuranceRenewal as insuranceRenewal,"
                    + " ba.insuranceHold as insuranceHold, ba.insuranceCompleted as insuranceCompleted, ba.allocatedCash as allocatedCash,"
                    + " ba.expense as expense, ba.agtAssigned as agtAssigned, ba.agtNotAssigned as agtNotAssigned, ba.rtoExpenses as rtoExpenses,"
                    + " ba.rtoCompleted as rtoCompleted, ba.docPending as docPending, ba.docCompleted as docCompleted,  ba.disbursalRepossessed as disbursalRepossessed,"
                    + " ba.disbursalReleased as disbursalReleased,  ba.disbursalBank as disbursalBank, ba.disbursalCash as disbursalCash,"
                    + " ba.customerLoanDisburse as customerLoanDisburse, ba.guarantorLoanDisburse as guarantorLoanDisburse, ba.thirdPartyLoanDisburse as thirdPartyLoanDisburse,"
                    + " ba.enquiryDirect as enquiryDirect, ba.enquiryWalkIn as enquiryWalkIn, ba.enquiryReloan as enquiryReloan ";
        }

        @Override
        public BranchAnalyticsAllData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long cashDemand = rs.getLong("cashDemand");

            final Long cashCollection = rs.getLong("cashCollection");

            final Long loanAmountBankCollection = rs.getLong("loanAmountBankCollection");

            final Long loanAmountCashCollection = rs.getLong("loanAmountCashCollection");

            final Long feAssigned = rs.getLong("feAssigned");

            final Long feFollowup = rs.getLong("feFollowup");

            final Long feCompleted = rs.getLong("feCompleted");

            final Long insuranceExpired = rs.getLong("insuranceExpired");

            final Long insuranceRenewal = rs.getLong("insuranceRenewal");

            final Long insuranceHold = rs.getLong("insuranceHold");

            final Long insuranceCompleted = rs.getLong("insuranceCompleted");

            final Long allocatedCash = rs.getLong("allocatedCash");

            final Long expense = rs.getLong("expense");

            final Long agtAssigned = rs.getLong("agtAssigned");

            final Long agtNotAssigned = rs.getLong("agtNotAssigned");

            final Long rtoExpenses = rs.getLong("rtoExpenses");

            final Long rtoCompleted = rs.getLong("rtoCompleted");

            final Long docPending = rs.getLong("docPending");

            final Long docCompleted = rs.getLong("docCompleted");

            final Long disbursalRepossessed = rs.getLong("disbursalRepossessed");

            final Long disbursalReleased = rs.getLong("disbursalReleased");

            final Long disbursalBank = rs.getLong("disbursalBank");

            final Long disbursalCash = rs.getLong("disbursalCash");

            final Long customerLoanDisburse = rs.getLong("customerLoanDisburse");

            final Long guarantorLoanDisburse = rs.getLong("guarantorLoanDisburse");

            final Long thirdPartyLoanDisburse = rs.getLong("thirdPartyLoanDisburse");

            final Long enquiryDirect = rs.getLong("enquiryDirect");

            final Long enquiryWalkIn = rs.getLong("enquiryWalkIn");

            final Long enquiryReloan = rs.getLong("enquiryReloan");

            return BranchAnalyticsAllData.instance(cashDemand, cashCollection, loanAmountBankCollection, loanAmountCashCollection,
                    feAssigned, feFollowup, feCompleted, insuranceExpired, insuranceRenewal, insuranceHold, insuranceCompleted,
                    allocatedCash, expense, agtAssigned, agtNotAssigned, rtoExpenses, rtoCompleted, docPending, docCompleted,
                    disbursalRepossessed, disbursalReleased, disbursalBank, disbursalCash, customerLoanDisburse, guarantorLoanDisburse,
                    thirdPartyLoanDisburse, enquiryDirect, enquiryWalkIn, enquiryReloan);
        }
    }

    private static final class LoanDisbursalLimitMapper implements RowMapper<LoanDisbursalLimitData> {

        public String schema() {
            return " lndsb.id as id, lndsb.cash_limit as cashLimit, lndsb.branch_name as branchName, lndsb.duration as duration, lndsb.requested_on as requestDate, lndsb.requested_amount as requestAmount, "
                    + " lndsb.approved_amount as approveAmount, lndsb.status as status ";
        }

        @Override
        public LoanDisbursalLimitData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final Long cashLimit = rs.getLong("cashLimit");
            final String branchName = rs.getString("branchName");
            final Long duration = rs.getLong("duration");
            final LocalDate requestDate = JdbcSupport.getLocalDate(rs, "requestDate");
            final Long requestAmount = rs.getLong("requestAmount");
            final Long approveAmount = rs.getLong("approveAmount");
            final String status = rs.getString("status");

            return LoanDisbursalLimitData.instance(id, cashLimit, branchName, duration, requestDate, requestAmount, approveAmount, status);
        }
    }

    @Override
    @Cacheable(value = "EmployeeData", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('CD')")
    public Collection<LoanApprovalLimitData> retrieveAllLoanApproval() {
        this.context.authenticatedUser();

        final LoanApprovalLimitMapper rm = new LoanApprovalLimitMapper();
        final String sql = "select " + rm.schema() + " from m_loan_approval_limit lnapp ";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

    @Override
    @Cacheable(value = "DisbursalLimitData", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('CD')")
    public Collection<LoanDisbursalLimitData> retrieveAllLoanDisbursal() {
        this.context.authenticatedUser();

        final LoanDisbursalLimitMapper rm = new LoanDisbursalLimitMapper();
        final String sql = "select " + rm.schema() + " from m_loan_disbursal_limit lndsb ";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

    @Override
    @Cacheable(value = "DisbursalLimitData", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('CD')")
    public Collection<BranchAnalyticsAllData> retrieveAllBranchAnalyticsData() {
        this.context.authenticatedUser();

        final BranchAnalyticsAllDataMapper rm = new BranchAnalyticsAllDataMapper();
        final String sql = "select " + rm.schema() + " from m_branchAnalytics_dummy_data ba ";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

}
