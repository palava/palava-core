package de.cosmocode.palava;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * the standalone palava client's main method
 * @author Tobias Sarnowski
 */
public class ClientBin
{
	public static void printUsage()
	{
		System.err.println("Usage:  <configfile> [-h <host>] [-p <port>] <option>");
		System.err.println();
		System.err.println("options:");
		System.err.println("   -e <code>   - execute the code");
		System.err.println("   -f <file>   - execute the file");
	}

	public static void main(String[] args)
	{
		try
		{
			int argcnt = 0;

			String host = "localhost";
			int port = 2007;

			// load config file
			String configfile = args[argcnt++];
			Properties config = new Properties();
			try
			{
				config.load(new FileInputStream(configfile));
			}
			catch (IOException ioe)
			{
				System.err.println("Cannot load configuration file! (" + configfile + ")");
				ioe.printStackTrace();
				printUsage();
				System.exit(1);
			}

			// parse the possible host
			if (args[argcnt].equals("-h"))
			{
				host = args[argcnt + 1];
				argcnt = argcnt + 2;
			}

			// parse the port
			if (args[argcnt].equals("-p"))
			{
				port = Integer.parseInt(args[argcnt + 1]);
				argcnt = argcnt + 2;
			}
			else
			{
				port = Integer.parseInt((String)config.get("PALAVA_PORT"));
			}

			// check options
			String jscode = "";
			if (args[argcnt].equals("-e"))
			{
				// read the rest
				jscode = implode(args, argcnt + 1);
			}
			else if (args[argcnt].equals("-f"))
			{
				// read the file
				File jsfile = new File(implode(args, argcnt + 1));
				if (!jsfile.exists())
				{
					System.err.println("Javascript file not found! (" + jsfile + ")");
					printUsage();
					System.exit(1);
				}
				InputStream in = new BufferedInputStream(new FileInputStream(jsfile));
				int data = 0;
				while (data >= 0)
				{
					data = in.read();
					if (data > 0)
					{
						jscode = jscode + (char)data;
					}
				}
			}
			else
			{
				System.err.println("No valid option found. Use -e or -f!");
				printUsage();
				System.exit(1);
			}
	
			// connect and execute the code
			Client client = new Client(host, port);

			String result = client.sendRequest("text", "@palava.system.console", jscode);
			result = str_replace("%%RED+%%", "", result);
			result = str_replace("%%+RED%%", "", result);
			result = str_replace("%%GREEN+%%", "", result);
			result = str_replace("%%+GREEN%%", "", result);
			result = str_replace("%%GREY+%%", "", result);
			result = str_replace("%%+GREY%%", "", result);
			result = str_replace("&lt;", "<", result);
			result = str_replace("&gt;", ">", result);
			System.out.println(result);

			client.close();
			System.exit(0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			printUsage();
			System.exit(1);
		}
	}


	private static String implode(String[] args, int start)
	{
		String result = "";
		int n = start;
		while (n < args.length)
		{
			result = result + " " + args[n];
			n++;
		}
		return result.trim();
	}

	private static String str_replace(String need, String replace, String text)
	{
		int index = text.indexOf(need);
		while (index >= 0)
		{
			String start = text.substring(0, index);
			String end = text.substring(index + need.length());
			text = start + replace + end;
			index = text.indexOf(need);
		}
		return text;
	}
}
