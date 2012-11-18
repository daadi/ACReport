package com.artisztikum.ac.server;

import java.io.IOException;
import java.util.List;
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

import com.artisztikum.ac.VelocityUtil;
import com.artisztikum.ac.ac.Project;
import com.artisztikum.ac.ac.Task;
import com.artisztikum.ac.cache.ProjectCache;
import com.artisztikum.ac.cache.TaskCache;

/**
 * Main handler for the task list.
 * 
 * @author Adam DAJKA (dajka@artisztikum.hu)
 * 
 */
public final class ProjectHomeHandler extends AbstractUrlPatternHandler
{
	/**
	 * Logger.
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(ProjectHomeHandler.class);

	/**
	 * The server instance.
	 */
	private Server server;

	/**
	 * Default.
	 */
	public ProjectHomeHandler()
	{
		super(Pattern.compile("^/projects/([\\d]+)[/]*$"));
	}

	@Override
	protected void doHandle(final Matcher m, final String target, final Request baseRequest,
			final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException
	{
		final Project project = ProjectCache.get().getProject(Long.valueOf(m.group(1)));
		final List<Task> tasks = TaskCache.get().getTasks(m.group(1));

		response.setStatus(200);
		response.setContentType("text/html;charset=utf-8");

		final VelocityContext ctx = new VelocityContext();
		ctx.put("tasks", tasks);
		ctx.put("util", new VelocityUtil());
		ctx.put("project", project);
		ctx.put("projects", ProjectCache.get().getProjects());

		final Template template;
		try {
			template = Velocity.getTemplate("/com/artisztikum/ac/ProjectHome.vm");
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
