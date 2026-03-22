package lk.ijse.dep.service;

public class PlayerScore {

    private String playerName;
    private int gamesPlayed;
    private int wins;
    private int draws;
    private int losses;
    private int bestMoves;

    public PlayerScore(String playerName, int gamesPlayed,
                       int wins, int draws, int losses, int bestMoves) {
        this.playerName = playerName;
        this.gamesPlayed = gamesPlayed;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
        this.bestMoves = bestMoves;
    }

    public String getPlayerName() { return playerName; }
    public int getGamesPlayed() { return gamesPlayed; }
    public int getWins() { return wins; }
    public int getDraws() { return draws; }
    public int getLosses() { return losses; }
    public int getBestMoves() { return bestMoves; }

    public void setGamesPlayed(int v) { gamesPlayed = v; }
    public void setWins(int v) { wins = v; }
    public void setDraws(int v) { draws = v; }
    public void setLosses(int v) { losses = v; }
    public void setBestMoves(int v) { bestMoves = v; }

}
