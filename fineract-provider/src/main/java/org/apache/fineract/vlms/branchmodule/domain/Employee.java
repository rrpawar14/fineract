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
package org.apache.fineract.vlms.branchmodule.domain;

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
import org.apache.fineract.portfolio.loanaccount.domain.BankDetails;

@Entity
@Table(name = "m_employee")
public class Employee extends AbstractPersistableCustom {

    @Column(name = "name")
    private String name;

    @Column(name = "calledname")
    private String calledName;

    @Column(name = "surname")
    private String surName;

    @Column(name = "mobilenumber")
    private String mobileNumber;

    @Column(name = "alternatenumber")
    private String altNumber;

    @Column(name = "officialnumber")
    private String officialNumber;

    @Column(name = "dob")
    private Date dob;

    @Column(name = "gender")
    private String gender;

    @Column(name = "age")
    private Integer age;

    @Column(name = "maritalstatus")
    private String maritalStatus;

    @Column(name = "designation")
    private String designation;

    @Column(name = "spousename")
    private String spousename;

    @Column(name = "bloodgroup")
    private String bloodGroup;

    @Column(name = "fathername")
    private String fatherName;

    @Column(name = "vehiclenumber")
    private String vehicleNumber;

    @Column(name = "vehicleType")
    private String vehicleType; // own or office

    @Column(name = "doj")
    private Date doj;

    @Column(name = "agtnumber")
    private String agtnumber;

    @OneToOne(optional = true)
    @JoinColumn(name = "communicationadd_id", nullable = true)
    private Address communicationAdd;

    @OneToOne(optional = true)
    @JoinColumn(name = "permanentadd_id", nullable = true)
    private Address permanentAdd;

    @OneToOne(optional = true)
    @JoinColumn(name = "bankdetails_id", nullable = true)
    private BankDetails bankDetails;

    @OneToOne(optional = true)
    @JoinColumn(name = "insurancedetails_id", nullable = true)
    private InsuranceDetails insuranceDetails;

    @OneToOne(optional = true)
    @JoinColumn(name = "accidentalinsurancedetails_id", nullable = true)
    private InsuranceDetails accidentalInsuranceDetails;

    @OneToOne(optional = true)
    @JoinColumn(name = "schoolqualification_id", nullable = true)
    private EducationQualification schoolQualification;

    @OneToOne(optional = true)
    @JoinColumn(name = "collegequalification_id", nullable = true)
    private EducationQualification collegeQualification;

    @OneToOne(optional = true)
    @JoinColumn(name = "graduatequalification_id", nullable = true)
    private EducationQualification graduateQualification;

    @OneToOne(optional = true)
    @JoinColumn(name = "postgraduatequalification_id", nullable = true)
    private EducationQualification postgraduateQualification;

    public static Employee fromJson(final JsonCommand command, final Address customerCommunicationAdd, final Address customerPermanentAdd,
            final BankDetails bankDetails, final InsuranceDetails insuranceDetails, final InsuranceDetails accidentalInsuranceDetails,
            final EducationQualification schoolQualification, final EducationQualification collegeQualification,
            final EducationQualification graduateQualification, final EducationQualification postgraduateQualification) {

        final String name = command.stringValueOfParameterNamed("name");
        final String calledName = command.stringValueOfParameterNamed("calledName");
        final String surName = command.stringValueOfParameterNamed("surName");
        final String mobileNumber = command.stringValueOfParameterNamed("mobileNumber");
        final String altNumber = command.stringValueOfParameterNamed("altNumber");
        final String officialNumber = command.stringValueOfParameterNamed("officialNumber");
        final Date dob = command.dateValueOfParameterNamed("dob");
        final String gender = command.stringValueOfParameterNamed("gender");
        final Integer age = command.integerValueOfParameterNamed("age");
        final String maritalStatus = command.stringValueOfParameterNamed("maritalStatus");
        final String designation = command.stringValueOfParameterNamed("designation");
        final String spousename = command.stringValueOfParameterNamed("spousename");
        final String bloodGroup = command.stringValueOfParameterNamed("bloodGroup");
        final String fatherName = command.stringValueOfParameterNamed("fatherName");
        final String vehicleNumber = command.stringValueOfParameterNamed("vehicleNumber");
        final String vehicleType = command.stringValueOfParameterNamed("vehicleType");
        final Date doj = command.dateValueOfParameterNamed("doj");

        return new Employee(name, calledName, surName, mobileNumber, altNumber, officialNumber, dob, gender, age, maritalStatus,
                designation, spousename, bloodGroup, fatherName, vehicleNumber, vehicleType, doj, customerCommunicationAdd,
                customerPermanentAdd, bankDetails, insuranceDetails, accidentalInsuranceDetails, schoolQualification, collegeQualification,
                graduateQualification, postgraduateQualification);

    }

