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
package org.apache.fineract.vlms.branchmodule.domain;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

@Entity
@Table(name = "m_loan_approval_limit")
public class LoanApprovalLimit extends AbstractPersistableCustom {

    @Column(name = "branch_name")
    private String branchName;

    @Column(name = "approve_limit")
    private BigDecimal cashLimit;

    @Column(name = "requested_on")
    private Date requestedOn;

    @Column(name = "requested_loan_amount")
    private BigDecimal requestedAmount;

    @Column(name = "approve_amount")
    private BigDecimal approvedAmount;

    @Column(name = "status")
    private String status;

    public static LoanApprovalLimit fromJson(final JsonCommand command) {
        final String branchName = command.stringValueOfParameterNamed("branchName");
        final BigDecimal cashLimit = command.bigDecimalValueOfParameterNamed("cashLimit");
        final Integer duration = command.integerValueOfParameterNamed("duration");
        final Date requestedOn = command.dateValueOfParameterNamed("requestedOn");
        final BigDecimal requestedAmount = command.bigDecimalValueOfParameterNamed("requestedAmount");
        final BigDecimal approvedAmount = command.bigDecimalValueOfParameterNamed("approvedAmount");
        final String status = command.stringValueOfParameterNamed("status");

        return new LoanApprovalLimit(branchName, cashLimit, requestedOn, requestedAmount, approvedAmount, status);
    }

    private LoanApprovalLimit(final String branchName, final BigDecimal cashLimit, final Date requestedOn, final BigDecimal requestedAmount,
            final BigDecimal approvedAmount, final String status) {
        this.branchName = branchName;
        this.cashLimit = cashLimit;
        this.requestedOn = requestedOn;
        this.requestedAmount = requestedAmount;
        this.approvedAmount = approvedAmount;
        this.status = status;
    }

}
