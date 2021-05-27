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

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

@Entity
@Table(name = "m_loan_change_request")
public class LoanChangeRequest extends AbstractPersistableCustom {

    @OneToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private FELoanDetails loan;

    @Column(name = "loan_interest", nullable = false)
    private BigDecimal loanInterest;

    @Column(name = "loan_amount", nullable = true)
    private BigDecimal loanAmount;

    @Column(name = "doc_charges", nullable = true)
    private BigDecimal docCharges;

    @Column(name = "processing_charges", nullable = true)
    private BigDecimal processingCharges;

    @Column(name = "loan_closure_amount", nullable = true)
    private BigDecimal loanClosureAmount;

    public LoanChangeRequest() {}

    public static LoanChangeRequest fromJson(final JsonCommand command, final FELoanDetails loan) {

        final BigDecimal loanInterest = command.bigDecimalValueOfParameterNamed("loanInterest");
        final BigDecimal loanAmount = command.bigDecimalValueOfParameterNamed("loanAmount");
        final BigDecimal docCharges = command.bigDecimalValueOfParameterNamed("docCharges");
        final BigDecimal processingCharges = command.bigDecimalValueOfParameterNamed("processingCharges");
        final BigDecimal loanClosureAmount = command.bigDecimalValueOfParameterNamed("loanClosureAmount");

        return new LoanChangeRequest(loan, loanInterest, loanAmount, docCharges, processingCharges, loanClosureAmount);

    }

    public LoanChangeRequest(final FELoanDetails loan, final BigDecimal loanInterest, final BigDecimal loanAmount,
            final BigDecimal docCharges, final BigDecimal processingCharges, final BigDecimal loanClosureAmount) {
        this.loan = loan;
        this.loanInterest = loanInterest;
        this.loanAmount = loanAmount;
        this.docCharges = docCharges;
        this.processingCharges = processingCharges;
        this.loanClosureAmount = loanClosureAmount;
    }

}
