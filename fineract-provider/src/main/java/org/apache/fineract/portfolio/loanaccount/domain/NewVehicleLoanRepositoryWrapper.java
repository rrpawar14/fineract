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
package org.apache.fineract.portfolio.loanaccount.domain;

import java.util.Collection;
import java.util.List;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.loanaccount.exception.LoanTransactionProcessingStrategyNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NewVehicleLoanRepositoryWrapper {

    private final NewVehicleLoanRepository repository;
    private final PlatformSecurityContext context;

    @Autowired
    public NewVehicleLoanRepositoryWrapper(final NewVehicleLoanRepository repository, final PlatformSecurityContext context) {
        this.repository = repository;
        this.context = context;
    }

    public NewVehicleLoan findOneWithNotFoundDetection(final Long id) {
        return this.findOneWithNotFoundDetection(id, false);
    }

    @Transactional(readOnly = true)
    public NewVehicleLoan findOneWithNotFoundDetection(final Long applicationId, final boolean loadLazyCollections) {

        final NewVehicleLoan newVehicleLoan = this.repository.findById(applicationId)
                .orElseThrow(() -> new LoanTransactionProcessingStrategyNotFoundException(applicationId));
        /*
         * if (loadLazyCollections) { NewVehicleLoan.loadLazyCollections(); }
         */
        return newVehicleLoan;
    }

    public List<NewVehicleLoan> findAll(final Collection<Long> vehicleLoanApplicationId) {
        return this.repository.findAllById(vehicleLoanApplicationId);
    }

    public void save(final NewVehicleLoan newVehicleLoan) {
        this.repository.save(newVehicleLoan);
    }

    public void saveAndFlush(final NewVehicleLoan newVehicleLoan) {
        this.repository.saveAndFlush(newVehicleLoan);
    }

    public void delete(final NewVehicleLoan newVehicleLoan) {
        this.repository.delete(newVehicleLoan);
    }

    public void flush() {
        this.repository.flush();
    }

    /*
     * public NewVehicleLoan getActiveClientInUserScope(Long clientId) { final NewVehicleLoan NewVehicleLoan =
     * this.findOneWithNotFoundDetection(clientId); if (NewVehicleLoan.isNotActive()) { throw new
     * ClientNotActiveException(NewVehicleLoan.getId()); }
     * this.context.validateAccessRights(NewVehicleLoan.getOffice().getHierarchy()); return NewVehicleLoan; }
     */

    public NewVehicleLoan getLoanApplicationByuserId(String accountNumber) {
        Long loanApplicationNumber = Long.parseLong(accountNumber);
        NewVehicleLoan newVehicleLoan = this.repository.getLoanApplicationByuserId(accountNumber);
        if (newVehicleLoan == null) {
            throw new LoanTransactionProcessingStrategyNotFoundException(loanApplicationNumber);
        }
        return newVehicleLoan;
    }

}
