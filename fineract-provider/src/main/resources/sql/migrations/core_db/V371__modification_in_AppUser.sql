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
ALTER TABLE m_appuser MODIFY COLUMN email VARCHAR(100) NULL;
ALTER TABLE m_appuser MODIFY COLUMN firstname VARCHAR(100) NULL;
ALTER TABLE m_appuser MODIFY COLUMN lastname VARCHAR(100) NULL;
ALTER TABLE m_appuser ADD COLUMN image_id INT(100) NULL;
ALTER TABLE m_appuser drop column firstname;
ALTER TABLE m_appuser drop column lastname;
ALTER TABLE m_appuser add column name VARCHAR(100) NULL after username;


-- create Loan Enquiry Table

CREATE TABLE `m_customerloanenquiry` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `user_id` VARCHAR(50) NOT NULL DEFAULT '0',
    `customer_name` VARCHAR(50) NOT NULL DEFAULT '0',
    `vehicle_number` VARCHAR(50) NULL DEFAULT '0',
    `email` VARCHAR(50) NULL DEFAULT '0',
    `pincode` VARCHAR(50) NULL DEFAULT '0',
    `enquiry_id` VARCHAR(50) NULL DEFAULT '0',
    `notes` VARCHAR(100) NULL DEFAULT '0',
    INDEX `id` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1;

CREATE TABLE `m_customer_guarantor` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `guarantor_name` varchar(100) NULL DEFAULT 'NULL',
    `mobile_number` BIGINT(20) NULL DEFAULT '0',
    `gender` varchar(100) NULL DEFAULT '0',
    `dob` DATE NULL DEFAULT NULL,
    `marital_status` VARCHAR(50) NULL DEFAULT '0',
    `spouse_name` VARCHAR(100) NULL DEFAULT '0',
    `profession` VARCHAR(50) NULL DEFAULT '0',
    INDEX `id` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1;


ALTER table m_address
ADD landmark varchar(100),
ADD area varchar(100),
ADD state varchar(100);


CREATE TABLE `m_customer_details` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NULL DEFAULT '0',
    `gender` VARCHAR(20) NULL DEFAULT '0',
    `dob` DATE NULL DEFAULT NULL,
    `maritalstatus` VARCHAR(50) NULL DEFAULT NULL,
    `spousename` VARCHAR(100) NULL DEFAULT NULL,
    `profession` VARCHAR(100) NULL DEFAULT NULL,
    `proof_image_id` BIGINT(11) NULL DEFAULT NULL,
    INDEX `id` (`id`),
    INDEX `FK_proof_image_id` (`proof_image_id`),
    CONSTRAINT `FK_proof_image_id` FOREIGN KEY (`proof_image_id`) REFERENCES `m_image` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;


CREATE TABLE `m_vehicle_details` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `vehicle_number` VARCHAR(50) NULL DEFAULT '0',
    `maker` VARCHAR(50) NULL DEFAULT '0',
    `model` VARCHAR(50) NULL DEFAULT '0',
    `color` VARCHAR(50) NULL DEFAULT '0',
    `mfg_year` VARCHAR(50) NULL DEFAULT '0',
    `engine_number` VARCHAR(50) NULL DEFAULT '0',
    `chassis_number` VARCHAR(50) NULL DEFAULT '0',
    `insurance_company` VARCHAR(50) NULL DEFAULT '0',
    `insurance_policy` VARCHAR(50) NULL DEFAULT '0',
    `insurance_expiry` DATE NULL DEFAULT NULL,
    `pollution_cert_expiry` DATE NULL DEFAULT NULL,
    `registration` DATE NULL DEFAULT NULL,
    `live_km_reading` BIGINT(20) NULL DEFAULT NULL,
    INDEX `id` (`id`)
)
ENGINE=InnoDB
AUTO_INCREMENT=1
;


