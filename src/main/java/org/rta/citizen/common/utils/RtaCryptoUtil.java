package org.rta.citizen.common.utils;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class RtaCryptoUtil {

    private static String ENCRYPTIONALGO = "AES";

    @SuppressWarnings("unused")
    private static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ENCRYPTIONALGO);
        keyGenerator.init(128);
        SecretKey secretKey = keyGenerator.generateKey();
        return secretKey;
    }

    @SuppressWarnings("unused")
    private static String KeyToString(SecretKey sk) {
        String keyStr = Base64.getEncoder().encodeToString(sk.getEncoded());
        return keyStr;
    }

    private static SecretKey StringToKey(String sk) {
        byte[] decodedKey = Base64.getDecoder().decode(sk);
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, ENCRYPTIONALGO);
        return originalKey;
    }

    public static String generateSecureToken(String userInfo, String claimSecret) {
        String token = userInfo;
        /*
         * try { Cipher cipher = Cipher.getInstance(ENCRYPTIONALGO);
         * cipher.init(Cipher.ENCRYPT_MODE, StringToKey(claimSecret)); byte[] uByte =
         * userInfo.getBytes(); byte[] encryptedByte = cipher.doFinal(uByte); Base64.Encoder encoder
         * = Base64.getEncoder(); token = encoder.encodeToString(encryptedByte); } catch
         * (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
         * IllegalBlockSizeException | BadPaddingException e) { e.printStackTrace(); }
         */

        return token;
    }

    public static String parseSecureToken(String token, String claimSecret) {
        String valueFromToken = token;
        /*
         * try { Base64.Decoder decoder = Base64.getDecoder(); byte[] tokenByte =
         * decoder.decode(token); byte[] encoded = StringToKey(claimSecret).getEncoded(); SecretKey
         * sk = new SecretKeySpec(encoded, ENCRYPTIONALGO);
         * 
         * Cipher cipher = Cipher.getInstance(ENCRYPTIONALGO); cipher.init(Cipher.DECRYPT_MODE, sk);
         * byte[] decryptedByte = cipher.doFinal(tokenByte); valueFromToken = new
         * String(decryptedByte); } catch (NoSuchAlgorithmException | NoSuchPaddingException |
         * InvalidKeyException | IllegalBlockSizeException | BadPaddingException |
         * IllegalArgumentException e) { throw new IllegalArgumentException(
         * "Please provide a valid info."); }
         */

        return valueFromToken;
    }

    /*
     * public static void main(String[] args) {
     * System.out.println(KeyToString(generateSecretKey())); System.out.println(); String token =
     * generateSecureToken("saurabh@gmail.com"); System.out.println(token); String data =
     * parseSecureToken(token); System.out.println(data); }
     */


}
