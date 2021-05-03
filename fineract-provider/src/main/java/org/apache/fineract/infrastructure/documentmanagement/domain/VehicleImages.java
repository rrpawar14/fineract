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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.loanaccount.domain.VehicleDetails;

@Entity
@Table(name = "m_vehicle_images")
public final class VehicleImages extends AbstractPersistableCustom {

    @Column(name = "location", length = 500)
    private String location;

    @Column(name = "entity_name")
    private String entityName;

    @Column(name = "storage_type_enum")
    private Integer storageType;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "vehicle_id", nullable = true)
    private VehicleDetails vehicleDetails;

    public VehicleImages(final String location, final String entityName, final StorageType storageType,
            final VehicleDetails vehicleDetails) {
        this.location = location;
        this.entityName = entityName;
        this.storageType = storageType.getValue();
        this.vehicleDetails = vehicleDetails;
    }

    VehicleImages() {

    }

    public String getLocation() {
        return this.location;
    }

    public Integer getStorageType() {
        return this.storageType;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public void setStorageType(final Integer storageType) {
        this.storageType = storageType;
    }

}
