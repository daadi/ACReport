package com.artisztikum.ac.objects;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.testng.Assert;
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
	 */
	@Test
	public void testTaskFields()
	{
		final Source testFile = new StreamSource(getClass().getResourceAsStream(
				"/com/artisztikum/ac/objects/task.clean.xml"));

		TaskCache.init(null);
		final Task t = TaskCache.get().unmarshal(testFile);

		Assert.assertEquals(t.getTaskId(), Long.valueOf(400), "TaskId mismatch");
		Assert.assertEquals(t.getId(), Long.valueOf(94523), "Id mismatch");
		Assert.assertEquals(t.getName(), "Dummy Task", "Name mismatch");
		Assert.assertEquals(t.getBody(), "Lorem ipsum dolor sit amet...", "Body mismatch");
		Assert.assertEquals(t.getVersion(), Long.valueOf(5), "Version mismatch");
		try {
			Assert.assertEquals(t.getPermalink(), new URL("https://ac.dummy.com/projects/251/tasks/400"),
					"Permalink mismatch");
		} catch (final MalformedURLException e) {
			throw new RuntimeException(e);
		}
		Assert.assertEquals(t.getAssignee(), new User(980L, "John", "Doe", 111L), "Invalid Assignee");
		Assert.assertEquals(t.getMilestone(), new Milestone("Dummy Milestone"), "Invalid milestone");

		Assert.assertEquals(t.getCreatedOn().getTime(), 1338547674L * 1000L, "Invalid createdOn");
		Assert.assertEquals(t.getCreatedBy(), new User(1338L, "Jane", "Doe", 144L), "Invalid createdBy");

		Assert.assertEquals(t.getUpdatedOn().getTime(), 1338897179L * 1000L, "Invalid updatedOn");
		Assert.assertEquals(t.getUpdatedBy(), new User(1325L, "Bobby", "Ewing", 144L), "Invalid createdBy");

		Assert.assertEquals(t.getDueOn().getTime(), 1338508800L * 1000L, "Invalid dueOn");
	}
}
