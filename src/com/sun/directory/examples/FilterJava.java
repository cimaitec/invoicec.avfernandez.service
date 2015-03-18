package com.sun.directory.examples;

import java.io.*; 
public class FilterJava implements FilenameFilter { 

// Esta clase implementa un filtro de archivos 
// usando la interface FilenameFilter 
// mediante el método accept 
String mascara; 
public FilterJava(String mascara) {
this.mascara = mascara;
} 
public boolean accept(File directorio, String nombre) {
//return nombre.indexOf(mascara) != -1;
	if (nombre.matches(mascara))
		return true;
	else
		return false;
}
} 
