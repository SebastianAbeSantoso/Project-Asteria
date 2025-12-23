package asteria.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class KeyProviderImpl implements KeyProvider {

    private final String S = "AsteriaProject25";
    String BLOB = "2hetp6AH8JdbNyPh7GtnW/RtTux6CaPdm0Vyts1KT4RvTjhbwDXhXb5rMCpkAOYkDQANE95/1LMFIt0NgkOFj6cuXzx+gt+mfvW9DzlnYy0JRCFWzCLeAXF8Utq6OtGTI1";

    public String getKey() {
        try {
            SecretKeySpec spec = new SecretKeySpec(S.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, spec);

            return new String(cipher.doFinal(Base64.getDecoder().decode(BLOB)));
        } catch (Exception e) {
            return null;
        }
    }
}
