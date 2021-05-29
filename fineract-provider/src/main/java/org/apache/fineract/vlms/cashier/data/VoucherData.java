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
public class VoucherData implements Serializable {

    private final long id;

    private final LocalDate createdDate;

    private final String particulars;

    private final String voucherType;

    private final String voucherNumber;

    private final BigDecimal credit;

    private final BigDecimal debit;

    private final String remarks;

    public static VoucherData instance(final Long id, final LocalDate createdDate, final String particulars, final String voucherType,
            final String voucherNumber, final BigDecimal credit, final BigDecimal debit, final String remarks) {
        return new VoucherData(id, createdDate, particulars, voucherType, voucherNumber, credit, debit, remarks);

    }

    private VoucherData(final Long id, final LocalDate createdDate, final String particulars, final String voucherType,
            final String voucherNumber, final BigDecimal credit, final BigDecimal debit, final String remarks) {
        this.id = id;
        this.createdDate = createdDate;
        this.particulars = particulars;
        this.voucherType = voucherType;
        this.voucherNumber = voucherNumber;
        this.credit = credit;
        this.debit = debit;
        this.remarks = remarks;
    }

}
