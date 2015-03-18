package com.sun.directory.examples;

import java.io.File;
import java.io.FilenameFilter;

public class FileDemo {
	
   public static File[] paths;
   public static String comodin = ".txt";
   
   public static File[] busqueda(String rutaBusqueda, String fileComodin) {      
      File f = null;            
      comodin = fileComodin;
      paths = null;
      try{      
         // create new file
         f = new File(rutaBusqueda);
         
         // create new filename filter
         FilenameFilter fileNameFilter = new FilenameFilter() {
   
            
            public boolean accept(File dir, String name) {
               if(name.lastIndexOf('.')>0)
               {
                  // get last index for '.' char
                  int lastIndex = name.lastIndexOf('.');
                  
                  // get extension
                  String str = name.substring(lastIndex);
                  
                  // match path name extension
                  if(str.equals(comodin))
                  {
                     return true;
                  }
               }
               return false;
            }
         };
         // returns pathnames for files and directory
         paths = f.listFiles(fileNameFilter);         
         return paths;
      }catch(Exception e){
         // if any error occurs
         e.printStackTrace();
      }
		 return paths;
   }
}