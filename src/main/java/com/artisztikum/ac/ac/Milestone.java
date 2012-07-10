package com.artisztikum.ac.ac;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Represents an Active Collab milestone.
 *
 * @author Adam DAJKA (dajka@artisztikum.hu)
 *
 */
@XmlAccessorOrder(XmlAccessOrder.UNDEFINED)
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@ToString
@EqualsAndHashCode
public class Milestone
{
	/**
	 * Name of the milestone.
	 */
	private String name;

	/**
	 * Default.
	 */
	public Milestone()
	{
	}

	/**
	 * @param name
	 *            The name of this Milestone.
	 */
	public Milestone(final String name)
	{
		this.name = name;
	}
}
