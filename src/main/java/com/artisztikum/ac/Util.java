package com.artisztikum.ac;

import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Common utility methods.
 *
 * @author Adam DAJKA (dajka@artisztikum.hu)
 *
 */
public final class Util
{

	/**
	 * Hidden.
	 */
	private Util()
	{
	}

	/**
	 * Gets the {@link Cookie} from a {@link HttpServletRequest} with a given name.
	 *
	 * @param request
	 *            The {@link HttpServletRequest}.
	 * @param cookieName
	 *            The name of the cookie to get.
	 * @return The {@link Cookie} instance or {@code null} if not found.
	 */
	public static Cookie getCookie(final HttpServletRequest request, final String cookieName)
	{
		for (final Cookie c : request.getCookies()) {
			if (c.getName().equals(cookieName)) {
				return c;
			}
		}
		return null;
	}

	/**
	 * Gets an {@link XPathExpression} with the given {@code expression}.
	 *
	 * @param expression
	 *            The expression to compile.
	 * @return The {@link XPathExpression}
	 * @throws XPathExpressionException
	 *             When cannot compile {@code expression}.
	 */
	public static XPathExpression getXpath(final String expression) throws XPathExpressionException
	{
		return XPathFactory.newInstance().newXPath().compile(expression);
	}

	/**
	 * Returns a String representation of {@code node} for debugging purposes.
	 *
	 * @param node
	 *            The {@link Node}.
	 * @return The String.
	 */
	public static String nodeToString(final Node node)
	{
		final StringWriter sw = new StringWriter();
		try {
			final Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.transform(new DOMSource(node), new StreamResult(sw));
		} catch (final TransformerException te) {
			System.out.println("nodeToString Transformer Exception");
		}
		return sw.toString();
	}

	/**
	 * Wraps an {@link InputSource} around a String.
	 *
	 * @param src
	 *            {@link String}
	 * @return {@link InputSource}.
	 */
	public static InputSource getSource(final String src)
	{
		return new InputSource(new StringReader(src));
	}

}
