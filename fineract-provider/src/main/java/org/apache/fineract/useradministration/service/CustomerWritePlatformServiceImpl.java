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
package org.apache.fineract.useradministration.service;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.PersistenceException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.security.service.PlatformPasswordEncoder;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.useradministration.domain.Customers;
import org.apache.fineract.useradministration.domain.CustomersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerWritePlatformServiceImpl implements CustomerWritePlatformService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerWritePlatformServiceImpl.class);
    private final PlatformSecurityContext context;
    private final PlatformPasswordEncoder applicationPasswordEncoder;
    private final CustomersRepository customersRepository;
    private final PasswordEncoder passwordEncoder;
    private final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

    @Autowired
    public CustomerWritePlatformServiceImpl(final PlatformSecurityContext context, final CustomersRepository customersRepository,
            final PlatformPasswordEncoder applicationPasswordEncoder, final PasswordEncoder passwordEncoder) {
        this.context = context;
        this.customersRepository = customersRepository;
        this.applicationPasswordEncoder = applicationPasswordEncoder;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    @Caching(evict = { @CacheEvict(value = "users", allEntries = true), @CacheEvict(value = "usersByUsername", allEntries = true) })
    public CommandProcessingResult createCustomer(final JsonCommand command) {
        try {
            this.context.authenticatedUser();

            // this.fromApiJsonDeserializer.validateForCreate(command.json());

            final String mobileNoParamName = "mobileNo";
            final String mobileNo = command.stringValueOfParameterNamed(mobileNoParamName);
            System.out.println("mobileNo: " + mobileNo);

            checkMobileNoDuplicate(mobileNo);

            final String passwordParamName = "password";
            final String password = command.stringValueOfParameterNamed(passwordParamName);

            Customers customers = Customers.fromJson(command);
            create(customers);

            // this.topicDomainService.subscribeUserToTopic(appUser);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(customers.getId()) //
                    // .withOfficeId(userOffice.getId()) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            throw handleDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
        } catch (final JpaSystemException | PersistenceException | AuthenticationServiceException dve) {
            LOG.error("createUser: JpaSystemException | PersistenceException | AuthenticationServiceException", dve);
            Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
            throw handleDataIntegrityIssues(command, throwable, dve);
        }
    }

    private void checkMobileNoDuplicate(String mobileNo) {
        System.out.println("mobileNo:: " + mobileNo);
        Customers customers = this.customersRepository.findAppUserByMobileNo(mobileNo);

        if (customers != null) {
            System.out.println("appUser: " + customers);
            if (customers.getMobileNo().equals(mobileNo)) {

                // PlatformDataIntegrityException("user.Account.already.exist", "UserAccount by Phone No:" + mobileNo +
                // "Already Exist");
                throw new PlatformApiDataValidationException("user.Account.already.exist",
                        "UserAccount by Phone No:" + mobileNo + " Already Exist", dataValidationErrors);

            }
        }
    }

    public void create(final Customers customers) {

        final String unencodedPassword = customers.getPassword();

        final String encodePassword = this.encode(customers);
        customers.updatePassword(encodePassword);

        this.customersRepository.saveAndFlush(customers);

        /*
         * if (sendPasswordToEmail.booleanValue()) { this.emailService.sendToUserAccount(appUser.getOffice().getName(),
         * appUser.getFirstname(), appUser.getEmail(), appUser.getUsername(), unencodedPassword); }
         */
    }

    public String encode(final Customers customer) {
        return this.passwordEncoder.encode(customer.getPassword());
    }

    @Override
    @Transactional
    @Caching(evict = { @CacheEvict(value = "users", allEntries = true), @CacheEvict(value = "usersByUsername", allEntries = true) })
    public CommandProcessingResult updateCustomerPassword(final JsonCommand command) {
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                // .withEntityId(command.getId()) //
                // .withOfficeId(userOffice.getId()) //
                .build();

    }

    private PlatformDataIntegrityException handleDataIntegrityIssues(final JsonCommand command, final Throwable realCause,
            final Exception dve) {
        if (realCause.getMessage().contains("'username_org'")) {
            final String username = command.stringValueOfParameterNamed("username");
            final StringBuilder defaultMessageBuilder = new StringBuilder("User with username ").append(username)
                    .append(" already exists.");
            return new PlatformDataIntegrityException("error.msg.user.duplicate.username", defaultMessageBuilder.toString(), "username",
                    username);
        }

        if (realCause.getMessage().contains("'unique_self_client'")) {
            return new PlatformDataIntegrityException("error.msg.user.self.service.user.already.exist",
                    "Self Service User Id is already created. Go to Admin->Users to edit or delete the self-service user.");
        }

        LOG.error("handleDataIntegrityIssues: Neither duplicate username nor existing user; unknown error occured", dve);
        return new PlatformDataIntegrityException("error.msg.unknown.data.integrity.issue", "Unknown data integrity issue with resource.");
    }

}
