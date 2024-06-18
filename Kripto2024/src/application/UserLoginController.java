package application;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ResourceBundle;

import org.unibl.etf.helper.AlertHelper;
import org.unibl.etf.helper.EncryptHelper;
import org.unibl.etf.helper.FileHelper;
import org.unibl.etf.model.CertificateTransferSingleton;
import org.unibl.etf.model.UserTransferSingleton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UserLoginController implements Initializable {

	private Stage stage;
	private Scene scene;
	private Parent root;
	
	private CertificateTransferSingleton data = CertificateTransferSingleton.getInstance();
	
	private UserTransferSingleton userData = UserTransferSingleton.getInstance();
	
	private String certificate;
	
	@FXML 
	private TextField userName;
	
	@FXML
	private PasswordField userPassword;
	
	@FXML 
	private TextField passwordText;
	
	@FXML
	private Button loginButton;
	
	@FXML
	private CheckBox passwordCheckBox;
	
	/* Three methods for manipulation with password field.*/
	@FXML
	private void makePasswordVisible(ActionEvent event)
	{
		if(passwordCheckBox.isSelected()) {
			showPasswordText();
			return;
		}
		else {
			hidePasswordText();
		}
	}
	
	@FXML 
	private  void showPasswordText()
	{
		passwordText.setText(userPassword.getText());;
		passwordText.setVisible(true);
		userPassword.setVisible(false);
	}
	
	@FXML
	private void hidePasswordText() {
		userPassword.setText(passwordText.getText());
		userPassword.setVisible(true);
		passwordText.setVisible(false);
	}
	
	/* Method which describe requirements in if statement in method userSignUp. */
	private boolean userSignUpRequirements() {
		if( userName.getText().equals("") && passwordText.getText().equals("") ) {
			return false;
		}
		else if( !(userName.getText().equals("")) && passwordText.getText().equals("")){
			return false;
		}
		else if( (userName.getText().equals("") && !(passwordText.getText().equals("")))) {
			return false;
		}
		else if( userName.getText().equals("") && userPassword.getText().equals("") )  {
			return false;
		}
		else if( !(userName.getText().equals("")) && userPassword.getText().equals("") ) {
			return false;
		}
		else if( (userName.getText().equals("") && !(userPassword.getText().equals(""))) ) {
			return false;
		}
		else {
			return true;
		}
		
	}
	
	@Override 
	public void initialize(URL location, ResourceBundle resources) {
		certificate = data.getCertificateName();
		System.out.println("Naziv sertifikata je : " + certificate);
	}
	
	public void userLogin(ActionEvent event) throws IOException, NoSuchAlgorithmException {
		certificate = data.getCertificateName();
		if(!(userSignUpRequirements())) {
			if(userName.getText().equals(certificate)) {
				
				// TODO check is there user with that credentials and certificate
				byte[] hashedUserName = EncryptHelper.generateHashedUsername(userName.getText());
				System.out.println("User login");
				if (FileHelper.isUserExist(hashedUserName)) {
					System.out.println("User name exist!");
					if (FileHelper.checkIsPasswordValid(userPassword.getText())) {
						System.out.println("User password valid!");
						try {
							if(EncryptHelper.validateCertificate(userName.getText())) {
								//TODO move to Simulacija page
								System.out.println("User certificate valid!");
								userData.setUserName(userName.getText());
								switchToSimulationPage(event);
							}
						} catch(SignatureException ex) {
							System.out.println("It is not trusted " + ex.getMessage());
						}
					}
					
				} else {
					// TODO show warning
					Alert warning = AlertHelper.createAlert(Alert.AlertType.WARNING, "Not valid", "Username/password/certificate not valid!");
					warning.show();
				}
				
			} else {
				Alert error = AlertHelper.createAlert(Alert.AlertType.ERROR,"Error wrong certificate ", "It is not certificate for logged user!");
				error.show();
				switchToCertificatePage(event);
			}
			
		} else {
			// TODO show error
			Alert error = AlertHelper.createAlert(Alert.AlertType.ERROR,"Error empty field ", "Username or password field empty!");
			error.show();
		}
	}
	
	private void switchToSimulationPage(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("Simulacija.fxml"));
		Node node1;
		stage = (Stage)(((Node)event.getSource()).getScene().getWindow());
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Simulacija");
		stage.show();
	}
	
	private void switchToCertificatePage(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("SertificateLogin.fxml"));
		Node node1;
		stage = (Stage)(((Node)event.getSource()).getScene().getWindow());
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Simulacija");
		stage.show();
	}
}
