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
package org.apache.fineract.vlms.fieldexecutive.domain;

import java.io.Serializable;

/**
 * Immutable data object representing a code.
 */

public class DocumentsData implements Serializable {

    // private final Long id;
    @SuppressWarnings("unused")
    private final String documentName;
    // @SuppressWarnings("unused")
    // private final boolean status;

    public static DocumentsData instance(final String documentName) {
        return new DocumentsData(documentName);
    }

    private DocumentsData(final String documentName) {
        // this.id = id;
        this.documentName = documentName;
        // this.status = status;
    }

    /*
     * public static DocumentsData instance(final Long id, final String documentName, final boolean status) { return new
     * DocumentsData(id, documentName, status); }
     *
     * private DocumentsData(final Long id, final String documentName, final String status) { this.id = id;
     * this.documentName = documentName; this.status = status; }
     */
    /*
     * public Long getDocumentId() { return this.id; }
     */
}
