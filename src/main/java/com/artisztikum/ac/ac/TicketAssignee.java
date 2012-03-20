package com.artisztikum.ac.ac;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.artisztikum.ac.ac.adapters.BooleanAdapter;

import lombok.Data;

/**
 * Necessary wrapper for the user id of the assignees.
 *
 * @author Adam DAJKA (dajka@artisztikum.hu)
 *
 */
@XmlAccessorOrder(XmlAccessOrder.UNDEFINED)
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class TicketAssignee
{
	/**
	 * The user id of the assignee.
	 */
	@XmlElement(name = "user_id")
	private Integer userId;

	/**
	 * True, if the assignee is a responsible.
	 */
	@XmlElement(name = "is_owner")
	@XmlJavaTypeAdapter(BooleanAdapter.class)
	private Boolean responsible;
}
