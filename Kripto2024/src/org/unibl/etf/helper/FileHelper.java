package org.unibl.etf.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javafx.fxml.LoadException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class FileHelper {
	
	
	public static void writeKeys(String filePath, Key privateKey, Key publicKey) throws IOException {
		System.out.println("FileHelper: Inside write keys");
		FileOutputStream out = new FileOutputStream(filePath + ".key");
		System.out.println("Out je " + out.toString() + " a output file je " + filePath.toString());
		File file = null;
		file = new File(filePath + ".key");
		file.setReadOnly();
		
		out.write(privateKey.getEncoded());
		out.close();
		
		out = new FileOutputStream(filePath + ".pub");
		out.write(publicKey.getEncoded());
		out.close();
	}
	
	public static void writeHashedUser(byte[] hashedUserName, byte[] hashedPassword) throws IOException {
		System.out.println("FileHelper: Inside writeHashedUser");
		FileOutputStream out = new FileOutputStream(Constants.PATH_TO_USERS);
		
		File file = new File(Constants.PATH_TO_USERS);
		out.write(hashedUserName);
		out.write(hashedPassword);
		out.close();
	}

	/*Pored toga, rezultat svake simulacije
	koju korisnik inicira, cuva se u tekstualnoj datoteci, u formatu: 
	TEKST | ALGORITAM | KLJUC | SIFRAT*/
	public static String writeEncryptedText(String userName, String pathToUserDirectory, String messageForEncrypt, String algorithm, String key, String messageEncrypted) throws IOException, NoSuchAlgorithmException {
		File file = null;
		
		file = new File(pathToUserDirectory + messageForEncrypt + "_" + 
				 algorithm + "_" + key + "_" + messageEncrypted + ".txt");//just example
		System.out.println("File " + file.toString());
		//file.createNewFile();
		//EncryptHelper.encryptFile(messageEncrypted, pathToUserDirectory + messageForEncrypt + "_" + 
		//		 algorithm + "_" + key + "_" + messageEncrypted + ".enc", userName);
		byte[] hashedEncryptedMessage = EncryptHelper.generateHashedUsername(messageEncrypted);
		String encodedHashEncryptedMessage = Base64.getEncoder().encodeToString(hashedEncryptedMessage);
		 PrintWriter writer;
	        try {
	            writer = new PrintWriter(new BufferedWriter(new FileWriter(file.getAbsolutePath(), true)));
	            writer.append(encodedHashEncryptedMessage);
	            writer.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		Alert message = AlertHelper.createAlert(AlertType.CONFIRMATION, "created", "Kreiran sifrat : " + messageEncrypted);
		message.show();
		return null;
	}
	
	public static String writeUser(byte[] hashUsername, byte[] hashPassword, byte[] salt) {
		File file = new File(Constants.PATH_TO_USERS);
		String encodedHashUsername = Base64.getEncoder().encodeToString(hashUsername);
		String encodedHashPassword = Base64.getEncoder().encodeToString(hashPassword);
        String encodedSalt = Base64.getEncoder().encodeToString(salt);
        PrintWriter writer;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(file.getAbsolutePath(), true)));
            writer.append(encodedHashUsername).append(" ");
            writer.append(encodedSalt).append(" ");
            writer.append(encodedHashPassword).append(" ");
            writer.append("\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		return encodedSalt;
	}
	
	public static boolean isUserExist(byte[] hashedUserName) {
		//ako je registrovan
        try {
        	BufferedReader in = new BufferedReader(new FileReader(Constants.PATH_TO_USERS));
	        String[] data;
	        String line;
	        while ((line = in.readLine()) != null) {
	            data = line.split(" ");
	            byte[] userNameDataHash = Base64.getDecoder().decode(data[0]);
	            System.out.println("HashedUserName is " + hashedUserName + " and userNameDataHash is " + userNameDataHash);
	            if (Arrays.equals(hashedUserName, userNameDataHash)) {
	            	System.out.println("File helper : User name exist");
	               return true;
	            }
	        }
	        
	      
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
        
        return false;
	}
	
	public static boolean checkIsPasswordValid(String password) {
		try {
			 BufferedReader in = new BufferedReader(new FileReader(Constants.PATH_TO_USERS));
             String[] data;
             String line;
             StringBuffer stringBuffer = new StringBuffer();
             while ((line = in.readLine()) != null) {
                 data = line.split(" ");
                 byte[] decodeSalt = Base64.getDecoder().decode(data[1]);
                 byte[] decodeHashPassword = Base64.getDecoder().decode(data[2]);
                 byte[] hashPassword = EncryptHelper.createHashedPassword(password, decodeSalt);
            
                 if (Arrays.equals(decodeHashPassword, hashPassword)) {
                	 return true;
                 }
             }
		} catch(IOException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static List<File> getFilesFromUserDirectory(String pathToUserDirectory){
		List<File> files = new ArrayList<>();
		File directory = new File(pathToUserDirectory);
		files = Arrays.asList(directory.listFiles());
		files.get(0).lastModified();
		return files;
		
	}
	
	public static boolean isDataInFileChanged(byte[] hashedEncryptedMessage, File file) throws IOException {

        try {
        	BufferedReader in = new BufferedReader(new FileReader(file.getPath()));
        	FileInputStream fl = new FileInputStream(file.getPath());
	        String[] data;
	        String line = in.readLine();
	        String encodedEncryptedMessage = Base64.getEncoder().encodeToString(hashedEncryptedMessage);;
	        if(line != null) {
	        	byte[] messageEncryptedHash = Base64.getDecoder().decode(line);
	        	if(MessageDigest.isEqual(hashedEncryptedMessage, messageEncryptedHash)) {
	        		System.out.println("HASH IS EQUAL return true");
	        		return true;
	        	}
	        }	        
	    } catch(Exception ex) {
	    	Alert warning = AlertHelper.createAlert(AlertType.WARNING, "Izmjenjena datoteka", "Vasa datoteka " + file.getPath() + " je izmjenjena i komprotivana!");
	    	warning.show();
	    } 
        System.out.println("File helper isDataInFileChanged: Changed! ");
        return false;
	}
	
	public static String getDataFromFile(String filePath) {

        byte[] messageEcnryptedHash = null;
        try {
        	BufferedReader in = new BufferedReader(new FileReader(filePath));
	        String[] data;
	        String line;
	        while ((line = in.readLine()) != null) {
	        	messageEcnryptedHash = Base64.getDecoder().decode(line);
	        }

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
        String encodedHashMessage = Base64.getEncoder().encodeToString(messageEcnryptedHash);
        return encodedHashMessage;
	}
	

}
