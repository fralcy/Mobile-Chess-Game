package com.example.chess_mobile.dto.response;

public class PlayerResponse implements Comparable<PlayerResponse>{
    private String playerId;
    private String email;
    private String playerName;
    private int matches;
    private int rank;
    private int win;
    private int score;
    public double getRating() {
        double rating = (double) win / matches;
        return (double) Math.round(rating * 100.0) / 100.0;
    }
    public String getId(){
        return playerId;
    }
    @Override
    public int compareTo(PlayerResponse b) {
        if(this.score>b.score) {
            return -1;
        }
        if(this.score<b.score) {
            return 1;
        }
        return 0;
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
}
