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
package org.apache.fineract.vlms.branchmodule.data;

/**
 * Immutable data object representing loan summary information.
 */

@SuppressWarnings("unused")
public class BranchAnalyticsAllData {

    final Long cashDemand;

    final Long cashCollection;

    final Long loanAmountBankCollection;

    final Long loanAmountCashCollection;

    final Long feAssigned;

    final Long feFollowup;

    final Long feCompleted;

    final Long insuranceExpired;

    final Long insuranceRenewal;

    final Long insuranceHold;

    final Long insuranceCompleted;

    final Long allocatedCash;

    final Long expense;

    final Long agtAssigned;

    final Long agtNotAssigned;

    final Long rtoExpenses;

    final Long rtoCompleted;

    final Long docPending;

    final Long docCompleted;

    final Long disbursalRepossessed;

    final Long disbursalReleased;

    final Long disbursalBank;

    final Long disbursalCash;

    final Long customerLoanDisburse;

    final Long guarantorLoanDisburse;

    final Long thirdPartyLoanDisburse;

    final Long enquiryDirect;

    final Long enquiryWalkIn;

    final Long enquiryReloan;

    public static BranchAnalyticsAllData instance(final Long cashDemand, final Long cashCollection, final Long loanAmountBankCollection,
            final Long loanAmountCashCollection, final Long feAssigned, final Long feFollowup, final Long feCompleted,
            final Long insuranceExpired, final Long insuranceRenewal, final Long insuranceHold, final Long insuranceCompleted,
            final Long allocatedCash, final Long expense, final Long agtAssigned, final Long agtNotAssigned, final Long rtoExpenses,
            final Long rtoCompleted, final Long docPending, final Long docCompleted, final Long disbursalRepossessed,
            final Long disbursalReleased, final Long disbursalBank, final Long disbursalCash, final Long customerLoanDisburse,
            final Long guarantorLoanDisburse, final Long thirdPartyLoanDisburse, final Long enquiryDirect, final Long enquiryWalkIn,
            final Long enquiryReloan) {
        return new BranchAnalyticsAllData(cashDemand, cashCollection, loanAmountBankCollection, loanAmountCashCollection, feAssigned,
                feFollowup, feCompleted, insuranceExpired, insuranceRenewal, insuranceHold, insuranceCompleted, allocatedCash, expense,
                agtAssigned, agtNotAssigned, rtoExpenses, rtoCompleted, docPending, docCompleted, disbursalRepossessed, disbursalReleased,
                disbursalBank, disbursalCash, customerLoanDisburse, guarantorLoanDisburse, thirdPartyLoanDisburse, enquiryDirect,
                enquiryWalkIn, enquiryReloan);
    }

    public BranchAnalyticsAllData(final Long cashDemand, final Long cashCollection, final Long loanAmountBankCollection,
            final Long loanAmountCashCollection, final Long feAssigned, final Long feFollowup, final Long feCompleted,
            final Long insuranceExpired, final Long insuranceRenewal, final Long insuranceHold, final Long insuranceCompleted,
            final Long allocatedCash, final Long expense, final Long agtAssigned, final Long agtNotAssigned, final Long rtoExpenses,
            final Long rtoCompleted, final Long docPending, final Long docCompleted, final Long disbursalRepossessed,
            final Long disbursalReleased, final Long disbursalBank, final Long disbursalCash, final Long customerLoanDisburse,
            final Long guarantorLoanDisburse, final Long thirdPartyLoanDisburse, final Long enquiryDirect, final Long enquiryWalkIn,
            final Long enquiryReloan) {
        this.cashDemand = cashDemand;
        this.cashCollection = cashCollection;
        this.loanAmountBankCollection = loanAmountBankCollection;
        this.loanAmountCashCollection = loanAmountCashCollection;
        this.feAssigned = feAssigned;
        this.feFollowup = feFollowup;
        this.feCompleted = feCompleted;
        this.insuranceExpired = insuranceExpired;
        this.insuranceRenewal = insuranceRenewal;
        this.insuranceHold = insuranceHold;
        this.insuranceCompleted = insuranceCompleted;
        this.allocatedCash = allocatedCash;
        this.expense = expense;
        this.agtAssigned = agtAssigned;
        this.agtNotAssigned = agtNotAssigned;
        this.rtoExpenses = rtoExpenses;
        this.rtoCompleted = rtoCompleted;
        this.docPending = docPending;
        this.docCompleted = docCompleted;
        this.disbursalRepossessed = disbursalRepossessed;
        this.disbursalReleased = disbursalReleased;
        this.disbursalBank = disbursalBank;
        this.disbursalCash = disbursalCash;
        this.customerLoanDisburse = customerLoanDisburse;
        this.guarantorLoanDisburse = guarantorLoanDisburse;
        this.thirdPartyLoanDisburse = thirdPartyLoanDisburse;
        this.enquiryDirect = enquiryDirect;
        this.enquiryWalkIn = enquiryWalkIn;
        this.enquiryReloan = enquiryReloan;

    }

}
