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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

@Entity
@Table(name = "m_insurancedetails")
public class InsuranceDetails extends AbstractPersistableCustom {

    @Column(name = "policynumber")
    private String policyNumber;

    @Column(name = "companycoverage")
    private String companyCoverage;

    @Column(name = "policycoverage")
    private String policyCoverage;

    public static InsuranceDetails fromJson(final JsonCommand command, final String paramName) {

        JsonObject addressObject = command.parsedJson().getAsJsonObject();
        JsonElement guarantorObject = addressObject.get(paramName);
        System.out.println("InsuranceDetails: " + guarantorObject);

        if (!(guarantorObject instanceof JsonNull)) { // NOTE : "element instanceof JsonNull" is for handling empty
                                                      // values (and
            // assigning null) while fetching data from results
            addressObject = (JsonObject) guarantorObject;
        }

        JsonObject borrowerInfos = null;
        String borrowerInfo = null;

        Gson gson = new Gson();

        guarantorObject = addressObject.get("policyNumber");
        System.out.println("addressLine1: " + guarantorObject);
        final String policyNumber = gson.toJson(guarantorObject);

        guarantorObject = addressObject.get("companyCoverage");
        System.out.println("addressLine2: " + guarantorObject);
        final String companyCoverage = gson.toJson(guarantorObject);

        guarantorObject = addressObject.get("policyCoverage");
        System.out.println("addressLine2: " + guarantorObject);
        final String policyCoverage = gson.toJson(guarantorObject);

        /*
         * final String policyNumber = command.stringValueOfParameterNamed("policyNumber"); final String companyCoverage
         * = command.stringValueOfParameterNamed("companyCoverage"); final String policyCoverage =
         * command.stringValueOfParameterNamed("policyCoverage");
         */

        return new InsuranceDetails(policyNumber, companyCoverage, policyCoverage);

    }

    private InsuranceDetails(final String policyNumber, final String companyCoverage, final String policyCoverage) {
        this.policyNumber = policyNumber;
        this.companyCoverage = companyCoverage;
        this.policyCoverage = policyCoverage;

    }

}
