package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

// server which accepts multiple client connections (more than 2 people can play at the same time)
public class Server {
	public static final int SERVER_PORT = 7777;

	public static void main(String[] args) {
		DataOutputStream player1dos;
		DataOutputStream player2dos;

		while (true) {
			try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT);) {
				System.out.println("4Wins -> Server running...");
				
				// accept first player
				Socket player1Socket = serverSocket.accept();
				System.out.println("1st client connected");
				player1dos = new DataOutputStream(player1Socket.getOutputStream());
				player1dos.writeUTF(Protocol.WAITING_FOR_OPPONENT);
				player1dos.writeInt(1);

				// accept second player
				Socket player2Socket = serverSocket.accept();
				System.out.println("2nd client connected");
				player2dos = new DataOutputStream(player2Socket.getOutputStream());
				player2dos.writeUTF(Protocol.WAITING_FOR_OPPONENT);
				player2dos.writeInt(2);

				player2dos.writeUTF(Protocol.OPPONENT_FOUND);
				player1dos.writeUTF(Protocol.OPPONENT_FOUND);

				// start listener thread for client messages
				new Thread(new ClientHandler(new DataInputStream(player1Socket.getInputStream()), player2dos)).start();
				new Thread(new ClientHandler(new DataInputStream(player2Socket.getInputStream()), player1dos)).start();
			} catch (SocketException s) {
				System.out.println("Client disconnected.");
				break;
			} catch (IOException e) {
				System.out.println("Something went wrong.");
				break;
			}
		}
	}
}
