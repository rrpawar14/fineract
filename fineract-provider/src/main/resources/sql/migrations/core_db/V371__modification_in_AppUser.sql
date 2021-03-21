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
    INDEX `id` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
;
