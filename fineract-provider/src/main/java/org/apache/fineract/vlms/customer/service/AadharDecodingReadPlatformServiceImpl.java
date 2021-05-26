/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.vlms.customer.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.fineract.vlms.customer.data.AadharData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class AadharDecodingReadPlatformServiceImpl {

    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_PARSE_ERROR = 1;
    private static final int NUMBER_OF_PARAMS_IN_SECURE_QR_CODE = 15;

    public static final String QR_CODE_TYPE_SECURE = "secure";
    public static final String QR_CODE_TYPE_XML = "xml";
    public static final String QR_CODE_TYPE_UID_NUMBER = "uid_number";
    public static final String QR_CODE_TYPE_UNKNOWN = "unknown";

    public final int statusCode;
    public final String rawString;
    public final String uid;
    public final String name;
    public final String gender; // M/F
    public final String yob; // year of birth
    public final String co; // "D/O: Father Name" (DMR): not sure what this means
    public final String house;
    public final String street;
    public final String lm; // address
    public final String loc; // neighborhood
    public final String vtc; // village
    public final String po; // city
    public final String dist; // district
    public final String subdist; // region
    public final String state;
    public final String pc; // postal code
    public final String dob; // date of birth
    public final String dobGuess; // date of birth or June 1 of year of birth
    public final String statusText; // either `` (success) or `` (any failure)
    public final String type; // either `` (success) or `` (any failure)

    private static final Logger log = LoggerFactory.getLogger(AadharDecodingReadPlatformServiceImpl.class);

    public static void main(String[] args) {
        System.out.println("Hello World");

        // String code =
        // "58234139195628742479424941950248076196028903185840750227540803108659170122707428379906507902679020599279752876185987229922588030109035021303252136836758436367688906393775146610950921919627532519022261551092935214861407805657879448770513454952201489625716148339952845168825445379337522645687839344133187905807508865358382281562726972417727675783831401433900333475720665261814747560310473821125471436359932433900201251014578893587349184196308254965033318510304482584880129241457300358970575154906245084106379862983975648448663658398792365813547672672163957950592565468428447037551934146963383420929760282755368210814068286008203155844553908241092432767801062259439054914217908965637142429156845702512848890271000487031203958928047240011706130582604481589500736425622671193044043283943685368081492790813293078202271434470940824959190972222927482328078471119089156535173938579318237759380989639782315642874376836248123174485848588877523163859737394726926932485954751428283455504758759844582496735997691403549871882516574076960250163625503375542382807355738222004639770496200169398307474304261998889344098485961208179206645368089458134320054661923655625504528407774342538436179155551842138532303523804905849360044051786857560751764840076136771964274751867429619587051968513153593260368978048250131877528120246472501672708644506213918848088390579159214660733819780233286575970675969036834810174273981512862470566194418912522296525958278276340294063739718377651342287175852354143732971481972529456023157491371988673317716757810781621731448995799090756252153414171311223560314269129477753071984586906882153958957918119545064191135545890216246972851341524985259501427985201343348313198857573656874284337417879515392124433982392630700410972297686008123026181269750517614553274309358326742467939017367013177131546928329016328969315275838872941149267646330630209385436118138492039504502627486299211247269265944642770984160836267639682682474399589422199662909294427608011107377912578931302334931728244637783584080349728784708129397538751939232032551642814760338840132942638632197620944449378480497924347306100089632034319000071886490211939024862454585256350379880619338930182745912305214583578449822138123501203060953049912294604814484935011784949573106417696900700538985010291027045930037161174921182149126247057264177394152920714041522528685103181234352816990621802184726927469447273548987973581791040920290277317280139472316758326772953664608471822008563596411886975577221637844361086307442476056882474563208509492335242702045774190604063902677846043213031149888795157546257935929415242248423449045553887551436080381172969630802828715223941074388920754986120009676121628895884355829893468600411352982372539479804032246784057266327589480964405571520050243741183620644825951672671299131062474699505437556062116399850295707657752209323651040810158836335218517740911347892298976780996664136703320672415668422577192012448093852497409712683658098746658401784902185254717422931188498883014213456668965713787070310272302587428022865487832015234325424969242558630100510209776382330034250244518985729744394461857234460571622032759193686804237076720593671012295856182781808507872203650611242525079908927978226587423592161706763791548010564413259703690526720";
        // HelloWorld decoding = new HelloWorld(code);

    }

    private String getAttributeOrEmptyString(NamedNodeMap attributes, String attributeName) {
        Node node = attributes.getNamedItem(attributeName);
        if (node != null) {
            return node.getTextContent();
        } else {
            return "";
        }
    }

    public AadharDecodingReadPlatformServiceImpl(String input) {
        rawString = input;

        // copied from http://www.java-samples.com/showtutorial.php?tutorialid=152
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document dom;
        try {

            // Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // Replace </?xml... with <?xml...
            if (input.startsWith("</?")) {
                input = input.replaceFirst("</\\?", "<?");
            }
            // Replace <?xml...?"> with <?xml..."?>
            input = input.replaceFirst("^<\\?xml ([^>]+)\\?\">", "<?xml $1\"?>");
            // parse using builder to get DOM representation of the XML file
            dom = db.parse(new ByteArrayInputStream(input.getBytes("UTF-8")));

        } catch (ParserConfigurationException | SAXException | IOException e) {
            dom = null;
        }

        if (dom != null) {
            type = QR_CODE_TYPE_XML;
            Node node = dom.getChildNodes().item(0);
            NamedNodeMap attributes = node.getAttributes();
            statusCode = STATUS_SUCCESS;
            uid = getAttributeOrEmptyString(attributes, "uid");
            name = getAttributeOrEmptyString(attributes, "name");
            String rawGender = getAttributeOrEmptyString(attributes, "gender");
            try {
                rawGender = formatGender(rawGender);
            } catch (ParseException e) {
                System.err.println("Expected gender to be one of m, f, male, female; got " + rawGender);
            }
            gender = rawGender;
            yob = getAttributeOrEmptyString(attributes, "yob");
            co = getAttributeOrEmptyString(attributes, "co");
            house = getAttributeOrEmptyString(attributes, "house");
            street = getAttributeOrEmptyString(attributes, "street");
            lm = getAttributeOrEmptyString(attributes, "lm");
            loc = getAttributeOrEmptyString(attributes, "loc");
            vtc = getAttributeOrEmptyString(attributes, "vtc");
            po = getAttributeOrEmptyString(attributes, "po");
            dist = getAttributeOrEmptyString(attributes, "dist");
            subdist = getAttributeOrEmptyString(attributes, "subdist");
            state = getAttributeOrEmptyString(attributes, "state");
            pc = getAttributeOrEmptyString(attributes, "pc");
            dob = formatDate(getAttributeOrEmptyString(attributes, "dob"), new String[] { "dd/MM/yyyy", "yyyy-MM-dd" });

        } else if (rawString.matches("\\d{12}")) {
            type = QR_CODE_TYPE_UID_NUMBER;
            statusCode = STATUS_SUCCESS;
            uid = rawString;
            name = "";
            gender = "";
            yob = "";
            co = "";
            house = "";
            street = "";
            lm = "";
            loc = "";
            vtc = "";
            po = "";
            dist = "";
            subdist = "";
            state = "";
            pc = "";
            dob = "";
        } else if (rawString.matches("[0-9]*")) {
            type = QR_CODE_TYPE_SECURE;
            byte[] msgInBytes = null;
            try {
                msgInBytes = decompressByteArray(new BigInteger(rawString).toByteArray());
            } catch (IOException e) {
                log.error("error occured in HTTP request-response method.", e);
            }
            if (msgInBytes != null) {
                int[] delimiters = locateDelimiters(msgInBytes);
                String referenceId = getValueInRange(msgInBytes, delimiters[0] + 1, delimiters[1]);
                uid = referenceId.substring(0, 4);
                name = getValueInRange(msgInBytes, delimiters[1] + 1, delimiters[2]);
                dob = formatDate(getValueInRange(msgInBytes, delimiters[2] + 1, delimiters[3]),
                        new String[] { "dd-MM-yyyy", "dd/MM/yyyy" });
                yob = dob.substring(0, 4);
                gender = getValueInRange(msgInBytes, delimiters[3] + 1, delimiters[4]);
                co = getValueInRange(msgInBytes, delimiters[4] + 1, delimiters[5]);
                dist = getValueInRange(msgInBytes, delimiters[5] + 1, delimiters[6]);
                lm = getValueInRange(msgInBytes, delimiters[6] + 1, delimiters[7]);
                house = getValueInRange(msgInBytes, delimiters[7] + 1, delimiters[8]);
                loc = getValueInRange(msgInBytes, delimiters[8] + 1, delimiters[9]);
                pc = getValueInRange(msgInBytes, delimiters[9] + 1, delimiters[10]);
                po = getValueInRange(msgInBytes, delimiters[10] + 1, delimiters[11]);
                state = getValueInRange(msgInBytes, delimiters[11] + 1, delimiters[12]);
                street = getValueInRange(msgInBytes, delimiters[12] + 1, delimiters[13]);
                subdist = getValueInRange(msgInBytes, delimiters[13] + 1, delimiters[14]);
                vtc = getValueInRange(msgInBytes, delimiters[14] + 1, delimiters[15]);
                statusCode = STATUS_SUCCESS;
            } else {
                statusCode = STATUS_PARSE_ERROR;
                uid = "";
                name = "";
                gender = "";
                yob = "";
                co = "";
                house = "";
                street = "";
                lm = "";
                loc = "";
                vtc = "";
                po = "";
                dist = "";
                subdist = "";
                state = "";
                pc = "";
                dob = "";
            }
        } else {
            type = QR_CODE_TYPE_UNKNOWN;
            statusCode = STATUS_PARSE_ERROR;
            uid = "";
            name = "";
            gender = "";
            yob = "";
            co = "";
            house = "";
            street = "";
            lm = "";
            loc = "";
            vtc = "";
            po = "";
            dist = "";
            subdist = "";
            state = "";
            pc = "";
            dob = "";
        }

        dobGuess = getDobGuess(dob, yob);
        statusText = getStatusText(statusCode);
    }

    private static int[] locateDelimiters(byte[] msgInBytes) {
        int[] delimiters = new int[NUMBER_OF_PARAMS_IN_SECURE_QR_CODE + 1];
        int index = 0;
        int delimiterIndex;
        for (int i = 0; i <= NUMBER_OF_PARAMS_IN_SECURE_QR_CODE; i++) {
            delimiterIndex = getNextDelimiterIndex(msgInBytes, index);
            delimiters[i] = delimiterIndex;
            index = delimiterIndex + 1;
        }
        return delimiters;
    }

    private static String getValueInRange(byte[] msgInBytes, int start, int end) {
        return new String(Arrays.copyOfRange(msgInBytes, start, end), Charset.forName("ISO-8859-1"));
    }

    private static int getNextDelimiterIndex(byte[] msgInBytes, int index) {
        int i = index;
        for (; i < msgInBytes.length; i++) {
            if (msgInBytes[i] == -1) {
                break;
            }
        }
        return i;
    }

    private static byte[] decompressByteArray(byte[] bytes) throws IOException {
        java.io.ByteArrayInputStream bytein = new ByteArrayInputStream(bytes);
        java.util.zip.GZIPInputStream gzin = new GZIPInputStream(bytein);
        java.io.ByteArrayOutputStream byteout = new ByteArrayOutputStream();

        int res = 0;
        byte[] buf = new byte[1024];
        while (res >= 0) {
            res = gzin.read(buf, 0, buf.length);
            if (res > 0) {
                byteout.write(buf, 0, res);
            }
        }
        return byteout.toByteArray();
    }

    private String formatDate(String rawDateString, String[] possibleFormats) {
        if (rawDateString.equals("")) {
            return "";
        }
        SimpleDateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        ParseException parseException = null;
        for (String fromFormatPattern : possibleFormats) {
            try {
                SimpleDateFormat fromFormat = new SimpleDateFormat(fromFormatPattern);
                date = fromFormat.parse(rawDateString);
                break;
            } catch (ParseException e) {
                parseException = e;
            }
        }
        if (date != null) {
            return toFormat.format(date);
        } else if (parseException != null) {
            System.err.println("Expected dob to be in dd/mm/yyyy or yyyy-mm-dd format, got " + rawDateString);
            return rawDateString;
        } else {
            throw new AssertionError("This code is unreachable");
        }
    }

    protected String formatGender(String gender) throws ParseException {
        String lowercaseGender = gender.toLowerCase();
        if (lowercaseGender.equals("male") || lowercaseGender.equals("m")) {
            return "M";
        } else if (lowercaseGender.equals("female") || lowercaseGender.equals("f")) {
            return "F";
        } else if (lowercaseGender.equals("other") || lowercaseGender.equals("o")) {
            return "O";
        } else {
            throw new ParseException("404 gender not found", 0);
        }
    }

    private String getStatusText(int statusCode) {
        switch (statusCode) {
            case AadharDecodingReadPlatformServiceImpl.STATUS_SUCCESS:
                return "";
            default:
                return "";
        }
    }

    private String getDobGuess(String dob, String yob) {
        if (dob.equals("")) {
            Integer yearInt;
            try {
                yearInt = Integer.parseInt(yob);
            } catch (NumberFormatException e) {
                return "";
            }
            // June 1 of the year
            return Integer.toString(yearInt) + "-06-01";
        } else {
            return dob;
        }
    }

    /*
     * private static final class BranchAnalyticsMapper implements RowMapper<AadharData> {
     *
     * public String schema() { return ""; }
     *
     * @Override public AadharData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws
     * SQLException {
     *
     * final Long data = rs.getLong("data");
     *
     * return AadharData.instance(data); } }
     */

    @Cacheable(value = "AadharData", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('CD')")
    public static AadharData fetchAdharDetails(final String commandParam) {
        // try {
        // this.context.authenticatedUser();
        AadharData aadharData = null;
        byte[] msgInBytes = null;
        try {
            System.out.println("coding in byteArray: ");

            System.out.println("biginteger: " + new BigInteger(commandParam));

            // System.out.println("new BigInteger(code).toByteArray().toString():: " + new
            // BigInteger(code).toByteArray().toString());

            msgInBytes = decompressByteArray(new BigInteger(commandParam).toByteArray());

            // System.out.println("coding in byteArray:: " + msgInBytes);

        } catch (IOException e) {
            log.error("error occured in HTTP request-response method.", e);
        }

        if (msgInBytes != null) {
            // System.out.println("msgInBytes: " + msgInBytes);
            int[] delimiters = locateDelimiters(msgInBytes);
            // System.out.println("delimiters: " + delimiters);

            String referenceId = getValueInRange(msgInBytes, delimiters[0] + 1, delimiters[1]);
            String uid = referenceId.substring(0, 4);
            /*
             * name = getValueInRange(msgInBytes, delimiters[1] + 1, delimiters[2]); dob =
             * formatDate(getValueInRange(msgInBytes, delimiters[2] + 1, delimiters[3]), new String[] {"dd-MM-yyyy",
             * "dd/MM/yyyy"}); yob = dob.substring(0, 4); gender = getValueInRange(msgInBytes, delimiters[3] + 1,
             * delimiters[4]); co = getValueInRange(msgInBytes, delimiters[4] + 1, delimiters[5]); dist =
             * getValueInRange(msgInBytes, delimiters[5] + 1, delimiters[6]); lm = getValueInRange(msgInBytes,
             * delimiters[6] + 1, delimiters[7]); house = getValueInRange(msgInBytes, delimiters[7] + 1, delimiters[8]);
             * loc = getValueInRange(msgInBytes, delimiters[8] + 1, delimiters[9]); pc = getValueInRange(msgInBytes,
             * delimiters[9] + 1, delimiters[10]); po = getValueInRange(msgInBytes, delimiters[10] + 1, delimiters[11]);
             * state = getValueInRange(msgInBytes, delimiters[11] + 1, delimiters[12]); street =
             * getValueInRange(msgInBytes, delimiters[12] + 1, delimiters[13]); subdist = getValueInRange(msgInBytes,
             * delimiters[13] + 1, delimiters[14]); vtc = getValueInRange(msgInBytes, delimiters[14] + 1,
             * delimiters[15]); statusCode = STATUS_SUCCESS;
             */
            String name = getValueInRange(msgInBytes, delimiters[1] + 1, delimiters[2]);
            // String dob = formatDate(getValueInRange(msgInBytes, delimiters[2] + 1, delimiters[3]),
            // new String[] {"dd-MM-yyyy", "dd/MM/yyyy"});
            // String yob = dob.substring(0, 4);
            String gender = getValueInRange(msgInBytes, delimiters[3] + 1, delimiters[4]);
            String co = getValueInRange(msgInBytes, delimiters[4] + 1, delimiters[5]);
            String dist = getValueInRange(msgInBytes, delimiters[5] + 1, delimiters[6]);
            String lm = getValueInRange(msgInBytes, delimiters[6] + 1, delimiters[7]);
            String house = getValueInRange(msgInBytes, delimiters[7] + 1, delimiters[8]);
            String loc = getValueInRange(msgInBytes, delimiters[8] + 1, delimiters[9]);
            String pc = getValueInRange(msgInBytes, delimiters[9] + 1, delimiters[10]);
            String po = getValueInRange(msgInBytes, delimiters[10] + 1, delimiters[11]);
            String state = getValueInRange(msgInBytes, delimiters[11] + 1, delimiters[12]);
            String street = getValueInRange(msgInBytes, delimiters[12] + 1, delimiters[13]);
            String subdist = getValueInRange(msgInBytes, delimiters[13] + 1, delimiters[14]);
            String vtc = getValueInRange(msgInBytes, delimiters[14] + 1, delimiters[15]);
            System.out.println("referenceId: " + uid);
            System.out.println("name: " + name);
            // System.out.println("dob: "+ dob);
            System.out.println("gender: " + gender);
            System.out.println("co: " + co);
            System.out.println("lm: " + lm);
            System.out.println("house: " + house);
            System.out.println("loc: " + loc);
            System.out.println("pc: " + pc);
            System.out.println("po: " + po);
            System.out.println("state: " + state);
            System.out.println("subdist: " + subdist);
            System.out.println("vtc: " + vtc);

            aadharData = AadharData.instance(uid, name, gender, co, dist, lm, house, loc, pc, po, state, street, subdist, vtc);
            // return this.jdbcTemplate.queryForObject(sql, rm, new Object[] {});

        }
        return aadharData;
        /*
         * } catch (final EmptyResultDataAccessException e) { return null; }
         */
    }

}
