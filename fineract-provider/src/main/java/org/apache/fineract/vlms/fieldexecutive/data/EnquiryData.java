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

/**
 * Immutable data object representing a code.
 */

public class EnquiryData implements Serializable {

    private final Long id;

    @SuppressWarnings("unused")
    private final String mobileNumber;

    @SuppressWarnings("unused")
    private final String customerName;

    @SuppressWarnings("unused")
    private final String vehicleNumber;

    @SuppressWarnings("unused")
    private final String email;

    @SuppressWarnings("unused")
    private final String pincode;

    @SuppressWarnings("unused")
    private final String enquiryId;

    @SuppressWarnings("unused")
    private final String notes;

    public static EnquiryData instance(final Long id, final String mobileNumber, final String customerName, final String vehicleNumber,
            final String pincode, final String email, final String enquiryId, final String notes) {
        return new EnquiryData(id, mobileNumber, customerName, vehicleNumber, pincode, email, enquiryId, notes);
    }

    private EnquiryData(final Long id, final String mobileNumber, final String customerName, final String vehicleNumber,
            final String pincode, final String email, final String enquiryId, final String notes) {
        this.id = id;
        this.mobileNumber = mobileNumber;
        this.customerName = customerName;
        this.vehicleNumber = vehicleNumber;
        this.pincode = pincode;
        this.email = email;
        this.enquiryId = enquiryId;
        this.notes = notes;
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
