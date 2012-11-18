package com.artisztikum.ac.ac;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

import com.artisztikum.ac.cache.CompanyCache;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

/**
 * Represents and Active Collab User.
 *
 * @author Adam DAJKA (dajka@artisztikum.hu)
 *
 */
@XmlAccessorOrder(XmlAccessOrder.UNDEFINED)
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public final class User
{
	/**
	 * User id.
	 */
	private Long id;

	/**
	 * First name of the User.
	 */
	@XmlElement(name = "first_name")
	private String firstName;

	/**
	 * Last name of the user.
	 */
	@XmlElement(name = "last_name")
	private String lastName;

	/**
	 * Permalink of the user. Used to get the company id.
	 */
	private String permalink;

	/**
	 * The company id.
	 */
	private Long companyId;

	/**
	 * Default.
	 */
	public User()
	{
	}

	/**
	 * @param id
	 *            User id.
	 * @param firstName
	 *            First name of the User.
	 * @param lastName
	 *            Last name of the User.
	 * @param permalink
	 *            Permalink for of the user. Used to get {@link #companyId}.
	 */
	public User(final Long id, final String firstName, final String lastName, final String permalink)
	{
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.permalink = permalink;
		this.companyId = getCompanyFromPermalink(permalink);
	}

	/**
	 * @return The {@link Company} from {@link CompanyCache} based on {@link #companyId}
	 */
	public Company getCompany()
	{
		if (null == companyId) {
			return null;
		}
		return CompanyCache.get().getCompany(companyId);
	}

	/**
	 * Overwrite the values for empty subelements to {@code null} in order to easily tested in the velocity macros.
	 *
	 * @param u
	 *            The unmarshaller
	 * @param parent
	 *            The parent
	 */
	public void afterUnmarshal(final Unmarshaller u, final Object parent)
	{
		if (null == permalink) {
			return;
		}

		companyId = getCompanyFromPermalink(permalink);
	}

	/**
	 * Gets the companyId from the given user's permalink.
	 *
	 * @param permalinkURL
	 *            The permalink
	 * @return The company id (actually the 3rd element from the url separated by slashes).
	 */
	private static Long getCompanyFromPermalink(final String permalinkURL)
	{
		return Long.valueOf(Iterables.get(Splitter.on("/").omitEmptyStrings().split(permalinkURL), 3));
	}
}
