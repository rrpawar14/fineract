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
import org.apache.fineract.vlms.cashier.data.HLPaymentDetailsData;
import org.apache.fineract.vlms.cashier.data.VoucherData;
import org.apache.fineract.vlms.cashier.data.VoucherDetailsData;
import org.apache.fineract.vlms.fieldexecutive.data.TaskData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class CashierModuleReadPlatformServiceImpl implements CashierModuleReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    @Autowired
    public CashierModuleReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource) {
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

    private static final class HLPaymentAllDataMapper implements RowMapper<HLPaymentDetailsData> {

        public String schema() {
            return " hl.id as id, hl.post_date as postDate,  hl.agent as agent, hl.post_type as postType, "
                    + " hld.id as hldId, hld.actual_amount as actualAmount, hld.post_amount as postAmount, hld.expiry_date as expiryDate, "
                    + " hld.agtno as AGTNO, hld.customer_name as customerName,  hld.policy_no as policyNo, "
                    + " hld.insurance_company as insuranceCompany,  hld.remark as remark ";
        }

        @Override
        public HLPaymentDetailsData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");

            final String AGTNO = rs.getString("AGTNO");

            final String customerName = rs.getString("customerName");

            final BigDecimal actualAmount = rs.getBigDecimal("actualAmount");

            final BigDecimal postAmount = rs.getBigDecimal("postAmount");

            final LocalDate expiryDate = JdbcSupport.getLocalDate(rs, "expiryDate");

            final String policyNo = rs.getString("policyNo");

            final String insuranceCompany = rs.getString("insuranceCompany");

            final String remark = rs.getString("remark");

            HLPaymentData hlPaymentData = HLPaymentData.instance(id, AGTNO, customerName, actualAmount, postAmount, expiryDate, policyNo,
                    insuranceCompany, remark);

            final Long hldId = rs.getLong("hldId");

            final LocalDate postDate = JdbcSupport.getLocalDate(rs, "postDate");

            final String postType = rs.getString("postType");

            final String agent = rs.getString("agent");

            return HLPaymentDetailsData.instance(hldId, postDate, postType, agent, hlPaymentData);

        }
    }

    private static final class VoucherDataMapper implements RowMapper<VoucherDetailsData> {

        public String schema() {
            return "v.id as id, v.created_date as createdDate, v.remarks as remarks, "
                    + " v.voucher_type as voucherType, v.voucher_number as voucherNumber, vd.id as vid, vd.credit as credit, "
                    + " vd.debit as debit , vd.particulars as particulars ";
        }

        @Override
        public VoucherDetailsData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");

            final LocalDate createdDate = JdbcSupport.getLocalDate(rs, "createdDate");

            final String remarks = rs.getString("remarks");

            final String voucherType = rs.getString("voucherType");

            final String voucherNumber = rs.getString("voucherNumber");

            VoucherData voucherData = VoucherData.instance(id, createdDate, remarks, voucherType, voucherNumber);// particulars,
                                                                                                                 // credit,
                                                                                                                 // debit,
                                                                                                                 // );

            final Long vid = rs.getLong("vid");

            final BigDecimal credit = rs.getBigDecimal("credit");

            final BigDecimal debit = rs.getBigDecimal("debit");

            final String particulars = rs.getString("particulars");

            return VoucherDetailsData.instance(vid, credit, debit, particulars, voucherData);

        }
    }

    private static final class TaskDataMapper implements RowMapper<TaskData> {

        public String schema() {
            return " task.id as id, task.taskType as taskType, task.customer_mobile_no as customerMobileNo, task.customer_reg_no as customerRegNo, task.vehicle_number as vehicleNumber, "
                    + " task.due_date as dueDate, task.assign_to as assignTo, task.assign_by as assignBy, task.description as description, task.status as status, task.branch as branch, task.created_date as createdDate ";

        }

        @Override
        public TaskData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String taskType = rs.getString("taskType");
            final String customerRegNo = rs.getString("customerRegNo");
            final String customerMobileNo = rs.getString("customerMobileNo");
            final String vehicleNumber = rs.getString("vehicleNumber");
            final LocalDate dueDate = JdbcSupport.getLocalDate(rs, "dueDate");
            final String assignTo = rs.getString("assignTo");
            final String assignBy = rs.getString("assignBy");
            final String description = rs.getString("description");
            final String branch = rs.getString("branch");
            final String status = rs.getString("status");
            final LocalDate createdDate = JdbcSupport.getLocalDate(rs, "createdDate");

            return TaskData.instance(id, taskType, customerRegNo, customerMobileNo, vehicleNumber, dueDate, assignTo, assignBy, branch,
                    description, status, createdDate);
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
    @Cacheable(value = "HLPaymentDetailsData", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('CD')")
    public Collection<HLPaymentDetailsData> retrieveAllHLPaymentData() {
        this.context.authenticatedUser();

        final HLPaymentAllDataMapper rm = new HLPaymentAllDataMapper();
        final String sql = "select " + rm.schema() + " from m_hl_payment hl " + " join m_hl_payment_data hld on hl.id = hld.hl_payment_id ";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

    @Override
    @Cacheable(value = "HLPaymentData", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('CD')")
    public Collection<VoucherDetailsData> retrieveAllVoucherData() {
        this.context.authenticatedUser();

        final VoucherDataMapper rm = new VoucherDataMapper();
        final String sql = "select " + rm.schema() + " from m_voucher v " + " right join m_voucher_data vd on v.id = vd.voucher_id ";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

    @Override
    @Cacheable(value = "taskdata", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('CD')")
    public Collection<TaskData> retrieveAllCashierTaskData() {
        this.context.authenticatedUser();

        final TaskDataMapper rm = new TaskDataMapper();
        final String sql = "select " + rm.schema() + " from m_cashiermodule_task task ";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

}
