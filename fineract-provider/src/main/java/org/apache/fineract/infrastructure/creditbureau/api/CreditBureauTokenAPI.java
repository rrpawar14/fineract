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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.creditbureau.data.CreditBureauData;
import org.apache.fineract.infrastructure.creditbureau.data.CreditBureauLoanProductMappingData;
import org.apache.fineract.infrastructure.creditbureau.data.CreditBureauTokenData;
import org.apache.fineract.infrastructure.creditbureau.service.CreditBureauReadConfigurationService;
import org.apache.fineract.infrastructure.creditbureau.service.CreditBureauReadPlatformService;
import org.apache.fineract.infrastructure.creditbureau.service.CreditBureauTokenReadPlatformService;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/CreditBureauToken")
@Component
@Scope("singleton")
@Api(value = "CreditBureau Token")
public class CreditBureauTokenAPI {

    private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("subscriptionId", "subscriptionKey", "userName"));
    private final String resourceNameForPermissions = "CreditBureau";
    private final PlatformSecurityContext context;
    private final CreditBureauReadPlatformService readPlatformService;
    private final DefaultToApiJsonSerializer<CreditBureauData> toApiJsonSerializer;
    // private final CreditBureauLoanProductMappingReadPlatformService
    // readPlatformServiceCreditBureauLoanProduct;
    private final CreditBureauTokenReadPlatformService readPlatformServiceCreditBureauToken;
    private final DefaultToApiJsonSerializer<CreditBureauLoanProductMappingData> toApiJsonSerializerCreditBureauLoanProduct;
    private final DefaultToApiJsonSerializer<CreditBureauTokenData> toApiJsonSerializerCreditBureauToken;
    // private final DefaultToApiJsonSerializer<CreditBureauTokenData>
    // toApiJsonSerializerReport;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final CreditBureauReadConfigurationService creditBureauConfiguration;

    @Autowired
    public CreditBureauTokenAPI(final PlatformSecurityContext context, final CreditBureauReadPlatformService readPlatformService,
            final DefaultToApiJsonSerializer<CreditBureauData> toApiJsonSerializer,
            // final CreditBureauTokenDataReadPlatformService
            // readPlatformServiceCreditBureauTokenData,

            final DefaultToApiJsonSerializer<CreditBureauLoanProductMappingData> toApiJsonSerializerCreditBureauLoanProduct,
            // final OrganisationCreditBureauReadPlatformService
            final CreditBureauTokenReadPlatformService readPlatformServiceCreditBureauToken,
            final DefaultToApiJsonSerializer<CreditBureauTokenData> toApiJsonSerializerCreditBureauToken,
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
        this.toApiJsonSerializerCreditBureauLoanProduct = toApiJsonSerializerCreditBureauLoanProduct;
        this.readPlatformServiceCreditBureauToken = readPlatformServiceCreditBureauToken;
        this.toApiJsonSerializerCreditBureauToken = toApiJsonSerializerCreditBureauToken;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        // this.toApiJsonSerializerReport = toApiJsonSerializerReport;
        this.creditBureauConfiguration = creditBureauConfiguration;

    }

    /*
     * @POST
     *
     * @Consumes({ MediaType.APPLICATION_JSON })
     *
     * @Produces({ MediaType.APPLICATION_JSON }) public String
     * addOrganisationCreditBureau(@PathParam("organisationCreditBureauId")
     * final Long organisationCreditBureauId, final String apiRequestBodyAsJson)
     * {
     *
     * // final CommandWrapper commandRequest = new //
     * CommandWrapperBuilder().createCreditBureauToken().withJson(
     * apiRequestBodyAsJson).build();
     *
     * // final CommandProcessingResult result = //
     * this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
     *
     * return this.toApiJsonSerializer.serialize(result); }
     */

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getOrganisationCreditBureau(@Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<CreditBureauTokenData> creditBureauTokenData = this.readPlatformServiceCreditBureauToken
                .retrieveAllCreditBureauToken();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializerCreditBureauToken.serialize(settings, creditBureauTokenData, this.RESPONSE_DATA_PARAMETERS);

    }

}
