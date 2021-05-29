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
package org.apache.fineract.vlms.cashier.service;

import javax.persistence.PersistenceException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.vlms.cashier.domain.HLPayment;
import org.apache.fineract.vlms.cashier.domain.HLPaymentRepository;
import org.apache.fineract.vlms.cashier.domain.Voucher;
import org.apache.fineract.vlms.cashier.domain.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CashierModuleWritePlatformServiceImpl implements CashierModuleWritePlatformService {

    private final PlatformSecurityContext context;
    private final HLPaymentRepository hlPaymentRepository;
    private final VoucherRepository voucherRepository;

    @Autowired
    public CashierModuleWritePlatformServiceImpl(final PlatformSecurityContext context, final HLPaymentRepository hlPaymentRepository,
            final VoucherRepository voucherRepository) {
        this.context = context;
        this.hlPaymentRepository = hlPaymentRepository;
        this.voucherRepository = voucherRepository;
    }

    @Transactional
    @Override
    @CacheEvict(value = "hlpayment", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('cv')")
    public CommandProcessingResult createHLPayment(final JsonCommand command) {

        try {
            final HLPayment hlPayment = HLPayment.fromJson(command);

            this.hlPaymentRepository.save(hlPayment);

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(hlPayment.getId()).build();
            // .withEntityId(code.getId())
        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return CommandProcessingResult.empty();
        } catch (final PersistenceException ee) {
            Throwable throwable = ExceptionUtils.getRootCause(ee.getCause());
            handleDataIntegrityIssues(command, throwable, ee);
            return CommandProcessingResult.empty();
        }

    }

    @Transactional
    @Override
    @CacheEvict(value = "voucher", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('cv')")
    public CommandProcessingResult createVoucher(final JsonCommand command) {

        try {
            final Voucher voucher = Voucher.fromJson(command);

            this.voucherRepository.save(voucher);

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(voucher.getId()).build();
            // .withEntityId(code.getId())
        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return CommandProcessingResult.empty();
        } catch (final PersistenceException ee) {
            Throwable throwable = ExceptionUtils.getRootCause(ee.getCause());
            handleDataIntegrityIssues(command, throwable, ee);
            return CommandProcessingResult.empty();
        }

    }

    private void handleDataIntegrityIssues(final JsonCommand command, final Throwable realCause, final Exception dve) {

        if (realCause.getMessage().contains("mobile_no")) {
            final String mobileNo = command.stringValueOfParameterNamed("mobileNo");
            throw new PlatformDataIntegrityException("error.msg.client.duplicate.mobileNo",
                    "Client with mobileNo `" + mobileNo + "` already exists", "mobileNo", mobileNo);
        }

        throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

}