CREATE TABLE `m_customer_bank_details` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `loan_eligible_amount` BIGINT(20) NULL DEFAULT '0',
    `account_type` VARCHAR(50) NULL DEFAULT '0',
    `disbursal_type` VARCHAR(50) NULL DEFAULT '0',
    `account_number` VARCHAR(50) NULL DEFAULT '0',
    `account_holder_name` VARCHAR(50) NULL DEFAULT '0',
    `bank_name` VARCHAR(50) NULL DEFAULT '0',
    `branch_name` VARCHAR(50) NULL DEFAULT '0',
    `IFSC` VARCHAR(50) NULL DEFAULT '0',
    INDEX `id` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;


CREATE TABLE `m_apply_new_vehicle_loan` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `customer_name` VARCHAR(50) NULL DEFAULT NULL,
    `vehicle_type` VARCHAR(50) NULL DEFAULT NULL,
    `dealer` VARCHAR(50) NULL DEFAULT NULL,
    `invoice_number` VARCHAR(50) NULL DEFAULT NULL,
    `address_id` BIGINT(20) NULL DEFAULT NULL,
    `customerdetails_id` INT(11) NULL DEFAULT NULL,
    `vehicledetails_id` INT(50) NULL DEFAULT NULL,
    `customerguarantor_id` INT(50) NULL DEFAULT NULL,
    `bankdetails_id` INT(50) NULL DEFAULT NULL,
     INDEX `id` (`id`),
    INDEX `FK_address_id` (`address_id`),
    INDEX `FK_customer_details_id` (`customerdetails_id`),
    INDEX `FK_vehicle_details_id` (`vehicledetails_id`),
    INDEX `FK_guarantor_details_id` (`customerguarantor_id`),
    INDEX `FK_bank_details_id` (`bankdetails_id`),
    CONSTRAINT `FK_address_id` FOREIGN KEY (`address_id`) REFERENCES `m_address` (`id`),
    CONSTRAINT `FK_bank_details_id` FOREIGN KEY (`bankdetails_id`) REFERENCES `m_customer_bank_details` (`id`),
    CONSTRAINT `FK_customer_details_id` FOREIGN KEY (`customerdetails_id`) REFERENCES `m_customer_details` (`id`),
    CONSTRAINT `FK_guarantor_details_id` FOREIGN KEY (`customerguarantor_id`) REFERENCES `m_customer_guarantor` (`id`),
    CONSTRAINT `FK_vehicle_details_id` FOREIGN KEY (`vehicledetails_id`) REFERENCES `m_vehicle_details` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;


CREATE TABLE `m_apply_used_vehicle_loan` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `customer_name` VARCHAR(50) NULL DEFAULT NULL,
    `vehicle_type` VARCHAR(50) NULL DEFAULT NULL,
    `loan_type` VARCHAR(50) NULL DEFAULT NULL,
    `address_id` BIGINT(20) NULL DEFAULT NULL,
    `customerdetails_id` INT(11) NULL DEFAULT NULL,
    `vehicledetails_id` INT(50) NULL DEFAULT NULL,
    `guarantordetails_id` INT(50) NULL DEFAULT NULL,
    `bankdetails_id` INT(50) NULL DEFAULT NULL,
    INDEX `id` (`id`),
    INDEX `FK_uv_address_id` (`address_id`),
    INDEX `FK_uv_customer_details_id` (`customerdetails_id`),
    INDEX `FK_uv_vehicle_details_id` (`vehicledetails_id`),
    INDEX `FK_uv_guarantor_details_id` (`guarantordetails_id`),
    INDEX `FK_uv_bank_details_id` (`bankdetails_id`),
    CONSTRAINT `FK_uv_address_id` FOREIGN KEY (`address_id`) REFERENCES `m_address` (`id`),
    CONSTRAINT `FK_uv_bank_details_id` FOREIGN KEY (`bankdetails_id`) REFERENCES `m_customer_bank_details` (`id`),
    CONSTRAINT `FK_uv_customer_details_id` FOREIGN KEY (`customerdetails_id`) REFERENCES `m_customer_details` (`id`),
    CONSTRAINT `FK_uv_guarantor_details_id` FOREIGN KEY (`guarantordetails_id`) REFERENCES `m_customer_guarantor` (`id`),
    CONSTRAINT `FK_uv_vehicle_details_id` FOREIGN KEY (`vehicledetails_id`) REFERENCES `m_vehicle_details` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
