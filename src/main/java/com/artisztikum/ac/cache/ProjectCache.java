package com.artisztikum.ac.cache;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.eclipse.jetty.client.ContentExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import com.artisztikum.ac.Util;
import com.artisztikum.ac.ac.Project;
import com.artisztikum.ac.httpclient.ACHttpClient;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class ProjectCache
{

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ProjectCache.class);

	/**
	 * Singleton instance.
	 */
	private static ProjectCache singleton;

	/**
	 * The cache. Projects by User.
	 */
	private final Cache<Integer, Iterable<Project>> cache;

	/**
	 * The client.
	 */
	private final ACHttpClient client;

	/**
	 * {@link JAXBContext} for unmarhalling {@link Project}s.
	 */
	private static JAXBContext projectJC;
	static {
		try {
			projectJC = JAXBContext.newInstance(Project.class);
		} catch (final JAXBException e1) {
			throw new RuntimeException(e1);
		}
	}

	public ProjectCache(final ACHttpClient client)
	{
		this.client = client;
		cache = CacheBuilder.newBuilder().concurrencyLevel(4).expireAfterWrite(10L, TimeUnit.MINUTES)
				.maximumSize(5000L).build();
	}

	public Iterable<Project> getProjects()
	{
		final Integer userID = client.getUserId();
		final Iterable<Project> result = cache.getIfPresent(userID);
		if (null != result) {
			return result;
		}

		final ContentExchange ex = client.sendGetWait("/projects/");

		final NodeList nl;
		try {
			nl = (NodeList) Util.getXpath("//projects/project").evaluate(Util.getSource(ex.getResponseContent()),
					XPathConstants.NODESET);
		} catch (final XPathExpressionException e) {
			throw new RuntimeException(e);
		} catch (final UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		final List<Project> projectsOfUser = new ArrayList<Project>();
		for (int i = 0; i < nl.getLength(); i++) {
			Project milestone;
			milestone = unmarshalProject(new DOMSource(nl.item(i)));
			projectsOfUser.add(milestone);
		}
		cache.put(userID, projectsOfUser);

		return projectsOfUser;
	}

	public static void init(final ACHttpClient client)
	{
		if (null != singleton) {
			throw new IllegalStateException("Already initialized");
		}
		singleton = new ProjectCache(client);
	}

	public static ProjectCache get()
	{
		if (null == singleton) {
			throw new IllegalStateException("Not initialized yet");
		}
		return singleton;
	}

	/**
	 * Unmarshals a {@link Source} into a {@link Project} with {@link #projectJC}.
	 *
	 * @param src
	 *            The {@link Source}
	 * @return The unmarshalled {@link Project} instance.
	 */
	public static Project unmarshalProject(final Source src)
	{
		try {
			final Unmarshaller u = projectJC.createUnmarshaller();
			final JAXBElement<Project> doc = u.unmarshal(src, Project.class);
			return doc.getValue();
		} catch (final JAXBException e) {
			throw new RuntimeException("Error de-serializing ticket", e);
		}
	}

}
