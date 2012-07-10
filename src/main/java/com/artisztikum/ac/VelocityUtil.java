package com.artisztikum.ac;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.artisztikum.ac.ac.Company;
import com.artisztikum.ac.ac.Milestone;
import com.artisztikum.ac.ac.User;

/**
 * Helper for formatting dates from velocity templates.
 *
 * @author Adam DAJKA (dajka@artisztikum.hu)
 *
 */
public final class VelocityUtil
{

	/**
	 * String shown in the table in the case of {@code null}.
	 */
	private static final String N_A = "n/a";

	/**
	 * Datetime format.
	 */
	private static final SimpleDateFormat DATETIMEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * Date format.
	 */
	private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * @param date
	 *            The Date instance
	 * @return The string representation in templates.
	 */
	public String simpleDateTime(final Date date)
	{
		return DATETIMEFORMAT.format(date);
	}

	/**
	 * @param date
	 *            The Date instance
	 * @return The string representation in templates.
	 */
	public String simpleDate(final Date date)
	{
		return DATEFORMAT.format(date);
	}

	/**
	 * Gets the name of the User.
	 *
	 * @param u
	 *            The User.
	 * @return The name of the User if available. Otherwise {@value #N_A}.
	 */
	public String getName(final User u)
	{
		if (null == u || null == u.getId()) {
			return N_A;
		}

		return String.format("%s, %s", u.getLastName(), u.getFirstName());
	}

	/**
	 * Gets the Company name of the User.
	 *
	 * @param u
	 *            The User.
	 * @return The Company name of the User if available. Otherwise {@value #N_A}.
	 */
	public String getCompany(final User u)
	{
		if (null == u) {
			return N_A;
		}

		final Company c = u.getCompany();

		if (null == c || c.getName() == null) {
			return N_A;
		}
		return c.getName();
	}

	/**
	 * Gets the Milestone name from the Milestone.
	 *
	 * @param m
	 *            The Milestone.
	 * @return The name of the Milestone if available. Otherwise {@value #N_A}.
	 */
	public String getMilestone(final Milestone m)
	{
		if (null == m || m.getName() == null) {
			return N_A;
		}

		return m.getName();
	}
}
