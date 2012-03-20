package com.artisztikum.ac.server;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.artisztikum.ac.httpclient.ACHttpClient;

/**
 * Shows a list of available projects.
 *
 * @author Adam DAJKA (dajka@artisztikum.hu)
 *
 */
public final class ProjectListHandler extends AbstractUrlPatternHandler
{
	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ProjectListHandler.class);

	/**
	 * The client.
	 */
	private final ACHttpClient client;

	/**
	 * The server instance.
	 */
	private Server server;

	/**
	 * @param client
	 *            the client.
	 */
	public ProjectListHandler(final ACHttpClient client)
	{
		super(Pattern.compile("^/projects[/]*$"));
		this.client = client;
	}

	@Override
	public void doHandle(final Matcher m, final String target, final Request baseRequest,
			final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException
	{
		LOG.debug("Processing url: {}", target);

		final ContentExchange ex = client.sendGetWait("/projects/");

		response.setContentType("text/xml;charset=utf-8");

		response.setStatus(200);
		baseRequest.setHandled(true);

		response.getWriter().println(ex.getResponseContent());
	}

	@Override
	public void setServer(final Server server)
	{
		this.server = server;
	}

	@Override
	public Server getServer()
	{
		return this.server;
	}

	@Override
	public void destroy()
	{
		// do nothing
	}
}
