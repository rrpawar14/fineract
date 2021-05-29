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
package org.apache.fineract.vlms.branchmodule.data;

import java.time.LocalDate;
import org.apache.fineract.vlms.customer.data.AddressData;
import org.apache.fineract.vlms.customer.data.BankDetailsData;

/**
 * Immutable data object representing loan summary information.
 */

@SuppressWarnings("unused")
public class EmployeeData {

    private Long id;

    private String name;

    private String calledName;

    private String surName;

    private String mobileNumber;

    private String altNumber;

    private String officialNumber;

    private LocalDate dob;

    private String gender;

    private Long age;

    private String maritalStatus;

    private String designation;

    private String spousename;

    private String bloodGroup;

    private String fatherName;

    private String vehicleNumber;

    private String vehicleType; // own or office

    private LocalDate doj;

    private String agtnumber;

    private String status;

    private AddressData communicationAdd;

    private AddressData permanentAdd;

    private BankDetailsData bankDetails;

    private InsuranceDetailsData insuranceDetails;

    private InsuranceDetailsData accidentalInsuranceDetails;

    private EducationQualificationData schoolQualification;

    private EducationQualificationData collegeQualification;

    private EducationQualificationData graduateQualification;

    private EducationQualificationData postgraduateQualification;

    public static EmployeeData instance(final Long id, final String name, final String calledName, final String surName,
            final String mobileNumber, final String altNumber, final String officialNumber, final LocalDate dob, final String gender,
            final Long age, final String maritalStatus, final String designation, final String spousename, final String bloodGroup,
            final String fatherName, final String vehicleNumber, final String vehicleType, final LocalDate doj, final String agtnumber,
            final String status, final AddressData communicationAdd, final AddressData permanentAdd, final BankDetailsData bankDetails,
            final InsuranceDetailsData insuranceDetails, final InsuranceDetailsData accidentalInsuranceDetails,
            final EducationQualificationData schoolQualification, final EducationQualificationData collegeQualification,
            final EducationQualificationData graduateQualification, final EducationQualificationData postgraduateQualification) {

        return new EmployeeData(id, name, calledName, surName, mobileNumber, altNumber, officialNumber, dob, gender, age, maritalStatus,
                designation, spousename, bloodGroup, fatherName, vehicleNumber, vehicleType, doj, agtnumber, status, communicationAdd,
                permanentAdd, bankDetails, insuranceDetails, accidentalInsuranceDetails, schoolQualification, collegeQualification,
                graduateQualification, postgraduateQualification);

    }

    private EmployeeData(final Long id, final String name, final String calledName, final String surName, final String mobileNumber,
            final String altNumber, final String officialNumber, final LocalDate dob, final String gender, final Long age,
            final String maritalStatus, final String designation, final String spousename, final String bloodGroup, final String fatherName,
            final String vehicleNumber, final String vehicleType, final LocalDate doj, final String agtnumber, final String status,
            final AddressData communicationAdd, final AddressData permanentAdd, final BankDetailsData bankDetails,
            final InsuranceDetailsData insuranceDetails, final InsuranceDetailsData accidentalInsuranceDetails,
            final EducationQualificationData schoolQualification, final EducationQualificationData collegeQualification,
            final EducationQualificationData graduateQualification, final EducationQualificationData postgraduateQualification) {
        this.id = id;
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
        this.agtnumber = agtnumber;
        this.status = status;
        this.communicationAdd = communicationAdd;
        this.permanentAdd = permanentAdd;
        this.bankDetails = bankDetails;
        this.insuranceDetails = insuranceDetails;
        this.accidentalInsuranceDetails = accidentalInsuranceDetails;
        this.schoolQualification = schoolQualification;
        this.collegeQualification = collegeQualification;
        this.graduateQualification = graduateQualification;
        this.postgraduateQualification = postgraduateQualification;
    }

}
