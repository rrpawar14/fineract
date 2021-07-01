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
ALTER TABLE m_appuser drop column firstname;
ALTER TABLE m_appuser drop column lastname;
ALTER TABLE m_appuser add column name VARCHAR(100) NULL after username;
ALTER TABLE m_appuser add column transaction_pin VARCHAR(100) NULL after username;

ALTER TABLE m_appuser ADD COLUMN image_id BIGINT(20) NULL;


 ALTER table m_appuser
 ADD customer_id BIGINT(100);

 ALTER TABLE `m_appuser`
 ADD INDEX `customer_id` (`customer_id`);


 ALTER TABLE m_appuser
 ADD CONSTRAINT fk_image_id
 FOREIGN KEY (image_id)
 REFERENCES `m_image` (`id`);


-- create Loan Enquiry Table

CREATE TABLE `m_customerloanenquiry` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`user_id` VARCHAR(50) NOT NULL DEFAULT '0',
	`customer_name` VARCHAR(50) NOT NULL DEFAULT '0',
	`mobile_number` VARCHAR(50) NULL DEFAULT NULL,
	`vehicle_number` VARCHAR(50) NULL DEFAULT '0',
	`email` VARCHAR(50) NULL DEFAULT '0',
	`pincode` VARCHAR(50) NULL DEFAULT '0',
	`enquiry_id` VARCHAR(50) NULL DEFAULT '0',
	`notes` VARCHAR(100) NOT NULL DEFAULT '0',
	`created_date` DATE NULL DEFAULT NULL,
	INDEX `id` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;


