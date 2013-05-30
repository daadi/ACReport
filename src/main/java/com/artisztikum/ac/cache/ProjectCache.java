package com.artisztikum.ac.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.eclipse.jetty.client.api.ContentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import com.artisztikum.ac.Util;
import com.artisztikum.ac.ac.Project;
import com.artisztikum.ac.httpclient.ACHttpClient;
import com.google.common.base.Predicate;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Iterables;

/**
 * Stores {@link Project}s.
 *
 * @author Adam DAJKA (dajka@artisztikum.hu)
 *
 */
public final class ProjectCache extends AbstractUnmarshaller<Project>
{
	/**
	 * Logger.
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(ProjectCache.class);

	/**
	 * Singleton instance.
	 */
	private static ProjectCache singleton;

	/**
	 * The cache. Projects by User.
	 */
	private final Cache<Long, Iterable<Project>> cache;

	/**
	 * The client.
	 */
	private final ACHttpClient client;

	/**
	 * @param client
	 *            the {@link ACHttpClient} for loading the cache.
	 */
	private ProjectCache(final ACHttpClient client)
	{
		this.client = client;
		cache = CacheBuilder.newBuilder().concurrencyLevel(4).expireAfterWrite(10L, TimeUnit.MINUTES)
				.maximumSize(5000L).build();
	}

	/**
	 * @return The {@link Project}s found in AC API for the given user.
	 */
	public Iterable<Project> getProjects()
	{
		final Long userID = client.getUserId();
		final Iterable<Project> result = cache.getIfPresent(userID);
		if (null != result) {
			return result;
		}

		final ContentResponse response = client.sendGetWait("/projects/");

		final NodeList nl;
		try {
			nl = (NodeList) Util.getXpath("//projects/project").evaluate(Util.getSource(response.getContentAsString()),
					XPathConstants.NODESET);
		} catch (final XPathExpressionException e) {
			throw new RuntimeException(e);
		}

		final List<Project> projectsOfUser = new ArrayList<Project>();
		for (int i = 0; i < nl.getLength(); i++) {
			Project milestone;
			milestone = unmarshal(new DOMSource(nl.item(i)));
			projectsOfUser.add(milestone);
		}
		cache.put(userID, projectsOfUser);

		return projectsOfUser;
	}

	/**
	 * Static init.
	 *
	 * @param client
	 *            The {@link ACHttpClient} to use.
	 */
	public static void init(final ACHttpClient client)
	{
		if (null != singleton) {
			throw new IllegalStateException("Already initialized");
		}
		singleton = new ProjectCache(client);
	}

	/**
	 * @return The singleton instance.
	 */
	public static ProjectCache get()
	{
		if (null == singleton) {
			throw new IllegalStateException("Not initialized yet");
		}
		return singleton;
	}

	/**
	 * @param projectId
	 *            The id of the project
	 * @return The project with the given projectId
	 */
	public Project getProject(final Long projectId)
	{
		return Iterables.find(getProjects(), new Predicate<Project>() {
			@Override
			public boolean apply(final Project input)
			{
				return input.getId().equals(projectId);
			}
		});

	}

	@Override
	protected JAXBContext getMarshallerContext()
	{
		try {
			return JAXBContext.newInstance(Project.class);
		} catch (final JAXBException e1) {
			throw new RuntimeException(e1);
		}
	}

	@Override
	protected Class<Project> getTargetClass()
	{
		return Project.class;
	}
}
