package de.adesso.xml;

import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@Component("xml_document_element")
public class Document {

    private List<Field> fields;

    private String uid;

    public Document() {
        this.fields = new ArrayList<>();
    }

    @XmlElement(name = "field")
    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    @XmlAttribute(name = "uid")
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
