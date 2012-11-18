package com.artisztikum.ac.objects;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.artisztikum.ac.ac.Milestone;
import com.artisztikum.ac.ac.Task;
import com.artisztikum.ac.ac.User;
import com.artisztikum.ac.cache.TaskCache;

/**
 * Tests filling the fields of {@link Task}.
 * 
 * @author Adam DAJKA (dajka@artisztikum.hu)
 * 
 */
public final class TestTask
{
	/**
	 * Tests all required fields of the {@link Task}.
	 * 
	 * @param actual
	 *            Actual value
	 * @param expected
	 *            Expected value
	 * @param field
	 *            Name of the tested field
	 */
	@Test(dataProvider = "dpTestTaskField")
	public void testTaskField(final Object actual, final Object expected, final String field)
	{
		Assert.assertEquals(actual, expected, String.format("Invalid %s", field));
	}

	/**
	 * @return Fields to test
	 */
	@DataProvider
	public Iterator<Object[]> dpTestTaskField()
	{
		final Collection<Object[]> result = new LinkedList<Object[]>();

		final Source testFile = new StreamSource(getClass().getResourceAsStream(
				"/com/artisztikum/ac/objects/task.clean.xml"));

		TaskCache.init(null);
		final Task t = TaskCache.get().unmarshal(testFile);

		result.add(new Object[] { t.getTaskId(), Long.valueOf(400), "taskId" });
		result.add(new Object[] { t.getId(), Long.valueOf(94523), "id" });
		result.add(new Object[] { t.getName(), "Dummy Task", "name" });
		result.add(new Object[] { t.getVersion(), Long.valueOf(5), "version" });

		final URL testUrl;
		try {
			testUrl = new URL("https://ac.dummy.com/projects/251/tasks/400");
		} catch (final MalformedURLException e1) {
			throw new RuntimeException(e1);
		}

		result.add(new Object[] { t.getPermalink(), testUrl, "permaLink" });
		result.add(new Object[] { t.getAssignee(),
				new User(980L, "John", "Doe", "https://ac.dummy.com/people/111/users/82"), "assignee" });
		result.add(new Object[] { t.getMilestone(), new Milestone("Dummy Milestone"), "milestone" });
		result.add(new Object[] { t.getCreatedOn().getTime(), 1309768423L * 1000L, "createdOn" });
		result.add(new Object[] { t.getCreatedBy(),
				new User(1338L, "Jane", "Doe", "https://ac.dummy.com/people/111/users/82"), "createdBy" });
		result.add(new Object[] { t.getUpdatedOn().getTime(), 1309768423L * 1000L, "updatedOn" });
		result.add(new Object[] { t.getUpdatedBy(),
				new User(1325L, "Bobby", "Ewing", "https://ac.dummy.com/people/111/users/82"), "createdBy" });
		result.add(new Object[] { t.getDueOn().getTime(), 1309737600L * 1000L, "dueOn" });

		return result.iterator();

	}
}
