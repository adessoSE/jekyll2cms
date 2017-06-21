package de.adesso.util;

/**
 * A collection of all possible commands.
 */
public enum Command {

    /**
     * The first parameter defines the argument name (e.g. "java -jar [name] -h" for help)
     * The second parameter defines the method name that gets called in the CmdService
     * The third parameter is a description that gets printed by calling -h from the CLI
     */
    HELP("h", "help", "Show help"),
    CLONE("c", "cloneRepo", "Clone remote repository"),
    BUILD("b", "build", "Run jekyll build command locally"),
    UPDATE("u", "update", "Updates database with new/changed content");

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
