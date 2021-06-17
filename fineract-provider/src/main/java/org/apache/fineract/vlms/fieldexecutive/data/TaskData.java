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
package org.apache.fineract.vlms.fieldexecutive.data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Immutable data object representing a code.
 */

public class TaskData implements Serializable {

    private final Long id;

    @SuppressWarnings("unused")
    private final String taskType;

    @SuppressWarnings("unused")
    private final String customerRegNo;

    @SuppressWarnings("unused")
    private final String customerMobileNo;

    @SuppressWarnings("unused")
    private final String vehicleNumber;

    @SuppressWarnings("unused")
    private final LocalDate dueDate;

    @SuppressWarnings("unused")
    private final String assignTo;

    @SuppressWarnings("unused")
    private final String assignBy;

    @SuppressWarnings("unused")
    private final String description;

    @SuppressWarnings("unused")
    private final String status;

    @SuppressWarnings("unused")
    private final String branch;

    @SuppressWarnings("unused")
    private final LocalDate createdDate;

    public static TaskData instance(final Long id, final String taskType, final String customerRegNo, final String customerMobileNo,
            final String vehicleNumber, final LocalDate dueDate, final String assignTo, final String assignBy, final String branch,
            final String description, final String status, final LocalDate createdDate) {
        return new TaskData(id, taskType, customerRegNo, customerMobileNo, vehicleNumber, dueDate, assignTo, assignBy, branch, description,
                status, createdDate);
    }

    private TaskData(final Long id, final String taskType, final String customerRegNo, final String customerMobileNo,
            final String vehicleNumber, final LocalDate dueDate, final String assignTo, final String assignBy, final String branch,
            final String description, final String status, final LocalDate createdDate) {
        this.id = id;
        this.taskType = taskType;
        this.customerRegNo = customerRegNo;
        this.customerMobileNo = customerMobileNo;
        this.vehicleNumber = vehicleNumber;
        this.dueDate = dueDate;
        this.assignTo = assignTo;
        this.assignBy = assignBy;
        this.branch = branch;
        this.description = description;
        this.status = status;
        this.createdDate = createdDate;
    }

    /*
     * public static DocumentsData instance(final Long id, final String documentName, final boolean status) { return new
     * DocumentsData(id, documentName, status); }
     *
     * private DocumentsData(final Long id, final String documentName, final String status) { this.id = id;
     * this.documentName = documentName; this.status = status; }
     */
    /*
     * public Long getDocumentId() { return this.id; }
     */
}
