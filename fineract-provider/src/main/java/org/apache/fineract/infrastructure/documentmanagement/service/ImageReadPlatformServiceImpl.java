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
package org.apache.fineract.infrastructure.documentmanagement.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.documentmanagement.api.ImagesApiResource.EntityTypeForImages;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepository;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepositoryFactory;
import org.apache.fineract.infrastructure.documentmanagement.data.FileData;
import org.apache.fineract.infrastructure.documentmanagement.data.ImageData;
import org.apache.fineract.infrastructure.documentmanagement.domain.DocumentImageRepository;
import org.apache.fineract.infrastructure.documentmanagement.domain.StorageType;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.organisation.staff.domain.StaffRepositoryWrapper;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.exception.ImageNotFoundException;
import org.apache.fineract.portfolio.loanaccount.domain.BankDetails;
import org.apache.fineract.portfolio.loanaccount.domain.BankDetailsRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.CustomerDetails;
import org.apache.fineract.portfolio.loanaccount.domain.CustomerDetailsRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.CustomerGuarantor;
import org.apache.fineract.portfolio.loanaccount.domain.CustomerGuarantorRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.NewVehicleLoanRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.VehicleDetails;
import org.apache.fineract.portfolio.loanaccount.domain.VehicleDetailsRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.VehicleLoan;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.useradministration.domain.AppUserRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ImageReadPlatformServiceImpl implements ImageReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final ContentRepositoryFactory contentRepositoryFactory;
    private final ClientRepositoryWrapper clientRepositoryWrapper;
    private final StaffRepositoryWrapper staffRepositoryWrapper;
    private final AppUserRepositoryWrapper appUserRepositoryWrapper;
    private final DocumentImageRepository documentImageRepository;
    private final BankDetailsRepositoryWrapper bankDetailsRepositoryWrapper;
    private final CustomerDetailsRepositoryWrapper customerDetailsRepositoryWrapper;
    private final CustomerGuarantorRepositoryWrapper customerGuarantorRepositoryWrapper;
    private final NewVehicleLoanRepositoryWrapper newVehicleLoanRepositoryWrapper;
    private final VehicleDetailsRepositoryWrapper vehicleDetailsRepositoryWrapper;

    @Autowired
    public ImageReadPlatformServiceImpl(final RoutingDataSource dataSource, final ContentRepositoryFactory documentStoreFactory,
            final ClientRepositoryWrapper clientRepositoryWrapper, StaffRepositoryWrapper staffRepositoryWrapper,
            final AppUserRepositoryWrapper appUserRepositoryWrapper, final BankDetailsRepositoryWrapper bankDetailsRepositoryWrapper,
            final CustomerDetailsRepositoryWrapper customerDetailsRepositoryWrapper,
            final VehicleDetailsRepositoryWrapper vehicleDetailsRepositoryWrapper,
            final CustomerGuarantorRepositoryWrapper customerGuarantorRepositoryWrapper,
            final DocumentImageRepository documentImageRepository, final NewVehicleLoanRepositoryWrapper newVehicleLoanRepositoryWrapper) {
        this.staffRepositoryWrapper = staffRepositoryWrapper;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.contentRepositoryFactory = documentStoreFactory;
        this.clientRepositoryWrapper = clientRepositoryWrapper;
        this.appUserRepositoryWrapper = appUserRepositoryWrapper;
        this.bankDetailsRepositoryWrapper = bankDetailsRepositoryWrapper;
        this.customerDetailsRepositoryWrapper = customerDetailsRepositoryWrapper;
        this.vehicleDetailsRepositoryWrapper = vehicleDetailsRepositoryWrapper;
        this.customerGuarantorRepositoryWrapper = customerGuarantorRepositoryWrapper;
        this.documentImageRepository = documentImageRepository;
        this.newVehicleLoanRepositoryWrapper = newVehicleLoanRepositoryWrapper;
    }

    private static final class ImageMapper implements RowMapper<ImageData> {

        private final String entityDisplayName;

        ImageMapper(final String entityDisplayName) {
            this.entityDisplayName = entityDisplayName;
        }

        public String schema(String entityType) {
            StringBuilder builder = new StringBuilder(" ");
            // "");
            if (EntityTypeForImages.CLIENTS.toString().equalsIgnoreCase(entityType)) {
                System.out.println("CLIENTS-Entity");
                builder.append(" from m_image image , m_client client " + " where client.image_id = image.id and client.id=?");
            } else if (EntityTypeForImages.STAFF.toString().equalsIgnoreCase(entityType)) {
                System.out.println("STAFF-Entity");
                builder.append("from m_image image , m_staff staff " + " where staff.image_id = image.id and staff.id=?");
            } else if (EntityTypeForImages.PROFILEIMAGE.toString().equalsIgnoreCase(entityType)) {
                System.out.println("profileimage");
                builder.append(
                        " image.id as id, image.location as location, image.storage_type_enum as storageType from m_image image , m_appuser appuser "
                                + " where appuser.image_id = image.id and appuser.id=?");
            } else if (EntityTypeForImages.CUSTOMERIMAGE.toString().equalsIgnoreCase(entityType)) {
                System.out.println("CUSTOMERIMAGE-Entity");
                builder.append(
                        " docim.id as id, docim.location as location, docim.storage_type_enum as storageType from m_documents_images docim , m_customer_details cd "
                                + " where cd.id = docim.customerdetails_id and docim.entity_name = 'customerimage'  and cd.id=? ");
            } else if (EntityTypeForImages.ADHARPHOTO.toString().equals(entityType)) {
                builder.append(
                        " docim.id as id, docim.location as location, docim.storage_type_enum as storageType from m_documents_images docim , m_customer_details cd "
                                + " where cd.id = docim.customerdetails_id and docim.entity_name = 'adharphoto' and cd.id=? ");

            } else if (EntityTypeForImages.PANCARD.toString().equals(entityType)) {
                builder.append(
                        " docim.id as id, docim.location as location, docim.storage_type_enum as storageType from m_documents_images docim , m_customer_details cd "
                                + " where cd.id = docim.customerdetails_id  and docim.entity_name = 'pancard' and cd.id=? ");

            } else if (EntityTypeForImages.VEHICLE_LICENCE.toString().equals(entityType)) {
                builder.append(
                        " docim.id as id, docim.location as location, docim.storage_type_enum as storageType from m_documents_images docim , m_customer_details cd "
                                + " where docim.entity_name = 'vehicle_licence' and cd.id = docim.customerdetails_id and cd.id=? ");

            } else if (EntityTypeForImages.GUARANTORIMAGE.toString().equalsIgnoreCase(entityType)) {
                System.out.println("CUSTOMERIMAGE-Entity");
                builder.append(
                        " docim.id as id, docim.location as location, docim.storage_type_enum as storageType from m_documents_images docim , m_customer_guarantor gd "
                                + " where gd.id = docim.guarantor_id and docim.entity_name = 'guarantorimage' and gd.id=? ");
            } else if (EntityTypeForImages.G_ADHARPHOTO.toString().equals(entityType)) {
                builder.append(
                        " docim.id as id, docim.location as location, docim.storage_type_enum as storageType  from m_documents_images docim , m_customer_guarantor gd "
                                + "   where gd.id = docim.guarantor_id  and docim.entity_name = 'g_adharphoto' and gd.id=? ");

            } else if (EntityTypeForImages.G_PANCARD.toString().equals(entityType)) {
                builder.append(
                        " docim.id as id, docim.location as location, docim.storage_type_enum as storageType from m_documents_images docim , m_customer_guarantor gd "
                                + " where gd.id = docim.guarantor_id and docim.entity_name = 'g_pancard' and gd.id=? ");

            } else if (EntityTypeForImages.G_VEHICLE_LICENCE.toString().equals(entityType)) {
                builder.append(
                        " docim.id as id, docim.location as location, docim.storage_type_enum as storageType from m_documents_images docim , m_customer_guarantor gd "
                                + " where gd.id = docim.guarantor_id and gd.id=? where docim.entity_name = g_vehicle_licence");
            } else if (EntityTypeForImages.INVOICEIMAGE.toString().equalsIgnoreCase(entityType)) {
                builder.append(
                        " docim.id as id, docim.location as location, docim.storage_type_enum as storageType from m_documents_images docim , m_apply_vehicle_loan vl "
                                + " where vl.id = docim.loan_id and docim.entity_name = 'invoiceimage' and vl.id=? ");

            } else if (EntityTypeForImages.ENGINE.toString().equals(entityType)) {
                builder.append(
                        "  vm.id as id, vm.location as location, vm.storage_type_enum as storageType  from m_vehicle_images vm , m_vehicle_details vd "
                                + "   where vd.id = vm.vehicle_id and vm.entity_name = 'engine' and vd.id= ? ");
            } else if (EntityTypeForImages.CHASSIS.toString().equals(entityType)) {
                builder.append(
                        " vm.id as id, vm.location as location, vm.storage_type_enum as storageType  from m_vehicle_images vm , m_vehicle_details vd "
                                + "   where vd.id = vm.vehicle_id and vm.entity_name = 'chassis' and vd.id= ? ");
            } else if (EntityTypeForImages.VEHICLEINSURANCE.toString().equals(entityType)) {
                builder.append(
                        " vm.id as id, vm.location as location, vm.storage_type_enum as storageType  from m_vehicle_images vm , m_vehicle_details vd "
                                + " where vd.id = vm.vehicle_id and vm.entity_name = 'vehicleinsurance' and vd.id= ? ");
            } else if (EntityTypeForImages.KMREADING.toString().equals(entityType)) {
                builder.append(
                        " vm.id as id, vm.location as location, vm.storage_type_enum as storageType  from m_vehicle_images vm , m_vehicle_details vd  "
                                + " where vd.id = vm.vehicle_id and vm.entity_name = 'kmreading' and vd.id= ? ");
            }

            else if (EntityTypeForImages.ENROLL_CUSTOMERIMAGE.toString().equals(entityType)) {
                builder.append(
                        " docim.id as id, docim.location as location, docim.storage_type_enum as storageType from m_documents_images docim , m_feenroll enr "
                                + " where enr.id = docim.feEnroll_id and docim.entity_name = 'enroll_customerimage' and enr.id=? ");
            }

            else if (EntityTypeForImages.ENROLL_ADHARPHOTO.toString().equals(entityType)) {
                builder.append(
                        " docim.id as id, docim.location as location, docim.storage_type_enum as storageType from m_documents_images docim , m_feenroll enr "
                                + " where enr.id = docim.feEnroll_id and docim.entity_name = 'enroll_adharphoto' and enr.id=? ");
            }

            else if (EntityTypeForImages.ENROLL_PANCARD.toString().equals(entityType)) {
                builder.append(
                        " docim.id as id, docim.location as location, docim.storage_type_enum as storageType from m_documents_images docim , m_feenroll enr "
                                + " where enr.id = docim.feEnroll_id and docim.entity_name = 'enroll_pancard' and enr.id=? ");
            }

            else if (EntityTypeForImages.RCBOOK.toString().equals(entityType)) {
                builder.append(
                        " vm.id as id, vm.location as location, vm.storage_type_enum as storageType  from m_vehicle_images vm , m_vehicle_details vd "
                                + " where vd.id = vm.vehicle_id and vm.entity_name = 'rcbook' and vd.id= ? ");
            } else if (EntityTypeForImages.VEHICLE_FRONT.toString().equals(entityType)) { // work needs to be done for
                                                                                          // Vehicle
                // images from different angle
                builder.append(
                        " vm.id as id, vm.location as location, vm.storage_type_enum as storageType  from m_vehicle_images vm , m_vehicle_details vd "
                                + " where vd.id = vm.vehicle_id and vm.entity_name = 'vehicle_front' and vd.id= ? ");
            } else if (EntityTypeForImages.VEHICLE_BACK.toString().equals(entityType)) { // work needs to be done for
                                                                                         // Vehicle
                // images from different angle
                builder.append(
                        " vm.id as id, vm.location as location, vm.storage_type_enum as storageType  from m_vehicle_images vm , m_vehicle_details vd "
                                + " where vd.id = vm.vehicle_id and vm.entity_name = 'vehicle_back' and vd.id= ? ");
            } else if (EntityTypeForImages.VEHICLE_RIGHT.toString().equals(entityType)) { // work needs to be done for
                                                                                          // Vehicle
                // images from different angle
                builder.append(
                        " vm.id as id, vm.location as location, vm.storage_type_enum as storageType  from m_vehicle_images vm , m_vehicle_details vd "
                                + " where vd.id = vm.vehicle_id and vm.entity_name = 'vehicle_right' and vd.id= ? ");
            } else if (EntityTypeForImages.VEHICLE_LEFT.toString().equals(entityType)) { // work needs to be done for
                                                                                         // Vehicle
                // images from different angle
                builder.append(
                        " vm.id as id, vm.location as location, vm.storage_type_enum as storageType  from m_vehicle_images vm , m_vehicle_details vd "
                                + " where vd.id = vm.vehicle_id and vm.entity_name = 'vehicle_left' and vd.id= ? ");
            }

            else if (EntityTypeForImages.BANK.toString().equals(entityType)) {
                builder.append(
                        " docim.id as id, docim.location as location, docim.storage_type_enum as storageType from m_documents_images docim , m_customer_bank_details bd "
                                + " where bd.id = docim.bank_id and docim.entity_name = 'bank' and bd.id=? ");
            }
            return builder.toString();
        }

        @Override
        public ImageData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final String location = rs.getString("location");
            final Integer storageTypeInt = JdbcSupport.getInteger(rs, "storageType");
            StorageType storageType = storageTypeInt != null ? StorageType.fromInt(storageTypeInt) : null;
            return new ImageData(location, storageType, this.entityDisplayName);
        }
    }

    @Override
    public FileData retrieveImage(String entityType, final Long entityId) {
        try {
            String displayName = null;
            if (EntityTypeForImages.CLIENTS.toString().equalsIgnoreCase(entityType)) {
                Client owner = this.clientRepositoryWrapper.findOneWithNotFoundDetection(entityId);
                displayName = owner.getDisplayName();
            } else if (EntityTypeForImages.STAFF.toString().equalsIgnoreCase(entityType)) {
                Staff owner = this.staffRepositoryWrapper.findOneWithNotFoundDetection(entityId);
                displayName = owner.displayName();
            } else if (EntityTypeForImages.INVOICEIMAGE.toString().equalsIgnoreCase(entityType)) {

                VehicleLoan owner = this.newVehicleLoanRepositoryWrapper.findOneWithNotFoundDetection(entityId);
                displayName = owner.getInvoiceNumber();

            } else if (EntityTypeForImages.PROFILEIMAGE.toString().equalsIgnoreCase(entityType)) {
                AppUser owner = this.appUserRepositoryWrapper.findOneWithNotFoundDetection(entityId);
                displayName = owner.getFirstname();

            } else if (EntityTypeForImages.CUSTOMERIMAGE.toString().equalsIgnoreCase(entityType)) {

                final CustomerDetails customerDetails = this.customerDetailsRepositoryWrapper.findOneWithNotFoundDetection(entityId);
                displayName = customerDetails.getName();
            }

            else if (EntityTypeForImages.ADHARPHOTO.toString().equals(entityType)) {
                final CustomerDetails customerDetails = this.customerDetailsRepositoryWrapper.findOneWithNotFoundDetection(entityId);
                displayName = customerDetails.getName();
            } else if (EntityTypeForImages.PANCARD.toString().equals(entityType)) {
                final CustomerDetails customerDetails = this.customerDetailsRepositoryWrapper.findOneWithNotFoundDetection(entityId);
                displayName = customerDetails.getName();
            }

            else if (EntityTypeForImages.VEHICLE_LICENCE.toString().equals(entityType)) {
                final CustomerDetails customerDetails = this.customerDetailsRepositoryWrapper.findOneWithNotFoundDetection(entityId);
                displayName = customerDetails.getName();
            } else if (EntityTypeForImages.GUARANTORIMAGE.toString().equals(entityType)) {
                CustomerGuarantor owner = this.customerGuarantorRepositoryWrapper.findOneWithNotFoundDetection(entityId);
                displayName = owner.getGuarantorName();
            } else if (EntityTypeForImages.G_PANCARD.toString().equals(entityType)) {
                CustomerGuarantor owner = this.customerGuarantorRepositoryWrapper.findOneWithNotFoundDetection(entityId);
                displayName = owner.getGuarantorName();
            } else if (EntityTypeForImages.G_ADHARPHOTO.toString().equals(entityType)) {
                CustomerGuarantor owner = this.customerGuarantorRepositoryWrapper.findOneWithNotFoundDetection(entityId);
                displayName = owner.getGuarantorName();
            } else if (EntityTypeForImages.G_VEHICLE_LICENCE.toString().equals(entityType)) {
                CustomerGuarantor owner = this.customerGuarantorRepositoryWrapper.findOneWithNotFoundDetection(entityId);
                displayName = owner.getGuarantorName();
            } else if (EntityTypeForImages.ENGINE.toString().equals(entityType)) {
                VehicleDetails vehicleDetails = this.vehicleDetailsRepositoryWrapper.findOneWithNotFoundDetection(entityId);
                displayName = vehicleDetails.getEngineNumber();
            } else if (EntityTypeForImages.CHASSIS.toString().equals(entityType)) {
                VehicleDetails vehicleDetails = this.vehicleDetailsRepositoryWrapper.findOneWithNotFoundDetection(entityId);
                displayName = vehicleDetails.getChassisNumber();
            } else if (EntityTypeForImages.VEHICLEINSURANCE.toString().equals(entityType)) {
                VehicleDetails vehicleDetails = this.vehicleDetailsRepositoryWrapper.findOneWithNotFoundDetection(entityId);
                displayName = vehicleDetails.insurancePolicyNumber();
            } else if (EntityTypeForImages.KMREADING.toString().equals(entityType)) {
                VehicleDetails vehicleDetails = this.vehicleDetailsRepositoryWrapper.findOneWithNotFoundDetection(entityId);
                displayName = vehicleDetails.getkmReading();
            } else if (EntityTypeForImages.RCBOOK.toString().equals(entityType)) {
                VehicleDetails vehicleDetails = this.vehicleDetailsRepositoryWrapper.findOneWithNotFoundDetection(entityId);
                displayName = vehicleDetails.getVehicleNumber();
            } else if (EntityTypeForImages.BANK.toString().equals(entityType)) {
                BankDetails owner = this.bankDetailsRepositoryWrapper.findOneWithNotFoundDetection(entityId);
                displayName = owner.getAccountHolderName();
            } else {
                displayName = "UnknownEntityType:" + entityType;
            }
            System.out.println("displayName: " + displayName);
            final ImageMapper imageMapper = new ImageMapper(displayName);

            // System.out.println("imageMapper: " + imageMapper);

            final String sql = "select " + imageMapper.schema(entityType);
            System.out.println("sql: " + sql);

            final ImageData imageData = this.jdbcTemplate.queryForObject(sql, imageMapper, entityId);
            System.out.println("imageData: " + imageData);
            // System.out.println("imageData: " + imageData);
            final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository(imageData.storageType());
            return contentRepository.fetchImage(imageData);
        } catch (final EmptyResultDataAccessException e) {
            throw new ImageNotFoundException("clients", entityId, e);
        }
    }
}
