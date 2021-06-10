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
public class LoanTransferDashboardData {

    final Long customerOnLine; // Prior to Customer Loan Amount Transferred

    final Long customerLoanTransferred; // After Customer Loan Amount Transferred

    final Long newBankAccount; // non-verified Bank Accounts

    final Long loanAmountTransferred;

    final Long BankAccountVerified;

    public static LoanTransferDashboardData instance(final Long customerOnLine, final Long customerLoanTransferred,
            final Long newBankAccount, final Long loanAmountTransferred, final Long BankAccountVerified) {
        return new LoanTransferDashboardData(customerOnLine, customerLoanTransferred, newBankAccount, loanAmountTransferred,
                BankAccountVerified);
    }

    public LoanTransferDashboardData(final Long customerOnLine, final Long customerLoanTransferred, final Long newBankAccount,
            final Long loanAmountTransferred, final Long BankAccountVerified) {
        this.customerOnLine = customerOnLine;
        this.customerLoanTransferred = customerLoanTransferred;
        this.newBankAccount = newBankAccount;
        this.loanAmountTransferred = loanAmountTransferred;
        this.BankAccountVerified = BankAccountVerified;
    }
}
