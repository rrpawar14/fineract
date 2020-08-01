package org.apache.fineract.infrastructure.creditbureau.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.creditbureau.data.CreditBureauToken;
import org.apache.fineract.infrastructure.creditbureau.data.CreditBureauTokenCredentialData;
import org.apache.fineract.infrastructure.creditbureau.data.CreditReportData;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreditBureauTokenReadPlatformServiceImpl implements CreditBureauTokenReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    public static String userName;
    public static String subscriptionId;
    public static String subscriptionKey;
    public static String password;
    public static String Token;
    private static final Logger LOG = LoggerFactory.getLogger(CreditBureauTokenReadPlatformServiceImpl.class);

    @Autowired
    public CreditBureauTokenReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // fetch token-data (Subscrption ID, Subscription Key, Username, Password)
    private static final class CreditBureauTokenDataMapper implements RowMapper<CreditBureauTokenCredentialData> {

        public String tokendataschema() {
            return "cbtd.userNames as userName,cbtd.subscription_id as subscriptionId,cbtd.subscription_key as subscriptionKey, cbtd.password as password from "
                    + " m_creditbureau_tokendata cbtd";
        }

        @Override
        public CreditBureauTokenCredentialData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
                throws SQLException {
            // final long tokenId = rs.getLong("tokenId");
            userName = rs.getString("userName");
            subscriptionId = rs.getString("subscriptionId");
            subscriptionKey = rs.getString("subscriptionKey");
            password = rs.getString("password");

            System.out.println("UserName : " + userName);
            System.out.println("subscriptionId : " + subscriptionId);
            System.out.println("subscriptionKey : " + subscriptionKey);
            System.out.println("subscriptionKey : " + password);

            return CreditBureauTokenCredentialData.instance(userName, subscriptionId, subscriptionKey, password);

        }
    }

    @Override
    public Collection<CreditBureauTokenCredentialData> retrieveAllCreditBureauTokenData() {
        this.context.authenticatedUser();

        final CreditBureauTokenDataMapper rm = new CreditBureauTokenDataMapper();
        final String sql = "select " + rm.tokendataschema(); // + " order by
                                                             // cbtd.id";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});

    }

    // Fetch Token
    private static final class CreditBureauTokenMapper implements RowMapper<CreditBureauToken> {

        public String tokenschema() {
            return "cbt.token as token from " + " m_creditbureau_token cbt";
        }

        @Override
        public CreditBureauToken mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            Token = rs.getString("token");
            System.out.println("token : " + Token);

            return CreditBureauToken.instance(Token);
        }
    }

    @Override
    public Collection<CreditBureauToken> retrieveAllCreditBureauToken() {
        this.context.authenticatedUser();

        final CreditBureauTokenMapper rm = new CreditBureauTokenMapper();
        final String sql = "select " + rm.tokenschema(); // + " order by
                                                         // cbtd.id";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});

    }

    // SimpleSearch Function

    @SuppressWarnings({ "CatchAndPrintStackTrace", "DefaultCharset" })
    @Override
    @Transactional
    @CacheEvict(value = "searchreport", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('cv')")
    public CreditReportData retrieveAllSearchReport(final String searchId) {
        String result = null;
        try {
            this.context.authenticatedUser();

            final Collection<CreditBureauTokenCredentialData> creditBureauTokenData = this.retrieveAllCreditBureauTokenData();
            final Collection<CreditBureauToken> creditBureauToken = this.retrieveAllCreditBureauToken();
            StringBuffer response = new StringBuffer();

            System.out.println("retrieved token data : " + creditBureauTokenData);

            final String POST_PARAMS = "\n" + "BODY=x-www-form-urlencoded&\r\n" + "grant_type=password&\r\n" + "userName=" + userName
                    + "&\r\n" + "password=" + password + "&\r\n";

            System.out.println(POST_PARAMS);

            URL obj = new URL("https://mmcix.azure-api.net/qa/20200324/api/Search/SimpleSearch?nrc=" + searchId);
            String readLine = null;

            HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
            postConnection.setRequestMethod("POST");
            postConnection.setRequestProperty("mcix-subscription-key", subscriptionKey);
            postConnection.setRequestProperty("mcix-subscription-id", subscriptionId);
            postConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            postConnection.setRequestProperty("Authorization", "Bearer " + Token);

            postConnection.setDoOutput(true);

            OutputStream os = postConnection.getOutputStream();
            os.write(POST_PARAMS.getBytes());
            os.flush();
            os.close();

            int responseCode = postConnection.getResponseCode();
            System.out.println("POST Response Code :  " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(postConnection.getInputStream()));
                // StringBuffer response = new StringBuffer();
                while ((readLine = in.readLine()) != null) {
                    response.append(readLine);
                }
                in.close();
                System.out.println("JSON String Result " + response.toString());

                // fetch Unique Id from Http-Response
                result = response.toString();
                // String readLineResult = readLine.toString();

                JsonObject jsonObjectAltResult = JsonParser.parseString(result).getAsJsonObject();
                // JsonObject jsonObjectAltReadLineResult =
                // JsonParser.parseString(readLineResult).getAsJsonObject();

                JsonArray getUniqueId = (JsonArray) jsonObjectAltResult.get("UniqueID");
                // JsonArray getUniqueId = (JsonArray)
                // jsonObjectAltReadLineResult.get("UniqueID");

                String uniqueID = getUniqueId.toString();

                // Fetch the Unique Id from Simple Search and Passing it to the
                // GetQueryMethod to fetch the credit reports

                System.out.println(POST_PARAMS);

                obj = new URL("https://mmcix.azure-api.net/qa/20200324/api/Dashboard/GetCreditReport?uniqueId=" + uniqueID);
                readLine = null;

                postConnection = (HttpURLConnection) obj.openConnection();
                postConnection.setRequestMethod("GET");
                postConnection.setRequestProperty("mcix-subscription-key", subscriptionKey);
                postConnection.setRequestProperty("mcix-subscription-id", subscriptionId);
                postConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                postConnection.setRequestProperty("Authorization", "Bearer " + Token);

                postConnection.setDoOutput(true);

                os = postConnection.getOutputStream();
                os.write(POST_PARAMS.getBytes());
                os.flush();
                os.close();

                responseCode = postConnection.getResponseCode();
                System.out.println("POST Response Code :  " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    in = new BufferedReader(new InputStreamReader(postConnection.getInputStream()));
                    response = new StringBuffer();
                    while ((readLine = in.readLine()) != null) {
                        response.append(readLine);
                    }
                    in.close();
                    System.out.println("---Fetched CREDIT REPORT---");
                    System.out.println("JSON String Result " + response.toString());
                    jsonObjectAltResult = JsonParser.parseString(result).getAsJsonObject();

                    getUniqueId = (JsonArray) jsonObjectAltResult.get("UniqueID");
                    uniqueID = getUniqueId.toString();

                    // fetching parameters from response data
                    result = response.toString();

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // logger.log("something has gone terribly wrong", e);
            LOG.error("Error occured.", e);
        }
        return CreditReportData.instance(result);
    }

    /*
     * private static final class ReportMapper implements
     * RowMapper<CreditReportData> {
     *
     *
     * // public String schema() { return //
     * " c.id as id, c.code_name as code_name, c.is_system_defined as systemDefined from m_code c "
     * // ; }
     *
     *
     * @Override public CreditReportData mapRow(final ResultSet
     * rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
     *
     * final String id = rs.getLong("id"); final String name =
     * rs.getString("code_name");
     *
     * return CreditReportData.instance(id, code_name); } }
     *
     * @Override public Collection<CreditReportData>
     * retrieveGetCreditReport(final Long searchId) { try {
     * this.context.authenticatedUser();
     *
     * final ReportMapper rm = new ReportMapper(); // final String sql =
     * "select " + rm.schema() + " where c.id = ?";
     *
     * return this.jdbcTemplate.queryForObject(rm, new Object[] {}); } catch
     * (final EmptyResultDataAccessException e) { throw new
     * CreditReportNotFoundException(searchId); } }
     */

    // Fetch Credit Report Separated (Extra)
    /*
     * @Override public Collection<CreditBureauTokenData>
     * retrieveCreditReport(final String uniqueID) {
     * this.context.authenticatedUser();
     *
     * final Collection<CreditBureauTokenData> creditBureauTokenData =
     * this.retrieveAllCreditBureauToken();
     *
     * System.out.println("retrieved token data : " + creditBureauTokenData);
     * System.out.println("Unique ID: " + uniqueID);
     *
     * final String POST_PARAMS = "\n" + "BODY=x-www-form-urlencoded&\r\n" +
     * "grant_type=password&\r\n" + "userName=" + userName + "&\r\n" +
     * "password=" + password + "&\r\n";
     *
     * System.out.println(POST_PARAMS);
     *
     * URL obj = new URL(
     * "https://mmcix.azure-api.net/qa/20200324/api/Dashboard/GetCreditReport?uniqueId="
     * +searchId); String readLine = null;
     *
     * HttpURLConnection postConnection = (HttpURLConnection)
     * obj.openConnection(); postConnection.setRequestMethod("POST");
     * postConnection.setRequestProperty("mcix-subscription-key",
     * subscriptionKey);
     * postConnection.setRequestProperty("mcix-subscription-id",
     * subscriptionId); postConnection.setRequestProperty("Content-Type",
     * "application/x-www-form-urlencoded");
     *
     * postConnection.setDoOutput(true);
     *
     * OutputStream os = postConnection.getOutputStream();
     * os.write(POST_PARAMS.getBytes()); os.flush(); os.close();
     *
     * int responseCode = postConnection.getResponseCode();
     * System.out.println("POST Response Code :  " + responseCode);
     *
     * if (responseCode == HttpURLConnection.HTTP_OK) { BufferedReader in = new
     * BufferedReader(new InputStreamReader(postConnection.getInputStream()));
     * StringBuffer response = new StringBuffer(); while ((readLine =
     * in.readLine()) != null) { response.append(readLine); } in.close();
     * System.out.println("JSON String Result " + response.toString());
     *
     * //fetch Unique Id from Http-Response JsonObject jsonObjectAltResult =
     * JsonParser.parseString(response).getAsJsonObject(); JsonObject
     * jsonObjectAltReadLineResult =
     * JsonParser.parseString(readLineResult).getAsJsonObject(); uniqueID =
     * (JsonArray) jsonObjectAltResult.get("UniqueID");
     *
     * return CreditBureauTokenData.instance(response); }
     *
     * }
     */

}
