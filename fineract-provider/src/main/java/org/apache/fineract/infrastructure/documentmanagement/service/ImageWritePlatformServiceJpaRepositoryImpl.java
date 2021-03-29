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
import org.apache.fineract.infrastructure.documentmanagement.domain.DocumentImageRepository;
import org.apache.fineract.infrastructure.documentmanagement.domain.DocumentImages;
import org.apache.fineract.infrastructure.documentmanagement.domain.Image;
import org.apache.fineract.infrastructure.documentmanagement.domain.ImageRepository;
import org.apache.fineract.infrastructure.documentmanagement.domain.StorageType;
import org.apache.fineract.infrastructure.documentmanagement.domain.VehicleImageRepository;
import org.apache.fineract.infrastructure.documentmanagement.domain.VehicleImages;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.organisation.staff.domain.StaffRepositoryWrapper;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.BankDetails;
import org.apache.fineract.portfolio.loanaccount.domain.BankDetailsRepository;
import org.apache.fineract.portfolio.loanaccount.domain.BankDetailsRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.CustomerGuarantor;
import org.apache.fineract.portfolio.loanaccount.domain.CustomerGuarantorRepository;
import org.apache.fineract.portfolio.loanaccount.domain.CustomerGuarantorRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.NewVehicleLoan;
import org.apache.fineract.portfolio.loanaccount.domain.NewVehicleLoanRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.UsedVehicleLoan;
import org.apache.fineract.portfolio.loanaccount.domain.UsedVehicleLoanRepository;
import org.apache.fineract.portfolio.loanaccount.domain.UsedVehicleLoanRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.VehicleDetails;
import org.apache.fineract.portfolio.loanaccount.domain.VehicleDetailsRepository;
import org.apache.fineract.portfolio.loanaccount.domain.VehicleDetailsRepositoryWrapper;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.useradministration.domain.AppUserRepository;
import org.apache.fineract.useradministration.domain.AppUserRepositoryWrapper;
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
    private final VehicleDetailsRepositoryWrapper vehicleDetailsRepositoryWrapper;
    private final UsedVehicleLoanRepository usedVehicleLoanRepository;
    private final VehicleDetailsRepository vehicleDetailsRepository;
    private final CustomerGuarantorRepository customerGuarantorRepository;
    private final BankDetailsRepository bankDetailsRepository;
    private final VehicleImageRepository vehicleImageRepository;
    private final DocumentImageRepository documentImageRepository;
    private final BankDetailsRepositoryWrapper bankDetailsRepositoryWrapper;
    private final CustomerGuarantorRepositoryWrapper customerGuarantorRepositoryWrapper;
    private final UsedVehicleLoanRepositoryWrapper usedVehicleLoanRepositoryWrapper;
    private final AppUserRepositoryWrapper appUserRepositoryWrapper;
    private final AppUserRepository appUserRepository;

    @Autowired
    public ImageWritePlatformServiceJpaRepositoryImpl(final ContentRepositoryFactory documentStoreFactory,
            final ClientRepositoryWrapper clientRepositoryWrapper, final ImageRepository imageRepository,
            StaffRepositoryWrapper staffRepositoryWrapper, final NewVehicleLoanRepositoryWrapper newVehicleLoanRepositoryWrapper,
            final UsedVehicleLoanRepository usedVehicleLoanRepository, final VehicleDetailsRepository vehicleDetailsRepository,
            final VehicleImageRepository vehicleImageRepository, DocumentImageRepository documentImageRepository,
            final CustomerGuarantorRepository customerGuarantorRepository, final BankDetailsRepository bankDetailsRepository,
            final BankDetailsRepositoryWrapper bankDetailsRepositoryWrapper,
            final VehicleDetailsRepositoryWrapper vehicleDetailsRepositoryWrapper,
            final CustomerGuarantorRepositoryWrapper customerGuarantorRepositoryWrapper,
            final UsedVehicleLoanRepositoryWrapper usedVehicleLoanRepositoryWrapper,
            final AppUserRepositoryWrapper appUserRepositoryWrapper, final AppUserRepository appUserRepository) {
        this.contentRepositoryFactory = documentStoreFactory;
        this.clientRepositoryWrapper = clientRepositoryWrapper;
        this.imageRepository = imageRepository;
        this.staffRepositoryWrapper = staffRepositoryWrapper;
        this.newVehicleLoanRepositoryWrapper = newVehicleLoanRepositoryWrapper;
        this.usedVehicleLoanRepository = usedVehicleLoanRepository;
        this.vehicleDetailsRepository = vehicleDetailsRepository;
        this.vehicleImageRepository = vehicleImageRepository;
        this.documentImageRepository = documentImageRepository;
        this.customerGuarantorRepository = customerGuarantorRepository;
        this.bankDetailsRepository = bankDetailsRepository;
        this.bankDetailsRepositoryWrapper = bankDetailsRepositoryWrapper;
        this.vehicleDetailsRepositoryWrapper = vehicleDetailsRepositoryWrapper;
        this.customerGuarantorRepositoryWrapper = customerGuarantorRepositoryWrapper;
        this.usedVehicleLoanRepositoryWrapper = usedVehicleLoanRepositoryWrapper;
        this.appUserRepositoryWrapper = appUserRepositoryWrapper;
        this.appUserRepository = appUserRepository;

    }

    @Transactional
    @Override
    public CommandProcessingResult saveOrUpdateImage(String entityName, final Long formId, final String imageName,
            final InputStream inputStream, final Long fileSize) {

        System.out.println("--image testing 1 --");

        Object owner = deletePreviousImage(entityName, formId);

        final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository();
        final String imageLocation = contentRepository.saveImage(inputStream, formId, imageName, fileSize); // it stores
                                                                                                            // the image
                                                                                                            // and get
                                                                                                            // the
                                                                                                            // location
        return updateImage(entityName, owner, imageLocation, contentRepository.getStorageType());
    }

    @Transactional
    @Override
    public CommandProcessingResult saveOrUpdateImage(String entityName, final Long clientId, final Base64EncodedImage encodedImage) {
        Object owner = deletePreviousImage(entityName, clientId);

        System.out.println("--image testing 2 --");
        final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository();
        final String imageLocation = contentRepository.saveImage(encodedImage, clientId, "image");
        System.out.println("--imageLocation--" + imageLocation);
        return updateImage(entityName, owner, imageLocation, contentRepository.getStorageType());
    }

    @Transactional
    @Override
    public CommandProcessingResult deleteImage(String entityName, final Long clientId) {
        Object owner = null;
        Image image = null; // create 2 table document vehicle
        if (EntityTypeForImages.CLIENTS.toString().equals(entityName)) {
            owner = this.clientRepositoryWrapper.findOneWithNotFoundDetection(clientId);
            Client client = (Client) owner;
            image = client.getImage();
            client.setImage(null);
            this.clientRepositoryWrapper.save(client);

        } /*
           * else if (EntityTypeForImages.NEWVEHICLE.toString().equals(entityName)) {
           * System.out.println("--deleteImage--"); owner =
           * this.newVehicleLoanRepositoryWrapper.findOneWithNotFoundDetection(clientId); NewVehicleLoan newVehicleLoan
           * = (NewVehicleLoan) owner; image = newVehicleLoan.getInvoiceImage(); newVehicleLoan.setImage(null);
           * this.newVehicleLoanRepositoryWrapper.save(newVehicleLoan);
           *
           * }
           */ else if (EntityTypeForImages.STAFF.toString().equals(entityName)) {
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
        DocumentImages documentImage = null;
        VehicleImages vehicleImage = null;
        // CustomerImage, AdharPhoto, GovermentDocument, InvoiceImage, Engine, Chassis, Vehicle, GuarantorImage,
        // GuarantorDocument, Bank
        System.out.println("Object creation");
        if (EntityTypeForImages.PROFILEIMAGE.toString().equals(entityName)) {
            // if (EntityTypeForImages.USEDVEHICLE.toString().equals(entityName)) {
            System.out.println("--PROFILEIMAGE--");
            AppUser appuser = this.appUserRepositoryWrapper.findOneWithNotFoundDetection(entityId);
            // image = usedVehicleLoan.getCustomerImage();
            owner = appuser;
            // }
        }
        // if (entityName.equals("CustomerImage")) {
        if (EntityTypeForImages.CUSTOMERIMAGE.toString().equals(entityName)) {
            // if (EntityTypeForImages.USEDVEHICLE.toString().equals(entityName)) {
            System.out.println("--CustomerImage--");
            UsedVehicleLoan usedVehicleLoan = this.usedVehicleLoanRepositoryWrapper.findOneWithNotFoundDetection(entityId);
            // image = usedVehicleLoan.getCustomerImage();
            owner = usedVehicleLoan;
            // }
        }
        // if (entityName.equals("AdharPhoto") || entityName.equals("GovernmentDocument")) {
        if (EntityTypeForImages.ADHARPHOTO.toString().equals(entityName)
                || EntityTypeForImages.GOVERNMENTDOCUMENT.toString().equals(entityName)) {
            // if (EntityTypeForImages.USEDVEHICLE.toString().equals(entityName)) {
            System.out.println("--AdharPhoto GovernmentDocument--");
            UsedVehicleLoan usedVehicleLoan = this.usedVehicleLoanRepositoryWrapper.findOneWithNotFoundDetection(entityId);
            // documentImage = usedVehicleLoan.getDocumentImage();
            owner = usedVehicleLoan;
            // }
        }
        if (EntityTypeForImages.NEWVEHICLE.toString().equals(entityName)) {
            // if (EntityTypeForImages.NEWVEHICLE.toString().equals(entityName)) {
            System.out.println("--InvoiceImage--");
            NewVehicleLoan newVehicleLoan = this.newVehicleLoanRepositoryWrapper.findOneWithNotFoundDetection(entityId);
            // documentImage = newVehicleLoan.getInvoiceImage();
            owner = newVehicleLoan;
            // }
        }
        // if (entityName.equals("Engine") || entityName.equals("Chassis") || entityName.equals("Vehicle")) {
        if (EntityTypeForImages.ENGINE.toString().equals(entityName) || EntityTypeForImages.CHASSIS.toString().equals(entityName)
                || EntityTypeForImages.VEHICLE.toString().equals(entityName)) {
            // else if (EntityTypeForImages.VEHICLEDETAILS.toString().equals(entityName)) {
            System.out.println("--Engine--");
            VehicleDetails vehicleDetails = this.vehicleDetailsRepositoryWrapper.findOneWithNotFoundDetection(entityId);
            // vehicleImage = vehicleDetails.getImage();
            owner = vehicleDetails;
            // }
        }
        // if (entityName.equals("GuarantorImage") || entityName.equals("GuarantorDocument")) {
        if (EntityTypeForImages.GUARANTORIMAGE.toString().equals(entityName)
                || EntityTypeForImages.GUARANTORDOCUMENT.toString().equals(entityName)) {
            // else if (EntityTypeForImages.GUARANTOR.toString().equals(entityName)) {
            System.out.println("--GUARANTOR--");
            CustomerGuarantor customerGuarantor = this.customerGuarantorRepositoryWrapper.findOneWithNotFoundDetection(entityId);
            // image = customerGuarantor.getGuarantorImage();
            owner = customerGuarantor;
            // }
        } else if (EntityTypeForImages.BANK.toString().equals(entityName)) {
            System.out.println("--BANK--");
            BankDetails bankDetails = this.bankDetailsRepositoryWrapper.findOneWithNotFoundDetection(entityId);
            // documentImage = bankDetails.getImage();
            owner = bankDetails;
        }

        if (EntityTypeForImages.CLIENTS.toString().equals(entityName)) {
            Client client = this.clientRepositoryWrapper.findOneWithNotFoundDetection(entityId);
            // image = client.getImage();
            owner = client;
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

    private CommandProcessingResult updateImage(String entityName, final Object owner, final String imageLocation,
            final StorageType storageType) {
        Image image = null; // NewVehicleLoan, UsedVehicleLoan, VehicleDetails, CustomerGuarantor, BankDetails
        DocumentImages documentImage = null;
        VehicleImages vehicleImage = null;
        Long clientId = null;
        if (owner instanceof Client) {
            System.out.println("--NewVehicleLoan--");
            Client client = (Client) owner;
            image = client.getImage();
            clientId = client.getId();
            image = createImage(image, imageLocation, storageType, null, null, null);
            client.setImage(image);
            this.clientRepositoryWrapper.save(client);
        } else if (owner instanceof AppUser) { // create 3 section same as database documents, images, vehicles
            System.out.println("--AppUser--");
            AppUser appUser = (AppUser) owner;

            // documentImage = newVehicleLoan.getInvoiceImage();
            clientId = appUser.getId(); // get the id of the table to make change in the specific table
            // documentImage = createDocumentImage(documentImage, imageLocation, storageType, null, null,
            // newVehicleLoan, null);
            image = createImage(image, imageLocation, storageType, null, null, appUser);
            this.imageRepository.save(image);
            // newVehicleLoan.setImage(documentImage);
            // newVehicleLoan.setInvoiceImage(documentImage);
            this.appUserRepository.save(appUser);
        } else if (owner instanceof NewVehicleLoan) { // create 3 section same as database documents, images, vehicles
            System.out.println("--NewVehicleLoan--");
            NewVehicleLoan newVehicleLoan = (NewVehicleLoan) owner;

            // documentImage = newVehicleLoan.getInvoiceImage();
            clientId = newVehicleLoan.getId(); // get the id of the table to make change in the specific table
            documentImage = createDocumentImage(documentImage, imageLocation, storageType, null, null, newVehicleLoan, null);
            this.documentImageRepository.saveAndFlush(documentImage);
            // newVehicleLoan.setImage(documentImage);
            // newVehicleLoan.setInvoiceImage(documentImage);
            this.newVehicleLoanRepositoryWrapper.save(newVehicleLoan);
        } else if (owner instanceof UsedVehicleLoan) {
            System.out.println("--UsedVehicleLoan--");
            UsedVehicleLoan usedVehicleLoan = (UsedVehicleLoan) owner;
            clientId = usedVehicleLoan.getId();
            if (entityName.equals("CustomerImage")) {
                System.out.println("--CustomerImage--");
                // image = usedVehicleLoan.getCustomerImage(); // usedvehicle has customerimage
                image = createImage(image, imageLocation, storageType, usedVehicleLoan, null, null);
                // usedVehicleLoan.setCustomerImage(image);
                this.usedVehicleLoanRepository.save(usedVehicleLoan);
            } else {// usedvehicle has document
                // documentImage = usedVehicleLoan.getDocumentImage();
                documentImage = createDocumentImage(documentImage, imageLocation, storageType, null, null, null, usedVehicleLoan);
                this.documentImageRepository.saveAndFlush(documentImage);
                // usedVehicleLoan.setDocumentImage(documentImage);
                this.usedVehicleLoanRepository.save(usedVehicleLoan);
            }
        } else if (owner instanceof VehicleDetails) {
            System.out.println("--VehicleDetails--");
            VehicleDetails vehicleDetails = (VehicleDetails) owner;
            // vehicleImage = vehicleDetails.getImage();
            clientId = vehicleDetails.getId();
            vehicleImage = createVehicleImage(vehicleImage, imageLocation, storageType, vehicleDetails);
            // vehicleDetails.setImage(vehicleImage);
            this.vehicleImageRepository.save(vehicleImage);
            this.vehicleDetailsRepository.save(vehicleDetails);
        } else if (owner instanceof CustomerGuarantor) {
            System.out.println("--CustomerGuarantor--");
            CustomerGuarantor customerGuarantor = (CustomerGuarantor) owner;
            clientId = customerGuarantor.getId(); // if guarantor has image and doucme

            if (entityName.equals("GuarantorImage")) {
                System.out.println("--GuarantorImage--");
                // image = customerGuarantor.getGuarantorImage();
                image = createImage(image, imageLocation, storageType, null, customerGuarantor, null);
                // customerGuarantor.setGuarantorImage(image);
                this.imageRepository.save(image);
                this.customerGuarantorRepository.save(customerGuarantor);
            } else { // storing guarantor documents
                // documentImage = customerGuarantor.getDocumentImage();
                System.out.println("--NotGuarantorImage--");
                documentImage = createDocumentImage(documentImage, imageLocation, storageType, null, customerGuarantor, null, null);
                // customerGuarantor.setDocumentImage(documentImage);
                this.documentImageRepository.saveAndFlush(documentImage);
                this.customerGuarantorRepository.save(customerGuarantor);
            }
        } else if (owner instanceof BankDetails) {
            System.out.println("--Bankdetails--");
            BankDetails bankDetails = (BankDetails) owner;
            // documentImage = bankDetails.getImage();
            clientId = bankDetails.getId();
            System.out.println("--clientId--:" + clientId);
            documentImage = createDocumentImage(documentImage, imageLocation, storageType, bankDetails, null, null, null);
            // bankDetails.setImage(documentImage);
            System.out.println("--setImage--" + bankDetails);
            this.documentImageRepository.saveAndFlush(documentImage);
            System.out.println("--documentImageRepository--" + bankDetails);
            this.bankDetailsRepository.saveAndFlush(bankDetails);
            System.out.println("--bankDetailsRepository--");

        } else if (owner instanceof Staff) {
            Staff staff = (Staff) owner;
            image = staff.getImage();
            clientId = staff.getId();
            image = createImage(image, imageLocation, storageType, null, null, null);
            staff.setImage(image);
            this.staffRepositoryWrapper.save(staff);
        }

        return new CommandProcessingResult(clientId);
    }

    private Image createImage(Image image, final String imageLocation, final StorageType storageType, final UsedVehicleLoan usedVehicle,
            final CustomerGuarantor customerGuarantor, final AppUser appUser) {
        if (image == null) {
            image = new Image(imageLocation, storageType, usedVehicle, customerGuarantor, appUser);
        } else {
            image.setLocation(imageLocation);
            image.setStorageType(storageType.getValue());
        }
        return image;
    }

    private DocumentImages createDocumentImage(DocumentImages documentImage, final String imageLocation, final StorageType storageType,
            final BankDetails bankDetails, final CustomerGuarantor customerGuarantor, final NewVehicleLoan newVehicleImage,
            final UsedVehicleLoan usedVehicleImage) {
        if (documentImage == null) {
            documentImage = new DocumentImages(imageLocation, storageType, bankDetails, customerGuarantor, newVehicleImage,
                    usedVehicleImage);
        } else {
            documentImage.setLocation(imageLocation);
            documentImage.setStorageType(storageType.getValue());
        }
        return documentImage;
    }

    private VehicleImages createVehicleImage(VehicleImages vehicleImages, final String imageLocation, final StorageType storageType,
            final VehicleDetails vehicleDetails) {
        if (vehicleImages == null) {
            vehicleImages = new VehicleImages(imageLocation, storageType, vehicleDetails);
        } else {
            vehicleImages.setLocation(imageLocation);
            vehicleImages.setStorageType(storageType.getValue());
        }
        return vehicleImages;
    }

}
