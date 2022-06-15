package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.Socket;

import game.GameView;
import game.StartView;
import javafx.scene.control.Alert;
import util.AlertUtil;

// client class to send information to server and to connect to server
public class Client {
	private Socket socket;
	private DataOutputStream dos;
	private GameView controller;
	private StartView startController;
	private ServerListener listener;

	public Client(GameView controller, StartView startController) {
		this.controller = controller;
		this.startController = startController;
	}

	// tries to connect to server socket on port 7777 and certain ip address or localhost
	public void connectToServer() {	
		try {
			this.socket = new Socket("localhost", Server.SERVER_PORT);
			System.out.println("Connected to server...");
			this.dos = new DataOutputStream(this.socket.getOutputStream());
			
			// starts listener for server messages once socket is connected
			listener = new ServerListener(new DataInputStream(socket.getInputStream()), controller, startController);
			new Thread(listener).start();
			startController.setConnecting(false);
		} catch (ConnectException c) {
			System.out.println("Error occurred.");
			c.printStackTrace();
			System.out.println("Proceeding...");
			AlertUtil.showMessageDialogShowShort("Start server first!");
			startController.setConnecting(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// sends players move to opponent including row and column number
	public void sendMoveToOpponent(int row, int column) {
		try {
			this.dos.writeUTF(Protocol.MOVE);
			this.dos.writeInt(row);
			this.dos.writeInt(column);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// sends info about game over
	public void sendGameOver() {
		try {
			this.dos.writeUTF(Protocol.GAME_OVER);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// sends any message
	public void send(String message) {
		try {
			this.dos.writeUTF(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// send info about quitting
	public void quit() {
		try {
			this.dos.writeUTF(Protocol.QUIT);
			listener.stop();
		} catch (IOException e) {
			System.out.println("Something went wrong");
		}
	}
}
