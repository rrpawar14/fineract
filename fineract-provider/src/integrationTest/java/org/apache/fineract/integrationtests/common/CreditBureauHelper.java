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
package org.apache.fineract.integrationtests.common;


import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import static org.mockito.Mockito.when;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.creditbureau.data.CreditReportData;
import org.apache.fineract.infrastructure.creditbureau.service.CreditReportWritePlatformServiceImpl;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreditBureauHelper {

    private static final Logger LOG = LoggerFactory.getLogger(CreditBureauHelper.class);
  
    private static final CreditReportWritePlatformServiceImpl creditBureauWritePlatformServiceImpl = Mockito.mock(CreditReportWritePlatformServiceImpl.class);

   
    static String process = "process";
    static String nrcID = "13/MiFoS(N)163525";

    static String userName = "demomfi1@mifos.com";
    static String password = "password";
    static String subscriptionKey = "15c15ff17493acb44cb223f2feab2fe4";

    static String subscriptionId = "4A7C-317A41BA-1FF8-8A64-F8EDBE0F625D";
    static String url = "url";
    static String token = "token";
    static Long uniqueID = 1L;

    static String testresult = "{   'Data': {'BorrowerInfo': {'MainIdentifier': '2113439293', 'Name': 'Aung Khant Min',"
            + "            'NRC': '13/MiFoS(N)163525', 'Gender': '', 'DOB': '1990-01-20', 'FatherName': '', 'Address': '',"
            + "            'LastUpdatedDtm': 'Jul  8 2020  9:27AM', 'PrintedDtm': 'Aug  2 2020  2:54AM'  },  "
            + "     'ActiveLoanStatus': 'Record found',  'ActiveLoans': [{'ReportingDate': '2020-03-01',"
            + "                'LoanGUID': 'BC309AD3-0444-4EFD-807E-79ECB99EE999',"
            + "                'Institution': 'Demo 1',   'Division': 'YANGON ',"
            + "                'Township': 'YANKIN ', 'DisbursedDate': 'xxxx',"
            + "                'DisbursedAmount': '500000', 'PrincipalOutstandingAmount': '300000',"
            + "                'TotalOutstandingAmount': 'xxxx', 'PrincipalOverdueAmount': '100000',"
            + "                'TotalOverdueAmount': '', 'DaysInDelay': '10',"
            + "                'UpdatedDtm': 'Apr 27 2020  9:41AM', 'SortColumn': '2020-03-01T00:00:00'}], "
            + "     'WriteOffLoanStatus': 'No record found', 'WriteOffLoans': null,"
            + "     'CreditScore': { 'Score': 'N/A','Class': 'N/A',  'Note': ''},"
            + "    'InquiryStatus': 'Record found', 'Inquiries': [{ 'InquiryDate': '', 'Institution': '','NoOfInquiry': ''} ] },"
            + "    'MessageDtm': '8/1/2020 8:24:20 PM UTC','SubscriptionID': '4A7C-317A41BA-1FF8-8A64-F8EDBE0F625D',"
            + "    'CallerIP': '207.46.228.155',"
            + "    'URI': 'https://qa-mmcix-api.azurewebsites.net/20200324/api/Dashboard/GetCreditReport?uniqueId=2113439292',"
            + "    'ResponseMessage': 'Record found'}";

   
    public static String getCreditReport() {

   
        when(creditBureauWritePlatformServiceImpl.httpConnectionMethod(process, nrcID, userName, password, subscriptionKey, subscriptionId,
                url, token, uniqueID)).thenReturn(testresult);

       
        String test =creditBureauWritePlatformServiceImpl.httpConnectionMethod(process, nrcID, userName, password, subscriptionKey, subscriptionId,url, token, uniqueID);
        return test;

    }

}
