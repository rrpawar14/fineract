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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

@Entity
@Table(name = "m_voucher_data")
public class VoucherDetails extends AbstractPersistableCustom {

    @ManyToOne
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @Column(name = "credit", length = 100)
    private BigDecimal credit;

    @Column(name = "debit", length = 100)
    private BigDecimal debit;

    @Column(name = "particulars", length = 100)
    private String particulars;

    protected VoucherDetails() {

    }

    public VoucherDetails(final Voucher voucher, final BigDecimal credit, final BigDecimal debit, final String particulars) {
        this.voucher = voucher;
        this.credit = credit;
        this.debit = debit;
        this.particulars = particulars;
    }

}
