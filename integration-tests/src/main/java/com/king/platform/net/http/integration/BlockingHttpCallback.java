// Copyright (C) king.com Ltd 2015
// https://github.com/king/king-http-client
// Author: Magnus Gustafsson
// License: Apache 2.0, https://raw.github.com/king/king-http-client/LICENSE-APACHE

package com.king.platform.net.http.integration;


import com.king.platform.net.http.HttpCallback;
import com.king.platform.net.http.HttpResponse;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

public class BlockingHttpCallback implements HttpCallback<String> {
	private final Logger logger = getLogger(getClass());
	private HttpResponse<String> httpResponse;
	private Throwable exception;

	private CountDownLatch countDownLatch = new CountDownLatch(1);

	public BlockingHttpCallback() {

	}


	@Override
	public void onCompleted(HttpResponse<String> httpResponse) {
		this.httpResponse = httpResponse;
		try {
			Thread.sleep(50);
		} catch (InterruptedException ignored) {
		}

		countDownLatch.countDown();
	}

	@Override
	public void onError(Throwable exception) {

		this.exception = exception;

		try {
			Thread.sleep(50);
		} catch (InterruptedException ignored) {
		}

		countDownLatch.countDown();
	}

	public int getStatusCode() {
		return httpResponse.getStatusCode();
	}

	public String getBody() {
		if (exception != null) {
			throw new RuntimeException(exception);
		}
		if (httpResponse == null) {
			return null;
		}

		return httpResponse.getBody();
	}

	public String getHeader(String name) {
		return httpResponse.getHeader(name);
	}

	public Throwable getException() {
		return exception;
	}

	public void waitForCompletion() throws InterruptedException {
		countDownLatch.await(20, TimeUnit.SECONDS);
	}

	public boolean waitForCompletion(long timeout, TimeUnit unit) throws InterruptedException {
		return countDownLatch.await(timeout, unit);
	}
}
