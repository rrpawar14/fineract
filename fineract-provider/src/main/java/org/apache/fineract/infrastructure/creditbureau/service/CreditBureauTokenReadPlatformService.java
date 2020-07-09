package org.apache.fineract.infrastructure.creditbureau.service;

import java.util.Collection;
import org.apache.fineract.infrastructure.creditbureau.data.CreditBureauTokenData;

public interface CreditBureauTokenReadPlatformService {

    Collection<CreditBureauTokenData> retrieveAllCreditBureauToken();
}
