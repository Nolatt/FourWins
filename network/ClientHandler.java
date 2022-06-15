package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketException;

// handles and listens for client messages implements runnable to be started in separate thread
public class ClientHandler implements Runnable {
	private DataInputStream playerDis;
	private DataOutputStream opponentDos;

	public ClientHandler(DataInputStream playerDis, DataOutputStream opponentDos) {
		this.playerDis = playerDis;
		this.opponentDos = opponentDos;
	}

	// gets started when new thread is started
	@Override
	public void run() {
		try {
			String command = "";

			// read input streams as long as input is not equal to game over string 
			while (!command.equals(Protocol.GAME_OVER)) {
				command = playerDis.readUTF();
				process(command);
			}
		} catch (SocketException s) {
			System.out.println("Client disconnected.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// take commands and mediate to opponent on opponents outputstream 
	public void process(String command) throws IOException {
		switch (command) {
		case Protocol.MOVE:
			int row = this.playerDis.readInt();
			int column = this.playerDis.readInt();

			opponentDos.writeUTF(Protocol.MOVE);
			opponentDos.writeInt(row);
			opponentDos.writeInt(column);
			break;
		case Protocol.GAME_OVER:
			opponentDos.writeUTF(Protocol.GAME_OVER);
			break;
		case Protocol.QUIT:
			opponentDos.writeUTF(Protocol.QUIT);
			break;
		default:
			opponentDos.writeUTF(command);
			break;
		}
	}

}
