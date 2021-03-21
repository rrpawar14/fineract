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
import org.apache.fineract.infrastructure.documentmanagement.domain.Image;

@Entity
@Table(name = "m_apply_new_vehicle_loan")
public class NewVehicleLoan extends AbstractPersistableCustom {

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Column(name = "vehicle_type", nullable = false, length = 100)
    private String vehicleType;

    @Column(name = "dealer", nullable = false, length = 100)
    private String dealer;

    @Column(name = "invoice_number", nullable = false, length = 100)
    private String invoiceNumber;

    @OneToOne(optional = true)
    @JoinColumn(name = "invoice_image_id", nullable = true)
    private Image image;

    public static NewVehicleLoan fromJson(final JsonCommand command) {

        final String customerName = command.stringValueOfParameterNamed("customerName");
        final String vehicleType = command.stringValueOfParameterNamed("vehicleType");
        final String dealer = command.stringValueOfParameterNamed("dealer");
        final String invoiceNumber = command.stringValueOfParameterNamed("invoiceNumber");
        // final Integer image = command.stringValueOfParameterNamed("invoiceImageId");

        return new NewVehicleLoan(customerName, vehicleType, dealer, invoiceNumber);

    }

    private NewVehicleLoan(final String customerName, final String vehicleType, final String dealer, final String invoiceNumber) {
        this.customerName = customerName;
        this.vehicleType = vehicleType;
        this.dealer = dealer;
        this.invoiceNumber = invoiceNumber;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Image getImage() {
        return this.image;
    }

}
