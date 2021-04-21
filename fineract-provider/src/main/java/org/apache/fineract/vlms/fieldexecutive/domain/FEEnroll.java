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
import javax.persistence.UniqueConstraint;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

@Entity
@Table(name = "m_feEnroll", uniqueConstraints = { @UniqueConstraint(columnNames = { "mobile_number" }, name = "mobile_number") })
public class FEEnroll extends AbstractPersistableCustom {

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Column(name = "mobile_number", nullable = false, length = 100)
    private String mobileNumber; // MobileNo is stored in username column for authentication

    @Column(name = "alternate_mobile_number", nullable = false, length = 100)
    private String alternateMobileNumber;

    @Column(name = "dob", nullable = false, length = 100)
    private Date dob;

    @Column(name = "father_name", nullable = false, length = 100)
    private String fatherName;

    @Column(name = "gender", nullable = false, length = 100)
    private String gender;

    @Column(name = "applicant_type", nullable = false, length = 100)
    private String applicantType;

    @Column(name = "applicant_id", nullable = false, length = 100)
    private String applicantId;

    public FEEnroll() {}

    public static FEEnroll fromJson(final JsonCommand command) {

        final String applicantId = command.stringValueOfParameterNamed("applicantId");
        final String customerName = command.stringValueOfParameterNamed("customerName");
        final String mobileNumber = command.stringValueOfParameterNamed("mobileNumber");
        final String alternateMobileNumber = command.stringValueOfParameterNamed("alternateMobileNumber");
        final Date dob = command.dateValueOfParameterNamed("dob");
        final String fatherName = command.stringValueOfParameterNamed("fatherName");
        final String gender = command.stringValueOfParameterNamed("gender");
        final String applicantType = command.stringValueOfParameterNamed("applicantType");

        return new FEEnroll(applicantId, customerName, mobileNumber, alternateMobileNumber, dob, fatherName, gender, applicantType);

    }

    private FEEnroll(final String applicantId, final String customerName, final String mobileNumber, final String alternateMobileNumber,
            final Date dob, final String fatherName, final String gender, final String applicantType) {
        this.applicantId = applicantId; // MobileNo is stored in username column for authentication
        this.customerName = customerName;
        this.mobileNumber = mobileNumber;
        this.alternateMobileNumber = alternateMobileNumber;
        this.dob = dob;
        this.fatherName = fatherName;
        this.gender = gender;
        this.applicantType = applicantType;
    }

}
