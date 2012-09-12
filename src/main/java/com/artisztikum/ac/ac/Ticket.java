package com.artisztikum.ac.ac;

import java.net.URL;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.artisztikum.ac.cache.MilestoneCache;
import com.artisztikum.ac.cache.UserCache;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * An ActiveCollab ticket.
 *
 * @author Adam DAJKA (dajka@artisztikum.hu)
 *
 */
@XmlAccessorOrder(XmlAccessOrder.UNDEFINED)
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@EqualsAndHashCode(callSuper = true)
public final class Ticket extends AbstractCompletableProjectObject
{
	/**
	 * The ticket id. As in the permalink.
	 */
	@XmlElement(name = "ticket_id")
	private Integer ticketId;

	/**
	 * The project id.
	 */
	@XmlElement(name = "project_id")
	private Integer projectId;

	/**
	 * Name of the ticket.
	 */
	private String name;

	/**
	 * Priority of the ticket.
	 */
	private Long priority;
	
	/**
	 * Body (opening comment).
	 */
	private String body;

	/**
	 * Version. Used for cache validation.
	 */
	private Integer version;

	/**
	 * The permalink to the ticket in the AC web interface.
	 */
	private URL permalink;

	/**
	 * The id of the milestone that this ticket is assigned.
	 */
	@XmlElement(name = "milestone_id")
	private Integer milestoneId;

	/**
	 * List of assignees.
	 */
	@XmlElementWrapper(name = "assignees")
	@XmlElement(name = "assignee")
	private List<TicketAssignee> assignees;

	/**
	 * @return The user_id of the responsible of this ticket or {@code null} if there is no responsible.
	 */
	public Integer getResponsibleId()
	{
		try {
			return Iterables.find(assignees, new Predicate<TicketAssignee>() {

				@Override
				public boolean apply(final TicketAssignee input)
				{
					return input.getResponsible();
				}
			}).getUserId();
		} catch (final NoSuchElementException e) {
			return null;
		}
	}

	/**
	 * @return The {@link User} object representing the responsible from the {@link UserCache}.
	 */
	public User getResponsible()
	{
		final Integer id = getResponsibleId();
		if (null == id) {
			return null;
		}
		return UserCache.get().getUserByProject(getProjectId(), id);
	}

	/**
	 * @return The {@link User} object representing the creator from the {@link UserCache}.
	 */
	public User getCreatedBy()
	{
		final Integer id = getCreatedById();
		if (null == id) {
			return null;
		}
		return UserCache.get().getUserByProject(getProjectId(), id);
	}

	/**
	 * @return The {@link User} object representing the creator from the {@link UserCache}.
	 */
	public User getUpdatedBy()
	{
		final Integer id = getUpdatedById();
		if (null == id) {
			return null;
		}
		return UserCache.get().getUserByProject(getProjectId(), id);
	}

	/**
	 * @return the {@link Milestone} object representing the milestone from the {@link MilestoneCache}.
	 */
	public Milestone getMilestone()
	{
		if (null == getMilestoneId() || Integer.valueOf(0).equals(getMilestoneId())) {
			return null;
		}
		return MilestoneCache.get().getMilestone(getProjectId(), getMilestoneId());
	}
}
