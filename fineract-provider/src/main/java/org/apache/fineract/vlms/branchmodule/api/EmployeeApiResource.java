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
package org.apache.fineract.vlms.branchmodule.api;

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
import org.apache.fineract.vlms.branchmodule.data.EmployeeData;
import org.apache.fineract.vlms.branchmodule.data.LoanApprovalLimitData;
import org.apache.fineract.vlms.branchmodule.data.LoanDisbursalLimitData;
import org.apache.fineract.vlms.branchmodule.service.BranchModuleReadPlatformService;
import org.apache.fineract.vlms.fieldexecutive.data.TaskData;
import org.apache.fineract.vlms.fieldexecutive.domain.DocumentsData;
import org.apache.fineract.vlms.fieldexecutive.service.FEReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/Employee")
@Component
@Scope("singleton")
public class EmployeeApiResource {

    private final PlatformSecurityContext context;
    private final BranchModuleReadPlatformService branchModuleReadPlatformService;
    private final FEReadPlatformService readPlatformService;
    private final DefaultToApiJsonSerializer<DocumentsData> toApiJsonSerializer;
    private final DefaultToApiJsonSerializer<EmployeeData> toApiEmployeeJsonSerializer;
    private final DefaultToApiJsonSerializer<TaskData> toApiTaskJsonSerializer;
    private final DefaultToApiJsonSerializer<LoanApprovalLimitData> toApiApprovalJsonSerializer;
    private final DefaultToApiJsonSerializer<LoanDisbursalLimitData> toApiDisbursalJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("documents"));
    private final String resourceNameForPermissions = "DOCUMENTS";

    @Autowired
    public EmployeeApiResource(final PlatformSecurityContext context, final FEReadPlatformService readPlatformService,
            final BranchModuleReadPlatformService branchModuleReadPlatformService,
            final DefaultToApiJsonSerializer<DocumentsData> toApiJsonSerializer,
            final DefaultToApiJsonSerializer<EmployeeData> toApiEmployeeJsonSerializer,
            final DefaultToApiJsonSerializer<TaskData> toApiTaskJsonSerializer,
            final DefaultToApiJsonSerializer<LoanApprovalLimitData> toApiApprovalJsonSerializer,
            final DefaultToApiJsonSerializer<LoanDisbursalLimitData> toApiDisbursalJsonSerializer,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
        this.context = context;
        this.readPlatformService = readPlatformService;
        this.branchModuleReadPlatformService = branchModuleReadPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.toApiEmployeeJsonSerializer = toApiEmployeeJsonSerializer;
        this.toApiTaskJsonSerializer = toApiTaskJsonSerializer;
        this.toApiApprovalJsonSerializer = toApiApprovalJsonSerializer;
        this.toApiDisbursalJsonSerializer = toApiDisbursalJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
    }

    @GET
    @Path("getEmployees")
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

        final Collection<EmployeeData> employeeData = this.branchModuleReadPlatformService.retrieveAllEmployee();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiEmployeeJsonSerializer.serialize(settings, employeeData, RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("getEmployees/{employeeId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Enquires", description = "Returns the list of Enquries.\n" + "\n" + "Example Requests:\n" + "\n"
            + "documents")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(array =
                                                                              // @ArraySchema(schema =
                                                                              // @Schema(implementation =
                                                                              // CodesApiResourceSwagger.GetCodesResponse.class))))
                                                                              // })
    public String retrieveEmployee(@Context final UriInfo uriInfo,
            @PathParam("employeeId") @Parameter(description = "employeeId") final Long employeeId) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final EmployeeData employeeData = this.branchModuleReadPlatformService.retrieveEmployeeById(employeeId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiEmployeeJsonSerializer.serialize(settings, employeeData, RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Path("createEmployee")
    @Operation(summary = "Create a User Loan Enquiry", description = "Removes the user and the associated roles and permissions.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(schema =
                                                                              // @Schema(implementation =
                                                                              // UsersApiResourceSwagger.DeleteUsersUserIdResponse.class)))
                                                                              // })
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String customerEnquiry(@Parameter(hidden = true) final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .createEmployee().withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("updateEmployee/{employeeId}")
    @Operation(summary = "Create a User Loan Enquiry", description = "Removes the user and the associated roles and permissions.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(schema =
                                                                              // @Schema(implementation =
                                                                              // UsersApiResourceSwagger.DeleteUsersUserIdResponse.class)))
                                                                              // })
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateEmployee(@Parameter(hidden = true) final String apiRequestBodyAsJson,
            @PathParam("employeeId") @Parameter(description = "employeeId") final Long employeeId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .updateEmployee(employeeId).withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("updateQualification/{qualificationId}")
    @Operation(summary = "Create a User Loan Enquiry", description = "Removes the user and the associated roles and permissions.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(schema =
                                                                              // @Schema(implementation =
                                                                              // UsersApiResourceSwagger.DeleteUsersUserIdResponse.class)))
                                                                              // })
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateQualification(@Parameter(hidden = true) final String apiRequestBodyAsJson,
            @PathParam("qualificationId") @Parameter(description = "qualificationId") final Long qualificationId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .updateEducation(qualificationId).withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("updateInsurance/{insuranceId}")
    @Operation(summary = "Create a User Loan Enquiry", description = "Removes the user and the associated roles and permissions.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(schema =
                                                                              // @Schema(implementation =
                                                                              // UsersApiResourceSwagger.DeleteUsersUserIdResponse.class)))
                                                                              // })
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateInsurance(@Parameter(hidden = true) final String apiRequestBodyAsJson,
            @PathParam("insuranceId") @Parameter(description = "insuranceId") final Long insuranceId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .updateInsurance(insuranceId).withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Path("getLoanApproval")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Enquires", description = "Returns the list of Enquries.\n" + "\n" + "Example Requests:\n" + "\n"
            + "documents")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(array =
                                                                              // @ArraySchema(schema =
                                                                              // @Schema(implementation =
                                                                              // CodesApiResourceSwagger.GetCodesResponse.class))))
                                                                              // })
    public String retrieveAllLoanApproval(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<LoanApprovalLimitData> loanApprovalData = this.branchModuleReadPlatformService.retrieveAllLoanApproval();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiApprovalJsonSerializer.serialize(settings, loanApprovalData, RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Path("submitLoanApproval")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Enquires", description = "Returns the list of Enquries.\n" + "\n" + "Example Requests:\n" + "\n"
            + "documents")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(array =
                                                                              // @ArraySchema(schema =
                                                                              // @Schema(implementation =
                                                                              // CodesApiResourceSwagger.GetCodesResponse.class))))
                                                                              // })
    public String submitLoanApproval(@Context final UriInfo uriInfo, @Parameter(hidden = true) final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .createLoanApprovalLimit().withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Path("getLoanDisbursal")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Enquires", description = "Returns the list of Enquries.\n" + "\n" + "Example Requests:\n" + "\n"
            + "documents")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(array =
                                                                              // @ArraySchema(schema =
                                                                              // @Schema(implementation =
                                                                              // CodesApiResourceSwagger.GetCodesResponse.class))))
                                                                              // })
    public String retrieveAllLoanDisbursal(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<LoanDisbursalLimitData> loanDisbursalData = this.branchModuleReadPlatformService.retrieveAllLoanDisbursal();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiDisbursalJsonSerializer.serialize(settings, loanDisbursalData, RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Path("submitLoanDisbursal")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Enquires", description = "Returns the list of Enquries.\n" + "\n" + "Example Requests:\n" + "\n"
            + "documents")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(array =
                                                                              // @ArraySchema(schema =
                                                                              // @Schema(implementation =
                                                                              // CodesApiResourceSwagger.GetCodesResponse.class))))
                                                                              // })
    public String submitLoanDisbursal(@Context final UriInfo uriInfo, @Parameter(hidden = true) final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .createLoanDisbursalLimit().withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

}
