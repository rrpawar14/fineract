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

public class EnrollData implements Serializable {

    private final Long id;

    @SuppressWarnings("unused")
    private final String customerName;

    @SuppressWarnings("unused")
    private final String mobileNumber;

    @SuppressWarnings("unused")
    private final String alternateMobileNumber;

    @SuppressWarnings("unused")
    private final LocalDate dob;

    @SuppressWarnings("unused")
    private final String fatherName;

    @SuppressWarnings("unused")
    private final String gender;

    @SuppressWarnings("unused")
    private final String applicantType;

    @SuppressWarnings("unused")
    private final String applicantId;

    public static EnrollData instance(final Long id, final String customerName, final String mobileNumber,
            final String alternateMobileNumber, final LocalDate dob, final String fatherName, final String gender,
            final String applicantType, final String applicantId) {
        return new EnrollData(id, customerName, mobileNumber, alternateMobileNumber, dob, fatherName, gender, applicantType, applicantId);
    }

    private EnrollData(final Long id, final String customerName, final String mobileNumber, final String alternateMobileNumber,
            final LocalDate dob, final String fatherName, final String gender, final String applicantType, final String applicantId) {
        this.id = id;
        this.customerName = customerName;
        this.mobileNumber = mobileNumber;
        this.alternateMobileNumber = alternateMobileNumber;
        this.dob = dob;
        this.fatherName = fatherName;
        this.gender = gender;
        this.applicantType = applicantType;
        this.applicantId = applicantId;
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
