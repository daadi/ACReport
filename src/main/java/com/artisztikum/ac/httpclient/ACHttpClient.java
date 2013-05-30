package com.artisztikum.ac.httpclient;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import lombok.Getter;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Communicates with the Active Collab API.
 *
 * @author Adam DAJKA (dajka@artisztikum.hu)
 *
 */
public final class ACHttpClient
{
	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ACHttpClient.class);

	/**
	 * The URL of the API. TODO Set from config.
	 */
	@Getter
	private final String apiHost;

	/**
	 * API key for a processing {@link Thread}.
	 */
	private final ThreadLocal<String> apiKey = new ThreadLocal<String>();

	/**
	 * The threadpool to execute all request.
	 */
	private final QueuedThreadPool executor;

	/**
	 * The jetty client.
	 */
	private final HttpClient client;

	/**
	 * Public constructor. Initializes the jetty client
	 *
	 * @param apiHost
	 *            The host of the AC API (with protocol)
	 * @param connectionsPerAddress
	 *            Number of maximum connections to the AC API
	 * @param threadSize
	 *            Number of client threads
	 * @param trustAll
	 *            Flag for accepting certificates
	 */
	public ACHttpClient(final String apiHost, final int connectionsPerAddress, final int threadSize,
			final boolean trustAll)
	{
		this.apiHost = apiHost;
		try {
			this.client = new HttpClient();

			LOG.trace("Starting client");

			// TODO what is this in jetty 9? is it needed?
			// client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
			client.setMaxConnectionsPerDestination(connectionsPerAddress);

			executor = new QueuedThreadPool(threadSize);
			client.setExecutor(executor);

			client.getSslContextFactory().setTrustAll(trustAll);

			client.start();

		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Sends a {@link Request} to the jetty client if there are enough threads available.
	 *
	 * @param req
	 *            The {@link Request} to send.
	 * @return The same {@link Request}.
	 */
	public Request send(final Request req)
	{
		synchronized (this) {
			while (executor.isLowOnThreads()) {
				try {
					LOG.info("Waiting for free threads. Maybe you should increase the limit?");
					wait(1000);
				} catch (final InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
			try {
				req.send();
			} catch (final InterruptedException e) {
				throw new RuntimeException("Request interrupted: " + req.getURI(), e);
			} catch (final TimeoutException e) {
				throw new RuntimeException("Request timeout: " + req.getURI(), e);
			} catch (final ExecutionException e) {
				throw new RuntimeException("Error executing request: " + req.getURI(), e);
			}
		}
		return req;
	}

	/**
	 * Sends a GET request and wait for the response.
	 *
	 * @param pathInfo
	 *            The format string of the {@code path_info} parameter (e.g. {@code /projects/%s/tasks/%s}.
	 * @param args
	 *            The args for the format string.
	 * @return The {@link ContentResponse} instance
	 */
	public ContentResponse sendGetWait(final String pathInfo, final Object... args)
	{
		try {
			return client.GET(getRequestURL(pathInfo, args));
		} catch (final InterruptedException e) {
			throw new RuntimeException(e);
		} catch (final ExecutionException e) {
			throw new RuntimeException(e);
		} catch (final TimeoutException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets a full request url with {@code args} subst'ed into {@code pathInfo}.
	 *
	 * @param pathInfo
	 *            The pathInfo param passed to api.php. e.g. {@code /projects/%d/}.
	 * @param args
	 *            The args to be used in {@code pathInfo}
	 * @return A request url with proper hostname, token and pathInfo parameter
	 */
	public String getRequestURL(final String pathInfo, final Object... args)
	{
		final String url = String.format("%s/api.php?auth_api_token=%s&path_info=%s", apiHost, apiKey.get(),
				String.format(pathInfo, args));
		LOG.trace("{}/api.php?path_info={}&auth_api_token=***", apiHost, String.format(pathInfo, args));
		return url;
	}

	/**
	 * Sends a GET request and does not wait for the response.
	 *
	 * @param pathInfo
	 *            The format string of the {@code path_info} parameter (e.g. {@code /projects/%s/tasks/%s}.
	 * @param args
	 *            The args for the format string.
	 * @return The resulted {@link Request} instance with the response not initialized.
	 */
	public Request newRequest(final String pathInfo, final Object... args)
	{
		return client.newRequest(getRequestURL(pathInfo, args));
	}

	/**
	 * Set the api key for the current thread.
	 *
	 * @param apiKey
	 *            The apiKey to set.
	 */
	public void setApiKey(final String apiKey)
	{
		if (null != this.apiKey.get()) {
			throw new IllegalStateException("Api key already set by this thread!");
		}

		this.apiKey.set(apiKey);
	}

	/**
	 * @return The api key for the current thread.
	 */
	public String getApiKey()
	{
		return this.apiKey.get();
	}

	/**
	 * Removes the api key for the current thread.
	 */
	public void resetApiKey()
	{
		this.apiKey.remove();
	}

	/**
	 * @return The user id of the current user (from {@link #apiKey}).
	 */
	public Long getUserId()
	{
		if (null == this.apiKey.get()) {
			throw new IllegalStateException("No api key set!");
		}
		try {
			return Long.valueOf(this.apiKey.get().split("-")[0]);
		} catch (final NumberFormatException e) {
			throw new RuntimeException("Cannot get User ID from API Key " + this.apiKey.get(), e);
		}
	}
}
