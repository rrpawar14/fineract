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
package org.apache.fineract.portfolio.loanaccount.domain;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.infrastructure.documentmanagement.domain.Image;

@Entity
@Table(name = "m_customer_details")
public class CustomerDetails extends AbstractPersistableCustom {

    @Column(name = "name")
    private String name;

    @Column(name = "gender")
    private Integer gender;

    @Column(name = "dob")
    private Date dob;

    @Column(name = "maritalStatus")
    private String maritalStatus;

    @Column(name = "spousename")
    private String spousename;

    @Column(name = "profession")
    private String profession;

    @Column(name = "proof_image_id")
    private Image proof;

    public static CustomerDetails fromJson(final JsonCommand command) {

        final String name = command.stringValueOfParameterNamed("name");
        final Integer gender = command.integerValueOfParameterNamed("gender");
        final Date dob = command.dateValueOfParameterNamed("dob");
        final String maritalStatus = command.stringValueOfParameterNamed("maritalStatus");
        final String spousename = command.stringValueOfParameterNamed("spousename");
        final String profession = command.stringValueOfParameterNamed("profession");

        return new CustomerDetails(name, gender, dob, maritalStatus, spousename, profession);

    }

    private CustomerDetails(final String name, final Integer gender, final Date dob, final String maritalStatus, final String spousename,
            final String profession) {
        this.name = name;
        this.gender = gender;
        this.dob = dob;
        this.maritalStatus = maritalStatus;
        this.spousename = spousename;
        this.profession = profession;
    }

    public void setImage(Image image) {
        this.proof = image;
    }

    public Image getImage() {
        return this.proof;
    }

}
