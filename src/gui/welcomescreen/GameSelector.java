package gui.welcomescreen;

import java.io.FileNotFoundException;
import java.util.Set;

import controller.player.GameController;
import controller.welcomeScreen.SceneController;
import engine.utilities.data.GameDataHandler;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tools.DisplayLanguage;

/**
 * Class that allows user to select a game to play.
 * 
 * @author David, Samarth, Ian
 *
 */
public class GameSelector extends MenuOptionsTemplate {

	private static final String SELECTOR_PATH = "Selector.gif";
	private static final int SELECTOR_WIDTH = 125;
	private static final int SELECTOR_HEIGHT = 125;
	private static final int HEADING_PADDING = 0;
	private static final int ENTRY_SPACING = 25;
	private static final int TREE_WIDTH = 890;
	private static final int EXPANDED_TREE_HEIGHT = 150;
	private static final int COLLAPSED_TREE_HEIGHT = 90;
	private static final String NEW_GAME_TEXT = "NewGame";
	private static final String CONTINUE_GAME_TEXT = "ContinueGame";

	private Stage stage;
	private SceneController sceneController;
	private ScrollPane contentPane;
	private VBox entriesBox;

	/**
	 * Constructor that sets the scene
	 * 
	 * @param currentStage
	 *            - stage that the game selector is displayed in
	 * @param currentSceneController
	 *            - scene that the game select is displayed in
	 */
	public GameSelector(Stage currentStage, SceneController currentSceneController) {
		super(currentStage, currentSceneController);
		createOptionScreen(SELECTOR_PATH, SELECTOR_WIDTH, SELECTOR_HEIGHT, HEADING_PADDING);

		stage = currentStage;
		sceneController = currentSceneController;

		entriesBox = new VBox();
		entriesBox.setAlignment(Pos.TOP_CENTER);
		contentPane = getScrollPane();
		contentPane.setContent(entriesBox);
	}

	/**
	 * Extracts names of user create games and creates an entry for each game
	 */
	public void createGameSelector() {
		
		Set<String> gameSet = GameDataHandler.knownProjectsWithDateModified().keySet();
		for (String game : gameSet) {
			createGameEntry(game);
		}
	}

	// creates an HBox for displaying the game title
	private HBox createTitleItem(String gameTitle) {

		HBox title = new HBox(new Label(gameTitle));
		title.setAlignment(Pos.BASELINE_CENTER);
		title.setId("title");
		return title;
	}

	// creates an HBox for displaying buttons to play the game
	private HBox createButtonPanel(String gameName) {

		HBox buttonPanel = new HBox(ENTRY_SPACING);
		Button newGame = createPlayGameButton(NEW_GAME_TEXT, e -> handleNewGame(gameName));
		Button continueGame = createPlayGameButton(CONTINUE_GAME_TEXT, e -> handleContinueGame());

		buttonPanel.setAlignment(Pos.BASELINE_CENTER);
		buttonPanel.getChildren().addAll(newGame, continueGame);

		return buttonPanel;
	}

	private void handleNewGame(String theGame) {		
		try {
			GameController gameController = new GameController(stage, theGame, sceneController);
		} catch (FileNotFoundException e) {
			System.out.println("Alert");
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText(e.getMessage());
			alert.showAndWait();
		}
	}

	// loads a game whose progress has been saved
	private void handleContinueGame() {
		//TODO 
	}

	// creates buttons to play the game
	private Button createPlayGameButton(String text, EventHandler<ActionEvent> handler) {
		Button btn = new Button();
		btn.textProperty().bind(DisplayLanguage.createStringBinding(text));
		btn.setOnAction(handler);
		return btn;
	}

	// creates a treeview that displays the game title and play buttons
	private void createGameEntry(String gameTitle) {

		TreeItem<HBox> title = new TreeItem<HBox>(createTitleItem(gameTitle));
		TreeItem<HBox> buttons = new TreeItem<HBox>(createButtonPanel(gameTitle));
		title.getChildren().add(buttons);
		title.setExpanded(false);

		TreeView<HBox> entry = new TreeView<HBox>(title);
		entry.setOnMouseClicked(e -> resizeTree(title, entry));
		// entry.setOnMouseEntered(e -> expandTree(title, entry));
		// entry.setOnMouseExited(e -> collapseTree(title, entry));
		entry.setPrefWidth(TREE_WIDTH);
		entry.setPrefHeight(COLLAPSED_TREE_HEIGHT);
		entry.getStylesheets().add(GameSelector.class.getResource("GameListStyle.css").toExternalForm());

		entriesBox.getChildren().add(entry);
	}

	// expands the treeview (unused for now)
	private void expandTree(TreeItem<HBox> title, TreeView<HBox> entry) {
		title.setExpanded(true);
		entry.setPrefHeight(EXPANDED_TREE_HEIGHT);
	}

	// collapses the treeview (unused for now)
	private void collapseTree(TreeItem<HBox> title, TreeView<HBox> entry) {
		title.setExpanded(false);
		entry.setPrefHeight(COLLAPSED_TREE_HEIGHT);
	}

	// expands or collapses the treeview depending on its current state
	private void resizeTree(TreeItem<HBox> title, TreeView<HBox> entry) {
		if (entry.getExpandedItemCount() == 1) {
			title.setExpanded(true);
			entry.setPrefHeight(EXPANDED_TREE_HEIGHT);
		} else {
			title.setExpanded(false);
			entry.setPrefHeight(COLLAPSED_TREE_HEIGHT);

		}
	}
}
