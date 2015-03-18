package com.util.util.key;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class EncriptaClave {
	protected static String keyAcceso = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
	protected static String algorithm = "PBEWithMD5AndDES";	
public static void main(String[] args) {	
	String claveEncriptada = encryptClave(args[0]);
	String nameFileClave= args[1] ;
	System.out.println("Clave Encriptada::"+claveEncriptada);
	
	int flagGeneraFile = generaFileKey(nameFileClave,claveEncriptada);
	if (flagGeneraFile == 1)
		System.out.println("File "+nameFileClave+" Creado Correctamente.");
			
		//System.out.println("Clave desencriptada::"+desencriptada);
		System.out.println("Leida de File ::"+getFileKey(nameFileClave));
		String claveFile = getFileKey(nameFileClave);
		String desencriptada = decryptClave(claveEncriptada);
		System.out.println("Desencriptada de File ::"+desencriptada+"::");
}

public static String getFileKey(String nameFile){
	 File archivo = null;
     FileReader fr = null;
     BufferedReader br = null;
     String linea = "";
     try {
        // Apertura del fichero y creacion de BufferedReader para poder
        // hacer una lectura comoda (disponer del metodo readLine()).
        archivo = new File (nameFile);
        fr = new FileReader (archivo);
        br = new BufferedReader(fr);

        // Lectura del fichero        
        linea=br.readLine();
        //System.out.println(linea);
     }
     catch(Exception e){
        e.printStackTrace();
     }finally{
        // En el finally cerramos el fichero, para asegurarnos
        // que se cierra tanto si todo va bien como si salta 
        // una excepcion.
        try{                    
           if( null != fr ){   
              fr.close();     
           }                  
        }catch (Exception e2){ 
           e2.printStackTrace();
        }
     }
     return decryptClave(linea);
}

public static int generaFileKey(String nameFile, String cadena){
        FileWriter fichero = null;
        PrintWriter pw = null;
        try
        {	
        	File file = new File(nameFile);
        	if (file.exists())
        		file.delete();
        	
            fichero = new FileWriter(nameFile);
            pw = new PrintWriter(fichero);
            pw.write(cadena);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
           try {
           // Nuevamente aprovechamos el finally para 
           // asegurarnos que se cierra el fichero.
           if (null != fichero)
              fichero.close();
           } catch (Exception e2) {
              e2.printStackTrace();
           }
        }
		return 1;
}

private static String encryptClave(String clave) {
    StandardPBEStringEncryptor s = new StandardPBEStringEncryptor();
    s.setAlgorithm(algorithm);
    s.setPassword(keyAcceso);
    String devuelve = "";
    try {	devuelve = s.encrypt(clave);	} catch (Exception e) {		e.printStackTrace();	}
    return devuelve;
}
/*
public static String encrypt(String cadena) {
    return EncriptaClave.encryptClave(cadena);
}*/

private static String decryptClave(String clave) {
    StandardPBEStringEncryptor s = new StandardPBEStringEncryptor();
    s.setPassword(keyAcceso);
    s.setAlgorithm(algorithm);
    String devuelve = "";
    try {	devuelve = s.decrypt(clave);	
    } catch (Exception e) {		e.printStackTrace();	}
    return devuelve;
}
/*
public static String decrypt(String cadena) {
    return decryptClave(cadena);
}
*/
}

