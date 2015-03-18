/**
 * 
 */
package com.util.util.key;

import java.util.Properties;


/**
 * @author Johnny Zurita Medranda
 */
public class ConfigLoader {

	 static String DELIM_START = "${";
	  static char   DELIM_STOP  = '}';
	  static int DELIM_START_LEN = 2;
	  static int DELIM_STOP_LEN  = 1;

	  /** ConfigLoader is a static class. */
	  public ConfigLoader() {}

	  public
	  String[] concatanateArrays(String[] l, String[] r) {
	    int len = l.length + r.length;
	    String[] a = new String[len];

	    System.arraycopy(l, 0, a, 0, l.length);
	    System.arraycopy(r, 0, a, l.length, r.length);

	    return a;
	  }

	  public
	  String convertSpecialChars(String s) {
	    char c;
	    int len = s.length();
	    StringBuffer sbuf = new StringBuffer(len);

	    int i = 0;
	    while(i < len) {
	      c = s.charAt(i++);
	      if (c == '\\') {
		c =  s.charAt(i++);
		if(c == 'n')      c = '\n';
		else if(c == 'r') c = '\r';
		else if(c == 't') c = '\t';
		else if(c == 'f') c = '\f';
		else if(c == '\b') c = '\b';
		else if(c == '\"') c = '\"';
		else if(c == '\'') c = '\'';
		else if(c == '\\') c = '\\';
	      }
	      sbuf.append(c);
	    }
	    return sbuf.toString();
	  }


	  /**
	     Very similar to <code>System.getProperty</code> except
	     that the {@link SecurityException} is hidden.

	     @param key The key to search for.
	     @param def The default value to return.
	     @return the string value of the system property, or the default
	     value if there is no property with that key.

	     @since 1.1 */
	  public
	  String getSystemProperty(String key, String def) {
	    try {
	      return System.getProperty(key, def);
	    } catch(Throwable e) { // MS-Java throws com.ms.security.SecurityExceptionEx
	      Environment.log.debug("Was not allowed to read system property \""+key+"\".");
	      return def;
	    }
	  }


	  /*public
	  Object instantiateByKey(Properties props, String key, Class superClass,
					Object defaultValue) {

	    // Get the value of the property in string form
	    String className = findAndSubst(key, props);
	    if(className == null) {
	    	Environment.log.error("Could not find value for key " + key);
	      return defaultValue;
	    }
	    // Trim className to avoid trailing spaces that cause problems.
	    return instantiateByClassName(className.trim(), superClass,defaultValue);
	  }*/

	  /**
	     If <code>value</code> is "true", then <code>true</code> is
	     returned. If <code>value</code> is "false", then
	     <code>true</code> is returned. Otherwise, <code>default</code> is
	     returned.

	     <p>Case of value is unimportant.  */
	  public
	  boolean toBoolean(String value, boolean dEfault) {
	    if(value == null)
	      return dEfault;
	    String trimmedVal = value.trim();
	    if("true".equalsIgnoreCase(trimmedVal))
	      return true;
	    if("false".equalsIgnoreCase(trimmedVal))
	      return false;
	    return dEfault;
	  }

	  public
	  int toInt(String value, int dEfault) {
	    if(value != null) {
	      String s = value.trim();
	      try {
		return Integer.valueOf(s).intValue();
	      }
	      catch (NumberFormatException e) {
	    	  Environment.log.error("[" + s + "] is not in proper int form.");
		e.printStackTrace();
	      }
	    }
	    return dEfault;
	  }


	  /**
	     Find the value corresponding to <code>key</code> in
	     <code>props</code>. Then perform variable substitution on the
	     found value.

	 */
	  public
	  String findAndSubst(String key, Properties props) {
	    String value = props.getProperty(key);
	    if(value == null)
	      return null;

	    try {
	      return substVars(value, props);
	    } catch(IllegalArgumentException e) {
	    	Environment.log.error("Bad option value ["+value+"].", e);
	      return value;
	    }
	  }

