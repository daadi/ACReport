package com.artisztikum.ac.ac;

import lombok.Data;

/**
 * Base class for all Active Collab objects.
 *
 * @author Adam DAJKA (dajka@artisztikum.hu)
 */
@Data
public abstract class AbstractProjectObject
{
	/**
	 * Internal id.
	 * Refer to AC api docs, we never use this.
	 */
	private Integer id;

	/**
	 * Type of the object.
	 * Refer to AC api docs, we never use this.
	 */
	private String type;
}
