/**
 * 
 */
package com.util.util.key;

import java.io.FileWriter;
import java.io.PrintWriter;



/**
 * @author Johnny Zurita M
 *22/08/2011
 */
public class LogFile {
	private PrintWriter log = null;
	private DateTime fecha = new DateTime();

	public LogFile() throws Exception
	{
		fecha = new DateTime(); 
		log = new PrintWriter ( new FileWriter (fecha.getDate() + ".log", true), true );
	}

	public LogFile(String prefix) throws Exception
	{
		fecha = new DateTime();
		log = new PrintWriter ( new FileWriter (prefix + fecha.getDate() + ".log", true), true );
	}

	public LogFile(String prefix, String complement) throws Exception
	{
		fecha = new DateTime();
		log = new PrintWriter ( new FileWriter (prefix + fecha.getDate() + complement + ".log", true), true);
	}

	public void close() throws Exception
	{
		log.close();
	}

	public void println(String message)
	{
		log.println(message);
	}

	public void printlnTime(String message)
	{
		fecha = new DateTime();
		log.println(message+" - "+fecha.getTime());
	}

	public void printlnTime2(String message1, String message2)
	{
		fecha = new DateTime();
		log.println(message1+" - "+fecha.getTime()+" - "+message2);
	}
}

