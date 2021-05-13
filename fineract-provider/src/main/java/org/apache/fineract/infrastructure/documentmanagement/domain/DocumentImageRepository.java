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
package org.apache.fineract.infrastructure.documentmanagement.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentImageRepository extends JpaRepository<DocumentImages, Long>, JpaSpecificationExecutor<DocumentImages> {

    @Query("select docim from DocumentImages docim where docim.customerDetails.id = :entityId and docim.entityName = :entityName ")
    DocumentImages getCustomerDocumentImageByEntityNameandEntityId(@Param("entityId") Long entityId,
            @Param("entityName") String entityName);

    @Query("select docim from DocumentImages docim where docim.customerGuarantor.id = :entityId and docim.entityName = :entityName ")
    DocumentImages getGurantorDocumentImageByEntityNameandEntityId(@Param("entityId") Long entityId,
            @Param("entityName") String entityName);

    @Query("select docim from DocumentImages docim where docim.employee.id = :entityId and docim.entityName = :entityName ")
    DocumentImages getEmployeeDocumentImageByEntityNameandEntityId(@Param("entityId") Long entityId,
            @Param("entityName") String entityName);

    @Query("select docim from DocumentImages docim where docim.bankImage.id = :entityId and docim.entityName = :entityName ")
    DocumentImages getBankDocumentImageByEntityNameandEntityId(@Param("entityId") Long entityId, @Param("entityName") String entityName);

    @Query("select docim from DocumentImages docim where docim.vehicleLoan.id = :entityId and docim.entityName = :entityName ")
    DocumentImages getLoanDocumentImageByEntityNameandEntityId(@Param("entityId") Long entityId, @Param("entityName") String entityName);

    @Query("select docim from DocumentImages docim where docim.feEnroll.id = :entityId and docim.entityName = :entityName ")
    DocumentImages getFEEnrollDocumentImageByEntityNameandEntityId(@Param("entityId") Long entityId,
            @Param("entityName") String entityName);

}
