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

import java.time.LocalDate;

/**
 * Immutable data object representing loan summary information.
 */
@SuppressWarnings("unused")
public class LoanDetailsData {

    private final Long id;

    private final Long loanAmount;

    private final Long loanTerm;

    private final Long loanInterest;

    private final Long emi;

    private final Long interestInr;

    private final Long docCharge;

    private final Long processingCharge;

    private final String pendingDoc;

    private final Long otherCharges;

    private final Long closingAC;

    private final Long closingDiscount;

    private final Long payout;

    private final LocalDate dueDate;

    public static LoanDetailsData instance(final Long id, final Long loanAmount, final Long loanTerm, final Long loanInterest,
            final Long emi, final Long interestInr, final Long docCharge, final Long processingCharge, final String pendingDoc,
            final Long otherCharges, final Long closingAC, final Long closingDiscount, final Long payout, final LocalDate dueDate) {

        return new LoanDetailsData(id, loanAmount, loanTerm, loanInterest, emi, interestInr, docCharge, processingCharge, pendingDoc,
                otherCharges, closingAC, closingDiscount, payout, dueDate);
    }

    public LoanDetailsData(final Long id, final Long loanAmount, final Long loanTerm, final Long loanInterest, final Long emi,
            final Long interestInr, final Long docCharge, final Long processingCharge, final String pendingDoc, final Long otherCharges,
            final Long closingAC, final Long closingDiscount, final Long payout, final LocalDate dueDate) {
        this.id = id;
        this.loanAmount = loanAmount;
        this.loanTerm = loanTerm;
        this.loanInterest = loanInterest;
        this.emi = emi;
        this.interestInr = interestInr;
        this.docCharge = docCharge;
        this.processingCharge = processingCharge;
        this.pendingDoc = pendingDoc;
        this.otherCharges = otherCharges;
        this.closingAC = closingAC;
        this.closingDiscount = closingDiscount;
        this.payout = payout;
        this.dueDate = dueDate;

    }

}
