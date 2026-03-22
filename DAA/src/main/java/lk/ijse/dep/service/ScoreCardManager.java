package lk.ijse.dep.service;

import java.io.*;
import java.util.*;

public class ScoreCardManager {

    private static final String FILE_PATH =
            System.getProperty("user.home") + "/connect4-scorecard.csv";

    /* ===============================
       LOAD SCORES FROM FILE
       =============================== */
    public static List<PlayerScore> loadScores() {

        List<PlayerScore> list = new ArrayList<>();
        File file = new File(FILE_PATH);

        // Create file if not exists
        if (!file.exists()) {
            createEmptyFile();
            return list;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line = br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                String[] d = line.split(",");

                list.add(new PlayerScore(
                        d[0],
                        Integer.parseInt(d[1]),
                        Integer.parseInt(d[2]),
                        Integer.parseInt(d[3]),
                        Integer.parseInt(d[4]),
                        Integer.parseInt(d[5])
                ));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    /* ===============================
       UPDATE SCORE (NO DUPLICATES)
       =============================== */
    public static void updateScore(String name, String result, int moves) {

        List<PlayerScore> scores = loadScores();
        PlayerScore player = null;

        for (PlayerScore ps : scores) {
            if (ps.getPlayerName().equalsIgnoreCase(name)) {
                player = ps;
                break;
            }
        }

        if (player == null) {
            player = new PlayerScore(name, 0, 0, 0, 0, moves);
            scores.add(player);
        }

        player.setGamesPlayed(player.getGamesPlayed() + 1);

        if (result.equals("WIN")) player.setWins(player.getWins() + 1);
        else if (result.equals("DRAW")) player.setDraws(player.getDraws() + 1);
        else player.setLosses(player.getLosses() + 1);

        player.setBestMoves(Math.min(player.getBestMoves(), moves));

        // 🔥 MERGE SORT USED HERE
        mergeSort(scores, 0, scores.size() - 1);

        saveScores(scores);
    }

    /* ===============================
       MERGE SORT (DAA)
       =============================== */
    private static void mergeSort(List<PlayerScore> list, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(list, left, mid);
            mergeSort(list, mid + 1, right);
            merge(list, left, mid, right);
        }
    }

    private static void merge(List<PlayerScore> list, int left, int mid, int right) {

        List<PlayerScore> L = new ArrayList<>(list.subList(left, mid + 1));
        List<PlayerScore> R = new ArrayList<>(list.subList(mid + 1, right + 1));

        int i = 0, j = 0, k = left;

        while (i < L.size() && j < R.size()) {
            if (compare(L.get(i), R.get(j)) <= 0) {
                list.set(k++, L.get(i++));
            } else {
                list.set(k++, R.get(j++));
            }
        }

        while (i < L.size()) list.set(k++, L.get(i++));
        while (j < R.size()) list.set(k++, R.get(j++));
    }

    /* ===============================
       COMPARISON LOGIC
       =============================== */
    private static int compare(PlayerScore a, PlayerScore b) {

        // 1️⃣ Best moves (ascending)
        if (a.getBestMoves() != b.getBestMoves()) {
            return Integer.compare(a.getBestMoves(), b.getBestMoves());
        }

        // 2️⃣ Wins (descending)
        if (a.getWins() != b.getWins()) {
            return Integer.compare(b.getWins(), a.getWins());
        }

        // 3️⃣ Player name (A–Z)
        return a.getPlayerName().compareToIgnoreCase(b.getPlayerName());
    }

    /* ===============================
       SAVE TO FILE
       =============================== */
    private static void saveScores(List<PlayerScore> scores) {

        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {

            pw.println("playerName,gamesPlayed,wins,draws,losses,bestMoves");

            for (PlayerScore ps : scores) {
                pw.println(
                        ps.getPlayerName() + "," +
                        ps.getGamesPlayed() + "," +
                        ps.getWins() + "," +
                        ps.getDraws() + "," +
                        ps.getLosses() + "," +
                        ps.getBestMoves()
                );
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createEmptyFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            pw.println("playerName,gamesPlayed,wins,draws,losses,bestMoves");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void clearScores() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            pw.println("playerName,gamesPlayed,wins,draws,losses,bestMoves");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
