package org.unibl.etf.model;

public class UserTransferSingleton {
	
	private static UserTransferSingleton instance;
	
	private String certificateName;
	
	private String userName;
	
	private UserTransferSingleton() {
		
	}

	public static UserTransferSingleton getInstance() {
		if(instance == null) instance = new UserTransferSingleton();
		return instance;
	}

	public String getCertificateName() {
		return certificateName;
	}

	public void setCertificateName(String certificateName) {
		this.certificateName = certificateName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}	

}
