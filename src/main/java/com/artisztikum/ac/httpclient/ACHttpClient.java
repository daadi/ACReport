package com.artisztikum.ac.httpclient;

import java.io.IOException;

import lombok.Getter;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
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
	 * The jetty client.
	 */
	private final HttpClient client = new HttpClient();

	/**
	 * Public constructor. Initializes the jetty client
	 */
	public ACHttpClient(final String apiHost, final int connectionsPerAddress, final int threadSize,
			final boolean trustAll)
	{
		this.apiHost = apiHost;
		synchronized (client) {
			if (!client.isStarted()) {
				try {
					LOG.trace("Starting client");
					client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
					client.setMaxConnectionsPerAddress(connectionsPerAddress);
					client.setThreadPool(new QueuedThreadPool(threadSize));
					client.getSslContextFactory().setTrustAll(trustAll);
					client.start();

				} catch (final Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	/**
	 * Sends a {@link ContentExchange} to the jetty client if there are enough threads available.
	 *
	 * @param ex
	 *            The {@link ContentExchange} to send.
	 * @return The same {@link ContentExchange}.
	 */
	public ContentExchange send(final ContentExchange ex)
	{
		synchronized (this) {
			while (client.getThreadPool().isLowOnThreads()) {
				try {
					LOG.info("Waiting for free threads. Maybe you should increase the limit?");
					wait(1000);
				} catch (final InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
			try {
				client.send(ex);
			} catch (final IOException e) {
				throw new RuntimeException("IO error during http request", e);
			}
		}
		return ex;
	}

	/**
	 * Gets the URL of an API request by path_info as a {@link String#format(String, Object...)} pattern and its args.
	 *
	 * @param pathInfo
	 *            The format string of the {@code path_info} parameter (e.g. {@code /projects/%s/tickets/%s}.
	 * @param args
	 *            The args for the format string.
	 * @return The full URL with {@link #AC_HOST} and token id.
	 */
	private String getURL(final String pathInfo, final Object... args)
	{
		return String.format("%s/api.php?token=%s&path_info=%s", apiHost, apiKey.get(), String.format(pathInfo, args));
	}

	/**
	 * Sends a GET request and wait for the response.
	 *
	 * @param pathInfo
	 *            The format string of the {@code path_info} parameter (e.g. {@code /projects/%s/tickets/%s}.
	 * @param args
	 *            The args for the format string.
	 * @return The resulted {@link ContentExchange} instance with the response already initialized.
	 */
	public ContentExchange sendGetWait(final String pathInfo, final Object... args)
	{
		final ContentExchange ex = sendGet(pathInfo, args);
		try {
			ex.waitForDone();
		} catch (final InterruptedException e) {
			throw new RuntimeException(e);
		}
		return ex;
	}

	/**
	 * Sends a GET request and does not wait for the response.
	 *
	 * @param pathInfo
	 *            The format string of the {@code path_info} parameter (e.g. {@code /projects/%s/tickets/%s}.
	 * @param args
	 *            The args for the format string.
	 * @return The resulted {@link ContentExchange} instance with the response not initialized.
	 */
	public ContentExchange sendGet(final String pathInfo, final Object... args)
	{
		final ContentExchange ex = new ContentExchange();
		ex.setMethod("GET");
		ex.setURL(getURL(pathInfo, args));
		return send(ex);
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
	public Integer getUserId()
	{
		if (null == this.apiKey.get()) {
			throw new IllegalStateException("No api key set!");
		}
		try {
			return Integer.valueOf(this.apiKey.get().split("-")[0]);
		} catch (final NumberFormatException e) {
			throw new RuntimeException("Cannot get User ID from API Key " + this.apiKey.get(), e);
		}
	}
}
