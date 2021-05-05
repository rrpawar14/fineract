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
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.vlms.fieldexecutive.domain.FELoanDetails;

@Entity
@Table(name = "m_apply_vehicle_loan")
public class VehicleLoan extends AbstractPersistableCustom {

    @Column(name = "customer_name", nullable = true, length = 100)
    private String customerName;

    @Column(name = "vehicle_type", nullable = true, length = 100)
    private String vehicleType;

    @Column(name = "dealer", nullable = true, length = 100)
    private String dealer;

    @Column(name = "invoice_number", nullable = true, length = 100)
    private String invoiceNumber;

    /*
     * @OneToOne(optional = true)
     *
     * @JoinColumn(name = "invoice_image_id", nullable = true) private DocumentImages image;
     *
     *
     * @ManyToOne private Address address;
     */

    /*
     * @ManyToOne private CustomerDetails customerDetails;
     *
     * @ManyToOne private VehicleDetails vehicleDetails;
     *
     * @ManyToOne private CustomerGuarantor customerGuarantor;
     *
     * @ManyToOne private BankDetails bankDetails;
     */

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
    @JoinColumn(name = "loandetails_id", nullable = true)
    private FELoanDetails loanDetails;

    @OneToOne(optional = true)
    @JoinColumn(name = "bankdetails_id", nullable = true)
    private BankDetails bankDetails;

    @OneToOne(optional = true)
    @JoinColumn(name = "customer_id", nullable = true)
    private AppUser appuser;

    public VehicleLoan() {}

    public static VehicleLoan fromJson(final JsonCommand command, final CustomerDetails customerDetails,
            final VehicleDetails vehicleDetails, final CustomerGuarantor customerGuarantor, final FELoanDetails loanDetails,
            final BankDetails bankDetails, final AppUser appuser) {

        final String customerName = command.stringValueOfParameterNamed("customerName");
        final String vehicleType = command.stringValueOfParameterNamed("vehicleType");
        final String dealer = command.stringValueOfParameterNamed("dealer");
        final String invoiceNumber = command.stringValueOfParameterNamed("invoiceNumber");
        final String vehicleCondition = command.stringValueOfParameterNamed("vehicleCondition");//

        // final Integer image = command.stringValueOfParameterNamed("invoiceImageId");

        return new VehicleLoan(customerName, vehicleType, dealer, invoiceNumber, customerDetails, vehicleDetails, customerGuarantor,
                loanDetails, bankDetails, appuser);

    }

    private VehicleLoan(final String customerName, final String vehicleType, final String dealer, final String invoiceNumber,
            final CustomerDetails customerDetails, final VehicleDetails vehicleDetails, final CustomerGuarantor customerGuarantor,
            final FELoanDetails loanDetails, final BankDetails bankDetails, final AppUser appuser) {
        this.customerName = customerName;
        this.vehicleType = vehicleType;
        this.dealer = dealer;
        this.invoiceNumber = invoiceNumber;
        this.customerDetails = customerDetails;
        this.vehicleDetails = vehicleDetails;
        this.customerGuarantor = customerGuarantor;
        this.loanDetails = loanDetails;
        this.bankDetails = bankDetails;
        this.appuser = appuser;
    }

    public String getName() {
        return this.customerName;
    }

    /*
     * public void setInvoiceImage(final DocumentImages invoiceImage) { this.image = invoiceImage; }
     *
     * public DocumentImages getInvoiceImage() { return this.image; }
     */

    /*
     * public void setImage(Image image) { this.image = image; }
     *
     * public Image getImage() { return this.image; }
     */

}
