package com.util.util.key;

import java.io.BufferedReader;
import java.io.FileReader;

public class Configuration {

	
	private BufferedReader environment = null;
	private String fileName="";

	public Configuration(String propertyFile) throws Exception
	{		
		fileName = propertyFile;
	}

	 public String getParameter(String parameter) throws Exception
		{
			String value = "";
			String result = "";
			int inicio = 0;
		    environment = new BufferedReader( new FileReader(fileName) );
			while ( (value = environment.readLine()) != null )
			{
				if (value.indexOf(parameter)!=-1)
				{
					inicio = value.indexOf("=") + 1;
					if (inicio > 0)
					{
						result = value.substring(inicio).trim();
						break;
					}
				}
			}
			
			
			environment.close();
			return result;
	    }


	
}
