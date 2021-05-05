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
    private final CustomerGuarantorRepositoryWrapper customerGuarantorRepositoryWrapper;
    private final NewVehicleLoanRepositoryWrapper newVehicleLoanRepositoryWrapper;
    private final VehicleDetailsRepositoryWrapper vehicleDetailsRepositoryWrapper;

    @Autowired
    public ImageReadPlatformServiceImpl(final RoutingDataSource dataSource, final ContentRepositoryFactory documentStoreFactory,
            final ClientRepositoryWrapper clientRepositoryWrapper, StaffRepositoryWrapper staffRepositoryWrapper,
            final AppUserRepositoryWrapper appUserRepositoryWrapper, final BankDetailsRepositoryWrapper bankDetailsRepositoryWrapper,
            final VehicleDetailsRepositoryWrapper vehicleDetailsRepositoryWrapper,
            final CustomerGuarantorRepositoryWrapper customerGuarantorRepositoryWrapper,
            final DocumentImageRepository documentImageRepository, final NewVehicleLoanRepositoryWrapper newVehicleLoanRepositoryWrapper) {
        this.staffRepositoryWrapper = staffRepositoryWrapper;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.contentRepositoryFactory = documentStoreFactory;
        this.clientRepositoryWrapper = clientRepositoryWrapper;
        this.appUserRepositoryWrapper = appUserRepositoryWrapper;
        this.bankDetailsRepositoryWrapper = bankDetailsRepositoryWrapper;
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
            StringBuilder builder = new StringBuilder(
                    "image.id as id, image.location as location, image.storage_type_enum as storageType ");
            if (EntityTypeForImages.CLIENTS.toString().equalsIgnoreCase(entityType)) {
                System.out.println("CLIENTS-Entity");
                builder.append(" from m_image image , m_client client " + " where client.image_id = image.id and client.id=?");
            } else if (EntityTypeForImages.STAFF.toString().equalsIgnoreCase(entityType)) {
                System.out.println("STAFF-Entity");
                builder.append("from m_image image , m_staff staff " + " where staff.image_id = image.id and staff.id=?");
            } else if (EntityTypeForImages.PROFILEIMAGE.toString().equalsIgnoreCase(entityType)) {
                System.out.println("profileimage");
                builder.append("from m_image image , m_appuser appuser " + " where appuser.image_id = image.id and appuser.id=?");
            } else if (EntityTypeForImages.CUSTOMERIMAGE.toString().equalsIgnoreCase(entityType)) {
                System.out.println("CUSTOMERIMAGE-Entity");
                builder.append("from m_documents_images docim , m_customer_details cd "
                        + " where cd.id = docim.customerdetails_id and cd.id=? where docim.entity_name = customerimage");
            } else if (EntityTypeForImages.ADHARPHOTO.toString().equals(entityType)) {
                builder.append("from m_documents_images docim , m_customer_details cd "
                        + " where cd.id = docim.customerdetails_id and cd.id=? where docim.entity_name = adharphoto");

            } else if (EntityTypeForImages.PANCARD.toString().equals(entityType)) {
                builder.append("from m_documents_images docim , m_customer_details cd "
                        + " where cd.id = docim.customerdetails_id and cd.id=? where docim.entity_name = pancard");

            } else if (EntityTypeForImages.VEHICLE_LICENCE.toString().equals(entityType)) {
                builder.append("from m_documents_images docim , m_customer_details cd "
                        + " where docim.entity_name = vehicle_licence and cd.id = docim.customerdetails_id and cd.id=? ");

            } else if (EntityTypeForImages.GUARANTORIMAGE.toString().equalsIgnoreCase(entityType)) {
                System.out.println("CUSTOMERIMAGE-Entity");
                builder.append("from m_documents_images docim , m_customer_guarantor gd "
                        + " where gd.id = docim.guarantor_id and gd.id=? where docim.entity_name = guarantorimage");
            } else if (EntityTypeForImages.G_ADHARPHOTO.toString().equals(entityType)) {
                builder.append("from m_documents_images docim , m_customer_guarantor gd "
                        + " where docim.entity_name = adharphoto and gd.id = docim.guarantor_id and gd.id=? ");

            } else if (EntityTypeForImages.G_PANCARD.toString().equals(entityType)) {
                builder.append("from m_documents_images docim , m_customer_guarantor gd "
                        + " where gd.id = docim.guarantor_id and gd.id=? where docim.entity_name = pancard");

            } else if (EntityTypeForImages.G_VEHICLE_LICENCE.toString().equals(entityType)) {
                builder.append("from m_documents_images docim , m_customer_guarantor gd "
                        + " where gd.id = docim.guarantor_id and gd.id=? where docim.entity_name = vehicle_licence");
            } else if (EntityTypeForImages.INVOICEIMAGE.toString().equalsIgnoreCase(entityType)) {
                builder.append("from m_documents_images docim , m_apply_vehicle_loan vl "
                        + " where vl.id = docim.loan_id and vl.id=? where docim.entity_name = invoiceimage ");

            } else if (EntityTypeForImages.ENGINE.toString().equals(entityType)) {
                builder.append(" from m_vehicle_images vm , m_vehicle_details vd "
                        + " where vd.id = vm.vehicle_id and vd.id=? where docim.entity_name = engine ");
            } else if (EntityTypeForImages.CHASSIS.toString().equals(entityType)) {
                builder.append("from m_vehicle_images vm , m_vehicle_details vd "
                        + " where vd.id = vm.vehicle_id and vd.id=? where docim.entity_name = chassis ");
            } else if (EntityTypeForImages.VEHICLEINSURANCE.toString().equals(entityType)) {
                builder.append("from m_vehicle_images vm , m_vehicle_details vd "
                        + " where vd.id = vm.vehicle_id and vd.id=? where docim.entity_name = vehicleinsurance ");
            } else if (EntityTypeForImages.KMREADING.toString().equals(entityType)) {
                builder.append("from m_vehicle_images vm , m_vehicle_details vd "
                        + " where vd.id = vm.vehicle_id and vd.id=? where docim.entity_name = kmreading ");
            }

            else if (EntityTypeForImages.RCBOOK.toString().equals(entityType)) {
                builder.append("from m_vehicle_images vm , m_vehicle_details vd "
                        + " where vd.id = vm.vehicle_id and vd.id=? where docim.entity_name = rcbook ");
            } else if (EntityTypeForImages.VEHICLE.toString().equals(entityType)) {
                builder.append("from m_image image , m_appuser appuser " + " where appuser.image_id = image.id and appuser.id=?");
            }

            else if (EntityTypeForImages.BANK.toString().equals(entityType)) {
                builder.append("from m_documents_images docim , m_customer_bank_details bd "
                        + " where bd.id = docim.bank_id and bd.id=? where docim.entity_name = bank ");
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
            } else if (EntityTypeForImages.PROFILEIMAGE.toString().equalsIgnoreCase(entityType)) {
                AppUser owner = this.appUserRepositoryWrapper.findOneWithNotFoundDetection(entityId);
                displayName = owner.getFirstname();
            } else if (EntityTypeForImages.ADHARPHOTO.toString().equals(entityType)
                    || EntityTypeForImages.CUSTOMERIMAGE.toString().equals(entityType)
                    || EntityTypeForImages.INVOICEIMAGE.toString().equalsIgnoreCase(entityType)) {
                VehicleLoan owner = this.newVehicleLoanRepositoryWrapper.findOneWithNotFoundDetection(entityId);
                displayName = owner.getName();
            } else if (EntityTypeForImages.ENGINE.toString().equals(entityType) || EntityTypeForImages.CHASSIS.toString().equals(entityType)
                    || EntityTypeForImages.VEHICLE.toString().equals(entityType)) {
                VehicleDetails vehicleDetails = this.vehicleDetailsRepositoryWrapper.findOneWithNotFoundDetection(entityId);
                // displayName = owner.getFirstname();
            } else if (EntityTypeForImages.GUARANTORIMAGE.toString().equals(entityType)) {
                CustomerGuarantor owner = this.customerGuarantorRepositoryWrapper.findOneWithNotFoundDetection(entityId);
                displayName = owner.getGuarantorName();
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
            // System.out.println("imageData: " + imageData);
            final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository(imageData.storageType());
            return contentRepository.fetchImage(imageData);
        } catch (final EmptyResultDataAccessException e) {
            throw new ImageNotFoundException("clients", entityId, e);
        }
    }
}
