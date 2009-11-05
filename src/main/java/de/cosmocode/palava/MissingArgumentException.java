package de.cosmocode.palava;

import java.util.List;

public class MissingArgumentException extends RuntimeException {

	private static final long serialVersionUID = -4016565094430756701L;
	
	private static String concatStringArray(String[] array) {
		String returnString = "";
		for (String string : array) {
			returnString += string + ", ";
		}
		returnString = returnString.substring(0, returnString.length()-2);
		return returnString;
	}

	/**
	 * Default MissingArgumentException. Just there because of convenience. Please use the other jobs.
	 */
	public MissingArgumentException() {
		super();
	}

	public MissingArgumentException(String arg0) {
		super(arg0);
	}
	
	public MissingArgumentException(Object msg) {
		super(msg == null ? "" : msg.toString());
	}

	public MissingArgumentException(String message, Throwable cause) {
        super(message, cause);
    }


    public MissingArgumentException(Throwable cause) {
        super(cause);
    }


    /**
	 * A job was missing an Argument.
	 * @param invokedJob : The job that is missing the argument
	 * @param missingArgument : the argument's name (e.g. "id")
	 */
	public MissingArgumentException(Job invokedJob, String missingArgument) {
		this(invokedJob, false, missingArgument);
	}
	
	/**
	 * A job was missing an argument. 
	 * showFullName determines whether to show 
	 * full canonical name of job in error message or not.
	 * @param invokedJob : The job that is missing the argument
	 * @param showFullName : whether to show full canonical name (e.g. de.cosmocode.testJob) in error message or simple name (e.g. testJob)
	 * @param missingArgument : the argument's name (e.g. "id")
	 */
	public MissingArgumentException(Job invokedJob, boolean showFullName, String missingArgument) {
		super("The Job `" 
				+ ((showFullName)
						? invokedJob.getClass().getCanonicalName() 
						: invokedJob.getClass().getSimpleName()) 
				+ "` is missing the argument `" + missingArgument + "`");
	}
	
	/**
	 * A job was missing an argument. 
	 * @param invokedJob : The job that is missing the argument
	 * @param missingArgument : the argument's name (e.g. "id")
	 * @param type : the argument's type (e.g. "int")
	 */
	public MissingArgumentException(Job invokedJob, String missingArgument, String type) {
		this(invokedJob, false, missingArgument, type);
	}
	
	/**
	 * A job was missing an argument.
	 * showFullName determines whether to show 
	 * full canonical name of job in error message or not.
	 * @param invokedJob : The job that is missing the argument
	 * @param showFullName : whether to show full canonical name (e.g. de.cosmocode.testJob) in error message or simple name (e.g. testJob)
	 * @param missingArgument : the argument's name (e.g. "id")
	 * @param type : the argument's type (e.g. "int")
	 */
	public MissingArgumentException(Job invokedJob, boolean showFullName, String missingArgument, String type) {
		super("The Job `" 
				+ ((showFullName)
						? invokedJob.getClass().getCanonicalName() 
						: invokedJob.getClass().getSimpleName())
				+ "` is missing the argument `" + missingArgument 
				+ "` of type " + type);
	}
	
	/**
	 * A job was missing several arguments. 
	 * @param invokedJob : The job that is missing the argument
	 * @param missingArguments : an array with all missing arguments
	 */
	public MissingArgumentException(Job invokedJob, String[] missingArguments) {
		super("The Job `" + invokedJob.getClass().getSimpleName()
				+ "` is missing the following arguments : " + MissingArgumentException.concatStringArray(missingArguments));
	}
	
	/**
	 * A job was missing several arguments.
	 * @param invokedJob : The job that is missing the argument
	 * @param missingArguments : a list with all missing arguments
	 */
 	public MissingArgumentException(Job invokedJob, List<String> missingArguments) {
 		super("The Job `" + invokedJob.getClass().getSimpleName()
				+ "` is missing the following arguments : " + missingArguments);
 	}
	
}