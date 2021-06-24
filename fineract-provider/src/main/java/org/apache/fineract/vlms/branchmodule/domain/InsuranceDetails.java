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
import java.util.LinkedHashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.commons.lang3.StringUtils;
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

    public InsuranceDetails() {}

    public static InsuranceDetails fromJson(final JsonCommand command, final String paramName) {

        JsonObject addressObject = command.parsedJson().getAsJsonObject();
        JsonElement guarantorObject = addressObject.get(paramName);

        if (!(guarantorObject instanceof JsonNull)) { // NOTE : "element instanceof JsonNull" is for handling empty
                                                      // values (and
            // assigning null) while fetching data from results
            addressObject = (JsonObject) guarantorObject;
        }

        JsonObject borrowerInfos = null;
        String borrowerInfo = null;

        Gson gson = new Gson();

        guarantorObject = addressObject.get("policyNumber");
        String policyNumber = gson.toJson(guarantorObject);
        policyNumber = policyNumber.replaceAll("[^a-zA-Z0-9]", "");

        guarantorObject = addressObject.get("companyCoverage");
        String companyCoverage = gson.toJson(guarantorObject);
        companyCoverage = companyCoverage.replaceAll("[^a-zA-Z0-9]", "");

        guarantorObject = addressObject.get("policyCoverage");
        String policyCoverage = gson.toJson(guarantorObject);
        policyCoverage = policyCoverage.replaceAll("[^a-zA-Z0-9]", "");

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

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(1);

        final String policyNumberParamName = "policyNumber";
        if (command.isChangeInStringParameterNamed(policyNumberParamName, this.policyNumber)) {
            final String newValue = command.stringValueOfParameterNamed(policyNumberParamName);
            actualChanges.put(policyNumberParamName, newValue);
            this.policyNumber = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String companyCoverageParamName = "companyCoverage";
        if (command.isChangeInStringParameterNamed(companyCoverageParamName, this.companyCoverage)) {
            final String newValue = command.stringValueOfParameterNamed(companyCoverageParamName);
            actualChanges.put(companyCoverageParamName, newValue);
            this.companyCoverage = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String policyCoverageParamName = "policyCoverage";
        if (command.isChangeInStringParameterNamed(policyCoverageParamName, this.policyCoverage)) {
            final String newValue = command.stringValueOfParameterNamed(policyCoverageParamName);
            actualChanges.put(policyCoverageParamName, newValue);
            this.policyCoverage = StringUtils.defaultIfEmpty(newValue, null);
        }

        return actualChanges;
    }

}
