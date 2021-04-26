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

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

@Entity
@Table(name = "m_fe_task")
public class FETask extends AbstractPersistableCustom {

    @Column(name = "taskType", nullable = false, length = 100)
    private String taskType;

    @Column(name = "customer_reg_no", nullable = false, length = 100)
    private String customerRegNo;

    @Column(name = "vehicle_number", nullable = false, length = 100)
    private String vehicleNumber;

    @Column(name = "due_date", nullable = false, length = 100)
    private Date dueDate;

    @Column(name = "assign_to", nullable = false, length = 100)
    private String assignTo;

    @Column(name = "description", nullable = false, length = 100)
    private String description;

    public static FETask fromJson(final JsonCommand command) {

        final String taskType = command.stringValueOfParameterNamed("taskType");
        final String customerRegNo = command.stringValueOfParameterNamed("customerRegNo");
        final String vehicleNumber = command.stringValueOfParameterNamed("vehicleNumber");
        final Date dueDate = command.dateValueOfParameterNamed("dueDate");
        final String assignTo = command.stringValueOfParameterNamed("assignTo");
        final String description = command.stringValueOfParameterNamed("description");

        return new FETask(taskType, customerRegNo, vehicleNumber, dueDate, assignTo, description);

    }

    public FETask() {}

    private FETask(final String taskType, final String customerRegNo, final String vehicleNumber, final Date dueDate, final String assignTo,
            final String description) {
        this.taskType = taskType;
        this.customerRegNo = customerRegNo;
        this.vehicleNumber = vehicleNumber;
        this.dueDate = dueDate;
        this.assignTo = assignTo;
        this.description = description;
    }

}
