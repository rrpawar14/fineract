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
package org.apache.fineract.vlms.customer.api;

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
import org.apache.fineract.infrastructure.codes.data.CodeData;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.vlms.customer.data.AadharData;
import org.apache.fineract.vlms.customer.data.BranchAnalyticsData;
import org.apache.fineract.vlms.customer.data.CustomerDetailsData;
import org.apache.fineract.vlms.customer.data.VehicleLoanData;
import org.apache.fineract.vlms.customer.service.AadharDecodingReadPlatformServiceImpl;
import org.apache.fineract.vlms.customer.service.VehicleLoanManagementReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/customers")
@Component
@Scope("singleton")
public class CustomerLoansApiResource {

    private final PlatformSecurityContext context;
    private final VehicleLoanManagementReadPlatformService readPlatformService;
    private final DefaultToApiJsonSerializer<VehicleLoanData> toApiJsonSerializer;
    private final DefaultToApiJsonSerializer<BranchAnalyticsData> tobranchApiJsonSerializer;
    private final DefaultToApiJsonSerializer<CodeData> toCodeDataApiJsonSerializer;
    private final DefaultToApiJsonSerializer<AadharData> toAadharDataApiJsonSerializer;
    private final DefaultToApiJsonSerializer<CustomerDetailsData> toCustomerDataApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    // private final AadharDecodingReadPlatformServiceImpl aadharDecodingReadPlatformServiceImpl;
    private static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("customers"));
    private final String resourceNameForPermissions = "CUSTOMERS";

    @Autowired
    public CustomerLoansApiResource(final PlatformSecurityContext context,
            final VehicleLoanManagementReadPlatformService readPlatformService,
            final DefaultToApiJsonSerializer<VehicleLoanData> toApiJsonSerializer,
            final DefaultToApiJsonSerializer<CodeData> toCodeDataApiJsonSerializer,
            final DefaultToApiJsonSerializer<AadharData> toAadharDataApiJsonSerializer,
            final DefaultToApiJsonSerializer<CustomerDetailsData> toCustomerDataApiJsonSerializer,
            final DefaultToApiJsonSerializer<BranchAnalyticsData> tobranchApiJsonSerializer,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
        // final AadharDecodingReadPlatformServiceImpl aadharDecodingReadPlatformServiceImpl) {
        this.context = context;
        this.readPlatformService = readPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.toCodeDataApiJsonSerializer = toCodeDataApiJsonSerializer;
        this.toAadharDataApiJsonSerializer = toAadharDataApiJsonSerializer;
        this.toCustomerDataApiJsonSerializer = toCustomerDataApiJsonSerializer;
        this.tobranchApiJsonSerializer = tobranchApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        // this.aadharDecodingReadPlatformServiceImpl = aadharDecodingReadPlatformServiceImpl;
    }

    @GET
    @Path("address")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve All Vehicle Loan Data", description = "Returns the list of Vehicle Loan Data.\n" + "\n"
            + "Example Requests:\n" + "\n" + "documents")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(array =
                                                                              // @ArraySchema(schema =
                                                                              // @Schema(implementation =
                                                                              // CodesApiResourceSwagger.GetCodesResponse.class))))
                                                                              // })
    public String retrieveAddressData(@Context final UriInfo uriInfo,
            @PathParam("encrypytedData") @Parameter(description = "encrypytedData") final String encrypytedData) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final AadharData aadharData = AadharDecodingReadPlatformServiceImpl.fetchAdharDetails(encrypytedData);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toAadharDataApiJsonSerializer.serialize(settings, aadharData, RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("aadharscanner/{encrypytedData}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve All Vehicle Loan Data", description = "Returns the list of Vehicle Loan Data.\n" + "\n"
            + "Example Requests:\n" + "\n" + "documents")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(array =
                                                                              // @ArraySchema(schema =
                                                                              // @Schema(implementation =
                                                                              // CodesApiResourceSwagger.GetCodesResponse.class))))
                                                                              // })
    public String retrieveAadharData(@Context final UriInfo uriInfo,
            @PathParam("encrypytedData") @Parameter(description = "encrypytedData") final String encrypytedData) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final AadharData aadharData = AadharDecodingReadPlatformServiceImpl.fetchAdharDetails(encrypytedData);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toAadharDataApiJsonSerializer.serialize(settings, aadharData, RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("mobileNumber/{mobileNo}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve All Vehicle Loan Data", description = "Returns the list of Vehicle Loan Data.\n" + "\n"
            + "Example Requests:\n" + "\n" + "documents")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(array =
                                                                              // @ArraySchema(schema =
                                                                              // @Schema(implementation =
                                                                              // CodesApiResourceSwagger.GetCodesResponse.class))))
                                                                              // })
    public String retrieveVehicleLoanData(@Context final UriInfo uriInfo,
            @PathParam("mobileNo") @Parameter(description = "mobileNo") final Long mobileNo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final CodeData mobileNumberStatus = this.readPlatformService.checkMobileNumberStatus(mobileNo);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toCodeDataApiJsonSerializer.serialize(settings, mobileNumberStatus, RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("customerDetails")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve All Vehicle Loan Data", description = "Returns the list of Vehicle Loan Data.\n" + "\n"
            + "Example Requests:\n" + "\n" + "documents")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(array =
                                                                              // @ArraySchema(schema =
                                                                              // @Schema(implementation =
                                                                              // CodesApiResourceSwagger.GetCodesResponse.class))))
                                                                              // })
    public String retrieveCustomerDetails(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<CustomerDetailsData> customerDetails = this.readPlatformService.retrieveCustomerData();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toCustomerDataApiJsonSerializer.serialize(settings, customerDetails, RESPONSE_DATA_PARAMETERS);
    }

    @PUT
    @Path("address/{addressId}")
    @Operation(summary = "Create a new applicant loan", description = "Removes the user and the associated roles and permissions.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(schema =
                                                                              // @Schema(implementation =
                                                                              // UsersApiResourceSwagger.DeleteUsersUserIdResponse.class)))
                                                                              // })
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String modifyAddress(@Parameter(hidden = true) final String apiRequestBodyAsJson,
            @PathParam("addressId") @Parameter(description = "addressId") final Long addressId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .updateCustomerAddress(addressId).withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Path("allCustomerLoanDetails")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve All Vehicle Loan Data", description = "Returns the list of Vehicle Loan Data.\n" + "\n"
            + "Example Requests:\n" + "\n" + "documents")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(array =
                                                                              // @ArraySchema(schema =
                                                                              // @Schema(implementation =
                                                                              // CodesApiResourceSwagger.GetCodesResponse.class))))
                                                                              // })
    public String retrieveVehicleLoanData(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<VehicleLoanData> vehicleLoanData = this.readPlatformService.retrieveAllCustomerVehicleLoan();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, vehicleLoanData, RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("loanByUserId/{userId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Vehicle Loan Data", description = "Returns the Vehicle Loan Data Based on UserId/CustomerId.\n" + "\n"
            + "Example Requests:\n" + "\n" + "documents")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(array =
                                                                              // @ArraySchema(schema =
                                                                              // @Schema(implementation =
                                                                              // CodesApiResourceSwagger.GetCodesResponse.class))))
                                                                              // })
    public String retrieveVehicleLoanDataByUserId(@Context final UriInfo uriInfo,
            @PathParam("userId") @Parameter(description = "userId") final Long userId) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<VehicleLoanData> vehicleLoanData = this.readPlatformService.retrieveVehicleLoanByUserId(userId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, vehicleLoanData, RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("loanByLoanId/{loanId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Vehicle Loan Data", description = "Returns the Vehicle Loan Data Based on UserId/CustomerId.\n" + "\n"
            + "Example Requests:\n" + "\n" + "documents")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(array =
                                                                              // @ArraySchema(schema =
                                                                              // @Schema(implementation =
                                                                              // CodesApiResourceSwagger.GetCodesResponse.class))))
                                                                              // })
    public String retrieveVehicleLoanDataByLoanId(@Context final UriInfo uriInfo,
            @PathParam("loanId") @Parameter(description = "loanId") final Long loanId) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final VehicleLoanData vehicleLoanData = this.readPlatformService.retrieveVehicleLoanByLoanId(loanId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, vehicleLoanData, RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("branchAnalytic")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Vehicle Loan Data", description = "Returns the Vehicle Loan Data Based on UserId/CustomerId.\n" + "\n"
            + "Example Requests:\n" + "\n" + "documents")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") }) // , content = @Content(array =
                                                                              // @ArraySchema(schema =
                                                                              // @Schema(implementation =
                                                                              // CodesApiResourceSwagger.GetCodesResponse.class))))
                                                                              // })
    public String retrieveVehicleLoanDataByLoanId(@Context final UriInfo uriInfo,
            @QueryParam("command") @Parameter(description = "command") final String commandParam) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final BranchAnalyticsData branchAnalytics = this.readPlatformService.getBranchAnalyticsData(commandParam);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.tobranchApiJsonSerializer.serialize(settings, branchAnalytics, RESPONSE_DATA_PARAMETERS);
    }
}
