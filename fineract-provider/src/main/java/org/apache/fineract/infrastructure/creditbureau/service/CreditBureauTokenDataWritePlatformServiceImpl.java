package org.apache.fineract.infrastructure.creditbureau.service;

import javax.persistence.PersistenceException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.creditbureau.domain.CreditBureauTokenCredential;
import org.apache.fineract.infrastructure.creditbureau.domain.CreditBureauTokenRepository;
import org.apache.fineract.infrastructure.creditbureau.serialization.CreditBureauTokenDataCommandFromApiJsonDeserializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreditBureauTokenDataWritePlatformServiceImpl implements CreditBureauTokenDataWritePlatformService {

    private static final Logger LOG = LoggerFactory.getLogger(CreditBureauTokenDataWritePlatformServiceImpl.class);

    private final PlatformSecurityContext context;
    private final CreditBureauTokenRepository creditBureauTokenRepository;
    private final CreditBureauTokenDataCommandFromApiJsonDeserializer fromApiJsonDeserializer;

    @Autowired
    public CreditBureauTokenDataWritePlatformServiceImpl(final PlatformSecurityContext context,
            final CreditBureauTokenRepository creditBureauTokenRepository,
            final CreditBureauTokenDataCommandFromApiJsonDeserializer fromApiJsonDeserializer) {
        this.context = context;
        this.creditBureauTokenRepository = creditBureauTokenRepository;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
    }

    @Transactional
    @Override
    @CacheEvict(value = "tokendata", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('cv')")
    public CommandProcessingResult addCreditBureauTokenData(final JsonCommand command) {

        try {
            this.context.authenticatedUser();

            this.fromApiJsonDeserializer.validateForCreate(command.json());

            final CreditBureauTokenCredential tokendata = CreditBureauTokenCredential.fromJson(command);
            this.creditBureauTokenRepository.save(tokendata);

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(tokendata.getId()).build();

        } catch (final DataIntegrityViolationException dve) {
            handleTokenDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return CommandProcessingResult.empty();
        } catch (final PersistenceException ee) {
            Throwable throwable = ExceptionUtils.getRootCause(ee.getCause());
            handleTokenDataIntegrityIssues(command, throwable, ee);
            return CommandProcessingResult.empty();
        }
    }

    private void handleTokenDataIntegrityIssues(final JsonCommand command, final Throwable realCause, final Exception dve) {
        /*
         * if (realCause.getMessage().contains("userNames")) { final String
         * username = command.stringValueOfParameterNamed("username"); throw new
         * PlatformDataIntegrityException("error.msg.token.duplicate.username",
         * "A token with username '" + username + "' already exists",
         * "username", username); }
         */

        LOG.error("Error occured.", dve);
        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());

    }
}
