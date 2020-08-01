package org.apache.fineract.infrastructure.creditbureau.domain;

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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.springframework.stereotype.Component;

@Component
@Entity
@Table(name = "m_creditbureau_token")
public class CreditBureauToken extends AbstractPersistableCustom {

    @Column(name = "username")
    private String userName;

    @Column(name = "token")
    private String access_token;

    @Column(name = "token_type")
    private String token_type;

    @Column(name = "expires_in")
    private String expires_in;

    @Column(name = "issued")
    private String issued;

    @Column(name = "expires")
    private String expires;

    public CreditBureauToken(String userName, String access_token, String token_type, String expires_in, String issued, String expires) {
        this.userName = userName;
        this.access_token = access_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
        this.issued = issued;
        this.expires = expires;
    }

    public static CreditBureauToken fromJson(final JsonCommand command) {
        final String userName = command.stringValueOfParameterNamed("userName");
        final String access_token = command.stringValueOfParameterNamed("access_token");
        final String token_type = command.stringValueOfParameterNamed("token_type");
        final String expires_in = command.stringValueOfParameterNamed("expires_in");
        final String issued = command.stringValueOfParameterNamed(".issued");
        final String expires = command.stringValueOfParameterNamed(".expires");

        return new CreditBureauToken(userName, access_token, token_type, expires_in, issued, expires);
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        System.out.println("username save");
    }

    public String getToken() {
        return this.access_token;
    }

    public void setTokens(String tokens) {
        this.access_token = tokens;
        System.out.println("tokens save");
    }

}
