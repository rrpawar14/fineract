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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.useradministration.domain.AppUser;

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

    @Column(name = "created_date", nullable = true, length = 100)
    private Date createdDate;

    @OneToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "customerdetails_id", nullable = true)
    private CustomerDetails customerDetails;

    @OneToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicledetails_id", nullable = true)
    private VehicleDetails vehicleDetails;

    @OneToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "guarantordetails_id", nullable = true)
    private CustomerGuarantor customerGuarantor;

    @OneToOne(cascade = CascadeType.ALL, optional = true, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "loandetail_id", nullable = true)
    private Loan loanDetails;

    @OneToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "bankdetails_id", nullable = true)
    private BankDetails bankDetails;

    @OneToOne(optional = true)
    @JoinColumn(name = "customer_id", nullable = true)
    private AppUser appuser;

    public VehicleLoan() {}

    public static VehicleLoan fromJson(final JsonCommand command, final CustomerDetails customerDetails,
            final VehicleDetails vehicleDetails, final CustomerGuarantor customerGuarantor, final Loan loanDetails,
            final BankDetails bankDetails, final AppUser appuser) {

        final String customerName = command.stringValueOfParameterNamed("customerName");
        final String vehicleType = command.stringValueOfParameterNamed("vehicleType");
        final String dealer = command.stringValueOfParameterNamed("dealer");
        final String invoiceNumber = command.stringValueOfParameterNamed("invoiceNumber");
        final String vehicleCondition = command.stringValueOfParameterNamed("vehicleCondition");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date createdDate = new Date();

        // final Integer image = command.stringValueOfParameterNamed("invoiceImageId");

        return new VehicleLoan(customerName, vehicleType, dealer, invoiceNumber, createdDate, customerDetails, vehicleDetails,
                customerGuarantor, loanDetails, bankDetails, appuser);

    }

    private VehicleLoan(final String customerName, final String vehicleType, final String dealer, final String invoiceNumber,
            final Date createdDate, final CustomerDetails customerDetails, final VehicleDetails vehicleDetails,
            final CustomerGuarantor customerGuarantor, final Loan loanDetails, final BankDetails bankDetails, final AppUser appuser) {
        this.customerName = customerName;
        this.vehicleType = vehicleType;
        this.dealer = dealer;
        this.invoiceNumber = invoiceNumber;
        this.createdDate = createdDate;
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

    public String getInvoiceNumber() {
        return this.invoiceNumber;
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
