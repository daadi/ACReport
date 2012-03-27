package com.artisztikum.ac;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTool {

	/**
	 * Datetime format.
	 */
	private SimpleDateFormat dateFormat;

	public DateTool() {
		this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		// TODO this is not the proper way to do this
		this.dateFormat.setTimeZone(Calendar.getInstance().getTimeZone());
	}

	public String simpleDateTime(final Date date) {
		return dateFormat.format(date);
	}

}
