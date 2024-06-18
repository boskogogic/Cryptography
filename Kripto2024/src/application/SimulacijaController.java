package application;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.unibl.etf.helper.AlertHelper;
import org.unibl.etf.helper.Constants;
import org.unibl.etf.helper.FileHelper;
import org.unibl.etf.helper.MyszkowskiHelper;
import org.unibl.etf.helper.PlayfairHelper;
import org.unibl.etf.helper.RailFenceHelper;
import org.unibl.etf.model.UserTransferSingleton;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class SimulacijaController {
	
	private static UserTransferSingleton userData = UserTransferSingleton.getInstance();
	
	private String userName;
	
	private static final String userDirectoryPath = Constants.PATH_TO_USER_FILES + Constants.SEPARATOR + userData.getUserName() + Constants.SEPARATOR;

	@FXML
	private AnchorPane rootPane;
	
	@FXML
	private TextArea textAreaEnkripcija;
	
	@FXML
	private ComboBox comboBoxSifrat;
	
	@FXML
	private TextArea textAreaKljuc;
	
	ObservableList<String> sifratiList = FXCollections
			.observableArrayList(Constants.RAIL_FENCE, Constants.MYSZKOWSKI, Constants.PLAYFAIR);
	
	@FXML
	private Button buttonEnkriptuj;

	@FXML
	private void initialize()
	{
		userName = userData.getUserName();
		System.out.println("User name je  : " + userName);
		comboBoxSifrat.setValue("Rail fence");
		comboBoxSifrat.setItems(sifratiList);
	}
	
	@FXML
	public void enkriptuj(ActionEvent event) {
		System.out.println("Simulation: enkriptuj start.....");
		
		String algoritam = comboBoxSifrat.getValue().toString();
		String porukaZaEnkripciju = textAreaEnkripcija.getText();
		String enkriptovanaPoruka = "";
		String kljuc = textAreaKljuc.getText();
	
		//create user directory if not exist
		File directory = new File(userDirectoryPath);
		if (!directory.exists()) {
			System.out.println("Created user " + userData.getUserName() + " directory");
			directory.mkdirs();
		}
		System.out.println("Directory path is " + directory.getAbsolutePath());
	
		try {
			if(porukaZaEnkripciju.isEmpty() || kljuc.isEmpty()) {
				Alert error = AlertHelper.createAlert(AlertType.ERROR, "ERROR", "Niste popunili sva potrebna polja!");
				error.show();
				
				
			} else if(Constants.RAIL_FENCE.equals(algoritam)) {
				//RAIL FENCE algoritam
				enkriptovanaPoruka= RailFenceHelper.encryptRailFence(porukaZaEnkripciju, Integer.parseInt(kljuc));
				System.out.println("enkriptuj: Enkriptovana poruka je " + enkriptovanaPoruka);
				
				FileHelper.writeEncryptedText(userData.getUserName(),userDirectoryPath, porukaZaEnkripciju, Constants.RAIL_FENCE, kljuc, enkriptovanaPoruka);
					//dodati upis 
					
			} else if(Constants.MYSZKOWSKI.equals(algoritam)) {
				//MyszkowskiAlgoritam 
				MyszkowskiHelper myszkowski = new MyszkowskiHelper(kljuc);
				enkriptovanaPoruka = myszkowski.encode(porukaZaEnkripciju);
				System.out.println("Enkriptovana poruka je " + enkriptovanaPoruka);
				FileHelper.writeEncryptedText(userData.getUserName(),userDirectoryPath, porukaZaEnkripciju, Constants.MYSZKOWSKI, kljuc, enkriptovanaPoruka);
				
				
			} else {
				//PLAYFAIR algoritam
				PlayfairHelper pfh = new PlayfairHelper(kljuc, porukaZaEnkripciju);
			    pfh.cleanPlayFairKey();
			    pfh.generateCipherKey();
			    enkriptovanaPoruka = pfh.encryptMessage();	
			    System.out.println("enkriptuj: Enkriptovana poruka je " + enkriptovanaPoruka);
			    FileHelper.writeEncryptedText(userData.getUserName(),userDirectoryPath, porukaZaEnkripciju, Constants.PLAYFAIR, kljuc, enkriptovanaPoruka);
			} 
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	@FXML
	public void izlistajDirektorijum(ActionEvent event) {
		System.out.println("Simulation: izlistajDirektorijum start.....");
		AnchorPane pane;
		try {
			pane = FXMLLoader.load(getClass().getResource("ListDirectory.fxml"));
			//pane.setPrefWidth(654);
			//pane.setPrefHeight(478);
			pane.setMinSize(654, 478);
			//pane.setMinHeight(478);
			rootPane.getChildren().setAll(pane);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO izlistati direktorijum od usera
	}
	
}


