package application;


import java.io.IOException;

import org.unibl.etf.helper.AlertHelper;
import org.unibl.etf.model.CertificateTransferSingleton;

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

public class SertificateLoginController {

	private Stage stage;
	private Scene scene;
	private Parent root;
	
	private CertificateTransferSingleton data = CertificateTransferSingleton.getInstance();
	
	@FXML
	private TextArea textAreaSertifikat;
	
	@FXML
	private Button buttonSertifikat;
	

	public void sertifikatLogin(ActionEvent event) throws IOException {
		if (textAreaSertifikat.getText().isEmpty()) {
			// TODO warning
			Alert warning = AlertHelper.createAlert(Alert.AlertType.ERROR,"Certificate error", "Certificate field empty");
			warning.show();	
		} else {
			data.setCertificateName(textAreaSertifikat.getText());
			Parent root = FXMLLoader.load(getClass().getResource("UserLogin.fxml"));
			Node node1;
			stage = (Stage)(((Node)event.getSource()).getScene().getWindow());
			scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("User login");
			stage.show();
			switchtoUserLogin(event);	
		}
	}

	private void switchtoUserLogin(ActionEvent event) {
		// TODO Auto-generated method stub
		
	}
}
