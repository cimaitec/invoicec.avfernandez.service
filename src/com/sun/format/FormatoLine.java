package com.sun.format;

import java.util.ArrayList;

public class FormatoLine {
	private ArrayList<Formatos> formatLine;

	public FormatoLine(){
		formatLine = new ArrayList<Formatos>();
	}
	
	public ArrayList<Formatos> getFormatLine() {
		return formatLine;
	}

	public void setFormatLine(ArrayList<Formatos> formatLine) {
		this.formatLine = formatLine;
	}
		
	public void addFormato(Formatos add){
		formatLine.add(add);
	}
	
}
