package com.elevenpaths.googleindexretriever.process;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.jsoup.nodes.Element;

import com.elevenpaths.googleindexretriever.Control;
import com.elevenpaths.googleindexretriever.GoogleSearch;
import com.elevenpaths.googleindexretriever.exceptions.EmptyQueryException;
import javafx.animation.AnimationTimer;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import java.util.concurrent.TimeUnit;

// TODO: Auto-generated Javadoc
/**
 * The Class MakeShotSpamProcess.
 */
@SuppressWarnings("restriction")
public class MakeShotSpamProcess extends Observer implements Runnable {

	/** The thread name. */
	public static String THREAD_NAME = "spamShotProcces";

	/** The thread. */
	private Thread thread;

	/** The control. */
	private final Control control;

	/** The query. */
	private final String query;

	/** The gs. */
	private final GoogleSearch gs;

	/** The that. */
	private final MakeShotSpamProcess that;

	/** The message queue. */
	private final BlockingQueue<String> messageQueue;

	/** The start time. */
	private final long startTime;

	/**
	 * Instantiates a new make shot spam process.
	 *
	 * @param control the control
	 * @param gs the gs
	 * @param query the query
	 */
	public MakeShotSpamProcess(final Control control, final GoogleSearch gs, final String query) {
		this.control = control;
		this.gs = gs;
		this.query = query;

		startTime = System.nanoTime();

		that = this;

		messageQueue = new ArrayBlockingQueue<>(1);

		final LongProperty lastUpdate = new SimpleLongProperty();

		final long minUpdateInterval = 0; // nanoseconds. Set to higher number to slow output.

		final AnimationTimer timer = new AnimationTimer() {

			@Override
			public void handle(final long now) {
				if (now - lastUpdate.get() > minUpdateInterval) {
					final String message = messageQueue.poll();
					if (message != null) {
						try {
							gs.setQuery(message, that);
						} catch (final UnsupportedEncodingException e) {
							control.stop();
							e.printStackTrace();

						} catch (final EmptyQueryException e) {
							control.stop();
							e.printStackTrace();
						}
					}
					lastUpdate.set(now);
				}
			}

		};

		timer.start();

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		try {

			semaphore = false;

			final String message = query;
			messageQueue.put(message);

			while (semaphore == false) {
				Thread.sleep(1000);
			}

			// Successful request
			final String time = control
					.calcHMS((int) TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS));
			control.setElepased(time);
			for (final Element e : elements) {
				control.addList(time, "", e.text());
			}

		} catch (final InterruptedException e) {
			e.printStackTrace();
		}

		control.stop();

	}

	/**
	 * Sets the result query.
	 *
	 * @param result the new result query
	 */
	public void setResultQuery(final String result) {
		this.result = result;
	}

	/**
	 * Start.
	 */
	public void start() {
		// System.out.println("Starting " + threadName );
		if (thread == null) {
			thread = new Thread(this, THREAD_NAME);
			thread.start();
		}
	}

	/**
	 * Gets the thread.
	 *
	 * @return the thread
	 */
	public Thread getThread() {
		return thread;
	}

}