;


INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('customer', 'UPDATE_CUSTOMER', 'CUSTOMER', 'UPDATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('vehicle', 'CREATE_NEWVEHICLELOAN', 'NEWVEHICLELOAN', 'CREATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('vehicle', 'CREATE_USEDVEHICLELOAN', 'USEDVEHICLELOAN', 'CREATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('vehicle', 'CREATE_ENQUIRY', 'ENQUIRY', 'CREATE', 0);

CREATE TABLE `m_documents_images` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `location` VARCHAR(500) NULL DEFAULT NULL,
    `storage_type_enum` SMALLINT(6) NULL DEFAULT NULL,
    `bank_id` INT(11) NULL DEFAULT NULL,
    `guarantor_id` INT(11) NULL DEFAULT NULL,
    `new_vehicle_id` INT(11) NULL DEFAULT NULL,
    `used_vehicle_id` INT(11) NULL DEFAULT NULL,
    PRIMARY KEY (`id`),
    INDEX `FK_document_bank_images` (`bank_id`),
    INDEX `FK_guarantor_id` (`guarantor_id`),
    INDEX `FK_usedvehicle` (`used_vehicle_id`),
    INDEX `FK_newvehicle_id` (`new_vehicle_id`),
    CONSTRAINT `FK_document_bank_images` FOREIGN KEY (`bank_id`) REFERENCES `m_customer_bank_details` (`id`),
    CONSTRAINT `FK_guarantor_id` FOREIGN KEY (`guarantor_id`) REFERENCES `m_customer_guarantor` (`id`),
    CONSTRAINT `FK_usedvehicle` FOREIGN KEY (`used_vehicle_id`) REFERENCES `m_apply_used_vehicle_loan` (`id`),
    CONSTRAINT `m_documents_images_ibfk_1` FOREIGN KEY (`new_vehicle_id`) REFERENCES `m_apply_new_vehicle_loan` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;

CREATE TABLE `m_vehicle_images` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `location` VARCHAR(500) NULL DEFAULT NULL,
    `vehicle_id` INT(11) NULL DEFAULT NULL,
    `storage_type_enum` SMALLINT(6) NULL DEFAULT NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_used_vehicle_customer` (`vehicle_id`),
    CONSTRAINT `fk_used_vehicle_customer` FOREIGN KEY (`vehicle_id`) REFERENCES `m_vehicle_details` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;

    ALTER TABLE m_image
    ADD guarantor_image int(11),
    ADD CONSTRAINT FOREIGN KEY (`guarantor_image`) REFERENCES `m_customer_guarantor` (`id`);

    ALTER TABLE m_image
    ADD customer_image int(11),
    ADD CONSTRAINT FOREIGN KEY (`customer_image`) REFERENCES `m_apply_used_vehicle_loan` (`id`);

     ALTER TABLE m_image
    ADD profile_image int(11);

    ALTER TABLE `m_image`
    ADD INDEX `profile_image` (`profile_image`);


 ALTER TABLE m_appuser
 ADD CONSTRAINT fk_image_id
 FOREIGN KEY (image_id)
REFERENCES `m_image` (`profile_image`);


/* CREATE TABLE `m_image` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `location` VARCHAR(500) NULL DEFAULT NULL,
    `guarantor_image` INT(11) NULL DEFAULT NULL,
    `customer_image` INT(11) NULL DEFAULT NULL,
    `storage_type_enum` SMALLINT(6) NULL DEFAULT NULL,
    PRIMARY KEY (`id`),
    INDEX `FK_guarantor_image` (`guarantor_image`),
    INDEX `customer_image` (`customer_image`),
    CONSTRAINT `FK_guarantor_image` FOREIGN KEY (`guarantor_image`) REFERENCES `m_customer_guarantor` (`id`),
    CONSTRAINT `m_image_ibfk_1` FOREIGN KEY (`customer_image`) REFERENCES `m_apply_used_vehicle_loan` (`id`)
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=2
;
*/
