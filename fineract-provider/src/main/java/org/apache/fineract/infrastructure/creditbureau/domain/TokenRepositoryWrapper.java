package org.apache.fineract.infrastructure.creditbureau.domain;

import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenRepositoryWrapper {

    private final TokenRepository repository;
    private final PlatformSecurityContext context;

    @Autowired
    public TokenRepositoryWrapper(final TokenRepository repository, final PlatformSecurityContext context) {
        this.repository = repository;
        this.context = context;
    }

    public void save(final CreditBureauToken token) {
        this.repository.save(token);
    }

    public void delete(final CreditBureauToken token) {
        this.repository.delete(token);
    }

    public CreditBureauToken getToken() {
        return this.repository.getToken();
    }

}
