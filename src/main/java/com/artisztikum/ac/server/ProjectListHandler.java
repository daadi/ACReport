package com.artisztikum.ac.server;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.artisztikum.ac.ac.Project;
import com.artisztikum.ac.cache.ProjectCache;
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
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(ProjectListHandler.class);

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
	}

	@Override
	public void doHandle(final Matcher m, final String target, final Request baseRequest,
			final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException
	{
		final Iterable<Project> projects = ProjectCache.get().getProjects();

		response.setStatus(200);
		response.setContentType("text/html;charset=utf-8");

		final VelocityContext ctx = new VelocityContext();
		ctx.put("projects", projects);

		final Template template;
		try {
			template = Velocity.getTemplate("/com/artisztikum/ac/ProjectList.vm");
		} catch (final ResourceNotFoundException rnfe) {
			throw new RuntimeException(rnfe);
		} catch (final ParseErrorException pee) {
			throw new RuntimeException(pee);
		} catch (final MethodInvocationException mie) {
			throw new RuntimeException(mie);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}

		template.merge(ctx, response.getWriter());

		baseRequest.setHandled(true);
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
