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
import javax.persistence.Table;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

@Entity
@Table(name = "m_hl_payment")
public class HLPayment extends AbstractPersistableCustom {

    @Column(name = "agtno", nullable = false, length = 100)
    private String AGTNO;

    @Column(name = "customerName", nullable = false, length = 100)
    private String customerName; // MobileNo is stored in username column for authentication

    @Column(name = "actualAmount", nullable = false, length = 100)
    private BigDecimal actualAmount;

    @Column(name = "postAmount", nullable = false, length = 100)
    private BigDecimal postAmount;

    @Column(name = "postDate", nullable = false, length = 100)
    private Date postDate;

    @Column(name = "expiryDate", nullable = false, length = 100)
    private Date expiryDate;

    @Column(name = "postType", nullable = false, length = 100)
    private String postType;

    @Column(name = "agent", nullable = false, length = 100)
    private String agent;

    @Column(name = "policyNo", nullable = false, length = 100)
    private String policyNo;

    @Column(name = "insuranceCompany", nullable = false, length = 100)
    private String insuranceCompany;

    @Column(name = "remark", nullable = false, length = 100)
    private String remark;

    public HLPayment() {}

    public static HLPayment fromJson(final JsonCommand command) {

        final String agtno = command.stringValueOfParameterNamed("agtno");
        final String customerName = command.stringValueOfParameterNamed("customerName");
        final BigDecimal actualAmount = command.bigDecimalValueOfParameterNamed("actualAmount");
        final BigDecimal postAmount = command.bigDecimalValueOfParameterNamed("postAmount");
        final Date postDate = command.dateValueOfParameterNamed("postDate");
        final Date expiryDate = command.dateValueOfParameterNamed("expiryDate");
        final String postType = command.stringValueOfParameterNamed("postType");
        final String agent = command.stringValueOfParameterNamed("agent");
        final String policyNo = command.stringValueOfParameterNamed("policyNo");
        final String insuranceCompany = command.stringValueOfParameterNamed("insuranceCompany");
        final String remark = command.stringValueOfParameterNamed("remark");

        return new HLPayment(agtno, customerName, actualAmount, postAmount, postDate, expiryDate, postType, policyNo, agent,
                insuranceCompany, remark);

    }

    private HLPayment(final String AGTNO, final String customerName, final BigDecimal actualAmount, final BigDecimal postAmount,
            final Date postDate, final Date expiryDate, final String postType, final String policyNo, final String agent,
            final String insuranceCompany, final String remark) {
        this.AGTNO = AGTNO;
        this.customerName = customerName;
        this.actualAmount = actualAmount;
        this.postAmount = postAmount;
        this.postDate = postDate;
        this.expiryDate = expiryDate;
        this.postType = postType;
        this.agent = agent;
        this.expiryDate = expiryDate;
        this.policyNo = policyNo;
        this.insuranceCompany = insuranceCompany;
        this.remark = remark;
    }

}
