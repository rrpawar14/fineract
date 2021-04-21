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
@Table(name = "m_vehicle_details")
public class VehicleDetails extends AbstractPersistableCustom {

    @Column(name = "vehicle_number")
    private String vehicleNumber;

    @Column(name = "maker")
    private String maker;

    @Column(name = "model")
    private String model;

    @Column(name = "color")
    private String color;

    @Column(name = "mfg_year")
    private String mfgYear;

    @Column(name = "engine_number")
    private String engineNumber;

    @Column(name = "chassis_number")
    private String chassisNumber;

    @Column(name = "insurance_company")
    private String insuranceCompany;

    @Column(name = "insurance_policy")
    private String insurancePolicy;

    @Column(name = "insurance_expiry")
    private Date insuranceExpiry;

    @Column(name = "pollution_cert_expiry")
    private Date pollutionCertExpiry;

    @Column(name = "registration")
    private Date registration;

    @Column(name = "live_km_reading")
    private Integer kmReading;

    /*
     * @OneToOne(optional = true)
     *
     * @JoinColumn(name = "rc_book_image_id", nullable = true) private VehicleImages vehicleImage;
     */

    public VehicleDetails() {}

    public static VehicleDetails fromJson(final JsonCommand command) {

        final String vehicleNumber = command.stringValueOfParameterNamed("vehicleNumber");
        final String maker = command.stringValueOfParameterNamed("maker");
        final String model = command.stringValueOfParameterNamed("model");
        final String color = command.stringValueOfParameterNamed("color");
        final String mfgYear = command.stringValueOfParameterNamed("mfgYear");
        final String engineNumber = command.stringValueOfParameterNamed("engineNumber");
        final String chassisNumber = command.stringValueOfParameterNamed("chassisNumber");
        final String insuranceCompany = command.stringValueOfParameterNamed("insuranceCompany");
        final String insurancePolicy = command.stringValueOfParameterNamed("insurancePolicy");
        final Date insuranceExpiry = command.dateValueOfParameterNamed("insuranceExpiry");
        final Date pollutionCertExpiry = command.dateValueOfParameterNamed("pollutionCertExpiry");
        final Date registration = command.dateValueOfParameterNamed("registration");
        final Integer kmReading = command.integerValueOfParameterNamed("kmReading");

        return new VehicleDetails(vehicleNumber, maker, model, color, mfgYear, engineNumber, chassisNumber, insuranceCompany,
                insurancePolicy, insuranceExpiry, pollutionCertExpiry, registration, kmReading);

    }

    private VehicleDetails(final String vehicleNumber, final String maker, final String model, final String color, final String mfgYear,
            final String engineNumber, final String chassisNumber, final String insuranceCompany, final String insurancePolicy,
            final Date insuranceExpiry, final Date pollutionCertExpiry, final Date registration, final Integer kmReading) {
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

    /*
     * public void setVehicleDetails(final VehicleImages vehicleImage) { this.vehicleImage = vehicleImage; }
     *
     * public void setImage(VehicleImages vehicleImage) { this.vehicleImage = vehicleImage; }
     *
     * public VehicleImages getImage() { return this.vehicleImage; }
     */

}
