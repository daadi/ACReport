package com.artisztikum.ac.ac;

import java.net.URL;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

/**
 * Represents and Active Collab User.
 *
 * @author Adam DAJKA (dajka@artisztikum.hu)
 *
 */
@XmlAccessorOrder(XmlAccessOrder.UNDEFINED)
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class User
{
	/**
	 * User id.
	 */
	private Integer id;

	/**
	 * First name of the user.
	 */
	@XmlElement(name = "first_name")
	private String firstName;

	/**
	 * Last name of the user.
	 */
	@XmlElement(name = "last_name")
	private String lastName;

	/**
	 * Permalink for the user (points to the active collab web interface).
	 */
	private URL permalink;

	/**
	 * Company of the user.
	 */
	private Company company;
}
