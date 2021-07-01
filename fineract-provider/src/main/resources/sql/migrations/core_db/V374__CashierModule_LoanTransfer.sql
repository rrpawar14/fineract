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


CREATE TABLE `m_branchanalytics_dummy_data` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`cashDemand` INT(11) NULL DEFAULT '0',
	`cashCollection` INT(11) NULL DEFAULT '0',
	`loanAmountBankCollection` INT(11) NULL DEFAULT '0',
	`loanAmountCashCollection` INT(11) NULL DEFAULT '0',
	`feAssigned` INT(11) NULL DEFAULT '0',
	`feFollowup` INT(11) NULL DEFAULT '0',
	`feCompleted` INT(11) NULL DEFAULT '0',
	`insuranceExpired` INT(11) NULL DEFAULT '0',
	`insuranceRenewal` INT(11) NULL DEFAULT '0',
	`insuranceHold` INT(11) NULL DEFAULT '0',
	`insuranceCompleted` INT(11) NULL DEFAULT '0',
	`allocatedCash` INT(11) NULL DEFAULT '0',
	`expense` INT(11) NULL DEFAULT '0',
	`agtAssigned` INT(11) NULL DEFAULT '0',
	`agtNotAssigned` INT(11) NULL DEFAULT '0',
	`rtoExpenses` INT(11) NULL DEFAULT '0',
	`rtoCompleted` INT(11) NULL DEFAULT '0',
	`docPending` INT(11) NULL DEFAULT '0',
	`docCompleted` INT(11) NULL DEFAULT '0',
	`disbursalRepossessed` INT(11) NULL DEFAULT '0',
	`disbursalReleased` INT(11) NULL DEFAULT '0',
	`disbursalBank` INT(11) NULL DEFAULT '0',
	`disbursalCash` INT(11) NULL DEFAULT '0',
	`customerLoanDisburse` INT(11) NULL DEFAULT '0',
	`guarantorLoanDisburse` INT(11) NULL DEFAULT '0',
	`thirdPartyLoanDisburse` INT(11) NULL DEFAULT '0',
	`enquiryDirect` INT(11) NULL DEFAULT '0',
	`enquiryWalkIn` INT(11) NULL DEFAULT '0',
	`enquiryReloan` INT(11) NULL DEFAULT '0',
	`newloan` INT(11) NULL DEFAULT '0',
	`closedloan` INT(11) NULL DEFAULT '0',
	`bank_cash_collection` INT(11) NULL DEFAULT '0',
	`task_details_bm_open` INT(11) NULL DEFAULT '0',
	`task_details_bm_closed` INT(11) NULL DEFAULT '0',
	`task_details_bm_inprogress` INT(11) NULL DEFAULT '0',
	`task_details_ho_open` INT(11) NULL DEFAULT '0',
	`task_details_ho_closed` INT(11) NULL DEFAULT '0',
	`task_details_ho_inprogress` INT(11) NULL DEFAULT '0',
	`customerOnLine` INT(11) NULL DEFAULT '0',
	`customerLoanTransferred` INT(11) NULL DEFAULT '0',
	`newBankAccount` INT(11) NULL DEFAULT '0',
	`loanAmountTransferred` INT(11) NULL DEFAULT '0',
	`BankAccountVerified` INT(11) NULL DEFAULT '0',
	INDEX `id` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=3
;


CREATE TABLE `m_cashiermodule_task` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`taskType` VARCHAR(50) NULL DEFAULT NULL,
	`customer_reg_no` VARCHAR(50) NULL DEFAULT NULL,
	`customer_mobile_no` VARCHAR(50) NULL DEFAULT NULL,
	`vehicle_number` VARCHAR(50) NULL DEFAULT NULL,
	`due_date` DATE NULL DEFAULT NULL,
	`assign_to` VARCHAR(50) NULL DEFAULT NULL,
	`assign_by` VARCHAR(50) NULL DEFAULT NULL,
	`description` VARCHAR(50) NULL DEFAULT NULL,
	`status` VARCHAR(50) NULL DEFAULT NULL,
	`branch` VARCHAR(50) NULL DEFAULT NULL,
	`created_date` DATE NULL DEFAULT NULL,
	INDEX `id` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=3
;

