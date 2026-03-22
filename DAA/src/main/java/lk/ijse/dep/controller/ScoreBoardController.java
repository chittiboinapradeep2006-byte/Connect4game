package lk.ijse.dep.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.dep.service.PlayerScore;
import lk.ijse.dep.service.ScoreCardManager;

import java.util.List;

public class ScoreBoardController {

    @FXML
    private TableView<PlayerScore> tblScore;

    @FXML
    private TableColumn<PlayerScore, String> colPlayer;

    @FXML
    private TableColumn<PlayerScore, Integer> colPlayed;

    @FXML
    private TableColumn<PlayerScore, Integer> colWins;

    @FXML
    private TableColumn<PlayerScore, Integer> colDraws;

    @FXML
    private TableColumn<PlayerScore, Integer> colLosses;

    @FXML
    private TableColumn<PlayerScore, Integer> colBestMoves;

    @FXML
    public void initialize() {

        colPlayer.setCellValueFactory(
                new PropertyValueFactory<>("playerName"));

        colPlayed.setCellValueFactory(
                new PropertyValueFactory<>("gamesPlayed"));

        colWins.setCellValueFactory(
                new PropertyValueFactory<>("wins"));

        colDraws.setCellValueFactory(
                new PropertyValueFactory<>("draws"));

        colLosses.setCellValueFactory(
                new PropertyValueFactory<>("losses"));

        colBestMoves.setCellValueFactory(
                new PropertyValueFactory<>("bestMoves"));

        // ✅ Load already-sorted data
        List<PlayerScore> scores = ScoreCardManager.loadScores();
        tblScore.getItems().setAll(scores);
    }
    @FXML
    public void btnClearOnAction() {

        ScoreCardManager.clearScores();

        // Clear table UI
        tblScore.getItems().clear();
    }

}
