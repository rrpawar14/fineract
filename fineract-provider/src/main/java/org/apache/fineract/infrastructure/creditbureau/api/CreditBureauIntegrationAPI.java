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

package org.apache.fineract.infrastructure.creditbureau.api;

import com.sun.jersey.multipart.FormDataParam;
import io.swagger.v3.oas.annotations.Parameter;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.creditbureau.data.CreditBureauReportData;
import org.apache.fineract.infrastructure.creditbureau.data.CreditReportData;
import org.apache.fineract.infrastructure.creditbureau.service.CreditReportReadPlatformService;
import org.apache.fineract.infrastructure.creditbureau.service.CreditReportWritePlatformService;
import org.apache.fineract.infrastructure.creditbureau.service.ThitsaWorksCreditBureauIntegrationWritePlatformServiceImpl;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/creditBureauIntegration")
@Component
@Scope("singleton")
public class CreditBureauIntegrationAPI {

    private static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("id", "creditBureauId", "nrc", "creditReport"));

    private final PlatformSecurityContext context;
    private final FromJsonHelper fromApiJsonHelper;
    private final DefaultToApiJsonSerializer<CreditReportData> toCreditReportApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final CreditReportWritePlatformService creditReportWritePlatformService;
    private final CreditReportReadPlatformService creditReportReadPlatformService;
    private final DefaultToApiJsonSerializer<CreditReportData> toApiJsonSerializer;
    private final ThitsaWorksCreditBureauIntegrationWritePlatformServiceImpl thitsaWorksCreditBureauIntegrationWritePlatformServiceImpl;
    private static final Logger LOG = LoggerFactory.getLogger(CreditBureauIntegrationAPI.class);

    // testing

    // testing

    @Autowired
    public CreditBureauIntegrationAPI(final PlatformSecurityContext context, final FromJsonHelper fromApiJsonHelper,
            final DefaultToApiJsonSerializer<CreditReportData> toCreditReportApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final CreditReportWritePlatformService creditReportWritePlatformService,
            final CreditReportReadPlatformService creditReportReadPlatformService,
            final DefaultToApiJsonSerializer<CreditReportData> toApiJsonSerializer,
            final ThitsaWorksCreditBureauIntegrationWritePlatformServiceImpl thitsaWorksCreditBureauIntegrationWritePlatformServiceImpl) {
        this.context = context;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.toCreditReportApiJsonSerializer = toCreditReportApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.creditReportWritePlatformService = creditReportWritePlatformService;
        this.creditReportReadPlatformService = creditReportReadPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.thitsaWorksCreditBureauIntegrationWritePlatformServiceImpl = thitsaWorksCreditBureauIntegrationWritePlatformServiceImpl;

    }

    @POST
    @Path("creditReport")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    // public String postCreditReport(@RequestParam("params") final Map<String, Object> params) {
    public String postCreditReport(@Parameter(hidden = true) final Map<String, Object> params) {
        LOG.info("params {}", params);
        /*
         * Gson gson = new Gson(); final String json = gson.toJson(params); final CommandWrapper commandRequest = new
         * CommandWrapperBuilder().getCreditReport().withJson(json).build(); final CommandProcessingResult result =
         * this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
         */

        // testing
        String result = "{\n" + "    \"Data\": {\n" + "        \"BorrowerInfo\": {\n" + "            \"MainIdentifier\": \"8113399260\",\n"
                + "            \"Name\": \"Aye Aye\",\n" + "            \"NRC\": \"12/KaMaRa(N)253426\",\n"
                + "            \"Gender\": \"Female\",\n" + "            \"DOB\": \"1991-05-22 1990-05-22\",\n"
                + "            \"FatherName\": \"U Aye Myint Maung\",\n" + "            \"Address\": \"\",\n"
                + "            \"LastUpdatedDtm\": \"Jul  8 2020  9:27AM\",\n" + "            \"PrintedDtm\": \"Aug  2 2020  2:52AM\"\n"
                + "        },\n" + "        \"ActiveLoanStatus\": \"Record found\",\n" + "        \"ActiveLoans\": [\n" + "            {\n"
                + "                \"ReportingDate\": \"2020-03-01\",\n"
                + "                \"LoanGUID\": \"219FDCB9-9653-4D2F-A3D7-8401DDEFAB98\",\n"
                + "                \"Institution\": \"Demo 1\",\n" + "                \"Division\": \"YANGON \",\n"
                + "                \"Township\": \"THONGWA \",\n" + "                \"DisbursedDate\": \"xxxx\",\n"
                + "                \"DisbursedAmount\": \"500000\",\n" + "                \"PrincipalOutstandingAmount\": \"300000\",\n"
                + "                \"TotalOutstandingAmount\": \"xxxx\",\n" + "                \"PrincipalOverdueAmount\": \"50000\",\n"
                + "                \"TotalOverdueAmount\": \"\",\n" + "                \"DaysInDelay\": \"5\",\n"
                + "                \"UpdatedDtm\": \"Apr 24 2020 10:25AM\",\n" + "                \"SortColumn\": \"2020-03-01T00:00:00\"\n"
                + "            },\n" + "            {\n" + "                \"ReportingDate\": \"2020-03-01\",\n"
                + "                \"LoanGUID\": \"1D3DB0D1-EB67-4D65-B799-E22642431F35\",\n"
                + "                \"Institution\": \"Demo 2\",\n" + "                \"Division\": \"YANGON \",\n"
                + "                \"Township\": \"TWANTAY\",\n" + "                \"DisbursedDate\": \"xxxx\",\n"
                + "                \"DisbursedAmount\": \"300000\",\n" + "                \"PrincipalOutstandingAmount\": \"200000\",\n"
                + "                \"TotalOutstandingAmount\": \"xxxx\",\n" + "                \"PrincipalOverdueAmount\": \"30000\",\n"
                + "                \"TotalOverdueAmount\": \"\",\n" + "                \"DaysInDelay\": \"10\",\n"
                + "                \"UpdatedDtm\": \"Apr 24 2020 10:26AM\",\n" + "                \"SortColumn\": \"2020-03-01T00:00:00\"\n"
                + "            }\n" + "        ],\n" + "        \"WriteOffLoanStatus\": \"Record found\",\n"
                + "        \"WriteOffLoans\": [\n" + "            {\n" + "                \"ReportingDate\": \"2020-03-01\",\n"
                + "                \"ClaimGUID\": \"9F2D8B97-AE60-443A-9546-663CDEB8650F\",\n" + "                \"Name\": aye  ,\n"
                + "                \"FatherName\": \"U Aye Myint Maung\",\n" + "                \"Division\": \"YANGON \",\n"
                + "                \"Township\": \"YANKIN \",\n" + "                \"WardVillage\": \"\",\n"
                + "                \"DisbursedDate\": \"2019-01-01\",\n" + "                \"DisbursedAmount\": \"300000\",\n"
                + "                \"WriteOffDate\": \"2020-01-01\",\n" + "                \"PrincipalWriteOffAmount\": \"150000\",\n"
                + "                \"Note\": \"Run Away.\",\n" + "                \"UpdatedDtm\": \"Apr 24 2020 10:26AM\",\n"
                + "                \"Status\": \"Y\",\n" + "                \"Institution\": \"Demo 3\"\n" + "            }\n"
                + "        ],\n" + "        \"CreditScore\": {\n" + "            \"Score\": \"N/A\",\n"
                + "            \"Class\": \"N/A\",\n" + "            \"Note\": \"\"\n" + "        },\n"
                + "        \"InquiryStatus\": \"Record found\",\n" + "        \"Inquiries\": [\n" + "            {\n"
                + "                \"InquiryDate\": \"\",\n" + "                \"Institution\": \"\",\n"
                + "                \"NoOfInquiry\": \"\"\n" + "            }\n" + "        ]\n" + "    },\n"
                + "    \"MessageDtm\": \"8/1/2020 8:22:38 PM UTC\",\n"
                + "    \"SubscriptionID\": \"317A1FF8-625D-41BA-BE0F-F8ED8A644A7C\",\n" + "    \"CallerIP\": \"207.46.228.155\",\n"
                + "    \"URI\": \"https://qa-mmcix-api.azurewebsites.net/20200324/api/Dashboard/GetCreditReport?uniqueId=8113399260\",\n"
                + "    \"ResponseMessage\": \"Record found\"\n" + "}";
        CreditBureauReportData reportobj = thitsaWorksCreditBureauIntegrationWritePlatformServiceImpl
                .getCreditReportFromThitsaWorksTesting(result);

        // final CommandProcessingResult result =
        // this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        CommandProcessingResultBuilder commandProcessingResultBuilder = new CommandProcessingResultBuilder();
        final CommandProcessingResult commandProcessingResult = commandProcessingResultBuilder.withCreditReport(reportobj).build();
        // testing

        return this.toCreditReportApiJsonSerializer.serialize(commandProcessingResult);

    }

    // submit loan file of clients to Credit Bureau
    @POST
    @Path("addCreditReport")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String addCreditReport(@FormDataParam("file") final File creditReport,
            @QueryParam("creditBureauId") @Parameter(description = "creditBureauId") final Long creditBureauId) {

        final String responseMessage = this.creditReportWritePlatformService.addCreditReport(creditReport, creditBureauId);
        return this.toCreditReportApiJsonSerializer.serialize(responseMessage);
    }

    // saves fetched-creditreport into database
    @POST
    @Path("saveCreditReport")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String saveCreditReport(@Parameter(hidden = true) final String apiRequestBodyAsJson,
            @QueryParam("creditBureauId") @Parameter(description = "creditBureauId") final Long creditBureauId,
            @QueryParam("creditReportNumber") @Parameter(description = "creditReportNumber") final String creditReportNumber) {

        LOG.info("apiRequestBodyAsJson {}", apiRequestBodyAsJson);

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .saveCreditReport(creditBureauId, creditReportNumber) // creditReportNumber is a NRC number for
                                                                      // Thitsawork
                .withJson(apiRequestBodyAsJson) // apiRequestBodyAsJson is a creditReport
                .build(); //

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toCreditReportApiJsonSerializer.serialize(result);

    }

    // fetch saved creditReports(NRC) from database by creditBureauId, to select for downloading and deleting the
    // reports
    @GET
    @Path("creditReport/{creditBureauId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getSavedCreditReport(@PathParam("creditBureauId") @Parameter(description = "creditBureauId") final Long creditBureauId,
            @Context final UriInfo uriInfo) {

        this.context.authenticatedUser();

        final Collection<CreditReportData> creditReport = this.creditReportReadPlatformService.retrieveCreditReport(creditBureauId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, creditReport, RESPONSE_DATA_PARAMETERS);

    }

    // download saved creditReports from database
    @GET
    @Path("downloadCreditReport")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response downloadCreditReport(@QueryParam("creditBureauId") @Parameter(description = "creditBureauId") final Long creditBureauId,
            @QueryParam("creditReportNumber") @Parameter(description = "creditReportNumber") final String creditReportNumber) {
        return creditReportWritePlatformService.downloadCreditReport(creditBureauId, creditReportNumber);

    }

    // deletes saved creditReports from database
    @DELETE
    @Path("deleteCreditReport/{creditBureauId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String deleteCreditReport(@PathParam("creditBureauId") @Parameter(description = "creditBureauId") final Long creditBureauId,
            @Parameter(hidden = true) final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteCreditReport(creditBureauId).withJson(apiRequestBodyAsJson)
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toCreditReportApiJsonSerializer.serialize(result);

    }
}
