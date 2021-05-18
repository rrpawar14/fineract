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
package org.apache.fineract.vlms.fieldexecutive.data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Immutable data object representing a code.
 */

public class LoanChangeRequestData implements Serializable {

    private final Long id;

    @SuppressWarnings("unused")
    private final Long loanDetailsId;

    @SuppressWarnings("unused")
    private final BigDecimal changeLoanInterest;

    @SuppressWarnings("unused")
    private final BigDecimal changeLoanAmount;

    @SuppressWarnings("unused")
    private final BigDecimal changeDocCharges;

    @SuppressWarnings("unused")
    private final BigDecimal changeProcessingCharges;

    @SuppressWarnings("unused")
    private final BigDecimal loanClosureAmount;

    public static LoanChangeRequestData instance(final Long id, final Long loanDetailsId, final BigDecimal changeLoanInterest,
            final BigDecimal changeLoanAmount, final BigDecimal changeDocCharges, final BigDecimal changeProcessingCharges,
            final BigDecimal loanClosureAmount) {
        return new LoanChangeRequestData(id, loanDetailsId, changeLoanInterest, changeLoanAmount, changeDocCharges, changeProcessingCharges,
                loanClosureAmount);
    }

    public LoanChangeRequestData(final Long id, final Long loanDetailsId, final BigDecimal changeLoanInterest,
            final BigDecimal changeLoanAmount, final BigDecimal changeDocCharges, final BigDecimal changeProcessingCharges,
            final BigDecimal loanClosureAmount) {
        this.id = id;
        this.loanDetailsId = loanDetailsId;
        this.changeLoanInterest = changeLoanInterest;
        this.changeLoanAmount = changeLoanAmount;
        this.changeDocCharges = changeDocCharges;
        this.changeProcessingCharges = changeProcessingCharges;
        this.loanClosureAmount = loanClosureAmount;
    }

}
