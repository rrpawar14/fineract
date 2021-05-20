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
package org.apache.fineract.vlms.fieldexecutive.data;

/**
 * Immutable data object representing loan summary information.
 */
@SuppressWarnings("unused")
public class FieldExecutiveData {

    private final Long id;

    private final String fieldExecutiveName;

    private final String mobileNo;

    private final Long age;

    private final String branch;

    private final String cashLimit;

    public static FieldExecutiveData instance(final Long id, final String fieldExecutiveName, final String mobileNo, final Long age,
            final String branch, final String cashLimit) {

        return new FieldExecutiveData(id, fieldExecutiveName, mobileNo, age, branch, cashLimit);
    }

    public FieldExecutiveData(final Long id, final String fieldExecutiveName, final String mobileNo, final Long age, final String branch,
            final String cashLimit) {
        this.id = id;
        this.fieldExecutiveName = fieldExecutiveName;
        this.mobileNo = mobileNo;
        this.age = age;
        this.branch = branch;
        this.cashLimit = cashLimit;
    }

}
