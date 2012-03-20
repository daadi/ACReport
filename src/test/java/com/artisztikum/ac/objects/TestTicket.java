package com.artisztikum.ac.objects;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.artisztikum.ac.ac.Ticket;
import com.artisztikum.ac.cache.TicketCache;
import com.google.common.collect.Iterables;

/**
 * Tests filling the fields of {@link Ticket}.
 *
 * @author Adam DAJKA (dajka@artisztikum.hu)
 *
 */
public final class TestTicket
{

	/**
	 * The datetime format of the Active Collab API.
	 */
	private static final SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * The date format of the Active Collab API.
	 */
	private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * Tests all required fields of the {@link Ticket}.
	 */
	@Test
	public void testTicketFields()
	{
		final Source testFile = new StreamSource(getClass().getResourceAsStream(
				"/com/artisztikum/ac/objects/ticket.xml"));

		final Ticket t = TicketCache.unmarshalTicket(testFile);

		Assert.assertEquals(t.getId(), Integer.valueOf(94523), "Id mismatch");
		Assert.assertEquals(t.getTicketId(), Integer.valueOf(400), "TicketId mismatch");
		Assert.assertEquals(t.getName(), "Dummy Ticket", "Name mismatch");
		Assert.assertEquals(t.getBody(), "Lorem ipsum dolor sit amet...", "Body mismatch");
		Assert.assertEquals(t.getVersion(), Integer.valueOf(5), "Version mismatch");
		try {
			Assert.assertEquals(t.getPermalink(), new URL("https://ac.dummy.com/projects/251/tickets/400"),
					"Permalink mismatch");
		} catch (final MalformedURLException e) {
			throw new RuntimeException(e);
		}
		Assert.assertEquals(t.getMilestoneId(), Integer.valueOf(0), "MilestoneId mismatch");
		Assert.assertEquals(t.getAssignees().size(), 7, "Assignee size mismatch");
		Assert.assertEquals(Iterables.getLast(t.getAssignees()).getUserId(), Integer.valueOf(1512),
				"Responsible Mismatch");

		try {
			Assert.assertEquals(t.getCreatedOn(), ISO8601DATEFORMAT.parse("2012-02-15 13:24:41"), "CreatedOn mismatch");
			Assert.assertEquals(t.getUpdatedOn(), ISO8601DATEFORMAT.parse("2012-03-02 13:04:50"), "UpdatedOn mismatch");
			Assert.assertEquals(t.getDueOn(), DATEFORMAT.parse("2012-03-05"), "DueOn mismatch");
		} catch (final ParseException e) {
			throw new RuntimeException(e);
		}
	}

}
