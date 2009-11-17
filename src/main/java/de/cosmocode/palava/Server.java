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

import com.google.common.collect.ImmutableSet;


/**
 * Main thread, accepts connections and inititates the worker.
 * @author Tobias Sarnowski
 */
public class Server extends Thread {
    
    private static final Logger log = Logger.getLogger(Server.class);

    private String version;
    private int listenPort;
    private long uptime_start;

    private ThreadGroup clients;

    private Boolean shutdown = false;

    public Properties config;
    public Properties alias;
    public String palava_dir = null;

    public JobManager jobs;
    public SessionManager sessions;
    public ComponentManager components;

    private final ImmutableSet<JobInterceptor> jobInterceptors;

    private boolean interactive;

    public Server() {
        jobInterceptors = ImmutableSet.copyOf(ServiceLoader.load(JobInterceptor.class));
    }

    public Server(Properties config, Properties alias, boolean interactive) {
        this();
        // inititate config
        this.config = config;
        this.alias = alias;
        this.interactive = interactive;
        

        palava_dir = config.getProperty("PALAVA_DIR");
        if (!palava_dir.endsWith(File.separator)) {
            palava_dir = palava_dir + File.separator;
        }

        // read in version
        File versionfile = new File(palava_dir + "/version");
        try
        {
            FileInputStream fstream = new FileInputStream(versionfile);
            BufferedReader in = new BufferedReader(new InputStreamReader(fstream));
            version = in.readLine().trim();
            in.close();
            fstream.close();
        }
        catch (Exception e)
        {
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
                components_conf = palava_dir + components_conf;
            }
            components = new DefaultComponentManager(new File(components_conf), this);
            components.initialize();
        } catch (Exception e) {
            log.fatal("Cannot initialize components!", e);
            e.printStackTrace();
            System.exit(1);
        }

        // socket options
        this.listenPort = Integer.parseInt((String)config.getProperty("PALAVA_PORT"));

        // we are important ;-)
        this.setPriority(NORM_PRIORITY + 1);
        
        // server.sessions
        sessions = new SessionManager();
        
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
        
        return palava_dir + filename;
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
            server.setSoTimeout(5000);


            // save starttime for uptime checks
            Date now = new Date();
            uptime_start = now.getTime();

            // the only system output
            System.out.println("server started at port " + listenPort + ".");

            // our server main-loop
            while(!shutdown_initiated()) {
                Socket client;
                try {
                    client = server.accept();
                } catch (SocketTimeoutException ste) {
                    continue;
                }

                // new client connected
                if (client != null) {
                    Worker worker = new Worker(client, this);

                    Thread wt = new Thread(clients, worker);
                    wt.start();
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

            // destroy all sessions
            sessions.purge(-1);
        }
    }


    // initiates the shutdown procedure
    public void shutdown()
    {
        log.info("Shutdown initiated.");
        shutdown = true;
    }


    // everyone can ask here to shutdown their functions
    public Boolean shutdown_initiated()
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

    public long getUptime()
    {
        Date now = new Date();
        return now.getTime() - uptime_start;
    }



    public static long[] timer = new long[255];  // max 255 possible timers

    public static void startBench(int index) {
        timer[index] = System.currentTimeMillis();
    }

    public static long getBench(int index) {
        return System.currentTimeMillis() - timer[index];
    }


    public String getPalavaDir() {
        return palava_dir;
    }
    
    public ImmutableSet<JobInterceptor> getJobInterceptors() {
        return jobInterceptors;
    }

}
