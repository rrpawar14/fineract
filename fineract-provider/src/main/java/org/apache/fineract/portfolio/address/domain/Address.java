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
package org.apache.fineract.portfolio.address.domain;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.portfolio.client.domain.ClientAddress;

@Entity
@Table(name = "m_address")
public class Address extends AbstractPersistableCustom {

    /*
     * @OneToMany(mappedBy = "address", cascade = CascadeType.ALL) private List<ClientAddress> clientaddress = new
     * ArrayList<>();
     */

    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL)
    private Set<ClientAddress> clientaddress;

    @Column(name = "street")
    private String street;

    @Column(name = "address_line_1")
    private String addressLine1;

    @Column(name = "address_line_2")
    private String addressLine2;

    @Column(name = "address_line_3")
    private String addressLine3;

    @Column(name = "town_village")
    private String townVillage;

    @Column(name = "city")
    private String city;

    @Column(name = "county_district")
    private String countyDistrict;

    @ManyToOne
    @JoinColumn(name = "state_province_id")
    private CodeValue stateProvince;

    @Column(name = "state")
    private String state;

    @Column(name = "landmark")
    private String landmark;

    @Column(name = "area")
    private String area;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private CodeValue country;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "latitude")
    private BigDecimal latitude;

    @Column(name = "longitude")
    private BigDecimal longitude;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_on")
    private Date createdOn;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_on")
    private Date updatedOn;

    private Address(final String street, final String addressLine1, final String addressLine2, final String addressLine3,
            final String townVillage, final String city, final String countyDistrict, final CodeValue stateProvince,
            final CodeValue country, final String postalCode, final BigDecimal latitude, final BigDecimal longitude, final String createdBy,
            final LocalDate createdOn, final String updatedBy, final LocalDate updatedOn) {
        this.street = street;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.addressLine3 = addressLine3;
        this.townVillage = townVillage;
        this.city = city;
        this.countyDistrict = countyDistrict;
        this.stateProvince = stateProvince;
        this.country = country;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdBy = createdBy;
        // this.createdOn = createdOn;
        this.updatedBy = updatedBy;
        // this.updatedOn = updatedOn;

        if (createdOn != null) {
            this.createdOn = Date.from(createdOn.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());

        }

        if (updatedOn != null) {
            this.updatedOn = Date.from(updatedOn.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
        }

    }

    public Address() {

    }

    public static Address fromJson(final JsonCommand command, final CodeValue stateProvince, final CodeValue country) {

        final String addressLine1 = command.stringValueOfParameterNamed("addressLine1");

        final String addressLine2 = command.stringValueOfParameterNamed("addressLine2");

        final String addressLine3 = command.stringValueOfParameterNamed("addressLine3");

        final String street = command.stringValueOfParameterNamed("street");

        final String townVillage = command.stringValueOfParameterNamed("townVillage");

        final String city = command.stringValueOfParameterNamed("city");

        final String countyDistrict = command.stringValueOfParameterNamed("countyDistrict");

        final String postalCode = command.stringValueOfParameterNamed("postalCode");

        final BigDecimal latitude = command.bigDecimalValueOfParameterNamed("latitude");

        final BigDecimal longitude = command.bigDecimalValueOfParameterNamed("longitude");

        final String createdBy = command.stringValueOfParameterNamed("createdBy");

        final LocalDate createdOn = command.localDateValueOfParameterNamed("createdOn");

        final String updatedBy = command.stringValueOfParameterNamed("updatedBy");

        final LocalDate updatedOn = command.localDateValueOfParameterNamed("updatedOn");

        return new Address(street, addressLine1, addressLine2, addressLine3, townVillage, city, countyDistrict, stateProvince, country,
                postalCode, latitude, longitude, createdBy, createdOn, updatedBy, updatedOn);
    }

    public static Address fromJson(final JsonCommand command, final String paramName) {

        JsonObject addressObject = command.parsedJson().getAsJsonObject();
        JsonElement guarantorObject = addressObject.get(paramName);
        System.out.println("guarantorObject: " + guarantorObject);

        if (!(guarantorObject instanceof JsonNull)) { // NOTE : "element instanceof JsonNull" is for handling empty
                                                      // values (and
            // assigning null) while fetching data from results
            addressObject = (JsonObject) guarantorObject;
        }

        JsonObject borrowerInfos = null;
        String borrowerInfo = null;

        Gson gson = new Gson();

        guarantorObject = addressObject.get("addressLine1");
        System.out.println("addressLine1: " + guarantorObject);
        final String addressLine1 = gson.toJson(guarantorObject);

        guarantorObject = addressObject.get("addressLine2");
        System.out.println("addressLine2: " + guarantorObject);
        final String addressLine2 = gson.toJson(guarantorObject);

        guarantorObject = addressObject.get("landmark");
        System.out.println("landmark: " + guarantorObject);
        final String landmark = gson.toJson(guarantorObject);

        guarantorObject = addressObject.get("pincode");
        System.out.println("pincode: " + guarantorObject);
        final String pincode = gson.toJson(guarantorObject);

        guarantorObject = addressObject.get("area");
        System.out.println("area: " + guarantorObject);
        final String area = gson.toJson(guarantorObject);

        guarantorObject = addressObject.get("city");
        System.out.println("city: " + guarantorObject);
        final String city = gson.toJson(guarantorObject);

        guarantorObject = addressObject.get("state");
        System.out.println("state: " + guarantorObject);
        final String state = gson.toJson(guarantorObject);

        return new Address(addressLine1, addressLine2, landmark, pincode, area, city, state);
    }

    private Address(final String addressLine1, final String addressLine2, final String landmark, final String postalCode, final String area,
            final String city, final String state) {

        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.landmark = landmark;
        this.postalCode = postalCode;
        this.area = area;
        this.city = city;
        this.state = state;
    }

    public static Address fromJsonObject(final JsonObject jsonObject, final CodeValue state_province, final CodeValue country) {
        String street = "";
        String addressLine1 = "";
        String addressLine2 = "";
        String addressLine3 = "";
        String townVillage = "";
        String city = "";
        String countyDistrict = "";
        String postalCode = "";
        BigDecimal latitude = BigDecimal.ZERO;
        BigDecimal longitude = BigDecimal.ZERO;
        String createdBy = "";
        Locale locale = Locale.ENGLISH;
        String updatedBy = "";
        LocalDate updatedOnDate = null;
        LocalDate createdOnDate = null;

        if (jsonObject.has("street")) {
            street = jsonObject.get("street").getAsString();

        }

        if (jsonObject.has("addressLine1")) {
            addressLine1 = jsonObject.get("addressLine1").getAsString();
        }
        if (jsonObject.has("addressLine2")) {

            addressLine2 = jsonObject.get("addressLine2").getAsString();
        }
        if (jsonObject.has("addressLine3")) {
            addressLine3 = jsonObject.get("addressLine3").getAsString();
        }
        if (jsonObject.has("townVillage")) {
            townVillage = jsonObject.get("townVillage").getAsString();
        }
        if (jsonObject.has("city")) {
            city = jsonObject.get("city").getAsString();
        }
        if (jsonObject.has("countyDistrict")) {
            countyDistrict = jsonObject.get("countyDistrict").getAsString();
        }
        if (jsonObject.has("postalCode")) {

            postalCode = jsonObject.get("postalCode").getAsString();
        }
        if (jsonObject.has("latitude")) {

            latitude = jsonObject.get("latitude").getAsBigDecimal();
        }
        if (jsonObject.has("longitude")) {

            longitude = jsonObject.get("longitude").getAsBigDecimal();
        }

        if (jsonObject.has("createdBy")) {
            createdBy = jsonObject.get("createdBy").getAsString();
        }
        if (jsonObject.has("createdOn")) {
            String createdOn = jsonObject.get("createdOn").getAsString();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            createdOnDate = LocalDate.parse(createdOn, formatter);

        }
        if (jsonObject.has("updatedBy")) {
            updatedBy = jsonObject.get("updatedBy").getAsString();
        }
        if (jsonObject.has("updatedOn")) {
            String updatedOn = jsonObject.get("updatedOn").getAsString();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            updatedOnDate = LocalDate.parse(updatedOn, formatter);
        }

        return new Address(street, addressLine1, addressLine2, addressLine3, townVillage, city, countyDistrict, state_province, country,
                postalCode, latitude, longitude, createdBy, createdOnDate, updatedBy, updatedOnDate);
    }

    public Set<ClientAddress> getClientaddress() {
        return this.clientaddress;
    }

    public void setClientaddress(Set<ClientAddress> clientaddress) {
        this.clientaddress = clientaddress;
    }

    public String getStreet() {
        return this.street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getAddressLine1() {
        return this.addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return this.addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine3() {
        return this.addressLine3;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    public String getTownVillage() {
        return this.townVillage;
    }

    public void setTownVillage(String townVillage) {
        this.townVillage = townVillage;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountyDistrict() {
        return this.countyDistrict;
    }

    public void setCountyDistrict(String countyDistrict) {
        this.countyDistrict = countyDistrict;
    }

    public CodeValue getStateProvince() {
        return this.stateProvince;
    }

    public void setStateProvince(CodeValue stateProvince) {
        this.stateProvince = stateProvince;
    }

    public CodeValue getCountry() {
        return this.country;
    }

    public void setCountry(CodeValue country) {
        this.country = country;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public BigDecimal getLatitude() {
        return this.latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return this.longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return this.createdOn;
    }

    public void setCreatedOn(LocalDate createdOn) {
        this.createdOn = Date.from(createdOn.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedOn() {
        return this.updatedOn;
    }

    public void setUpdatedOn(LocalDate updatedOn) {
        this.updatedOn = Date.from(updatedOn.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
    }

}
