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
package org.apache.fineract.vlms.branchmodule.data;

import java.time.LocalDate;

@SuppressWarnings("unused")
public class LoanApprovalLimitData {

    private final Long id;

    private final Long approvalLimit;

    private final String branchName;

    private final LocalDate requestOnDate;

    private final Long requestLoanAmount;

    private final Long approveAmount;

    private final String status;

    public static LoanApprovalLimitData instance(final Long id, final Long approvalLimit, final String branchName,
            final LocalDate requestOnDate, final Long requestLoanAmount, final Long approveAmount, final String status) {
        return new LoanApprovalLimitData(id, approvalLimit, branchName, requestOnDate, requestLoanAmount, approveAmount, status);
    }

    public LoanApprovalLimitData(final Long id, final Long approvalLimit, final String branchName, final LocalDate requestOnDate,
            final Long requestLoanAmount, final Long approveAmount, final String status) {
        this.id = id;
        this.approvalLimit = approvalLimit;
        this.branchName = branchName;
        this.requestOnDate = requestOnDate;
        this.requestLoanAmount = requestLoanAmount;
        this.approveAmount = approveAmount;
        this.status = status;
    }

}
