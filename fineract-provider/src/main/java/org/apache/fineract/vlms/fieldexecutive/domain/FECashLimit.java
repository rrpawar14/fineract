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

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

@Entity
@Table(name = "m_fe_cashinhand")
public class FECashLimit extends AbstractPersistableCustom {

    @Column(name = "name", nullable = false, length = 100)
    private String feName; // MobileNo is stored in username column for authentication

    @Column(name = "cash_in_hand", nullable = false, length = 100)
    private Integer cashInHand;

    @Column(name = "required_on", nullable = false, length = 100)
    private Date requiredOnDate;

    @Column(name = "required_amount", nullable = false, length = 100)
    private Integer requiredAmount;

    @Column(name = "status", nullable = false, length = 100)
    private String status;

    public static FECashLimit fromJson(final JsonCommand command) {

        final String feName = command.stringValueOfParameterNamed("feName");
        final Integer cashInHand = command.integerValueOfParameterNamed("cashInHand");
        final Date requiredOnDate = command.dateValueOfParameterNamed("requiredOnDate");
        final Integer requiredAmount = command.integerValueOfParameterNamed("requiredAmount");
        final String status = command.stringValueOfParameterNamed("status");

        return new FECashLimit(feName, cashInHand, requiredOnDate, requiredAmount, status);

    }

    private FECashLimit(final String feName, final Integer cashInHand, final Date requiredOnDate, final Integer requiredAmount,
            final String status) {
        this.feName = feName; // MobileNo is stored in username column for authentication
        this.cashInHand = cashInHand;
        this.requiredOnDate = requiredOnDate;
        this.requiredAmount = requiredAmount;
        this.status = status;
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(1);

        final String statusTypeParamName = "status";
        if (command.isChangeInStringParameterNamed(statusTypeParamName, this.status)) {
            final String newValue = command.stringValueOfParameterNamed(statusTypeParamName);
            actualChanges.put(statusTypeParamName, newValue);
            this.status = StringUtils.defaultIfEmpty(newValue, null);
        }
        return actualChanges;
    }

}
