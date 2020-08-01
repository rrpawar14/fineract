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
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.creditbureau.data.CreditReportData;
import org.apache.fineract.infrastructure.creditbureau.domain.CreditBureauToken;
import org.apache.fineract.infrastructure.creditbureau.domain.CreditBureauTokenCredential;
import org.apache.fineract.infrastructure.creditbureau.domain.TokenDataRepositoryWrapper;
import org.apache.fineract.infrastructure.creditbureau.domain.TokenRepositoryWrapper;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreditBureauReportsReadPlatformServiceImpl implements CreditBureauReportsReadPlatformService {

    private static final Logger LOG = LoggerFactory.getLogger(CreditBureauReportsReadPlatformServiceImpl.class);
    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final TokenRepositoryWrapper tokenRepository;
    private final TokenDataRepositoryWrapper tokenDataRepository;

    @Autowired
    public CreditBureauReportsReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final TokenRepositoryWrapper tokenRepository, final TokenDataRepositoryWrapper tokenDataRepository) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.tokenRepository = tokenRepository;
        this.tokenDataRepository = tokenDataRepository;
    }

    @SuppressWarnings({ "CatchAndPrintStackTrace", "DefaultCharset" })
    @Override
    @Transactional
    @CacheEvict(value = "searchreport", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('cv')")
    public CreditReportData retrieveAllSearchReport(final String searchId) {
        String result = null;
        Long uniqueID;
        try {
            this.context.authenticatedUser();

            final CreditBureauTokenCredential credittokendata = this.tokenDataRepository.getTokenCredential();

            String userName = credittokendata.getUserName();
            String password = credittokendata.getPassword();
            String subscriptionId = credittokendata.getSubscriptionId();
            String subscriptionKey = credittokendata.getSubscriptionKey();

            final CreditBureauToken creditbureautoken = this.tokenRepository.getToken();
            String token = creditbureautoken.getToken();

            StringBuffer response = new StringBuffer();
            System.out.println("retrieved token : " + token);

            final String POST_PARAMS = "BODY=x-www-form-urlencoded&nrc=" + searchId + "&";

            System.out.println(POST_PARAMS);

            URL obj = new URL("https://mmcix.azure-api.net/qa/20200324/api/Search/SimpleSearch?nrc=" + searchId);
            String readLine = null;
            HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();

            postConnection.setRequestMethod("POST");
            postConnection.setRequestProperty("mcix-subscription-key", subscriptionKey);
            postConnection.setRequestProperty("mcix-subscription-id", subscriptionId);
            postConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            postConnection.setRequestProperty("Authorization", "Bearer " + token);

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
                result=response.toString();
                
                // to fetch the parameters from result JsonObject jsonObject =
                // JsonObject jsonObject =
                // JsonParser.parseString(result).getAsJsonObject();
                // final String dataarray = jsonObject.get("Data").toString();

                JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();
                JsonArray jArray = jsonObject.getAsJsonArray("Data");
                System.out.println("json array from data :" + jArray);

                JsonObject jobject = jArray.get(0).getAsJsonObject();
                String uniqueIdString = jobject.get("UniqueID").toString();
  
                String TrimUniqueId = uniqueIdString.substring(1, uniqueIdString.length() - 1);
                System.out.println("leadString :" + TrimUniqueId);
                uniqueID = Long.parseLong(TrimUniqueId);            
                

            } else {
                System.out.println("Request is Invalid");

            }
            //
            System.out.println("-----Unique ID fetched successfully--------" + uniqueID);
            System.out.println(POST_PARAMS);

            obj = new URL("https://mmcix.azure-api.net/qa/20200324/api/Dashboard/GetCreditReport?uniqueId=" + uniqueID);
            readLine = null;
            System.out.println(" get credit report url " + obj);
            postConnection = (HttpURLConnection) obj.openConnection();
            postConnection.setRequestMethod("GET");
            postConnection.setRequestProperty("mcix-subscription-key", subscriptionKey);
            postConnection.setRequestProperty("mcix-subscription-id", subscriptionId);
            postConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            postConnection.setRequestProperty("Authorization", "Bearer " + token);

            postConnection.setDoOutput(true);

            os = postConnection.getOutputStream();
            os.write(POST_PARAMS.getBytes());
            os.flush();
            os.close();

            responseCode = postConnection.getResponseCode();
            System.out.println("POST Response Code :  " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(postConnection.getInputStream()));
                in = new BufferedReader(new InputStreamReader(postConnection.getInputStream()));
                response = new StringBuffer();
                while ((readLine = in.readLine()) != null) {
                    response.append(readLine);
                }
                in.close();
                System.out.println("---Fetched CREDIT REPORT---");
                System.out.println("JSON String Result " + response.toString());
                result = response.toString();
                
               
            } else {
                System.out.println("Request is Invalid");

            }
            //

        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("Error occured.", e);
        }
        return CreditReportData.instance(result);
    }

}
