package com.artisztikum.ac.cache;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.artisztikum.ac.Util;
import com.artisztikum.ac.ac.Company;
import com.artisztikum.ac.httpclient.ACHttpClient;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Gets the companies.
 *
 * @author Adam DAJKA (dajka@artisztikum.hu)
 *
 */
public final class CompanyCache extends AbstractUnmarshaller<Company>
{
	/**
	 * Logger.
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(CompanyCache.class);

	/**
	 * The http client.
	 */
	final ACHttpClient client;

	/**
	 * The singleton instance for static access.
	 */
	private static CompanyCache singleton;

	/**
	 * The cache timeout for {@link #cachedCompanyList}.
	 */
	private final long cacheTimeout;

	/**
	 * Company cache. Managed by {@link #getCompany(Long)} and {@link #refreshCompanyList()}.
	 */
	private final Cache<Long, Company> cache;

	/**
	 * The global company list from the AC API.
	 */
	private String cachedCompanyList;

	/**
	 * The {@link Date} when {@link #cachedCompanyList} was downloaded.
	 */
	private Date companyListLastRefresh;

	/**
	 * @param client
	 *            the {@link ACHttpClient} for loading the cache.
	 * @param cacheTimeout
	 *            When the global company must be refreshed.
	 * @param cacheSize
	 *            The max size of the company cache
	 */
	private CompanyCache(final ACHttpClient client, final long cacheTimeout, final long cacheSize)
	{
		super();
		this.client = client;
		this.cacheTimeout = cacheTimeout;
		this.cache = CacheBuilder.newBuilder().maximumSize(cacheSize).build();
	}

	/**
	 * Gets the given {@link Company} from an AC API XML answer represented as {@link InputSource}.
	 *
	 * @param src
	 *            The XML.
	 * @param id
	 *            Id of the company.
	 * @return The {@link Company} or {@code null} if not found.
	 */
	private Company getCompanyFromList(final InputSource src, final long id)
	{
		Preconditions.checkNotNull(src, "Source document cannot be null");
		Preconditions.checkNotNull(id, "Company id cannot be null");

		final Node c;
		try {
			c = (Node) Util.getXpath(String.format("//companies/company[id = %d]", id)).evaluate(
					getCachedCompanyList(), XPathConstants.NODE);
		} catch (final XPathExpressionException e) {
			throw new RuntimeException("Cannot run Xpath on company list!", e);
		}

		if (null == c) {
			return null;
		}

		return unmarshal(new DOMSource(c));
	}

	/**
	 * Gets the company for the given {@code id}.
	 *
	 * First try from the local cache. If no company found then download a new list.
	 *
	 * @param id
	 *            The id of the {@link Company}.
	 * @return The {@link Company}.
	 */
	public Company getCompany(final Long id)
	{
		try {
			return cache.get(id, new Callable<Company>() {

				@Override
				public Company call()
				{
					Company result = getCompanyFromList(getCachedCompanyList(), id);

					if (null == result) {
						// must be a new company if it's missing from our list,
						// let's refresh the list!
						result = getCompanyFromList(refreshCompanyList(), id);
					}

					if (null == result) {
						throw new RuntimeException("Couldn't find company " + id);
					}

					return result;
				}
			});
		} catch (final ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected JAXBContext getMarshallerContext()
	{
		try {
			return JAXBContext.newInstance(Company.class);
		} catch (final JAXBException e1) {
			throw new RuntimeException(e1);
		}
	}

	@Override
	protected Class<Company> getTargetClass()
	{
		return Company.class;
	}

	/**
	 * Downloads a new company list from the AC API. Also updates {@link #companyListLastRefresh}.
	 *
	 * @return The freshly downloaded company list.
	 */
	private synchronized InputSource refreshCompanyList()
	{
		cachedCompanyList = client.sendGetWait("/people/").getContentAsString();
		companyListLastRefresh = new Date();
		cache.invalidateAll();
		return Util.getSource(cachedCompanyList);
	}

	/**
	 * Returns a "valid" company list. If the previous list was downloaded before {@link #cacheTimeout}, then downloads
	 * a new one.
	 *
	 * @return A company list.
	 */
	private synchronized InputSource getCachedCompanyList()
	{
		if (null == cachedCompanyList
				|| ((Calendar.getInstance().getTimeInMillis() - companyListLastRefresh.getTime()) > cacheTimeout)) {
			return refreshCompanyList();
		}
		return Util.getSource(cachedCompanyList);
	}

	/**
	 * Static init of the singleton instance.
	 *
	 * @param client
	 *            the {@link ACHttpClient} for loading the cache.
	 * @param cacheTimeout
	 *            When the global company must be refreshed.
	 * @param cacheSize
	 *            The max size of the company cache
	 */
	public static void init(final ACHttpClient client, final long cacheTimeout, final long cacheSize)
	{
		if (null != singleton) {
			throw new IllegalStateException("Already initialized");
		}

		singleton = new CompanyCache(client, cacheTimeout, cacheSize);
	}

	/**
	 * @return the singleton instance.
	 */
	public static CompanyCache get()
	{
		if (null == singleton) {
			throw new IllegalStateException("Not initialized yet");
		}

		return singleton;
	}
}
