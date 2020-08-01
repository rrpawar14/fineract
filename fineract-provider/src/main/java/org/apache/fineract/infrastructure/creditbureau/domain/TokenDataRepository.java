package org.apache.fineract.infrastructure.creditbureau.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface TokenDataRepository
        extends JpaRepository<CreditBureauTokenCredential, Long>, JpaSpecificationExecutor<CreditBureauTokenCredential> {

    @Query("select creditbureautokencredential from CreditBureauTokenCredential creditbureautokencredential")
    CreditBureauTokenCredential getTokenCredential();
    // void findUsername();

}
