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
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.infrastructure.documentmanagement.domain.Image;

@Entity
@Table(name = "m_apply_new_vehicle_loan")
public class VehicleDetails extends AbstractPersistableCustom {

    @Column(name = "vehicle_number", nullable = false, length = 100)
    private String vehicleNumber;

    @Column(name = "maker", nullable = false, length = 100)
    private String maker;

    @Column(name = "model", nullable = false, length = 100)
    private String model;

    @Column(name = "color", nullable = false, length = 100)
    private String color;

    @Column(name = "mfg_year", nullable = false, length = 100)
    private String mfgYear;

    @Column(name = "engine_number", nullable = false, length = 100)
    private String engineNumber;

    @Column(name = "chassis_number", nullable = false, length = 100)
    private String chassisNumber;

    @Column(name = "insurance_company", nullable = false, length = 100)
    private String insuranceCompany;

    @Column(name = "insurance_policy", nullable = false, length = 100)
    private String insurancePolicy;

    @Column(name = "insurance_expiry", nullable = false, length = 100)
    private Date insuranceExpiry;

    @Column(name = "pollution_cert_expiry", nullable = false, length = 100)
    private Date pollutionCertExpiry;

    @Column(name = "registration", nullable = false, length = 100)
    private Date registration;

    @Column(name = "live_km_reading", nullable = false, length = 100)
    private String kmReading;

    @OneToOne(optional = true)
    @JoinColumn(name = "rc_book_image_id", nullable = true)
    private Image image;

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
        final String kmReading = command.stringValueOfParameterNamed("kmReading");

        return new VehicleDetails(vehicleNumber, maker, model, color, mfgYear, engineNumber, chassisNumber, insuranceCompany,
                insurancePolicy, insuranceExpiry, pollutionCertExpiry, registration, kmReading);

    }

    private VehicleDetails(final String vehicleNumber, final String maker, final String model, final String color, final String mfgYear,
            final String engineNumber, final String chassisNumber, final String insuranceCompany, final String insurancePolicy,
            final Date insuranceExpiry, final Date pollutionCertExpiry, final Date registration, final String kmReading) {
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

    public void setImage(Image image) {
        this.image = image;
    }

    public Image getImage() {
        return this.image;
    }

}
