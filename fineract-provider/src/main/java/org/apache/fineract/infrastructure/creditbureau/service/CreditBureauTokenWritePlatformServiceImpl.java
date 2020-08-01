/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements. See the NOTICE file distributed with this
   work for additional information regarding copyright ownership. The ASF
   licenses this file to you under the Apache License, Version 2.0 (the
   "License"); you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
   WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
   License for the specific language governing permissions and limitations under
   the License.
  */
package org.apache.fineract.infrastructure.creditbureau.service;

import com.google.gson.JsonElement;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.creditbureau.domain.CreditBureauToken;
import org.apache.fineract.infrastructure.creditbureau.domain.CreditBureauTokenCredential;
import org.apache.fineract.infrastructure.creditbureau.domain.CreditBureauTokenRepository;
import org.apache.fineract.infrastructure.creditbureau.domain.TokenDataRepository;
import org.apache.fineract.infrastructure.creditbureau.domain.TokenDataRepositoryWrapper;
import org.apache.fineract.infrastructure.creditbureau.domain.TokenRepositoryWrapper;
import org.apache.fineract.infrastructure.creditbureau.serialization.CreditBureauTokenCommandFromApiJsonDeserializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreditBureauTokenWritePlatformServiceImpl implements CreditBureauTokenWritePlatformService {

    private static final Logger LOG = LoggerFactory.getLogger(CreditBureauTokenWritePlatformServiceImpl.class);
    private final PlatformSecurityContext context;
    private final FromJsonHelper fromApiJsonHelper;
    private final CreditBureauTokenRepository creditBureauTokenRepository;
    private final CreditBureauTokenCommandFromApiJsonDeserializer fromApiJsonDeserializer;
    private final CreditBureauTokenReadPlatformService readPlatformServiceCreditBureauToken;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final CreditBureauToken creditBureauToken;
    private final TokenDataRepository tokendatRepository;
    private final TokenRepositoryWrapper tokenRepository;
    private final TokenDataRepositoryWrapper tokenDataRepository;
    private static String tokenstr;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CreditBureauTokenWritePlatformServiceImpl(final PlatformSecurityContext context,
            final CreditBureauTokenRepository creditBureauTokenRepository, final TokenRepositoryWrapper tokenRepository,
            final TokenDataRepositoryWrapper tokenDataRepository, final TokenDataRepository tokendatRepository,
            final CreditBureauTokenCommandFromApiJsonDeserializer fromApiJsonDeserializer, final FromJsonHelper fromApiJsonHelper,
            final CreditBureauTokenReadPlatformService readPlatformServiceCreditBureauToken,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService, final CreditBureauToken creditBureauToken,
            final RoutingDataSource dataSource) {
        this.context = context;
        this.creditBureauTokenRepository = creditBureauTokenRepository;
        this.tokenDataRepository = tokenDataRepository;
        this.tokenRepository = tokenRepository;
        this.tokendatRepository = tokendatRepository;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.readPlatformServiceCreditBureauToken = readPlatformServiceCreditBureauToken;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.creditBureauToken = creditBureauToken;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // used to store data which fetched from database

    /*
     * public Collection<CreditBureauTokenCredentialData>
     * retrieveAllCreditBureauToken() { this.context.authenticatedUser();
     *
     * final CreditBureauTokenMapper rm = new CreditBureauTokenMapper(); final
     * String sql = "select " + rm.schema(); // + " order by cbtd.id";
     *
     * return this.jdbcTemplate.query(sql, rm, new Object[] {});
     *
     * }
     *
     * private static final class CreditBureauTokenMapper implements
     * RowMapper<CreditBureauTokenCredentialData> {
     *
     * public String schema() { return
     * "cbtd.userNames as userName,cbtd.subscription_id as subscriptionId,cbtd.subscription_key as subscriptionKey, cbtd.password as password from "
     * + " m_creditbureau_tokendata cbtd"; }
     *
     * @Override public CreditBureauTokenCredentialData mapRow(final ResultSet
     * rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
     * // final long tokenId = rs.getLong("tokenId"); userName =
     * rs.getString("userName"); subscriptionId =
     * rs.getString("subscriptionId"); subscriptionKey =
     * rs.getString("subscriptionKey"); password = rs.getString("password");
     *
     * return CreditBureauTokenCredentialData.instance(userName, subscriptionId,
     * subscriptionKey, password);
     *
     * } }
     */
    @SuppressWarnings({ "CatchAndPrintStackTrace", "DefaultCharset" })
    @Override
    @Transactional
    @CacheEvict(value = "credittoken", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('cv')")
    public CommandProcessingResult createCreditBureauToken(JsonCommand command) {
        try {
            this.context.authenticatedUser();

            // fetch the token credentials for using it in header
            // final Collection<CreditBureauTokenCredentialData>
            // creditBureauTokenData = this.retrieveAllCreditBureauToken();

            final CreditBureauTokenCredential credittokendata = this.tokenDataRepository.getTokenCredential();

            String userName = credittokendata.getUserName();
            String password = credittokendata.getPassword();
            String subscriptionId = credittokendata.getSubscriptionId();
            String subscriptionKey = credittokendata.getSubscriptionKey();

            final String POST_PARAMS = "\n" + "BODY=x-www-form-urlencoded&\r\n" + "grant_type=password&\r\n" + "userName=" + userName
                    + "&\r\n" + "password=" + password + "&\r\n";

            System.out.println(POST_PARAMS);

            URL obj = new URL("https://mmcix.azure-api.net/qa/20200324/Token");
            String readLine = null;

            HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
            postConnection.setRequestMethod("POST");
            postConnection.setRequestProperty("mcix-subscription-key", subscriptionKey);
            postConnection.setRequestProperty("mcix-subscription-id", subscriptionId);
            postConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            postConnection.setDoOutput(true);

            OutputStream os = postConnection.getOutputStream();
            os.write(POST_PARAMS.getBytes());
            os.flush();
            os.close();

            int responseCode = postConnection.getResponseCode();
            System.out.println("POST Response Code :  " + responseCode);
            System.out.println("POST Response Message : " + postConnection.getResponseMessage());
            String ResponseMessage = postConnection.getResponseMessage();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(postConnection.getInputStream()));
                StringBuffer response = new StringBuffer();
                while ((readLine = in.readLine()) != null) {
                    response.append(readLine);
                }
                in.close();
                System.out.println("JSON String Result " + response.toString());

                String result = response.toString();

                /*
                 * //to fetch the parameters from result JsonObject jsonObject =
                 * JsonParser.parseString(result).getAsJsonObject(); final
                 * String access_token =
                 * jsonObject.get("access_token").toString(); String tokentrial
                 * = "{'tokens':'" + access_token + "'}";
                 */

                final CommandWrapper wrapper = new CommandWrapperBuilder().withJson(result).build();
                final String json = wrapper.getJson(); // CommandProcessingResult
                result = null;
                JsonCommand apicommand = null;
                boolean isApprovedByChecker = false;
                final JsonElement parsedCommand = this.fromApiJsonHelper.parse(json);

                apicommand = JsonCommand.from(json, parsedCommand, this.fromApiJsonHelper, wrapper.getEntityName(), wrapper.getEntityId(),
                        wrapper.getSubentityId(), wrapper.getGroupId(), wrapper.getClientId(), wrapper.getLoanId(), wrapper.getSavingsId(),
                        wrapper.getTransactionId(), wrapper.getHref(), wrapper.getProductId(), wrapper.getCreditBureauId(),
                        wrapper.getOrganisationCreditBureauId());

                this.fromApiJsonDeserializer.validateForCreate(apicommand.json());

                final CreditBureauToken token = CreditBureauToken.fromJson(apicommand);

                final CreditBureauToken credittoken = this.tokenRepository.getToken();
                if (credittoken != null) {
                    String fetoken = credittoken.getToken();
                    System.out.println("previous token : " + fetoken);

                    this.tokenRepository.delete(credittoken);
                }

                this.tokenRepository.save(token);

            } else {
                System.out.println("Request is Invalid");

            }

        } catch (IOException e) {
            e.printStackTrace();
            // logger.log("something has gone terribly wrong", e);
            LOG.error("Error occured.", e);
        }

        return new CommandProcessingResultBuilder().withCommandId(command.commandId()).build();

    }
}
