package com.artisztikum.ac.ac;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Data;

/**
 * Represents a company.
 *
 * @author Adam DAJKA (dajka@artisztikum.hu)
 *
 */
@XmlAccessorOrder(XmlAccessOrder.UNDEFINED)
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Company
{
	/**
	 * The company id.
	 */
	private Long id;

	/**
	 * The company name.
	 */
	private String name;
}
