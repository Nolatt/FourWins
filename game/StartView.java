package game;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import network.Client;
import util.AlertUtil;

// shows start screen 
public class StartView {
	private Stage stage;
	private GameView gameController;
	private Client client;
	private boolean connecting;
	private Button button;
	private Text waitingText;
	private HBox hBox;

	// create circles below start game button 
	private void createCircles() {
		boolean red = true;
		for (int i = 0; i < 4; i++) {
			Circle circle = new Circle((i * 80) + 40, (i * 80) + 40, 40, red ? Color.RED : Color.YELLOW);
			red = !red;
			hBox.getChildren().add(circle);
		}
	}

	// show start screen
	public void showStartInterface() {
		// create play button
		button = new Button("START GAME");
		button.setPrefHeight(40);
		button.setPrefWidth(160);

		hBox = new HBox();
		createCircles();
		hBox.setAlignment(Pos.CENTER);
		
		Text title = new Text("4 WINS");
		title.setFont(Font.font(60));
		title.setStrokeWidth(2);
		title.setStroke(Color.YELLOW);

		waitingText = new Text("Waiting for opponent...");
		waitingText.setFont(Font.font(24));
		waitingText.setVisible(false);
		
		VBox vBox = new VBox();
		vBox.getChildren().add(button);
		vBox.getChildren().add(waitingText);
		vBox.setAlignment(Pos.CENTER);

		// create borderpane
		BorderPane borderPane = new BorderPane();
		borderPane.setPrefWidth(560);
		borderPane.setPrefHeight(480);
		borderPane.setPadding(new Insets(40));
		borderPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		borderPane.setCenter(vBox);
		borderPane.setBottom(hBox);
		borderPane.setTop(title);
		BorderPane.setAlignment(title, Pos.CENTER);

		// create scene
		Scene scene = new Scene(borderPane);
		stage.setScene(scene);

		// set mouseClickListener
		// this button starts action to finding opponent and showing game board
		button.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				if (connecting) {
					return;
				}

				connecting = true;
				client.connectToServer();
			}
		});
	}

	// some setters 
	
	public void setGameController(GameView controller) {
		this.gameController = controller;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public void setConnecting(boolean isConnecting) {
		this.connecting = isConnecting;
	}

	// method that is called when server notifies about found opponent
	// until then wait and show wait for opponent textfield
	public void startGame() {
		Platform.runLater(() -> {
			button.setVisible(true);
			waitingText.setVisible(false);
			gameController.showGameInterface();
		});
	}

	// show waiting for opponent text
	public void showAlert() {
		Platform.runLater(() -> {
			button.setVisible(false);
			waitingText.setVisible(true);
		});
	}
}
