package com.artisztikum.ac.ac.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * JAXB Adapter to convert Boolean from Active Collab API.
 *
 * @author Adam DAJKA (dajka@artisztikum.hu)
 *
 */
public final class BooleanAdapter extends XmlAdapter<String, Boolean>
{
	@Override
	public String marshal(final Boolean v) throws Exception
	{
		return Boolean.TRUE.equals(v) ? "1" : "0";
	}

	@Override
	public Boolean unmarshal(final String v) throws Exception
	{
		return "1".equals(v) ? Boolean.TRUE : Boolean.FALSE;
	}
}
