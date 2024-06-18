package org.unibl.etf.helper;

import java.nio.file.FileSystems;
import java.security.SecureRandom;
import java.util.Random;

public class Constants {

	public static final String RAIL_FENCE = "Rail_fence";
	public static final int KEY_FOR_RAIL_FENCE = 3;
	
	public static final String MYSZKOWSKI = "Myszkowski";
	public static final String KEY_FOR_MYSZKOWSKI = "Poland";
	
	public static final String PLAYFAIR = "Playfair";
	public static final String KEY_FOR_PLAYFAIR = "Problem";
	
	public static final Random RANDOM = new SecureRandom();
	
	public static final String SEPARATOR = FileSystems.getDefault().getSeparator();
	
	public static final String CRL_LIST = "./crl/crllista.crl";
	
	public static final String ROOT_CERTIFICATE = "certproject";
	public static final String ROOT_KEY = "private4096";
	
	public static String PATH_TO_USERS = "C:\\Users\\bgogi\\eclipse-workspace\\Kripto2024\\registracija\\users.txt";

	public static String PATH_TO_USER_CERTIFICATE = "C:\\Users\\bgogi\\eclipse-workspace\\Kripto2024\\certs";
	
	public static String PATH_TO_USER_FILES = "C:\\Users\\bgogi\\eclipse-workspace\\Kripto2024\\userFiles";

	public static final String ALGORITHM = "RSA";
	public static final String TRANSFORMATION = "AES/CBC/PKCS7Padding"; // Bouncy Castle uses PKCS7Padding
}
