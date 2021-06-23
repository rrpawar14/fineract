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
package org.apache.fineract.vlms.loantransfer.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.vlms.fieldexecutive.data.TaskData;
import org.apache.fineract.vlms.loantransfer.data.LoanTransferDashboardData;
import org.apache.fineract.vlms.loantransfer.data.LoanTransferTeamApplicationStatusData;
import org.apache.fineract.vlms.loantransfer.service.LoanTransferReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/loanTransfer")
@Component
@Scope("singleton")
public class LoanTransferApiResource {

    private final PlatformSecurityContext context;
    private final LoanTransferReadPlatformService loanTransferReadPlatformService;
    private final DefaultToApiJsonSerializer<TaskData> toApiTaskJsonSerializer;
    private final DefaultToApiJsonSerializer<LoanTransferTeamApplicationStatusData> toApiLoanStatusDataJsonSerializer;
    private final DefaultToApiJsonSerializer<LoanTransferDashboardData> toApiLoanTransferAnalyticsJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("branch"));
    private final String resourceNameForPermissions = "LOANTRANSFER";

    @Autowired
    public LoanTransferApiResource(final PlatformSecurityContext context,
            final LoanTransferReadPlatformService loanTransferReadPlatformService,
            final DefaultToApiJsonSerializer<TaskData> toApiTaskJsonSerializer,
            final DefaultToApiJsonSerializer<LoanTransferTeamApplicationStatusData> toApiLoanStatusDataJsonSerializer,
            final DefaultToApiJsonSerializer<LoanTransferDashboardData> toApiLoanTransferAnalyticsJsonSerializer,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
        this.context = context;
        this.loanTransferReadPlatformService = loanTransferReadPlatformService;
        this.toApiTaskJsonSerializer = toApiTaskJsonSerializer;
        this.toApiLoanStatusDataJsonSerializer = toApiLoanStatusDataJsonSerializer;
        this.toApiLoanTransferAnalyticsJsonSerializer = toApiLoanTransferAnalyticsJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
    }

    @GET
    @Path("getLoanApplicationStatus")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Enquires", description = "Returns the list of Enquries.\n" + "\n" + "Example Requests:\n" + "\n"
            + "documents")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(array =
                                                                              // @ArraySchema(schema =
                                                                              // @Schema(implementation =
                                                                              // CodesApiResourceSwagger.GetCodesResponse.class))))
                                                                              // })
    public String retrieveAllLoanTransferDashboardData(@Context final UriInfo uriInfo,
            @QueryParam("loanType") @Parameter(description = "loanType") final String loanType,
            @QueryParam("loanId") @Parameter(description = "loanId") final Long loanId) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final LoanTransferTeamApplicationStatusData LoanTransferTeamApplicationStatus = this.loanTransferReadPlatformService
                .retrieveAllLoanApplicationStatus(loanType, loanId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiLoanStatusDataJsonSerializer.serialize(settings, LoanTransferTeamApplicationStatus, RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("getLoanTransferDashboardData")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Enquires", description = "Returns the list of Enquries.\n" + "\n" + "Example Requests:\n" + "\n"
            + "documents")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(array =
                                                                              // @ArraySchema(schema =
                                                                              // @Schema(implementation =
                                                                              // CodesApiResourceSwagger.GetCodesResponse.class))))
                                                                              // })
    public String retrieveAllLoanTransferDashboardData(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<LoanTransferDashboardData> loanTransferDashboardData = this.loanTransferReadPlatformService
                .retrieveAllLoanTransferDashboardData();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiLoanTransferAnalyticsJsonSerializer.serialize(settings, loanTransferDashboardData, RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("getAllLoanTransferTask")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Enquires", description = "Returns the list of Enquries.\n" + "\n" + "Example Requests:\n" + "\n"
            + "documents")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(array =
                                                                              // @ArraySchema(schema =
                                                                              // @Schema(implementation =
                                                                              // CodesApiResourceSwagger.GetCodesResponse.class))))
                                                                              // })
    public String retrieveAllLoanTransferTask(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<TaskData> taskData = this.loanTransferReadPlatformService.retrieveAllTask();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiTaskJsonSerializer.serialize(settings, taskData, RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("getByTaskStatus")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Task", description = "Returns the list of Task.\n" + "\n" + "Example Requests:\n" + "\n" + "documents")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(array =
                                                                              // @ArraySchema(schema =
                                                                              // @Schema(implementation =
                                                                              // CodesApiResourceSwagger.GetCodesResponse.class))))
                                                                              // })
    public String retrieveTaskByStatus(@Context final UriInfo uriInfo,
            @QueryParam("taskStatus") @Parameter(description = "taskStatus") final String taskStatus) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<TaskData> taskData = this.loanTransferReadPlatformService.retrieveAllTaskStatus(taskStatus);

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

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createLoanTransferTask().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiLoanTransferAnalyticsJsonSerializer.serialize(result);
    }

    @PUT
    @Path("editTask/{taskId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Create a Task", description = "Creates a Field Executive Task. FE created through api are always 'user defined' and so system defined is marked as false.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") })
    public String updateLoanTransferTask(@Parameter(hidden = true) final String apiRequestBodyAsJson,
            @PathParam("taskId") @Parameter(description = "taskId") final Long taskId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().editLoanTransferTask(taskId).withJson(apiRequestBodyAsJson)
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiLoanTransferAnalyticsJsonSerializer.serialize(result);
    }

    @DELETE
    @Path("deleteTask/{id}")
    @Operation(summary = "Create a User Loan Enquiry", description = "Removes the user and the associated roles and permissions.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(schema =
                                                                              // @Schema(implementation =
                                                                              // UsersApiResourceSwagger.DeleteUsersUserIdResponse.class)))
                                                                              // })
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String customerDeleteEnquiry(@Parameter(hidden = true) final String apiRequestBodyAsJson,
            @PathParam("id") @Parameter(description = "id") final Long id) {
        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .deleteLoanTransferTask(id) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiLoanTransferAnalyticsJsonSerializer.serialize(result);
    }

    @POST
    @Path("{loanId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") })
    public String stateTransitions(@PathParam("loanId") @Parameter(description = "loanId") final Long loanId,
            @QueryParam("command") @Parameter(description = "command") final String commandParam,
            @Parameter(hidden = true) final String apiRequestBodyAsJson) {

        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);

        CommandProcessingResult result = null;

        if (is(commandParam, "loanTransfer")) {
            final CommandWrapper commandRequest = builder.LoanTransferApplication(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }

        else if (is(commandParam, "dcTransfer")) {
            final CommandWrapper commandRequest = builder.DCTransferApplication(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }

        else if (is(commandParam, "loanTransferRequest")) {
            final CommandWrapper commandRequest = builder.LoanTransferRequest(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }

        else if (is(commandParam, "dcTransferRequest")) {
            final CommandWrapper commandRequest = builder.DCTransferRequest(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }

        else if (is(commandParam, "changeRequest")) {
            final CommandWrapper commandRequest = builder.changeRequest(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }

        else if (is(commandParam, "reminderRequest")) {
            final CommandWrapper commandRequest = builder.reminderRequest(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }

        else if (is(commandParam, "additionalDocumentRequest")) {
            final CommandWrapper commandRequest = builder.additionalDocumentRequest(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }

        return this.toApiLoanTransferAnalyticsJsonSerializer.serialize(result);
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }

}
