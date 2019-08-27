package com.elevenpaths.googleindexretriever;

import org.jsoup.select.Elements;

/**
 * The Class Observer.
 */
public class Observer {

	/** The semaphore. */
	protected boolean semaphore = false;

	protected boolean stop = false;

	/** The result. */
	protected String result;

	/** The eleements. */
	protected Elements elements;

	/**
	 * Sets the result.
	 *
	 * @param result the new result
	 */
	public void setResult(final String result) {
		this.result = result;
	}

	/**
	 * Checks if is semaphore.
	 *
	 * @return true, if is semaphore
	 */
	public boolean isSemaphore() {
		return semaphore;
	}

	/**
	 * Sets the semaphore.
	 *
	 * @param semaphore the new semaphore
	 */
	public void setSemaphore(final boolean semaphore) {
		this.semaphore = semaphore;
	}

	/**
	 * Sets the eleements.
	 *
	 * @param eleements the new eleements
	 */
	public void setElements(final Elements elements) {
		this.elements = elements;
	}

	public void stop() {
		stop = true;
	}

}
