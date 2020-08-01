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


CREATE TABLE `oauth_client_details` (
  `client_id` varchar(128) NOT NULL,
  `resource_ids` varchar(256) DEFAULT NULL,
  `client_secret` varchar(256) DEFAULT NULL,
  `scope` varchar(256) DEFAULT NULL,
  `authorized_grant_types` varchar(256) DEFAULT NULL,
  `web_server_redirect_uri` varchar(256) DEFAULT NULL,
  `authorities` varchar(256) DEFAULT NULL,
  `access_token_validity` INT DEFAULT NULL,
  `refresh_token_validity` INT DEFAULT NULL,
  `additional_information` varchar(4096) DEFAULT NULL,
  `autoapprove` BIT(1) NULL DEFAULT NULL,
  PRIMARY KEY (`client_id`)
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1;

INSERT INTO `oauth_client_details` (`client_id`, `client_secret`, `scope`, `authorized_grant_types`) VALUES ('community-app', '123', 'all', 'password,refresh_token');


CREATE TABLE `oauth_access_token` (
  `token_id` varchar(256) DEFAULT NULL,
  `token` blob,
  `authentication_id` varchar(256) DEFAULT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `client_id` varchar(256) DEFAULT NULL,
  `authentication` blob,
  `refresh_token` varchar(256) DEFAULT NULL
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1;

CREATE TABLE `oauth_refresh_token` (
  `token_id` varchar(256) DEFAULT NULL,
  `token` blob,
  `authentication` blob
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB;

CREATE TABLE `m_creditbureau_tokendata` (
  `id` INT(128) NOT NULL AUTO_INCREMENT,
  `userNames` varchar(128) DEFAULT NULL,
  `password` varchar(128) DEFAULT NULL,
  `subscription_id` varchar(128) DEFAULT NULL,
  `subscription_key` varchar(128) DEFAULT NULL,

  PRIMARY KEY (`id`)
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB;

INSERT INTO `m_creditbureau_tokendata` (`id`, `userNames`, `password`, `subscription_id`, `subscription_key`) VALUES
('1', 'demomfi1@gmail.com','Sampleuser123*' ,'317A1FF8-625D-41BA-BE0F-F8ED8A644A7C', 'cb225c15ff1742feab2f1fb444393ace'),
('2', 'demomfi2@gmail.com','Sampleuser123*' ,'B76FEFF5-5B42-4A36-AFDA-AB5C7398220C', '91ce69a972b14c7fab057788fe61ce8a'),
('3', 'demomfi3@gmail.com','Sampleuser123*' ,'31333A80-BDE7-43EE-B1FD-6699C518AF85', '86f4f58542554e0ba9a309003eadd1fc'),
('4', 'demomfi4@gmail.com','Sampleuser123*' ,'C929C107-E779-4233-8493-176CF6DEA251', '302ed3b928df43ccb0de97f008b07320');

-- permissions added


INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('configuration', 'CREATE_CREDITBUREAUTOKEN', 'CREDITBUREAUTOKEN', 'CREATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('configuration', 'CREATE_CREDITBUREAUTOKENDATA', 'CREDITBUREAUTOKENDATA', 'CREATE', 0);

CREATE TABLE `m_creditbureau_token` (
  `id` varchar(128) NOT NULL AUTO_INCREMENT,
  `username` varchar(128) DEFAULT NULL,
  `token` varchar(1000) DEFAULT NULL,
  `token_type` varchar(128) DEFAULT NULL,
  `expires_in` varchar(128) DEFAULT NULL,
  `issued` varchar(128) DEFAULT NULL,
  `expires` varchar(128) DEFAULT NULL,


  PRIMARY KEY (`id`)
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB;
