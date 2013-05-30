package com.artisztikum.ac.cache;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.InputStreamResponseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.artisztikum.ac.Util;
import com.artisztikum.ac.ac.Milestone;
import com.artisztikum.ac.ac.Task;
import com.artisztikum.ac.ac.User;
import com.artisztikum.ac.httpclient.ACHttpClient;

/**
 * Contains the {@link Task}s.
 *
 * @author Adam DAJKA (dajka@artisztikum.hu)
 *
 */
public final class TaskCache extends AbstractUnmarshaller<Task>
{
	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(TaskCache.class);

	/**
	 * Singleton instance.
	 */
	private static TaskCache singleton;

	/**
	 * Task cache. Configurable maximum size.
	 */
	private final Map<String, Task> allTasks = Collections
			.<String, Task> synchronizedMap(new LinkedHashMap<String, Task>() {
				/**
				 * Serial.
				 */
				private static final long serialVersionUID = 1L;

				private static final int MAX_SIZE = 10000;

				@Override
				protected boolean removeEldestEntry(final Map.Entry<String, Task> eldest)
				{
					return size() > MAX_SIZE;
				}
			});

	/**
	 * The http client.
	 */
	private final ACHttpClient client;

	/**
	 * @param client
	 *            Http client to use.
	 */
	public TaskCache(final ACHttpClient client)
	{
		this.client = client;
	}

	/**
	 * Gets all tasks for the given project.
	 *
	 * @param projectId
	 *            The id of the project
	 * @return All tasks for the given projects.
	 */
	public List<Task> getTasks(final String projectId)
	{
		// get the fresh list of tasks from the Active Collab API
		final ContentResponse response = client.sendGetWait("/projects/%s/tasks/", projectId);
		LOG.debug("Got tasks xml");

		// parse the response => get the list of tasks
		final NodeList nl;
		try {
			nl = (NodeList) Util.getXpath("//tasks/task[is_completed = '0']").evaluate(
					new InputSource(new StringReader(response.getContentAsString())), XPathConstants.NODESET);
		} catch (final XPathExpressionException e) {
			throw new RuntimeException(e);
		}

		final List<Task> result = new ArrayList<Task>();

		final Map<String, InputStreamResponseListener> reqs = new HashMap<String, InputStreamResponseListener>();
		for (int i = 0; i < nl.getLength(); i++) {
			final Node n = nl.item(i);

			final String id;
			final String taskId;
			final String taskVersion;
			try {
				id = (String) Util.getXpath("id").evaluate(n, XPathConstants.STRING);
				taskId = (String) Util.getXpath("task_id").evaluate(n, XPathConstants.STRING);
				taskVersion = (String) Util.getXpath("version").evaluate(n, XPathConstants.STRING);
			} catch (final XPathExpressionException e) {
				throw new RuntimeException(e);
			}

			if (!allTasks.containsKey(id) || !allTasks.get(id).getVersion().equals(Long.parseLong(taskVersion))) {
				final InputStreamResponseListener listener = new InputStreamResponseListener();
				client.newRequest("/projects/%s/tasks/%s", projectId, taskId).send(listener);
				reqs.put(id, listener);

			} else {
				result.add(allTasks.get(id));
			}
		}
		LOG.debug("Cache: {}, Requests: {}", result.size(), reqs.size());

		while (!reqs.isEmpty()) {

			synchronized (this) {
				try {
					wait(2000);
				} catch (final InterruptedException e) {
					throw new RuntimeException(e);
				}
			}

			final Iterator<Entry<String, InputStreamResponseListener>> it = reqs.entrySet().iterator();

			while (it.hasNext()) {
				final Entry<String, InputStreamResponseListener> req = it.next();
				try {

					final InputStreamResponseListener listener = req.getValue();
					final Task fetchedTask = unmarshal(new StreamSource(listener.getInputStream()));
					final Request request = listener.await(0, TimeUnit.MILLISECONDS).getRequest();
					fetchedTask.setUrl(String.format("%s://%s:%d/%s", request.getScheme(), request.getHost(),
							request.getPort(), request.getPath()));
					result.add(fetchedTask);
					allTasks.put(req.getKey(), fetchedTask);

					it.remove();
				} catch (final TimeoutException e) {
					// do nothing, we will try it again later
					// FIXME not sure if this is the best solution...
				} catch (final InterruptedException e) {
					throw new RuntimeException(e);
				}
			}

			LOG.debug("number of pending requests: {}", reqs.size());
		}

		return result;
	}

	/**
	 * Static init of the singleton instance.
	 *
	 * @param client
	 *            the client
	 */
	public static void init(final ACHttpClient client)
	{
		if (null != singleton) {
			throw new IllegalStateException("Already initialized");
		}

		singleton = new TaskCache(client);
	}

	/**
	 * @return the singleton instance.
	 */
	public static TaskCache get()
	{
		if (null == singleton) {
			throw new IllegalStateException("Not initialized yet");
		}

		return singleton;
	}

	@Override
	protected Class<Task> getTargetClass()
	{
		return Task.class;
	}

	@Override
	protected JAXBContext getMarshallerContext()
	{
		try {
			return JAXBContext.newInstance(Task.class, Milestone.class, User.class);
		} catch (final JAXBException e1) {
			throw new RuntimeException(e1);
		}
	}
}
