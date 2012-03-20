package com.artisztikum.ac.ac;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents an Active Collab milestone.
 *
 * @author Adam DAJKA (dajka@artisztikum.hu)
 *
 */
@XmlAccessorOrder(XmlAccessOrder.UNDEFINED)
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@EqualsAndHashCode(callSuper = true)
public class Milestone extends AbstractCompletableProjectObject
{
	/**
	 * Name of the milestone.
	 */
	private String name;

	/**
	 * Body of the milestone.
	 */
	private String body;

	/**
	 * State of the milestone.
	 */
	private Integer state;

	/**
	 * Visibility of the milestone.
	 */
	private Integer visibility;

	/**
	 * Version of the milestone.
	 */
	private Integer version;
}
