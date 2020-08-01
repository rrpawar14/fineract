package org.apache.fineract.infrastructure.creditbureau.domain;

import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenDataRepositoryWrapper {

    private final TokenDataRepository repository;
    private final PlatformSecurityContext context;

    @Autowired
    public TokenDataRepositoryWrapper(final TokenDataRepository repository, final PlatformSecurityContext context) {
        this.repository = repository;
        this.context = context;
    }

    public CreditBureauTokenCredential getTokenCredential() {
        return this.repository.getTokenCredential();
    }

}
