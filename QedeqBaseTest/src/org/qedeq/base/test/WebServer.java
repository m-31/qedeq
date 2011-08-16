package org.qedeq.base.test;

/**
 * This web server code was adapted from sun's simple web server tutorial: 
 *   http://java.sun.com/developer/technicalArticles/Networking/Webserver/
 *
 */

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

public class WebServer {

    /** The web server's virtual root. */
    private final File root;

    /** Timeout on client connections. */
    private final int timeout;

    /** We work on this port. */
    private final int port;

    /** Debug output? */
    private boolean debug = true;

    /** Thread the server works in. */
    private Thread thread;

    private ServerSocket ss;

    public static void main(String[] a) throws Exception {
        final WebServer server = new WebServer();
        server.start();
//        IoUtility.sleep(50000);
//        server.stop();
    }

    /**
     * Default constructor.
     *
     * @throws Exception
     */
    public WebServer() {
        this.port = 3081;
        this.root = new File(".");
        this.debug = true;
        this.timeout = 5000;
    }

    /**
     * Construct a web server.
     *
     * @param   port    Port we should listen.
     * @param   root    Root point of served file system.
     * @param   timeout Client timeout.
     * @param   debug   Output debug messages?
     */
    public WebServer(final int port, final File root, final int timeout, final boolean debug) {
        this.port = port;
        this.root = root;
        this.timeout = timeout;
        this.debug = debug;
    }

    public synchronized void start() throws IOException {
        ss = new ServerSocket(port);
        WebServerWorker ws = new WebServerWorker(timeout, root, ss, debug);
        thread = (new Thread(ws, "additional worker"));
//            thread.setDaemon(true);
        thread.start();
    }

    public synchronized void stop() {
        thread.interrupt();
        thread = null;
        if (ss != null) {
            try {
                ss.close();
            } catch (Exception e) {
                // ignore
            }
        }
    }

}

