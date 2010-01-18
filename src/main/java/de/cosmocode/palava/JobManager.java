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

/**
 * manages the job classes.
 * 
 * @author Tobias Sarnowski
 */
public class JobManager {

    private final Server server;

    public JobManager(Server server) {
        this.server = server;
    }

    /**
     * Parses an aliased job name and creates the corresponding job.
     * 
     * @param aliased
     * @return
     * @throws Exception
     */
    public Job getJob(String aliased) throws Exception {
        final String name;
        if (aliased.startsWith("@")) {
            final int pos = aliased.indexOf(".");
            final String aliasname = aliased.substring(0, pos);
            final String fullpath = server.alias.getProperty(aliasname);
            name = fullpath + aliased.substring(pos);
        } else {
            name = aliased;
        }
        final Class<? extends Job> jobClass = Class.forName(name).asSubclass(Job.class);
        return jobClass.newInstance();
    }

}
