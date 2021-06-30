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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import org.apache.fineract.infrastructure.codes.data.CodeData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.vlms.customer.data.AddressData;
import org.apache.fineract.vlms.customer.data.BranchAnalyticsData;
import org.apache.fineract.vlms.customer.data.CustomerDetailsData;
import org.apache.fineract.vlms.customer.data.GuarantorDetailsData;
import org.apache.fineract.vlms.customer.data.LoanDetailsData;
import org.apache.fineract.vlms.customer.data.VehicleDetailsData;
import org.apache.fineract.vlms.customer.data.VehicleLoanData;
import org.apache.fineract.vlms.fieldexecutive.data.LoanChangeRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class VehicleLoanManagementReadPlatformServiceImpl implements VehicleLoanManagementReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    @Autowired
    public VehicleLoanManagementReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class VehicleLoanMapper implements RowMapper<VehicleLoanData> {

        public String schema() {
            return // " au.id as id, au.customer_id as customerId, "
            // " au.id as id, "

            // customer new vehicle
            " cnv.id as loanId, cnv.customer_name as customerName,cnv.loan_type as loanType, cnv.vehicle_type as vehicleType, cnv.dealer as dealer, "
                    + " cnv.invoice_number as invoicenumber, "

                    // customer details
                    + " cd.id as customerDetailsId, cd.name as customer_name, cd.gender as gender, cd.mobile_number as mobileNumber,"
                    + " cd.alternate_number as alternateNumber, cd.father_name as fatherName, cd.applicant_type as applicantType,"
                    + " cd.refer_by as referBy, cd.company_name as companyName, cd.monthly_income as monthlyIncome, cd.salary_date as salaryDate,"
                    + " cd.salary_period as salaryPeriod, cd.dob as dob, "
                    + " cd.marital_status as maritalstatus,  cd.spousename as spouseName, cd.profession as profession, cd.proof_image_id as proofImageId, "

                    // communicationaddress cadd of customerdetails
                    + " cadd.id as ccaddId, cadd.street as ccstreet, cadd.address_line_1 as ccaddressline1, cadd.address_line_2 as ccaddressline2, "
                    + " cadd.city as cccity, cadd.postal_code as ccpostalcode, cadd.landmark as cclandmark, "
                    + " cadd.area as ccarea, cadd.state as ccstate, "

                    // permanentaddress padd
                    + " padd.id as cpaddId, padd.street as cpstreet, padd.address_line_1 as cpaddressline1, padd.address_line_2 as cpaddressline2, "
                    + " padd.city as cpcity, padd.postal_code as cppostalcode, padd.landmark as cplandmark, "
                    + " padd.area as cparea, padd.state as cpstate, "

                    // officeaddress oadd
                    + " oadd.id as coaddId, oadd.street as costreet, oadd.address_line_1 as coaddressline1, oadd.address_line_2 as coaddressline2, "
                    + " oadd.city as cocity, oadd.postal_code as copostalcode, oadd.landmark as colandmark, "
                    + " oadd.area as coarea, oadd.state as costate, "

                    // guarantor details
                    + " cg.id as guarantorDetailsId, cg.guarantor_name as guarantorName, cg.gender as ggender, cg.dob as gdob, cg.mobile_number as guarantorMobileNumber, "
                    + " cg.marital_status as gmaritalstatus,  cg.spouse_name as gspousename, cg.profession as gprofession, "

                    // communicationaddress gcadd of guarantordetails
                    + " gcadd.id as gcaddId, gcadd.street as gcstreet, gcadd.address_line_1 as gcaddressline1, gcadd.address_line_2 as gcaddressline2, "
                    + " gcadd.city as gccity, gcadd.postal_code as gcpostalcode, gcadd.landmark as gclandmark, "
                    + " gcadd.area as gcarea, gcadd.state as gcstate, "

                    // permanentaddress gpadd
                    + " gpadd.id as gpaddId, gpadd.street as gpstreet, gpadd.address_line_1 as gpaddressline1, gpadd.address_line_2 as gpaddressline2, "
                    + " gpadd.city as gpcity, gpadd.postal_code as gppostalcode, gpadd.landmark as gplandmark, "
                    + " gpadd.area as gparea, gpadd.state as gpstate, "

                    // officeaddress goadd
                    + " goadd.id as goaddId, goadd.street as gostreet, goadd.address_line_1 as goaddressline1, goadd.address_line_2 as goaddressline2, "
                    + " goadd.city as gocity, goadd.postal_code as gopostalcode, goadd.landmark as golandmark, "
                    + " goadd.area as goarea, goadd.state as gostate, "

                    // bankdetails bd of customer new vehicle
                    /*
                     * +
                     * " bd.id as bankDetailsId, bd.loan_eligible_amount as eligibleAmount, bd.account_type as accountType, "
                     * + " bd.disbursal_type as disbursalType, bd.account_number as accountNumber, " +
                     * " bd.account_holder_name as accountHolderName, bd.bank_name as bankName, " +
                     * " bd.branch_name as branchName, bd.IFSC as IFSC, "
                     */

                    // loandetails ld
                    + " ld.id as loandetailsId, ld.principal_amount_proposed as principal, ld.term_frequency as loanTerm, "
                    + " ld.nominal_interest_rate_per_period as loanInterest, ld.expected_disbursedon_date as dueDate, "

                    + " ld.interest_charged_derived as interestInr,"

                    // + " ld.payout as payout, "

                    + " cr.id as changeRequestId, cr.loan_id as cloandetailsId, cr.loan_interest as changeLoanInterest, cr.loan_amount as changeLoanAmount, "
                    + " cr.doc_charges as changeDocCharges, cr.processing_charges as changeProcessingCharges, cr.loan_closure_amount as loanClosureAmount, "

                    // vehicledetails vd of customer new vehicle
                    + " vd.id as vehicleId, vd.vehicle_number as vehicleNumber," + " vd.maker as maker, vd.model as model, "
                    + " vd.color as color, vd.mfg_year as mfgYear, "
                    + " vd.engine_number as engineNumber, vd.chassis_number as chassisNumber, "
                    + " vd.insurance_company as insuranceCompany, vd.insurance_policy as insurancePolicy, "
                    + " vd.insurance_expiry as insuranceExpiry, vd.pollution_cert_expiry as pollutionCertExpiry, "
                    + " vd.registration as registration, vd.live_km_reading as liveKmReading  ";

        }

        public String schemaJoin() {
            return " join m_customer_details cd on cnv.customerdetails_id = cd.id "
                    + " join m_vehicle_details vd on cnv.vehicledetails_id = vd.id "
                    + " left join m_customer_guarantor cg on cnv.guarantordetails_id = cg.id "
                    + " left join m_customer_bank_details bd on cnv.bankdetails_id = bd.id "
                    + " left join m_loan ld on cnv.loandetails_id = ld.id " + " left join m_loan_change_request cr on cr.loan_id = ld.id "
                    // + " left join m_loan_charge lc on lc.loan_id = ld.id "
                    + " left join m_address cadd on cd.communicationadd_id = cadd.id  "

                    + " left join m_address padd on cd.permanentadd_id = padd.id "
                    + " left join m_address oadd on cd.officeadd_id = oadd.id "

                    + " left join m_address gcadd on cg.communicationadd_id = gcadd.id  "

                    + "  left join m_address gpadd on cg.permanentadd_id = gpadd.id "
                    + " left join m_address goadd on cg.officeadd_id = goadd.id ";
        }

        @Override
        public VehicleLoanData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            // final Long id = rs.getLong("id");
            // final String customerId = rs.getString("customerId");

            // final Long detailsId = rs.getLong("id");
            final Long loanId = rs.getLong("loanId");
            final Long customerDetailsId = rs.getLong("customerDetailsId");
            final String customerName = rs.getString("customerName");
            final String gender = rs.getString("gender");
            final String mobileNumber = rs.getString("mobileNumber");
            final String alternateNumber = rs.getString("alternateNumber");
            final String fatherName = rs.getString("fatherName");
            final String applicantType = rs.getString("applicantType");
            final String referBy = rs.getString("referBy");
            final String companyName = rs.getString("companyName");
            final String monthlyIncome = rs.getString("monthlyIncome");
            final LocalDate salaryDate = JdbcSupport.getLocalDate(rs, "salaryDate");
            final String salaryPeriod = rs.getString("salaryPeriod");

            // final LocalDate dob = JdbcSupport.getLocalDate(rs, "overdueSinceDate");
            final LocalDate dob = JdbcSupport.getLocalDate(rs, "dob");
            final String maritalStatus = rs.getString("maritalstatus");
            final String spouseName = rs.getString("spouseName");
            final String profession = rs.getString("profession");
            final Long imageId = rs.getLong("proofImageId");
            // final Long communicationAdd = rs.getLong("communicationadd_id");
            // final Long permanentAdd = rs.getLong("permanentadd_id");
            // final Long officeAdd = rs.getLong("officeadd_id");

            // fetched addresses and create address objects and inject in customerDetailsData

            Long addId = rs.getLong("ccaddId");
            String street = rs.getString("ccstreet");
            String addressLine1 = rs.getString("ccaddressLine1");
            String addressLine2 = rs.getString("ccaddressLine2");
            String city = rs.getString("cccity");

            String pincode = rs.getString("ccpostalcode");
            String landmark = rs.getString("cclandmark");
            String area = rs.getString("ccarea");
            String state = rs.getString("ccstate");

            final AddressData communicationAddressData = new AddressData(addId, addressLine1, addressLine2, city, pincode, landmark, area,
                    state);

            addId = rs.getLong("cpaddId");
            street = rs.getString("cpstreet");
            addressLine1 = rs.getString("cpaddressLine1");
            addressLine2 = rs.getString("cpaddressLine2");
            city = rs.getString("cpcity");

            pincode = rs.getString("cppostalcode");
            landmark = rs.getString("cplandmark");
            area = rs.getString("cparea");
            state = rs.getString("cpstate");

            final AddressData permanentAddressData = new AddressData(addId, addressLine1, addressLine2, city, pincode, landmark, area,
                    state);

            addId = rs.getLong("coaddId");
            street = rs.getString("costreet");
            addressLine1 = rs.getString("coaddressLine1");
            addressLine2 = rs.getString("coaddressLine2");
            city = rs.getString("cocity");

            pincode = rs.getString("copostalcode");
            landmark = rs.getString("colandmark");
            area = rs.getString("coarea");
            state = rs.getString("costate");

            final AddressData officeAddressData = new AddressData(addId, addressLine1, addressLine2, city, pincode, landmark, area, state);

            CustomerDetailsData customerDetailsData = new CustomerDetailsData(customerDetailsId, customerName, gender, mobileNumber,
                    alternateNumber, fatherName, applicantType, referBy, companyName, monthlyIncome, salaryDate, salaryPeriod, dob,
                    maritalStatus, spouseName, profession, communicationAddressData, permanentAddressData, officeAddressData);

            final Long guarantorDetailsId = rs.getLong("guarantorDetailsId");
            final String guarantorName = rs.getString("guarantorName");
            final String guarantorMobileNumber = rs.getString("guarantorMobileNumber");
            final String ggender = rs.getString("ggender");
            final LocalDate gdob = JdbcSupport.getLocalDate(rs, "gdob");

            final String gmaritalStatus = rs.getString("gmaritalstatus");
            final String gspouseName = rs.getString("gspousename");
            final String gprofession = rs.getString("gprofession");
            // final String state = rs.getString("state");

            addId = rs.getLong("gcaddId");
            street = rs.getString("gcstreet");
            addressLine1 = rs.getString("gcaddressLine1");
            addressLine2 = rs.getString("gcaddressLine2");
            city = rs.getString("gccity");

            pincode = rs.getString("gcpostalcode");
            landmark = rs.getString("gclandmark");
            area = rs.getString("gcarea");
            state = rs.getString("gcstate");

            final AddressData guarantor_communicationAddressData = new AddressData(addId, addressLine1, addressLine2, city, pincode,
                    landmark, area, state);

            addId = rs.getLong("gpaddId");
            street = rs.getString("gpstreet");
            addressLine1 = rs.getString("gpaddressLine1");
            addressLine2 = rs.getString("gpaddressLine2");
            city = rs.getString("gpcity");

            pincode = rs.getString("gppostalcode");
            landmark = rs.getString("gplandmark");
            area = rs.getString("gparea");
            state = rs.getString("gpstate");

            final AddressData guarantor_permanentAddressData = new AddressData(addId, addressLine1, addressLine2, city, pincode, landmark,
                    area, state);

            addId = rs.getLong("goaddId");
            street = rs.getString("gostreet");
            addressLine1 = rs.getString("goaddressLine1");
            addressLine2 = rs.getString("goaddressLine2");
            city = rs.getString("gocity");

            pincode = rs.getString("gopostalcode");
            landmark = rs.getString("golandmark");
            area = rs.getString("goarea");
            state = rs.getString("gostate");

            final AddressData guarantor_officeAddressData = new AddressData(addId, addressLine1, addressLine2, city, pincode, landmark,
                    area, state);

            GuarantorDetailsData guarantorDetailsData = new GuarantorDetailsData(guarantorDetailsId, guarantorName, guarantorMobileNumber,
                    ggender, gdob, gmaritalStatus, gspouseName, gprofession, guarantor_communicationAddressData,
                    guarantor_permanentAddressData, guarantor_officeAddressData);

            /*
             * final Long bankDetailsId = rs.getLong("bankDetailsId"); final Long eligibleAmount =
             * rs.getLong("eligibleAmount"); final String accountType = rs.getString("accountType"); final String
             * disbursalType = rs.getString("disbursalType"); final String accountNumber =
             * rs.getString("accountNumber");
             *
             * final String accountHolderName = rs.getString("accountHolderName"); final String bankName =
             * rs.getString("bankName"); final String branchName = rs.getString("branchName"); final String IFSC =
             * rs.getString("IFSC");
             *
             * BankDetailsData bankDetailsData = new BankDetailsData(bankDetailsId, eligibleAmount, accountType,
             * disbursalType, accountNumber, accountHolderName, bankName, branchName, IFSC);
             */
            // final BigDecimal loanAmount = rs.getBigDecimal("loanAmount");
            // final BigDecimal loanInterest = rs.getBigDecimal("loanInterest");

            final Long loandetailsId = rs.getLong("loandetailsId");
            final Long loanTerm = rs.getLong("loanTerm");
            final BigDecimal principal = rs.getBigDecimal("principal");
            final BigDecimal loanInterest = rs.getBigDecimal("loanInterest");
            final BigDecimal interestInr = rs.getBigDecimal("interestInr");

            final LocalDate dueDate = JdbcSupport.getLocalDate(rs, "dueDate");

            LoanDetailsData loandetailsData = new LoanDetailsData(loandetailsId, principal, loanTerm, loanInterest, interestInr, dueDate);

            final Long changeRequestId = rs.getLong("changeRequestId");
            final Long cloandetailsId = rs.getLong("cloandetailsId");
            final BigDecimal changeLoanInterest = rs.getBigDecimal("changeLoanInterest");
            final BigDecimal changeLoanAmount = rs.getBigDecimal("changeLoanAmount");

            final BigDecimal changeDocCharges = rs.getBigDecimal("changeDocCharges");
            final BigDecimal changeProcessingCharges = rs.getBigDecimal("changeProcessingCharges");
            final BigDecimal loanClosureAmount = rs.getBigDecimal("loanClosureAmount");

            LoanChangeRequestData loanChangeRequestData = new LoanChangeRequestData(changeRequestId, cloandetailsId, changeLoanInterest,
                    changeLoanAmount, changeDocCharges, changeProcessingCharges, loanClosureAmount);

            final Long vehicleId = rs.getLong("vehicleId");
            final String vehicleNumber = rs.getString("vehicleNumber");
            final String maker = rs.getString("maker");
            final String color = rs.getString("color");
            final String model = rs.getString("model");
            final String mfgYear = rs.getString("mfgYear");
            final String engineNumber = rs.getString("engineNumber");

            final String chassisNumber = rs.getString("chassisNumber");
            final String insuranceCompany = rs.getString("insuranceCompany");
            final String insurancePolicy = rs.getString("insurancePolicy");
            final LocalDate insuranceExpiry = JdbcSupport.getLocalDate(rs, "insuranceExpiry");
            final LocalDate pollutionCertExpiry = JdbcSupport.getLocalDate(rs, "pollutionCertExpiry");
            final LocalDate registration = JdbcSupport.getLocalDate(rs, "registration");
            final Long liveKmReading = rs.getLong("liveKmReading");

            VehicleDetailsData vehicleDetailsData = new VehicleDetailsData(vehicleId, vehicleNumber, maker, model, color, mfgYear,
                    engineNumber, chassisNumber, insuranceCompany, insurancePolicy, insuranceExpiry, pollutionCertExpiry, registration,
                    liveKmReading);

            final String vehicleType = rs.getString("vehicleType");
            final String loanType = rs.getString("loanType");

            return VehicleLoanData.instance(loanId, customerName, vehicleType, loanType, customerDetailsData, guarantorDetailsData,
                    vehicleDetailsData, loandetailsData, loanChangeRequestData);

        }
    }

    private static final class CustomerDetailsMapper implements RowMapper<CustomerDetailsData> {

        public String schema() {
            return " cd.id as customerDetailsId, cd.name as customerName, cd.gender as gender, cd.mobile_number as mobileNumber,"
                    + " cd.alternate_number as alternateNumber, cd.father_name as fatherName, cd.applicant_type as applicantType,"
                    + " cd.refer_by as referBy, cd.company_name as companyName, cd.monthly_income as monthlyIncome, cd.salary_date as salaryDate,"
                    + " cd.salary_period as salaryPeriod, cd.dob as dob, "
                    + " cd.marital_status as maritalstatus,  cd.spousename as spouseName, cd.profession as profession, cd.proof_image_id as proofImageId ";
        }

        @Override
        public CustomerDetailsData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long customerDetailsId = rs.getLong("customerDetailsId");
            final String customerName = rs.getString("customerName");
            final String gender = rs.getString("gender");
            final String mobileNumber = rs.getString("mobileNumber");
            final String alternateNumber = rs.getString("alternateNumber");
            final String fatherName = rs.getString("fatherName");
            final String applicantType = rs.getString("applicantType");
            final String referBy = rs.getString("referBy");
            final String companyName = rs.getString("companyName");
            final String monthlyIncome = rs.getString("monthlyIncome");
            final LocalDate salaryDate = JdbcSupport.getLocalDate(rs, "salaryDate");
            final String salaryPeriod = rs.getString("salaryPeriod");

            // final LocalDate dob = JdbcSupport.getLocalDate(rs, "overdueSinceDate");
            final LocalDate dob = JdbcSupport.getLocalDate(rs, "dob");
            final String maritalStatus = rs.getString("maritalstatus");
            final String spouseName = rs.getString("spouseName");
            final String profession = rs.getString("profession");
            final Long imageId = rs.getLong("proofImageId");
            /*
             * final Long addId = rs.getLong("addId"); final String street = rs.getString("street"); final String
             * addressLine1 = rs.getString("addressLine1"); final String addressLine2 = rs.getString("addressLine2");
             * final String city = rs.getString("city");
             *
             * final String pincode = rs.getString("postalcode"); final String landmark = rs.getString("landmark");
             * final String area = rs.getString("area"); final String state = rs.getString("state");
             *
             * final AddressData communicationAddressData = new AddressData(addId, addressLine1, addressLine2, city,
             * pincode, landmark, area, state);
             *
             * final AddressData permanentAddressData = new AddressData(addId, addressLine1, addressLine2, city,
             * pincode, landmark, area, state);
             *
             * final AddressData officeAddressData = new AddressData(addId, addressLine1, addressLine2, city, pincode,
             * landmark, area, state);
             */

            return CustomerDetailsData.instance(customerDetailsId, customerName, gender, mobileNumber, alternateNumber, fatherName,
                    applicantType, referBy, companyName, monthlyIncome, salaryDate, salaryPeriod, dob, maritalStatus, spouseName,
                    profession, null, null, null);
        }

    }

    @Override
    @Cacheable(value = "VehicleLoanData", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('CD')")
    public Collection<VehicleLoanData> retrieveAllCustomerVehicleLoan() {
        this.context.authenticatedUser();

        final VehicleLoanMapper rm = new VehicleLoanMapper();
        final String sql = "select " + rm.schema() + " from m_apply_vehicle_loan cnv " + rm.schemaJoin() + " order by cnv.customer_name ";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

    @Override
    @Cacheable(value = "VehicleLoanData", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('CD')")
    public Collection<VehicleLoanData> retrieveVehicleLoanByUserId(final Long userId) {
        try {
            this.context.authenticatedUser();

            final VehicleLoanMapper rm = new VehicleLoanMapper();
            final String sql = "select au.id as id, au.customer_id as customerId, " + rm.schema()
                    + " from  m_apply_vehicle_loan cnv left join m_appuser au  on cnv.customer_id = au.customer_id " + rm.schemaJoin()
                    + " where cnv.customer_id=? order by cnv.created_date DESC ";

            return this.jdbcTemplate.query(sql, rm, new Object[] { userId });
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    @Cacheable(value = "VehicleLoanData", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('CD')")
    public Collection<VehicleLoanData> retrieveVehicleLoanByMobileNumber(final String mobileNo) {
        try {
            this.context.authenticatedUser();

            final VehicleLoanMapper rm = new VehicleLoanMapper();
            final String sql = "select au.id as id, au.customer_id as customerId, " + rm.schema()
                    + " from  m_apply_vehicle_loan cnv left join m_appuser au  on cnv.customer_id = au.customer_id " + rm.schemaJoin()
                    + " where cd.mobile_number=? order by cnv.created_date DESC ";

            return this.jdbcTemplate.query(sql, rm, new Object[] { mobileNo });
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    @Cacheable(value = "CustomerDetailsData", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('CD')")
    public Collection<CustomerDetailsData> retrieveCustomerData() {
        try {
            this.context.authenticatedUser();

            final CustomerDetailsMapper rm = new CustomerDetailsMapper();
            final String sql = " select" + rm.schema() + " from m_customer_details cd ";

            return this.jdbcTemplate.query(sql, rm, new Object[] {});
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

    private static final class MobileNumberMapper implements RowMapper<CodeData> {

        public String schema() {
            return " au.id as id, au.username as mobileNo, au.enabled as enabled ";
        }

        @Override
        public CodeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String mobileNumber = rs.getString("mobileNo");
            final boolean enabled = rs.getBoolean("enabled");

            return CodeData.instance(id, mobileNumber, enabled);
        }
    }

    private static final class BranchAnalyticsMapper implements RowMapper<BranchAnalyticsData> {

        public String schema() {
            return " SELECT COUNT(id) FROM m_loan_details ";
        }

        @Override
        public BranchAnalyticsData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long data = rs.getLong("data");

            return BranchAnalyticsData.instance(data);
        }
    }

    @Override
    @Cacheable(value = "VehicleLoanData", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('CD')")
    public VehicleLoanData retrieveVehicleLoanByLoanId(final Long loanId) {
        try {
            this.context.authenticatedUser();

            final VehicleLoanMapper rm = new VehicleLoanMapper();
            final String sql = "select au.id as id, au.customer_id as customerId, " + rm.schema()
                    + " from  m_apply_vehicle_loan cnv left join m_appuser au  on cnv.customer_id = au.customer_id " + rm.schemaJoin()
                    + " where cnv.id=? order by cnv.customer_name";

            return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { loanId });
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    @Cacheable(value = "VehicleLoanData", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('CD')")
    public CodeData checkMobileNumberStatus(final Long mobileNo) {
        try {
            this.context.authenticatedUser();

            final MobileNumberMapper rm = new MobileNumberMapper();
            final String sql = "select " + rm.schema() + " from m_appuser au where username = ?";

            return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { mobileNo });
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    @Cacheable(value = "VehicleLoanData", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('CD')")
    public BranchAnalyticsData getBranchAnalyticsData(final String commandParam) {
        try {
            this.context.authenticatedUser();
            String sql = null;

            final BranchAnalyticsMapper rm = new BranchAnalyticsMapper();
            if (commandParam.equals("loanApplications")) {
                sql = " SELECT COUNT(id) as data FROM m_loan_details ";

            } else if (commandParam.equals("enquiry")) {
                sql = " SELECT COUNT(id) as data FROM m_customerloanenquiry ";
            } else if (commandParam.equals("customerOnboard")) {
                sql = "SELECT COUNT(id) as data FROM m_customer_details";

            }
            return this.jdbcTemplate.queryForObject(sql, rm, new Object[] {});
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

}
