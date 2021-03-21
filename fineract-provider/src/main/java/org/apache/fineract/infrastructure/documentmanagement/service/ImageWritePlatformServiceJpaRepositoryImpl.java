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

import java.io.InputStream;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.domain.Base64EncodedImage;
import org.apache.fineract.infrastructure.documentmanagement.api.ImagesApiResource.EntityTypeForImages;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepository;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepositoryFactory;
import org.apache.fineract.infrastructure.documentmanagement.domain.Image;
import org.apache.fineract.infrastructure.documentmanagement.domain.ImageRepository;
import org.apache.fineract.infrastructure.documentmanagement.domain.StorageType;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.organisation.staff.domain.StaffRepositoryWrapper;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.NewVehicleLoan;
import org.apache.fineract.portfolio.loanaccount.domain.NewVehicleLoanRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImageWritePlatformServiceJpaRepositoryImpl implements ImageWritePlatformService {

    private final ContentRepositoryFactory contentRepositoryFactory;
    private final ClientRepositoryWrapper clientRepositoryWrapper;
    private final ImageRepository imageRepository;
    private final StaffRepositoryWrapper staffRepositoryWrapper;
    private final NewVehicleLoanRepositoryWrapper newVehicleLoanRepositoryWrapper;

    @Autowired
    public ImageWritePlatformServiceJpaRepositoryImpl(final ContentRepositoryFactory documentStoreFactory,
            final ClientRepositoryWrapper clientRepositoryWrapper, final ImageRepository imageRepository,
            StaffRepositoryWrapper staffRepositoryWrapper, final NewVehicleLoanRepositoryWrapper newVehicleLoanRepositoryWrapper) {
        this.contentRepositoryFactory = documentStoreFactory;
        this.clientRepositoryWrapper = clientRepositoryWrapper;
        this.imageRepository = imageRepository;
        this.staffRepositoryWrapper = staffRepositoryWrapper;
        this.newVehicleLoanRepositoryWrapper = newVehicleLoanRepositoryWrapper;
    }

    @Transactional
    @Override
    public CommandProcessingResult saveOrUpdateImage(String entityName, final Long LoanApplicationId, final String imageName,
            final InputStream inputStream, final Long fileSize) {
        Object owner = deletePreviousImage(entityName, LoanApplicationId);

        System.out.println("--image testing 1 --");
        final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository();
        final String imageLocation = contentRepository.saveImage(inputStream, LoanApplicationId, imageName, fileSize);
        return updateImage(owner, imageLocation, contentRepository.getStorageType());
    }

    @Transactional
    @Override
    public CommandProcessingResult saveOrUpdateImage(String entityName, final Long clientId, final Base64EncodedImage encodedImage) {
        Object owner = deletePreviousImage(entityName, clientId);

        System.out.println("--image testing 2 --");
        final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository();
        final String imageLocation = contentRepository.saveImage(encodedImage, clientId, "image");

        return updateImage(owner, imageLocation, contentRepository.getStorageType());
    }

    @Transactional
    @Override
    public CommandProcessingResult deleteImage(String entityName, final Long clientId) {
        Object owner = null;
        Image image = null;
        if (EntityTypeForImages.CLIENTS.toString().equals(entityName)) {
            owner = this.clientRepositoryWrapper.findOneWithNotFoundDetection(clientId);
            Client client = (Client) owner;
            image = client.getImage();
            client.setImage(null);
            this.clientRepositoryWrapper.save(client);

        } else if (EntityTypeForImages.LoanApplication.toString().equals(entityName)) {
            System.out.println("--deleteImage--");
            owner = this.newVehicleLoanRepositoryWrapper.findOneWithNotFoundDetection(clientId);
            NewVehicleLoan newVehicleLoan = (NewVehicleLoan) owner;
            image = newVehicleLoan.getImage();
            newVehicleLoan.setImage(null);
            this.newVehicleLoanRepositoryWrapper.save(newVehicleLoan);

        } else if (EntityTypeForImages.STAFF.toString().equals(entityName)) {
            owner = this.staffRepositoryWrapper.findOneWithNotFoundDetection(clientId);
            Staff staff = (Staff) owner;
            image = staff.getImage();
            staff.setImage(null);
            this.staffRepositoryWrapper.save(staff);

        }
        // delete image from the file system
        if (image != null) {
            final ContentRepository contentRepository = this.contentRepositoryFactory
                    .getRepository(StorageType.fromInt(image.getStorageType()));
            contentRepository.deleteImage(image.getLocation());
            this.imageRepository.delete(image);
        }

        return new CommandProcessingResult(clientId);
    }

    /**
     * @param entityName
     * @param entityId
     * @return
     */
    private Object deletePreviousImage(String entityName, final Long entityId) {
        Object owner = null;
        Image image = null;
        if (EntityTypeForImages.CLIENTS.toString().equals(entityName)) {
            Client client = this.clientRepositoryWrapper.findOneWithNotFoundDetection(entityId);
            image = client.getImage();
            owner = client;
        } else if (EntityTypeForImages.LoanApplication.toString().equals(entityName)) {
            System.out.println("--deletePreviousImage--");
            NewVehicleLoan newVehicleLoan = this.newVehicleLoanRepositoryWrapper.findOneWithNotFoundDetection(entityId);
            image = newVehicleLoan.getImage();
            owner = newVehicleLoan;
        } else if (EntityTypeForImages.STAFF.toString().equals(entityName)) {
            Staff staff = this.staffRepositoryWrapper.findOneWithNotFoundDetection(entityId);
            image = staff.getImage();
            owner = staff;
        }
        if (image != null) {
            final ContentRepository contentRepository = this.contentRepositoryFactory
                    .getRepository(StorageType.fromInt(image.getStorageType()));
            contentRepository.deleteImage(image.getLocation());
        }
        return owner;
    }

    private CommandProcessingResult updateImage(final Object owner, final String imageLocation, final StorageType storageType) {
        Image image = null;
        Long clientId = null;
        if (owner instanceof Client) {
            Client client = (Client) owner;
            image = client.getImage();
            clientId = client.getId();
            image = createImage(image, imageLocation, storageType);
            client.setImage(image);
            this.clientRepositoryWrapper.save(client);
        } else if (owner instanceof NewVehicleLoan) {
            System.out.println("--updateImage--");
            NewVehicleLoan newVehicleLoan = (NewVehicleLoan) owner;
            image = newVehicleLoan.getImage();
            clientId = newVehicleLoan.getId();
            image = createImage(image, imageLocation, storageType);
            newVehicleLoan.setImage(image);
            this.newVehicleLoanRepositoryWrapper.save(newVehicleLoan);
        } else if (owner instanceof Staff) {
            Staff staff = (Staff) owner;
            image = staff.getImage();
            clientId = staff.getId();
            image = createImage(image, imageLocation, storageType);
            staff.setImage(image);
            this.staffRepositoryWrapper.save(staff);
        }

        this.imageRepository.save(image);
        return new CommandProcessingResult(clientId);
    }

    private Image createImage(Image image, final String imageLocation, final StorageType storageType) {
        if (image == null) {
            image = new Image(imageLocation, storageType);
        } else {
            image.setLocation(imageLocation);
            image.setStorageType(storageType.getValue());
        }
        return image;
    }

}
