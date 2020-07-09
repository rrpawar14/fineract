package org.apache.fineract.infrastructure.creditbureau.data;

public class CreditBureauTokenData {

    private final long tokenId;

    private final String userName;

    private final String subscriptionId;

    private final String subscriptionKey;

    // private final String password;

    private CreditBureauTokenData(final long tokenId, final String userName, final String subscriptionId, final String subscriptionKey)
    // final String password
    {
        this.tokenId = tokenId;
        this.userName = userName;
        this.subscriptionId = subscriptionId;
        this.subscriptionKey = subscriptionKey;
        // this.password = password;
    }

    public static CreditBureauTokenData instance(final long tokenId, final String userName, final String subscriptionId,
            final String subscriptionKey) {

        return new CreditBureauTokenData(tokenId, userName, subscriptionId, subscriptionKey);
    }

    public long getTokenId() {
        return this.tokenId;
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

}
