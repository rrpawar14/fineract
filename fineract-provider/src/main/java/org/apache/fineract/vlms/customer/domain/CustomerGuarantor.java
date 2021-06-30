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
package org.apache.fineract.vlms.customer.domain;

import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.portfolio.address.domain.Address;

@Entity
@Table(name = "m_customer_guarantor")
public class CustomerGuarantor extends AbstractPersistableCustom {

    // @Column(name = "g", nullable = false, length = 100)
    // private BigDecimal loanEligibleAmount;

    @Column(name = "guarantor_name")
    private String guarantorName;

    @Column(name = "age")
    private Integer age;

    @Column(name = "guarantor_addressType")
    private String addressType;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "gender")
    private String gender;

    @Column(name = "dob")
    private Date dob;

    @Column(name = "marital_status")
    private String maritalStatus;

    @Column(name = "physical_challenge")
    private String physicalChallenge;

    @Column(name = "spouse_name")
    private String spouseName;

    @Column(name = "father_name")
    private String fatherName;

    @Column(name = "profession")
    private String profession;

    @Column(name = "salary_type")
    private String salaryType;

    @Column(name = "applicant_type")
    private String applicantType;

    @Column(name = "employeeType") //
    private String employeeType;

    @Column(name = "individualType") //
    private String individualType;

    @Column(name = "relation_with_customer")
    private String relationshipWithCustomer;

    @Column(name = "net_income")
    private Integer netIncome;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "salary_date")
    private Integer salaryDate;

    @OneToOne(optional = true)
    @JoinColumn(name = "communicationadd_id", nullable = true)
    private Address communicationAdd;

    @OneToOne(optional = true)
    @JoinColumn(name = "permanentadd_id", nullable = true)
    private Address permanentAdd;

    @OneToOne(optional = true)
    @JoinColumn(name = "officeadd_id", nullable = true)
    private Address officeAdd;

    /*
     * @OneToOne(optional = true)
     *
     * @JoinColumn(name = "guarantor_image_id", nullable = true) private Image guarantorImage;
     *
     * @OneToOne(optional = true)
     *
     * @JoinColumn(name = "document_image_id", nullable = true) private DocumentImages documentImage;
     */
    public static CustomerGuarantor fromJson(final JsonCommand command, final Address customerCommunicationAdd,
            final Address customerPermanentAdd, final Address customerOfficeAdd) {

        final String guarantorName = command.stringValueOfParameterNamed("guarantor_name");
        final Integer age = command.integerValueOfParameterNamed("guarantor_age"); //
        final String addressType = command.stringValueOfParameterNamed("guarantor_addressType"); //
        final String mobileNumber = command.stringValueOfParameterNamed("guarantor_mobile_number");
        final String gender = command.stringValueOfParameterNamed("guarantor_gender");
        final Date dob = command.dateValueOfParameterNamed("guarantor_dob");
        final String maritalStatus = command.stringValueOfParameterNamed("guarantor_maritalStatus");
        final String physicalChallenge = command.stringValueOfParameterNamed("guarantor_physicalChallenge");
        final String spouseName = command.stringValueOfParameterNamed("guarantor_spouseName");
        final String fatherName = command.stringValueOfParameterNamed("guarantor_fatherName");
        final String profession = command.stringValueOfParameterNamed("guarantor_profession");
        final String employeeType = command.stringValueOfParameterNamed("guarantor_employeeType");
        final String individualType = command.stringValueOfParameterNamed("guarantor_individualType");
        final String salaryType = command.stringValueOfParameterNamed("guarantor_salaryType");
        final String applicantType = command.stringValueOfParameterNamed("guarantor_applicantType");
        final Integer netIncome = command.integerValueOfParameterNamed("guarantor_netIncome");
        final String companyName = command.stringValueOfParameterNamed("guarantor_companyName");
        final String relationshipWithCustomer = command.stringValueOfParameterNamed("relation_with_customer");
        final Integer salaryDate = command.integerValueOfParameterNamed("guarantor_salaryDate"); //

        return new CustomerGuarantor(guarantorName, age, mobileNumber, gender, dob, maritalStatus, physicalChallenge, spouseName,
                fatherName, profession, employeeType, individualType, salaryType, applicantType, netIncome, companyName,
                relationshipWithCustomer, salaryDate, customerCommunicationAdd, customerPermanentAdd, customerOfficeAdd);
    }

    public CustomerGuarantor() {}

    private CustomerGuarantor(final String guarantorName, final Integer age, final String mobileNumber, final String gender, final Date dob,
            final String maritalStatus, final String physicalChallenge, final String spouseName, final String fatherName,
            final String profession, final String employeeType, final String individualType, final String salaryType,
            final String applicantType, final Integer netIncome, final String companyName, final String relationshipWithCustomer,
            final Integer salaryDate, final Address customerCommunicationAdd, final Address customerPermanentAdd,
            final Address customerOfficeAdd) {
        this.guarantorName = guarantorName;
        this.age = age;
        this.mobileNumber = mobileNumber;
        this.gender = gender;
        this.dob = dob;
        this.maritalStatus = maritalStatus;
        this.physicalChallenge = physicalChallenge;
        this.spouseName = spouseName;
        this.fatherName = fatherName;
        this.profession = profession;
        this.employeeType = employeeType;
        this.individualType = individualType;
        this.salaryType = salaryType;
        this.applicantType = applicantType;
        this.netIncome = netIncome;
        this.companyName = companyName;
        this.relationshipWithCustomer = relationshipWithCustomer;
        this.salaryDate = salaryDate;
        this.communicationAdd = customerCommunicationAdd;
        this.permanentAdd = customerPermanentAdd;
        this.officeAdd = customerOfficeAdd;
    }

    public String getGuarantorName() {
        return this.guarantorName;
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(1);

        final String guarantorNameParamName = "guarantor_name";
        if (command.isChangeInStringParameterNamed(guarantorNameParamName, this.guarantorName)) {
            final String newValue = command.stringValueOfParameterNamed(guarantorNameParamName);
            actualChanges.put(guarantorNameParamName, newValue);
            this.guarantorName = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String guarantorAgeParamName = "guarantor_age";
        if (command.isChangeInIntegerParameterNamed(guarantorAgeParamName, this.age)) {
            final Integer newValue = command.integerValueOfParameterNamed(guarantorAgeParamName);
            actualChanges.put(guarantorAgeParamName, newValue);
            this.age = newValue;
        }

        final String genderParamName = "guarantor_gender";
        if (command.isChangeInStringParameterNamed(genderParamName, this.gender)) {
            final String newValue = command.stringValueOfParameterNamed(genderParamName);
            actualChanges.put(genderParamName, newValue);
            this.gender = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String mobileNoParamName = "guarantor_mobile_number";
        if (command.isChangeInStringParameterNamed(mobileNoParamName, this.mobileNumber)) {
            final String newValue = command.stringValueOfParameterNamed(mobileNoParamName);
            actualChanges.put(mobileNoParamName, newValue);
            this.mobileNumber = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String dateFormatAsInput = command.dateFormat();
        final String localeAsInput = command.locale();

        /*
         * final String salaryDateParamName = "guarantor_salaryDate"; final String localeParamName = "locale"; final
         * String dateFormatParamName = "dateFormat"; if (command.isChangeInIntegerParameterNamed(salaryDateParamName,
         * this.salaryDate)) { final String valueAsInput = command.stringValueOfParameterNamed(salaryDateParamName);
         * actualChanges.put(salaryDateParamName, valueAsInput); actualChanges.put(dateFormatParamName,
         * dateFormatAsInput); actualChanges.put(localeParamName, localeAsInput);
         *
         * final LocalDate newValue = command.localDateValueOfParameterNamed(salaryDateParamName); this.salaryDate =
         * Date.from(newValue.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant()); }
         */

        final String relationParamName = "relation_with_customer";
        if (command.isChangeInStringParameterNamed(relationParamName, this.relationshipWithCustomer)) {
            final String newValue = command.stringValueOfParameterNamed(relationParamName);
            actualChanges.put(relationParamName, newValue);
            this.relationshipWithCustomer = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String salaryDateParamName = "guarantor_salaryDate";
        if (command.isChangeInIntegerParameterNamed(salaryDateParamName, this.salaryDate)) {
            final Integer newValue = command.integerValueOfParameterNamed(salaryDateParamName);
            actualChanges.put(salaryDateParamName, newValue);
            this.salaryDate = newValue;
        }

        final String dobParamName = "guarantor_dob";
        if (command.isChangeInDateParameterNamed(dobParamName, this.dob)) {
            final String valueAsInput = command.stringValueOfParameterNamed(dobParamName);
            actualChanges.put(dobParamName, valueAsInput);

            final LocalDate newValue = command.localDateValueOfParameterNamed(dobParamName);
            this.dob = Date.from(newValue.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
        }

        final String maritalStatusParamName = "guarantor_maritalStatus";
        if (command.isChangeInStringParameterNamed(maritalStatusParamName, this.maritalStatus)) {
            final String newValue = command.stringValueOfParameterNamed(maritalStatusParamName);
            actualChanges.put(maritalStatusParamName, newValue);
            this.maritalStatus = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String physicalChallengeParamName = "guarantor_physicalChallenge";
        if (command.isChangeInStringParameterNamed(physicalChallengeParamName, this.physicalChallenge)) {
            final String newValue = command.stringValueOfParameterNamed(physicalChallengeParamName);
            actualChanges.put(physicalChallengeParamName, newValue);
            this.physicalChallenge = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String spousenameParamName = "guarantor_spouseName";
        if (command.isChangeInStringParameterNamed(spousenameParamName, this.spouseName)) {
            final String newValue = command.stringValueOfParameterNamed(spousenameParamName);
            actualChanges.put(spousenameParamName, newValue);
            this.spouseName = StringUtils.defaultIfEmpty(newValue, null);
        }

        /*
         * final String mobileNoParamName = "mobileNo"; if (command.isChangeInStringParameterNamed(mobileNoParamName,
         * this.mobileNo)) { final String newValue = command.stringValueOfParameterNamed(mobileNoParamName);
         * actualChanges.put(mobileNoParamName, newValue); this.mobileNo = StringUtils.defaultIfEmpty(newValue, null); }
         */

        final String fatherNameParamName = "guarantor_fatherName";
        if (command.isChangeInStringParameterNamed(fatherNameParamName, this.fatherName)) {
            final String newValue = command.stringValueOfParameterNamed(fatherNameParamName);
            actualChanges.put(fatherNameParamName, newValue);
            this.fatherName = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String professionParamName = "guarantor_profession";
        if (command.isChangeInStringParameterNamed(professionParamName, this.profession)) {
            final String newValue = command.stringValueOfParameterNamed(professionParamName);
            actualChanges.put(professionParamName, newValue);
            this.profession = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String applicantTypeParamName = "guarantor_applicantType";
        if (command.isChangeInStringParameterNamed(applicantTypeParamName, this.applicantType)) {
            final String newValue = command.stringValueOfParameterNamed(applicantTypeParamName);
            actualChanges.put(applicantTypeParamName, newValue);
            this.applicantType = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String employeeTypeParamName = "guarantor_employeeType";
        if (command.isChangeInStringParameterNamed(employeeTypeParamName, this.employeeType)) {
            final String newValue = command.stringValueOfParameterNamed(employeeTypeParamName);
            actualChanges.put(employeeTypeParamName, newValue);
            this.employeeType = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String individualTypeParamName = "guarantor_individualType";
        if (command.isChangeInStringParameterNamed(individualTypeParamName, this.individualType)) {
            final String newValue = command.stringValueOfParameterNamed(individualTypeParamName);
            actualChanges.put(individualTypeParamName, newValue);
            this.individualType = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String salaryTypeParamName = "guarantor_salaryType";
        if (command.isChangeInStringParameterNamed(salaryTypeParamName, this.salaryType)) {
            final String newValue = command.stringValueOfParameterNamed(salaryTypeParamName);
            actualChanges.put(salaryTypeParamName, newValue);
            this.salaryType = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String companyNameParamName = "guarantor_companyName";
        if (command.isChangeInStringParameterNamed(companyNameParamName, this.companyName)) {
            final String newValue = command.stringValueOfParameterNamed(companyNameParamName);
            actualChanges.put(companyNameParamName, newValue);
            this.companyName = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String netIncomeParamName = "guarantor_netIncome";
        if (command.isChangeInIntegerParameterNamed(netIncomeParamName, this.netIncome)) {
            final Integer newValue = command.integerValueOfParameterNamed(netIncomeParamName);
            actualChanges.put(netIncomeParamName, newValue);
            this.netIncome = newValue;
        }

        return actualChanges;
    }

    /*
     * public void setDocumentImage(final DocumentImages documentImages) { this.documentImage = documentImages; }
     *
     * public void setGuarantorImage(final Image image) { this.guarantorImage = image; }
     *
     * public DocumentImages getDocumentImage() { return this.documentImage; }
     *
     * public Image getGuarantorImage() { return this.guarantorImage; }
     */
}
