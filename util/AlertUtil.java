package util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

// util class for showing alerts in game and start view
public class AlertUtil {
	public static Alert showMessageDialog(String msg) {
		Alert dialog = new Alert(AlertType.INFORMATION);
		dialog.setContentText(msg);
		dialog.showAndWait();
		return dialog;
	}
	
	public static Alert showMessageDialogShowShort(String msg) {
		Alert dialog = new Alert(AlertType.INFORMATION);
		dialog.setContentText(msg);
		dialog.show();
		return dialog;
	}
}
