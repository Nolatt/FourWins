package network;

import java.io.DataInputStream;
import java.io.IOException;
import game.GameView;
import game.StartView;
import javafx.application.Platform;

// helper for client class to listen for server messages
public class ServerListener implements Runnable {
	private DataInputStream dis;
	private GameView gameController;
	private StartView startController;
	private String command;

	public ServerListener(DataInputStream dis, GameView controller, StartView startController) {
		this.dis = dis;
		this.gameController = controller;
		this.startController = startController;
	}

	// gets started once thread is started
	public void run() {
		System.out.println("Server listener running...");
		command = "";

		try {
			// run while command is not equal to quit string
			while (!command.equals(Protocol.QUIT)) {
				command = this.dis.readUTF();
				System.out.println("Remote: " + command);

				// process command
				switch (command) {
				case Protocol.WAITING_FOR_OPPONENT:
					int turn = this.dis.readInt();
					System.out.println("You are player " + turn);
					gameController.setPlayer(turn);
					startController.showAlert();
					break;
				case Protocol.OPPONENT_FOUND:
					startController.startGame();
					break;
				case Protocol.MOVE:
					int row = this.dis.readInt();
					int column = this.dis.readInt();
					gameController.setOpponentCircle(row, column);
					break;
				case Protocol.GAME_OVER:
					break;
				case Protocol.QUIT:
					System.out.println("Opponent quit");
					gameController.send(Protocol.QUIT);
					this.command = Protocol.QUIT;
					Platform.runLater(() -> gameController.showDialog("Opponent left the game! Start new game."));
					break;
				default:
					ChatHandler.addMessage(command, 2);
					Platform.runLater(() -> gameController.showChatMessages());
					break;
				}
			}
		} catch (IOException e) {
			System.out.println("Error occured.");
		}
		System.out.println("Listener stopped.");
	}

	// for stopping while loop
	public void stop() {
		this.command = Protocol.QUIT;
	}
}
