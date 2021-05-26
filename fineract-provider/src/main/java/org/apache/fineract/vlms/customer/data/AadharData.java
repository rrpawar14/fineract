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
public class AadharData {

    private final String uid;

    private final String name;

    private final String gender;

    private final String co;

    private final String dist;

    private final String lm;

    private final String house;

    private final String loc;

    private final String pc;

    private final String po;

    private final String state;

    private final String street;

    private final String subdist;

    private final String vtc;

    public static AadharData instance(final String uid, final String name, final String gender, final String co, final String dist,
            final String lm, final String house, final String loc, final String pc, final String po, final String state,
            final String street, final String subdist, final String vtc) {

        return new AadharData(uid, name, gender, co, dist, lm, house, loc, pc, po, state, street, subdist, vtc);
    }

    public AadharData(final String uid, final String name, final String gender, final String co, final String dist, final String lm,
            final String house, final String loc, final String pc, final String po, final String state, final String street,
            final String subdist, final String vtc) {
        this.uid = uid;

        this.name = name;

        this.gender = gender;

        this.co = co;

        this.dist = dist;

        this.lm = lm;

        this.house = house;

        this.loc = loc;

        this.pc = pc;

        this.po = po;

        this.state = state;

        this.street = street;

        this.subdist = subdist;

        this.vtc = vtc;
    }

}
