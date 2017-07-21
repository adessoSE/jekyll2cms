package de.adesso.util;

/**
 * A collection of all XML field names .
 */
public enum XmlFieldName {

    TEASER("teaser"),
    CONTENT("content"),
    TITLE("title"),
    SUBLINE("subline"),
    LAYOUT("layout"),
    CATEGORIES("categories"),
    TAGS("tags"),
    DATE_DATE("date_date"),
    CHANGE_DATE("change_date");

    private String XmlFieldName;

    XmlFieldName(String xmlFieldName) {
        XmlFieldName = xmlFieldName;
    }

    public String getXmlFieldName() {
        return XmlFieldName;
    }
}
