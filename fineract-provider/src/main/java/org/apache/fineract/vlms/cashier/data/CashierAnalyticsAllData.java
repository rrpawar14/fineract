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

/**
 * Immutable data object representing loan summary information.
 */

@SuppressWarnings("unused")
public class CashierAnalyticsAllData {

    final Long newloan;

    final Long closedloan;

    final Long bankCashCollection;

    final Long taskDetailsBMOpen;

    final Long taskDetailsBMClosed;

    final Long taskDetailsBMInProgress;

    final Long taskDetailsHOOpen;

    final Long taskDetailsHOClosed;

    final Long taskDetailsHOInprogress;

    public static CashierAnalyticsAllData instance(final Long newloan, final Long closedloan, final Long bankCashCollection,
            final Long taskDetailsBMOpen, final Long taskDetailsBMClosed, final Long taskDetailsBMInProgress, final Long taskDetailsHOOpen,
            final Long taskDetailsHOClosed, final Long taskDetailsHOInprogress) {
        return new CashierAnalyticsAllData(newloan, closedloan, bankCashCollection, taskDetailsBMOpen, taskDetailsBMClosed,
                taskDetailsBMInProgress, taskDetailsHOOpen, taskDetailsHOClosed, taskDetailsHOInprogress);
    }

    public CashierAnalyticsAllData(final Long newloan, final Long closedloan, final Long bankCashCollection, final Long taskDetailsBMOpen,
            final Long taskDetailsBMClosed, final Long taskDetailsBMInProgress, final Long taskDetailsHOOpen,
            final Long taskDetailsHOClosed, final Long taskDetailsHOInprogress) {
        this.newloan = newloan;
        this.closedloan = closedloan;
        this.bankCashCollection = bankCashCollection;
        this.taskDetailsBMOpen = taskDetailsBMOpen;
        this.taskDetailsBMClosed = taskDetailsBMClosed;
        this.taskDetailsBMInProgress = taskDetailsBMInProgress;
        this.taskDetailsHOOpen = taskDetailsHOOpen;
        this.taskDetailsHOClosed = taskDetailsHOClosed;
        this.taskDetailsHOInprogress = taskDetailsHOInprogress;
    }

}
