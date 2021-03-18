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
package org.apache.fineract.useradministration.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

@Entity
@Table(name = "m_customers", uniqueConstraints = @UniqueConstraint(columnNames = { "username" }, name = "mobileNo_UK"))
public class Customers extends AbstractPersistableCustom {

    @Column(name = "username", nullable = false, length = 100)
    private String username; // MobileNo is stored in username column for authentication

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "enabled", nullable = false, length = 100)
    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "m_appuser_role", joinColumns = @JoinColumn(name = "customer_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    public static Customers fromJson(final JsonCommand command) {

        final String mobileNo = command.stringValueOfParameterNamed("mobileNo");
        final String password = command.stringValueOfParameterNamed("password");

        return new Customers(mobileNo, password);

    }

    public Customers(final String mobileNo, final String password) {
        this.username = mobileNo; // MobileNo is stored in username column for authentication
        this.password = password;
    }

    public void updatePassword(final String encodePassword) {
        this.password = encodePassword;
    }

    public String getMobileNo() {
        return this.username; // MobileNo is stored in username column for authentication
    }

    public String getPassword() {
        return this.password;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

}
