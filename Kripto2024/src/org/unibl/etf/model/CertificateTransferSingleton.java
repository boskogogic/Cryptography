package org.unibl.etf.model;

public class CertificateTransferSingleton {
	
	private static CertificateTransferSingleton instance;
	
	private String certificateName;
	
	private CertificateTransferSingleton() {
		
	}

	public static CertificateTransferSingleton getInstance() {
		if(instance == null) instance = new CertificateTransferSingleton();
		return instance;
	}

	public String getCertificateName() {
		return certificateName;
	}

	public void setCertificateName(String certificateName) {
		this.certificateName = certificateName;
	}
}
