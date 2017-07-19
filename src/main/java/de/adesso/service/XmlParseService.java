package de.adesso.service;

import de.adesso.persistence.Post;
import de.adesso.persistence.PostMetaData;
import de.adesso.xml.Document;
import de.adesso.xml.Documents;
import de.adesso.xml.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class XmlParseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlParseService.class);

    @Value("${xml-output.path}")
    private String XML_OUTPUT_PATH;

    private PostParseService postParseService;

    private List<Field> fields;

    @Autowired
    public XmlParseService(PostParseService postParseService) {
        this.postParseService = postParseService;
    }

    public XmlParseService(){
    }

    public void addFieldFromPost(Post post) {
        Field field = new Field("teaser", post.getTeaserHtml());
        this.fields.add(field);
        field = new Field("content", post.getContent());
        this.fields.add(field);
    }

    public void addFieldFromMetaData(PostMetaData metaData) {

        Field field = new Field("title", metaData.getTitle());
        this.fields.add(field);
        field = new Field("subline", metaData.getSubline());
        this.fields.add(field);
        field = new Field("layout", metaData.getLayout());
        this.fields.add(field);
        field = new Field("categories", metaData.getCategories());
        this.fields.add(field);
        field = new Field("tags", metaData.getTags());
        this.fields.add(field);
        field = new Field("date_date", metaData.getDate().toString());
        this.fields.add(field);
        field = new Field("change_date", metaData.getModifiedDate() != null
                ? metaData.getModifiedDate().toString()
                : metaData.getDate().toString() );
        this.fields.add(field);
    }

    public void generateXmlPostFiles() {
        fields = new ArrayList<>();
        postParseService.getAllHtmlPosts()
                .forEach(post -> {
                    // get corresponding metadata file of current post
                    PostMetaData metaData = postParseService.findCorrespondingMetadataFile(post);

                    addFieldFromPost(post);
                    addFieldFromMetaData(metaData);
                    generateXmlPostFile(generateXmlFileName(metaData), post.getHashValue());
                });
        LOGGER.info("generating XML-files was successfull.");

    }

    private String generateXmlFileName(PostMetaData postMetaData) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return String.format(XML_OUTPUT_PATH + "%s-%s.xml", sdf.format(postMetaData.getDate()), postMetaData.getTitle());
    }

    public void generateXmlPostFile(String filePath, String documentUID) {
        try {
            File file = new File(filePath);
            LOGGER.info("Creating following XML file: {}", filePath);
            JAXBContext jaxbContext = JAXBContext.newInstance(Documents.class);

            /* **** Populate XML Elements **** */
            Documents documents = new Documents();
            Document doc = new Document();
            doc.setUid(documentUID);
            doc.setFields(fields);

            documents.getDocuments().add(doc);

            Marshaller marshaller = null;

            marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(documents, file);

        } catch (JAXBException e) {
            e.printStackTrace();
        }


    }
}
