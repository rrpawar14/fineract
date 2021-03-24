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
package org.apache.fineract.portfolio.loanaccount.domain;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

@Entity
@Table(name = "m_customer_guarantor")
public class CustomerGuarantor extends AbstractPersistableCustom {

    // @Column(name = "g", nullable = false, length = 100)
    // private BigDecimal loanEligibleAmount;

    @Column(name = "mobile_number")
    private Integer mobileNumber;

    @Column(name = "gender")
    private String gender;

    @Column(name = "dob")
    private Date dob;

    @Column(name = "marital_status")
    private String maritalStatus;

    @Column(name = "spouse_name")
    private String spouseName;

    @Column(name = "profession")
    private String profession;

    public static CustomerGuarantor fromJson(final JsonCommand command) {

        final Integer mobileNumber = command.integerValueOfParameterNamed("guarantor_mobile_number");
        final String gender = command.stringValueOfParameterNamed("guarantor_gender");
        final Date dob = command.dateValueOfParameterNamed("guarantor_dob");
        final String maritalStatus = command.stringValueOfParameterNamed("guarantor_maritalStatus");
        final String spouseName = command.stringValueOfParameterNamed("guarantor_spouseName");
        final String profession = command.stringValueOfParameterNamed("guarantor_profession");

        return new CustomerGuarantor(mobileNumber, gender, dob, maritalStatus, spouseName, profession);
    }

    private CustomerGuarantor(final Integer mobileNumber, final String gender, final Date dob, final String maritalStatus,
            final String spouseName, final String profession) {
        this.mobileNumber = mobileNumber;
        this.gender = gender;
        this.dob = dob;
        this.maritalStatus = maritalStatus;
        this.spouseName = spouseName;
        this.profession = profession;
    }
}
