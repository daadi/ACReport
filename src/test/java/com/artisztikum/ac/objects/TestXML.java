package com.artisztikum.ac.objects;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

public class TestXML {

	@Test(dataProvider = "dpTestTaskXML")
	public void testTaskXML(final Source xmlSchema, final Source xmlFile)
			throws SAXException {
		SchemaFactory schemaFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(xmlSchema);
		Validator validator = schema.newValidator();
		try {
			validator.validate(xmlFile);
		} catch (SAXException e) {
			Assert.fail(xmlFile.getSystemId() + " is NOT valid" + "Reason: "
					+ e.getLocalizedMessage(), e);
		} catch (IOException e) {
			Assert.fail("IO error", e);
		}
	}

	@DataProvider
	public Iterator<Object[]> dpTestTaskXML() throws Exception {

		final HttpClient client = new HttpClient();

		client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
		client.getSslContextFactory().setTrustAll(true);
		client.start();

		final Collection<Object[]> result = new LinkedList<Object[]>();

		result.add(new Object[] {
				new StreamSource(getClass().getResourceAsStream("/task.xsd")),
				new StreamSource(getClass().getResourceAsStream(
						"/com/artisztikum/ac/objects/task.clean.xml")) });

		ContentExchange ce = new ContentExchange();
		ce.setMethod("GET");
		ce.setURL(System.getProperty("com.artisztikum.ac.test.task.url"));
		client.send(ce);
		ce.waitForDone();
		
		result.add(new Object[] {
				new StreamSource(getClass().getResourceAsStream("/task.xsd")),
				new StreamSource(new StringReader(ce.getResponseContent())) });

		return result.iterator();
	}

}
