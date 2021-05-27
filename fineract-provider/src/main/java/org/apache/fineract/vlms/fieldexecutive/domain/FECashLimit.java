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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

@Entity
@Table(name = "m_fe_cashinhand")
public class FECashLimit extends AbstractPersistableCustom {

    @Column(name = "name", nullable = false, length = 100)
    private String feName; // MobileNo is stored in username column for authentication

    @ManyToOne
    @JoinColumn(name = "fe_id", nullable = true)
    private FieldExecutive fieldExecutive;

    @Column(name = "cash_in_hand", nullable = false, length = 100)
    private BigDecimal cashInHand;

    @Column(name = "cash_limit", nullable = false, length = 100)
    private BigDecimal cashLimit;

    @Column(name = "required_on", nullable = false, length = 100)
    private Date requiredOnDate;

    @Column(name = "required_amount", nullable = false, length = 100)
    private BigDecimal requiredAmount;

    @Column(name = "status", nullable = false, length = 100)
    private String status;

    public static FECashLimit fromJson(final FieldExecutive fieldExecutive, final JsonCommand command) {

        final String feName = command.stringValueOfParameterNamed("feName");
        final BigDecimal cashInHand = command.bigDecimalValueOfParameterNamed("cashInHand");
        final BigDecimal cashLimit = command.bigDecimalValueOfParameterNamed("cashLimit");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date createdDate = new Date();
        final BigDecimal requiredAmount = command.bigDecimalValueOfParameterNamed("requiredAmount");
        final String status = command.stringValueOfParameterNamed("status");

        return new FECashLimit(feName, cashInHand, cashLimit, createdDate, requiredAmount, status, fieldExecutive);

    }

    public FECashLimit() {}

    private FECashLimit(final String feName, final BigDecimal cashInHand, final BigDecimal cashLimit, final Date requiredOnDate,
            final BigDecimal requiredAmount, final String status, final FieldExecutive fieldExecutive) {
        this.feName = feName; // MobileNo is stored in username column for authentication
        this.cashInHand = cashInHand;
        this.cashLimit = cashLimit;
        this.requiredOnDate = requiredOnDate;
        this.requiredAmount = requiredAmount;
        this.status = status;
        this.fieldExecutive = fieldExecutive;
    }

    /*
     * public Money getPrincpal() { return this.loanRepaymentScheduleDetail.getPrincipal(); }
     */

    public Map<String, Object> update(final FECashLimit feCashLimit, final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(1);
        final String status = command.stringValueOfParameterNamed("status");
        // final FECashLimit feCashLimit = retrieveCashLimitRequestBy(requestId);
        if (status.equals("approved")) {
            BigDecimal cashRequired = feCashLimit.getRequiredAmount();
            BigDecimal cashLimit = feCashLimit.getCashLimit();

            // BigDecimal newCash = cashRequired + cashLimit;
            // Money cashLimitMoney = Money.of(this.currency, cashLimit);
            // Money cashRequiredMoney = Money.of(this.currency, cashRequired);

            BigDecimal newCash = cashRequired.add(cashLimit);

            final String cashLimitParamName = "cashLimit";
            this.cashLimit = newCash;

            actualChanges.put(cashLimitParamName, newCash);
        }

        final String statusTypeParamName = "status";
        if (command.isChangeInStringParameterNamed(statusTypeParamName, this.status)) {
            final String newValue = command.stringValueOfParameterNamed(statusTypeParamName);
            actualChanges.put(statusTypeParamName, newValue);
            this.status = StringUtils.defaultIfEmpty(newValue, null);
        }
        return actualChanges;
    }

    public BigDecimal getRequiredAmount() {
        return this.requiredAmount;
    }

    public BigDecimal getCashLimit() {
        return this.cashLimit;
    }

    /*
     * public Money getCashLimit() { return Money.of(this.currency, this.cashLimit); }
     *
     * public Money getCashRequired() { return Money.of(this.currency, this.requiredAmount); }
     */
}
