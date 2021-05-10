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
package org.apache.fineract.vlms.fieldexecutive.api;

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
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.vlms.customer.data.CustomerDocumentsData;
import org.apache.fineract.vlms.fieldexecutive.data.EnquiryData;
import org.apache.fineract.vlms.fieldexecutive.data.EnrollData;
import org.apache.fineract.vlms.fieldexecutive.data.FeCashInHandLimit;
import org.apache.fineract.vlms.fieldexecutive.data.TaskData;
import org.apache.fineract.vlms.fieldexecutive.domain.DocumentsData;
import org.apache.fineract.vlms.fieldexecutive.service.FEReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/fieldExecutive")
@Component
@Scope("singleton")
public class FELoansApiResource {

    private final PlatformSecurityContext context;
    private final FEReadPlatformService readPlatformService;
    private final DefaultToApiJsonSerializer<DocumentsData> toApiJsonSerializer;
    private final DefaultToApiJsonSerializer<CustomerDocumentsData> toApiCustomerDocumentsJsonSerializer;
    private final DefaultToApiJsonSerializer<TaskData> toApiTaskJsonSerializer;
    private final DefaultToApiJsonSerializer<FeCashInHandLimit> toApiCashInHandJsonSerializer;
    private final DefaultToApiJsonSerializer<EnquiryData> toApiEnquiryJsonSerializer;
    private final DefaultToApiJsonSerializer<EnrollData> toApiEnrollJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("documents"));
    private final String resourceNameForPermissions = "DOCUMENTS";

    @Autowired
    public FELoansApiResource(final PlatformSecurityContext context, final FEReadPlatformService readPlatformService,
            final DefaultToApiJsonSerializer<DocumentsData> toApiJsonSerializer,
            final DefaultToApiJsonSerializer<CustomerDocumentsData> toApiCustomerDocumentsJsonSerializer,
            final DefaultToApiJsonSerializer<TaskData> toApiTaskJsonSerializer,
            final DefaultToApiJsonSerializer<EnquiryData> toApiEnquiryJsonSerializer,
            final DefaultToApiJsonSerializer<EnrollData> toApiEnrollJsonSerializer,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final DefaultToApiJsonSerializer<FeCashInHandLimit> toApiCashInHandJsonSerializer) {
        this.context = context;
        this.readPlatformService = readPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.toApiCustomerDocumentsJsonSerializer = toApiCustomerDocumentsJsonSerializer;
        this.toApiTaskJsonSerializer = toApiTaskJsonSerializer;
        this.toApiEnquiryJsonSerializer = toApiEnquiryJsonSerializer;
        this.toApiEnrollJsonSerializer = toApiEnrollJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.toApiCashInHandJsonSerializer = toApiCashInHandJsonSerializer;
    }

    @GET
    @Path("getEnquiry")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Enquires", description = "Returns the list of Enquries.\n" + "\n" + "Example Requests:\n" + "\n"
            + "documents")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(array =
                                                                              // @ArraySchema(schema =
                                                                              // @Schema(implementation =
                                                                              // CodesApiResourceSwagger.GetCodesResponse.class))))
                                                                              // })
    public String retrieveAllEnquires(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<EnquiryData> enquiresData = this.readPlatformService.retrieveAllEnquires();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiEnquiryJsonSerializer.serialize(settings, enquiresData, RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Path("enquiry")
    @Operation(summary = "Create a User Loan Enquiry", description = "Removes the user and the associated roles and permissions.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(schema =
                                                                              // @Schema(implementation =
                                                                              // UsersApiResourceSwagger.DeleteUsersUserIdResponse.class)))
                                                                              // })
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String customerEnquiry(@Parameter(hidden = true) final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .createFEEnquiry().withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Path("getEnroll")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Enroll", description = "Returns the list of Enroll.\n" + "\n" + "Example Requests:\n" + "\n"
            + "documents")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(array =
                                                                              // @ArraySchema(schema =
                                                                              // @Schema(implementation =
                                                                              // CodesApiResourceSwagger.GetCodesResponse.class))))
                                                                              // })
    public String retrieveAllEnroll(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<EnrollData> enrollData = this.readPlatformService.retrieveAllEnroll();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiEnrollJsonSerializer.serialize(settings, enrollData, RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Path("enroll")
    @Operation(summary = "Create a User Loan Enquiry", description = "Removes the user and the associated roles and permissions.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(schema =
                                                                              // @Schema(implementation =
                                                                              // UsersApiResourceSwagger.DeleteUsersUserIdResponse.class)))
                                                                              // })
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String customerEnroll(@Parameter(hidden = true) final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .createFEEnroll().withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @POST
    @Path("applyLoan")
    @Operation(summary = "Create a Loan", description = "Removes the user and the associated roles and permissions.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(schema =
                                                                              // @Schema(implementation =
                                                                              // UsersApiResourceSwagger.DeleteUsersUserIdResponse.class)))
                                                                              // })
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String customerApplyLoan(@Parameter(hidden = true) final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .createNewLoan().withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @POST
    @Path("newApplicantLoan")
    @Operation(summary = "Create a new applicant loan", description = "Removes the user and the associated roles and permissions.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(schema =
                                                                              // @Schema(implementation =
                                                                              // UsersApiResourceSwagger.DeleteUsersUserIdResponse.class)))
                                                                              // })
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String customerNewApplicantLoan(@Parameter(hidden = true) final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .createNewApplicantLoan().withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("modifyCustomerDetails/{customerDetailId}")
    @Operation(summary = "Create a new applicant loan", description = "Removes the user and the associated roles and permissions.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(schema =
                                                                              // @Schema(implementation =
                                                                              // UsersApiResourceSwagger.DeleteUsersUserIdResponse.class)))
                                                                              // })
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String modifyNewApplicantLoan(@Parameter(hidden = true) final String apiRequestBodyAsJson,
            @PathParam("customerDetailId") @Parameter(description = "customerDetailId") final Long customerDetailId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .updateCustomerDetail(customerDetailId).withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("modifyGuarantorDetails/{guarantorDetailsId}")
    @Operation(summary = "Create a new applicant loan", description = "Removes the user and the associated roles and permissions.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(schema =
                                                                              // @Schema(implementation =
                                                                              // UsersApiResourceSwagger.DeleteUsersUserIdResponse.class)))
                                                                              // })
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String modifyGuarantorApplication(@Parameter(hidden = true) final String apiRequestBodyAsJson,
            @PathParam("guarantorDetailsId") @Parameter(description = "guarantorDetailsId") final Long guarantorDetailsId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .updateGuarantorDetail(guarantorDetailsId).withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("modifyVehicleDetails/{vehicleDetailsId}")
    @Operation(summary = "Create a new applicant loan", description = "Removes the user and the associated roles and permissions.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(schema =
                                                                              // @Schema(implementation =
                                                                              // UsersApiResourceSwagger.DeleteUsersUserIdResponse.class)))
                                                                              // })
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String modifyVehicleDetailsApplication(@Parameter(hidden = true) final String apiRequestBodyAsJson,
            @PathParam("vehicleDetailsId") @Parameter(description = "vehicleDetailsId") final Long vehicleDetailsId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .updateVehicleDetail(vehicleDetailsId).withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("modifyBankDetails/{bankDetailsId}")
    @Operation(summary = "Create a new applicant loan", description = "Removes the user and the associated roles and permissions.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(schema =
                                                                              // @Schema(implementation =
                                                                              // UsersApiResourceSwagger.DeleteUsersUserIdResponse.class)))
                                                                              // })
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String modifyBankApplication(@Parameter(hidden = true) final String apiRequestBodyAsJson,
            @PathParam("bankDetailsId") @Parameter(description = "bankDetailsId") final Long bankDetailsId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .updateBankDetail(bankDetailsId).withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("modifyLoanDetails/{loanDetailsId}")
    @Operation(summary = "Create a new applicant loan", description = "Removes the user and the associated roles and permissions.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(schema =
                                                                              // @Schema(implementation =
                                                                              // UsersApiResourceSwagger.DeleteUsersUserIdResponse.class)))
                                                                              // })
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String modifyLoanDetailsApplication(@Parameter(hidden = true) final String apiRequestBodyAsJson,
            @PathParam("loanDetailsId") @Parameter(description = "loanDetailsId") final Long loanDetailsId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .updateLoanDetail(loanDetailsId).withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Path("getDocuments")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Document_Type", description = "Returns the list of documentsType.\n" + "\n" + "Example Requests:\n"
            + "\n" + "documents")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(array =
                                                                              // @ArraySchema(schema =
                                                                              // @Schema(implementation =
                                                                              // CodesApiResourceSwagger.GetCodesResponse.class))))
                                                                              // })
    public String retrieveDocuments(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<DocumentsData> documentsType = this.readPlatformService.retrieveAllDocumentsType();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, documentsType, RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("getDocumentsData/{clientId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Document_Type", description = "Returns the list of documentsType.\n" + "\n" + "Example Requests:\n"
            + "\n" + "documents")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(array =
                                                                              // @ArraySchema(schema =
                                                                              // @Schema(implementation =
                                                                              // CodesApiResourceSwagger.GetCodesResponse.class))))
                                                                              // })
    public String retrieveDocumentsData(@Context final UriInfo uriInfo,
            @QueryParam("command") @Parameter(description = "command") final String commandParam,
            @PathParam("clientId") @Parameter(description = "clientId") final Long clientId) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<CustomerDocumentsData> documentsData = this.readPlatformService.retrieveDocumentData(commandParam, clientId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiCustomerDocumentsJsonSerializer.serialize(settings, documentsData, RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Path("task")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Create a Task", description = "Creates a Field Executive Task. FE created through api are always 'user defined' and so system defined is marked as false.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") })
    public String createFETask(@Parameter(hidden = true) final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createFETask().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("editTask/{taskId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Create a Task", description = "Creates a Field Executive Task. FE created through api are always 'user defined' and so system defined is marked as false.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") })
    public String editFETask(@Parameter(hidden = true) final String apiRequestBodyAsJson,
            @PathParam("taskId") @Parameter(description = "taskId") final Long taskId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().editFETask(taskId).withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @DELETE
    @Path("deleteTask/{taskId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Create a Task", description = "Creates a Field Executive Task. FE created through api are always 'user defined' and so system defined is marked as false.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") })
    public String deleteFETask(@Parameter(hidden = true) final String apiRequestBodyAsJson,
            @PathParam("taskId") @Parameter(description = "taskId") final Long taskId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteFETask(taskId).withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Path("getTask")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Task", description = "Returns the list of Task.\n" + "\n" + "Example Requests:\n" + "\n" + "documents")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(array =
                                                                              // @ArraySchema(schema =
                                                                              // @Schema(implementation =
                                                                              // CodesApiResourceSwagger.GetCodesResponse.class))))
                                                                              // })
    public String retrieveTask(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<TaskData> documentsType = this.readPlatformService.retrieveAllTask();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiTaskJsonSerializer.serialize(settings, documentsType, RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("feCashInHand")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Task", description = "Returns the list of Task.\n" + "\n" + "Example Requests:\n" + "\n" + "documents")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(array =
                                                                              // @ArraySchema(schema =
                                                                              // @Schema(implementation =
                                                                              // CodesApiResourceSwagger.GetCodesResponse.class))))
                                                                              // })
    public String retrievefeCashInHand(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<FeCashInHandLimit> feCashInHandLimit = this.readPlatformService.retrieveAllfeCashLimitData();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiCashInHandJsonSerializer.serialize(settings, feCashInHandLimit, RESPONSE_DATA_PARAMETERS);
    }

    @PUT
    @Path("feCashInHand/{Id}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Create a Task", description = "Creates a Field Executive Task. FE created through api are always 'user defined' and so system defined is marked as false.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") })
    public String updateFECashLimit(@Parameter(hidden = true) final String apiRequestBodyAsJson,
            @PathParam("Id") @Parameter(description = "Id") final Long Id) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateFECashLimit(Id).withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

}
