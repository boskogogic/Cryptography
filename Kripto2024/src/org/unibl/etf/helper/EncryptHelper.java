package org.unibl.etf.helper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Certificate;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;
//import org.bouncycastle.openssl;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import org.bouncycastle.util.io.pem.PemReader;

public class EncryptHelper {
	
	public static String encrypt(String messageForEncrypt, String algorithm, String key) {
		return null;
	}
	
	public static byte[] generateHashedUsername(String userName) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.reset();
        System.out.println("GenerateHashedUsername: " + digest.digest(userName.getBytes(StandardCharsets.UTF_8)));
        return digest.digest(userName.getBytes(StandardCharsets.UTF_8));
    }
    
	public static byte[] generateHashedPassword(String userPassword, byte[] salt) {
		
		byte[] hashedPassword = null;
		try{
			hashedPassword = createHashedPassword(userPassword, salt); 
		}catch(Exception e) {
			System.out.print(e.getMessage());
		}
		return hashedPassword;
	}
	public static byte[] createHashedPassword(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        digest.update(salt);
        return digest.digest(password.getBytes(StandardCharsets.UTF_8));
    }
	
	public static KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyPairGenerator pair = KeyPairGenerator.getInstance("RSA", "BC");
		pair.initialize(2048, new SecureRandom());
		return pair.generateKeyPair();
	}
	
	public static String encodeToString(byte[] hashOrSalt) {
		return Base64.getEncoder().encodeToString(hashOrSalt);
	}
	
	public static byte[] decode(String data) {
		return Base64.getDecoder().decode(data);
	}
	
	public static X509Certificate loadCertificate(String fromUser) {
        String certLocation = "certs" + Constants.SEPARATOR + fromUser + ".crt";
        try {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) factory.generateCertificate(new FileInputStream(certLocation));
            return certificate;
        } catch (CertificateException | FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
	
	public static PrivateKey loadPrivateKey(String fromUser) {
	        String keyPath = "private" + Constants.SEPARATOR + fromUser + ".key";
	        PemReader reader;
			
			  try (PEMParser parser = new PEMParser(new BufferedReader(new FileReader(keyPath)))) 
			  { 
				  PEMKeyPair pemKeyPair = (PEMKeyPair) parser.readObject(); 
				  KeyPair keyPair = new JcaPEMKeyConverter().getKeyPair(pemKeyPair); 
				  return keyPair.getPrivate(); 
			  }
			  	catch (IOException e) { e.printStackTrace(); 
			  }
			 
			  return null;
	   }
	
	public static boolean validateCertificate(String fromUser) throws SignatureException {
        String certLocation = "certs" + Constants.SEPARATOR + fromUser + ".p12";
        try {
            PublicKey rootPubKey;
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            KeyStore keystore = KeyStore.getInstance("PKCS12");
            InputStream loadKeystore = new FileInputStream(certLocation);
            keystore.load(loadKeystore, new char[0]);
            X509Certificate certificate = (X509Certificate) keystore.getCertificate(fromUser);
            System.out.println("Debug certificate is " + certificate.toString());
           String issuerName = certificate.getIssuerX500Principal().getName();  //s ovom linijom koda dobijamo kompletan DN iz issuer - a
            X500Name x500Name = new JcaX509CertificateHolder(certificate).getIssuer();
            System.out.println("Issuer name : " + issuerName);
            rootPubKey = loadPublicKey(Constants.ROOT_CERTIFICATE);
            
            System.out.println("Root public key is " + rootPubKey.getFormat() + " " + rootPubKey);

            certificate.checkValidity(new Date(System.currentTimeMillis()));

            //provjeri da nije sertifikat povucen od strane crl liste
            Path crlPath = Paths.get(Constants.CRL_LIST);
            if (Files.exists(crlPath)) {
                X509CRLEntry revokedCertificate;
                X509CRL crl = (X509CRL) factory.generateCRL(new DataInputStream(new FileInputStream(Constants.CRL_LIST)));
                revokedCertificate = crl.getRevokedCertificate(certificate.getSerialNumber());
                return revokedCertificate == null;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
	
	public static PublicKey loadPublicKey(String fromUser) {
        String certLocation = "certs" + Constants.SEPARATOR + fromUser + ".crt";
        try {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) factory.generateCertificate(new FileInputStream(certLocation));
            System.out.println("Debug loadPublicKey : " + certificate.getPublicKey().toString());
            return certificate.getPublicKey();
        } catch (CertificateException | FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
	
	public static void encryptFile(String content, String outputFile, String userName) {
        
		try {
        	System.out.println("encrypt : Output file  " + outputFile);
        	PublicKey key = getPublicKey(userName);
        	//loadPublicKey(userName);
            FileOutputStream outputStream = new FileOutputStream(outputFile);
           
            byte[] keyBytes = key.toString().getBytes();
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, Constants.ALGORITHM);
            Cipher cipher = Cipher.getInstance(Constants.ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            //outputStream.write(cipher.getIV()); // Write the IV first

            //byte[] buffer = new byte[8192];
            //int count;
            byte[] encryptedBytes = content.getBytes();
            System.out.println("encrypted bytes " + encryptedBytes);
            outputStream.write(encryptedBytes);
            outputStream.write(cipher.doFinal());

            outputStream.close();
            System.out.println("File encrypted successfully!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
	
	 public static String decryptFile(String inputFile, String userName) {
        try {
        	System.out.println("Decrypt file start... Input file " + inputFile + " a userName " + userName);
        	PublicKey key = getPublicKey(userName);
            FileInputStream inputStream = new FileInputStream(inputFile);
    

            byte[] keyBytes = key.toString().getBytes();
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, Constants.ALGORITHM);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            byte[] encryptedBytes = baos.toByteArray();
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            System.out.println("File decrypted successfully!");
            inputStream.close();
            return new String(decryptedBytes);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
	 }
	 
	 public static PublicKey getPublicKey(String fromUser) {
		FileInputStream fm;
		try {
			fm = new FileInputStream
					 (Constants.PATH_TO_USER_CERTIFICATE + Constants.SEPARATOR + fromUser + ".p12");
		
		    KeyStore ks = KeyStore.getInstance("PKCS12");
		    try {
		        ks.load(fm, "".toCharArray());
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		    Key key = ks.getKey("cert", "".toCharArray());
		    
		    //System.out.println("Key is " + key.getAlgorithm() + "");
		    X509Certificate cert =  (X509Certificate) ks.getCertificate(fromUser);
		    PublicKey publicKey = cert.getPublicKey();
		    System.out.println("Public key " + publicKey.getFormat() + " " + publicKey.getAlgorithm());
		    System.out.println(Base64.getEncoder().encodeToString(publicKey.getEncoded()));
		    fm.close();
		    return publicKey;
		    
		}catch (IOException | UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
		}
		return null;
		
	 }
}
