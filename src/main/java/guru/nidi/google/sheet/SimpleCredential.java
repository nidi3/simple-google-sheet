package guru.nidi.google.sheet;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

/**
 *
 */
public class SimpleCredential {
    private static final String SCOPE_SHEET = "https://spreadsheets.google.com/feeds/";
    private static final String SCOPE_DRIVE = "https://www.googleapis.com/auth/drive";

    final Credential credential;
    final HttpTransport httpTransport;
    final JsonFactory jsonFactory;

    public SimpleCredential() throws GeneralSecurityException, IOException {
        this(null, null);
    }

    public SimpleCredential(String serviceAccountEmail, File serviceAccountPrivateKeyFileP12) throws GeneralSecurityException, IOException {
        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        jsonFactory = JacksonFactory.getDefaultInstance();

        credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setServiceAccountId(serviceAccountEmail)
                .setServiceAccountScopes(Arrays.asList(SCOPE_SHEET, SCOPE_DRIVE))
                .setServiceAccountPrivateKeyFromP12File(serviceAccountPrivateKeyFileP12)
                .build();
    }

    public Credential getCredential() {
        return credential;
    }
}
