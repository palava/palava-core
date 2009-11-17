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

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


/**
 * an internal client class for the palava protocol
 * used by the console job for javascript usage and
 * the stand alone palavaclient
 * @author Tobias Sarnowski
 */
public class Client
{

	private String host;
	private int port;

	private Socket socket;


	private String sessionid = null;



	public static Client openConnection(Server server)
	{
		return new Client(server);
	}

	public static Client openConnection(String host, int port)
	{
		return new Client(host, port);
	}

	public Client()
	{
		// do nothing, provide the static methods to the script engine
	}


	public Client(Server server)
	{
		host = "localhost";
		port = server.getListenPort();

		connect();
	}

	public Client(String host, int port)
	{
		this.host = host;
		this.port = port;

		connect();
	}


	private void connect()
	{

		try {
			socket = new Socket(host, port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public String send(String rawdata) throws Exception
	{
		OutputStream out = socket.getOutputStream();
		out.write(rawdata.getBytes());
		out.flush();
		return receive();
	}

	public String sendRequest(String type, String job, String sessionid, String data) throws Exception
	{
		if (sessionid == null)
			sessionid = "";

		String rawdata = type + "://" + job + "/" + sessionid + "/(" + data.length() + ")?";

		rawdata = rawdata + data;
		return send(rawdata);
	}

	public String sendRequest(String type, String job, String data) throws Exception
	{
		return sendRequest(type, job, sessionid, data);
	}

	public String sendRequest(String type, String job) throws Exception
	{
		return sendRequest(type, job, "");
	}

	public String sendRequest(String job) throws Exception
	{
		return sendRequest("data", job);
	}


	private String receive() throws Exception
	{
		InputStream in = socket.getInputStream();
		

		// read the header

		String mimetype = "";
		String contentlength = "";

		int part = 0;
		boolean end = false;
		while (!end) {
			int result = 0;
			while (result <= 0) {
				result = in.read();
			}
			char data = (char)result;

			switch (part) {
				case 0:
						if (data == ':')
							part++;
						else
							mimetype = mimetype + data;
						break;
				case 1:
				case 2:
						if (data == '/')
							part++;
						else
							throw new Exception("protocol error 1");
						break;
				case 3:
						if (data == '(')
							part++;
						else
							throw new Exception("protocol error 2");
						break;
				case 4:
						if (data == ')')
							part++;
						else
							contentlength = contentlength + data;
						break;
				case 5:
						if (data == '?')
							end = true;
						else
							throw new Exception("protocol error 3");
			}
		}

		// read the content
		String content = "";

		long length = Long.parseLong(contentlength);
		for (long n = 0; n < length; n++) {
			int result = 0;
			while (result <= 0) {
				result = in.read();
			}
			content = content + (char)result;
		}
		return content;
	}

	public String startSession(String sessionid) throws Exception
	{
		sessionid = sendRequest("data", "@palava.session.initialize", sessionid, "ip=" + socket.getLocalAddress() + "&ua=Palava Server");
		sessionid = stripPHP(sessionid);
		this.sessionid = sessionid;
		return sessionid;
	}

	public String stripPHP(String text)
	{
		text = text.substring(1);
		text = text.substring(0, text.length() - 1);
		return text;
	}

	public String startSession() throws Exception
	{
		return startSession(null);
	}

	public String getSessionID()
	{
		return sessionid;
	}

	public void close() throws Exception
	{
		sendRequest("@palava.system.close");
		socket.close();
	}


}

