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
 * manages the job classes
 * FIXME: implement the dynamic loading
 * @author Tobias Sarnowski
 */
public class JobManager
{

	private Server server;

	public JobManager(Server server)
	{
		this.server = server;
	}


	public Job getJob(String jobname) throws Exception
	{
		if (jobname.startsWith("@")) {
			int pos = jobname.indexOf(".");
			String aliasname = jobname.substring(0, pos);
			String fullpath = server.alias.getProperty(aliasname);
			jobname = fullpath + jobname.substring(pos);
		}
        return (Job)Class.forName(jobname).newInstance();
	}

}
