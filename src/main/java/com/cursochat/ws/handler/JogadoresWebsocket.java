package com.cursochat.ws.handler;

import com.cursochat.ws.classes.Comunication;
import com.cursochat.ws.classes.Monsters;
import com.cursochat.ws.classes.Player;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JogadoresWebsocket extends TextWebSocketHandler {

    // Map de sessionId para Player, incluindo WebSocketSession no Player
    private final Map<String, Player> playersConnected = new ConcurrentHashMap<>();
    private final Map<String, Monsters> monsterCreated = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() throws IOException {
        System.out.println("Servidor subiu!");
        respawnMonsters();
    }

    private void respawnMonsters() throws IOException {
        createMonster("Rat", 20, 0, 2, 2);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("[afterConnectionEstablished] session id " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        System.out.println("[handleTextMessage] message " + message.getPayload());

        Comunication comunication = objectMapper.readValue(message.getPayload(), Comunication.class);

        if (comunication.getAction().equals("LOG_IN")) {
            logInPlayer(comunication, session);
        } else if (comunication.getAction().equals("UPDATE_POSITION")) {
            updatePosition(comunication, session);
            broadcastMonster();
        }
    }

    private void updatePosition(Comunication comunication, WebSocketSession session) {
        Player player = playersConnected.get(session.getId());

        if (player != null) {
            // Atualizar a posição do jogador
            player.setPositionX(comunication.getPlayer().getPositionX());
            player.setPositionY(comunication.getPlayer().getPositionY());
            player.setScore(comunication.getPlayer().getScore());

            System.out.println("Jogador atualizado: " + player.getName() + " Nova posição: X=" + player.getPositionX() + " Y=" + player.getPositionY() + " Score: " + player.getScore());


            // Envia a lista de jogadores atualizada para todos os clientes
            try {
                broadcastPlayers();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Jogador não encontrado para o id da sessão: " + session.getId());
        }
    }

    private void createMonster(String nome, int vida, int mana, int positionX, int positionY) throws IOException {
        String idSession = UUID.randomUUID().toString();
        Monsters monster = new Monsters(idSession, nome, vida, mana, positionX, positionY);
        monsterCreated.put(idSession, monster);

        // Envia a lista de monstros atualizada para todos os clientes
        broadcastMonster();
    }

    private void logInPlayer(Comunication comunication, WebSocketSession session) throws IOException {
        String playerName = comunication.getPlayer().getName();
        int positionX = comunication.getPlayer().getPositionX();
        int positionY = comunication.getPlayer().getPositionY();
        int score = comunication.getPlayer().getScore();

        Player player = new Player(UUID.randomUUID().toString(), playerName, positionX, positionY, score, session);
        playersConnected.put(session.getId(), player);

        // Envia a lista de jogadores atualizada para todos os clientes
        broadcastPlayers();
    }

    // Conexão fechada
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println("[afterConnectionClosed] session id " + session.getId());

        // Remove o jogador quando desconectar
        playersConnected.remove(session.getId());

        // Envia a lista de jogadores atualizada para todos os clientes
        try {
            broadcastPlayers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Jogadores conectados
    private void broadcastPlayers() throws IOException {
        List<Player> playerList = new ArrayList<>(playersConnected.values());

        String playerListJson = objectMapper.writeValueAsString(playerList);

        for (Player player : playerList) {
            if (player.getSession().isOpen()) {
                player.getSession().sendMessage(new TextMessage("players="+playerListJson));
            }
        }
    }

    // Monstros criados
    private void broadcastMonster() throws IOException {
        List<Monsters> monstersList = new ArrayList<>(monsterCreated.values());

        String monsterListJson = objectMapper.writeValueAsString(monstersList);

        // Envia a lista de monstros para todos os jogadores
        for (Player player : playersConnected.values()) {
            if (player.getSession().isOpen()) {
                player.getSession().sendMessage(new TextMessage("monsters=" + monsterListJson));
            }
        }
    }
}
