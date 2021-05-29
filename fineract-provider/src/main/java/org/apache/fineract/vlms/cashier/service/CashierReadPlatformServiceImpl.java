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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.vlms.cashier.data.CashierAnalyticsAllData;
import org.apache.fineract.vlms.cashier.data.HLPaymentData;
import org.apache.fineract.vlms.cashier.data.VoucherData;
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

    private static final class HLPaymentAllDataMapper implements RowMapper<HLPaymentData> {

        public String schema() {
            return " hl.id as id, hl.agtno as AGTNO, hl.customerName as customerName,"
                    + " hl.actualAmount as actualAmount, hl.postAmount as postAmount, hl.postDate as postDate,"
                    + " hl.postType as postType, hl.agent as agent, hl.expiryDate as expiryDate,"
                    + " hl.policyNo as policyNo, hl.insuranceCompany as insuranceCompany,  hl.remark as remark ";
        }

        @Override
        public HLPaymentData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");

            final String AGTNO = rs.getString("AGTNO");

            final String customerName = rs.getString("customerName");

            final BigDecimal actualAmount = rs.getBigDecimal("actualAmount");

            final BigDecimal postAmount = rs.getBigDecimal("postAmount");

            final LocalDate postDate = JdbcSupport.getLocalDate(rs, "postDate");

            final String postType = rs.getString("postType");

            final String agent = rs.getString("agent");

            final LocalDate expiryDate = JdbcSupport.getLocalDate(rs, "expiryDate");

            final String policyNo = rs.getString("policyNo");

            final String insuranceCompany = rs.getString("insuranceCompany");

            final String remark = rs.getString("remark");

            return HLPaymentData.instance(id, AGTNO, customerName, actualAmount, postAmount, postDate, postType, agent, expiryDate,
                    policyNo, insuranceCompany, remark);
        }
    }

    private static final class VoucherDataMapper implements RowMapper<VoucherData> {

        public String schema() {
            return " v.id as id, v.created_date as createdDate, v.particulars as particulars,"
                    + " v.voucher_type as voucherType, v.voucher_number as voucherNumber, v.credit as credit,"
                    + " v.debit as debit, v.remarks as remarks ";
        }

        @Override
        public VoucherData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");

            final LocalDate createdDate = JdbcSupport.getLocalDate(rs, "createdDate");

            final String particulars = rs.getString("particulars");

            final String voucherType = rs.getString("voucherType");

            final String voucherNumber = rs.getString("voucherNumber");

            final BigDecimal credit = rs.getBigDecimal("credit");

            final BigDecimal debit = rs.getBigDecimal("debit");

            final String remarks = rs.getString("remarks");

            return VoucherData.instance(id, createdDate, particulars, voucherType, voucherNumber, credit, debit, remarks);
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

    @Override
    @Cacheable(value = "HLPaymentData", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('CD')")
    public Collection<HLPaymentData> retrieveAllHLPaymentData() {
        this.context.authenticatedUser();

        final HLPaymentAllDataMapper rm = new HLPaymentAllDataMapper();
        final String sql = "select " + rm.schema() + " from m_hl_payment hl ";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

    @Override
    @Cacheable(value = "HLPaymentData", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('CD')")
    public Collection<VoucherData> retrieveAllVoucherData() {
        this.context.authenticatedUser();

        final VoucherDataMapper rm = new VoucherDataMapper();
        final String sql = "select " + rm.schema() + " from m_voucher v ";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

}
