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

package org.apache.fineract.useradministration.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

@Entity
@Table(name = "m_customerloanenquiry")
public class CustomerLoanEnquiry extends AbstractPersistableCustom {

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customername; // MobileNo is stored in username column for authentication

    @Column(name = "vehicle_number", nullable = false, length = 100)
    private String vehiclenumber;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "pincode", nullable = false, length = 100)
    private String pincode;

    @Column(name = "enquiry_id", nullable = false, length = 100)
    private String enquiryId;

    @Column(name = "notes", nullable = false, length = 100)
    private String notes;

    public static CustomerLoanEnquiry fromJson(final JsonCommand command) {

        final String customername = command.stringValueOfParameterNamed("customername");
        final String vehiclenumber = command.stringValueOfParameterNamed("vehiclenumber");
        final String email = command.stringValueOfParameterNamed("email");
        final String pincode = command.stringValueOfParameterNamed("pincode");
        final String enquiryId = command.stringValueOfParameterNamed("enquiryId");
        final String notes = command.stringValueOfParameterNamed("notes");
        System.out.println("loanenquirydetails: " + customername + vehiclenumber + email + pincode + notes);

        return new CustomerLoanEnquiry(customername, vehiclenumber, email, pincode, enquiryId, notes);

    }

    public CustomerLoanEnquiry() {}

    private CustomerLoanEnquiry(final String customername, final String vehiclenumber, final String email, final String pincode,
            final String enquiryId, final String notes) {
        this.customername = customername; // MobileNo is stored in username column for authentication
        this.vehiclenumber = vehiclenumber;
        this.email = email;
        this.pincode = pincode;
        this.enquiryId = enquiryId;
        this.notes = notes;
    }

}
