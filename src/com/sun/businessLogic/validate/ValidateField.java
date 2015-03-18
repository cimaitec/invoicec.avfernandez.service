package com.sun.businessLogic.validate;

import java.util.Date;

public class ValidateField {
	//< 0 Warning
	//0 Ok
	//> 0 Error
	private int li_error=0;
	private String ls_msg_error = "";
	
	private int valueInt;
	private long valueLong;
	private double valueDouble;	
	private String valueString;
	private Date	valueDate;
	
	
	
	public int getLi_error() {
		return li_error;
	}
	public void setLi_error(int li_error) {
		this.li_error = li_error;
	}
	public String getLs_msg_error() {
		return ls_msg_error;
	}
	public void setLs_msg_error(String ls_msg_error) {
		this.ls_msg_error = ls_msg_error;
	}
	
	public int getValueInt() {
		return valueInt;
	}
	public void setValueInt(int valueInt) {
		this.valueInt = valueInt;
	}
	public long getValueLong() {
		return valueLong;
	}
	public void setValueLong(long valueLong) {
		this.valueLong = valueLong;
	}
	public double getValueDouble() {
		return valueDouble;
	}
	public void setValueDouble(double valueDouble) {
		this.valueDouble = valueDouble;
	}
	public String getValueString() {
		return valueString;
	}
	public void setValueString(String valueString) {
		this.valueString = valueString;
	}
	public Date getValueDate() {
		return valueDate;
	}
	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}
	
	
}
