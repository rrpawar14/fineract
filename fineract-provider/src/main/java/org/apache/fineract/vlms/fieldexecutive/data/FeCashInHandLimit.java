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

import java.time.LocalDate;

/**
 * Immutable data object representing loan summary information.
 */
@SuppressWarnings("unused")
public class FeCashInHandLimit {

    private final Long id;

    private final String fieldExecutiveName;

    private final Long cashInHand;

    private final LocalDate requiredOn;

    private final Long requiredAmount;

    private final String status;

    public static FeCashInHandLimit instance(final Long id, final String fieldExecutiveName, final Long cashInHand,
            final LocalDate requiredOn, final Long requiredAmount, final String status) {

        return new FeCashInHandLimit(id, fieldExecutiveName, cashInHand, requiredOn, requiredAmount, status);
    }

    public FeCashInHandLimit(final Long id, final String fieldExecutiveName, final Long cashInHand, final LocalDate requiredOn,
            final Long requiredAmount, final String status) {
        this.id = id;
        this.fieldExecutiveName = fieldExecutiveName;
        this.cashInHand = cashInHand;
        this.requiredOn = requiredOn;
        this.requiredAmount = requiredAmount;
        this.status = status;
    }

}
