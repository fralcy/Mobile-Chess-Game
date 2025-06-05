package com.example.chess_mobile.model.player;

public class Player implements Comparable<Player>{
    private String playerId;
    private String email;
    private String playerName;
    private int matches;
    private int rank;
    private int win;
    private int score;

    public Player() {
    }

    public Player(String email, int matches, String playerId, String playerName, int rank, int score, int win) {
        this.email = email;
        this.matches = matches;
        this.playerId = playerId;
        this.playerName = playerName;
        this.rank = rank;
        this.score = score;
        this.win = win;
    }

    public String getEmail() {
        return email;
    }

    public int getMatches() {
        return matches;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getRank() {
        return rank;
    }

    public int getScore() {
        return score;
    }

    public int getWin() {
        return win;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMatches(int matches) {
        this.matches = matches;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setWin(int win) {
        this.win = win;
    }

    @Override
    public int compareTo(Player player) {
        return 0;
    }
}
