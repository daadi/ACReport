package com.artisztikum.ac.ac;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents and Active Collab Project.
 *
 * @author Adam DAJKA (dajka@artisztikum.hu)
 *
 */
@Data
@EqualsAndHashCode
@XmlAccessorOrder(XmlAccessOrder.UNDEFINED)
@XmlAccessorType(XmlAccessType.FIELD)
public class Project
{
	/**
	 * Project id.
	 */
	private Long id;

	/**
	 * Project name.
	 */
	private String name;

	/**
	 * Project status.
	 */
	@XmlElement(name = "status_verbose")
	private String status;

	/**
	 * Permalink of the project. (points to the Active Collab web interface).
	 */
	private String permalink;
}
