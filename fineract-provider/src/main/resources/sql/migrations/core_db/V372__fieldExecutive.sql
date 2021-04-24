--
-- Licensed to the Apache Software Foundation (ASF) under one
-- or more contributor license agreements. See the NOTICE file
-- distributed with this work for additional information
-- regarding copyright ownership. The ASF licenses this file
-- to you under the Apache License, Version 2.0 (the
-- "License"); you may not use this file except in compliance
-- with the License. You may obtain a copy of the License at
--
-- http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an
-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied. See the License for the
-- specific language governing permissions and limitations
-- under the License.
--

-- ALTER TABLE `m_appuser`

/* CREATE TABLE `m_feEnquiry` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `mobile_number` VARCHAR(50) NULL DEFAULT NULL,
    `customer_name` VARCHAR(50) NULL DEFAULT NULL,
    `vehicle_number` VARCHAR(50) NULL DEFAULT NULL,
    `email` VARCHAR(50) NULL DEFAULT NULL,
    `enquiry_id` VARCHAR(50) NULL DEFAULT NULL,
    INDEX `id` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;*/


/* CREATE TABLE `m_feEnroll` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `customer_name` VARCHAR(50) NULL DEFAULT NULL,
    `mobile_number` VARCHAR(50) NULL DEFAULT NULL,
    `alternate_mobile_number` VARCHAR(50) NULL DEFAULT NULL,
    `dob` DATE NULL DEFAULT NULL,
    `father_name` VARCHAR(50) NULL DEFAULT NULL,
    `gender` VARCHAR(50) NULL DEFAULT NULL,
    `applicant_type` VARCHAR(50) NULL DEFAULT NULL,
    `applicant_id` VARCHAR(50) NULL DEFAULT NULL,
    INDEX `id` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;*/

CREATE TABLE `m_applicant_details` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `customerId` VARCHAR(50) NULL DEFAULT NULL,
    `mobile_number` VARCHAR(50) NULL DEFAULT NULL,
    `customer_name` VARCHAR(50) NULL DEFAULT NULL,
    `father_name` VARCHAR(50) NULL DEFAULT NULL,
    `reference_name` VARCHAR(50) NULL DEFAULT NULL,
    `reference_contact` VARCHAR(50) NULL DEFAULT NULL,
    `applicant_type` VARCHAR(50) NULL DEFAULT NULL,
    `company_name` VARCHAR(50) NULL DEFAULT NULL,
    `net_income` INT(11) NULL DEFAULT NULL,
    `income_frequency` VARCHAR(50) NULL DEFAULT NULL,
    `salary_date` DATE NULL DEFAULT NULL,
    `dob` DATE NULL DEFAULT NULL,
    `age` INT(11) NULL DEFAULT NULL,
    `marital_status` VARCHAR(50) NULL DEFAULT NULL,
    `spouse_name` VARCHAR(50) NULL DEFAULT NULL,
    `communicationadd_id` BIGINT(20) NULL DEFAULT NULL,
    `permanentadd_id` BIGINT(20) NULL DEFAULT NULL,
    `officeadd_id` BIGINT(20) NULL DEFAULT NULL,
    INDEX `id` (`id`),
    INDEX `communicationadd_id` (`communicationadd_id`),
    INDEX `permenantadd_org` (`permanentadd_id`),
    INDEX `officeadd_org` (`officeadd_id`),
    CONSTRAINT `customeradd_org` FOREIGN KEY (`communicationadd_id`) REFERENCES `m_address` (`id`),
    CONSTRAINT `officeadd_org` FOREIGN KEY (`officeadd_id`) REFERENCES `m_address` (`id`),
    CONSTRAINT `permenantadd_org` FOREIGN KEY (`permanentadd_id`) REFERENCES `m_address` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;


CREATE TABLE `m_co_applicant_details` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `customerId` VARCHAR(50) NULL DEFAULT NULL,
    `mobile_number` VARCHAR(50) NULL DEFAULT NULL,
    `customer_name` VARCHAR(50) NULL DEFAULT NULL,
    `applicant_type` VARCHAR(50) NULL DEFAULT NULL,
    `company_name` VARCHAR(50) NULL DEFAULT NULL,
    `net_income` INT(11) NULL DEFAULT NULL,
    `income_frequency` VARCHAR(50) NULL DEFAULT NULL,
    `salary_date` DATE NULL DEFAULT NULL,
    `dob` DATE NULL DEFAULT NULL,
    `age` INT(11) NULL DEFAULT NULL,
    `marital_status` VARCHAR(50) NULL DEFAULT NULL,
    `spouse_name` VARCHAR(50) NULL DEFAULT NULL,
    `communicationadd_id` BIGINT(20) NULL DEFAULT NULL,
    `permanentadd_id` BIGINT(20) NULL DEFAULT NULL,
    `officeadd_id` BIGINT(20) NULL DEFAULT NULL,
    INDEX `id` (`id`),
    INDEX `communicationadd_id` (`communicationadd_id`),
    INDEX `permanentadd_id` (`permanentadd_id`),
    INDEX `officeadd_id` (`officeadd_id`),
    CONSTRAINT `coappcommunication_org` FOREIGN KEY (`communicationadd_id`) REFERENCES `m_address` (`id`),
    CONSTRAINT `coappoffice_org` FOREIGN KEY (`officeadd_id`) REFERENCES `m_address` (`id`),
    CONSTRAINT `coapppermenant_org` FOREIGN KEY (`permanentadd_id`) REFERENCES `m_address` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;


