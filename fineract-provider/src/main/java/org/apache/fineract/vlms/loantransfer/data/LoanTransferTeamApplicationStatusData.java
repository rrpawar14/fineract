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
package org.apache.fineract.vlms.loantransfer.data;

/**
 * Immutable data object representing loan summary information.
 */

@SuppressWarnings("unused")
public class LoanTransferTeamApplicationStatusData {

    final Boolean LoanStatus; // Prior to Customer Loan Amount Transferred

    final Long loanId;

    /*
     * final Boolean childLoanStatus; // Prior to Customer Loan Amount Transferred
     *
     * final Long childLoanId;
     *
     * final Boolean handLoanStatus; // Prior to Customer Loan Amount Transferred
     *
     * final Long handLoanId;
     */

    public static LoanTransferTeamApplicationStatusData instance(final Boolean LoanStatus, final Long loanId) {
        // final Boolean childLoanStatus, final Long childLoanId, final Boolean handLoanStatus, final Long handLoanId) {
        return new LoanTransferTeamApplicationStatusData(LoanStatus, loanId);
        // , childLoanStatus, childLoanId,handLoanStatus, handLoanId);
    }

    public LoanTransferTeamApplicationStatusData(final Boolean LoanStatus, final Long loanId) {
        this.LoanStatus = LoanStatus;
        this.loanId = loanId;

    }

}
