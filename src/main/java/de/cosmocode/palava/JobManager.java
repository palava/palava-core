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
