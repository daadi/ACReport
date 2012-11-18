package com.artisztikum.ac.ac.adapters;

import static com.artisztikum.ac.ac.TaskPriority.High;
import static com.artisztikum.ac.ac.TaskPriority.Highest;
import static com.artisztikum.ac.ac.TaskPriority.Low;
import static com.artisztikum.ac.ac.TaskPriority.Lowest;
import static com.artisztikum.ac.ac.TaskPriority.Normal;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.artisztikum.ac.ac.TaskPriority;

/**
 * Converts numeric priority values from AC API to {@link TaskPriority} and vice versa.
 * 
 * @author Adam DAJKA (dajka@artisztikum.hu)
 * 
 */
public final class TaskPriorityAdapter extends XmlAdapter<Integer, TaskPriority>
{
	@Override
	public Integer marshal(final TaskPriority data) throws Exception
	{
		switch (data) {
		case Highest:
			return 2;
		case High:
			return 1;
		case Normal:
			return 0;
		case Low:
			return -1;
		case Lowest:
			return -2;
		default:
			return null;

		}
	}

	@Override
	public TaskPriority unmarshal(final Integer xmlData) throws Exception
	{
		switch (xmlData) {
		case 2:
			return Highest;
		case 1:
			return High;
		case 0:
			return Normal;
		case -1:
			return Low;
		case -2:
			return Lowest;
		default:
			return null;

		}
	}
}
