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
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.address.domain.Address;

@Entity
@Table(name = "m_fe_co_applicant_details")
public class FECoApplicantDetails extends AbstractPersistableCustom {

    @Column(name = "customerId", nullable = true, length = 100)
    private String customerId;

    @Column(name = "mobile_number", nullable = true, length = 100)
    private String mobileNumber;

    @Column(name = "customer_name", nullable = true, length = 100)
    private String customerName;

    @Column(name = "applicant_type", nullable = true, length = 100)
    private String applicantType;

    @Column(name = "company_name", nullable = true, length = 100)
    private String companyName;

    @Column(name = "net_income", nullable = true, length = 100)
    private Integer netIncome;

    @Column(name = "income_frequency", nullable = true, length = 100)
    private String incomeFrequency;

    @Column(name = "salary_date", nullable = true, length = 100)
    private Date salaryDate;

    @Column(name = "dob", nullable = true, length = 100)
    private Date dob;

    @Column(name = "age", nullable = true, length = 100)
    private Integer age;

    @Column(name = "marital_status", nullable = true, length = 100)
    private String maritalStatus;

    @Column(name = "spouse_name", nullable = true, length = 100)
    private String spouseName;

    @OneToOne(optional = true)
    @JoinColumn(name = "communicationadd_id", nullable = true)
    private Address communicationAdd;

    @OneToOne(optional = true)
    @JoinColumn(name = "permanentadd_id", nullable = true)
    private Address permanentAdd;

    @OneToOne(optional = true)
    @JoinColumn(name = "officeadd_id", nullable = true)
    private Address officeAdd;

    public static FECoApplicantDetails fromJson(final JsonCommand command, final Address customerCommunicationAdd,
            final Address customerPermanentAdd, final Address customerOfficeAdd) {

        final String customerId = command.stringValueOfParameterNamed("customerId");
        final String mobileNumber = command.stringValueOfParameterNamed("coappmobileNumber");
        final String customerName = command.stringValueOfParameterNamed("coappName");
        final String applicantType = command.stringValueOfParameterNamed("coappapplicantType");
        final String companyName = command.stringValueOfParameterNamed("coappcompanyName");
        final Integer netIncome = command.integerValueOfParameterNamed("coappnetIncome");
        final String incomeFrequency = command.stringValueOfParameterNamed("coappincomeFrequency");
        final Date salaryDate = command.dateValueOfParameterNamed("coappsalaryDate");
        final Date dob = command.dateValueOfParameterNamed("coappdob");
        final Integer age = command.integerValueOfParameterNamed("coappage");
        final String maritalStatus = command.stringValueOfParameterNamed("coappmaritalStatus");
        final String spouseName = command.stringValueOfParameterNamed("coappspouseName");

        return new FECoApplicantDetails(customerId, mobileNumber, customerName, applicantType, companyName, netIncome, incomeFrequency,
                salaryDate, dob, age, maritalStatus, spouseName, customerCommunicationAdd, customerPermanentAdd, customerOfficeAdd);

    }

    private FECoApplicantDetails(final String customerId, final String mobileNumber, final String customerName, final String applicantType,
            final String companyName, final Integer netIncome, final String incomeFrequency, final Date salaryDate, final Date dob,
            final Integer age, final String maritalStatus, final String spouseName, final Address customerCommunicationAdd,
            final Address customerPermanentAdd, final Address customerOfficeAdd) {
        this.customerId = customerId;
        this.mobileNumber = mobileNumber;
        this.customerName = customerName;
        this.applicantType = applicantType;
        this.companyName = companyName;
        this.netIncome = netIncome;
        this.incomeFrequency = incomeFrequency;
        this.salaryDate = salaryDate;
        this.dob = dob;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.spouseName = spouseName;
        this.communicationAdd = customerCommunicationAdd;
        this.permanentAdd = customerPermanentAdd;
        this.officeAdd = customerOfficeAdd;
    }

}
