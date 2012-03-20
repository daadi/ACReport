package com.artisztikum.ac.ac.adapters;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public String marshal(final Date v) throws Exception {
        return dateFormat.format(v);
    }

    @Override
    public Date unmarshal(final String v) throws Exception {
        return dateFormat.parse(v);
    }
}
