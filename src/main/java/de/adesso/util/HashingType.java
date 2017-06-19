package de.adesso.util;

/**
 * Interface for defining the Hashing method.
 */
public interface HashingType {
    /**
     * Generates a hash value from the given input value.
     * @param inputValue
     * @return String
     */
    String generateHashValue(String inputValue);
}

