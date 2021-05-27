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
@Table(name = "m_fieldexecutive")
public class FieldExecutive extends AbstractPersistableCustom {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "mobile_number", nullable = false, length = 100)
    private String mobileNumber;

    @Column(name = "age", nullable = false, length = 100)
    private Integer age;

    @Column(name = "branch", nullable = false, length = 100)
    private String branch;

    @Column(name = "cash_limit", nullable = false, length = 100)
    private String cashLimit;

    public FieldExecutive() {}

    public static FieldExecutive fromJson(final JsonCommand command) {

        final String name = command.stringValueOfParameterNamed("name");
        final String mobileNumber = command.stringValueOfParameterNamed("mobileNumber");
        final Integer age = command.integerValueOfParameterNamed("age");
        final String branch = command.stringValueOfParameterNamed("branch");
        final String cashLimit = command.stringValueOfParameterNamed("cashLimit");

        return new FieldExecutive(name, mobileNumber, age, branch, cashLimit);

    }

    private FieldExecutive(final String name, final String mobileNumber, final Integer age, final String branch, final String cashLimit) {
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.age = age;
        this.branch = branch;
        this.cashLimit = cashLimit;
    }
}
