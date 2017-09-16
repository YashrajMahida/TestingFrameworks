package CoverLetterSorter;
/**
 * Created by Yashraj Mahida on 3/28/2017.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class CoverLetterChallenge {

    static String url = "https://wd51nn4ogc.execute-api.us-east-1.amazonaws.com/cover_letters?id=";

    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    // function to check email must be validation as per RFC 5322, section 3.4.1.
    public boolean checkEMAIL(String email) {

        if (email == null) {
            return false;
        }

        Pattern pattern = Pattern.compile("^([^`\\{\\|\\}~a-zA-Z0-9_-]+[" +
                " ^`\\{\\|\\}~a-zA-Z0-9_-]+" +
                "@{1}((([0-9A-Za-z_-]+)([\\.]{1}[0-9A-Za-z_-]+)*\\.{1}([A-Za" + "-z]){1,6})|(([0-9]{1,3}[\\.]{1}){3}([0-9]{1,3}){1}))$");

        Matcher matcher = pattern.matcher(email);

        boolean found = false;
        while (matcher.find()) {

            found = true;
        }
        return found;
    }

    //fuction to validate Phone Number as per requirements
    public boolean checkPhoneNumber(String phone) {
        if (phone.matches("[+#()\\-0-9 ]+") && (phone.length() <= 30))

        {
            return true;
        } else
            return false;
    }

    //function to cheack website url validation as per RFC 3986
    public boolean checkURL(String url) {

        if (url == null) {
            return false;
        }

        Pattern pattern = Pattern.compile("^http(s{0,1})://[a-zA-Z0-9_/\\-\\.]+\\.([A-Za-z/]{2,5})[a-zA-Z0-9_/\\&\\?\\=\\-\\.\\~\\%]*");

        Matcher matcher = pattern.matcher(url);

        boolean found = false;

        while (matcher.find()) {
            found = true;
        }
        boolean valid = true;
        try {
            URL u = new URL(url);
        } catch (MalformedURLException e) {
            valid = false;
        }

        if (found && valid) {
            return true;
        } else
            return false;
    }

    private boolean checkOther(JSONArray element) throws JSONException {

        boolean isValidElement = true;
        for (int i = 0; i < element.length(); i++) {
            JSONObject objectValidation = element.getJSONObject(i);
            if (objectValidation.names().length() != 2) {
                isValidElement = false;
                break;
            }
            if (!objectValidation.has("type") || !objectValidation.has("value")) {
                isValidElement = false;
                break;
            }
        }
        return isValidElement;
    }

    private boolean checkChallengeCheckValue(String value) {

        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");

        Matcher matcher = pattern.matcher(value);

        boolean found = false;

        while (matcher.find()) {
            found = true;
        }
        return found;
    }

    private boolean validateName(String name) {

        Pattern pattern = Pattern.compile("[a-zA-Z]");

        Matcher matcher = pattern.matcher((CharSequence) name);

        boolean found = false;

        while (matcher.find()) {
            found = true;
        }
        return found;
    }

    private boolean validateContactDetails(JSONObject contactDetails) throws Exception {
        boolean isValidContactDetails = true;
        boolean isValidWebsite = true;
        boolean isValidPhoneNumber = true;
        boolean isValidEmail = true;
        boolean isValidOther = true;


        Object PhoneNumer = null;
        if (contactDetails.has("phone")) {
            PhoneNumer = contactDetails.get("phone");
            if (PhoneNumer != null) {
                isValidPhoneNumber = checkPhoneNumber(PhoneNumer.toString());
            }
        }

        if (contactDetails.has("website")) {
            Object website = contactDetails.get("website");
            if (website != null) {
                isValidWebsite = checkURL(website.toString());
            }
        }

        Object email = null;
        if (contactDetails.has("email")) {
            email = contactDetails.get("email");
            if (email != null) {
                isValidEmail = checkEMAIL(email.toString());
            }
        }

        if (contactDetails.has("other")) {
            Object other = contactDetails.getJSONArray("other");
            if (other != null) {
                isValidOther = checkOther((JSONArray) other);
            }
        }

        //'contact_details' MUST contain at least one of 'email' and 'phone
        if (PhoneNumer == null && email == null) {
            return false;
        }

        isValidContactDetails = isValidEmail && isValidPhoneNumber && isValidOther && isValidWebsite;
        return isValidContactDetails;
    }

    public boolean validateContent(JSONObject content) throws JSONException {
        boolean isValidcontent = true;
        boolean isValidLetterBody = false;
        boolean isValidChallengeCheckValue = true;
        boolean isKeysValid = false;

        if (content.has("letter_body")) {
            Object letterBody = content.get("letter_body");
            if (letterBody != null) {
                isValidLetterBody = true;
            }
        }
        if (content.has("challenge_checkvalue")) {
            Object challenge = content.get("challenge_checkvalue");
            if (challenge != null) {
                isValidChallengeCheckValue = checkChallengeCheckValue(challenge.toString());
            }
        }

        if ((content.length() > 2)) {
            isValidcontent = false;
        } else if (content.length() == 1) {
            content.has("letter_body");
            isValidcontent = true;
        } else if (content.length() == 2) {
            if (content.has("letter_body") && content.has("challenge_checkvalue")) {
                isValidcontent = true;
            }
        }

        isValidcontent = isValidLetterBody && isValidChallengeCheckValue && isValidcontent;

        return isValidcontent;
    }

    private boolean validCoverLetter(JSONObject json) throws Exception {
        boolean isValid = false;
        boolean validateName = false;

        if (json.has("name")) {
            String name = String.valueOf(json.get("name"));
            validateName = validateName(name);
        }
        JSONObject ContactDetails = json.getJSONObject("contact_details");
        JSONObject Content = json.getJSONObject("content");


        boolean validContanctDetails = validateContactDetails(ContactDetails);
        boolean validContent = validateContent(Content);

        try {
            isValid = validateName && validContanctDetails && validContent;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return isValid;
    }

    public static void main(String[] args) throws Exception {
        CoverLetterChallenge challengeCoverLetter = new CoverLetterChallenge();
        LinkedList<String> invalidDocs = new LinkedList<>();

        for (int i = 0; i <= 99; i++) {
            DecimalFormat decimalFormat = new DecimalFormat("00");
            JSONObject json = challengeCoverLetter.readJsonFromUrl("https://wd51nn4ogc.execute-api.us-east-1.amazonaws.com/cover_letters?id=" + decimalFormat.format(i));
            //System.out.println(json.toString());

            if (challengeCoverLetter.validCoverLetter(json)) {
                //System.out.println(i);
            } else {
                // Handling invlid letter
                System.out.print(decimalFormat.format(i).toString());
                invalidDocs.addLast(decimalFormat.format(i));
            }

        }
    }

}
