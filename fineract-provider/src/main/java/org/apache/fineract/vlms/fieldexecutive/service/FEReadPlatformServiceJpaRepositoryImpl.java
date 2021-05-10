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
package org.apache.fineract.vlms.fieldexecutive.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.vlms.customer.data.CustomerDocumentsData;
import org.apache.fineract.vlms.fieldexecutive.data.EnquiryData;
import org.apache.fineract.vlms.fieldexecutive.data.EnrollData;
import org.apache.fineract.vlms.fieldexecutive.data.FeCashInHandLimit;
import org.apache.fineract.vlms.fieldexecutive.data.TaskData;
import org.apache.fineract.vlms.fieldexecutive.domain.DocumentsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class FEReadPlatformServiceJpaRepositoryImpl implements FEReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    @Autowired
    public FEReadPlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class DocumentMapper implements RowMapper<DocumentsData> {

        public String schema() {
            return " d.documents_name as documents_name from m_documents_type d ";
        }

        @Override
        public DocumentsData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            // final Long id = rs.getLong("id");
            final String documents_name = rs.getString("documents_name");
            // final boolean status = rs.getBoolean("status");

            return DocumentsData.instance(documents_name);
        }
    }

    private static final class TaskDataMapper implements RowMapper<TaskData> {

        public String schema() {
            return " task.id as id, task.taskType as taskType, task.customer_mobile_no as customerMobileNo, task.customer_reg_no as customerRegNo, task.vehicle_number as vehicleNumber, "
                    + " task.due_date as dueDate, task.assign_to as assignTo, task.description as description  ";

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
            final String description = rs.getString("description");
            // final boolean status = rs.getBoolean("status");

            return TaskData.instance(id, taskType, customerRegNo, customerMobileNo, vehicleNumber, dueDate, assignTo, description);
        }
    }

    @Override
    @Cacheable(value = "documents", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('CD')")
    public Collection<DocumentsData> retrieveAllDocumentsType() {
        this.context.authenticatedUser();

        final DocumentMapper rm = new DocumentMapper();
        final String sql = "select " + rm.schema() + " where d.status=1 order by d.documents_name";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

    @Override
    @Cacheable(value = "taskdata", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('CD')")
    public Collection<TaskData> retrieveAllTask() {
        this.context.authenticatedUser();

        final TaskDataMapper rm = new TaskDataMapper();
        final String sql = "select " + rm.schema() + " from m_fe_task task ";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

    /*
     * @Override public DocumentsData retrieveCode(final Long codeId) { try { this.context.authenticatedUser();
     *
     * final DocumentMapper rm = new DocumentMapper(); final String sql = "select " + rm.schema() + " where c.id = ?";
     *
     * return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { codeId }); } catch (final
     * EmptyResultDataAccessException e) { throw new CodeNotFoundException(codeId, e); } }
     *
     * @Override public DocumentsData retriveCode(final String codeName) { try { this.context.authenticatedUser();
     *
     * final DocumentMapper rm = new DocumentMapper(); final String sql = "select " + rm.schema() +
     * " where c.code_name = ?";
     *
     * return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { codeName }); } catch (final
     * EmptyResultDataAccessException e) { throw new CodeNotFoundException(codeName, e); } }
     */

    private static final class EnquiryDataMapper implements RowMapper<EnquiryData> {

        public String schema() {
            return " cln.id as id, cln.customer_name as customerName, cln.mobile_number as mobileNumber,	cln.vehicle_number as vehicleNumber, "
                    + "	cln.email as email, " + "	cln.pincode as pincode, " + "	cln.enquiry_id as enquiryId, " + "	cln.notes as notes";
        }

        @Override
        public EnquiryData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String mobileNumber = rs.getString("mobileNumber");
            final String customerName = rs.getString("customerName");
            final String vehicleNumber = rs.getString("vehicleNumber");
            final String pincode = rs.getString("pincode");
            final String email = rs.getString("email");
            final String enquiryId = rs.getString("enquiryId");
            final String notes = rs.getString("notes");
            // final boolean status = rs.getBoolean("status");

            return EnquiryData.instance(id, mobileNumber, customerName, vehicleNumber, pincode, email, enquiryId, notes);
        }
    }

    @Override
    @Cacheable(value = "taskdata", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('CD')")
    public Collection<EnquiryData> retrieveAllEnquires() {
        this.context.authenticatedUser();

        final EnquiryDataMapper rm = new EnquiryDataMapper();
        final String sql = "select " + rm.schema() + " from m_customerloanenquiry cln ";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

    private static final class EnrollDataMapper implements RowMapper<EnrollData> {

        public String schema() {
            return " erl.id as id, erl.customer_name as customerName, erl.mobile_number as mobileNumber, "
                    + "	erl.alternate_mobile_number as alternateMobileNumber , erl.dob as dob, erl.father_name as fatherName, "
                    + "	erl.gender as gender, erl.applicant_type as applicantType, erl.applicant_id as applicantId ";

        }

        @Override
        public EnrollData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String customerName = rs.getString("customerName");
            final String mobileNumber = rs.getString("mobileNumber");
            final String alternateMobileNumber = rs.getString("alternateMobileNumber");
            final LocalDate dob = JdbcSupport.getLocalDate(rs, "dob");
            final String fatherName = rs.getString("fatherName");
            final String gender = rs.getString("gender");
            final String applicantType = rs.getString("applicantType");
            final String applicantId = rs.getString("applicantId");

            return EnrollData.instance(id, customerName, mobileNumber, alternateMobileNumber, dob, fatherName, gender, applicantType,
                    applicantId);
        }
    }

    @Override
    @Cacheable(value = "taskdata", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('CD')")
    public Collection<EnrollData> retrieveAllEnroll() {
        this.context.authenticatedUser();

        final EnrollDataMapper rm = new EnrollDataMapper();
        final String sql = "select " + rm.schema() + " from m_feenroll erl ";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

    private static final class DocumentsDetailsDataMapper implements RowMapper<CustomerDocumentsData> {

        public String schema() {
            return " doc.id as id, doc.entity_name as entityName, doc.document_number as documentNumber  ";

        }

        @Override
        public CustomerDocumentsData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String entityName = rs.getString("entityName");
            final String documentNumber = rs.getString("documentNumber");

            return CustomerDocumentsData.instance(id, entityName, documentNumber);
        }
    }

    @Override
    @Cacheable(value = "CustomerDetailsData", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('CD')")
    public Collection<CustomerDocumentsData> retrieveDocumentData(String commandParam, Long clientId) {
        this.context.authenticatedUser();
        String sql = null;
        final DocumentsDetailsDataMapper rm = new DocumentsDetailsDataMapper();
        if (commandParam.equals("customerData")) {
            sql = "select " + rm.schema() + " from m_documents_images doc where customerdetails_id= ? ";
        } else if (commandParam.equals("guarantorData")) {
            sql = "select " + rm.schema() + " from m_documents_images doc  where guarantor_id= ? ";
        }

        return this.jdbcTemplate.query(sql, rm, new Object[] { clientId });
    }

    private static final class CashinHandDataMapper implements RowMapper<FeCashInHandLimit> {

        public String schema() {
            return " fecash.id as id, fecash.name as feName, fecash.cash_in_hand as cashInHand, fecash.required_on as requiredDate, "
                    + " fecash.required_amount as requiredAmount, fecash.status as status";
        }

        @Override
        public FeCashInHandLimit mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String name = rs.getString("feName");

            final Long cashInHand = rs.getLong("cashInHand");

            final LocalDate requiredDate = JdbcSupport.getLocalDate(rs, "requiredDate");

            final Long requiredAmount = rs.getLong("requiredAmount");
            final String status = rs.getString("status");

            return FeCashInHandLimit.instance(id, name, cashInHand, requiredDate, requiredAmount, status);
        }
    }

    @Override
    @Cacheable(value = "FeCashInHandLimitData", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('CD')")
    public Collection<FeCashInHandLimit> retrieveAllfeCashLimitData() {
        this.context.authenticatedUser();

        final CashinHandDataMapper rm = new CashinHandDataMapper();
        final String sql = "select " + rm.schema() + " from m_fe_cashinhand fecash ";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

    /*
     * @Override
     *
     * @Cacheable(value = "CustomerDetailsData", key =
     * "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('CD')")
     * public Collection<CustomerDocumentsData> retrieveDataforAnalytics(String commandParam, Long clientId) {
     * this.context.authenticatedUser(); String sql = null; final DocumentsDetailsDataMapper rm = new
     * DocumentsDetailsDataMapper(); if (commandParam.equals("customerData")) { sql = "select " + rm.schema() +
     * " from m_documents_images doc where customerdetails_id= ? "; } else if (commandParam.equals("guarantorData")) {
     * sql = "select " + rm.schema() + " from m_documents_images doc  where guarantor_id= ? "; }
     *
     * return this.jdbcTemplate.query(sql, rm, new Object[] { clientId }); }
     */
}
