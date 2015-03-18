package com.sun.directory.examples;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FindElementsByAbsoluteLocationWithXPath {

	public static void main(String[] args) throws Exception {
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		DocumentBuilder db = dbf.newDocumentBuilder();
		
		Document doc = db.parse(new FileInputStream(new File("in.xml")));
		
		XPathFactory factory = XPathFactory.newInstance();
		
		XPath xpath = factory.newXPath();
		
		String expression;
		Node node;
		NodeList nodeList;
		
		// 1. root element
		expression = "/*";
		node = (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
		System.out.println("1. " + node.getNodeName());
		
		// 2. root element (by name)
		expression = "/rss";
		node = (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
		System.out.println("2. " + node.getNodeName());

		// 3. element under rss
		expression = "/rss/channel";
		node = (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
		System.out.println("3. " + node.getNodeName());
		
		// 4. all elements under rss/channel
		expression = "/rss/channel/*";
		nodeList = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
		System.out.print("4. ");
		for (int i = 0; i < nodeList.getLength(); i++) {
			System.out.print(nodeList.item(i).getNodeName() + " ");
		}
		System.out.println();
		
		// 5. all title elements in the document
		expression = "//title";
		nodeList = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
		System.out.print("5. ");
		for (int i = 0; i < nodeList.getLength(); i++) {
			System.out.print(nodeList.item(i).getNodeName() + " ");
		}
		System.out.println();
		
		// 6. all elements in the document except title
		expression = "//*[name() != 'title']";
		nodeList = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
		System.out.print("6. ");
		for (int i = 0; i < nodeList.getLength(); i++) {
			System.out.print(nodeList.item(i).getNodeName() + " ");
		}
		System.out.println();
		
		// 7. all elements with at least one child element
		expression = "//*[*]";
		nodeList = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
		System.out.print("7. ");
		for (int i = 0; i < nodeList.getLength(); i++) {
			System.out.print(nodeList.item(i).getNodeName() + " ");
		}
		System.out.println();
		
		// 8. all level-5 elements (the root being at level 1)
		expression = "/*/*/*/*";
		nodeList = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
		System.out.print("8. ");
		for (int i = 0; i < nodeList.getLength(); i++) {
			System.out.print(nodeList.item(i).getNodeName() + " ");
		}
		System.out.println();
		
	}
	
}

