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
package org.apache.fineract.infrastructure.creditbureau.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.creditbureau.domain.CreditBureauTokenRepository;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreditBureauTokenWritePlatformServiceImpl implements CreditBureauTokenWritePlatformService {

    private final PlatformSecurityContext context;
    private final CreditBureauTokenRepository creditBureauTokenRepository;
    // private final CreditBureauCommandFromApiJsonDeserializer
    // fromApiJsonDeserializer;

    @Autowired
    public CreditBureauTokenWritePlatformServiceImpl(final PlatformSecurityContext context,
            final CreditBureauTokenRepository creditBureauTokenRepository
    // final CreditBureauCommandFromApiJsonDeserializer fromApiJsonDeserializer
    ) {
        this.context = context;
        this.creditBureauTokenRepository = creditBureauTokenRepository;
        // this.fromApiJsonDeserializer = fromApiJsonDeserializer;

    }

    @Override
    public CommandProcessingResult addCreditBureauToken(JsonCommand command) {
        this.context.authenticatedUser();
        // this.fromApiJsonDeserializer.validateForCreate(command.json());

        /*
         * final String SubscriptionKey =
         * command.stringValueOfParameterNamed("SubscriptionKey"); final String
         * SubscriptionId =
         * command.stringValueOfParameterNamed("SubscriptionId"); final String
         * UserName = command.stringValueOfParameterNamed("UserName");
         */

        // final CreditBureau creditBureau =
        // this.creditBureauRepository.getOne(creditBureauId);

        // final CreditBureauToken creditBureauToken =
        // final CreditBureauToken token = CreditBureauToken.fromJson(command);

        // this.creditBureauTokenRepository.save(token);

        // return new
        // CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(CreditBureauToken.getSubscriptionId()).build();
        return new CommandProcessingResultBuilder().withCommandId(command.commandId()).build();
    }

}
