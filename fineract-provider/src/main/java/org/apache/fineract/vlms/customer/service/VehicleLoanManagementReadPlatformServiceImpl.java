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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import org.apache.fineract.infrastructure.codes.data.CodeData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.vlms.customer.data.AddressData;
import org.apache.fineract.vlms.customer.data.BankDetailsData;
import org.apache.fineract.vlms.customer.data.CustomerDetailsData;
import org.apache.fineract.vlms.customer.data.GuarantorDetailsData;
import org.apache.fineract.vlms.customer.data.VehicleDetailsData;
import org.apache.fineract.vlms.customer.data.VehicleLoanData;
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
                    + " cadd.id as addId, cadd.street as street, cadd.address_line_1 as addressline1, cadd.address_line_2 as addressline2, "
                    + " cadd.city as city, cadd.postal_code as postalcode, cadd.landmark as landmark, "
                    + " cadd.area as area, cadd.state as state, "

                    // permanentaddress padd
                    + " padd.street as street, padd.address_line_1 as addressline1, padd.address_line_2 as addressline2, "
                    + " padd.city as city, padd.postal_code as postalcode, padd.landmark as landmark, "
                    + " padd.area as area, padd.state as state, "

                    // officeaddress oadd
                    + " oadd.street as street, oadd.address_line_1 as addressline1, oadd.address_line_2 as addressline2, "
                    + " oadd.city as city, oadd.postal_code as postalcode, oadd.landmark as landmark, "
                    + " oadd.area as area, oadd.state as state, "

                    // guarantor details
                    + " cg.id as guarantorDetailsId, cg.guarantor_name as guarantorName, cg.gender as gender, cg.dob as dob, cg.mobile_number as guarantorMobileNumber, "
                    + " cg.marital_status as maritalstatus,  cg.spouse_name as gspousename, cg.profession as profession, "

                    // communicationaddress gcadd of guarantordetails
                    + " gcadd.street as street, gcadd.address_line_1 as addressline1, gcadd.address_line_2 as addressline2, "
                    + " gcadd.city as city, gcadd.postal_code as postalcode, gcadd.landmark as landmark, "
                    + " gcadd.area as area, gcadd.state as state, "

                    // permanentaddress gpadd
                    + " gpadd.street as street, gpadd.address_line_1 as addressline1, gpadd.address_line_2 as addressline2, "
                    + " gpadd.city as city, gpadd.postal_code as postalcode, gpadd.landmark as landmark, "
                    + " gpadd.area as area, gpadd.state as state, "

                    // officeaddress goadd
                    + " goadd.street as street, goadd.address_line_1 as addressline1, goadd.address_line_2 as addressline2, "
                    + " goadd.city as city, goadd.postal_code as postalcode, goadd.landmark as landmark, "
                    + " goadd.area as area, goadd.state as state, "

                    // bankdetails bd of customer new vehicle
                    + " bd.id as bankDetailsId, bd.loan_eligible_amount as eligibleAmount, bd.account_type as accountType, "
                    + " bd.disbursal_type as disbursalType, bd.account_number as accountNumber, "
                    + " bd.account_holder_name as accountHolderName, bd.bank_name as bankName, "
                    + " bd.branch_name as branchName, bd.IFSC as IFSC, "

                    // vehicledetails vd of customer new vehicle
                    + " vd.id as vehicleId, vd.vehicle_number as vehicleNumber," + " vd.maker as maker, vd.model as model, "
                    + " vd.color as color, vd.mfg_year as mfgYear, "
                    + " vd.engine_number as engineNumber, vd.chassis_number as chassisNumber, "
                    + " vd.insurance_company as insuranceCompany, vd.insurance_policy as insurancePolicy, "
                    + " vd.insurance_expiry as insuranceExpiry, vd.pollution_cert_expiry as pollutionCertExpiry, "
                    + " vd.registration as registration, vd.live_km_reading as liveKmReading  ";

            // customer new vehicle completed

            ///////////////////////////////////////////////////////////

            // customer used vehicle cuv
            /*
             * + " cnv.customer_name as customerName, cnv.vehicle_type as vehicleType, cnv.loan_id as loanId, " +
             * " cnv.loan_type as loanType, "
             *
             * // customer details uvcd + " uvcd.name as customer_name, uvcd.gender as gender, uvcd.dob as dob, " +
             * " uvcd.maritalstatus as maritalstatus,  uvcd.spousename as spousename, uvcd.profession as profession, uvcd.proof_image_id as proofImageId, "
             *
             * // communicationaddress uvcadd of customerdetails +
             * " uvcadd.street as street, uvcadd.address_line_1 as addressline1, uvcadd.address_line_2 as addressline2, "
             * + " uvcadd.city as city, uvcadd.postal_code as postalcode, uvcadd.landmark as landmark, " +
             * " uvcadd.area as area, uvcadd.state as state, "
             *
             * // permanentaddress uvpadd +
             * " uvpadd.street as street, uvpadd.address_line_1 as addressline1, uvpadd.address_line_2 as addressline2, "
             * + " uvpadd.city as city, uvpadd.postal_code as postalcode, uvpadd.landmark as landmark, " +
             * " uvpadd.area as area, uvpadd.state as state, "
             *
             * // officeaddress uvoadd +
             * " uvoadd.street as street, uvoadd.address_line_1 as addressline1, uvoadd.address_line_2 as addressline2, "
             * + " uvoadd.city as city, uvoadd.postal_code as postalcode, uvoadd.landmark as landmark, " +
             * " uvoadd.area as area, uvoadd.state as state, "
             *
             * // guarantor details uvcg +
             * " uvcg.guarantor_name as guarantorName, uvcg.mobile_number as guarantorMobileNumber, uvcg.gender as gender, uvcg.dob as dob, "
             * +
             * " uvcg.marital_status as maritalStatus,  uvcg.spouse_name as spousename, uvcg.profession as profession, "
             *
             * // communicationaddress uvgcadd of guarantordetails +
             * " uvgcadd.street as street, uvgcadd.address_line_1 as addressline1, uvgcadd.address_line_2 as addressline2, "
             * + " uvgcadd.city as city, uvgcadd.postal_code as postalcode, uvgcadd.landmark as landmark, " +
             * " uvgcadd.area as area, uvgcadd.state as state, "
             *
             * // permanentaddress uvgpadd +
             * " uvgpadd.street as street, uvgpadd.address_line_1 as addressline1, uvgpadd.address_line_2 as addressline2, "
             * + " uvgpadd.city as city, uvgpadd.postal_code as postalcode, uvgpadd.landmark as landmark, " +
             * " uvgpadd.area as area, uvgpadd.state as state, "
             *
             * // officeaddress uvgoadd +
             * " uvgoadd.street as street, uvgoadd.address_line_1 as addressline1, uvgoadd.address_line_2 as addressline2, "
             * + " uvgoadd.city as city, uvgoadd.postal_code as postalcode, uvgoadd.landmark as landmark, " +
             * " uvgoadd.area as area, uvgoadd.state as state, "
             *
             * // bankdetails uvbd of customer new vehicle +
             * " uvbd.loan_eligible_amount as eligibleAmount, uvbd.account_type as accountType, " +
             * " uvbd.disbursal_type as disbursalType, uvbd.account_number as accountNumber, " +
             * " uvbd.account_holder_name as accountHolderName, uvbd.bank_name as bankName, " +
             * " uvbd.branch_name as branchName, uvbd.IFSC as IFSC, "
             *
             * // vehicledetails uvd of customer new vehicle +
             * " uvd.vehicle_number as vehicleNumber, uvd.maker as accountType, " +
             * " uvd.maker as maker, uvd.model as model, " + " uvd.color as color, uvd.mfg_year as mfgYear, " +
             * " uvd.engine_number as engineNumber, uvd.chassis_number as chassisNumber, " +
             * " uvd.insurance_company as insuranceCompany, uvd.insurance_policy as insurancePolicy, " +
             * " uvd.insurance_expiry as insuranceExpiry, uvd.pollution_cert_expiry as pollutionCertExpiry, " +
             * " uvd.registration as registration, uvd.live_km_reading as liveKmReading from m_apply_used_vehicle_loan cuv "
             */

            // customer used vehicle completed

            // join the customer new vehicle
            // + " from m_appuser au join m_apply_new_vehicle_loan cnv on cnv.customer_id = au.customer_id "

            // join the customer used vehicle
            /*
             * + " join m_apply_used_vehicle_loan cuv on cuv.customer_id = au.customer_id " +
             * " join m_customer_details uvcd on cuv.customerdetails_id = uvcd.id " +
             * " join m_vehicle_details uvd on cuv.vehicledetails_id = uvd.id " +
             * " join m_customer_guarantor uvcg on cuv.guarantordetails_id = uvcg.id " +
             * " join m_customer_bank_details uvbd on cuv.bankdetails_id = uvbd.id "
             *
             * + " join m_address uvcadd on uvcd.communicationadd_id = uvcadd.id " //
             * customer-usedvehiclecommunicationaddress
             *
             * + " join m_address uvpadd on uvcd.permanentadd_id = uvpadd.id " // customer-usedvehiclepermenantaddress +
             * " join m_address uvoadd on uvcd.officeadd_id = uvoadd.id " // customer-usedvehicleofficeaddress
             *
             * + " join m_address uvgcadd on uvcg.communicationadd_id = uvgcadd.id " // guarantor-communicationaddress
             *
             * + " join m_address uvgpadd on uvcg.permanentadd_id = uvgpadd.id " // join the //
             * guarantor-permenantaddress + " join m_address uvgoadd on uvcg.officeadd_id = uvgoadd.id "; // join the
             * guarantor-officeaddress
             *
             * // + " join m_customer_details cd on nv.customerdetails_id = cd.id ";
             */
        }

        public String schemaJoin() {
            return " join m_customer_details cd on cnv.customerdetails_id = cd.id "
                    + " join m_vehicle_details vd on cnv.vehicledetails_id = vd.id "
                    + " left join m_customer_guarantor cg on cnv.guarantordetails_id = cg.id "
                    + " left join m_customer_bank_details bd on cnv.bankdetails_id = bd.id "

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

            final Long addId = rs.getLong("addId");
            final String street = rs.getString("street");
            final String addressLine1 = rs.getString("addressLine1");
            final String addressLine2 = rs.getString("addressLine2");
            final String city = rs.getString("city");

            final String pincode = rs.getString("postalcode");
            final String landmark = rs.getString("landmark");
            final String area = rs.getString("area");
            final String state = rs.getString("state");

            final AddressData communicationAddressData = new AddressData(addId, addressLine1, addressLine2, city, pincode, landmark, area,
                    state);

            final AddressData permanentAddressData = new AddressData(addId, addressLine1, addressLine2, city, pincode, landmark, area,
                    state);

            final AddressData officeAddressData = new AddressData(addId, addressLine1, addressLine2, city, pincode, landmark, area, state);

            CustomerDetailsData customerDetailsData = new CustomerDetailsData(customerDetailsId, customerName, gender, mobileNumber,
                    alternateNumber, fatherName, applicantType, referBy, companyName, monthlyIncome, salaryDate, salaryPeriod, dob,
                    maritalStatus, spouseName, profession, communicationAddressData, permanentAddressData, officeAddressData);

            final Long guarantorDetailsId = rs.getLong("guarantorDetailsId");
            final String guarantorName = rs.getString("guarantorName");
            final String guarantorMobileNumber = rs.getString("guarantorMobileNumber");
            // final String gender = rs.getString("gender");
            // final LocalDate dob = JdbcSupport.getLocalDate(rs, "dob");

            // final String maritalStatus = rs.getString("maritalStatus");
            final String gspouseName = rs.getString("gspousename");
            // final String profession = rs.getString("profession");
            // final String state = rs.getString("state");

            GuarantorDetailsData guarantorDetailsData = new GuarantorDetailsData(guarantorDetailsId, guarantorName, guarantorMobileNumber,
                    gender, dob, maritalStatus, gspouseName, profession, communicationAddressData, permanentAddressData, officeAddressData);

            final Long bankDetailsId = rs.getLong("bankDetailsId");
            final Long eligibleAmount = rs.getLong("eligibleAmount");
            final String accountType = rs.getString("accountType");
            final String disbursalType = rs.getString("disbursalType");
            final String accountNumber = rs.getString("accountNumber");

            final String accountHolderName = rs.getString("accountHolderName");
            final String bankName = rs.getString("bankName");
            final String branchName = rs.getString("branchName");
            final String IFSC = rs.getString("IFSC");

            BankDetailsData bankDetailsData = new BankDetailsData(bankDetailsId, eligibleAmount, accountType, disbursalType, accountNumber,
                    accountHolderName, bankName, branchName, IFSC);

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
                    vehicleDetailsData, bankDetailsData);

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
                    + " where cnv.customer_id=? order by cnv.customer_name";

            return this.jdbcTemplate.query(sql, rm, new Object[] { userId });
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

}
