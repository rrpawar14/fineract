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
import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.infrastructure.core.service.DateUtils;

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

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(1);

        final String loanAmountParamName = "loanAmount";
        if (command.isChangeInIntegerParameterNamed(loanAmountParamName, this.loanAmount)) {
            final Integer newValue = command.integerValueOfParameterNamed(loanAmountParamName);
            actualChanges.put(loanAmountParamName, newValue);
            this.loanAmount = newValue;// StringUtils.defaultIfEmpty(newValue, null);
        }

        final String loanTermParamName = "loanTerm";
        if (command.isChangeInIntegerParameterNamed(loanTermParamName, this.loanTerm)) {
            final Integer newValue = command.integerValueOfParameterNamed(loanTermParamName);
            actualChanges.put(loanTermParamName, newValue);
            this.loanTerm = newValue;// StringUtils.defaultIfEmpty(newValue, null);
        }

        final String loanInterestParamName = "loanInterest";
        if (command.isChangeInBigDecimalParameterNamed(loanInterestParamName, this.loanInterest)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(loanInterestParamName);
            actualChanges.put(loanInterestParamName, newValue);
            this.loanInterest = newValue;// StringUtils.defaultIfEmpty(newValue, null);
        }

        final String emiParamName = "emi";
        if (command.isChangeInIntegerParameterNamed(emiParamName, this.emi)) {
            final Integer newValue = command.integerValueOfParameterNamed(emiParamName);
            actualChanges.put(emiParamName, newValue);
            this.emi = newValue;// StringUtils.defaultIfEmpty(newValue, null);
        }

        final String interestINRParamName = "interestINR";
        if (command.isChangeInIntegerParameterNamed(interestINRParamName, this.interestINR)) {
            final Integer newValue = command.integerValueOfParameterNamed(interestINRParamName);
            actualChanges.put(interestINRParamName, newValue);
            this.interestINR = newValue;// StringUtils.defaultIfEmpty(newValue, null);
        }

        /*
         * final String mobileNoParamName = "mobileNo"; if (command.isChangeInStringParameterNamed(mobileNoParamName,
         * this.mobileNo)) { final String newValue = command.stringValueOfParameterNamed(mobileNoParamName);
         * actualChanges.put(mobileNoParamName, newValue); this.mobileNo = StringUtils.defaultIfEmpty(newValue, null); }
         */
        final String dateFormatAsInput = command.dateFormat();
        final String localeAsInput = command.locale();

        final String dueDateParamName = "dueDate";
        final String localeParamName = "locale";
        final String dateFormatParamName = "dateFormat";
        if (command.isChangeInDateParameterNamed(dueDateParamName, this.dueDate)) {
            final String valueAsInput = command.stringValueOfParameterNamed(dueDateParamName);
            actualChanges.put(dueDateParamName, valueAsInput);
            actualChanges.put(dateFormatParamName, dateFormatAsInput);
            actualChanges.put(localeParamName, localeAsInput);

            final LocalDate newValue = command.localDateValueOfParameterNamed(dueDateParamName);
            this.dueDate = Date.from(newValue.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
        }
        return actualChanges;
    }

}
