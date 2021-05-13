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
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

@Entity
@Table(name = "m_customerloanenquiry")
public class FEEnquiry extends AbstractPersistableCustom {

    @Column(name = "mobile_number", nullable = false, length = 100)
    private String mobileNumber; // MobileNo is stored in username column for authentication

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Column(name = "vehicle_number", nullable = false, length = 100)
    private String vehicleNumber;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "enquiry_id", nullable = false, length = 100)
    private String enquiryId;

    @Column(name = "created_date", nullable = false, length = 100)
    private Date createdDate;

    public static FEEnquiry fromJson(final JsonCommand command) {

        final String mobileNumber = command.stringValueOfParameterNamed("mobileNumber");
        final String customerName = command.stringValueOfParameterNamed("customerName");
        final String vehicleNumber = command.stringValueOfParameterNamed("vehicleNumber");
        final String email = command.stringValueOfParameterNamed("email");
        final String enquiryId = command.stringValueOfParameterNamed("enquiryId");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date dateobj = new Date();

        return new FEEnquiry(mobileNumber, customerName, vehicleNumber, email, enquiryId, dateobj);

    }

    public FEEnquiry() {}

    private FEEnquiry(final String mobileNumber, final String customerName, final String vehicleNumber, final String email,
            final String enquiryId, final Date date) {
        this.mobileNumber = mobileNumber; // MobileNo is stored in username column for authentication
        this.customerName = customerName;
        this.vehicleNumber = vehicleNumber;
        this.email = email;
        this.enquiryId = enquiryId;
        this.createdDate = date;
    }

}
