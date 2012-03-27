package com.artisztikum.ac;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Helper for formatting dates from velocity templates.
 *
 * @author Adam DAJKA (dajka@artisztikum.hu)
 *
 */
public class DateTool
{

	/**
	 * Datetime format.
	 */
	private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * @param date
	 *            The Date instance
	 * @return The string representation in templates.
	 */
	public String simpleDateTime(final Date date)
	{
		return DATEFORMAT.format(date);
	}

}
