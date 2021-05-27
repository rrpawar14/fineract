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
package org.apache.fineract.vlms.cashier.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.vlms.cashier.data.CashierAnalyticsAllData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class CashierReadPlatformServiceImpl implements CashierModuleReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    @Autowired
    public CashierReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class CashierAnalyticsAllDataMapper implements RowMapper<CashierAnalyticsAllData> {

        public String schema() {
            return " ba.newloan as newloan, ba.closedloan as closedloan, ba.bank_cash_collection as bankCashCollection,"
                    + " ba.task_details_bm_open as taskDetailsBMOpen, ba.task_details_bm_closed as taskDetailsBMClosed, ba.task_details_bm_inprogress as taskDetailsBMInProgress,"
                    + " ba.task_details_ho_open as taskDetailsHOOpen, ba.task_details_ho_closed as taskDetailsHOClosed,  ba.task_details_ho_inprogress as taskDetailsHOInprogress ";
        }

        @Override
        public CashierAnalyticsAllData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long newloan = rs.getLong("newloan");

            final Long closedloan = rs.getLong("closedloan");

            final Long bankCashCollection = rs.getLong("bankCashCollection");

            final Long taskDetailsBMOpen = rs.getLong("taskDetailsBMOpen");

            final Long taskDetailsBMClosed = rs.getLong("taskDetailsBMClosed");

            final Long taskDetailsBMInProgress = rs.getLong("taskDetailsBMInProgress");

            final Long taskDetailsHOOpen = rs.getLong("taskDetailsHOOpen");

            final Long taskDetailsHOClosed = rs.getLong("taskDetailsHOClosed");

            final Long taskDetailsHOInprogress = rs.getLong("taskDetailsHOInprogress");

            return CashierAnalyticsAllData.instance(newloan, closedloan, bankCashCollection, taskDetailsBMOpen, taskDetailsBMClosed,
                    taskDetailsBMInProgress, taskDetailsHOOpen, taskDetailsHOClosed, taskDetailsHOInprogress);
        }
    }

    @Override
    @Cacheable(value = "CashierAnalyticsAllData", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('CD')")
    public Collection<CashierAnalyticsAllData> retrieveAllCashierAnalyticsData() {
        this.context.authenticatedUser();

        final CashierAnalyticsAllDataMapper rm = new CashierAnalyticsAllDataMapper();
        final String sql = "select " + rm.schema() + " from m_branchAnalytics_dummy_data ba ";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

}
