package application;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import org.unibl.etf.helper.AlertHelper;
import org.unibl.etf.helper.Constants;
import org.unibl.etf.helper.EncryptHelper;
import org.unibl.etf.helper.FileHelper;
import org.unibl.etf.model.UserTransferSingleton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class ListDirectoryController {
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	private static UserTransferSingleton userData = UserTransferSingleton.getInstance();
	
	private String userName;
	
	private static final String userDirectoryPath = Constants.PATH_TO_USER_FILES + Constants.SEPARATOR + userData.getUserName();

	@FXML 
	TextArea fileTextArea;
	
	@FXML
	Button backButton;
	
	private List<File> files = new ArrayList<>();
	
	@FXML
	private void initialize()
	{
		userName = userData.getUserName();
		System.out.println("User name je  : " + userName);
		files = FileHelper.getFilesFromUserDirectory(userDirectoryPath);
		
		System.out.println("User directory path je : " + userDirectoryPath);
		for(File f : files) {
			String[] nameOfFile = f.getName().split("_");
			String sifratTxt ="";
			sifratTxt = nameOfFile[(nameOfFile.length-1)]; //example OITAVTE ROSJNEN.txt
			String[] sifratNiz = sifratTxt.split("\\.");
			String sifrat = sifratNiz[0];
			System.out.println("Sifrat je : " + sifrat);
			byte[] hashedSifrat = null;
			try {
				hashedSifrat = EncryptHelper.generateHashedUsername(sifrat);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 			
			Path path = Paths.get(f.toURI());
			String content = "";
			try {
				if (!FileHelper.isDataInFileChanged(hashedSifrat, f)) {
					Alert warning = AlertHelper.createAlert(Alert.AlertType.WARNING, "Izmjenjen dokument", "DOKUMENT " + f.getPath() + " JE IZMJENJEN!");
					warning.show();
					System.out.println("FIRST IF  data changed");
					//continue;
				} else {
					content = FileHelper.getDataFromFile(f.getPath());
					fileTextArea.appendText("NAZIV DATOTEKE JE: " + f.getName() + 
							"\n SADRZAJ DATOTEKE JE : " + sifrat + "\n\n");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	
	public void switchToSimulacija(ActionEvent event) throws IOException, NoSuchAlgorithmException {

		try {
			switchToSceneSimulacija(event);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
		



	private void switchToSceneSimulacija(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("Simulacija.fxml"));
		Node node1;
		stage = (Stage)(((Node)event.getSource()).getScene().getWindow());
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("List Directory");
	
		stage.show();
	}
}
