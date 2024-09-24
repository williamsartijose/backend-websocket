package com.cursochat.ws.classes;

public class GameMap {
    private String[][] map;

    public GameMap(String[][] map) {
        this.map = map;
    }

    public String[][] getMap() {
        return map;
    }

    public void setMap(String[][] map) {
        this.map = map;
    }
}
