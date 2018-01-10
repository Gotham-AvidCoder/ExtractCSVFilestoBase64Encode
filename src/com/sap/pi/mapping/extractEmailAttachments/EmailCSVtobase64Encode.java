package com.sap.pi.mapping.extractEmailAttachments;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sap.aii.mapping.api.*;

public class EmailCSVtobase64Encode extends AbstractTransformation {

	/**
	 * Testing Purpose
	 */

	// public static void main(String[] args) {
	// // TODO Auto-generated method stub
	//
	// }

	List<String> attachmentNames = new ArrayList<String>();
	Map<String, String> csvAttachments = new TreeMap<String, String>();
	List<String> contentIDs = new ArrayList<String>();

	@Override
	public void transform(TransformationInput arg0, TransformationOutput arg1) throws StreamTransformationException {

		OutputStream outputStream = arg1.getOutputPayload().getOutputStream();
		InputHeader inputHeader = arg0.getInputHeader();
		InputAttachments inputAttachments = arg0.getInputAttachments();

		getAttachmentsInBase64(inputAttachments);
		
		try {
			
			writeXMLtoOutputstream(createXMLwithEncodedFiles(inputHeader), outputStream);
			
		} catch (ParserConfigurationException e) {
			getTrace().addWarning("Parsing of xml document failed");
			e.printStackTrace();
		}

	}

	public void getAttachmentsInBase64(InputAttachments inputAttachments) {
		if (inputAttachments.areAttachmentsAvailable()) {
			contentIDs = (List<String>) inputAttachments.getAllContentIds(true);
			for (int attachmentCount = 0; attachmentCount < contentIDs.size(); attachmentCount++) {

				String contentType = inputAttachments.getAttachment(contentIDs.get(attachmentCount)).getContentType();
				contentType = contentType.replaceAll("\"", "");
				int lastIndex = contentType.lastIndexOf("=") + 1;
				String attachmentName = contentType.substring(lastIndex, contentType.length());

				attachmentNames.add(attachmentName);
				csvAttachments.put(attachmentName,
						inputAttachments.getAttachment(contentIDs.get(attachmentCount)).getBase64EncodedContent());
			}
		}
	}

	public Document createXMLwithEncodedFiles(InputHeader inputHeader) throws ParserConfigurationException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();

		Element rootElement = document.createElement("Record");
		document.appendChild(rootElement);

		Element header = document.createElement("Header");
		rootElement.appendChild(header);

		Element messageId = document.createElement("MESSAGE_ID");
		messageId.appendChild(document.createTextNode(inputHeader.getMessageId()));
		header.appendChild(messageId);

		Element senderParty = document.createElement("SENDER_PARTY");
		senderParty.appendChild(document.createTextNode(inputHeader.getSenderParty()));
		header.appendChild(header);

		Element senderService = document.createElement("SENDER_SERVICE");
		senderService.appendChild(document.createTextNode(inputHeader.getSenderService()));
		header.appendChild(senderParty);

		Element interfaceName = document.createElement("INTERFACE");
		interfaceName.appendChild(document.createTextNode(inputHeader.getInterface()));
		header.appendChild(interfaceName);

		Element interfaceNamespace = document.createElement("INTERFACE_NAMESPACE");
		interfaceNamespace.appendChild(document.createTextNode(inputHeader.getInterfaceNamespace()));
		header.appendChild(interfaceNamespace);

		Element qualityOfService = document.createElement("QUALITY_OF_SERVICE");
		qualityOfService.appendChild(document.createTextNode("Exactly_Once"));
		header.appendChild(qualityOfService);

		Element receiverParty = document.createElement("RECEIVER_PARTY");
		receiverParty.appendChild(document.createTextNode(inputHeader.getReceiverParty()));
		header.appendChild(receiverParty);

		Element receiverService = document.createElement("RECEIVER_SERVICE");
		receiverService.appendChild(document.createTextNode(inputHeader.getReceiverService()));
		header.appendChild(receiverService);

		for (int uploadCount = 0; uploadCount < contentIDs.size(); uploadCount++) {

			Element uploadNode = document.createElement("Upload");
			rootElement.appendChild(uploadNode);

			Element fileName = document.createElement("Filename");
			fileName.appendChild(document.createTextNode(attachmentNames.get(uploadCount)));
			uploadNode.appendChild(fileName);

			Element encodedNode = document.createElement("Base64EncodedContent");
			encodedNode.appendChild(document.createTextNode(csvAttachments.get(attachmentNames.get(uploadCount))));
			uploadNode.appendChild(encodedNode);

		}

		return document;

	}

	public void writeXMLtoOutputstream(Document document, OutputStream outputStream) {

		try {

			TransformerFactory transformFactory = TransformerFactory.newInstance();
			Transformer transformer = transformFactory.newTransformer();
			transformer.transform(new DOMSource(document), new StreamResult(outputStream));

		} catch (TransformerConfigurationException e) {
			getTrace().addWarning("TransformerConfiguration has thrown this Exception");
			e.printStackTrace();
		} catch (TransformerException e) {
			getTrace().addWarning("Transformer has thrown this Exception");
			e.printStackTrace();
		}

	}

}
