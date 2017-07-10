package de.adesso.xml;

import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;

@Component("xml_field_element")
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class Field {

    @XmlAttribute(name = "name", required = true)
    private String fieldNameAttr;


    @XmlValue
    private String fieldValue;

    public Field() {}

    public Field(String fieldNameAttr) {

        this.fieldNameAttr = fieldNameAttr;

    }

    public Field(String fieldNameAttr, String fieldValue) {
        this.fieldNameAttr = fieldNameAttr;
        this.setFieldValue(fieldValue);
    }

    public String getFieldNameAttr() {
        return fieldNameAttr;
    }

    public void setFieldNameAttr(String fieldName) {
        this.fieldNameAttr = fieldName;

    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {

        this.fieldValue = String.format("<![CDATA[%s]]>", fieldValue);
    }
}
