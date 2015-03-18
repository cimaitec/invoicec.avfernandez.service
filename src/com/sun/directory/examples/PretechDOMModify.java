package com.sun.directory.examples;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PretechDOMModify {
       public static void main(String[] args) {
              try {
                     String filepath = "PretechDOM.xml";
                     DocumentBuilderFactory docFactory = DocumentBuilderFactory
                                  .newInstance();
                     DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                     Document doc = docBuilder.parse(filepath);

                     // Get the ordernumber element
                     Node ordernumber = doc.getElementsByTagName("Ordernumber").item(0);

                     // update ordernumber attribute
                     NamedNodeMap attributes = ordernumber.getAttributes();
                    
                     Node nodeAttr = attributes.getNamedItem("Number");
                     nodeAttr.setTextContent("1111");

                     // append a new node to ordernumber
                     Element orderOffercoupon = doc.createElement("OrderCoupon");
                     orderOffercoupon.appendChild(doc.createTextNode("NEWYEAR"));
                     ordernumber.appendChild(orderOffercoupon);

                     // loop the ordernumber child node
                     NodeList list = ordernumber.getChildNodes();
                     //Updating Order price
                     //Removing Order Discount
                     for (int i = 0; i < list.getLength(); i++) {
                           Node node = list.item(i);
                           if ("OrderitemPrice".equals(node.getNodeName())) {
                                  node.setTextContent("100");
                           }
                           // Remove OrderDiscount Element
                           if ("OrderDiscount".equals(node.getNodeName())) {
                                  ordernumber.removeChild(node);
                           }
                     }
                     // Writing to xml file
                     TransformerFactory transformerFactory = TransformerFactory
                                  .newInstance();
                     Transformer transformer = transformerFactory.newTransformer();
                     DOMSource source = new DOMSource(doc);
                     StreamResult result = new StreamResult(new File(filepath));
                     transformer.transform(source, result);
                     System.out.println("Updation completed.. Please check PretechDOM");
              } catch (ParserConfigurationException pce) {
                     pce.printStackTrace();
              } catch (TransformerException tfe) {
                     tfe.printStackTrace();
              } catch (IOException ioe) {
                     ioe.printStackTrace();
              } catch (SAXException sae) {
                     sae.printStackTrace();
              }
       }

}