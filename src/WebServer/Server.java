package WebServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import AbstractTypes.ServerSettings;
import Utilities.Logger;

/**
 * Reference - https://github.com/jrudolph/Pooling-web-server
 * A simple pooling web server. It waits on the main thread for new connections
 * and then schedules them for processing with one of the threads from the pool.
 *
 * You can define the behaviour by setting executor and handler. Field
 * `executor` specifies the pooling strategy to use. The handler is called in
 * its own thread to handle an incoming connection.
 * 
 * Visit "ServerSettings.java in AbstractTypes to change endpoint, thread pool
 * size and handler type.
 */

public class Server {
	private final ExecutorService executor = ServerSettings.createExecutor();
	private ServerSocket theServer;
	/**
	 * Entry point for all request, socket is kept open until program stops.
	 * callable executor service makes sure executor comes back and close client
	 * every time and when there is an exception in the flow which avoids server 
	 * being unresponsive for long time.
	 * 
	 * @throws IOException
	 */
	public void run() throws IOException {
		theServer = new ServerSocket();
		try {
			theServer.bind(ServerSettings.endpoint);
		} catch (IOException e) {
			System.err.println("Port already in use!");
			return;
		}

		System.out.println("Server started with " + ServerSettings.threadPoolSize + " thread pools"
				+ "\nwaiting for connections at port " + ServerSettings.endpoint);

		try {
			while (true) {
				final Socket client = theServer.accept();
				Logger.log("New connection: %s", client);
				executor.submit(new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						try {
							ServerSettings.handler.handleConnection(client);
						} catch (IOException exception) {
							System.err.println("Error when handling request: " + exception.getMessage());
							exception.printStackTrace(System.err);
						} finally {
							if (!client.isClosed())
								client.close();
						}
						return null;
					}
				});
			}
		} finally {
			close();
		}
	}

	public void close() {
		try {
			theServer.close();
			executor.shutdown();
		} catch (IOException e) {
			System.err.println("Error when shutdown: " + e.getMessage());
		}
	}
	
	public static void main(String[] args) throws IOException {
		new Server().run();
	}
}