CREATE TABLE `m_cashier_task` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`taskType` VARCHAR(50) NULL DEFAULT NULL,
	`customer_reg_no` VARCHAR(50) NULL DEFAULT NULL,
	`customer_mobile_no` VARCHAR(50) NULL DEFAULT NULL,
	`vehicle_number` VARCHAR(50) NULL DEFAULT NULL,
	`due_date` DATE NULL DEFAULT NULL,
	`assign_to` VARCHAR(50) NULL DEFAULT NULL,
	`assign_by` VARCHAR(50) NULL DEFAULT NULL,
	`description` VARCHAR(50) NULL DEFAULT NULL,
	`status` VARCHAR(50) NULL DEFAULT NULL,
	`created_date` DATE NULL DEFAULT NULL,
	INDEX `id` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=2
;

CREATE TABLE `m_fe_cashinhand` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(50) NULL DEFAULT NULL,
	`fe_id` BIGINT(20) NULL DEFAULT NULL,
	`cash_in_hand` BIGINT(20) NULL DEFAULT NULL,
	`cash_limit` BIGINT(20) NULL DEFAULT '0',
	`required_on` DATE NULL DEFAULT NULL,
	`required_amount` BIGINT(20) NULL DEFAULT NULL,
	`status` VARCHAR(50) NULL DEFAULT NULL,
	INDEX `id` (`id`),
	INDEX `fe_id_org` (`fe_id`),
	CONSTRAINT `fe_id_org` FOREIGN KEY (`fe_id`) REFERENCES `m_fieldexecutive` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=7
;


CREATE TABLE `m_hl_payment` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`post_date` DATE NULL DEFAULT NULL,
	`post_type` VARCHAR(50) NULL DEFAULT NULL,
	`agent` VARCHAR(50) NULL DEFAULT NULL,
	INDEX `id` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;


CREATE TABLE `m_hl_payment_data` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`hl_payment_id` BIGINT(20) NULL DEFAULT '0',
	`agtno` VARCHAR(50) NULL DEFAULT '0',
	`customer_name` VARCHAR(50) NULL DEFAULT '0',
	`actual_amount` DECIMAL(10,2) NULL DEFAULT '0.00',
	`post_amount` DECIMAL(10,2) NULL DEFAULT '0.00',
	`expiry_date` DATE NULL DEFAULT NULL,
	`policy_no` VARCHAR(50) NULL DEFAULT '0',
	`insurance_company` VARCHAR(50) NULL DEFAULT '0',
	`remark` VARCHAR(50) NULL DEFAULT '0',
	INDEX `id` (`id`),
	INDEX `hl_payment_id` (`hl_payment_id`),
	CONSTRAINT `hl_payment_org` FOREIGN KEY (`hl_payment_id`) REFERENCES `m_hl_payment` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;


CREATE TABLE `m_loantransfer_task` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`taskType` VARCHAR(50) NULL DEFAULT NULL,
	`customer_reg_no` VARCHAR(50) NULL DEFAULT NULL,
	`customer_mobile_no` VARCHAR(50) NULL DEFAULT NULL,
	`vehicle_number` VARCHAR(50) NULL DEFAULT NULL,
	`due_date` DATE NULL DEFAULT NULL,
	`assign_to` VARCHAR(50) NULL DEFAULT NULL,
	`assignBy` VARCHAR(50) NULL DEFAULT NULL,
	`branch` VARCHAR(50) NULL DEFAULT NULL,
	`description` VARCHAR(100) NULL DEFAULT NULL,
	`status` VARCHAR(100) NULL DEFAULT NULL,
	`created_date` DATE NULL DEFAULT NULL,
	INDEX `id` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
;


CREATE TABLE `m_voucher` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`created_date` DATE NULL DEFAULT NULL,
	`voucher_type` VARCHAR(50) NULL DEFAULT NULL,
	`voucher_number` VARCHAR(50) NULL DEFAULT NULL,
	`remarks` VARCHAR(50) NULL DEFAULT NULL,
	INDEX `id` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;


CREATE TABLE `m_voucher_data` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`voucher_id` BIGINT(20) NOT NULL,
	`credit` DECIMAL(10,2) NULL DEFAULT NULL,
	`debit` DECIMAL(10,2) NULL DEFAULT NULL,
	`particulars` VARCHAR(50) NULL DEFAULT NULL,
	INDEX `id` (`id`),
	INDEX `voucher_id` (`voucher_id`),
	CONSTRAINT `voucher_org` FOREIGN KEY (`voucher_id`) REFERENCES `m_voucher` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;





