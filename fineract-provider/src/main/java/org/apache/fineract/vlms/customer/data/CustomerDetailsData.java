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
package org.apache.fineract.vlms.customer.data;

import java.time.LocalDate;

/**
 * Immutable data object representing loan summary information.
 */
@SuppressWarnings("unused")
public class CustomerDetailsData {

    private final Long id;

    private final String name;

    private final String gender;

    private final String mobileNo;

    private final String fatherName;

    private final String altNumber;

    private final String applicantType;

    private final String refBy;

    private final String companyName;

    private final String monthlyIncome;

    private final LocalDate salaryDate;

    private final String salaryPeriod;

    private final LocalDate dob;

    private final String maritalStatus;

    private final String spouseName;

    private final String profession;

    private final AddressData communicationAdd;

    private final AddressData permanentAdd;

    private final AddressData officeAdd;

    public static CustomerDetailsData instance(final Long id, final String name, final String gender, final String mobileNo,
            final String altNumber, final String fatherName, final String applicantType, final String refBy, final String companyName,
            final String monthlyIncome, final LocalDate salaryDate, final String salaryPeriod, final LocalDate dob,
            final String maritalStatus, final String spouseName, final String profession, final AddressData communicationAdd,
            final AddressData permanentAdd, final AddressData officeAdd) {

        return new CustomerDetailsData(id, name, gender, mobileNo, altNumber, fatherName, applicantType, refBy, companyName, monthlyIncome,
                salaryDate, salaryPeriod, dob, maritalStatus, spouseName, profession, communicationAdd, permanentAdd, officeAdd);
    }

    public CustomerDetailsData(final Long id, final String name, final String gender, final String mobileNo, final String altNumber,
            final String fatherName, final String applicantType, final String refBy, final String companyName, final String monthlyIncome,
            final LocalDate salaryDate, final String salaryPeriod, final LocalDate dob, final String maritalStatus, final String spouseName,
            final String profession, final AddressData communicationAdd, final AddressData permanentAdd, final AddressData officeAdd) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.mobileNo = mobileNo;
        this.fatherName = fatherName;
        this.altNumber = altNumber;
        this.applicantType = applicantType;
        this.refBy = refBy;
        this.companyName = companyName;
        this.monthlyIncome = monthlyIncome;
        this.salaryDate = salaryDate;
        this.salaryPeriod = salaryPeriod;
        this.dob = dob;
        this.maritalStatus = maritalStatus;
        this.spouseName = spouseName;
        this.profession = profession;
        this.communicationAdd = communicationAdd;
        this.permanentAdd = permanentAdd;
        this.officeAdd = officeAdd;

    }
}