    public Employee() {}

    private Employee(final String name, final String calledName, final String surName, final String mobileNumber, final String altNumber,
            final String officialNumber, final Date dob, final String gender, final Integer age, final String maritalStatus,
            final String designation, final String spousename, final String bloodGroup, final String fatherName, final String vehicleNumber,
            final String vehicleType, final Date doj, final Address customerCommunicationAdd, final Address customerPermanentAdd,
            final BankDetails bankDetails, final InsuranceDetails insuranceDetails, final InsuranceDetails accidentalInsuranceDetails,
            final EducationQualification schoolQualification, final EducationQualification collegeQualification,
            final EducationQualification graduateQualification, final EducationQualification postgraduateQualification) {
        this.name = name;
        this.calledName = calledName;
        this.surName = surName;
        this.mobileNumber = mobileNumber;
        this.altNumber = altNumber;
        this.officialNumber = officialNumber;
        this.dob = dob;
        this.gender = gender;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.designation = designation;
        this.spousename = spousename;
        this.bloodGroup = bloodGroup;
        this.fatherName = fatherName;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.doj = doj;
        this.communicationAdd = customerCommunicationAdd;
        this.permanentAdd = customerPermanentAdd;
        this.bankDetails = bankDetails;
        this.insuranceDetails = insuranceDetails;
        this.accidentalInsuranceDetails = accidentalInsuranceDetails;
        this.schoolQualification = schoolQualification;
        this.collegeQualification = collegeQualification;
        this.graduateQualification = graduateQualification;
        this.postgraduateQualification = postgraduateQualification;
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(1);

        final String nameParamName = "name";
        if (command.isChangeInStringParameterNamed(nameParamName, this.name)) {
            final String newValue = command.stringValueOfParameterNamed(nameParamName);
            actualChanges.put(nameParamName, newValue);
            this.name = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String calledNameParamName = "calledName";
        if (command.isChangeInStringParameterNamed(calledNameParamName, this.calledName)) {
            final String newValue = command.stringValueOfParameterNamed(calledNameParamName);
            actualChanges.put(calledNameParamName, newValue);
            this.calledName = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String surNameParamName = "surName";
        if (command.isChangeInStringParameterNamed(surNameParamName, this.surName)) {
            final String newValue = command.stringValueOfParameterNamed(surNameParamName);
            actualChanges.put(surNameParamName, newValue);
            this.surName = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String mobileNumberParamName = "mobileNumber";
        if (command.isChangeInStringParameterNamed(mobileNumberParamName, this.mobileNumber)) {
            final String newValue = command.stringValueOfParameterNamed(mobileNumberParamName);
            actualChanges.put(mobileNumberParamName, newValue);
            this.mobileNumber = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String altNumberParamName = "altNumber";
        if (command.isChangeInStringParameterNamed(altNumberParamName, this.altNumber)) {
            final String newValue = command.stringValueOfParameterNamed(altNumberParamName);
            actualChanges.put(altNumberParamName, newValue);
            this.altNumber = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String officialNumberParamName = "officialNumber";
        if (command.isChangeInStringParameterNamed(officialNumberParamName, this.officialNumber)) {
            final String newValue = command.stringValueOfParameterNamed(officialNumberParamName);
            actualChanges.put(officialNumberParamName, newValue);
            this.officialNumber = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String dateFormatAsInput = command.dateFormat();
        final String localeAsInput = command.locale();

        final String dobParamName = "dob";
        final String localeParamName = "locale";
        final String dateFormatParamName = "dateFormat";
        if (command.isChangeInDateParameterNamed(dobParamName, this.dob)) {
            final String valueAsInput = command.stringValueOfParameterNamed(dobParamName);
            actualChanges.put(dobParamName, valueAsInput);
            actualChanges.put(dateFormatParamName, dateFormatAsInput);
            actualChanges.put(localeParamName, localeAsInput);

            final LocalDate newValue = command.localDateValueOfParameterNamed(dobParamName);
            this.dob = Date.from(newValue.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
        }

        final String genderParamName = "gender";
        if (command.isChangeInStringParameterNamed(genderParamName, this.name)) {
            final String newValue = command.stringValueOfParameterNamed(genderParamName);
            actualChanges.put(genderParamName, newValue);
            this.gender = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String ageParamName = "age";
        if (command.isChangeInIntegerParameterNamed(ageParamName, this.age)) {
            final Integer newValue = command.integerValueOfParameterNamed(ageParamName);
            actualChanges.put(ageParamName, newValue);
            this.age = newValue;
        }

        final String maritalStatusParamName = "maritalStatus";
        if (command.isChangeInStringParameterNamed(maritalStatusParamName, this.maritalStatus)) {
            final String newValue = command.stringValueOfParameterNamed(maritalStatusParamName);
            actualChanges.put(maritalStatusParamName, newValue);
            this.maritalStatus = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String designationParamName = "designation";
        if (command.isChangeInStringParameterNamed(designationParamName, this.designation)) {
            final String newValue = command.stringValueOfParameterNamed(designationParamName);
            actualChanges.put(designationParamName, newValue);
            this.designation = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String spousenameParamName = "spousename";
        if (command.isChangeInStringParameterNamed(spousenameParamName, this.spousename)) {
            final String newValue = command.stringValueOfParameterNamed(spousenameParamName);
            actualChanges.put(spousenameParamName, newValue);
            this.spousename = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String bloodGroupParamName = "bloodGroup";
        if (command.isChangeInStringParameterNamed(bloodGroupParamName, this.bloodGroup)) {
            final String newValue = command.stringValueOfParameterNamed(bloodGroupParamName);
            actualChanges.put(bloodGroupParamName, newValue);
            this.bloodGroup = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String fatherNameParamName = "fatherName";
        if (command.isChangeInStringParameterNamed(fatherNameParamName, this.fatherName)) {
            final String newValue = command.stringValueOfParameterNamed(fatherNameParamName);
            actualChanges.put(fatherNameParamName, newValue);
            this.fatherName = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String vehicleNumberParamName = "vehicleNumber";
        if (command.isChangeInStringParameterNamed(vehicleNumberParamName, this.vehicleNumber)) {
            final String newValue = command.stringValueOfParameterNamed(vehicleNumberParamName);
            actualChanges.put(vehicleNumberParamName, newValue);
            this.vehicleNumber = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String vehicleTypeParamName = "vehicleType";
        if (command.isChangeInStringParameterNamed(vehicleTypeParamName, this.vehicleType)) {
            final String newValue = command.stringValueOfParameterNamed(vehicleTypeParamName);
            actualChanges.put(vehicleTypeParamName, newValue);
            this.vehicleType = StringUtils.defaultIfEmpty(newValue, null);
        }

        /*
         * if (command.isChangeInStringParameterNamed(dojParamName, this.doj)) { final String newValue =
         * command.stringValueOfParameterNamed(dojParamName); actualChanges.put(dojParamName, newValue); this.doj =
         * StringUtils.defaultIfEmpty(newValue, null); }
         */

        final String dojParamName = "doj";
        if (command.isChangeInDateParameterNamed(dojParamName, this.doj)) {
            final String valueAsInput = command.stringValueOfParameterNamed(dojParamName);
            actualChanges.put(dojParamName, valueAsInput);
            actualChanges.put(dateFormatParamName, dateFormatAsInput);
            actualChanges.put(localeParamName, localeAsInput);

            final LocalDate newValue = command.localDateValueOfParameterNamed(dojParamName);
            this.doj = Date.from(newValue.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
        }

        final String agtnumberParamName = "agtnumber";
        if (command.isChangeInStringParameterNamed(agtnumberParamName, this.agtnumber)) {
            final String newValue = command.stringValueOfParameterNamed(agtnumberParamName);
            actualChanges.put(agtnumberParamName, newValue);
            this.vehicleType = StringUtils.defaultIfEmpty(newValue, null);
        }

        return actualChanges;
    }

    public String getName() {
        return this.name;
    }

}
