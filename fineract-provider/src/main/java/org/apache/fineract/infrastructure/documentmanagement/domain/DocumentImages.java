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
package org.apache.fineract.infrastructure.documentmanagement.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.loanaccount.domain.BankDetails;
import org.apache.fineract.portfolio.loanaccount.domain.CustomerDetails;
import org.apache.fineract.portfolio.loanaccount.domain.CustomerGuarantor;
import org.apache.fineract.portfolio.loanaccount.domain.VehicleLoan;
import org.apache.fineract.vlms.branchmodule.domain.Employee;
import org.apache.fineract.vlms.fieldexecutive.domain.FEEnroll;

@Entity
@Table(name = "m_documents_images")
public final class DocumentImages extends AbstractPersistableCustom {

    @Column(name = "location", length = 500)
    private String location;

    @Column(name = "storage_type_enum")
    private Integer storageType;

    @Column(name = "entity_name")
    private String entityName;

    @Column(name = "document_number")
    private String documentNumber;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "bank_id", nullable = true)
    private BankDetails bankImage;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "guarantor_id", nullable = true)
    private CustomerGuarantor customerGuarantor;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "loan_id", nullable = true)
    private VehicleLoan vehicleLoan;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "feEnroll_id", nullable = true)
    private FEEnroll feEnroll;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "customerdetails_id", nullable = true)
    private CustomerDetails customerDetails;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "employee_id", nullable = true)
    private Employee employee;

    public DocumentImages(final String location, final String entityName, final String documentNumber, final StorageType storageType,
            final BankDetails bankDetailsImage, final CustomerGuarantor customerGuarantor, final VehicleLoan vehicleLoan,
            final FEEnroll feEnroll, final CustomerDetails customerDetails, final Employee employee) {
        this.location = location;
        this.entityName = entityName;
        this.storageType = storageType.getValue();
        this.bankImage = bankDetailsImage;
        this.customerGuarantor = customerGuarantor;
        this.vehicleLoan = vehicleLoan;
        this.feEnroll = feEnroll;
        this.customerDetails = customerDetails;
        this.employee = employee;
        this.documentNumber = documentNumber;
        System.out.println("documentNumberT: " + documentNumber);
    }

    DocumentImages() {

    }

    public String getLocation() {
        return this.location;
    }

    public Integer getStorageType() {
        return this.storageType;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public void setCustomerId(final CustomerDetails customerDetail) {
        this.customerDetails = customerDetail;
    }

    public void setGuarantorId(final CustomerGuarantor customerGuarantor) {
        this.customerGuarantor = customerGuarantor;
    }

    public void setBankId(final BankDetails bankImage) {
        this.bankImage = bankImage;
    }

    public void setFeEnrollId(final FEEnroll feEnroll) {
        this.feEnroll = feEnroll;
    }

    public void setVehicleLoanId(final VehicleLoan vehicleLoan) {
        this.vehicleLoan = vehicleLoan;
    }

    public void setEmployeeId(final Employee employee) {
        this.employee = employee;
    }

    public CustomerDetails getCustomerId() {
        return this.customerDetails;
    }

    public void setStorageType(final Integer storageType) {
        this.storageType = storageType;
    }

}
