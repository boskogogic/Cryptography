package application;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

import org.unibl.etf.helper.AlertHelper;
import org.unibl.etf.helper.Constants;
import org.unibl.etf.helper.EncryptHelper;
import org.unibl.etf.helper.FileHelper;
import org.unibl.etf.model.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


/*
 * userEmail: user@gmail.com
 * password: user
 * 
 */
public class RegistrationAndLoginController {
	private Stage stage;
	private Scene scene;
	private Parent root;
	//private Scene2Controller sc2 = new Scene2Controller();
	
	/* For my auto increment ID in database. */
	private static final AtomicInteger counter = new AtomicInteger();
	
	@FXML
	private Button signUp;
	
	@FXML
	private Button loginButton;
	
	@FXML
	private CheckBox passwordCheckBox;
	
	@FXML 
	private TextField userName;
	
	@FXML
	private PasswordField userPassword;
	
	@FXML 
	private TextField passwordText;
	

	public void initialize(URL location, ResourceBundle resources) {
		stage.setResizable(false);
	}
	
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
	private void showPasswordText()
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
	
	/*	Method where user try to signup
	 * Where i need to create certificate and pair of RSA keys */
	public void userSignUp(ActionEvent event) throws IOException, NoSuchAlgorithmException {
		
		if ((userSignUpRequirements())) {
			// TODO check is user alredy exist
			byte[] hashedUserName = EncryptHelper.generateHashedUsername(userName.getText());
			if (FileHelper.isUserExist(hashedUserName)) {
				// TODO Warning 
				Alert warning = AlertHelper.createAlert("Warning user exist ", "User with that username already exist!");
				warning.show();
				
			} else {
				User user = new User(userName.getText(),userPassword.getText());
				System.out.println("Created user is " + user.getUserName());
				Alert information = AlertHelper.createAlert(Alert.AlertType.CONFIRMATION, "User is created", 
						Constants.PATH_TO_USER_CERTIFICATE + Constants.SEPARATOR +  user.getUserName() + ".p12");
				information.show();
				switchToSertificateLoginPage(event);
			}
			
		} else {
			// TODO show error
			Alert error = AlertHelper.createAlert(Alert.AlertType.ERROR,"Error empty field ", "Username or password field empty!");
			error.show();
		}
	}
	
	/* Method which describe requirements in if statement in method userSignUp. */
	private boolean userSignUpRequirements() {
//		if( userName.getText().equals("") && passwordText.getText().equals("") ) {
//			return false;
//		}
//		else if( !(userName.getText().equals("")) && passwordText.getText().equals("")){
//			return false;
//		}
//		else if( (userName.getText().equals("") && !(passwordText.getText().equals("")))) {
//			return false;
//		}
		 if( userName.getText().equals("") && userPassword.getText().equals("") )  {
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

	public void userLogin(ActionEvent event) throws IOException {
		switchToSertificateLoginPage(event);
	}
	
	private void switchToSertificateLoginPage(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("SertificateLogin.fxml"));
		Node node1;
		stage = (Stage)(((Node)event.getSource()).getScene().getWindow());
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Certificate login");
		stage.show();
	}
}
