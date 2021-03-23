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
;

CREATE TABLE `m_apply_new_vehicle_loan` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `customer_name` VARCHAR(50) NULL DEFAULT NULL,
    `vehicle_type` INT(11) NULL DEFAULT NULL,
    `dealer` VARCHAR(50) NULL DEFAULT NULL,
    `invoice_number` VARCHAR(50) NULL DEFAULT NULL,
    `invoice_image` INT(11) NULL DEFAULT NULL,
    `address_id` VARCHAR(50) NULL DEFAULT NULL,
    `customer_details_id` VARCHAR(50) NULL DEFAULT NULL,
    `vehicle_details_id` VARCHAR(50) NULL DEFAULT NULL,
    `guarantor_details_id` VARCHAR(50) NULL DEFAULT NULL,
    `bank_details_id` VARCHAR(50) NULL DEFAULT NULL,
    INDEX `id` (`id`)
/*    INDEX `FK_address_id` (`address_id`),
    INDEX `FK_customer_details_id` (`customer_details_id`),
    INDEX `FK_vehicle_details_id` (`vehicle_details_id`),
    INDEX `FK_guarantor_details_id` (`guarantor_details_id`),
    INDEX `FK_bank_details_id` (`bank_details_id`),

CONSTRAINT `FK_address_id` FOREIGN KEY (`address_id`) REFERENCES `m_address` (`id`),
CONSTRAINT `FK_customer_details_id` FOREIGN KEY (`customer_details_id`) REFERENCES `m_customer_details` (`id`),
CONSTRAINT `FK_vehicle_details_id` FOREIGN KEY (`vehicle_details_id`) REFERENCES `m_vehicle_details` (`id`),
CONSTRAINT `FK_guarantor_details_id` FOREIGN KEY (`guarantor_details_id`) REFERENCES `m_customer_guarantor` (`id`),
CONSTRAINT `FK_bank_details_id` FOREIGN KEY (`bank_details_id`) REFERENCES `m_customer_bank_details` (`id`)*/
);

CREATE TABLE `m_apply_used_vehicle_loan` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `customer_name` VARCHAR(50) NULL DEFAULT NULL,
    `vehicle_type` VARCHAR(50) NULL DEFAULT NULL,
    `loan_type` VARCHAR(50) NULL DEFAULT NULL,
    `address_id` VARCHAR(50) NULL DEFAULT NULL,
    `customer_details_id` VARCHAR(50) NULL DEFAULT NULL,
    `vehicle_details_id` VARCHAR(50) NULL DEFAULT NULL,
    `guarantor_details_id` VARCHAR(50) NULL DEFAULT NULL,
    `bank_details_id` VARCHAR(50) NULL DEFAULT NULL,
    INDEX `id` (`id`)
/*    INDEX `FK_address_id` (`address_id`),
    INDEX `FK_customer_details_id` (`customer_details_id`),
    INDEX `FK_vehicle_details_id` (`vehicle_details_id`),
    INDEX `FK_guarantor_details_id` (`guarantor_details_id`),
    INDEX `FK_bank_details_id` (`bank_details_id`),

CONSTRAINT `FK_address_id` FOREIGN KEY (`address_id`) REFERENCES `m_address` (`id`),
CONSTRAINT `FK_customer_details_id` FOREIGN KEY (`customer_details_id`) REFERENCES `m_customer_details` (`id`),
CONSTRAINT `FK_vehicle_details_id` FOREIGN KEY (`vehicle_details_id`) REFERENCES `m_vehicle_details` (`id`),
CONSTRAINT `FK_guarantor_details_id` FOREIGN KEY (`guarantor_details_id`) REFERENCES `m_customer_guarantor` (`id`),
CONSTRAINT `FK_bank_details_id` FOREIGN KEY (`bank_details_id`) REFERENCES `m_customer_bank_details` (`id`)*/
);



ALTER table m_address
ADD landmark varchar(100),
ADD area varchar(100),
ADD state varchar(100);


CREATE TABLE `m_customer_details` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NULL DEFAULT '0',
    `gender` SMALLINT(6) NULL DEFAULT '0',
    `dob` DATE NULL DEFAULT NULL,
    `maritalstatus` SMALLINT(6) NULL DEFAULT NULL,
    `spousename` VARCHAR(100) NULL DEFAULT NULL,
    `profession` VARCHAR(100) NULL DEFAULT NULL,
    `proof_image_id` INT(11) NULL DEFAULT NULL,
    INDEX `id` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
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
;


CREATE TABLE `m_customer_bank_details` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `loan_eligible_amount` BIGINT(20) NOT NULL DEFAULT '0',
    `account_type` VARCHAR(50) NOT NULL DEFAULT '0',
    `disbursal_type` VARCHAR(50) NOT NULL DEFAULT '0',
    `account_number` VARCHAR(50) NOT NULL DEFAULT '0',
    `account_holder_name` VARCHAR(50) NOT NULL DEFAULT '0',
    `bank_name` VARCHAR(50) NOT NULL DEFAULT '0',
    `branch_name` VARCHAR(50) NOT NULL DEFAULT '0',
    `IFSC` VARCHAR(50) NOT NULL DEFAULT '0',
    `passbook_image_id` INT(11) NOT NULL DEFAULT '0',
    INDEX `id` (`id`)
)
ENGINE=InnoDB
;