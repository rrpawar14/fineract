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
import java.util.Map;
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

    private final DefaultToApiJsonSerializer<CreditReportData> toCreditReportApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final CreditReportWritePlatformService creditReportWritePlatformService;
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
        this.toCreditReportApiJsonSerializer = toCreditReportApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.creditReportWritePlatformService = creditReportWritePlatformService;
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

    // submit loan file of clients to thitsawork
    @POST
    @Path("addCreditReport")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String addCreditReport(@FormDataParam("file") final File creditreport,
            @QueryParam("creditBureauId") @Parameter(description = "creditBureauId") final Long creditBureauId) {
        LOG.info("creditreport api {}", creditreport);
        final String importDocumentId = creditReportWritePlatformService.addCreditReport(creditreport, creditBureauId);
        return this.toCreditReportApiJsonSerializer.serialize(importDocumentId);
    }

    // save fetched creditreport in local database
    @POST
    @Path("saveCreditReport")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String saveCreditReport(@RequestParam("apiRequestBodyAsJson") final String apiRequestBodyAsJson,
            @QueryParam("creditBureauId") @Parameter(description = "creditBureauId") final Long creditBureauId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .saveCreditReport(creditBureauId) //
                .withJson(apiRequestBodyAsJson) //
                .build(); //

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toCreditReportApiJsonSerializer.serialize(result);

    }
}
