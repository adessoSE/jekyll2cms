package de.adesso.xml;

import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@Component("xml_root_element")
@XmlRootElement(name = "documents")
public class Documents {


    private List<Document> documents;


    public Documents() {
        this.documents = new ArrayList<>();
    }

    @XmlElement(name = "document")
    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

}
