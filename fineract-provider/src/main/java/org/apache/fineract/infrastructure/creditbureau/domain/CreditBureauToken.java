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

package org.apache.fineract.infrastructure.creditbureau.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

@Entity
@Table(name = "m_creditbureau_token")
public class CreditBureauToken extends AbstractPersistableCustom {

    // private final long creditBureauId;

    // private final String creditBureauName;

    // private final String country;

    @Column(name = "token_id")
    private String tokenId;

    @Column(name = "userName")
    private String userName;

    @Column(name = "password")
    private String password;

    @Column(name = "subscription_id")
    private String subscriptionId;

    @Column(name = "subscription_key")
    private String subscriptionKey;

    public CreditBureauToken(String subscriptionKey, String subscriptionId, String userName) {
        this.subscriptionKey = subscriptionKey;
        this.subscriptionId = subscriptionId;
        this.userName = userName;

    }

    public static CreditBureauToken fromJson(final JsonCommand command) {
        final String subscriptionKey = command.stringValueOfParameterNamed("subscriptionKey");
        final String subscriptionId = command.stringValueOfParameterNamed("subscriptionId");
        final String userName = command.stringValueOfParameterNamed("userName");

        return new CreditBureauToken(subscriptionKey, subscriptionId, userName);
    }

    public String getSubscriptionId() {
        return this.subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getSubscriptionKey() {
        return this.subscriptionKey;
    }

    public void setSubscriptionKey(String subscriptionKey) {
        this.subscriptionKey = subscriptionKey;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
