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

import com.google.gson.Gson;
import com.sun.jersey.multipart.FormDataParam;
import io.swagger.v3.oas.annotations.Parameter;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.creditbureau.data.CreditBureauLoanProductMappingData;
import org.apache.fineract.infrastructure.creditbureau.data.CreditReportData;
import org.apache.fineract.infrastructure.creditbureau.service.CreditBureauReadConfigurationService;
import org.apache.fineract.infrastructure.creditbureau.service.CreditBureauReadPlatformService;
import org.apache.fineract.infrastructure.creditbureau.service.CreditReportWritePlatformService;
import org.apache.fineract.infrastructure.creditbureau.service.ThitsaWorksCreditBureauIntegrationWritePlatformServiceImpl;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

@Path("/creditBureauIntegration")
@Component
@Scope("singleton")
public class CreditBureauIntegrationAPI {

    private final Set<String> reponseDataParameters = new HashSet<>(Arrays.asList("subscriptionId", "subscriptionKey", "userName"));
    private final String resourceNameForPermissions = "CreditBureauToken";
    private final PlatformSecurityContext context;
    private final CreditBureauReadPlatformService readPlatformService;
    private final DefaultToApiJsonSerializer<CreditReportData> toCreditReportApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final CreditBureauReadConfigurationService creditBureauConfiguration;
    private final CreditReportWritePlatformService creditReportWritePlatformService;
    private final ThitsaWorksCreditBureauIntegrationWritePlatformServiceImpl thitsaWorksCreditBureauIntegrationWritePlatformServiceImpl;
    private static final Logger LOG = LoggerFactory.getLogger(CreditBureauIntegrationAPI.class);

    @Autowired
    public CreditBureauIntegrationAPI(final PlatformSecurityContext context, final CreditBureauReadPlatformService readPlatformService,
            final DefaultToApiJsonSerializer<CreditReportData> toCreditReportApiJsonSerializer,
            final DefaultToApiJsonSerializer<CreditBureauLoanProductMappingData> toApiJsonSerializerCreditBureauLoanProduct,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final CreditBureauReadConfigurationService creditBureauConfiguration,
            final CreditReportWritePlatformService creditReportWritePlatformService,
            final ThitsaWorksCreditBureauIntegrationWritePlatformServiceImpl thitsaWorksCreditBureauIntegrationWritePlatformServiceImpl) {
        this.context = context;
        this.readPlatformService = readPlatformService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toCreditReportApiJsonSerializer = toCreditReportApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.creditBureauConfiguration = creditBureauConfiguration;
        this.creditReportWritePlatformService = creditReportWritePlatformService;
        this.thitsaWorksCreditBureauIntegrationWritePlatformServiceImpl = thitsaWorksCreditBureauIntegrationWritePlatformServiceImpl;

    }

    @POST
    @Path("creditReport")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String postCreditReport(@Context final UriInfo uriInfo, @RequestParam("params") final Map<String, Object> params) {

        Gson gson = new Gson();
        final String json = gson.toJson(params);
        final CommandWrapper commandRequest = new CommandWrapperBuilder().getCreditReport().withJson(json).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toCreditReportApiJsonSerializer.serialize(result);

    }

    // submit to thitsawork
    @POST
    @Path("addCreditReport")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String addCreditReport(@FormDataParam("file") final File creditreport,
            @QueryParam("creditBureauId") @Parameter(description = "creditBureauId") final String creditBureauId) {
        LOG.info("creditreport api {}", creditreport);
        final String importDocumentId = creditReportWritePlatformService.addCreditReport(creditreport, creditBureauId);
        return this.toCreditReportApiJsonSerializer.serialize(importDocumentId);
    }

    // save creditreport in local database
    @POST
    @Path("saveCreditReport")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String saveCreditReport(@RequestParam("apiRequestBodyAsJson") final String apiRequestBodyAsJson,
            @QueryParam("creditBureauId") @Parameter(description = "creditBureauId") final String creditBureauId) {
        // public String saveCreditReport(@RequestParam("borrowerInfo") final String borrowerInfo,
        // @RequestParam("creditscore") final String creditscore,
        // @RequestParam("ActiveLoans") final String ActiveLoans, @RequestParam("PaidLoans") final String PaidLoans) {

        // Gson gson = new Gson();
        // final String json = gson.toJson(params);
        // final CommandWrapper commandRequest = new CommandWrapperBuilder().getCreditReport().withJson(json).build();

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .saveCreditReport() //
                .withJson(apiRequestBodyAsJson) //
                .build(); //

        LOG.info("apiRequestBodyAsJson api {}", apiRequestBodyAsJson);
        // final CommandProcessingResult result =
        // thitsaWorksCreditBureauIntegrationWritePlatformServiceImpl.saveCreditReport(borrowerInfo, creditscore,
        // ActiveLoans, PaidLoans);
        // return this.toCreditReportApiJsonSerializer.serialize(result);

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toCreditReportApiJsonSerializer.serialize(result);

    }
}
