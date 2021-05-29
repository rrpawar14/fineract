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
@Table(name = "m_voucher")
public class Voucher extends AbstractPersistableCustom {

    @Column(name = "created_date", nullable = false, length = 100)
    private Date createdDate;

    @Column(name = "particulars", nullable = false, length = 100)
    private String particulars;

    @Column(name = "voucher_type", nullable = false, length = 100)
    private String voucherType;

    @Column(name = "voucher_number", nullable = false, length = 100)
    private String voucherNumber;

    @Column(name = "credit", nullable = false, length = 100)
    private BigDecimal credit;

    @Column(name = "debit", nullable = false, length = 100)
    private BigDecimal debit;

    @Column(name = "remarks", nullable = false, length = 100)
    private String remarks;

    public static Voucher fromJson(final JsonCommand command) {

        final Date createdDate = command.dateValueOfParameterNamed("createdDate");
        final String particulars = command.stringValueOfParameterNamed("particulars");
        final String voucherType = command.stringValueOfParameterNamed("voucherType");
        final String voucherNumber = command.stringValueOfParameterNamed("voucherNumber");
        final BigDecimal credit = command.bigDecimalValueOfParameterNamed("credit");
        final BigDecimal debit = command.bigDecimalValueOfParameterNamed("debit");
        final String remarks = command.stringValueOfParameterNamed("remarks");

        return new Voucher(createdDate, particulars, voucherType, voucherNumber, credit, debit, remarks);

    }

    private Voucher(final Date createdDate, final String particulars, final String voucherType, final String voucherNumber,
            final BigDecimal credit, final BigDecimal debit, final String remarks) {
        this.createdDate = createdDate;
        this.particulars = particulars;
        this.voucherType = voucherType;
        this.voucherNumber = voucherNumber;
        this.credit = credit;
        this.debit = debit;
        this.remarks = remarks;
    }

}