CREATE TABLE `m_apply_vehicle_loan` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `customer_name` VARCHAR(50) NULL DEFAULT NULL,
    `customer_id` BIGINT(20) NULL DEFAULT '0',
    `vehicle_type` VARCHAR(50) NULL DEFAULT NULL,
    `loan_classification` VARCHAR(50) NULL DEFAULT NULL,
    `loan_type` VARCHAR(50) NULL DEFAULT NULL,
    `dealer` VARCHAR(50) NULL DEFAULT NULL,
    `vehicle_condition` VARCHAR(50) NULL DEFAULT NULL,
    `invoice_number` VARCHAR(50) NULL DEFAULT NULL,
    `loan_id` VARCHAR(50) NULL DEFAULT NULL,
    `applicantdetails_id` BIGINT(20) NULL DEFAULT NULL,
    `coapplicantdetails_id` BIGINT(20) NULL DEFAULT NULL,
    `vehicledetails_id` BIGINT(20) NULL DEFAULT NULL,
    `bankdetails_id` BIGINT(20) NULL DEFAULT NULL,
    `loandetails_id` BIGINT(20) NULL DEFAULT NULL,
    INDEX `id` (`id`),
    INDEX `FK_customer_details_id` (`applicantdetails_id`),
    INDEX `FK_vehicle_details_id` (`vehicledetails_id`),
    INDEX `FK_guarantor_details_id` (`coapplicantdetails_id`),
    INDEX `FK_bank_details_id` (`bankdetails_id`),
    INDEX `customer_id` (`customer_id`),
    INDEX `loandetails_id` (`loandetails_id`),
    CONSTRAINT `FK_bank_details_id` FOREIGN KEY (`bankdetails_id`) REFERENCES `m_customer_bank_details` (`id`),
    CONSTRAINT `FK_applicant_details_id` FOREIGN KEY (`applicantdetails_id`) REFERENCES `m_applicant_details` (`id`),
    CONSTRAINT `FK_coapplicant_details_id` FOREIGN KEY (`coapplicantdetails_id`) REFERENCES `m_co_applicant_details` (`id`),
    CONSTRAINT `FK_vehicle_details_id` FOREIGN KEY (`vehicledetails_id`) REFERENCES `m_vehicle_details` (`id`),
    CONSTRAINT `c_new_vehicle_org` FOREIGN KEY (`customer_id`) REFERENCES `m_appuser` (`customer_id`),
    CONSTRAINT `loan_details_org` FOREIGN KEY (`loandetails_id`) REFERENCES `m_loan_details` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;

CREATE TABLE `m_documents_images` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `location` VARCHAR(500) NULL DEFAULT NULL,
    `storage_type_enum` SMALLINT(6) NULL DEFAULT NULL,
    `bank_id` BIGINT(20) NULL DEFAULT NULL,
    `coapplicant_id` INT(11) NULL DEFAULT NULL,
    `vehicle_id` INT(11) NULL DEFAULT NULL,
    `vehicle_id` INT(11) NULL DEFAULT NULL,
    PRIMARY KEY (`id`),
    INDEX `FK_document_bank_images` (`bank_id`),
    INDEX `FK_guarantor_id` (`guarantor_id`),
    INDEX `FK_usedvehicle` (`used_vehicle_id`),
    INDEX `FK_newvehicle_id` (`new_vehicle_id`),
    CONSTRAINT `FK_document_bank_images` FOREIGN KEY (`bank_id`) REFERENCES `m_customer_bank_details` (`id`),
    CONSTRAINT `FK_guarantor_id` FOREIGN KEY (`coapplicant_id`) REFERENCES `m_co_applicant_details` (`id`),
    CONSTRAINT `FK_vehicle` FOREIGN KEY (`vehicle_id`) REFERENCES `m_apply_vehicle_loan` (`id`),
   -- CONSTRAINT `m_documents_images_ibfk_1` FOREIGN KEY (`new_vehicle_id`) REFERENCES `m_apply_new_vehicle_loan` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;

ALTER TABLE m_image
    ADD customer_image BIGINT(11),
    ADD CONSTRAINT FOREIGN KEY (`customer_image`) REFERENCES `m_apply_vehicle_loan` (`id`);

