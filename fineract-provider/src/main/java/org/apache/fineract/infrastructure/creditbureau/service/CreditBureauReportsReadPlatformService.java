package org.apache.fineract.infrastructure.creditbureau.service;

import org.apache.fineract.infrastructure.creditbureau.data.CreditReportData;

public interface CreditBureauReportsReadPlatformService {

    CreditReportData retrieveAllSearchReport(final String searchId);

}
