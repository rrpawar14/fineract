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
package org.apache.fineract.vlms.customer.data;

/**
 * Immutable data object representing loan summary information.
 */
@SuppressWarnings("unused")
public class VehicleLoanData {

    private Long id;

    private String customerName;

    private String vehicleType;

    private String loanType;

    private CustomerDetailsData customerDetails;

    private GuarantorDetailsData customerGuarantor;

    private VehicleDetailsData vehicleDetails;

    private BankDetailsData bankDetails;

    public static VehicleLoanData instance(final Long id, final String customerName, final String vehicleType, final String loanType,
            final CustomerDetailsData customerDetails, final GuarantorDetailsData customerGuarantor,
            final VehicleDetailsData vehicleDetails, final BankDetailsData bankDetails) {

        return new VehicleLoanData(id, customerName, vehicleType, loanType, customerDetails, customerGuarantor, vehicleDetails,
                bankDetails);
    }

    public VehicleLoanData(final Long id, final String customerName, final String vehicleType, final String loanType,
            final CustomerDetailsData customerDetails, final GuarantorDetailsData customerGuarantor,
            final VehicleDetailsData vehicleDetails, final BankDetailsData bankDetails) {

        this.id = id;

        this.customerName = customerName;

        this.vehicleType = vehicleType;

        this.loanType = loanType;

        this.customerDetails = customerDetails;

        this.customerGuarantor = customerGuarantor;

        this.vehicleDetails = vehicleDetails;

        this.bankDetails = bankDetails;
    }
}
