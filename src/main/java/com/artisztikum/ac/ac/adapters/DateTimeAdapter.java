package com.artisztikum.ac.ac.adapters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * JAXB Adapter to convert Datetimes from Active Collab API.
 *
 * @author Adam DAJKA (dajka@artisztikum.hu)
 *
 */
public final class DateTimeAdapter extends XmlAdapter<String, Date>
{

	/**
	 * Datetime format.
	 */
	private SimpleDateFormat dateFormat;

	/**
	 * Default constructor. Intitializes {@link #dateFormat}.
	 */
	public DateTimeAdapter()
	{
		this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// Active Collab API uses GTM times.
		this.dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	@Override
	public String marshal(final Date v) throws Exception
	{
		return dateFormat.format(v);
	}

	@Override
	public Date unmarshal(final String v) throws Exception
	{
		return dateFormat.parse(v);
	}
}
