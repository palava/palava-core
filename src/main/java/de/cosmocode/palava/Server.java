/**
 * palava - a java-php-bridge
 * Copyright (C) 2007  CosmoCode GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.cosmocode.palava;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.ServiceLoader;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

import de.cosmocode.palava.core.CoreModule;
import de.cosmocode.palava.core.session.HttpSessionManager;

/**
 * Main thread, accepts connections and inititates the worker.
 * @author Tobias Sarnowski
 */
public class Server extends Thread {
    
    private static final Logger log = Logger.getLogger(Server.class);

    private String version;
    private int listenPort;
    private long uptimeStart;

    private ThreadGroup clients;

    private Boolean shutdown = false;

    private Properties config;
    public Properties alias;
    
    public String directoryName;
//    private final File baseDirectory;

    public JobManager jobs;
    public HttpSessionManager sessions;
    public ComponentManager components;

    private final ImmutableSet<JobInterceptor> jobInterceptors;

    private boolean interactive;
    
    private Injector injector;

    public Server() {
        jobInterceptors = ImmutableSet.copyOf(ServiceLoader.load(JobInterceptor.class));
    }

    public Server(Properties config, Properties alias, boolean interactive) {
        this();
        // inititate config
        this.config = config;
        this.alias = alias;
        this.interactive = interactive;
        

        directoryName = config.getProperty("PALAVA_DIR");
        if (!directoryName.endsWith(File.separator)) {
            directoryName = directoryName + File.separator;
        }
        
//        baseDirectory = new File(palava_dir);

        // read in version
        final File versionfile = new File(directoryName + "/version");
        
        try {
            FileInputStream fstream = new FileInputStream(versionfile);
            BufferedReader in = new BufferedReader(new InputStreamReader(fstream));
            version = in.readLine().trim();
            in.close();
            fstream.close();
        } catch (Exception e) {
            log.fatal("Cannot resolve version: " + e);
            return;
        }
        // print license
        System.out.println("palava version " + getVersion() + ", Copyright (C) 2007, 2008  CosmoCode GmbH");
        System.out.println("palava comes with ABSOLUTELY NO WARRANTY; for details");
        System.out.println("see the LICENSE file.  This is free software, and you are");
        System.out.println("welcome to redistribute it under certain conditions.");
        System.out.println();

        // initialize components
        try {
            String components_conf = config.getProperty("COMPONENTS_XML");
            if (!components_conf.startsWith(File.separator)) {
                components_conf = directoryName + components_conf;
            }
            final Element root = new SAXBuilder().build(components_conf).getRootElement();
            
            final GuiceComponentManager<Object> manager = GuiceComponentManager.create(root, this);
            components = manager;
            components.initialize();
            injector = Guice.createInjector(new CoreModule(), manager);
            
        } catch (Exception e) {
            log.fatal("Cannot initialize components!", e);
            e.printStackTrace();
            System.exit(1);
        }

        // socket options
        this.listenPort = Integer.parseInt(config.getProperty("PALAVA_PORT"));

        // we are important ;-)
        this.setPriority(NORM_PRIORITY + 1);
        
        // server.sessions
        sessions = getInjector().getInstance(HttpSessionManager.class);
        
        // server.jobs
        jobs = new JobManager(this);


    }
    
    /** if the filename is relative, it is prefixed with palava_dir and returned.
     * otherwise it is returned unchanged.
     * @param filename
     * @return 
     */
    public String getFilename( String filename ){
        if ( filename.startsWith( File.separator )) return filename;
        
        return directoryName + filename;
    }


    public void run()
    {
        if ( interactive){
            System.out.println("server running interactive.");
            RequestConsumer consumer = new RequestConsumer();
            
            try {
                consumer.consumeRequest(this, System.in, System.out, new HashMap<String,Object>());
            } catch ( Exception e ){
                log.error("cannot consume request", e);
                
            }
            
            return;
        }
        try {
            // prepare the sockets
            ServerSocket server = new ServerSocket(listenPort);
            log.info("Server main-thread is listening on port " + listenPort);
            
            clients = new ThreadGroup("clients");

            // check every 5 seconds for shutdown
            // TODO make configurable
            server.setSoTimeout(5000);

            // save starttime for uptime checks
            uptimeStart = System.currentTimeMillis();

            // the only system output
            System.out.println("server started at port " + listenPort + ".");

            // our server main-loop
            while (!shutdownInitiated()) {
                Socket client;
                try {
                    client = server.accept();
                } catch (SocketTimeoutException ste) {
                    continue;
                }

                // new client connected
                if (client != null) {
                    final Worker worker = new Worker(client, this);
                    final Thread thread = new Thread(clients, worker);
                    thread.start();
                }

            }

            System.out.println("shutting down server.");
        } catch (IOException ioe) {
            log.fatal("Server main-thread ended!", ioe);
            ioe.printStackTrace();
            shutdown();
        } finally {
            // give the Threads a chance to end
            try {
                log.debug("No more connections; 5 seconds until program exits.");
                Thread.sleep(3000);
                clients.interrupt();
                Thread.sleep(2000);
            }
            catch (Exception ex) {}

            components.shutdown();
            
            // destroy all sessions
            sessions.purge();
        }
    }


    // initiates the shutdown procedure
    public void shutdown()
    {
        log.info("Shutdown initiated.");
        shutdown = true;
    }


    // everyone can ask here to shutdown their functions
    public boolean shutdownInitiated()
    {
        return shutdown;
    }

    
    // some statistic functions
    public String getVersion()
    {
        return version;
    }

    public int getConnectionCount()
    {
        return clients.activeCount();
    }

    public int getListenPort()
    {
        return listenPort;
    }

    public long getUptime() {
        return System.currentTimeMillis() - uptimeStart;
    }

    public String getPalavaDir() {
        return directoryName;
    }
    
    public ImmutableSet<JobInterceptor> getJobInterceptors() {
        return jobInterceptors;
    }

    public Injector getInjector() {
        return injector;
    }
    
}
