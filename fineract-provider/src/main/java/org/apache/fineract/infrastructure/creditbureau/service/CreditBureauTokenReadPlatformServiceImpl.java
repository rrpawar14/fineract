package org.apache.fineract.infrastructure.creditbureau.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.creditbureau.data.CreditBureauTokenData;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class CreditBureauTokenReadPlatformServiceImpl implements CreditBureauTokenReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    @Autowired
    public CreditBureauTokenReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class CreditBureauTokenMapper implements RowMapper<CreditBureauTokenData> {

        public String schema() {
            return " cbt.token_id as tokenId,cbt.username as userName,cbt.subscription_id as subscriptionId,cbt.subscription_key as subscriptionKey from "
                    + " m_creditbureau_token cbt";
        }

        @Override
        public CreditBureauTokenData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final long tokenId = rs.getLong("tokenId");
            final String userName = rs.getString("userName");
            final String subscriptionId = rs.getString("subscriptionId");
            final String subscriptionKey = rs.getString("subscriptionKey");
            return CreditBureauTokenData.instance(tokenId, userName, subscriptionId, subscriptionKey);

        }
    }

    @Override
    public Collection<CreditBureauTokenData> retrieveAllCreditBureauToken() {
        this.context.authenticatedUser();

        final CreditBureauTokenMapper rm = new CreditBureauTokenMapper();
        final String sql = "select " + rm.schema() + " order by cbt.token_id";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});

    }
}
