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
package org.apache.fineract.vlms.customer.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.VehicleDetails;
import org.apache.fineract.vlms.branchmodule.domain.Employee;
import org.apache.fineract.vlms.fieldexecutive.domain.FEEnroll;

@Entity
@Table(name = "m_documents")
public class Documents extends AbstractPersistableCustom {

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "document_number")
    private String documentNumber;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private CustomerDetails customerDetails;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "guarantor_id")
    private CustomerGuarantor customerGuarantor;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id")
    private BankDetails bankDetails;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "enroll_id")
    private FEEnroll feEnroll;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id")
    private Loan loan;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private VehicleDetails vehicleDetails;

    public static Documents fromJson(final JsonCommand command, final CustomerDetails customerDetails,
            final CustomerGuarantor customerGuarantor, final Employee employee, final BankDetails bankDetails, final FEEnroll enroll,
            final Loan loan, final VehicleDetails vehicleDetails) {

        final String documentType = command.stringValueOfParameterNamed("documentType");
        final String documentNumber = command.stringValueOfParameterNamed("documentNumber");

        return new Documents(documentType, documentNumber, customerDetails, customerGuarantor, employee, bankDetails, enroll, loan,
                vehicleDetails);
    }

    private Documents(final String documentType, final String documentNumber, final CustomerDetails customerDetails,
            final CustomerGuarantor customerGuarantor, final Employee employee, final BankDetails bankDetails, final FEEnroll enroll,
            final Loan loan, final VehicleDetails vehicleDetails) {
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.customerDetails = customerDetails;
        this.customerGuarantor = customerGuarantor;
        this.employee = employee;
        this.bankDetails = bankDetails;
        this.feEnroll = enroll;
        this.loan = loan;
        this.vehicleDetails = vehicleDetails;
    }
}
