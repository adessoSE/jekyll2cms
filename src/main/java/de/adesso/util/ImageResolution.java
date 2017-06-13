package de.adesso.util;

/**
 * This enum defines the constants for image resolutions into which images will be transformed.
 */
public enum ImageResolution {
    // 320, 570, 820, 1070

    SMALL(320),
    MEDIUM(570),
    LARGE(820),
    XLARGE(1070);

    private int value;

    /**
     * Constructor.
     * @param value - Value of constant.
     */
    ImageResolution(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
