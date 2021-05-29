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
package org.apache.fineract.vlms.cashier.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@SuppressWarnings("unused")
public class HLPaymentData implements Serializable {

    private final long id;

    private final String agtNo;

    private final String customerName;

    private final BigDecimal actualAmount;

    private final BigDecimal postAmount;

    private final LocalDate postDate;

    private final String postType;

    private final String agent;

    private final LocalDate expiryDate;

    private final String policyNo;

    private final String insuranceCompany;

    private final String remark;

    private HLPaymentData(final long id, final String agtNo, final String customerName, final BigDecimal actualAmount,
            final BigDecimal postAmount, final LocalDate postDate, final String postType, final String agent, final LocalDate expiryDate,
            final String policyNo, final String insuranceCompany, final String remark) {
        this.id = id;
        this.agtNo = agtNo;
        this.customerName = customerName;
        this.actualAmount = actualAmount;
        this.postAmount = postAmount;
        this.postDate = postDate;
        this.postType = postType;
        this.agent = agent;
        this.expiryDate = expiryDate;
        this.policyNo = policyNo;
        this.insuranceCompany = insuranceCompany;
        this.remark = remark;
    }

    public static HLPaymentData instance(final long id, final String agtNo, final String customerName, final BigDecimal actualAmount,
            final BigDecimal postAmount, final LocalDate postDate, final String postType, final String agent, final LocalDate expiryDate,
            final String policyNo, final String insuranceCompany, final String remark) {
        return new HLPaymentData(id, agtNo, customerName, actualAmount, postAmount, postDate, postType, agent, expiryDate, policyNo,
                insuranceCompany, remark);
    }

}
