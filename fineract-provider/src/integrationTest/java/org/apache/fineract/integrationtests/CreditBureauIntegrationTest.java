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
package org.apache.fineract.integrationtests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.creditbureau.service.CreditReportWritePlatformServiceImpl;
import org.apache.fineract.integrationtests.common.CreditBureauHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreditBureauIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(CreditBureauIntegrationTest.class);
    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
    private CommandProcessingResult commandProcessingResult;

    // CreditReportWritePlatformServiceImpl creditBureauWritePlatformServiceImpl =
    // Mockito.mock(CreditReportWritePlatformServiceImpl.class);

    // credentials
    /*
     * import org.junit.Test; public void setup() { Utils.initializeRESTAssured(); this.requestSpec = new
     * RequestSpecBuilder().setContentType(ContentType.JSON).build(); this.requestSpec.header("Authorization", "Basic "
     * + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey()); this.responseSpec = new
     * ResponseSpecBuilder().expectStatusCode(200).build(); }
     */

    static String process = "process";
    static String nrcID = "13/MiFoS(N)163525";

    static String userName = "demomfi1@mifos.com";
    static String password = "password";
    static String subscriptionKey = "15c15ff17493acb44cb223f2feab2fe4";

    static String subscriptionId = "4A7C-317A41BA-1FF8-8A64-F8EDBE0F625D";
    static String url = "url";
    static String token = "token";
    static Long uniqueID = 1L;


    CreditReportWritePlatformServiceImpl creditReportWritePlatformServiceImpl = Mockito.mock(CreditReportWritePlatformServiceImpl.class);


    @Test
    public void getCreditreport() {
        
         String curentNrc = "13/MiFoS(N)163525";

         final String creditReport = CreditBureauHelper.getCreditReport();
         LOG.info("creditReport : {} ", creditReport);
        
         JsonObject resultObject = JsonParser.parseString(creditReport).getAsJsonObject(); 
         String data = resultObject.get("Data").toString();
         
          JsonObject dataObject = JsonParser.parseString(data).getAsJsonObject(); 
          String borrowerInfo = dataObject.get("BorrowerInfo").toString();
         
          JsonObject borrowerObject = JsonParser.parseString(borrowerInfo).getAsJsonObject(); 
          String nrc = borrowerObject.get("NRC").toString(); 
        
          nrc = nrc.substring(1, nrc.length() - 1);
          LOG.info("nrc : {} ", nrc);
          assertEquals(nrc, curentNrc);

    }

}
