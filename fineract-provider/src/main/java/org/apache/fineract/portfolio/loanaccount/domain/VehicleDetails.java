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
import javax.persistence.Table;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.infrastructure.core.service.DateUtils;

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

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(1);

        final String vehicleNumberParamName = "vehicleNumber";
        if (command.isChangeInStringParameterNamed(vehicleNumberParamName, this.vehicleNumber)) {
            final String newValue = command.stringValueOfParameterNamed(vehicleNumberParamName);
            actualChanges.put(vehicleNumberParamName, newValue);
            this.vehicleNumber = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String makerParamName = "maker";
        if (command.isChangeInStringParameterNamed(makerParamName, this.maker)) {
            final String newValue = command.stringValueOfParameterNamed(makerParamName);
            actualChanges.put(makerParamName, newValue);
            this.maker = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String modelParamName = "model";
        if (command.isChangeInStringParameterNamed(modelParamName, this.model)) {
            final String newValue = command.stringValueOfParameterNamed(modelParamName);
            actualChanges.put(modelParamName, newValue);
            this.model = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String colorParamName = "color";
        if (command.isChangeInStringParameterNamed(colorParamName, this.color)) {
            final String newValue = command.stringValueOfParameterNamed(colorParamName);
            actualChanges.put(colorParamName, newValue);
            this.color = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String mfgYearParamName = "mfgYear";
        if (command.isChangeInStringParameterNamed(mfgYearParamName, this.mfgYear)) {
            final String newValue = command.stringValueOfParameterNamed(mfgYearParamName);
            actualChanges.put(mfgYearParamName, newValue);
            this.mfgYear = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String engineNumberParamName = "engineNumber";
        if (command.isChangeInStringParameterNamed(engineNumberParamName, this.engineNumber)) {
            final String newValue = command.stringValueOfParameterNamed(engineNumberParamName);
            actualChanges.put(engineNumberParamName, newValue);
            this.engineNumber = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String chassisNumberParamName = "chassisNumber";
        if (command.isChangeInStringParameterNamed(chassisNumberParamName, this.chassisNumber)) {
            final String newValue = command.stringValueOfParameterNamed(chassisNumberParamName);
            actualChanges.put(chassisNumberParamName, newValue);
            this.chassisNumber = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String insuranceCompanyParamName = "insuranceCompany";
        if (command.isChangeInStringParameterNamed(insuranceCompanyParamName, this.insuranceCompany)) {
            final String newValue = command.stringValueOfParameterNamed(insuranceCompanyParamName);
            actualChanges.put(insuranceCompanyParamName, newValue);
            this.insuranceCompany = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String insurancePolicyParamName = "insurancePolicy";
        if (command.isChangeInStringParameterNamed(insurancePolicyParamName, this.insurancePolicy)) {
            final String newValue = command.stringValueOfParameterNamed(insurancePolicyParamName);
            actualChanges.put(insurancePolicyParamName, newValue);
            this.insurancePolicy = StringUtils.defaultIfEmpty(newValue, null);
        }

        /*
         * final String mobileNoParamName = "mobileNo"; if (command.isChangeInStringParameterNamed(mobileNoParamName,
         * this.mobileNo)) { final String newValue = command.stringValueOfParameterNamed(mobileNoParamName);
         * actualChanges.put(mobileNoParamName, newValue); this.mobileNo = StringUtils.defaultIfEmpty(newValue, null); }
         */
        final String dateFormatAsInput = command.dateFormat();
        final String localeAsInput = command.locale();
        final String localeParamName = "locale";
        final String dateFormatParamName = "dateFormat";

        final String insuranceExpiryParamName = "insuranceExpiry";

        if (command.isChangeInDateParameterNamed(insuranceExpiryParamName, this.insuranceExpiry)) {
            final String valueAsInsuranceInput = command.stringValueOfParameterNamed(insuranceExpiryParamName);

            actualChanges.put(insuranceExpiryParamName, valueAsInsuranceInput);

            final LocalDate newValue = command.localDateValueOfParameterNamed(insuranceExpiryParamName);
            this.insuranceExpiry = Date.from(newValue.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());

        }

        final String pollutionCertExpiryParamName = "pollutionCertExpiry";

        if (command.isChangeInDateParameterNamed(pollutionCertExpiryParamName, this.pollutionCertExpiry)) {

            final String valueAsExpiryInput = command.stringValueOfParameterNamed(pollutionCertExpiryParamName);

            actualChanges.put(pollutionCertExpiryParamName, valueAsExpiryInput);

            final LocalDate newValue = command.localDateValueOfParameterNamed(pollutionCertExpiryParamName);
            this.pollutionCertExpiry = Date.from(newValue.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());

        }

        final String registrationParamName = "registration";

        if (command.isChangeInDateParameterNamed(registrationParamName, this.registration)) {

            final String valueAsRegistrationInput = command.stringValueOfParameterNamed(registrationParamName);
            actualChanges.put(registrationParamName, valueAsRegistrationInput);

            final LocalDate newValue = command.localDateValueOfParameterNamed(registrationParamName);
            this.registration = Date.from(newValue.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());

        }

        actualChanges.put(dateFormatParamName, dateFormatAsInput);
        actualChanges.put(localeParamName, localeAsInput);

        return actualChanges;
    }

    public String getEngineNumber() {
        return this.engineNumber;
    }

    public String getChassisNumber() {
        return this.chassisNumber;
    }

    public String insurancePolicyNumber() {
        return this.insurancePolicy;
    }

    public String getkmReading() {
        return this.kmReading.toString();
    }

    public String getVehicleNumber() {
        return this.vehicleNumber;
    }
    /*
     * public void setVehicleDetails(final VehicleImages vehicleImage) { this.vehicleImage = vehicleImage; }
     *
     * public void setImage(VehicleImages vehicleImage) { this.vehicleImage = vehicleImage; }
     *
     * public VehicleImages getImage() { return this.vehicleImage; }
     */

}