CREATE TABLE `m_customer_guarantor` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`guarantor_name` VARCHAR(100) NULL DEFAULT 'NULL',
	`mobile_number` VARCHAR(100) NULL DEFAULT 'NULL',
	`gender` VARCHAR(100) NULL DEFAULT '0',
	`age` INT(11) NULL DEFAULT '0',
	`guarantor_addressType` VARCHAR(50) NULL DEFAULT '0',
	`physical_challenge` VARCHAR(50) NULL DEFAULT '0',
	`dob` DATE NULL DEFAULT NULL,
	`marital_status` VARCHAR(50) NULL DEFAULT '0',
	`spouse_name` VARCHAR(100) NULL DEFAULT '0',
	`father_name` VARCHAR(100) NULL DEFAULT '0',
	`profession` VARCHAR(50) NULL DEFAULT '0',
	`applicant_type` VARCHAR(50) NULL DEFAULT '0',
	`employeeType` VARCHAR(50) NULL DEFAULT '0',
	`individualType` VARCHAR(50) NULL DEFAULT '0',
	`relation_with_customer` VARCHAR(50) NULL DEFAULT '0',
	`company_name` VARCHAR(50) NULL DEFAULT '0',
	`net_income` BIGINT(20) NULL DEFAULT '0',
	`salary_date` DATE NULL DEFAULT NULL,
	`salary_type` VARCHAR(50) NULL DEFAULT NULL,
	`communicationadd_id` BIGINT(11) NULL DEFAULT NULL,
	`permanentadd_id` BIGINT(11) NULL DEFAULT NULL,
	`officeadd_id` BIGINT(11) NULL DEFAULT NULL,
	INDEX `id` (`id`),
	INDEX `fk_guarantorcommunicationadd` (`communicationadd_id`),
	INDEX `fk_guarantorpermenantadd` (`permanentadd_id`),
	INDEX `fk_guarantorofficeadd` (`officeadd_id`),
	CONSTRAINT `fk_guarantorcommunicationadd` FOREIGN KEY (`communicationadd_id`) REFERENCES `m_address` (`id`),
	CONSTRAINT `fk_guarantorofficeadd` FOREIGN KEY (`officeadd_id`) REFERENCES `m_address` (`id`),
	CONSTRAINT `fk_guarantorpermenantadd` FOREIGN KEY (`permanentadd_id`) REFERENCES `m_address` (`id`)
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
	`age` INT(11) NULL DEFAULT '0',
	`ages` INT(11) NULL DEFAULT '0',
	`name` VARCHAR(100) NULL DEFAULT '0',
	`employeeType` VARCHAR(100) NULL DEFAULT '0',
	`individualType` VARCHAR(100) NULL DEFAULT '0',
	`physical_challenge` VARCHAR(100) NULL DEFAULT '0',
	`physical_challenges` VARCHAR(100) NULL DEFAULT '0',
	`gender` VARCHAR(20) NULL DEFAULT '0',
	`mobile_number` VARCHAR(20) NULL DEFAULT '0',
	`account_no` VARCHAR(20) NULL DEFAULT '0',
	`display_name` VARCHAR(20) NULL DEFAULT '0',
	`transfer_to_office_id` VARCHAR(20) NULL DEFAULT '0',
	`addressType` VARCHAR(20) NULL DEFAULT '0',
	`office_id` BIGINT(20) NULL DEFAULT NULL,
	`branch` BIGINT(20) NULL DEFAULT NULL,
	`father_name` VARCHAR(20) NULL DEFAULT '0',
	`alternate_number` VARCHAR(20) NULL DEFAULT '0',
	`applicant_type` VARCHAR(20) NULL DEFAULT '0',
	`refer_by` VARCHAR(20) NULL DEFAULT '0',
	`company_name` VARCHAR(20) NULL DEFAULT '0',
	`monthly_income` VARCHAR(20) NULL DEFAULT '0',
	`salary_date` DATE NULL DEFAULT NULL,
	`salary_period` VARCHAR(50) NULL DEFAULT '0',
	`dob` DATE NULL DEFAULT NULL,
	`marital_status` VARCHAR(50) NULL DEFAULT NULL,
	`spousename` VARCHAR(100) NULL DEFAULT NULL,
	`profession` VARCHAR(100) NULL DEFAULT NULL,
	`proof_image_id` BIGINT(11) NULL DEFAULT NULL,
	`communicationadd_id` BIGINT(11) NULL DEFAULT NULL,
	`permanentadd_id` BIGINT(11) NULL DEFAULT NULL,
	`officeadd_id` BIGINT(11) NULL DEFAULT NULL,
	INDEX `id` (`id`),
	INDEX `FK_proof_image_id` (`proof_image_id`),
	INDEX `fk_communicationadd` (`communicationadd_id`),
	INDEX `fk_permenantadd` (`permanentadd_id`),
	INDEX `fk_officeadd` (`officeadd_id`),
	INDEX `parent_office` (`office_id`),
	CONSTRAINT `FK_proof_image_id` FOREIGN KEY (`proof_image_id`) REFERENCES `m_image` (`id`),
	CONSTRAINT `fk_communicationadd` FOREIGN KEY (`communicationadd_id`) REFERENCES `m_address` (`id`),
	CONSTRAINT `fk_officeadd` FOREIGN KEY (`officeadd_id`) REFERENCES `m_address` (`id`),
	CONSTRAINT `fk_permenantadd` FOREIGN KEY (`permanentadd_id`) REFERENCES `m_address` (`id`),
	CONSTRAINT `m_office_orgg` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`)
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
	`dealer_mobile_number` VARCHAR(50) NULL DEFAULT NULL,
	INDEX `id` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;


CREATE TABLE `m_customer_bank_details` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`payment_type_id` INT(11) NULL DEFAULT '0',
	`loan_eligible_amount` BIGINT(20) NULL DEFAULT '0',
	`to_pay` VARCHAR(50) NULL DEFAULT '0',
	`account_type` VARCHAR(50) NULL DEFAULT '0',
	`transfer_mode` VARCHAR(50) NULL DEFAULT '0',
	`transfer_type` VARCHAR(50) NULL DEFAULT '0',
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


CREATE TABLE `m_apply_vehicle_loan` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`customer_name` VARCHAR(50) NULL DEFAULT NULL,
	`vehicle_type` VARCHAR(50) NULL DEFAULT NULL,
	`advance_for_new_vehicle` VARCHAR(50) NULL DEFAULT NULL,
	`dealer` VARCHAR(50) NULL DEFAULT NULL,
	`invoice_number` VARCHAR(50) NULL DEFAULT NULL,
	`created_date` DATE NULL DEFAULT NULL,
	`customerdetails_id` INT(11) NULL DEFAULT NULL,
	`vehicledetails_id` INT(50) NULL DEFAULT NULL,
	`guarantordetails_id` INT(50) NULL DEFAULT NULL,
	`bankdetails_id` INT(50) NULL DEFAULT NULL,
	`loandetails_id` BIGINT(20) NULL DEFAULT NULL,
	`loandetail_id` BIGINT(20) NULL DEFAULT NULL,
	`loandetailq_id` BIGINT(20) NULL DEFAULT NULL,
	`loan_type` VARCHAR(50) NULL DEFAULT NULL,
	`customer_id` BIGINT(20) NULL DEFAULT NULL,
	INDEX `id` (`id`),
	INDEX `FK_customer_details_id` (`customerdetails_id`),
	INDEX `FK_vehicle_details_id` (`vehicledetails_id`),
	INDEX `FK_guarantor_details_id` (`guarantordetails_id`),
	INDEX `FK_loan_details_id` (`loandetails_id`),
	INDEX `FK_bank_details_id` (`bankdetails_id`),
	INDEX `customer_id` (`customer_id`),
	INDEX `m_loan_details_org` (`loandetail_id`),
	CONSTRAINT `FK_bank_details_id` FOREIGN KEY (`bankdetails_id`) REFERENCES `m_customer_bank_details` (`id`),
	CONSTRAINT `FK_customer_details_id` FOREIGN KEY (`customerdetails_id`) REFERENCES `m_customer_details` (`id`),
	CONSTRAINT `FK_guarantor_details_id` FOREIGN KEY (`guarantordetails_id`) REFERENCES `m_customer_guarantor` (`id`),
	CONSTRAINT `FK_loan_details_id` FOREIGN KEY (`loandetails_id`) REFERENCES `m_loan_details` (`id`),
	CONSTRAINT `FK_vehicle_details_id` FOREIGN KEY (`vehicledetails_id`) REFERENCES `m_vehicle_details` (`id`),
	CONSTRAINT `fK_used_vehicle_org` FOREIGN KEY (`customer_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `m_loan_details_org` FOREIGN KEY (`loandetail_id`) REFERENCES `m_loan` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;




INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('customer', 'UPDATE_CUSTOMER', 'CUSTOMER', 'UPDATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('vehicle', 'CREATE_NEWVEHICLELOAN', 'NEWVEHICLELOAN', 'CREATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('vehicle', 'CREATE_USEDVEHICLELOAN', 'USEDVEHICLELOAN', 'CREATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('vehicle', 'CREATE_ENQUIRY', 'ENQUIRY', 'CREATE', 0);





CREATE TABLE `m_vehicle_images` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `location` VARCHAR(500) NULL DEFAULT NULL,
    `vehicle_id` INT(11) NULL DEFAULT NULL,
    `storage_type_enum` SMALLINT(6) NULL DEFAULT NULL,
    `entity_name` VARCHAR(50) NULL DEFAULT NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_used_vehicle_customer` (`vehicle_id`),
    CONSTRAINT `fk_used_vehicle_customer` FOREIGN KEY (`vehicle_id`) REFERENCES `m_vehicle_details` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;
