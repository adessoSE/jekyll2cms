package de.adesso.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class implements HashingType and generates a SHA-256 hash value.
 */
public class SHA256Hash implements HashingType {

    /**
     * Generates a SHA-256 hash value from the given input value.
     *
     * @param inputValue
     * @return String
     */
    @Override
    public String generateHashValue(String inputValue) {
        StringBuilder hashValue = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(inputValue.getBytes());
            byte byteData[] = md.digest();
            for (int i = 0; i < byteData.length; i++) {
                hashValue.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return hashValue.toString();
    }
}
