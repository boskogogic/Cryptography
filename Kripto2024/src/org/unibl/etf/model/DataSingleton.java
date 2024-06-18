package org.unibl.etf.model;

public class DataSingleton {
	
	private static DataSingleton instance;
	
	private String certificateName;
	
	private DataSingleton() {
		
	}

	public static DataSingleton getInstance() {
		if(instance == null) instance = new DataSingleton();
		return instance;
	}

	public String getCertificateName() {
		return certificateName;
	}

	public void setCertificateName(String certificateName) {
		this.certificateName = certificateName;
	}	
}
