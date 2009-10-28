package de.cosmocode.palava.jobs.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import de.cosmocode.palava.ConnectionLostException;
import de.cosmocode.palava.DataRequest;
import de.cosmocode.palava.FileContent;
import de.cosmocode.palava.Job;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.Session;
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
	public void process(Request request, Response response, Session session, Server server, Map<String,Object> caddy)
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
