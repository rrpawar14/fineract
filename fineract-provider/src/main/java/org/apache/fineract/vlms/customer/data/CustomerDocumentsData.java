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
package org.apache.fineract.vlms.customer.data;

import java.io.Serializable;

/**
 * Immutable data object representing loan summary information.
 */
@SuppressWarnings("unused")
public class CustomerDocumentsData implements Serializable {

    private final Long id;

    private final String entityName;

    private final String documentNumber;

    public static CustomerDocumentsData instance(final Long id, final String entityName, final String documentNumber) {

        return new CustomerDocumentsData(id, entityName, documentNumber);
    }

    public CustomerDocumentsData(final Long id, final String entityName, final String documentNumber) {
        this.id = id;
        this.entityName = entityName;
        this.documentNumber = documentNumber;
    }

}
