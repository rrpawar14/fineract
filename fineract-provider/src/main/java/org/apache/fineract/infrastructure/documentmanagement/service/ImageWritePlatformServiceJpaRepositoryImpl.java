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
import org.apache.fineract.portfolio.loanaccount.domain.NewVehicleLoanRepository;
import org.apache.fineract.portfolio.loanaccount.domain.NewVehicleLoanRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.VehicleDetails;
import org.apache.fineract.portfolio.loanaccount.domain.VehicleDetailsRepository;
import org.apache.fineract.portfolio.loanaccount.domain.VehicleDetailsRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.VehicleLoan;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.useradministration.domain.AppUserRepository;
import org.apache.fineract.useradministration.domain.AppUserRepositoryWrapper;
import org.apache.fineract.vlms.branchmodule.domain.Employee;
import org.apache.fineract.vlms.branchmodule.domain.EmployeeRepository;
import org.apache.fineract.vlms.branchmodule.domain.EmployeeRepositoryWrapper;
import org.apache.fineract.vlms.customer.domain.BankDetails;
import org.apache.fineract.vlms.customer.domain.BankDetailsRepository;
import org.apache.fineract.vlms.customer.domain.BankDetailsRepositoryWrapper;
import org.apache.fineract.vlms.customer.domain.CustomerDetails;
import org.apache.fineract.vlms.customer.domain.CustomerDetailsRepository;
import org.apache.fineract.vlms.customer.domain.CustomerGuarantor;
import org.apache.fineract.vlms.customer.domain.CustomerGuarantorRepository;
import org.apache.fineract.vlms.customer.domain.CustomerGuarantorRepositoryWrapper;
import org.apache.fineract.vlms.fieldexecutive.domain.FEEnroll;
import org.apache.fineract.vlms.fieldexecutive.domain.FEEnrollRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.FEEnrollRepositoryWrapper;
import org.apache.fineract.vlms.fieldexecutive.domain.NewLoanRepository;
import org.apache.fineract.vlms.fieldexecutive.domain.NewLoanRepositoryWrapper;
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
    private final NewVehicleLoanRepository usedVehicleLoanRepository;
    private final VehicleDetailsRepository vehicleDetailsRepository;
    private final CustomerDetailsRepository customerDetailsRepository;
    private final CustomerGuarantorRepository customerGuarantorRepository;
    private final BankDetailsRepository bankDetailsRepository;
    private final VehicleImageRepository vehicleImageRepository;
    private final DocumentImageRepository documentImageRepository;
    private final BankDetailsRepositoryWrapper bankDetailsRepositoryWrapper;
    private final FEEnrollRepositoryWrapper feEnrollRepositoryWrapper;
    private final FEEnrollRepository feEnrollRepository;
    private final CustomerGuarantorRepositoryWrapper customerGuarantorRepositoryWrapper;
    private final NewVehicleLoanRepositoryWrapper usedVehicleLoanRepositoryWrapper;
    private final AppUserRepositoryWrapper appUserRepositoryWrapper;
    private final AppUserRepository appUserRepository;
    private final NewLoanRepository newLoanRepository;
    private final NewLoanRepositoryWrapper newLoanRepositoryWrapper;
    private final EmployeeRepository employeeRepository;
    private final EmployeeRepositoryWrapper employeeRepositoryWrapper;

    @Autowired
    public ImageWritePlatformServiceJpaRepositoryImpl(final ContentRepositoryFactory documentStoreFactory,
            final ClientRepositoryWrapper clientRepositoryWrapper, final ImageRepository imageRepository,
            StaffRepositoryWrapper staffRepositoryWrapper, final NewVehicleLoanRepositoryWrapper newVehicleLoanRepositoryWrapper,
            final NewVehicleLoanRepository usedVehicleLoanRepository, final VehicleDetailsRepository vehicleDetailsRepository,
            final CustomerDetailsRepository customerDetailsRepository, final VehicleImageRepository vehicleImageRepository,
            DocumentImageRepository documentImageRepository, final CustomerGuarantorRepository customerGuarantorRepository,
            final BankDetailsRepository bankDetailsRepository, final BankDetailsRepositoryWrapper bankDetailsRepositoryWrapper,
            final VehicleDetailsRepositoryWrapper vehicleDetailsRepositoryWrapper,
            final CustomerGuarantorRepositoryWrapper customerGuarantorRepositoryWrapper,
            final NewVehicleLoanRepositoryWrapper usedVehicleLoanRepositoryWrapper, final AppUserRepositoryWrapper appUserRepositoryWrapper,
            final AppUserRepository appUserRepository, final FEEnrollRepositoryWrapper feEnrollRepositoryWrapper,
            final FEEnrollRepository feEnrollRepository, final NewLoanRepository newLoanRepository,
            final NewLoanRepositoryWrapper newLoanRepositoryWrapper, final EmployeeRepository employeeRepository,
            final EmployeeRepositoryWrapper employeeRepositoryWrapper) {
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
        this.customerDetailsRepository = customerDetailsRepository;
        this.vehicleDetailsRepositoryWrapper = vehicleDetailsRepositoryWrapper;
        this.customerGuarantorRepositoryWrapper = customerGuarantorRepositoryWrapper;
        this.usedVehicleLoanRepositoryWrapper = usedVehicleLoanRepositoryWrapper;
        this.appUserRepositoryWrapper = appUserRepositoryWrapper;
        this.appUserRepository = appUserRepository;
        this.feEnrollRepositoryWrapper = feEnrollRepositoryWrapper;
        this.feEnrollRepository = feEnrollRepository;
        this.newLoanRepository = newLoanRepository;
        this.newLoanRepositoryWrapper = newLoanRepositoryWrapper;
        this.employeeRepository = employeeRepository;
        this.employeeRepositoryWrapper = employeeRepositoryWrapper;
    }

    @Transactional
    @Override
    public CommandProcessingResult saveOrUpdateImage(String entityName, final Long formId, String documentNumber, final String imageName,
            final InputStream inputStream, final Long fileSize) {

        Object owner = deletePreviousImage(entityName, formId);

        final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository();

        final String imageLocation = contentRepository.saveImage(inputStream, formId, imageName, fileSize); // it stores
        // the image
        // and get
        // the
        // location
        return updateImage(entityName, owner, imageLocation, documentNumber, contentRepository.getStorageType());
    }

    @Transactional
    @Override
    public CommandProcessingResult saveOrUpdateImage(String entityName, final Long clientId, String documentNumber,
            final Base64EncodedImage encodedImage) {
        Object owner = deletePreviousImage(entityName, clientId);

        final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository();
        final String imageLocation = contentRepository.saveImage(encodedImage, clientId, "image");

        return updateImage(entityName, owner, imageLocation, documentNumber, contentRepository.getStorageType());
    }

    @Transactional
    @Override
    public CommandProcessingResult deleteImage(String entityName, final Long clientId) {
        Object owner = null;
        Image image = null; // create 2 table document vehicle
        DocumentImages documentImage = null;
        VehicleImages vehicleImage = null;

        if (EntityTypeForImages.CLIENTS.toString().equals(entityName)) {
            owner = this.clientRepositoryWrapper.findOneWithNotFoundDetection(clientId);
            Client client = (Client) owner;
            image = client.getImage();
            client.setImage(null);
            this.clientRepositoryWrapper.save(client);

        } else if (EntityTypeForImages.ADHARPHOTO.toString().equals(entityName) || EntityTypeForImages.PANCARD.toString().equals(entityName)
                || EntityTypeForImages.VEHICLE_LICENCE.toString().equals(entityName)
                || EntityTypeForImages.FRONT_ADHARCARD.toString().equals(entityName)
                || EntityTypeForImages.BACK_ADHARCARD.toString().equals(entityName)
                || EntityTypeForImages.FRONT_PANCARD.toString().equals(entityName)
                || EntityTypeForImages.BACK_PANCARD.toString().equals(entityName)
                || EntityTypeForImages.FRONT_VEHICLE_LICENCE.toString().equals(entityName)
                || EntityTypeForImages.BACK_VEHICLE_LICENCE.toString().equals(entityName)
                || EntityTypeForImages.CUSTOMERIMAGE.toString().equals(entityName)) {
            // if (EntityTypeForImages.USEDVEHICLE.toString().equals(entityName)) {

            CustomerDetails cd = null;
            // CustomerDetails customerDetails = this.customerDetailsRepository.getOne(entityId);
            documentImage = this.documentImageRepository.getCustomerDocumentImageByEntityNameandEntityId(clientId, entityName);

            documentImage.setCustomerId(null);

        } else if (EntityTypeForImages.GUARANTORIMAGE.toString().equals(entityName)
                || EntityTypeForImages.G_ADHARPHOTO.toString().equals(entityName)
                || EntityTypeForImages.G_PANCARD.toString().equals(entityName)
                || EntityTypeForImages.G_VEHICLE_LICENCE.toString().equals(entityName)
                || EntityTypeForImages.G_FRONT_ADHARCARD.toString().equals(entityName)
                || EntityTypeForImages.G_BACK_ADHARCARD.toString().equals(entityName)
                || EntityTypeForImages.G_FRONT_PANCARD.toString().equals(entityName)
                || EntityTypeForImages.G_BACK_PANCARD.toString().equals(entityName)
                || EntityTypeForImages.G_FRONT_VEHICLE_LICENCE.toString().equals(entityName)
                || EntityTypeForImages.G_BACK_VEHICLE_LICENCE.toString().equals(entityName)) {
            // if (EntityTypeForImages.USEDVEHICLE.toString().equals(entityName)) {

            // CustomerDetails customerDetails = this.customerDetailsRepository.getOne(entityId);
            documentImage = this.documentImageRepository.getGurantorDocumentImageByEntityNameandEntityId(clientId, entityName);

            documentImage.setGuarantorId(null);

        } else if (EntityTypeForImages.BANK.toString().equals(entityName)) {
            // if (EntityTypeForImages.USEDVEHICLE.toString().equals(entityName)) {

            // CustomerDetails customerDetails = this.customerDetailsRepository.getOne(entityId);
            documentImage = this.documentImageRepository.getBankDocumentImageByEntityNameandEntityId(clientId, entityName);

            documentImage.setBankId(null);

        } else if (EntityTypeForImages.ENROLL_CUSTOMERIMAGE.toString().equals(entityName)
                || EntityTypeForImages.ENROLL_ADHARPHOTO.toString().equals(entityName)
                || EntityTypeForImages.ENROLL_PANCARD.toString().equals(entityName)
                || EntityTypeForImages.ENROLL_FRONT_ADHARCARD.toString().equals(entityName)
                || EntityTypeForImages.ENROLL_BACK_ADHARCARD.toString().equals(entityName)
                || EntityTypeForImages.ENROLL_FRONT_PANCARD.toString().equals(entityName)
                || EntityTypeForImages.ENROLL_BACK_PANCARD.toString().equals(entityName)
                || EntityTypeForImages.ENROLL_FRONT_VEHICLE_LICENCE.toString().equals(entityName)
                || EntityTypeForImages.ENROLL_BACK_VEHICLE_LICENCE.toString().equals(entityName)) {
            // if (EntityTypeForImages.USEDVEHICLE.toString().equals(entityName)) {

            // CustomerDetails customerDetails = this.customerDetailsRepository.getOne(entityId);
            documentImage = this.documentImageRepository.getFEEnrollDocumentImageByEntityNameandEntityId(clientId, entityName);

            documentImage.setFeEnrollId(null);

        } else if (EntityTypeForImages.EMPLOYEE_ADHAR.toString().equals(entityName)
                || EntityTypeForImages.EMPLOYEE_PANCARD.toString().equals(entityName)
                || EntityTypeForImages.EMPLOYEE_VEHICLE_LICENCE.toString().equals(entityName)) {
            // if (EntityTypeForImages.USEDVEHICLE.toString().equals(entityName)) {

            // CustomerDetails customerDetails = this.customerDetailsRepository.getOne(entityId);
            documentImage = this.documentImageRepository.getEmployeeDocumentImageByEntityNameandEntityId(clientId, entityName);

            documentImage.setEmployeeId(null);

        } else if (EntityTypeForImages.ENGINE.toString().equals(entityName) || EntityTypeForImages.CHASSIS.toString().equals(entityName)
                || EntityTypeForImages.VEHICLE_FRONT.toString().equals(entityName)
                || EntityTypeForImages.VEHICLE_LEFT.toString().equals(entityName)
                || EntityTypeForImages.VEHICLE_RIGHT.toString().equals(entityName)
                || EntityTypeForImages.VEHICLE_BACK.toString().equals(entityName)
                || EntityTypeForImages.VEHICLE_CHALAN_REPORT.toString().equals(entityName)
                || EntityTypeForImages.VEHICLE_TRANSACTION_HISTORY.toString().equals(entityName)) {
            // if (EntityTypeForImages.USEDVEHICLE.toString().equals(entityName)) {

            // CustomerDetails customerDetails = this.customerDetailsRepository.getOne(entityId);
            vehicleImage = this.vehicleImageRepository.getVehicleImageByEntityNameandEntityId(clientId, entityName);

            vehicleImage.setVehicleId(null);

        } else if (EntityTypeForImages.INVOICEIMAGE.toString().equals(entityName)) {
            // if (EntityTypeForImages.USEDVEHICLE.toString().equals(entityName)) {

            // CustomerDetails customerDetails = this.customerDetailsRepository.getOne(entityId);
            documentImage = this.documentImageRepository.getLoanDocumentImageByEntityNameandEntityId(clientId, entityName);

            documentImage.setVehicleLoanId(null);

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
        } else if (documentImage != null) {

            final ContentRepository contentRepository = this.contentRepositoryFactory
                    .getRepository(StorageType.fromInt(documentImage.getStorageType()));
            contentRepository.deleteImage(documentImage.getLocation());
            this.documentImageRepository.delete(documentImage);
        } else if (vehicleImage != null) {

            final ContentRepository contentRepository = this.contentRepositoryFactory
                    .getRepository(StorageType.fromInt(vehicleImage.getStorageType()));
            contentRepository.deleteImage(vehicleImage.getLocation());
            this.vehicleImageRepository.delete(vehicleImage);
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

        if (EntityTypeForImages.PROFILEIMAGE.toString().equals(entityName) || entityName.equals("CustomerImage")) {
            // if (EntityTypeForImages.USEDVEHICLE.toString().equals(entityName)) {

            AppUser appuser = this.appUserRepositoryWrapper.findOneWithNotFoundDetection(entityId);
            image = appuser.getImage();
            owner = appuser;
            // }
        }
        // if (entityName.equals("CustomerImage")) {
        /*
         * if (EntityTypeForImages.CUSTOMERIMAGE.toString().equals(entityName)) { // if
         * (EntityTypeForImages.USEDVEHICLE.toString().equals(entityName)) { System.out.println("--CustomerImage--");
         * NewVehicleLoan usedVehicleLoan =
         * this.usedVehicleLoanRepositoryWrapper.findOneWithNotFoundDetection(entityId); // image =
         * usedVehicleLoan.getCustomerImage(); owner = usedVehicleLoan; // } }
         */
        // if (entityName.equals("AdharPhoto") || entityName.equals("GovernmentDocument")) {
        if (EntityTypeForImages.ADHARPHOTO.toString().equals(entityName) || EntityTypeForImages.PANCARD.toString().equals(entityName)
                || EntityTypeForImages.VEHICLE_LICENCE.toString().equals(entityName)
                || EntityTypeForImages.CUSTOMERIMAGE.toString().equals(entityName)
                || EntityTypeForImages.FRONT_ADHARCARD.toString().equals(entityName)
                || EntityTypeForImages.BACK_ADHARCARD.toString().equals(entityName)
                || EntityTypeForImages.FRONT_PANCARD.toString().equals(entityName)
                || EntityTypeForImages.BACK_PANCARD.toString().equals(entityName)
                || EntityTypeForImages.FRONT_VEHICLE_LICENCE.toString().equals(entityName)
                || EntityTypeForImages.BACK_VEHICLE_LICENCE.toString().equals(entityName)) {
            // if (EntityTypeForImages.USEDVEHICLE.toString().equals(entityName)) {

            CustomerDetails customerDetails = this.customerDetailsRepository.getOne(entityId);
            documentImage = this.documentImageRepository.getCustomerDocumentImageByEntityNameandEntityId(entityId, entityName);
            owner = customerDetails;
            // }
        }
        System.out.println("E:" + EntityTypeForImages.INVOICEIMAGE.toString() + " : " + entityName);
        if (EntityTypeForImages.INVOICEIMAGE.toString().equals(entityName)) {
            // if (EntityTypeForImages.NEWVEHICLE.toString().equals(entityName)) {

            VehicleLoan newVehicleLoan = this.newVehicleLoanRepositoryWrapper.findOneWithNotFoundDetection(entityId);
            documentImage = this.documentImageRepository.getCustomerDocumentImageByEntityNameandEntityId(entityId, entityName);
            owner = newVehicleLoan;
            // }
        }
        // if (entityName.equals("Engine") || entityName.equals("Chassis") || entityName.equals("Vehicle")) {
        if (EntityTypeForImages.ENGINE.toString().equals(entityName) || EntityTypeForImages.CHASSIS.toString().equals(entityName)
                || EntityTypeForImages.VEHICLEINSURANCE.toString().equals(entityName)
                || EntityTypeForImages.KMREADING.toString().equals(entityName) || EntityTypeForImages.RCBOOK.toString().equals(entityName)
                || EntityTypeForImages.VEHICLE_FRONT.toString().equals(entityName)
                || EntityTypeForImages.VEHICLE_LEFT.toString().equals(entityName)
                || EntityTypeForImages.VEHICLE_RIGHT.toString().equals(entityName)
                || EntityTypeForImages.VEHICLE_BACK.toString().equals(entityName)
                || EntityTypeForImages.VEHICLE_CHALAN_REPORT.toString().equals(entityName)
                || EntityTypeForImages.VEHICLE_TRANSACTION_HISTORY.toString().equals(entityName)) {
            // else if (EntityTypeForImages.VEHICLEDETAILS.toString().equals(entityName)) {

            VehicleDetails vehicleDetails = this.vehicleDetailsRepositoryWrapper.findOneWithNotFoundDetection(entityId);
            vehicleImage = this.vehicleImageRepository.getVehicleImageByEntityNameandEntityId(entityId, entityName);
            owner = vehicleDetails;
            // }
        }
        // if (entityName.equals("GuarantorImage") || entityName.equals("GuarantorDocument")) {
        if (EntityTypeForImages.GUARANTORIMAGE.toString().equals(entityName)
                || EntityTypeForImages.G_ADHARPHOTO.toString().equals(entityName)
                || EntityTypeForImages.G_PANCARD.toString().equals(entityName)
                || EntityTypeForImages.G_VEHICLE_LICENCE.toString().equals(entityName)
                || EntityTypeForImages.G_FRONT_ADHARCARD.toString().equals(entityName)
                || EntityTypeForImages.G_BACK_ADHARCARD.toString().equals(entityName)
                || EntityTypeForImages.G_FRONT_PANCARD.toString().equals(entityName)
                || EntityTypeForImages.G_BACK_PANCARD.toString().equals(entityName)
                || EntityTypeForImages.G_FRONT_VEHICLE_LICENCE.toString().equals(entityName)
                || EntityTypeForImages.G_BACK_VEHICLE_LICENCE.toString().equals(entityName)) {
            // else if (EntityTypeForImages.GUARANTOR.toString().equals(entityName)) {

            CustomerGuarantor customerGuarantor = this.customerGuarantorRepositoryWrapper.findOneWithNotFoundDetection(entityId);
            documentImage = this.documentImageRepository.getGurantorDocumentImageByEntityNameandEntityId(entityId, entityName);
            owner = customerGuarantor;
            // }
        } else if (EntityTypeForImages.BANK.toString().equals(entityName)) {

            BankDetails bankDetails = this.bankDetailsRepositoryWrapper.findOneWithNotFoundDetection(entityId);
            documentImage = this.documentImageRepository.getBankDocumentImageByEntityNameandEntityId(entityId, entityName);
            owner = bankDetails;
        } else if (EntityTypeForImages.ENROLL_CUSTOMERIMAGE.toString().equals(entityName)
                || EntityTypeForImages.ENROLL_ADHARPHOTO.toString().equals(entityName)
                || EntityTypeForImages.ENROLL_PANCARD.toString().equals(entityName)
                || EntityTypeForImages.ENROLL_FRONT_ADHARCARD.toString().equals(entityName)
                || EntityTypeForImages.ENROLL_BACK_ADHARCARD.toString().equals(entityName)
                || EntityTypeForImages.ENROLL_FRONT_PANCARD.toString().equals(entityName)
                || EntityTypeForImages.ENROLL_BACK_PANCARD.toString().equals(entityName)
                || EntityTypeForImages.ENROLL_FRONT_VEHICLE_LICENCE.toString().equals(entityName)
                || EntityTypeForImages.ENROLL_BACK_VEHICLE_LICENCE.toString().equals(entityName)) {

            FEEnroll feEnroll = this.feEnrollRepositoryWrapper.findOneWithNotFoundDetection(entityId);
            documentImage = this.documentImageRepository.getFEEnrollDocumentImageByEntityNameandEntityId(entityId, entityName);
            owner = feEnroll;
        }

        else if (EntityTypeForImages.FENEWLOAN.toString().equals(entityName)) {

            VehicleLoan newLoan = this.newVehicleLoanRepositoryWrapper.findOneWithNotFoundDetection(entityId);
            // documentImage = bankDetails.getImage();
            owner = newLoan;
        }

        if (EntityTypeForImages.EMPLOYEE_ADHAR.toString().equals(entityName)
                || EntityTypeForImages.EMPLOYEE_PANCARD.toString().equals(entityName)
                || EntityTypeForImages.EMPLOYEE_VEHICLE_LICENCE.toString().equals(entityName)) {
            // if (EntityTypeForImages.NEWVEHICLE.toString().equals(entityName)) {

            Employee employee = this.employeeRepositoryWrapper.findOneWithNotFoundDetection(entityId);

            documentImage = this.documentImageRepository.getEmployeeDocumentImageByEntityNameandEntityId(entityId, entityName);
            owner = employee;
            // }
        }

        if (EntityTypeForImages.CLIENTS.toString().equals(entityName)) {
            Client client = this.clientRepositoryWrapper.findOneWithNotFoundDetection(entityId);
            image = client.getImage();
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

            this.imageRepository.delete(image);
        }
        if (documentImage != null) {

            final ContentRepository contentRepository = this.contentRepositoryFactory
                    .getRepository(StorageType.fromInt(documentImage.getStorageType()));
            contentRepository.deleteImage(documentImage.getLocation());

            this.documentImageRepository.delete(documentImage);
        }

        if (vehicleImage != null) {

            final ContentRepository contentRepository = this.contentRepositoryFactory
                    .getRepository(StorageType.fromInt(vehicleImage.getStorageType()));
            contentRepository.deleteImage(vehicleImage.getLocation());

            this.vehicleImageRepository.delete(vehicleImage);
        }
        return owner;
    }

    private CommandProcessingResult updateImage(String entityName, final Object owner, final String imageLocation, String documentNumber,
            final StorageType storageType) {

        Image image = null; // NewVehicleLoan, NewVehicleLoan, VehicleDetails, CustomerGuarantor, BankDetails
        DocumentImages documentImage = null;
        VehicleImages vehicleImage = null;
        Long clientId = null;
        if (owner instanceof Client) {

            Client client = (Client) owner;
            image = client.getImage();
            clientId = client.getId();
            image = createImage(entityName, image, imageLocation, storageType, null, null);
            client.setImage(image);
            this.clientRepositoryWrapper.save(client);
        } else if (owner instanceof AppUser) { // create 3 section same as database documents, images, vehicles

            AppUser appUser = (AppUser) owner;

            // documentImage = newVehicleLoan.getInvoiceImage();
            clientId = appUser.getId(); // get the id of the table to make change in the specific table
            // documentImage = createDocumentImage(documentImage, imageLocation, storageType, null, null,
            // newVehicleLoan, null);
            image = createImage(entityName, image, imageLocation, storageType, null, null);
            this.imageRepository.save(image);
            appUser.setImage(image);
            // newVehicleLoan.setImage(documentImage);
            // newVehicleLoan.setInvoiceImage(documentImage);
            this.appUserRepository.save(appUser);
        } else if (owner instanceof VehicleLoan) { // create 3 section same as database documents, images, vehicles

            VehicleLoan newVehicleLoan = (VehicleLoan) owner;

            // documentImage = newVehicleLoan.getInvoiceImage();
            clientId = newVehicleLoan.getId(); // get the id of the table to make change in the specific table
            documentImage = createDocumentImage(entityName, documentImage, imageLocation, documentNumber, storageType, null, null,
                    newVehicleLoan, null, null, null);
            this.documentImageRepository.saveAndFlush(documentImage);
            // newVehicleLoan.setImage(documentImage);
            // newVehicleLoan.setInvoiceImage(documentImage);
            this.newVehicleLoanRepositoryWrapper.save(newVehicleLoan);
        } /*
           * else if (owner instanceof NewVehicleLoan) { System.out.println("--NewVehicleLoan--"); NewVehicleLoan
           * usedVehicleLoan = (NewVehicleLoan) owner; clientId = usedVehicleLoan.getId(); if
           * (entityName.equals("CustomerImage")) { System.out.println("--CustomerImage--"); // image =
           * usedVehicleLoan.getCustomerImage(); // usedvehicle has customerimage image = createImage(image,
           * imageLocation, storageType, usedVehicleLoan, null); // usedVehicleLoan.setCustomerImage(image);
           * this.usedVehicleLoanRepository.save(usedVehicleLoan); } /*else {// usedvehicle has document //
           * documentImage = usedVehicleLoan.getDocumentImage(); documentImage = createDocumentImage(documentImage,
           * imageLocation, documentNumber, storageType, null, null, null, usedVehicleLoan, null, null);
           * this.documentImageRepository.saveAndFlush(documentImage); //
           * usedVehicleLoan.setDocumentImage(documentImage); this.usedVehicleLoanRepository.save(usedVehicleLoan); }
           */
        else if (owner instanceof CustomerDetails) {

            CustomerDetails customerDetails = (CustomerDetails) owner;
            // vehicleImage = vehicleDetails.getImage();
            clientId = customerDetails.getId();
            documentImage = createDocumentImage(entityName, documentImage, imageLocation, documentNumber, storageType, null, null, null,
                    null, customerDetails, null);
            // vehicleDetails.setImage(vehicleImage);
            this.documentImageRepository.saveAndFlush(documentImage);
            // this.vehicleDetailsRepository.save(vehicleDetails);
        } else if (owner instanceof VehicleDetails) {

            VehicleDetails vehicleDetails = (VehicleDetails) owner;
            // vehicleImage = vehicleDetails.getImage();
            clientId = vehicleDetails.getId();
            vehicleImage = createVehicleImage(entityName, vehicleImage, imageLocation, storageType, vehicleDetails);
            // vehicleDetails.setImage(vehicleImage);
            this.vehicleImageRepository.save(vehicleImage);
            this.vehicleDetailsRepository.save(vehicleDetails);
        } else if (owner instanceof CustomerGuarantor) {

            CustomerGuarantor customerGuarantor = (CustomerGuarantor) owner;
            clientId = customerGuarantor.getId(); // if guarantor has image and doucme

            if (entityName.equals("GuarantorImage")) {

                // image = customerGuarantor.getGuarantorImage();
                image = createImage(entityName, image, imageLocation, storageType, null, customerGuarantor);
                // customerGuarantor.setGuarantorImage(image);
                this.imageRepository.save(image);
                this.customerGuarantorRepository.save(customerGuarantor);
            } else { // storing guarantor documents
                // documentImage = customerGuarantor.getDocumentImage();

                if (EntityTypeForImages.G_ADHARPHOTO.toString().equals(entityName)) {
                    System.out.println("--ADHARPHOTO--");
                    // entityName = "adharphoto";
                    documentImage = createDocumentImage(entityName, documentImage, imageLocation, documentNumber, storageType, null,
                            customerGuarantor, null, null, null, null);
                } else if (EntityTypeForImages.G_PANCARD.toString().equals(entityName)) {
                    // entityName = "pancard";
                    documentImage = createDocumentImage(entityName, documentImage, imageLocation, documentNumber, storageType, null,
                            customerGuarantor, null, null, null, null);
                } else if (EntityTypeForImages.G_VEHICLE_LICENCE.toString().equals(entityName)) {
                    // entityName = "vehicle_licence";
                    documentImage = createDocumentImage(entityName, documentImage, imageLocation, documentNumber, storageType, null,
                            customerGuarantor, null, null, null, null);
                }
                // customerGuarantor.setDocumentImage(documentImage);
                this.documentImageRepository.saveAndFlush(documentImage);
                this.customerGuarantorRepository.save(customerGuarantor);
            }
        } else if (owner instanceof BankDetails) {

            BankDetails bankDetails = (BankDetails) owner;
            // documentImage = bankDetails.getImage();
            clientId = bankDetails.getId();

            documentImage = createDocumentImage(entityName, documentImage, imageLocation, documentNumber, storageType, bankDetails, null,
                    null, null, null, null);
            // bankDetails.setImage(documentImage);

            this.documentImageRepository.saveAndFlush(documentImage);

            this.bankDetailsRepository.saveAndFlush(bankDetails);

        } else if (owner instanceof Employee) {

            Employee employee = (Employee) owner;
            // documentImage = bankDetails.getImage();
            clientId = employee.getId();

            documentImage = createDocumentImage(entityName, documentImage, imageLocation, documentNumber, storageType, null, null, null,
                    null, null, employee);
            // bankDetails.setImage(documentImage);

            this.documentImageRepository.saveAndFlush(documentImage);

            this.employeeRepository.saveAndFlush(employee);

        } else if (owner instanceof FEEnroll) {

            FEEnroll feEnroll = (FEEnroll) owner;
            clientId = feEnroll.getId();

            documentImage = createDocumentImage(entityName, documentImage, imageLocation, documentNumber, storageType, null, null, null,
                    feEnroll, null, null);

            this.documentImageRepository.saveAndFlush(documentImage);

            this.feEnrollRepository.saveAndFlush(feEnroll);
            // documentImage = bankDetails.getImage();
        } /*
           * else if (owner instanceof NewLoan) {
           *
           * NewLoan newLoan = (NewLoan) owner; clientId = newLoan.getId();
           *
           * documentImage = createDocumentImage(entityName, documentImage, imageLocation, documentNumber, storageType,
           * null, null, null, null, newLoan);
           *
           * this.documentImageRepository.saveAndFlush(documentImage); System.out.println("--newLoanRepository--" +
           * newLoan);
           *
           * this.newLoanRepository.saveAndFlush(newLoan); // documentImage = bankDetails.getImage(); }
           */else if (owner instanceof Staff) {
            Staff staff = (Staff) owner;
            image = staff.getImage();
            clientId = staff.getId();
            image = createImage(entityName, image, imageLocation, storageType, null, null);
            staff.setImage(image);
            this.staffRepositoryWrapper.save(staff);
        }

        return new CommandProcessingResult(clientId);
    }

    private Image createImage(final String entityName, Image image, final String imageLocation, final StorageType storageType,
            final VehicleLoan usedVehicle, final CustomerGuarantor customerGuarantor) {
        if (image == null) {
            image = new Image(imageLocation, entityName, storageType, usedVehicle, customerGuarantor);
        } else {
            image.setLocation(imageLocation);
            image.setStorageType(storageType.getValue());
        }
        return image;
    }

    private DocumentImages createDocumentImage(final String entityName, DocumentImages documentImage, final String imageLocation,
            final String documentNumber, final StorageType storageType, final BankDetails bankDetails,
            final CustomerGuarantor customerGuarantor, final VehicleLoan newVehicleImage, final FEEnroll feEnroll,
            CustomerDetails customerDetails, final Employee employee) {
        if (documentImage == null) {

            documentImage = new DocumentImages(imageLocation, entityName, documentNumber, storageType, bankDetails, customerGuarantor,
                    newVehicleImage, feEnroll, customerDetails, employee);
        } else {
            documentImage.setLocation(imageLocation);
            documentImage.setStorageType(storageType.getValue());
        }
        return documentImage;
    }

    private VehicleImages createVehicleImage(final String entityName, VehicleImages vehicleImages, final String imageLocation,
            final StorageType storageType, final VehicleDetails vehicleDetails) {
        if (vehicleImages == null) {
            vehicleImages = new VehicleImages(imageLocation, entityName, storageType, vehicleDetails);
        } else {
            vehicleImages.setLocation(imageLocation);
            vehicleImages.setStorageType(storageType.getValue());
        }
        return vehicleImages;
    }

}
