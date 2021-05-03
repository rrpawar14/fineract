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
CREATE TABLE `m_education_qualification` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `school_university` VARCHAR(50) NOT NULL DEFAULT '0',
    `qualification` VARCHAR(50) NOT NULL DEFAULT '0',
    `percentage` VARCHAR(50) NOT NULL DEFAULT '0',
    `passingyear` VARCHAR(50) NOT NULL DEFAULT '0',
    INDEX `id` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
;

CREATE TABLE `m_insurancedetails` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `policynumber` VARCHAR(50) NOT NULL DEFAULT '0',
    `companycoverage` VARCHAR(50) NOT NULL DEFAULT '0',
    `policycoverage` VARCHAR(50) NOT NULL DEFAULT '0',
    INDEX `id` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
;


CREATE TABLE `m_employee` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NULL DEFAULT '0',
    `calledname` VARCHAR(50) NULL DEFAULT '0',
    `surname` VARCHAR(50) NULL DEFAULT '0',
    `mobilenumber` VARCHAR(50) NULL DEFAULT '0',
    `alternatenumber` VARCHAR(50) NULL DEFAULT '0',
    `officialnumber` VARCHAR(50) NULL DEFAULT '0',
    `dob` DATE NULL DEFAULT NULL,
    `gender` VARCHAR(50) NULL DEFAULT '0',
    `age` INT(11) NULL DEFAULT '0',
    `maritalstatus` VARCHAR(50) NULL DEFAULT '0',
    `designation` VARCHAR(50) NULL DEFAULT '0',
    `spousename` VARCHAR(50) NULL DEFAULT '0',
    `bloodgroup` VARCHAR(50) NULL DEFAULT '0',
    `fathername` VARCHAR(50) NULL DEFAULT '0',
    `vehiclenumber` VARCHAR(50) NULL DEFAULT '0',
    `vehicleType` VARCHAR(50) NULL DEFAULT '0',
    `doj` DATE NULL DEFAULT NULL,
    `agtnumber` VARCHAR(50) NULL DEFAULT '0',
    `communicationadd_id` BIGINT(20) NULL DEFAULT '0',
    `permanentadd_id` BIGINT(20) NULL DEFAULT '0',
    `bankdetails_id` INT(11) NULL DEFAULT '0',
    `insurancedetails_id` BIGINT(20) NULL DEFAULT '0',
    `accidentalinsurancedetails_id` BIGINT(20) NULL DEFAULT '0',
    `schoolqualification_id` BIGINT(20) NULL DEFAULT '0',
    `collegequalification_id` BIGINT(20) NULL DEFAULT '0',
    `graduatequalification_id` BIGINT(20) NULL DEFAULT '0',
    `postgraduatequalification_id` BIGINT(20) NULL DEFAULT '0',
    INDEX `id` (`id`),
    INDEX `fk_commaddress_org` (`communicationadd_id`),
    INDEX `fk_permanentaddress_org` (`permanentadd_id`),
    INDEX `fk_bankdetails_org` (`bankdetails_id`),
    INDEX `fk_generalinsurance_org` (`insurancedetails_id`),
    INDEX `fk_accidentalinsurance_org` (`accidentalinsurancedetails_id`),
    INDEX `fk_schoolqualification_org` (`schoolqualification_id`),
    INDEX `fk_collegequalification_org` (`collegequalification_id`),
    INDEX `fk_graduatequalification_org` (`graduatequalification_id`),
    INDEX `fk_postgraduatequalification_org` (`postgraduatequalification_id`),
    CONSTRAINT `fk_accidentalinsurance_org` FOREIGN KEY (`accidentalinsurancedetails_id`) REFERENCES `m_insurancedetails` (`id`),
    CONSTRAINT `fk_bankdetails_org` FOREIGN KEY (`bankdetails_id`) REFERENCES `m_customer_bank_details` (`id`),
    CONSTRAINT `fk_collegequalification_org` FOREIGN KEY (`collegequalification_id`) REFERENCES `m_education_qualification` (`id`),
    CONSTRAINT `fk_commaddress_org` FOREIGN KEY (`communicationadd_id`) REFERENCES `m_address` (`id`),
    CONSTRAINT `fk_generalinsurance_org` FOREIGN KEY (`insurancedetails_id`) REFERENCES `m_insurancedetails` (`id`),
    CONSTRAINT `fk_graduatequalification_org` FOREIGN KEY (`graduatequalification_id`) REFERENCES `m_education_qualification` (`id`),
    CONSTRAINT `fk_permanentaddress_org` FOREIGN KEY (`permanentadd_id`) REFERENCES `m_address` (`id`),
    CONSTRAINT `fk_postgraduatequalification_org` FOREIGN KEY (`postgraduatequalification_id`) REFERENCES `m_education_qualification` (`id`),
    CONSTRAINT `fk_schoolqualification_org` FOREIGN KEY (`schoolqualification_id`) REFERENCES `m_education_qualification` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
;

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('vehicle', 'CREATE_EMPLOYEE', 'EMPLOYEE', 'CREATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('vehicle', 'EDIT_FETASK', 'FETASK', 'EDIT', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('vehicle', 'DELETE_FETASK', 'FETASK', 'DELETE', 0);
