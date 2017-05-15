package de.adesso.util;

public enum Command {

    HELP("h", "help", "Show help"),
    CLONE("c", "cloneRepo", "Clone remote repository"),
    BUILD("b", "build", "Run jekyll build command locally");

    private String shortName;
    private String longName;
    private String description;

    Command(String shortName, String longName, String description) {
        this.shortName = shortName;
        this.longName = longName;
        this.description = description;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public String getDescription() {
        return description;
    }
}
