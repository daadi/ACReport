package com.artisztikum.ac.ac;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.artisztikum.ac.ac.adapters.DateAdapter;
import com.artisztikum.ac.ac.adapters.DateTimeAdapter;

/**
 * Active Collab object that has a creation date, update date, due date and complete date. It can be completed.
 *
 * @author Adam DAJKA (dajka@artisztikum.hu)
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@XmlAccessorOrder(XmlAccessOrder.UNDEFINED)
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractCompletableProjectObject extends AbstractProjectObject
{
	/**
	 * The creation datetime.
	 */
	@XmlElement(name = "created_on")
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date createdOn;

	/**
	 * The user id of the creator.
	 */
	@XmlElement(name = "created_by_id")
	private Integer createdById;

	/**
	 * Last updated datetime.
	 */
	@XmlElement(name = "updated_on")
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date updatedOn;

	/**
	 * The user id of the last updater.
	 */
	@XmlElement(name = "updated_by_id")
	private Integer updatedById;

	/**
	 * Due on date.
	 */
	@XmlElement(name = "due_on")
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date dueOn;

	/**
	 * The completition date.
	 */
	@XmlElement(name = "completed_on")
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date completedOn;

	/**
	 * The user id of the closer.
	 */
	@XmlElement(name = "completed_by_id")
	private Integer completedById;
}
