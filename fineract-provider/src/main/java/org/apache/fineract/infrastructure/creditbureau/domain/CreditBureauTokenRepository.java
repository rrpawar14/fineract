package org.apache.fineract.infrastructure.creditbureau.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CreditBureauTokenRepository extends JpaRepository<CreditBureauToken, Long>, JpaSpecificationExecutor<CreditBureauToken> {

}
