package com.cursochat.ws.classes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.socket.WebSocketSession;

public class Player {

    private String id;

    @JsonProperty("name") // Mapeia o campo "name" do JSON
    private String name;

    @JsonProperty("positionX") // Mapeia o campo "positionX" do JSON
    private int position_x;

    @JsonProperty("positionY") // Mapeia o campo "positionY" do JSON
    private int position_y;

    @JsonProperty("score") // Mapeia o campo "positionY" do JSON
    private int score;

    @JsonIgnore // Ignora o campo "session" na serialização
    private WebSocketSession session;

    public Player() {}

    public Player(String id, String name, int position_x, int position_y, int score, WebSocketSession session) {
        this.id = id;
        this.name = name;
        this.position_x = position_x;
        this.position_y = position_y;
        this.score = score;
        this.session = session;
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPositionX() {
        return position_x;
    }

    public void setPositionX(int position_x) {
        this.position_x = position_x;
    }

    public int getPositionY() {
        return position_y;
    }

    public void setPositionY(int position_y) {
        this.position_y = position_y;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public void setSession(WebSocketSession session) {
        this.session = session;
    }
}
