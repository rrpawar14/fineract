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
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.address.domain.Address;

@Entity
@Table(name = "m_customer_guarantor")
public class CustomerGuarantor extends AbstractPersistableCustom {

    // @Column(name = "g", nullable = false, length = 100)
    // private BigDecimal loanEligibleAmount;

    @Column(name = "guarantor_name")
    private String guarantorName;

    @Column(name = "mobile_number")
    private Integer mobileNumber;

    @Column(name = "gender")
    private String gender;

    @Column(name = "dob")
    private Date dob;

    @Column(name = "marital_status")
    private String maritalStatus;

    @Column(name = "spouse_name")
    private String spouseName;

    @Column(name = "profession")
    private String profession;

    @OneToOne(optional = true)
    @JoinColumn(name = "communicationadd_id", nullable = true)
    private Address communicationAdd;

    @OneToOne(optional = true)
    @JoinColumn(name = "permanentadd_id", nullable = true)
    private Address permanentAdd;

    @OneToOne(optional = true)
    @JoinColumn(name = "officeadd_id", nullable = true)
    private Address officeAdd;

    /*
     * @OneToOne(optional = true)
     *
     * @JoinColumn(name = "guarantor_image_id", nullable = true) private Image guarantorImage;
     *
     * @OneToOne(optional = true)
     *
     * @JoinColumn(name = "document_image_id", nullable = true) private DocumentImages documentImage;
     */
    public static CustomerGuarantor fromJson(final JsonCommand command, final Address customerCommunicationAdd,
            final Address customerPermanentAdd, final Address customerOfficeAdd) {

        final String guarantorName = command.stringValueOfParameterNamed("guarantor_name");
        final Integer mobileNumber = command.integerValueOfParameterNamed("guarantor_mobile_number");
        final String gender = command.stringValueOfParameterNamed("guarantor_gender");
        final Date dob = command.dateValueOfParameterNamed("guarantor_dob");
        final String maritalStatus = command.stringValueOfParameterNamed("guarantor_maritalStatus");
        final String spouseName = command.stringValueOfParameterNamed("guarantor_spouseName");
        final String profession = command.stringValueOfParameterNamed("guarantor_profession");

        return new CustomerGuarantor(guarantorName, mobileNumber, gender, dob, maritalStatus, spouseName, profession,
                customerCommunicationAdd, customerPermanentAdd, customerOfficeAdd);
    }

    private CustomerGuarantor(final String guarantorName, final Integer mobileNumber, final String gender, final Date dob,
            final String maritalStatus, final String spouseName, final String profession, final Address customerCommunicationAdd,
            final Address customerPermanentAdd, final Address customerOfficeAdd) {
        this.guarantorName = guarantorName;
        this.mobileNumber = mobileNumber;
        this.gender = gender;
        this.dob = dob;
        this.maritalStatus = maritalStatus;
        this.spouseName = spouseName;
        this.profession = profession;
        this.communicationAdd = customerCommunicationAdd;
        this.permanentAdd = customerPermanentAdd;
        this.officeAdd = customerOfficeAdd;
    }

    public String getGuarantorName() {
        return this.guarantorName;
    }

    /*
     * public void setDocumentImage(final DocumentImages documentImages) { this.documentImage = documentImages; }
     *
     * public void setGuarantorImage(final Image image) { this.guarantorImage = image; }
     *
     * public DocumentImages getDocumentImage() { return this.documentImage; }
     *
     * public Image getGuarantorImage() { return this.guarantorImage; }
     */
}
