package org.unibl.etf.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.X509Certificate;

import org.unibl.etf.helper.Constants;
import org.unibl.etf.helper.EncryptHelper;
import org.unibl.etf.helper.FileHelper;

public class User {
	
	private String userName;
	
	private String userPassword;
	
	private byte[] hashPassword;
	
	private byte[] salt;
	
	private byte[] hashUsername;
	
	private KeyPair keyPair;
	
	private Key publicKey;
	
	private Key privateKey;
	
	private Signature sign;
	
	private String certificate;
	
	public User(String certificate) {
		this.certificate = certificate;
	}
	public User(String userName, String userPassword) {
		this.userName = userName;
		this.userPassword=userPassword;
		generateKeyAndCertificate(userName);
		writeHashedUser(userName, userPassword);
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	private void writeHashedUser(String userName, String userPassword) {
		try {
			byte[] hashedUserName = EncryptHelper.generateHashedUsername(userName);
			salt = new byte[16];
			Constants.RANDOM.nextBytes(salt);
			byte[] hashedPassword = EncryptHelper.generateHashedPassword(userPassword,salt);
			FileHelper.writeUser(hashedUserName, hashedPassword,salt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
 	}
	
	private void generateKeyAndCertificate(String userName) {
		X509Certificate cert = EncryptHelper.loadCertificate(Constants.ROOT_CERTIFICATE);
		PrivateKey key = EncryptHelper.loadPrivateKey(Constants.ROOT_KEY);
		GeneratedCertificate issuer = new GeneratedCertificate(key, cert);
		try {
			char[] emptyPassword = new char[0];
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(null, emptyPassword);
            keyStore.setKeyEntry(userName, GeneratedCertificate.privateKey, emptyPassword,
                    new X509Certificate[] { GeneratedCertificate.certificate });
            File myCert = new File("./certs/" + userName + ".p12");
            try (FileOutputStream store = new FileOutputStream(myCert)) {
                keyStore.store(store, emptyPassword);
                System.out.println(myCert.getAbsolutePath());
            }
		} catch (Exception e) {
			System.out.println("User: generateKeyAndSertificate " + e.getMessage());
		}
	}
}
