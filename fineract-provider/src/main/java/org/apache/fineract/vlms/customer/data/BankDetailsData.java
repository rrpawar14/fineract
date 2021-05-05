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
public class BankDetailsData {

    private final Long id;

    private final Long loanEligibleAmount;

    private final String accountType;

    private final String disbursalType;

    private final String accountNumber;

    private final String accountHolderName;

    private final String bankName;

    private final String branchName;

    private final String IFSC;

    public static BankDetailsData instance(final Long id, final Long loanEligibleAmount, final String accountType,
            final String disbursalType, final String accountNumber, final String accountHolderName, final String bankName,
            final String branchName, final String IFSC) {

        return new BankDetailsData(id, loanEligibleAmount, accountType, disbursalType, accountNumber, accountHolderName, bankName,
                branchName, IFSC);
    }

    public BankDetailsData(final Long id, final Long loanEligibleAmount, final String accountType, final String disbursalType,
            final String accountNumber, final String accountHolderName, final String bankName, final String branchName, final String IFSC) {
        this.id = id;
        this.loanEligibleAmount = loanEligibleAmount;
        this.accountType = accountType;
        this.disbursalType = disbursalType;
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.bankName = bankName;
        this.branchName = branchName;
        this.IFSC = IFSC;
    }
}
