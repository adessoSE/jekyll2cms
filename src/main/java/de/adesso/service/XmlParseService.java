package de.adesso.service;

import de.adesso.persistence.Author;
import de.adesso.persistence.Post;
import de.adesso.persistence.PostMetaData;
import de.adesso.util.XmlFieldName;
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

/**
 * This class creates XML files.
 */
@Service
public class XmlParseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlParseService.class);

    @Value("${xml-output.path}")
    private String XML_OUTPUT_PATH;

    private PostParseService postParseService;

    /** List of Field objects */
    private List<Field> fields;

    private static String LANGUAGE_DE = "DE";

    @Autowired
    public XmlParseService(PostParseService postParseService) {
        this.postParseService = postParseService;
    }

    public XmlParseService(){
    }

    /**
     * creates Field objects corresponding to given Post objects properties.
     * @param post
     */
    public void addPostFields(Post post) {
        Field field = new Field(XmlFieldName.TEASER.getXmlFieldName(), post.getTeaserHtml());
        this.fields.add(field);
        field = new Field(XmlFieldName.CONTENT.getXmlFieldName(), post.getContent());
        this.fields.add(field);
    }

    /**
     * creates Field objects corresponding to given PostMetaData objects properties.
     * @param metaData
     */
    public void addMetaDataFields(PostMetaData metaData) {


        Field field = new Field(XmlFieldName.TITLE.getXmlFieldName(), metaData.getTitle());
        this.fields.add(field);
        field = new Field(XmlFieldName.SUBLINE.getXmlFieldName(), metaData.getSubline());
        this.fields.add(field);
        field = new Field(XmlFieldName.LAYOUT.getXmlFieldName(), metaData.getLayout());
        this.fields.add(field);
        field = new Field(XmlFieldName.CATEGORIES.getXmlFieldName(), metaData.getCategories());
        this.fields.add(field);
        field = new Field(XmlFieldName.TAGS.getXmlFieldName(), metaData.getTags());
        this.fields.add(field);
        field = new Field(XmlFieldName.DATE_DATE.getXmlFieldName(), metaData.getDate().toString());
        this.fields.add(field);
        field = new Field(XmlFieldName.CHANGE_DATE.getXmlFieldName(), metaData.getModifiedDate() != null
                ? metaData.getModifiedDate().toString()
                : metaData.getDate().toString() );
        this.fields.add(field);
    }

    /**
     * creates Field objects corresponding to given Author objects properties.
     * @param author
     */
    public void addAuthorFields(Author author) {
        Field field = new Field(XmlFieldName.AUTHOR_FIRST_NAME.getXmlFieldName(), author.getFirstName());
        this.fields.add(field);
        field = new Field(XmlFieldName.AUTHOR_LAST_NAME.getXmlFieldName(), author.getLastName());
        this.fields.add(field);
    }

    /**
     * creates neutral Field objects that have constants or doesn't belong to any other persistence object
     */
    public void addNeutralFields() {
        Field field = new Field(XmlFieldName.LANGUAGE_MULTI_KEYWORD.getXmlFieldName(), LANGUAGE_DE);
        this.fields.add(field);
    }

    /**
     * generates XML files
     */
    public void generateXmlFiles() {
        fields = new ArrayList<>();
        postParseService.getAllHtmlPosts()
                .forEach(post -> {
                    // get corresponding metadata file of current post
                    PostMetaData metaData = postParseService.findCorrespondingMetadataFile(post);

                    addPostFields(post);
                    addMetaDataFields(metaData);
                    addAuthorFields(metaData.getAuthor());
                    addNeutralFields();
                    generateXmlFile(generateXmlFileName(metaData), "testUID");
                });
        LOGGER.info("generating XML-files was successfull.");

    }

    /**
     * Generates XML file name.
     * @param postMetaData - PostMetaData object
     * @return String
     */
    private String generateXmlFileName(PostMetaData postMetaData) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return String.format(XML_OUTPUT_PATH + "%s-%s.xml", sdf.format(postMetaData.getDate()), postMetaData.getTitle());
    }

    /**
     * Generates XML file.
     * @param fileOutputPath - output file path
     * @param documentUID
     */
    public void generateXmlFile(String fileOutputPath, String documentUID) {
        try {
            File file = new File(fileOutputPath);
            LOGGER.info("Creating following XML file: {}", fileOutputPath);
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
