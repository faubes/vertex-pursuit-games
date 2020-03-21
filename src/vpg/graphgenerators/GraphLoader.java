package vpg.graphgenerators;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;

public class GraphLoader {

    public static boolean loadIpe(String filename) throws ParserConfigurationException, IOException, SAXException {
        File inputFile = new File(filename);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(inputFile);
        NodeList pathNodes = doc.getElementsByTagName("path");
        // NodeList doesn't support iterator or foreach??
        for (int i = 0; i < pathNodes.getLength(); i++) {

            Node n = pathNodes.item(i);
            NamedNodeMap attribute = n.getAttributes();
            if (attribute.getNamedItem("layer") != null &&
            attribute.getNamedItem("layer").getTextContent().equals("graph")) {
                Element e = (Element) n;
                //System.out.println(e);
                //System.out.println(n.getNodeValue());
                System.out.println(n.getTextContent());
                //System.out.println(n.hasChildNodes());
                //System.out.println(n.getNodeName());
                //System.out.println(n.getAttributes());
                //System.out.println(n.getChildNodes().toString());
            }

        }
        return false;
    }

    public static void main(String[] args) {

        try {
            loadIpe("v7.ipe");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


}
