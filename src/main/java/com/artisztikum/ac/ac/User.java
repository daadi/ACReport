package com.artisztikum.ac.ac;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

import com.artisztikum.ac.cache.CompanyCache;

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
	 * Company id of the user.
	 */
	@XmlElement(name = "company_id")
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
	 * @param companyId
	 *            Company id of the user.
	 */
	public User(final Long id, final String firstName, final String lastName, final Long companyId)
	{
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.companyId = companyId;
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
}
