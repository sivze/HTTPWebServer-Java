package AbstractTypes;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import AbstractTypes.BaseHandler;
import Handlers.FileHandler;
import Handlers.UriHandler;

public abstract class ServerSettings {
	//Reference - https://github.com/jrudolph/Pooling-web-server
    // This is the most important configuration to do and you would have to find
    // out experimentally which is the setting for the pool.
    // Without keep-alive and when the handling of request is mainly CPU-bound
    // you would use about N threads (with N being the number of CPUs).
    // When processing requests contains wait times (e.g. because of I/O) you
    // would have to increase the number of threads to N*(1+WT/ST) with WT/ST being the ratio
    // of wait time to service time.
    //
    // E.g. see Brian Goetz, Java theory and practice: Thread pools and work queues
    // http://www.ibm.com/developerworks/java/library/j-jtp0730.html
    //
    // With keep-alive in place, configuration becomes harder because you have
    // to figure out both, keep-alive timeouts and the number of threads. With a long too long timeout
    // you might keep open many connections and threads waiting for requests and using system resources.
    //
    // One possibility is to decouple connections and threads by putting open, waiting connections into
    // a list which is monitored for new data. When new data arrives the requests are scheduled again
    // for processing. (not implemented here)
    public final static ExecutorService createExecutor() {
        return Executors.newFixedThreadPool(threadPoolSize);
    }
    /**
     * Default thread pool size for this project.
     */
    public final static int threadPoolSize = 2;
    /**
     * The handler which handles requests. Change the instance type to UriHandler("webapi")
     * if you want to test Message WebService requests using GET and DELETE methods. 
     * And change instance type to FileHandler("www") if you want to send GET request for file.
     */
    public static BaseHandler handler = new FileHandler("www");
    
    /**
     * Port Number
     */
    public final static int port = 9090;
    /**
     * The endpoint to bind to.
     */
    public final static SocketAddress endpoint = new InetSocketAddress(port);
    /**
     * The socket timeout limit between connection accept and expecting the first request.
     */
    public final static int firstReadTimeout = 5000;
    /**
     * The timeout when waiting for headers.
     */
    public final static int headerTimeout = 2000;
    /**
     * The timeout in keep-alive connections when waiting for the next request
     */
    public final static int keepAliveTimeout = 20000;

}
