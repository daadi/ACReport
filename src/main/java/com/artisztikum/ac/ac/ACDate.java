package com.artisztikum.ac.ac;

import java.util.Date;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * A subclass of {@link java.util.Date} for de-serializing the date format of AC API. The {@link Unmarshaller} will call
 * {@link #setTimestamp(Long)} and this will call {@link #setTime(long)} setting the time of this instance.
 * 
 * @author Adam DAJKA (dajka@artisztikum.hu)
 * 
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class ACDate extends Date
{
	/**
	 * Default.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @return the timestamp The timestamp of this instance in second.
	 */
	public Long getTimestamp()
	{
		return getTime() / 1000;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set. This is seconds, not milliseconds.
	 */
	public void setTimestamp(final Long timestamp)
	{
		setTime(timestamp * 1000);
	}

	/**
	 * Constructor for setting the time initially.
	 * 
	 * @param timestamp
	 *            The timestamp to set.
	 * @see #setTimestamp(Long)
	 */
	public ACDate(final Long timestamp)
	{
		super();
		setTimestamp(timestamp);
	}

	/**
	 * Default constructor.
	 */
	public ACDate()
	{
	}
}
