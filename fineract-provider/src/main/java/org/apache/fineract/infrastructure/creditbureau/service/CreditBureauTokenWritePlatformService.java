package org.apache.fineract.infrastructure.creditbureau.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

//@Configuration
@Component
public interface CreditBureauTokenWritePlatformService {

    @Transactional
    CommandProcessingResult createCreditBureauToken(JsonCommand command);

    // CommandProcessingResult updateCreditBureauToken(JsonCommand command);

}
