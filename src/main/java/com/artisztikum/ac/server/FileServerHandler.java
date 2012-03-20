package com.artisztikum.ac.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Serves all content under {@code /static/}.
 *
 * @author Adam DAJKA (dajka@artisztikum.hu)
 *
 */
public final class FileServerHandler extends AbstractLifeCycle implements Handler
{

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(FileServerHandler.class);

	/**
	 * Reference to the server.
	 */
	private Server server;

	/**
	 * The Resource handler for file handling.
	 */
	private ResourceHandler fileServer;

	/**
	 * Constructor.
	 */
	public FileServerHandler()
	{
		fileServer = new ResourceHandler();
		fileServer.setDirectoriesListed(true);
		fileServer.setResourceBase(".");
		LOG.info("serving {}", fileServer.getBaseResource());
	}

	@Override
	public void handle(final String target, final Request baseRequest, final HttpServletRequest request,
			final HttpServletResponse response) throws IOException, ServletException
	{
		if (target.startsWith("/static/")) {
			fileServer.handle(target, baseRequest, request, response);
		}
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
		// empty
	}

}
