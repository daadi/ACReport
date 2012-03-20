package com.artisztikum.ac.ac;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents and Active Collab Project.
 *
 * @author Adam DAJKA (dajka@artisztikum.hu)
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Project extends AbstractProjectObject
{
	/**
	 * Project name.
	 */
	private String name;

	/**
	 * Project overview.
	 */
	private String overview;

	/**
	 * Project status.
	 */
	private String status;

	/**
	 * Permalink of the project. (points to the Active Collab web interface).
	 */
	private String permalink;

	/**
	 * User id of the leader.
	 */
	private Integer leaderId;

	/**
	 * Company id of the client.
	 */
	private Integer companyId;

	/**
	 * Project group id.
	 */
	private Integer groupId;
}
