package org.unibl.etf.helper;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertHelper {
	
	public static Alert createAlert(String message) {
		return createAlert(Alert.AlertType.WARNING, "Kriptografija warning",message);
	}
	public static Alert createAlert(String title, String message) {
		return createAlert(Alert.AlertType.WARNING, title, message);
	}
	public static Alert createAlert(AlertType type, String title, String message) {
		Alert warning = new Alert(type);
		warning.setTitle(title);
		warning.setContentText(message);
		return warning;
	}
}
