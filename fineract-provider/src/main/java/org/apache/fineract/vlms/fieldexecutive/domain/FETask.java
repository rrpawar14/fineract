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
package org.apache.fineract.vlms.fieldexecutive.domain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.infrastructure.core.service.DateUtils;

@Entity
@Table(name = "m_fe_task")
public class FETask extends AbstractPersistableCustom {

    @Column(name = "taskType", nullable = false, length = 100)
    private String taskType;

    @Column(name = "customer_reg_no", nullable = false, length = 100)
    private String customerRegNo;

    @Column(name = "customer_mobile_no", nullable = false, length = 100)
    private String customerMobileNo;

    @Column(name = "vehicle_number", nullable = false, length = 100)
    private String vehicleNumber;

    @Column(name = "due_date", nullable = false, length = 100)
    private Date dueDate;

    @Column(name = "assign_to", nullable = false, length = 100)
    private String assignTo;

    @Column(name = "description", nullable = false, length = 100)
    private String description;

    @Column(name = "status", nullable = false, length = 100)
    private String status;

    @Column(name = "created_date", nullable = false, length = 100)
    private Date createdDate;

    public static FETask fromJson(final JsonCommand command) {

        final String taskType = command.stringValueOfParameterNamed("taskType");
        final String customerRegNo = command.stringValueOfParameterNamed("customerRegNo");
        final String customerMobileNo = command.stringValueOfParameterNamed("customerMobileNo");
        final String vehicleNumber = command.stringValueOfParameterNamed("vehicleNumber");
        final Date dueDate = command.dateValueOfParameterNamed("dueDate");
        final String assignTo = command.stringValueOfParameterNamed("assignTo");
        final String description = command.stringValueOfParameterNamed("description");
        final String status = command.stringValueOfParameterNamed("status");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date createdDate = new Date();

        return new FETask(taskType, customerRegNo, customerMobileNo, vehicleNumber, dueDate, assignTo, description, status, createdDate);

    }

    public FETask() {}

    private FETask(final String taskType, final String customerRegNo, final String customerMobileNo, final String vehicleNumber,
            final Date dueDate, final String assignTo, final String description, final String status, final Date createdDate) {
        this.taskType = taskType;
        this.customerRegNo = customerRegNo;
        this.customerMobileNo = customerMobileNo;
        this.vehicleNumber = vehicleNumber;
        this.dueDate = dueDate;
        this.assignTo = assignTo;
        this.description = description;
        this.status = status;
        this.createdDate = createdDate;
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(1);

        final String taskTypeParamName = "taskType";
        if (command.isChangeInStringParameterNamed(taskTypeParamName, this.taskType)) {
            final String newValue = command.stringValueOfParameterNamed(taskTypeParamName);
            actualChanges.put(taskTypeParamName, newValue);
            this.taskType = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String customerRegNoParamName = "customerRegNo";
        if (command.isChangeInStringParameterNamed(customerRegNoParamName, this.customerRegNo)) {
            final String newValue = command.stringValueOfParameterNamed(customerRegNoParamName);
            actualChanges.put(customerRegNoParamName, newValue);
            this.customerRegNo = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String customerMobileNoParamName = "customerMobileNo";
        if (command.isChangeInStringParameterNamed(customerMobileNoParamName, this.customerMobileNo)) {
            final String newValue = command.stringValueOfParameterNamed(customerMobileNoParamName);
            actualChanges.put(customerMobileNoParamName, newValue);
            this.customerMobileNo = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String vehicleNumberParamName = "vehicleNumber";
        if (command.isChangeInStringParameterNamed(vehicleNumberParamName, this.vehicleNumber)) {
            final String newValue = command.stringValueOfParameterNamed(vehicleNumberParamName);
            actualChanges.put(vehicleNumberParamName, newValue);
            this.vehicleNumber = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String dateFormatAsInput = command.dateFormat();
        final String localeAsInput = command.locale();

        final String dueDateParamName = "dueDate";
        final String localeParamName = "locale";
        final String dateFormatParamName = "dateFormat";
        if (command.isChangeInDateParameterNamed(dueDateParamName, this.dueDate)) {
            final String valueAsInput = command.stringValueOfParameterNamed(dueDateParamName);
            actualChanges.put(dueDateParamName, valueAsInput);
            actualChanges.put(dateFormatParamName, dateFormatAsInput);
            actualChanges.put(localeParamName, localeAsInput);

            final LocalDate newValue = command.localDateValueOfParameterNamed(dueDateParamName);
            this.dueDate = Date.from(newValue.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
        }

        final String assignToParamName = "assignTo";
        if (command.isChangeInStringParameterNamed(assignToParamName, this.assignTo)) {
            final String newValue = command.stringValueOfParameterNamed(assignToParamName);
            actualChanges.put(assignToParamName, newValue);
            this.assignTo = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String descriptionParamName = "description";
        if (command.isChangeInStringParameterNamed(descriptionParamName, this.description)) {
            final String newValue = command.stringValueOfParameterNamed(descriptionParamName);
            actualChanges.put(descriptionParamName, newValue);
            this.description = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String statusParamName = "status";
        if (command.isChangeInStringParameterNamed(statusParamName, this.status)) {
            final String newValue = command.stringValueOfParameterNamed(statusParamName);
            actualChanges.put(statusParamName, newValue);
            this.status = StringUtils.defaultIfEmpty(newValue, null);
        }

        return actualChanges;
    }

}
