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
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.vlms.customer.data.CustomerDetailsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class CustomerVehicleLoanReadPlatformServiceImpl implements CustomerVehicleLoanReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    @Autowired
    public CustomerVehicleLoanReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class VehicleLoanMapper implements RowMapper<CustomerDetailsData> {

        public String schema() {
            return " au.id as id, au.customer_id as customer_id from m_appuser au, "
                    + " cnv.customer_name as customer_name, cnv.vehicle_type as vehicle_type, cnv.dealer as dealer, "
                    + " cnv.invoice_number as invoice_number, "

                    + " cnv.customer_name as customername, cnv.vehicle_type as vehicletype, cnv.dealer as dealer, "
                    + " cnv.invoice_number as invoicenumber, "

                    // customer details
                    + " cd.name as customer_name, cd.gender as gender, cd.dob as dob, "
                    + " cnv.maritalstatus as maritalstatus,  cd.spousename as spousename, cd.profession as profession, cd.proof_image_id as proofImageId, "

                    // communicationaddress cadd of customerdetails
                    + " cadd.street as street, cadd.address_line_1 as addressline1, cadd.address_line_2 as addressline2, "
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
                    + " cg.guarantor_name as guarantorname, cg.gender as gender, cg.dob as dob, "
                    + " cg.marital_status as maritalstatus,  cg.spouse_name as spousename, cg.profession as profession, "

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
                    + " bd.loan_eligible_amount as eligibleAmount, bd.account_type as accountType, "
                    + " bd.disbursal_type as disbursalType, bd.account_number as accountNumber, "
                    + " bd.account_holder_name as accountHolderName, bd.bank_name as bankName, "
                    + " bd.branch_name as branchName, bd.IFSC as IFSC, "

                    // vehicledetails vd of customer new vehicle
                    + " vd.vehicle_number as vehicleNnumber, vd.maker as accountType, " + " vd.maker as maker, vd.model as model, "
                    + " vd.color as color, vd.mfg_year as mfg_year, "
                    + " vd.engine_number as vehicleNnumber, vd.chassis_number as chassisNumber, "
                    + " vd.insurance_company as insuranceCompany, vd.insurance_policy as insurancePolicy, "
                    + " vd.insurance_expiry as insuranceExpiry, vd.pollution_cert_expiry as pollutionCertExpiry, "
                    + " vd.registration as registration, vd.live_km_reading as liveKmReading, "

                    // customer new vehicle completed

                    + " join m_apply_new_vehicle_loan cnv on cnv.customer_id = au.customer_id "
                    + " join m_customer_details cd on cnv.customerdetails_id = cd.id "
                    + " join m_vehicle_details vd on cnv.vehicledetails_id = vd.id "
                    + " join m_customer_guarantor cg on nv.guarantordetails_id = cg.id "
                    + " join m_customer_bank_details bd on nv.bankdetails_id = cd.id "

                    + " join m_address cadd on cd.communicationadd_id = cadd.id" // join the
                                                                                 // customer-communicationaddress
                    + " join m_address padd on cd.permanentadd_id = padd.id" // join the customer-permenantaddress
                    + " join m_address oadd on cd.officeadd_id = oadd.id" // join the customer-officeaddress

                    + " join m_address gcadd on cg.communicationadd_id = gcadd.id" // join the
                                                                                   // guarantor-communicationaddress
                    + " join m_address gpadd on cg.permanentadd_id = gpadd.id" // join the guarantor-permenantaddress
                    + " join m_address goadd on cg.officeadd_id = goadd.id"; // join the guarantor-officeaddress

            // + " join m_customer_details cd on nv.customerdetails_id = cd.id ";
        }

        @Override
        public CustomerDetailsData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String customerId = rs.getString("customer_id");

            final Long detailsId = rs.getLong("id");
            final String name = rs.getString("name");
            final String gender = rs.getString("gender");

            // final LocalDate dob = JdbcSupport.getLocalDate(rs, "overdueSinceDate");
            final LocalDate dob = JdbcSupport.getLocalDate(rs, "dob");
            final String maritalStatus = rs.getString("maritalstatus");
            final String profession = rs.getString("profession");
            final Long imageId = rs.getLong("proof_image_id");
            // final Long communicationAdd = rs.getLong("communicationadd_id");
            // final Long permanentAdd = rs.getLong("permanentadd_id");
            // final Long officeAdd = rs.getLong("officeadd_id");

            // fetched addresses and create address objects and inject in customerDetailsData

            return CustomerDetailsData.instance(name, gender, dob, maritalStatus, null, profession, null, null, null);
        }
    }

    @Override
    @Cacheable(value = "customerDetailsData", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('CD')")
    public Collection<CustomerDetailsData> retrieveAllCustomerVehicleLoan() {
        this.context.authenticatedUser();

        final VehicleLoanMapper rm = new VehicleLoanMapper();
        final String sql = "select " + rm.schema() + " order by nv.customer_name";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

}
