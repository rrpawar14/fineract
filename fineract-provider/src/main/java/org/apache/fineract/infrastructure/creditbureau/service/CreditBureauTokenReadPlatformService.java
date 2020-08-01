package org.apache.fineract.infrastructure.creditbureau.service;

import java.util.Collection;
import org.apache.fineract.infrastructure.creditbureau.data.CreditBureauToken;
import org.apache.fineract.infrastructure.creditbureau.data.CreditBureauTokenCredentialData;
import org.apache.fineract.infrastructure.creditbureau.data.CreditReportData;

public interface CreditBureauTokenReadPlatformService {

    Collection<CreditBureauTokenCredentialData> retrieveAllCreditBureauTokenData();

    Collection<CreditBureauToken> retrieveAllCreditBureauToken();

    CreditReportData retrieveAllSearchReport(final String searchId);

    // Collection<CreditReportData> retrieveGetCreditReport(final Long
    // searchId);
}
