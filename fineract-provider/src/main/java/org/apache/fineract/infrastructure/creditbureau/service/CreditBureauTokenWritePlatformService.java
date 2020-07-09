package org.apache.fineract.infrastructure.creditbureau.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface CreditBureauTokenWritePlatformService {

    CommandProcessingResult addCreditBureauToken(JsonCommand command);

    // CommandProcessingResult updateCreditBureauToken(JsonCommand command);

}
