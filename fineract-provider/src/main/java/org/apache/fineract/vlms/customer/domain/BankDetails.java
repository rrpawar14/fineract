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
package org.apache.fineract.vlms.customer.domain;

import java.math.BigDecimal;
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
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentType;
import org.apache.fineract.vlms.branchmodule.domain.Employee;

@Entity
@Table(name = "m_customer_bank_details")
public class BankDetails extends AbstractPersistableCustom {

    @Column(name = "loan_eligible_amount")
    private BigDecimal loanEligibleAmount;

    @Column(name = "to_pay")
    private BigDecimal toPay;

    @Column(name = "transfer_type")
    private String transferType;

    @Column(name = "transfer_mode")
    private String transferMode;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "account_holder_name")
    private String accountHolderName;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "branch_name")
    private String branchName;

    @Column(name = "IFSC")
    private String IFSC;

    @ManyToOne(optional = true)
    @JoinColumn(name = "payment_type_id")
    private PaymentType paymentTypeId; // 1st party or 3rd party apps

    @ManyToOne(optional = true)
    @JoinColumn(name = "loan_id")
    private Loan loan;

    @ManyToOne(optional = true)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    /*
     * @OneToOne(optional = true, cascade = CascadeType.ALL)
     *
     * @JoinColumn(name = "passbook_image_id", nullable = true) private DocumentImages image;
     */
    public static BankDetails fromJson(final Loan loan, final Employee employee, final PaymentType paymentType, final JsonCommand command) {

        final BigDecimal loanEligibleAmount = command.bigDecimalValueOfParameterNamed("loanEligibleAmount");
        final BigDecimal toPay = command.bigDecimalValueOfParameterNamed("toPay");
        final String transferType = command.stringValueOfParameterNamed("transferType");
        final String transferMode = command.stringValueOfParameterNamed("transferMode");
        final String accountNumber = command.stringValueOfParameterNamed("accountNumber");
        final String accountHolderName = command.stringValueOfParameterNamed("accountHolderName");
        final String bankName = command.stringValueOfParameterNamed("bankName");
        final String branchName = command.stringValueOfParameterNamed("branchName");
        final String IFSC = command.stringValueOfParameterNamed("IFSC");

        return new BankDetails(loan, employee, paymentType, loanEligibleAmount, toPay, transferType, transferMode, accountNumber,
                accountHolderName, bankName, branchName, IFSC);

    }

    public BankDetails() {}

    private BankDetails(final Loan loan, final Employee employee, final PaymentType paymentType, final BigDecimal loanEligibleAmount,
            final BigDecimal toPay, final String accountType, final String disbursalType, final String accountNumber,
            final String accountHolderName, final String bankName, final String branchName, final String IFSC) {
        this.loan = loan;
        this.employee = employee;
        this.paymentTypeId = paymentType;
        this.loanEligibleAmount = loanEligibleAmount;
        this.toPay = toPay;
        this.transferType = accountType;
        this.transferMode = disbursalType;
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.bankName = bankName;
        this.branchName = branchName;
        this.IFSC = IFSC;
    }

    public String getAccountHolderName() {
        return this.accountHolderName;
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(1);

        final String loanEligibleAmountParamName = "loanEligibleAmount";
        if (command.isChangeInBigDecimalParameterNamed(loanEligibleAmountParamName, this.loanEligibleAmount)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(loanEligibleAmountParamName);
            actualChanges.put(loanEligibleAmountParamName, newValue);
            this.loanEligibleAmount = newValue;// StringUtils.defaultIfEmpty(newValue, null);
        }

        final String toPayParamName = "toPay";
        if (command.isChangeInBigDecimalParameterNamed(toPayParamName, this.toPay)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(toPayParamName);
            actualChanges.put(toPayParamName, newValue);
            this.toPay = newValue;
        }

        final String transferTypeParamName = "transferType";
        if (command.isChangeInStringParameterNamed(transferTypeParamName, this.transferType)) {
            final String newValue = command.stringValueOfParameterNamed(transferTypeParamName);
            actualChanges.put(transferTypeParamName, newValue);
            this.transferType = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String transferModeParamName = "transferMode";
        if (command.isChangeInStringParameterNamed(transferModeParamName, this.transferMode)) {
            final String newValue = command.stringValueOfParameterNamed(transferModeParamName);
            actualChanges.put(transferModeParamName, newValue);
            this.transferMode = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String accountNumberParamName = "accountNumber";
        if (command.isChangeInStringParameterNamed(accountNumberParamName, this.accountNumber)) {
            final String newValue = command.stringValueOfParameterNamed(accountNumberParamName);
            actualChanges.put(accountNumberParamName, newValue);
            this.accountNumber = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String accountHolderNameParamName = "accountHolderName";
        if (command.isChangeInStringParameterNamed(accountHolderNameParamName, this.accountHolderName)) {
            final String newValue = command.stringValueOfParameterNamed(accountHolderNameParamName);
            actualChanges.put(accountHolderNameParamName, newValue);
            this.accountHolderName = StringUtils.defaultIfEmpty(newValue, null);
        }

        /*
         * final String mobileNoParamName = "mobileNo"; if (command.isChangeInStringParameterNamed(mobileNoParamName,
         * this.mobileNo)) { final String newValue = command.stringValueOfParameterNamed(mobileNoParamName);
         * actualChanges.put(mobileNoParamName, newValue); this.mobileNo = StringUtils.defaultIfEmpty(newValue, null); }
         */
        final String bankNameParamName = "bankName";
        if (command.isChangeInStringParameterNamed(bankNameParamName, this.bankName)) {
            final String newValue = command.stringValueOfParameterNamed(bankNameParamName);
            actualChanges.put(bankNameParamName, newValue);
            this.bankName = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String branchNameParamName = "branchName";
        if (command.isChangeInStringParameterNamed(branchNameParamName, this.branchName)) {
            final String newValue = command.stringValueOfParameterNamed(branchNameParamName);
            actualChanges.put(branchNameParamName, newValue);
            this.branchName = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String IFSCParamName = "IFSC";
        if (command.isChangeInStringParameterNamed(IFSCParamName, this.IFSC)) {
            final String newValue = command.stringValueOfParameterNamed(IFSCParamName);
            actualChanges.put(IFSCParamName, newValue);
            this.IFSC = StringUtils.defaultIfEmpty(newValue, null);
        }
        return actualChanges;
    }

    /*
     * public void setImage(DocumentImages image) { this.image = image; }
     *
     * public DocumentImages getImage() { return this.image; }
     */
}
