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
            return " v.id as id, v.customer_id as customer_id from m_vehicle_loans v "
                    + " nv.customer_name as customer_name, nv.vehicle_type as vehicle_type, nv.dealer as dealer "
                    + " nv.invoice_number as invoice_number, nv.vehicle_type as vehicle_type, nv.dealer as dealer "
                    + " join m_apply_new_vehicle_loan nv on nv.loan_id = v.id " + " join m_apply_used_vehicle_loan uv on uv.loan_id = v.id "
                    + " join m_customer_details cd on nv.customerdetails_id = cd.id "
                    + " join m_vehicle_details vd on nv.vehicledetails_id = vd.id "
                    + " join m_customer_guarantor cg on nv.guarantordetails_id = cg.id "
                    + " join m_customer_bank_details bd on nv.bankdetails_id = cd.id "
                    + " join m_customer_details cd on nv.customerdetails_id = cd.id ";
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
