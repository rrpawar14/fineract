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
package org.apache.fineract.vlms.fieldexecutive.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

@Entity
@Table(name = "m_fe_used_vehicle_loan")
public class FEUsedVehicleLoan extends AbstractPersistableCustom {

    @Column(name = "customerId", nullable = false, length = 100)
    private String customerId;

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Column(name = "loan_classification", nullable = false, length = 100)
    private String loanClassification;

    @OneToOne(optional = true)
    @JoinColumn(name = "applicant_details_id", nullable = true)
    private FEApplicantDetails applicantDetails;

    @OneToOne(optional = true)
    @JoinColumn(name = "coapplicant_details_id", nullable = true)
    private FECoApplicantDetails coApplicantDetails;

    @OneToOne(optional = true)
    @JoinColumn(name = "vehicle_details_id", nullable = true)
    private FEVehicleDetails vehicleDetails;

    @OneToOne(optional = true)
    @JoinColumn(name = "loan_details_id", nullable = true)
    private FELoanDetails loanDetails;

    @OneToOne(optional = true)
    @JoinColumn(name = "transfer_details_id", nullable = true)
    private FETransferDetails transferDetails;

    public static FEUsedVehicleLoan fromJson(final JsonCommand command, final FEApplicantDetails applicantDetails,
            final FECoApplicantDetails coApplicantDetails, final FEVehicleDetails vehicleDetails, final FELoanDetails loanDetails,
            final FETransferDetails transferDetails) {

        final String customerId = command.stringValueOfParameterNamed("customerId");
        final String customerName = command.stringValueOfParameterNamed("customerName");
        final String loanClassification = command.stringValueOfParameterNamed("loanClassification");

        return new FEUsedVehicleLoan(customerId, customerName, loanClassification, applicantDetails, coApplicantDetails, vehicleDetails,
                loanDetails, transferDetails);

    }

    private FEUsedVehicleLoan(final String customerId, final String customerName, final String loanClassification,
            final FEApplicantDetails applicantDetails, final FECoApplicantDetails coApplicantDetails, final FEVehicleDetails vehicleDetails,
            final FELoanDetails loanDetails, final FETransferDetails transferDetails) {
        this.customerId = customerId; // MobileNo is stored in username column for authentication
        this.customerName = customerName;
        this.loanClassification = loanClassification;
        this.applicantDetails = applicantDetails;
        this.coApplicantDetails = coApplicantDetails;
        this.vehicleDetails = vehicleDetails;
        this.loanDetails = loanDetails;
        this.transferDetails = transferDetails;
    }

}
