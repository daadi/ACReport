package com.artisztikum.ac.cache;

import java.io.UnsupportedEncodingException;
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
import com.artisztikum.ac.ac.AbstractCompletableProjectObject;
import com.artisztikum.ac.ac.AbstractProjectObject;
import com.artisztikum.ac.ac.Milestone;
import com.artisztikum.ac.httpclient.ACHttpClient;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Milestone cache access.
 *
 * @author Adam DAJKA (dajka@artisztikum.hu)
 *
 */
public final class MilestoneCache
{
	/**
	 * The singleton instance.
	 */
	private static MilestoneCache singleton;

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(MilestoneCache.class);

	/**
	 * {@link JAXBContext} for unmarshalling {@link Milestone}s.
	 */
	private static JAXBContext milestoneJC;
	static {
		try {
			milestoneJC = JAXBContext.newInstance(Milestone.class, AbstractProjectObject.class,
					AbstractCompletableProjectObject.class);
		} catch (final JAXBException e1) {
			throw new RuntimeException(e1);
		}
	}

	/**
	 * The cache. Every item expires after 30 minutes.
	 */
	private final Cache<Integer, Milestone> milestones;

	/**
	 * The client.
	 */
	private final ACHttpClient client;

	/**
	 * Hidden.
	 *
	 * @param client
	 *            The client.
	 * @param cacheExpire
	 *            The number of minutes after cached Milestones will expire.
	 * @param maximumSize
	 *            The maximum size of the cache.
	 */
	private MilestoneCache(final ACHttpClient client, final long cacheExpire, final long maximumSize)
	{
		this.client = client;
		// @formatter:off
		this.milestones =
			CacheBuilder.newBuilder()
				.expireAfterWrite(cacheExpire, TimeUnit.MINUTES)
				.concurrencyLevel(4)
				.maximumSize(maximumSize)
				.build();
		// @formatter:on
	}

	/**
	 * Loads or updates all milestones of the project in the cache.
	 *
	 * @param projectId
	 *            The project id.
	 */
	private void loadMilestonesOfProject(final Integer projectId)
	{
		LOG.debug("Fetching milestones of project {}", projectId);

		final ContentExchange ex = client.sendGetWait("/projects/%d/milestones/", projectId);

		final NodeList nl;
		try {
			nl = (NodeList) Util.getXpath("//milestones/milestone").evaluate(Util.getSource(ex.getResponseContent()),
					XPathConstants.NODESET);
		} catch (final XPathExpressionException e) {
			throw new RuntimeException(e);
		} catch (final UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		for (int i = 0; i < nl.getLength(); i++) {
			Milestone milestone;
			milestone = unmarshalMilestone(new DOMSource(nl.item(i)));
			milestones.put(milestone.getId(), milestone);
		}

	}

	/**
	 * Gets the given milestone of the given project. If the milestone is not in the cache, then updates all milestones
	 * of the project.
	 *
	 * @param projectId
	 *            The project id
	 * @param milestoneId
	 *            The milestone id
	 * @return The Milestone
	 */
	public Milestone getMilestone(final Integer projectId, final Integer milestoneId)
	{
		LOG.trace("Getting milestone {} from project {}", milestoneId, projectId);
		final Milestone result = milestones.getIfPresent(milestoneId);
		if (null != result) {
			return result;
		}

		loadMilestonesOfProject(projectId);

		return milestones.getIfPresent(milestoneId);
	}

	/**
	 * Static init of the singleton instance.
	 *
	 * @param client
	 *            the client
	 * @param cacheExpire
	 *            The number of minutes after cached Milestones will expire.
	 * @param maximumSize
	 *            The maximum size of the cache.
	 */
	public static void init(final ACHttpClient client, final long cacheExpire, final long maximumSize)
	{
		if (null != singleton) {
			throw new IllegalStateException("Already initialized!");
		}
		singleton = new MilestoneCache(client, cacheExpire, maximumSize);
	}

	/**
	 * @return The singleton instance
	 */
	public static MilestoneCache get()
	{
		if (null == singleton) {
			throw new IllegalStateException("Not initialized yet!");
		}
		return singleton;
	}

	/**
	 * Unmarshals a {@link Source} into an {@link Milestone} with {@link #milestoneJC}.
	 *
	 * @param src
	 *            The {@link Source}
	 * @return The unmarshalled {@link Milestone} instance.
	 */
	public static Milestone unmarshalMilestone(final Source src)
	{
		try {
			final Unmarshaller u = milestoneJC.createUnmarshaller();
			final JAXBElement<Milestone> doc = u.unmarshal(src, Milestone.class);
			return doc.getValue();
		} catch (final JAXBException e) {
			throw new RuntimeException("Error de-serializing user", e);
		}
	}
}
