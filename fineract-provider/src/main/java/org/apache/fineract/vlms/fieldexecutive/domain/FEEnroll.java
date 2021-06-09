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
import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.infrastructure.core.service.DateUtils;

@Entity
@Table(name = "m_feenroll", uniqueConstraints = { @UniqueConstraint(columnNames = { "mobile_number" }, name = "mobile_number") })
public class FEEnroll extends AbstractPersistableCustom {

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Column(name = "mobile_number", nullable = false, length = 100)
    private String mobileNumber; // MobileNo is stored in username column for authentication

    @Column(name = "alternate_mobile_number", nullable = false, length = 100)
    private String alternateMobileNumber;

    @Column(name = "alternate_mobile_number_two", nullable = false, length = 100)
    private String alternateMobileNumberTwo;

    @Column(name = "dob", nullable = false, length = 100)
    private Date dob;

    @Column(name = "father_name", nullable = false, length = 100)
    private String fatherName;

    @Column(name = "spouse_name", nullable = false, length = 100)
    private String spouseName;

    @Column(name = "gender", nullable = false, length = 100)
    private String gender;

    @Column(name = "applicant_type", nullable = false, length = 100)
    private String applicantType;

    @Column(name = "applicant_id", nullable = false, length = 100)
    private String applicantId;

    @Column(name = "created_date", nullable = false, length = 100)
    private Date createdDate;

    @Column(name = "enroll_id", nullable = false, length = 100)
    private String enrollId;

    public FEEnroll() {}

    public static FEEnroll fromJson(final JsonCommand command) {

        final String applicantId = command.stringValueOfParameterNamed("applicantId");
        final String customerName = command.stringValueOfParameterNamed("customerName");
        final String mobileNumber = command.stringValueOfParameterNamed("mobileNumber");
        final String alternateMobileNumber = command.stringValueOfParameterNamed("alternateMobileNumber");
        final String alternateMobileNumberTwo = command.stringValueOfParameterNamed("alternateMobileNumberTwo");
        final Date dob = command.dateValueOfParameterNamed("dob");
        final String fatherName = command.stringValueOfParameterNamed("fatherName");
        final String spouseName = command.stringValueOfParameterNamed("spouseName");
        final String gender = command.stringValueOfParameterNamed("gender");
        final String applicantType = command.stringValueOfParameterNamed("applicantType");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date dateobj = new Date();

        return new FEEnroll(applicantId, customerName, mobileNumber, alternateMobileNumber, alternateMobileNumberTwo, dob, fatherName,
                spouseName, gender, applicantType, dateobj);

    }

    private FEEnroll(final String applicantId, final String customerName, final String mobileNumber, final String alternateMobileNumber,
            final String alternateMobileNumberTwo, final Date dob, final String fatherName, final String spouseName, final String gender,
            final String applicantType, final Date date) {
        this.applicantId = applicantId;
        this.customerName = customerName;
        this.mobileNumber = mobileNumber;
        this.alternateMobileNumber = alternateMobileNumber;
        this.alternateMobileNumberTwo = alternateMobileNumberTwo;
        this.dob = dob;
        this.fatherName = fatherName;
        this.spouseName = spouseName;
        this.gender = gender;
        this.applicantType = applicantType;
        this.createdDate = date;
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(1);

        final String customerNameParamName = "customerName";
        if (command.isChangeInStringParameterNamed(customerNameParamName, this.customerName)) {
            final String newValue = command.stringValueOfParameterNamed(customerNameParamName);
            actualChanges.put(customerNameParamName, newValue);
            this.customerName = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String mobileNumberParamName = "mobileNumber";
        if (command.isChangeInStringParameterNamed(mobileNumberParamName, this.mobileNumber)) {
            final String newValue = command.stringValueOfParameterNamed(mobileNumberParamName);
            actualChanges.put(mobileNumberParamName, newValue);
            this.mobileNumber = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String alternateMobileNumberParamName = "alternateMobileNumber";
        if (command.isChangeInStringParameterNamed(alternateMobileNumberParamName, this.alternateMobileNumber)) {
            final String newValue = command.stringValueOfParameterNamed(alternateMobileNumberParamName);
            actualChanges.put(alternateMobileNumberParamName, newValue);
            this.alternateMobileNumber = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String alternateMobileNumberTwoParamName = "alternateMobileNumberTwo";
        if (command.isChangeInStringParameterNamed(alternateMobileNumberTwoParamName, this.alternateMobileNumberTwo)) {
            final String newValue = command.stringValueOfParameterNamed(alternateMobileNumberTwoParamName);
            actualChanges.put(alternateMobileNumberTwoParamName, newValue);
            this.alternateMobileNumberTwo = StringUtils.defaultIfEmpty(newValue, null);
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

        final String fatherNameParamName = "fatherName";
        if (command.isChangeInStringParameterNamed(fatherNameParamName, this.fatherName)) {
            final String newValue = command.stringValueOfParameterNamed(fatherNameParamName);
            actualChanges.put(fatherNameParamName, newValue);
            this.fatherName = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String spouseNameParamName = "spouseName";
        if (command.isChangeInStringParameterNamed(spouseNameParamName, this.spouseName)) {
            final String newValue = command.stringValueOfParameterNamed(spouseNameParamName);
            actualChanges.put(spouseNameParamName, newValue);
            this.spouseName = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String genderParamName = "gender";
        if (command.isChangeInStringParameterNamed(genderParamName, this.gender)) {
            final String newValue = command.stringValueOfParameterNamed(genderParamName);
            actualChanges.put(genderParamName, newValue);
            this.gender = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String applicantTypeParamName = "applicantType";
        if (command.isChangeInStringParameterNamed(applicantTypeParamName, this.applicantType)) {
            final String newValue = command.stringValueOfParameterNamed(applicantTypeParamName);
            actualChanges.put(applicantTypeParamName, newValue);
            this.applicantType = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String applicantIdParamName = "applicantId";
        if (command.isChangeInStringParameterNamed(applicantTypeParamName, this.applicantType)) {
            final String newValue = command.stringValueOfParameterNamed(applicantIdParamName);
            actualChanges.put(applicantIdParamName, newValue);
            this.applicantId = StringUtils.defaultIfEmpty(newValue, null);
        }

        return actualChanges;
    }

    public void updateEnrollId(final String enrollId) {
        this.enrollId = enrollId;
    }

    public String getEnrollId() {
        return this.enrollId;
    }

}
