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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.creditbureau.data.CreditBureauLoanProductMappingData;
import org.apache.fineract.infrastructure.creditbureau.data.CreditBureauTokenCredentialData;
import org.apache.fineract.infrastructure.creditbureau.data.CreditReportData;
import org.apache.fineract.infrastructure.creditbureau.service.CreditBureauReadConfigurationService;
import org.apache.fineract.infrastructure.creditbureau.service.CreditBureauReadPlatformService;
import org.apache.fineract.infrastructure.creditbureau.service.CreditBureauReportsReadPlatformService;
import org.apache.fineract.infrastructure.creditbureau.service.CreditBureauTokenReadPlatformService;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/creditBureauToken")
@Component
@Scope("singleton")
@Api(value = "CreditBureauToken")
public class CreditBureauTokenAPI {

    private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("subscriptionId", "subscriptionKey", "userName"));
    private final String resourceNameForPermissions = "CreditBureauToken";
    private final PlatformSecurityContext context;
    private final CreditBureauReadPlatformService readPlatformService;
    // private final DefaultToApiJsonSerializer<CreditBureauTokenData>
    // toApiJsonSerializer;
    // private final CreditBureauLoanProductMappingReadPlatformService
    // readPlatformServiceCreditBureauLoanProduct;
    private final CreditBureauTokenReadPlatformService readPlatformServiceCreditBureauToken;
    private final CreditBureauReportsReadPlatformService readPlatformServiceCreditBureauReports;
    // private final
    // DefaultToApiJsonSerializer<CreditBureauLoanProductMappingData>
    // toApiJsonSerializerCreditBureauLoanProduct;
    private final DefaultToApiJsonSerializer<CreditReportData> toCreditReportApiJsonSerializer;
    private final DefaultToApiJsonSerializer<CreditBureauTokenCredentialData> toApiJsonSerializer;
    // private final DefaultToApiJsonSerializer<CreditBureauTokenData>
    // toApiJsonSerializerReport;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final CreditBureauReadConfigurationService creditBureauConfiguration;

    @Autowired
    public CreditBureauTokenAPI(final PlatformSecurityContext context, final CreditBureauReadPlatformService readPlatformService,
            final DefaultToApiJsonSerializer<CreditBureauTokenCredentialData> toApiJsonSerializer,
            final DefaultToApiJsonSerializer<CreditReportData> toCreditReportApiJsonSerializer,
            // final CreditBureauTokenDataReadPlatformService
            // readPlatformServiceCreditBureauTokenData,

            final DefaultToApiJsonSerializer<CreditBureauLoanProductMappingData> toApiJsonSerializerCreditBureauLoanProduct,
            // final OrganisationCreditBureauReadPlatformService
            final CreditBureauTokenReadPlatformService readPlatformServiceCreditBureauToken,
            final CreditBureauReportsReadPlatformService readPlatformServiceCreditBureauReports,
            // final DefaultToApiJsonSerializer<CreditBureauTokenData>
            // toApiJsonSerializerCreditBureauToken,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            // final DefaultToApiJsonSerializer<CreditBureauConfigurationData>
            // toApiJsonSerializerReport,

            final CreditBureauReadConfigurationService creditBureauConfiguration) {
        this.context = context;
        this.readPlatformService = readPlatformService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        // this.readPlatformServiceCreditBureauLoanProduct =
        // readPlatformServiceCreditBureauLoanProduct;
        // this.toApiJsonSerializerCreditBureauLoanProduct =
        // toApiJsonSerializerCreditBureauLoanProduct;
        this.readPlatformServiceCreditBureauToken = readPlatformServiceCreditBureauToken;
        this.readPlatformServiceCreditBureauReports = readPlatformServiceCreditBureauReports;
        // this.toApiJsonSerializerCreditBureauToken =
        // toApiJsonSerializerCreditBureauToken;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.toCreditReportApiJsonSerializer = toCreditReportApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        // this.toApiJsonSerializerReport = toApiJsonSerializerReport;
        this.creditBureauConfiguration = creditBureauConfiguration;

    }

    @POST
    @Path("addtokendata")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    // @ApiOperation(value = "Create a Token", httpMethod = "POST")
    public String addCreditBureauTokenData(final String apiRequestBodyAsJson) {
        final CommandWrapper commandRequest = new CommandWrapperBuilder().addCreditBureauTokenData().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        System.out.println("token command request");
        return this.toApiJsonSerializer.serialize(result);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    // @ApiOperation(value = "Create a Token", httpMethod = "POST")
    public String createCreditBureauToken(final String apiRequestBodyAsJson) {
        final CommandWrapper commandRequest = new CommandWrapperBuilder().createCreditBureauToken().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        System.out.println("token command request");
        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getCreditBureauToken(@Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<CreditBureauTokenCredentialData> creditBureauTokenData = this.readPlatformServiceCreditBureauToken
                .retrieveAllCreditBureauTokenData();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, creditBureauTokenData, this.RESPONSE_DATA_PARAMETERS);

    }

    @GET
    @Path("getcreditreport")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getCreditReport(@Context final UriInfo uriInfo,
            @QueryParam("searchId") @ApiParam(value = "searchId") final String searchId) {
        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final CreditReportData creditReportData = this.readPlatformServiceCreditBureauReports.retrieveAllSearchReport(searchId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toCreditReportApiJsonSerializer.serialize(settings, creditReportData, this.RESPONSE_DATA_PARAMETERS);

    }

}
