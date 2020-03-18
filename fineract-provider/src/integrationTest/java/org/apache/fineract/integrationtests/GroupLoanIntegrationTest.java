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

import static org.junit.Assert.assertEquals;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.integrationtests.common.ClientHelper;
import org.apache.fineract.integrationtests.common.CommonConstants;
import org.apache.fineract.integrationtests.common.GroupHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.loans.LoanApplicationTestBuilder;
import org.apache.fineract.integrationtests.common.loans.LoanProductTestBuilder;
import org.apache.fineract.integrationtests.common.loans.LoanStatusChecker;
import org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper;
import org.apache.fineract.integrationtests.common.savings.SavingsAccountHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Group Loan Integration Test for checking Loan Application Repayment Schedule.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class GroupLoanIntegrationTest {

    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
    private LoanTransactionHelper loanTransactionHelper;

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
    }

    @Test
    public void checkGroupLoanCreateAndDisburseFlow() {
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);

        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        Integer groupID = GroupHelper.createGroup(this.requestSpec, this.responseSpec, true);
        groupID = GroupHelper.associateClient(this.requestSpec, this.responseSpec, groupID.toString(), clientID.toString());

        final Integer loanProductID = createLoanProduct();
        final Integer loanID = applyForLoanApplication(groupID, loanProductID);
        final ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec,
                loanID);
        verifyLoanRepaymentSchedule(loanSchedule);

    }
    
    @Test
    public void checkGlimAccountCommands() {
    	
    	this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
    	final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        Integer groupID = GroupHelper.createGroup(this.requestSpec, this.responseSpec, true);
        groupID = GroupHelper.associateClient(this.requestSpec, this.responseSpec, groupID.toString(), clientID.toString());
        final Integer loanProductID = createLoanProduct();
    
        HashMap<String,Integer> glim = applyForGlimApplication(clientID, groupID, loanProductID);
        System.out.println("Glim Loan Application: "+ glim);
        
        final Integer glimId=glim.get("glimId");
        System.out.println("GlimId: "+ glimId);
        
        final Integer loanId=glim.get("loanId");
        System.out.println("LoanId: "+ loanId);
    
        List<Map<String, Object>> approvalFormData = new ArrayList<>();
        approvalFormData.add(approvalFormData(loanId,"22 September 2011"));
        
        HashMap loanStatusHashMap = this.loanTransactionHelper.approveGlimAccount(this.requestSpec, this.responseSpec,
        		approvalFormData, glimId);
        System.out.println("glim approval loanSchedule"+ loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
        
        loanStatusHashMap = this.loanTransactionHelper.disburseGlimAccount("25 September 2011", glimId);
        System.out.println("glim disbursement loanSchedule"+ loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);
        
        loanStatusHashMap = this.loanTransactionHelper.undoDisburseGlimAccount(glimId);
        System.out.println("glim undodisbursement loanSchedule"+ loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);
        
        loanStatusHashMap = this.loanTransactionHelper.undoApprovalGlimAccount(glimId);
        System.out.println("glim undoApproval loanSchedule"+ loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);
        
        loanStatusHashMap = this.loanTransactionHelper.rejectGlimAccount("22 September 2011",glimId);
        System.out.println("glim reject loanSchedule"+ loanStatusHashMap);
        LoanStatusChecker.verifyLoanAccountRejected(loanStatusHashMap);
    }
    
    @Test
    public void getGlimAccountByGroupId() {
    	
    	this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
        
    	final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        Assert.assertNotNull(clientID);

        Integer groupID = GroupHelper.createGroup(this.requestSpec, this.responseSpec, true);
        Assert.assertNotNull(groupID);

        groupID = GroupHelper.associateClient(this.requestSpec, this.responseSpec, groupID.toString(), clientID.toString());
        Assert.assertNotNull(groupID);

        final Integer loanProductID = createLoanProduct();
        Assert.assertNotNull(loanProductID);

        HashMap<String,Integer> glim = applyForGlimApplication(clientID, groupID, loanProductID);
        System.out.println("Glim Loan Application: "+ glim);
        
        final Integer glimId=glim.get("glimId");
        System.out.println("GlimId: "+ glimId);
        
        final Integer loanId=glim.get("loanId");
        System.out.println("LoanId: "+ loanId);
       
        final List<String> retrievedGlimId=GroupHelper.verifyRetrieveGlimAccountsByGroupId(this.requestSpec, this.responseSpec, groupID);
        Assert.assertNotNull(retrievedGlimId.toString());
    }
    
    @Test
    public void getGlimAccountByGlimId() {
    	
    	this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
        
    	final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        Assert.assertNotNull(clientID);

        Integer groupID = GroupHelper.createGroup(this.requestSpec, this.responseSpec, true);
        Assert.assertNotNull(groupID);

        groupID = GroupHelper.associateClient(this.requestSpec, this.responseSpec, groupID.toString(), clientID.toString());
        Assert.assertNotNull(groupID);

        final Integer loanProductID = createLoanProduct();
        Assert.assertNotNull(loanProductID);

        HashMap<String,Integer> glim = applyForGlimApplication(clientID, groupID, loanProductID);
        System.out.println("Glim Loan Application: "+ glim);
        
        final Integer glimId=glim.get("glimId");
        System.out.println("GlimId: "+ glimId);
        
        final Integer loanId=glim.get("loanId");
        System.out.println("LoanId: "+ loanId);
       
        final List<String> retrievedGlimAccountId=GroupHelper.verifyRetrieveGlimAccountsByGlimId(this.requestSpec, this.responseSpec, glimId);
        Assert.assertNotNull(retrievedGlimAccountId);
    }
    
    private Map<String, Object> approvalFormData(final Integer loanId, final String approvedOnDate) {
        Map<String, Object> map = new HashMap<>();
        map.put("loanId", loanId);
        map.put("approvedOnDate", approvedOnDate);
        map.put("dateFormat", CommonConstants.dateFormat);
        map.put("locale", "en");
        return map;
    }

    private Integer createLoanProduct() {
        System.out.println("------------------------------CREATING NEW LOAN PRODUCT ---------------------------------------");
        final String loanProductJSON = new LoanProductTestBuilder() //
                .withPrincipal("12,000.00") //
                .withNumberOfRepayments("4") //
                .withRepaymentAfterEvery("1") //
                .withRepaymentTypeAsMonth() //
                .withinterestRatePerPeriod("1") //
                .withInterestRateFrequencyTypeAsMonths() //
                .withAmortizationTypeAsEqualInstallments() //
                .withInterestTypeAsDecliningBalance() //
                .build(null);
        return this.loanTransactionHelper.getLoanProductId(loanProductJSON);
    }

    private Integer applyForLoanApplication(final Integer groupID, final Integer loanProductID) {
        System.out.println("--------------------------------APPLYING FOR LOAN APPLICATION--------------------------------");
        final String loanApplicationJSON = new LoanApplicationTestBuilder() //
                .withPrincipal("12,000.00") //
                .withLoanTermFrequency("4") //
                .withLoanTermFrequencyAsMonths() //
                .withNumberOfRepayments("4") //
                .withRepaymentEveryAfter("1") //
                .withRepaymentFrequencyTypeAsMonths() //
                .withInterestRatePerPeriod("2") //
                .withAmortizationTypeAsEqualInstallments() //
                .withInterestTypeAsDecliningBalance() //
                .withInterestCalculationPeriodTypeSameAsRepaymentPeriod() //
                .withExpectedDisbursementDate("20 September 2011") //
                .withSubmittedOnDate("20 September 2011") //
                .withLoanType("group").build(groupID.toString(), loanProductID.toString(), null);
        System.out.println(loanApplicationJSON);
        return this.loanTransactionHelper.getLoanId(loanApplicationJSON);
    }
    
    private HashMap<String,Integer> applyForGlimApplication(final Integer clientID, final Integer groupID, final Integer loanProductID) {
        System.out.println("--------------------------------APPLYING FOR LOAN APPLICATION--------------------------------");
        final String GlimApplicationJSON = new LoanApplicationTestBuilder() //
                .withPrincipal("12,000.00") //
                .withLoanTermFrequency("4") //
                .withLoanTermFrequencyAsMonths() //
                .withNumberOfRepayments("4") //
                .withRepaymentEveryAfter("1") //
                .withRepaymentFrequencyTypeAsMonths() //
                .withInterestRatePerPeriod("2") //
                .withAmortizationTypeAsEqualInstallments() //
                .withInterestTypeAsDecliningBalance() //
                .withInterestCalculationPeriodTypeSameAsRepaymentPeriod() //
                .withExpectedDisbursementDate("20 September 2011") //
                .withSubmittedOnDate("20 September 2011")
                .withLoanType("glim")
                .withtotalLoan("10000")
                .withParentAccount("1").build(clientID.toString(), groupID.toString(), loanProductID.toString(), null);
        System.out.println(GlimApplicationJSON);
        return this.loanTransactionHelper.getGlimId(GlimApplicationJSON);
    }

    private void verifyLoanRepaymentSchedule(final ArrayList<HashMap> loanSchedule) {
        System.out.println("--------------------VERIFYING THE PRINCIPAL DUES,INTEREST DUE AND DUE DATE--------------------------");

        assertEquals("Checking for Due Date for 1st Month", new ArrayList<>(Arrays.asList(2011, 10, 20)),
                loanSchedule.get(1).get("dueDate"));
        assertEquals("Checking for Principal Due for 1st Month", new Float("2911.49"), loanSchedule.get(1).get("principalOriginalDue"));
        assertEquals("Checking for Interest Due for 1st Month", new Float("240.00"), loanSchedule.get(1).get("interestOriginalDue"));

        assertEquals("Checking for Due Date for 2nd Month", new ArrayList<>(Arrays.asList(2011, 11, 20)),
                loanSchedule.get(2).get("dueDate"));
        assertEquals("Checking for Principal Due for 2nd Month", new Float("2969.72"), loanSchedule.get(2).get("principalDue"));
        assertEquals("Checking for Interest Due for 2nd Month", new Float("181.77"), loanSchedule.get(2).get("interestOriginalDue"));

        assertEquals("Checking for Due Date for 3rd Month", new ArrayList<>(Arrays.asList(2011, 12, 20)),
                loanSchedule.get(3).get("dueDate"));
        assertEquals("Checking for Principal Due for 3rd Month", new Float("3029.11"), loanSchedule.get(3).get("principalDue"));
        assertEquals("Checking for Interest Due for 3rd Month", new Float("122.38"), loanSchedule.get(3).get("interestOriginalDue"));

        assertEquals("Checking for Due Date for 4th Month", new ArrayList<>(Arrays.asList(2012, 1, 20)),
                loanSchedule.get(4).get("dueDate"));
        assertEquals("Checking for Principal Due for 4th Month", new Float("3089.68"), loanSchedule.get(4).get("principalDue"));
        assertEquals("Checking for Interest Due for 4th Month", new Float("61.79"), loanSchedule.get(4).get("interestOriginalDue"));
    }
}