package game;

import javafx.application.Application;
import javafx.stage.Stage;
import network.Client;

// main class which has to be started to play

public class Game extends Application{
	StartView startView = new StartView();
	GameView gameView;




	public void showStartScreen() {
		startView.showStartInterface();
	}

	// when window close button is clicked, override this method to stop game properly before platform is closed/exited
	@Override
	public void stop() throws Exception {
		if (gameView.getRunning()) {
			gameView.quitGame();
		}
		super.stop();
	}

	@Override
	public void start(Stage primaryStage) {
		// create GameView
		gameView = new GameView(primaryStage, this);
		Client client = new Client(gameView, startView);
		startView.setStage(primaryStage);
		startView.setGameController(gameView);
		startView.setClient(client);
		gameView.setClient(client);

		// start game by showing start screen first
		showStartScreen();

		primaryStage.setTitle("4 WINS");
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
