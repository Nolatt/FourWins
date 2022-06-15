package game;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import network.ChatHandler;
import network.Client;
import util.AlertUtil;

// for showing game screen
public class GameView {
	private BorderPane pane;
	private Game game;
	private Stage stage;
	int[][] board = new int[7][6];
	int player = 0;
	private Text text = new Text();
	private boolean running;
	private Client client;
	private HBox hBox;
	ListView<String> chatView = new ListView<>();
	private TextField playerInputText;

	public GameView(Stage stage, Game game) {
		this.stage = stage;
		this.game = game;
	}

	// method for drawing game board depending on 2 dimensional array variable "board"
	public void drawBoard() {
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 6; j++) {
				// create new circles holes for empty spaces in board
				// game board has 7 columns and six rows each has height and width of 80
				Circle circle = new Circle((i * 80) + 40, (j * 80) + 40, 40, Color.WHITE);

				// if current index of array has a value, fill with corresponding color
				if (board[i][j] == 1)
					circle.setFill(Color.RED);
				if (board[i][j] == 2)
					circle.setFill(Color.YELLOW);
				pane.getChildren().add(circle);
			}
		}

		// reset value of whose turn it is
		text.setText(player == 1 ? "Your turn!" : "Opponents turn!");

		// check if any player has won already
		hasPlayerWon();
	}

	public void setPlayer(int num) {
		this.player = num;
	}

	// shows alert after player has won
	private void showDialog(int player) {
		String msg = "";
		if (player == 0) {
			msg = "TIE GAME!";
		} else if (player == 1) {
			msg = "Yes! You won the game!";
		} else {
			msg = "You lost!";
		}

		// send to server that game is over
		client.sendGameOver();
		showDialog(msg);
	}

	// for showing alert dialog
	public void showDialog(String msg) {
		text.setText(msg);
		Alert dialog = new Alert(AlertType.INFORMATION);
		dialog.setContentText(msg);
		dialog.initOwner(stage);
		dialog.showAndWait();
		reset();
		game.showStartScreen();
	}

	// get index of next free spot in column
	public int calcuteNextFreeSpot(int x) {
		// if user has clicked outside of board
		if (x > 6) {
			return -1;
		}

		for (int y = 5; y >= 0; y--)
			if (board[x][y] == 0) {
				// this means no player has set a circle here
				return y;
			}
		return -1;
	}

	// method that checks whether game is over
	public void hasPlayerWon() {
		// check if any player has won
		hasFourEqualCirclesInColumn();
		hasFourEqualCirclesInRow();
		hasFourEqualCirclesDiagonal();
		checkIfTiedGame();
	}

	// checks in each column of the board whether there are 4 consecutive circles
	// from one player
	private void hasFourEqualCirclesInColumn() {
		for (int row = 0; row < 7; row++) {
			for (int column = 0; column < 6; column++) {
				if (getPlayerAtPosition(row, column) != 0
						&& getPlayerAtPosition(row, column) == getPlayerAtPosition(row, column + 1)
						&& getPlayerAtPosition(row, column + 1) == getPlayerAtPosition(row, column + 2)
						&& getPlayerAtPosition(row, column + 2) == getPlayerAtPosition(row, column + 3)) {
					showDialog(getPlayerAtPosition(row, column));
				}
			}
		}
	}

	// checks in each row of the board whether there are 4 consecutive circles from
	// one player
	private void hasFourEqualCirclesInRow() {
		for (int row = 0; row < 7; row++) {
			for (int column = 0; column < 6; column++) {
				if (getPlayerAtPosition(row, column) != 0
						&& getPlayerAtPosition(row, column) == getPlayerAtPosition(row + 1, column)
						&& getPlayerAtPosition(row + 1, column) == getPlayerAtPosition(row + 2, column)
						&& getPlayerAtPosition(row + 2, column) == getPlayerAtPosition(row + 3, column)) {
					showDialog(getPlayerAtPosition(row, column));
				}
			}
		}
	}

	// checks if there are 4 diagonal consecutive circles
	private void hasFourEqualCirclesDiagonal() {
		for (int row = 0; row < 7; row++) {
			for (int column = 0; column < 6; column++) {
				// check from left top to right bottom
				if (getPlayerAtPosition(row, column) != 0
						&& getPlayerAtPosition(row, column) == getPlayerAtPosition(row + 1, column + 1)
						&& getPlayerAtPosition(row + 1, column + 1) == getPlayerAtPosition(row + 2, column + 2)
						&& getPlayerAtPosition(row + 2, column + 2) == getPlayerAtPosition(row + 3, column + 3)) {
					showDialog(getPlayerAtPosition(row, column));
				}

				// check from left bottom to right top
				if (getPlayerAtPosition(row, column) != 0
						&& getPlayerAtPosition(row, column) == getPlayerAtPosition(row - 1, column + 1)
						&& getPlayerAtPosition(row - 1, column + 1) == getPlayerAtPosition(row - 2, column + 2)
						&& getPlayerAtPosition(row - 2, column + 2) == getPlayerAtPosition(row - 3, column + 3)) {
					showDialog(getPlayerAtPosition(row, column));
				}
			}
		}
	}

	// checks if game is tied, true when all fields are filled with non 0 values and
	// no consecutive 4 circles exist
	private void checkIfTiedGame() {
		for (int row = 0; row < 7; row++) {
			for (int column = 0; column < 6; column++) {
				if (getPlayerAtPosition(row, column) == 0) {
					return;
				}
			}
		}
		// tie
		showDialog(0);
	}

	// returns the value which is stored at given row and column
	// returns 0 if row or column parameter is greater than number of rows or
	// columns
	// or less than 0
	private int getPlayerAtPosition(int row, int column) {
		if (row > 6 || row < 0 || column > 5 || column < 0) {
			return 0;
		}
		return board[row][column];
	}

	// once the game is finished, reset all necessary values such as board and
	// counter
	private void reset() {
		board = new int[7][6];
		player = 1;
		running = false;
		chatView.getItems().clear();
	}
	
	// show chat messages
	public void showChatMessages() {
		chatView.getItems().clear();
		chatView.getItems().addAll(ChatHandler.getMessages());
	}

	// method for showing game board
	public void showGameInterface() {
		running = true;
		pane = new BorderPane();
		// game board is rectangle with a width of 560 and height of 480
		Rectangle rectangle = new Rectangle(0, 0, 560, 480);
		// background should be blue
		rectangle.setFill(Color.BLUE);
		// set border
		rectangle.setStrokeWidth(2);
		rectangle.setStroke(Color.BLACK);

		Text arrowText = new Text("--> ");
		arrowText.setFont(Font.font(30));
		arrowText.setX(40);
		arrowText.setY(530);

		// text shows whose it is
		text.setX(100);
		text.setY(530);
		text.setFont(Font.font(30));

		hBox = new HBox();
		hBox.getChildren().add(arrowText);
		hBox.getChildren().add(text);
		
		VBox vBox = new VBox();
		vBox.setMaxHeight(500);
		vBox.setPrefHeight(490);
		
		Button sendBtn = new Button("SEND");
		sendBtn.setPrefWidth(280);
		playerInputText = new TextField();
		playerInputText.setPrefWidth(280);

		chatView.setMaxHeight(460);
		chatView.setPrefWidth(280);
		
		vBox.getChildren().add(chatView);
		vBox.getChildren().add(playerInputText);
		vBox.getChildren().add(sendBtn);

		pane.setLeft(rectangle);
		pane.setPadding(new Insets(0, 0, 20, 0));
		pane.setBottom(hBox);
		pane.setRight(vBox);

		showChatMessages();
		drawBoard();

		// onClickListener for game board
		pane.setOnMouseClicked(e -> {
			if (player == 1 && e.getButton() == MouseButton.PRIMARY) {
				// calculate which row was clicked by taking x from clickEvent and divide by 80
				// (width of column)
				int x = (int) (e.getX() / 80);
				int y = calcuteNextFreeSpot(x);

				if (y >= 0 && x <= 6) {
					board[x][y] = player;
					player = 2;
					client.sendMoveToOpponent(x, y);
					drawBoard();
				}
			} else {
				AlertUtil.showMessageDialogShowShort("Not your turn");
			}
		});
		
		sendBtn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				String message = playerInputText.getText();
				
				if (message != null && !message.isEmpty()) {
					ChatHandler.addMessage(message, 1);
					client.send(message);
					showChatMessages();
					playerInputText.clear();
				}
			}
		});

		Scene scene = new Scene(pane);
		stage.setScene(scene);
	}

	// needed when close window button is clicked
	public boolean getRunning() {
		return running;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	// set opponents circle when info is received from server 
	public void setOpponentCircle(int row, int column) {
		board[row][column] = 2;
		
		// Platform.runLater needs to be called when non mainthread is trying to update ui 
		Platform.runLater(() -> {
			player = 1;
			drawBoard();
		});

	}

	// send info to server that player has quitted
	public void quitGame() {
		System.out.println("quitting game");
		client.quit();
	}
	
	// send any message to server/opponent
	public void send(String message) {
		client.send(message);
	}
}
