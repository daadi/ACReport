package com.artisztikum.ac.ac.adapters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * JAXB Adapter to convert Dates from Active Collab API.
 *
 * @author Adam DAJKA (dajka@artisztikum.hu)
 *
 */
public final class DateAdapter extends XmlAdapter<String, Date> {

    /**
     * The date format.
     */
    private final SimpleDateFormat dateFormat;

    public DateAdapter() {
		this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		this.dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
    
    @Override
    public String marshal(final Date v) throws Exception {
    	return dateFormat.format(v);
    }

    @Override
    public Date unmarshal(final String v) throws Exception {
		if (null == v || v.isEmpty()) {
			return null;
		}
        return dateFormat.parse(v);
    }
}
