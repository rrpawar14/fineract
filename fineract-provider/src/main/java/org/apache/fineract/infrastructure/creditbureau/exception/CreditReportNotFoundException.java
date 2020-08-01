package org.apache.fineract.infrastructure.creditbureau.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class CreditReportNotFoundException extends AbstractPlatformResourceNotFoundException {

    public CreditReportNotFoundException(final Long searchId) {
        super("error.msg.creditreport.identifier.not.found", "CreditReport with identifier `" + searchId + "` does not exist", searchId);
    }

}
