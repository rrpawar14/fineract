package org.apache.fineract.infrastructure.creditbureau.data;

public class CreditBureauTokenCredentialData {

    private final String userName;

    private final String subscriptionId;

    private final String subscriptionKey;

    private final String password;

    private CreditBureauTokenCredentialData(final String userName, final String subscriptionId, final String subscriptionKey,
            final String password) {

        this.userName = userName;
        this.subscriptionId = subscriptionId;
        this.subscriptionKey = subscriptionKey;
        this.password = password;
    }

    public static CreditBureauTokenCredentialData instance(final String userName, final String subscriptionId, final String subscriptionKey,
            final String password) {

        return new CreditBureauTokenCredentialData(userName, subscriptionId, subscriptionKey, password);
    }

    public String getUserName() {
        return this.userName;
    }

    public String subscriptionId() {
        return this.subscriptionId;
    }

    public String subscriptionKey() {
        return this.subscriptionKey;
    }

    public String password() {
        return this.password;
    }

}
