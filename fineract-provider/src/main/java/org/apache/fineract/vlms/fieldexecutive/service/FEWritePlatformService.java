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
package org.apache.fineract.vlms.fieldexecutive.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface FEWritePlatformService {

    CommandProcessingResult createFEEnquiry(JsonCommand command);

    // CommandProcessingResult updateFEEnquiry(JsonCommand command);

    CommandProcessingResult createFEEnroll(JsonCommand command);

    CommandProcessingResult createNewLoan(JsonCommand command);

    CommandProcessingResult createFEUsedVehicleLoan(JsonCommand command);

    CommandProcessingResult createFETask(JsonCommand command);

    CommandProcessingResult editFETask(Long taskId, JsonCommand command);

    CommandProcessingResult deleteFETask(Long taskId, JsonCommand command);
}
