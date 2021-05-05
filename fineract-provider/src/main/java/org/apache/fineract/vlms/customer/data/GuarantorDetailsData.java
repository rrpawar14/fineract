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
public class GuarantorDetailsData {

    private final Long id;

    private final String guarantorName;

    private final String mobileNumber;

    private final String gender;

    private final LocalDate dob;

    private final String maritalStatus;

    private final String spouseName;

    private final String profession;

    private final AddressData communicationAdd;

    private final AddressData permanentAdd;

    private final AddressData officeAdd;

    public static GuarantorDetailsData instance(final Long id, final String name, final String mobileNumber, final String gender,
            final LocalDate dob, final String maritalStatus, final String spouseName, final String profession,
            final AddressData communicationAdd, final AddressData permanentAdd, final AddressData officeAdd) {

        return new GuarantorDetailsData(id, name, mobileNumber, gender, dob, maritalStatus, spouseName, profession, communicationAdd,
                permanentAdd, officeAdd);
    }

    public GuarantorDetailsData(final Long id, final String name, final String mobileNumber, final String gender, final LocalDate dob,
            final String maritalStatus, final String spouseName, final String profession, final AddressData communicationAdd,
            final AddressData permanentAdd, final AddressData officeAdd) {
        this.id = id;
        this.guarantorName = name;
        this.mobileNumber = mobileNumber;
        this.gender = gender;
        this.dob = dob;
        this.maritalStatus = maritalStatus;
        this.spouseName = spouseName;
        this.profession = profession;
        this.communicationAdd = communicationAdd;
        this.permanentAdd = permanentAdd;
        this.officeAdd = officeAdd;

    }
}
