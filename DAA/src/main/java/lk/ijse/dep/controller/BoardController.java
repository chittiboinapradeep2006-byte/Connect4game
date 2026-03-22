package lk.ijse.dep.controller;

import com.jfoenix.controls.JFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.dep.service.*;

import java.io.IOException;
import java.util.Stack;

public class BoardController implements BoardUI {

    private static final int RADIUS = 42;

    public Label lblStatus;
    public Group grpCols;
    public AnchorPane root;
    public Pane pneOver;
    public JFXButton btnPlayAgain;
    public JFXButton btnHint;
    public JFXButton btnUndo;

    private String playerName;
    private boolean isAiPlaying;
    private boolean isGameOver;

    private Player humanPlayer;
    private Player aiPlayer;

    private int moveCount;
    private Stack<Integer> moveHistory = new Stack<>();

    private Board board;

    /* ================= INITIALIZATION ================= */

    private void initializeGame() {

        board = new BoardImpl(this);   // FIX: removed local variable

        humanPlayer = new HumanPlayer(board);
        aiPlayer = new AiPlayer(board);

        isAiPlaying = false;
        isGameOver = false;

        moveCount = 0;

        moveHistory.clear();   // important for undo
    }
    public void initialize() {

        initializeGame();
        

        grpCols.getChildren()
                .stream()
                .map(n -> (VBox) n)
                .forEach(v -> v.setOnMouseClicked(e -> colOnClick(v)));
    }

    private void colOnClick(VBox col) {

        if (!isAiPlaying && !isGameOver) {
            humanPlayer.movePiece(grpCols.getChildren().indexOf(col));
        }
    }

    public void initData(String name) {

        this.playerName = name;
        lblStatus.setText(name + ", it is your turn!");
    }

    /* ================= UPDATE BOARD ================= */

    @Override
    public void update(int col, boolean isHuman) {

        if (isGameOver) return;

        moveCount++;
        moveHistory.push(col);

        VBox vCol = (VBox) grpCols.lookup("#col" + col);

        Circle circle = new Circle(RADIUS);
        circle.getStyleClass().add(isHuman ? "circle-human" : "circle-ai");

        vCol.getChildren().add(0, circle);

        TranslateTransition tt = new TranslateTransition(Duration.millis(250), circle);
        tt.setFromY(-50);
        tt.setToY(circle.getLayoutY());
        tt.playFromStart();

        if (isHuman) {

            isAiPlaying = true;
            lblStatus.setText("Wait, AI is playing");

            new Timeline(new KeyFrame(Duration.seconds(0.6), e -> {
                if (!isGameOver) aiPlayer.movePiece(-1);
            })).play();

        } else {

            isAiPlaying = false;

            if (!isGameOver)
                lblStatus.setText(playerName + ", it is your turn!");
        }
    }

    /* ================= WINNER HANDLING ================= */

    @Override
    public void notifyWinner(Winner winner) {

        isGameOver = true;
        isAiPlaying = false;

        lblStatus.getStyleClass().clear();
        lblStatus.getStyleClass().add("final");

        String result;

        switch (winner.getWinningPiece()) {

            case BLUE:
                lblStatus.setText(playerName + ", you WON the game!");
                result = "WIN";
                break;

            case GREEN:
                lblStatus.setText("Game is over, AI has won the game!");
                result = "LOSE";
                break;

            default:
                lblStatus.setText("Game is tied!");
                result = "DRAW";
        }

        ScoreCardManager.updateScore(playerName, result, moveCount);

        /* ===== Highlight ===== */
        /* ===== Highlight Winning Discs ===== */

        if (winner.getWinningPiece() != Piece.EMPTY) {

            int colStep = Integer.signum(winner.getCol2() - winner.getCol1());
            int rowStep = Integer.signum(winner.getRow2() - winner.getRow1());

            int c = winner.getCol1();
            int r = winner.getRow1();

            for (int i = 0; i < 4; i++) {

                VBox vCol = (VBox) grpCols.lookup("#col" + c);

                int uiIndex = vCol.getChildren().size() - 1 - r;

                if (uiIndex >= 0 && uiIndex < vCol.getChildren().size()) {

                    Circle circle = (Circle) vCol.getChildren().get(uiIndex);

                    circle.getStyleClass().add("winning-circle");
                }

                c += colStep;
                r += rowStep;
            }
        }
        
        
    }

