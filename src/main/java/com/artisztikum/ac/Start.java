package com.artisztikum.ac;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.velocity.app.Velocity;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.artisztikum.ac.cache.CompanyCache;
import com.artisztikum.ac.cache.ProjectCache;
import com.artisztikum.ac.cache.TaskCache;
import com.artisztikum.ac.httpclient.ACHttpClient;
import com.artisztikum.ac.server.ApiKeyCheckerHandler;
import com.artisztikum.ac.server.FileServerHandler;
import com.artisztikum.ac.server.ProjectHomeHandler;
import com.artisztikum.ac.server.ProjectListHandler;

/**
 * Initializes and starts the service.
 * 
 * @author Adam DAJKA (dajka@artisztikum.hu)
 * 
 */
public final class Start
{
	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(Start.class);

	/**
	 * Writes an error message to STDERR, then quits.
	 * 
	 * @param msg
	 *            The message.
	 */
	private static void error(final String msg)
	{
		System.err.println(msg);
		System.exit(1);
	}

	/**
	 * @param args
	 *            Not used
	 */
	public static void main(final String[] args)
	{
		LOG.info("Starting service");

		final Properties velocityConfig = new Properties();
		try {
			velocityConfig.load(Start.class.getResourceAsStream("/velocity.properties"));
		} catch (final IOException e1) {
			LOG.error("Cannot read velocity config", e1);
			error("Internal error. Check the logs!");
		}
		Velocity.init(velocityConfig);

		FileReader r;
		try {
			r = new FileReader(new File("config.properties"));
		} catch (final FileNotFoundException e1) {
			error("Missing config file (./config.properties) See config.sample.properties!");
			throw new RuntimeException(e1);
		}

		//
		// Read configuration
		//
		final Properties config = new Properties();
		try {
			config.load(r);
		} catch (final IOException e1) {
			error("Error reading config file (./config.properties)");
			throw new RuntimeException(e1);
		}

		/* *****************************************************
		 * 
		 * HTTP Client
		 */

		// httpclient.host
		// Active Collab API host
		String apiHost = config.getProperty("httpclient.apihost");
		if (null == apiHost || apiHost.isEmpty()) {
			error("Missing parameter: 'apihost'");
		}
		if (apiHost.endsWith("/")) {
			apiHost = apiHost.substring(0, apiHost.length() - 1);
		}

		// create client
		final ACHttpClient client = new ACHttpClient(apiHost, getIntValue(config, "httpclient.maxConnections", "20"),
				getIntValue(config, "httpclient.threadPoolSize", "100"), true);

		/* *****************************************************
		 * 
		 * Task cache
		 */

		// init caches
		TaskCache.init(client);
		CompanyCache.init(client, getLongValue(config, "cache.company.timeout", "1800000"),
				getLongValue(config, "cache.company.size", "5000"));
		ProjectCache.init(client);

		// creating handlers
		final HandlerList hc = new HandlerList();

		// cookie handling
		hc.addHandler(new FileServerHandler());

		// cookie handling
		hc.addHandler(new ApiKeyCheckerHandler(client));

		// handler for project list
		hc.addHandler(new ProjectListHandler(client));

		// handler for project home
		hc.addHandler(new ProjectHomeHandler());

		// creating the server
		final Server server = new Server(getIntValue(config, "server.port", "8080"));
		server.setHandler(hc);

		try {
			server.start();
		} catch (final Exception e) {
			LOG.error("Cannot start web server", e);
			System.exit(1);
		}
		try {
			server.join();
		} catch (final InterruptedException e) {
			LOG.error("Cannot join web server", e);
			System.exit(2);
		}

		LOG.info("Service stopped");
	}

	/**
	 * Hidden.
	 */
	private Start()
	{
	}

	/**
	 * Gets an {@code int} value from a {@link Properties}.
	 * 
	 * @param cfg
	 *            The {@link Properties}.
	 * @param key
	 *            The config key
	 * @param defaultValue
	 *            The default value {@link Properties#getProperty(String)} returns {@code null} for {@code key}.
	 * @return The value as {@code long} for {@code key} in {@code cfg} or if it is {@code null}, then
	 *         {@code defaultValue}.
	 */
	public static int getIntValue(final Properties cfg, final String key, final String defaultValue)
	{
		try {
			return Integer.parseInt(cfg.getProperty(key, defaultValue));
		} catch (final NumberFormatException e) {
			error("Invalid value for parameter 'httpclient.threadPoolSize'" + e.getMessage());
			throw new RuntimeException("Never thrown.", e);
		}
	}

	/**
	 * Gets an {@code long} value from a {@link Properties}.
	 * 
	 * @param cfg
	 *            The {@link Properties}.
	 * @param key
	 *            The config key
	 * @param defaultValue
	 *            The default value {@link Properties#getProperty(String)} returns {@code null} for {@code key}.
	 * @return The value as {@code long} for {@code key} in {@code cfg} or if it is {@code null}, then
	 *         {@code defaultValue}.
	 */
	public static long getLongValue(final Properties cfg, final String key, final String defaultValue)
	{
		try {
			return Long.parseLong(cfg.getProperty(key, defaultValue));
		} catch (final NumberFormatException e) {
			error("Invalid value for parameter 'httpclient.threadPoolSize'" + e.getMessage());
			throw new RuntimeException("Never thrown.", e);
		}
	}

}
