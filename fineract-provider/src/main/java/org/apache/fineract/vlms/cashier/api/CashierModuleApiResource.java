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
package org.apache.fineract.vlms.cashier.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.vlms.cashier.data.CashierAnalyticsAllData;
import org.apache.fineract.vlms.cashier.data.HLPaymentData;
import org.apache.fineract.vlms.cashier.data.HLPaymentDetailsData;
import org.apache.fineract.vlms.cashier.data.VoucherData;
import org.apache.fineract.vlms.cashier.data.VoucherDetailsData;
import org.apache.fineract.vlms.cashier.service.CashierModuleReadPlatformService;
import org.apache.fineract.vlms.fieldexecutive.data.TaskData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/Cashier")
@Component
@Scope("singleton")
public class CashierModuleApiResource {

    private final PlatformSecurityContext context;
    private final CashierModuleReadPlatformService cashierModuleReadPlatformService;
    private final DefaultToApiJsonSerializer<CashierAnalyticsAllData> toApiCashierAnalyticsJsonSerializer;
    private final DefaultToApiJsonSerializer<HLPaymentData> toApiHLPaymentAnalyticsJsonSerializer;
    private final DefaultToApiJsonSerializer<HLPaymentDetailsData> toApiHLPaymentDetailsAnalyticsJsonSerializer;
    private final DefaultToApiJsonSerializer<VoucherData> toApiVoucherJsonSerializer;
    private final DefaultToApiJsonSerializer<VoucherDetailsData> toApiVoucherDetailsJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final DefaultToApiJsonSerializer<TaskData> toApiTaskJsonSerializer;
    private static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("cashier"));
    private final String resourceNameForPermissions = "CASHIER";

    @Autowired
    public CashierModuleApiResource(final PlatformSecurityContext context,
            final CashierModuleReadPlatformService cashierModuleReadPlatformService,
            final DefaultToApiJsonSerializer<CashierAnalyticsAllData> toApiCashierAnalyticsJsonSerializer,
            final DefaultToApiJsonSerializer<HLPaymentData> toApiHLPaymentAnalyticsJsonSerializer,
            final DefaultToApiJsonSerializer<HLPaymentDetailsData> toApiHLPaymentDetailsAnalyticsJsonSerializer,
            final DefaultToApiJsonSerializer<VoucherData> toApiVoucherJsonSerializer,
            final DefaultToApiJsonSerializer<VoucherDetailsData> toApiVoucherDetailsJsonSerializer,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final DefaultToApiJsonSerializer<TaskData> toApiTaskJsonSerializer) {
        this.context = context;
        this.cashierModuleReadPlatformService = cashierModuleReadPlatformService;
        this.toApiCashierAnalyticsJsonSerializer = toApiCashierAnalyticsJsonSerializer;
        this.toApiHLPaymentAnalyticsJsonSerializer = toApiHLPaymentAnalyticsJsonSerializer;
        this.toApiHLPaymentDetailsAnalyticsJsonSerializer = toApiHLPaymentDetailsAnalyticsJsonSerializer;
        this.toApiVoucherJsonSerializer = toApiVoucherJsonSerializer;
        this.toApiVoucherDetailsJsonSerializer = toApiVoucherDetailsJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.toApiTaskJsonSerializer = toApiTaskJsonSerializer;
    }

    @GET
    @Path("getCashierAnalyticsData")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Cashier Data", description = "Returns the Data of Cashier DashBoard.\n" + "\n" + "Example Requests:\n"
            + "\n" + "documents")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(array =
                                                                              // @ArraySchema(schema =
                                                                              // @Schema(implementation =
                                                                              // CodesApiResourceSwagger.GetCodesResponse.class))))
                                                                              // })
    public String retrieveAllBranchAnalytics(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<CashierAnalyticsAllData> cashierData = this.cashierModuleReadPlatformService.retrieveAllCashierAnalyticsData();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiCashierAnalyticsJsonSerializer.serialize(settings, cashierData, RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("getHLData")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Cashier Data", description = "Returns the Data of Cashier DashBoard.\n" + "\n" + "Example Requests:\n"
            + "\n" + "documents")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(array =
                                                                              // @ArraySchema(schema =
                                                                              // @Schema(implementation =
                                                                              // CodesApiResourceSwagger.GetCodesResponse.class))))
                                                                              // })
    public String retrieveHLData(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<HLPaymentDetailsData> hlPaymentData = this.cashierModuleReadPlatformService.retrieveAllHLPaymentData();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiHLPaymentDetailsAnalyticsJsonSerializer.serialize(settings, hlPaymentData, RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Path("createHLPayment")
    @Operation(summary = "Create a HL Payment", description = "")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(schema =
                                                                              // @Schema(implementation =
                                                                              // UsersApiResourceSwagger.DeleteUsersUserIdResponse.class)))
                                                                              // })
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createHLPayment(@Parameter(hidden = true) final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .createHLPayment().withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiHLPaymentAnalyticsJsonSerializer.serialize(result);
    }

    @GET
    @Path("getVoucherData")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Cashier Data", description = "Returns the Data of Cashier DashBoard.\n" + "\n" + "Example Requests:\n"
            + "\n" + "documents")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(array =
                                                                              // @ArraySchema(schema =
                                                                              // @Schema(implementation =
                                                                              // CodesApiResourceSwagger.GetCodesResponse.class))))
                                                                              // })
    public String retrieveVoucherData(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<VoucherDetailsData> voucherDetailsData = this.cashierModuleReadPlatformService.retrieveAllVoucherData();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiVoucherDetailsJsonSerializer.serialize(settings, voucherDetailsData, RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Path("createVoucher")
    @Operation(summary = "Create a HL Payment", description = "")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(schema =
                                                                              // @Schema(implementation =
                                                                              // UsersApiResourceSwagger.DeleteUsersUserIdResponse.class)))
                                                                              // })
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createVoucher(@Parameter(hidden = true) final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .createVoucher().withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiVoucherJsonSerializer.serialize(result);
    }

    @GET
    @Path("getCashierTaskData")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Cashier Data", description = "Returns the Data of Cashier DashBoard.\n" + "\n" + "Example Requests:\n"
            + "\n" + "documents")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(array =
                                                                              // @ArraySchema(schema =
                                                                              // @Schema(implementation =
                                                                              // CodesApiResourceSwagger.GetCodesResponse.class))))
                                                                              // })
    public String retrieveCashierTaskData(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<TaskData> taskData = this.cashierModuleReadPlatformService.retrieveAllCashierTaskData();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiTaskJsonSerializer.serialize(settings, taskData, RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Path("task")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Create a Task", description = "Creates a Field Executive Task. FE created through api are always 'user defined' and so system defined is marked as false.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") })
    public String createLoanTransferTask(@Parameter(hidden = true) final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createCashierModuleTask().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiTaskJsonSerializer.serialize(result);
    }

    @PUT
    @Path("editTask/{taskId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Create a Task", description = "Creates a Field Executive Task. FE created through api are always 'user defined' and so system defined is marked as false.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") })
    public String updateLoanTransferTask(@Parameter(hidden = true) final String apiRequestBodyAsJson,
            @PathParam("taskId") @Parameter(description = "taskId") final Long taskId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().editCashierModuleTask(taskId).withJson(apiRequestBodyAsJson)
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiTaskJsonSerializer.serialize(result);
    }

}
