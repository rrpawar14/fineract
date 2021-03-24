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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.address.domain.Address;

@Entity
@Table(name = "m_apply_used_vehicle_loan")
public class UsedVehicleLoan extends AbstractPersistableCustom {

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Column(name = "vehicle_type", nullable = false, length = 100)
    private String vehicleType;

    @Column(name = "loan_type", nullable = false, length = 100)
    private String loanType;

    @OneToOne(optional = true)
    @JoinColumn(name = "address_id", nullable = true)
    private Address address;

    @OneToOne(optional = true)
    @JoinColumn(name = "customerdetails_id", nullable = true)
    private CustomerDetails customerDetails;

    @OneToOne(optional = true)
    @JoinColumn(name = "vehicledetails_id", nullable = true)
    private VehicleDetails vehicleDetails;

    @OneToOne(optional = true)
    @JoinColumn(name = "guarantordetails_id", nullable = true)
    private CustomerGuarantor customerGuarantor;

    @OneToOne(optional = true)
    @JoinColumn(name = "bankdetails_id", nullable = true)
    private BankDetails bankDetails;

    public static UsedVehicleLoan fromJson(final JsonCommand command, final Address address, final CustomerDetails customerDetails,
            final VehicleDetails vehicleDetails, final CustomerGuarantor customerGuarantor, final BankDetails bankDetails) {

        final String customerName = command.stringValueOfParameterNamed("customerName");
        final String vehicleType = command.stringValueOfParameterNamed("vehicleType");
        final String loanType = command.stringValueOfParameterNamed("loanType");
        // final String invoiceNumber = command.stringValueOfParameterNamed("invoiceNumber");
        // final Integer image = command.stringValueOfParameterNamed("invoiceImageId");

        return new UsedVehicleLoan(customerName, vehicleType, loanType, address, customerDetails, vehicleDetails, customerGuarantor,
                bankDetails);

    }

    private UsedVehicleLoan(final String customerName, final String vehicleType, final String loanType, final Address address,
            final CustomerDetails customerDetails, final VehicleDetails vehicleDetails, final CustomerGuarantor customerGuarantor,
            final BankDetails bankDetailsObj) {
        this.customerName = customerName;
        this.vehicleType = vehicleType;
        this.loanType = loanType;
        this.address = address;
        this.customerDetails = customerDetails;
        this.vehicleDetails = vehicleDetails;
        this.customerGuarantor = customerGuarantor;
        this.bankDetails = bankDetailsObj;
    }

    /*
     * public void setImage(Image image) { this.image = image; }
     *
     * public Image getImage() { return this.image; }
     *
     */

}
