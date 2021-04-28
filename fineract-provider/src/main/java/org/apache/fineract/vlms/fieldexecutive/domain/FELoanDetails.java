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
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

@Entity
@Table(name = "m_loan_details")
public class FELoanDetails extends AbstractPersistableCustom {

    @Column(name = "loan_amount")
    private Integer loanAmount;

    @Column(name = "loan_term")
    private Integer loanTerm;

    @Column(name = "loan_interest")
    private BigDecimal loanInterest;

    @Column(name = "emi")
    private Integer emi;

    @Column(name = "interest_inr")
    private Integer interestINR;

    @Column(name = "due_date")
    private Date dueDate;

    /*
     * @OneToOne(optional = true)
     *
     * @JoinColumn(name = "rc_book_image_id", nullable = true) private VehicleImages vehicleImage;
     */

    public static FELoanDetails fromJson(final JsonCommand command) {

        final Integer loanAmount = command.integerValueOfParameterNamed("loanAmount");
        final Integer loanTerm = command.integerValueOfParameterNamed("loanTerm");
        final BigDecimal loanInterest = command.bigDecimalValueOfParameterNamed("loanInterest");
        final Integer emi = command.integerValueOfParameterNamed("emi");
        final Integer interestINR = command.integerValueOfParameterNamed("interestINR");
        final Date dueDate = command.dateValueOfParameterNamed("dueDate");

        return new FELoanDetails(loanAmount, loanTerm, loanInterest, emi, interestINR, dueDate);

    }

    public FELoanDetails() {}

    private FELoanDetails(final Integer loanAmount, final Integer loanTerm, final BigDecimal loanInterest, final Integer emi,
            final Integer interestINR, final Date dueDate) {
        this.loanAmount = loanAmount;
        this.loanTerm = loanTerm;
        this.loanInterest = loanInterest;
        this.emi = emi;
        this.interestINR = interestINR;
        this.dueDate = dueDate;
    }
}
