package org.apache.fineract.infrastructure.creditbureau.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface TokenRepository extends JpaRepository<CreditBureauToken, Long>, JpaSpecificationExecutor<CreditBureauToken> {

    // public static final String FIND_CLIENT_BY_ACCOUNT_NUMBER = "select token
    // from CreditBureauToken token where client.tokenid = :token_Id"; // check
    // again

    // @Query(FIND_CLIENT_BY_ACCOUNT_NUMBER)
    // CreditBureauToken getClientByAccountNumber(@Param("accountNumber") String
    // accountNumber);// check
    // again

    @Query("select creditbureautoken from CreditBureauToken creditbureautoken")
    CreditBureauToken getToken();

}
