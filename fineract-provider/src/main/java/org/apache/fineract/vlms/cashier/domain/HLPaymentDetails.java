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
package org.apache.fineract.vlms.cashier.domain;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

@Entity
@Table(name = "m_hl_payment_data")
public class HLPaymentDetails extends AbstractPersistableCustom {

    @ManyToOne
    @JoinColumn(name = "hl_payment_id")
    private HLPayment hlPayment;

    @Column(name = "agtno", length = 100)
    private String agtno;

    @Column(name = "customer_name", length = 100)
    private String customerName; // MobileNo is stored in username column for authentication

    @Column(name = "actual_amount", length = 100)
    private BigDecimal actualAmount;

    @Column(name = "post_amount", length = 100)
    private BigDecimal postAmount;

    @Column(name = "expiry_date", length = 100)
    private Date expiryDate;

    @Column(name = "policy_no", length = 100)
    private String policyNo;

    @Column(name = "insurance_company", length = 100)
    private String insuranceCompany;

    @Column(name = "remark", length = 100)
    private String remark;

    protected HLPaymentDetails() {}

    /*
     * public static HLPaymentDetails fromJson(final HLPayment hlPayment, final JsonCommand command) {
     *
     * final String agtno = command.stringValueOfParameterNamVoucherDetailsed("agtno"); final String customerName =
     * command.stringValueOfParameterNamed("customerName"); final BigDecimal actualAmount =
     * command.bigDecimalValueOfParameterNamed("actualAmount"); final BigDecimal postAmount =
     * command.bigDecimalValueOfParameterNamed("postAmount"); final Date expiryDate =
     * command.dateValueOfParameterNamed("expiryDate"); final String policyNo =
     * command.stringValueOfParameterNamed("policyNo"); final String insuranceCompany =
     * command.stringValueOfParameterNamed("insuranceCompany"); final String remark =
     * command.stringValueOfParameterNamed("remark");
     *
     * return new HLPaymentDetails(hlPayment, agtno, customerName, actualAmount, postAmount, expiryDate, policyNo,
     * insuranceCompany, remark);
     *
     * }
     */

    public HLPaymentDetails(final HLPayment hlPayment, final String agtno, final String customerName, final BigDecimal actualAmount,
            final BigDecimal postAmount, final Date expiryDate, final String policyNo, final String insuranceCompany, final String remark) {
        this.hlPayment = hlPayment;
        this.agtno = agtno;
        this.customerName = customerName;
        this.actualAmount = actualAmount;
        this.postAmount = postAmount;
        this.expiryDate = expiryDate;
        this.policyNo = policyNo;
        this.insuranceCompany = insuranceCompany;
        this.remark = remark;
    }

}
