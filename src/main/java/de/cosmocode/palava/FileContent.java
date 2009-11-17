package de.cosmocode.palava;

import java.io.File;
import java.io.FileInputStream;


/**
 * send a local file to the browser
 * @author Tobias Sarnowski
 */
public class FileContent extends StreamContent
{
	public FileContent(File file) throws Exception
	{
		super(new FileInputStream(file), file.length(), MimeTypes.SINGLETON.getMimeTypeByName(file.toString()));
	}
}
