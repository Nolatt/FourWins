package network;

import java.util.ArrayList;
import java.util.List;

// stores chat messages in an arraylist
public class ChatHandler {
	private static List<String> chatMessages = new ArrayList<>();
	
	// adds new chat message depending on who sent it
	public static void addMessage(String message, int player) {
		System.out.println("Called "+message);
		String sender = player == 1 ? "You: " : "Opponent: ";
		chatMessages.add(sender + message);
	}
	
	// gets all messages to show in gameView
	public static List<String> getMessages() {
		if (chatMessages.isEmpty()) {
			chatMessages.add("Chat between you and opponent:");
		}
		return chatMessages;
	}
	
	// deletes all messages once game is over
	public static void deleteAllMessages() {
		chatMessages.clear();
	}
}
