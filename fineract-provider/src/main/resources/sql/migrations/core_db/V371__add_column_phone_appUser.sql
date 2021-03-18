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
drop table m_customers;

CREATE TABLE `m_customers` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(100) NOT NULL,
    `password` VARCHAR(100) NOT NULL,
    `enabled` SMALLINT(6) NULL DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `username_UK` (`username`)
);

-- ALTER TABLE `m_appuser`
ALTER TABLE m_appuser MODIFY COLUMN email VARCHAR(100) NULL;
ALTER TABLE m_appuser MODIFY COLUMN firstname VARCHAR(100) NULL;
ALTER TABLE m_appuser MODIFY COLUMN lastname VARCHAR(100) NULL;

ALTER TABLE m_appuser
DROP INDEX username_org;

drop table m_appuser_role;

CREATE TABLE `m_appuser_role` (
    `appuser_id` BIGINT(20) NOT NULL,
    `role_id` BIGINT(20) NOT NULL,
    `customer_id` BIGINT(20) NOT NULL,
    PRIMARY KEY (`appuser_id`, `role_id`, `customer_id`),
    INDEX `FK7662CE59B4100309` (`appuser_id`),
    INDEX `FK7662CE5915CEC7AB` (`role_id`),
    INDEX `FK7662CE59B4100` (`customer_id`),
    CONSTRAINT `FK7662CE5915CEC7AB` FOREIGN KEY (`role_id`) REFERENCES `m_role` (`id`),
    CONSTRAINT `FK7662CE59B4100309` FOREIGN KEY (`appuser_id`) REFERENCES `m_appuser` (`id`),
    CONSTRAINT `FK7662CE59B4100` FOREIGN KEY (`customer_id`) REFERENCES `m_customers` (`id`)
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
;

-- add permission for create_customer


/* ALTER TABLE `m_appuser`
ADD COLUMN `mobileNo` BIGINT NULL DEFAULT NULL AFTER `password`;
ALTER TABLE m_appuser
ADD CONSTRAINT mobileNo_UK UNIQUE (mobileNo); */
