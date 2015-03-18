package com.sun.directory.examples;

public class InitCapExample {
	public static void main(String[] args) {
		System.out.println(toInitCap("yashwant prueba"));
		System.out.println(capitalize("yashwant prueba"));
		System.out.println(capitalizeString("yashwant prueba"));
	}

	public static String toInitCap(String param) {
		if(param != null && param.length()>0){			
			char[] charArray = param.toCharArray(); // convert into char array
			charArray[0] = Character.toUpperCase(charArray[0]); // set capital letter to first postion
			return new String(charArray); // return desired output
		}else{
			return "";
		}
	}
	public static String capitalize(String line)
	{
	  return Character.toUpperCase(line.charAt(0)) + line.substring(1);
	}
	
	public static String capitalizeString(String string) {
		  char[] chars = string.toLowerCase().toCharArray();
		  boolean found = false;
		  for (int i = 0; i < chars.length; i++) {
		    if (!found && Character.isLetter(chars[i])) {
		      chars[i] = Character.toUpperCase(chars[i]);
		      found = true;
		    } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
		      found = false;
		    }
		  }
		  return String.valueOf(chars);
		}
}