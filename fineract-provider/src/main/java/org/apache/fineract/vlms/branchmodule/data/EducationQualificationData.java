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

/**
 * Immutable data object representing loan summary information.
 */

@SuppressWarnings("unused")
public class EducationQualificationData {

    private String university;

    private String qualification;

    private String percentage;

    private String passingyear;

    public static EducationQualificationData instance(final String university, final String qualification, final String percentage,
            final String passingyear) {

        return new EducationQualificationData(university, qualification, percentage, passingyear);
    }

    public EducationQualificationData(final String university, final String qualification, final String percentage,
            final String passingyear) {
        this.university = university;
        this.qualification = qualification;
        this.percentage = percentage;
        this.passingyear = passingyear;
    }

}