    /* ================= PLAY AGAIN ================= */

    public void btnPlayAgainOnAction(ActionEvent e) {

        initializeGame();

        pneOver.setVisible(false);
        lblStatus.setText("LET'S PLAY!");

        grpCols.getChildren().forEach(n -> {
            VBox v = (VBox) n;
            v.getChildren().clear();
        });

        if (root.lookup("#rectOverlay") != null)
            root.getChildren().remove(root.lookup("#rectOverlay"));
    }

    /* ================= SCOREBOARD ================= */

    public void btnScorecardOnAction(ActionEvent e) throws IOException {

        Stage stage = new Stage();

        stage.setScene(new Scene(
                FXMLLoader.load(getClass().getResource("/view/ScoreBoard.fxml"))
        ));

        stage.setTitle("Scoreboard");
        stage.setResizable(false);
        stage.show();
    }

    /* ================= HINT ================= */

    public void btnHintOnAction(ActionEvent e) {

        AiPlayer ai = (AiPlayer) aiPlayer;

        int col = ai.getBestHintMove();

        lblStatus.setText("Hint: Try column " + (col + 1));
    }

    /* ================= UNDO ================= */

    public void btnUndoOnAction(ActionEvent e) {

        if (moveHistory.size() < 2) return;

        // remove AI + player move
        removeLastDisc();
        removeLastDisc();

        // rebuild internal board state
        rebuildBoard();

        isGameOver = false;
        isAiPlaying = false;

        pneOver.setVisible(false);

        // remove highlight rectangle
        if (root.lookup("#rectOverlay") != null) {
            root.getChildren().remove(root.lookup("#rectOverlay"));
        }

        // remove diagonal highlights
        grpCols.getChildren().forEach(n -> {
            VBox v = (VBox) n;
            v.getChildren().forEach(node -> {
                if (node instanceof Circle) {
                    node.getStyleClass().remove("winning-circle");
                }
            });
        });

        lblStatus.getStyleClass().clear();
        lblStatus.setText(playerName + ", it is your turn!");
    }
    /* ================= REMOVE DISC ================= */

    private void removeLastDisc() {

        int col = moveHistory.pop();

        VBox vCol = (VBox) grpCols.getChildren().get(col);

        if (!vCol.getChildren().isEmpty()) {

            // remove UI disc only
            vCol.getChildren().remove(0);

            moveCount--;
        }
    }

    /* ================= REBUILD BOARD ================= */

    private void rebuildBoard() {

        BoardImpl boardImpl = (BoardImpl) board;

        // clear board
        for (int c = 0; c < Board.NUM_OF_COLS; c++) {
            for (int r = 0; r < Board.NUM_OF_ROWS; r++) {
                boardImpl.updateMove(c, r, Piece.EMPTY);
            }
        }

        // rebuild board from UI discs
        for (int col = 0; col < grpCols.getChildren().size(); col++) {

            VBox vCol = (VBox) grpCols.getChildren().get(col);

            for (int i = 0; i < vCol.getChildren().size(); i++) {

                Circle circle = (Circle) vCol.getChildren().get(i);

                Piece piece;

                if (circle.getStyleClass().contains("circle-human")) {
                    piece = Piece.BLUE;
                } else {
                    piece = Piece.GREEN;
                }

                int row = vCol.getChildren().size() - 1 - i;

                boardImpl.updateMove(col, row, piece);
            }
        }
    }
}