/* CREATE TABLE `m_fe_vehicle_details` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `vehicle_number` VARCHAR(50) NULL DEFAULT NULL,
    `maker` VARCHAR(50) NULL DEFAULT NULL,
    `model` VARCHAR(50) NULL DEFAULT NULL,
    `color` VARCHAR(50) NULL DEFAULT NULL,
    `mfg_year` VARCHAR(50) NULL DEFAULT NULL,
    `engine_number` VARCHAR(50) NULL DEFAULT NULL,
    `chassis_number` VARCHAR(50) NULL DEFAULT NULL,
    `insurance_company` VARCHAR(50) NULL DEFAULT NULL,
    `insurance_policy` VARCHAR(50) NULL DEFAULT NULL,
    `insurance_expiry` DATE NULL DEFAULT NULL,
    `live_km_reading` BIGINT(20) NULL DEFAULT NULL,
    INDEX `id` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;*/


/*CREATE TABLE `m_fe_loan_details` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `loan_amount` BIGINT(20) NULL DEFAULT NULL,
    `loan_term` BIGINT(20) NULL DEFAULT NULL,
    `loan_interest` BIGINT(20) NULL DEFAULT NULL,
    `emi` BIGINT(20) NULL DEFAULT NULL,
    `interest_inr` BIGINT(20) NULL DEFAULT NULL,
    `due_date` DATE NULL DEFAULT NULL,
    INDEX `id` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;*/


/*CREATE TABLE `m_fe_transfer_details` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `disbursal_type` VARCHAR(50) NULL DEFAULT NULL,
    `account_number` VARCHAR(50) NULL DEFAULT NULL,
    `account_holder_name` VARCHAR(50) NULL DEFAULT NULL,
    `bank_name` VARCHAR(50) NULL DEFAULT NULL,
    `branch_name` VARCHAR(50) NULL DEFAULT NULL,
    `IFSC` VARCHAR(50) NULL DEFAULT NULL,
    INDEX `id` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;*/

/*CREATE TABLE `m_fe_used_vehicle_loan` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `customer_id` BIGINT(20) NULL DEFAULT '0',
    `customer_name` VARCHAR(50) NULL DEFAULT '0',
    `loan_classification` VARCHAR(50) NULL DEFAULT '0',
    `applicant_details_id` BIGINT(20) NULL DEFAULT '0',
    `coapplicant_details_id` BIGINT(20) NULL DEFAULT '0',
    `vehicle_details_id` BIGINT(20) NULL DEFAULT '0',
    `loan_details_id` BIGINT(20) NULL DEFAULT '0',
    `transfer_details_id` BIGINT(20) NULL DEFAULT '0',
    INDEX `id` (`id`),
    INDEX `applicantdetails_org` (`applicant_details_id`),
    INDEX `customer_id` (`customer_id`),
    INDEX `fe_used_vehicle` (`coapplicant_details_id`),
    INDEX `fe_vehicle_details` (`vehicle_details_id`),
    INDEX `fe_loan_details` (`loan_details_id`),
    INDEX `fe_transfer_details` (`transfer_details_id`),
    CONSTRAINT `applicantdetails_org` FOREIGN KEY (`applicant_details_id`) REFERENCES `m_fe_applicant_details` (`id`),
    CONSTRAINT `fe_loan_details` FOREIGN KEY (`loan_details_id`) REFERENCES `m_fe_loan_details` (`id`),
    CONSTRAINT `fe_transfer_details` FOREIGN KEY (`transfer_details_id`) REFERENCES `m_fe_transfer_details` (`id`),
    CONSTRAINT `fe_used_vehicle` FOREIGN KEY (`coapplicant_details_id`) REFERENCES `m_fe_co_applicant_details` (`id`),
    CONSTRAINT `fe_used_vehicle_org` FOREIGN KEY (`customer_id`) REFERENCES `m_appuser` (`customer_id`),
    CONSTRAINT `fe_vehicle_details` FOREIGN KEY (`vehicle_details_id`) REFERENCES `m_fe_vehicle_details` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=2
;

CREATE TABLE `m_documents_type` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `documents_name` VARCHAR(50) NULL DEFAULT NULL,
    `status` INT(11) NULL DEFAULT NULL,
    INDEX `id` (`id`)
)
ENGINE=InnoDB
;

INSERT INTO `fineract_default`.`m_documents_type` (`id`, `documents_name`, `status`) VALUES ('1', 'aadhaar', '1');
INSERT INTO `fineract_default`.`m_documents_type` (`id`, `documents_name`, `status`) VALUES ('2', 'pancard', '1');

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('vehicle', 'CREATE_FEENQUIRYLOAN', 'FEENQUIRYLOAN', 'CREATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('vehicle', 'CREATE_FEENROLL', 'FEENROLL', 'CREATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('vehicle', 'CREATE_NEWLOAN', 'NEWLOAN', 'CREATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('vehicle', 'CREATE_NEWAPPLICANTLOAN', 'NEWAPPLICANTLOAN', 'CREATE', 0);*/
