package com.elevenpaths.googleindexretriever;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.elevenpaths.googleindexretriever.exceptions.EmptyQueryException;
import javafx.animation.AnimationTimer;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * The Class MakeShotProcess.
 */
public class MakeShotProcess extends Observer implements Runnable {

	/** The thread. */
	private Thread thread;

	/** The control. */
	private final Control control;

	/** The query. */
	private final String query;

	/** The gs. */
	private final GoogleSearch gs;

	/** The that. */
	private final MakeShotProcess that;

	/** The message queue. */
	private final BlockingQueue<String> messageQueue;

	/** The start time. */
	private final long startTime;

	/**
	 * Instantiates a new make shot process.
	 *
	 * @param control the control
	 * @param gs the gs
	 * @param query the query
	 */
	public MakeShotProcess(final Control control, final GoogleSearch gs, final String query) {
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

							final Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("Information Dialog");
							alert.setHeaderText(null);
							alert.setContentText("Unsupported Encoding Exception");
							alert.showAndWait();

							e.printStackTrace();

							control.stop();

						} catch (final EmptyQueryException e) {
							// TODO Auto-generated catch block
							final Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("Information Dialog");
							alert.setHeaderText(null);
							alert.setContentText("Empty Query");
							alert.showAndWait();

							control.stop();
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

			final String time = control
					.calcHMS((int) TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS));
			control.setElepased(time);

			control.addList(time, "", result);// found word search
		} catch (final Exception e) {
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
			thread = new Thread(this, "makeShotProcces");
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