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
public class VehicleDetailsData {

    private Long id;

    private String vehicleNumber;

    private String maker;

    private String model;

    private String color;

    private String mfgYear;

    private String engineNumber;

    private String chassisNumber;

    private String insuranceCompany;

    private String insurancePolicy;

    private LocalDate insuranceExpiry;

    private LocalDate pollutionCertExpiry;

    private LocalDate registration;

    private Long kmReading;

    public static VehicleDetailsData instance(final Long id, final String vehicleNumber, final String maker, final String model,
            final String color, final String mfgYear, final String engineNumber, final String chassisNumber, final String insuranceCompany,
            final String insurancePolicy, final LocalDate insuranceExpiry, final LocalDate pollutionCertExpiry,
            final LocalDate registration, final Long kmReading) {

        return new VehicleDetailsData(id, vehicleNumber, maker, model, color, mfgYear, engineNumber, chassisNumber, insuranceCompany,
                insurancePolicy, insuranceExpiry, pollutionCertExpiry, registration, kmReading);
    }

    public VehicleDetailsData(final Long id, final String vehicleNumber, final String maker, final String model, final String color,
            final String mfgYear, final String engineNumber, final String chassisNumber, final String insuranceCompany,
            final String insurancePolicy, final LocalDate insuranceExpiry, final LocalDate pollutionCertExpiry,
            final LocalDate registration, final Long kmReading) {
        this.id = id;
        this.vehicleNumber = vehicleNumber;
        this.maker = maker;
        this.model = model;
        this.color = color;
        this.mfgYear = mfgYear;
        this.engineNumber = engineNumber;
        this.chassisNumber = chassisNumber;
        this.insuranceCompany = insuranceCompany;
        this.insurancePolicy = insurancePolicy;
        this.insuranceExpiry = insuranceExpiry;
        this.pollutionCertExpiry = pollutionCertExpiry;
        this.registration = registration;
        this.kmReading = kmReading;
    }
}