	  /**
	     Instantiate an object given a class name. Check that the
	     <code>className</code> is a subclass of
	     <code>superClass</code>. If that test fails or the object could
	     not be instantiated, then <code>defaultValue</code> is returned.

	     @param className The fully qualified class name of the object to instantiate.
	     @param superClass The class to which the new object should belong.
	     @param defaultValue The object to return in case of non-fulfillment
	   */
	  /*public
	  Object instantiateByClassName(String className, Class superClass,
					Object defaultValue) {
	    if(className != null) {
	      try {
	    Loader loader= new Loader();
		Class classObj = loader.loadClass(className);
		if(!superClass.isAssignableFrom(classObj)) {
			Environment.log.error("A \""+className+"\" object is not assignable to a \""+
			       superClass.getName() + "\" variable.");
			Environment.log.error("The class \""+ superClass.getName()+"\" was loaded by ");
			Environment.log.error("["+superClass.getClassLoader()+"] whereas object of type ");
			Environment.log.error("\"" +classObj.getName()+"\" was loaded by ["
			       +classObj.getClassLoader()+"].");
		  return defaultValue;
		}
		return classObj.newInstance();
	      } catch (Exception e) {
	    	  Environment.log.error("Could not instantiate class [" + className + "].", e);
	      }
	    }
	    return defaultValue;
	  }*/


	  /**
	     Perform variable substitution in string <code>val</code> from the
	     values of keys found in the system propeties.

	     <p>The variable substitution delimeters are <b>${</b> and <b>}</b>.

	     <p>For example, if the System properties contains "key=value", then
	     the call
	     <pre>
	     String s = ConfigLoader.substituteVars("Value of key is ${key}.");
	     </pre>

	     will set the variable <code>s</code> to "Value of key is value.".

	     <p>If no value could be found for the specified key, then the
	     <code>props</code> parameter is searched, if the value could not
	     be found there, then substitution defaults to the empty string.

	     <p>For example, if system propeties contains no value for the key
	     "inexistentKey", then the call

	     <pre>
	     String s = ConfigLoader.subsVars("Value of inexistentKey is [${inexistentKey}]");
	     </pre>
	     will set <code>s</code> to "Value of inexistentKey is []"

	     <p>An {@link java.lang.IllegalArgumentException} is thrown if
	     <code>val</code> contains a start delimeter "${" which is not
	     balanced by a stop delimeter "}". </p>

	     <p><b>Author</b> Avy Sharell</a></p>

	     @param val The string on which variable substitution is performed.
	     @throws IllegalArgumentException if <code>val</code> is malformed.

	  */
	  public 
	  String substVars(String val, Properties props) throws
	                        IllegalArgumentException {

	    StringBuffer sbuf = new StringBuffer();

	    int i = 0;
	    int j, k;

	    while(true) {
	      j=val.indexOf(DELIM_START, i);
	      if(j == -1) {
		// no more variables
		if(i==0) { // this is a simple string
		  return val;
		} else { // add the tail string which contails no variables and return the result.
		  sbuf.append(val.substring(i, val.length()));
		  return sbuf.toString();
		}
	      } else {
		sbuf.append(val.substring(i, j));
		k = val.indexOf(DELIM_STOP, j);
		if(k == -1) {
		  throw new IllegalArgumentException('"'+val+
			      "\" has no closing brace. Opening brace at position " + j
						     + '.');
		} else {
		  j += DELIM_START_LEN;
		  String key = val.substring(j, k);
		  // first try in System properties
		  String replacement = getSystemProperty(key, null);
		  // then try props parameter
		  if(replacement == null && props != null) {
		    replacement =  props.getProperty(key);
		  }

		  if(replacement != null) {
		    // Do variable substitution on the replacement string
		    // such that we can solve "Hello ${x2}" as "Hello p1" 
	            // the where the properties are
		    // x1=p1
	            // x2=${x1}
		    String recursiveReplacement = substVars(replacement, props);
		    sbuf.append(recursiveReplacement);
		  }
		  i = k + DELIM_STOP_LEN;
		}
	      }
	    }
	  }
	  
	  public void configure(){
	  }
	
	
}

