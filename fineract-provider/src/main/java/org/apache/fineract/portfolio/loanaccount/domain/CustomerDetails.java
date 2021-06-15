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
@Table(name = "m_customer_details")
public class CustomerDetails extends AbstractPersistableCustom {

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private Integer age;

    @Column(name = "gender")
    private String gender;

    @Column(name = "mobile_number")
    private String mobileNo;

    @Column(name = "alternate_number")
    private String altNumber;

    @Column(name = "father_name")
    private String fatherName;

    @Column(name = "applicant_type")
    private String applicantType;

    @Column(name = "refer_by")
    private String referBy;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "monthly_income")
    private String monthlyIncome;

    @Column(name = "salary_date")
    private Date salaryDate;

    @Column(name = "salary_period")
    private String salaryPeriod;

    @Column(name = "dob")
    private Date dob;

    @Column(name = "marital_status")
    private String maritalStatus;

    @Column(name = "spousename")
    private String spousename;

    @Column(name = "profession")
    private String profession;

    @OneToOne(optional = true)
    @JoinColumn(name = "communicationadd_id", nullable = true)
    private Address communicationAdd;

    @OneToOne(optional = true)
    @JoinColumn(name = "permanentadd_id", nullable = true)
    private Address permanentAdd;

    @OneToOne(optional = true)
    @JoinColumn(name = "officeadd_id", nullable = true)
    private Address officeAdd;

    public static CustomerDetails fromJson(final JsonCommand command, final Address customerCommunicationAdd,
            final Address customerPermanentAdd, final Address customerOfficeAdd) {

        final String name = command.stringValueOfParameterNamed("customerName");
        final Integer age = command.integerValueOfParameterNamed("customerAge");
        final String gender = command.stringValueOfParameterNamed("gender");
        final String mobileNo = command.stringValueOfParameterNamed("mobileNo");//
        final String fatherName = command.stringValueOfParameterNamed("fatherName");//
        final String altNumber = command.stringValueOfParameterNamed("altNumber");//
        final String applicantType = command.stringValueOfParameterNamed("applicantType");//
        final String refBy = command.stringValueOfParameterNamed("refBy");//
        final String companyName = command.stringValueOfParameterNamed("companyName");//
        final String monthlyIncome = command.stringValueOfParameterNamed("monthlyIncome");//
        final Date salaryDate = command.dateValueOfParameterNamed("salaryDate");//
        final String salaryPeriod = command.stringValueOfParameterNamed("salaryPeriod");//
        final Date dob = command.dateValueOfParameterNamed("dob");
        final String maritalStatus = command.stringValueOfParameterNamed("maritalStatus");
        final String spousename = command.stringValueOfParameterNamed("spouseName");
        final String profession = command.stringValueOfParameterNamed("profession");

        return new CustomerDetails(name, age, gender, mobileNo, fatherName, altNumber, applicantType, refBy, companyName, monthlyIncome,
                salaryDate, salaryPeriod, dob, maritalStatus, spousename, profession, customerCommunicationAdd, customerPermanentAdd,
                customerOfficeAdd);

    }

    public CustomerDetails() {}

    private CustomerDetails(final String name, final Integer age, final String gender, final String mobileNo, final String fatherName,
            final String altNumber, final String applicantType, final String refBy, final String companyName, final String monthlyIncome,
            final Date salaryDate, final String salaryPeriod, final Date dob, final String maritalStatus, final String spousename,
            final String profession, final Address customerCommunicationAdd, final Address customerPermanentAdd,
            final Address customerOfficeAdd) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.mobileNo = mobileNo;
        this.fatherName = fatherName;
        this.altNumber = altNumber;
        this.applicantType = applicantType;
        this.referBy = refBy;
        this.companyName = companyName;
        this.monthlyIncome = monthlyIncome;
        this.salaryDate = salaryDate;
        this.salaryPeriod = salaryPeriod;
        this.dob = dob;
        this.maritalStatus = maritalStatus;
        this.spousename = spousename;
        this.profession = profession;
        this.communicationAdd = customerCommunicationAdd;
        this.permanentAdd = customerPermanentAdd;
        this.officeAdd = customerOfficeAdd;

    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(1);

        final String customerNameParamName = "customerName";
        if (command.isChangeInStringParameterNamed(customerNameParamName, this.name)) {
            final String newValue = command.stringValueOfParameterNamed(customerNameParamName);
            actualChanges.put(customerNameParamName, newValue);
            this.name = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String customerAgeParamName = "customerAge";
        if (command.isChangeInIntegerParameterNamed(customerAgeParamName, this.age)) {
            final Integer newValue = command.integerValueOfParameterNamed(customerAgeParamName);
            actualChanges.put(customerAgeParamName, newValue);
            this.age = newValue;
        }

        final String genderParamName = "gender";
        if (command.isChangeInStringParameterNamed(genderParamName, this.gender)) {
            final String newValue = command.stringValueOfParameterNamed(genderParamName);
            actualChanges.put(genderParamName, newValue);
            this.gender = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String mobileNoParamName = "mobileNo";
        if (command.isChangeInStringParameterNamed(mobileNoParamName, this.mobileNo)) {
            final String newValue = command.stringValueOfParameterNamed(mobileNoParamName);
            actualChanges.put(mobileNoParamName, newValue);
            this.mobileNo = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String dateFormatAsInput = command.dateFormat();
        final String localeAsInput = command.locale();

        final String salaryDateParamName = "salaryDate";
        final String localeParamName = "locale";
        final String dateFormatParamName = "dateFormat";
        if (command.isChangeInDateParameterNamed(salaryDateParamName, this.salaryDate)) {
            final String valueAsInput = command.stringValueOfParameterNamed(salaryDateParamName);
            actualChanges.put(salaryDateParamName, valueAsInput);
            actualChanges.put(dateFormatParamName, dateFormatAsInput);
            actualChanges.put(localeParamName, localeAsInput);

            final LocalDate newValue = command.localDateValueOfParameterNamed(salaryDateParamName);
            this.salaryDate = Date.from(newValue.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
        }

        final String dobParamName = "dob";
        if (command.isChangeInDateParameterNamed(dobParamName, this.dob)) {
            final String valueAsInput = command.stringValueOfParameterNamed(dobParamName);
            actualChanges.put(dobParamName, valueAsInput);

            final LocalDate newValue = command.localDateValueOfParameterNamed(dobParamName);
            this.dob = Date.from(newValue.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
        }

        /*
         * final String mobileNoParamName = "mobileNo"; if (command.isChangeInStringParameterNamed(mobileNoParamName,
         * this.mobileNo)) { final String newValue = command.stringValueOfParameterNamed(mobileNoParamName);
         * actualChanges.put(mobileNoParamName, newValue); this.mobileNo = StringUtils.defaultIfEmpty(newValue, null); }
         */
        final String altNumberParamName = "altNumber";
        if (command.isChangeInStringParameterNamed(altNumberParamName, this.altNumber)) {
            final String newValue = command.stringValueOfParameterNamed(altNumberParamName);
            actualChanges.put(altNumberParamName, newValue);
            this.altNumber = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String fatherNameParamName = "fatherName";
        if (command.isChangeInStringParameterNamed(fatherNameParamName, this.fatherName)) {
            final String newValue = command.stringValueOfParameterNamed(fatherNameParamName);
            actualChanges.put(fatherNameParamName, newValue);
            this.fatherName = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String applicantTypeParamName = "applicantType";
        if (command.isChangeInStringParameterNamed(applicantTypeParamName, this.applicantType)) {
            final String newValue = command.stringValueOfParameterNamed(applicantTypeParamName);
            actualChanges.put(applicantTypeParamName, newValue);
            this.applicantType = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String referByParamName = "refBy";
        if (command.isChangeInStringParameterNamed(referByParamName, this.referBy)) {
            final String newValue = command.stringValueOfParameterNamed(referByParamName);
            actualChanges.put(referByParamName, newValue);
            this.referBy = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String companyNameParamName = "companyName";
        if (command.isChangeInStringParameterNamed(companyNameParamName, this.companyName)) {
            final String newValue = command.stringValueOfParameterNamed(companyNameParamName);
            actualChanges.put(companyNameParamName, newValue);
            this.companyName = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String monthlyIncomeParamName = "monthlyIncome";
        if (command.isChangeInStringParameterNamed(monthlyIncomeParamName, this.monthlyIncome)) {
            final String newValue = command.stringValueOfParameterNamed(monthlyIncomeParamName);
            actualChanges.put(monthlyIncomeParamName, newValue);
            this.monthlyIncome = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String salaryPeriodParamName = "salaryPeriod";
        if (command.isChangeInStringParameterNamed(monthlyIncomeParamName, this.salaryPeriod)) {
            final String newValue = command.stringValueOfParameterNamed(salaryPeriodParamName);
            actualChanges.put(salaryPeriodParamName, newValue);
            this.salaryPeriod = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String maritalStatusParamName = "maritalStatus";
        if (command.isChangeInStringParameterNamed(maritalStatusParamName, this.maritalStatus)) {
            final String newValue = command.stringValueOfParameterNamed(maritalStatusParamName);
            actualChanges.put(maritalStatusParamName, newValue);
            this.maritalStatus = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String spousenameParamName = "spouseName";
        if (command.isChangeInStringParameterNamed(spousenameParamName, this.spousename)) {
            final String newValue = command.stringValueOfParameterNamed(spousenameParamName);
            actualChanges.put(spousenameParamName, newValue);
            this.spousename = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String professionParamName = "profession";
        if (command.isChangeInStringParameterNamed(professionParamName, this.profession)) {
            final String newValue = command.stringValueOfParameterNamed(professionParamName);
            actualChanges.put(professionParamName, newValue);
            this.profession = StringUtils.defaultIfEmpty(newValue, null);
        }

        return actualChanges;
    }

    /*
     * public void setImage(Image image) { this.proof = image; }
     *
     * public Image getImage() { return this.proof; }
     */

    public String getName() {
        return this.name;
    }

}
