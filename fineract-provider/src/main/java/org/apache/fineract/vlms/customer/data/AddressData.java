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

/**
 * Immutable data object representing loan summary information.
 */
@SuppressWarnings("unused")
public class AddressData {

    private final String addressLine1;

    private final String addressLine2;

    private final String city;

    private final String postalCode;

    private final String landmark;

    private final String area;

    private final String state;

    public static AddressData instance(final String addressLine1, final String addressLine2, final String city, final String postalCode,
            final String landmark, final String area, final String state) {

        return new AddressData(addressLine1, addressLine2, city, postalCode, landmark, area, state);
    }

    public AddressData(final String addressLine1, final String addressLine2, final String city, final String postalCode,
            final String landmark, final String area, final String state) {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.postalCode = postalCode;
        this.landmark = landmark;
        this.area = area;
        this.state = state;
    }
}
