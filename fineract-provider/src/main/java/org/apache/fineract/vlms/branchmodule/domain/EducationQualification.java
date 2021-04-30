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
package org.apache.fineract.vlms.branchmodule.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

@Entity
@Table(name = "m_education_qualification")
public class EducationQualification extends AbstractPersistableCustom {

    @Column(name = "school_university")
    private String university;

    @Column(name = "qualification")
    private String qualification;

    @Column(name = "percentage")
    private String percentage;

    @Column(name = "passingyear")
    private String passingyear;

    public static EducationQualification fromJson(final JsonCommand command) {

        final String university = command.stringValueOfParameterNamed("university");
        final String qualification = command.stringValueOfParameterNamed("qualification");
        final String percentage = command.stringValueOfParameterNamed("percentage");
        final String passingyear = command.stringValueOfParameterNamed("passingyear");

        return new EducationQualification(university, qualification, percentage, passingyear);

    }

    private EducationQualification(final String university, final String qualification, final String percentage, final String passingyear) {
        this.university = university;
        this.qualification = qualification;
        this.percentage = percentage;
        this.passingyear = passingyear;
    }

}
