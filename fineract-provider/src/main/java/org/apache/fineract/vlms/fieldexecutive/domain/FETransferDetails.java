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
package org.apache.fineract.vlms.fieldexecutive.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

@Entity
@Table(name = "m_fe_transfer_details")
public class FETransferDetails extends AbstractPersistableCustom {

    @Column(name = "disbursal_type")
    private String disbursalType;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "account_holder_name")
    private String accountHolderName;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "branch_name")
    private String branchName;

    @Column(name = "IFSC")
    private String IFSC;

    /*
     * @OneToOne(optional = true, cascade = CascadeType.ALL)
     *
     * @JoinColumn(name = "passbook_image_id", nullable = true) private DocumentImages image;
     */
    public static FETransferDetails fromJson(final JsonCommand command) {

        final String disbursalType = command.stringValueOfParameterNamed("disbursalType");
        final String accountNumber = command.stringValueOfParameterNamed("accountNumber");
        final String accountHolderName = command.stringValueOfParameterNamed("accountHolderName");
        final String bankName = command.stringValueOfParameterNamed("bankName");
        final String branchName = command.stringValueOfParameterNamed("branchName");
        final String IFSC = command.stringValueOfParameterNamed("IFSC");

        return new FETransferDetails(disbursalType, accountNumber, accountHolderName, bankName, branchName, IFSC);

    }

    public FETransferDetails() {}

    private FETransferDetails(final String disbursalType, final String accountNumber, final String accountHolderName, final String bankName,
            final String branchName, final String IFSC) {
        this.disbursalType = disbursalType;
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.bankName = bankName;
        this.branchName = branchName;
        this.IFSC = IFSC;
    }

}
