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

package de.cosmocode.palava.jobs.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import de.cosmocode.palava.ConnectionLostException;
import de.cosmocode.palava.Job;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.core.protocol.DataRequest;
import de.cosmocode.palava.core.protocol.FileContent;
import de.cosmocode.palava.core.protocol.Call;
import de.cosmocode.palava.core.protocol.Response;
import de.cosmocode.palava.core.session.HttpSession;
/**
 * DummyJob -- returns content of a file
 * 
 * @author huettemann
 *
 */
public class FileJob implements Job {
	@Override
	/**
	 * process
	 * @param request DataRequest
	 * 
	 * looks for an entry "file" in the requests args and returns the FileContent of it
	 * if filename is relative, the filename is expanded with palava dir
	 * if filename is not specified, the classname of the (probably derived) class is used.
	 */
	public void process(Call request, Response response, HttpSession session, Server server, Map<String,Object> caddy)
			throws ConnectionLostException, Exception {
		
		String dir = server.getPalavaDir();
		
		DataRequest req = (DataRequest) request;
		
		Map args = req.getArgs();
		
		String filename = (String) args.get( "file");
		
		if ( filename == null  )
			filename = getClass().getName()+".txt";
		
		if ( filename.startsWith("/"))
			filename = server.getPalavaDir() + File.separator + filename;
		
		File file = new File(filename);
		
		if ( ! file.exists()){
			throw new FileNotFoundException(filename);
		}
		
		response.setContent( new FileContent(file) );
		
	}

}
