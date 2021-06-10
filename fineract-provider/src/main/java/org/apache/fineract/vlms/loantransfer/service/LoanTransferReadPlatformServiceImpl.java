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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.vlms.loantransfer.data.LoanTransferDashboardData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class LoanTransferReadPlatformServiceImpl implements LoanTransferReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    @Autowired
    public LoanTransferReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class LoanTransferDashboardMapper implements RowMapper<LoanTransferDashboardData> {

        public String schema() {
            return " ba.customerOnLine as customerOnLine, ba.customerLoanTransferred as customerLoanTransferred, ba.newBankAccount as newBankAccount,"
                    + " ba.loanAmountTransferred as loanAmountTransferred, ba.BankAccountVerified as BankAccountVerified ";
        }

        @Override
        public LoanTransferDashboardData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long customerOnLine = rs.getLong("customerOnLine");

            final Long customerLoanTransferred = rs.getLong("customerLoanTransferred");

            final Long newBankAccount = rs.getLong("newBankAccount");

            final Long loanAmountTransferred = rs.getLong("loanAmountTransferred");

            final Long BankAccountVerified = rs.getLong("BankAccountVerified");

            return LoanTransferDashboardData.instance(customerOnLine, customerLoanTransferred, newBankAccount, loanAmountTransferred,
                    BankAccountVerified);
        }
    }

    @Override
    @Cacheable(value = "LoanTransferAllData", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('CD')")
    public Collection<LoanTransferDashboardData> retrieveAllLoanTransferDashboardData() {
        this.context.authenticatedUser();

        final LoanTransferDashboardMapper rm = new LoanTransferDashboardMapper();
        final String sql = "select " + rm.schema() + " from m_branchAnalytics_dummy_data ba ";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

}
