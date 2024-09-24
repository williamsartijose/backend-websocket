package com.cursochat.ws.handler;

import com.cursochat.ws.classes.Comunication;
import com.cursochat.ws.classes.Monsters;
import com.cursochat.ws.classes.Player;
import com.cursochat.ws.classes.GameMap; // Nova classe de mapa
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

    private final Map<String, Player> playersConnected = new ConcurrentHashMap<>();
    private final Map<String, Monsters> monsterCreated = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();


    // Definir um mapa inicial
    private GameMap gameMap;

    @PostConstruct
    public void init() throws IOException {
        System.out.println("Servidor subiu!");
        String[][] initialMap = generateInitialMap(); // Método para gerar o mapa inicial
        gameMap = new GameMap(initialMap);
        respawnMonsters();
    }
    private void respawnMonsters() throws IOException {
        createMonster("Rat", 20, 0, 2, 2);
    }

    private String[][] generateInitialMap() {
        System.out.println("Gerando mapa inicial");
        // Aqui geramos um mapa 20x20 simples como exemplo (0 = caminho livre, 1 = obstáculo)
        return new String[][] {
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"}
        };
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        System.out.println("[afterConnectionEstablished] session id " + session.getId());

        // Enviar o mapa para o jogador recém-conectado
        broadcastMapUpdate();

        // Broadcast de jogadores para o novo jogador
        broadcastPlayers();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        System.out.println("[handleTextMessage] message " + message.getPayload());

        Comunication comunication = objectMapper.readValue(message.getPayload(), Comunication.class);

        if (comunication.getAction().equals("LOG_IN")) {
            logInPlayer(comunication, session);
            broadcastMapUpdate();
        } else if (comunication.getAction().equals("UPDATE_POSITION")) {
            updatePosition(comunication, session);
            broadcastMonster();
        }else if(comunication.getAction().equals("MAP_UPDATE")){
            gameMap = new GameMap(comunication.getMap());
            broadcastMapUpdate();
        }else if(comunication.getAction().equals("SCORE_UPDATE")){
            scoreUpdate(comunication.getColor());
            updatePosition(comunication, session);
        }else if(comunication.getAction().equals("REDIRECT_TO_RANKING")){
            broadcastRedirect();
        }
    }

    private void broadcastRedirect() throws IOException {
        String redirectMessage = "REDIRECT_TO_RANKING";
        String jsonString = "{\"action\":\"" + redirectMessage + "\"}";

        List<Player> playerList = new ArrayList<>(playersConnected.values());
        for (Player player : playerList) {
            if (player.getSession().isOpen()) {
                player.getSession().sendMessage(new TextMessage("messageRanking=" + jsonString));
            }
        }
    }

    private void scoreUpdate(String color){
        playersConnected.forEach((index, item)->{
            if (item.getColor().equals(color)){
                if(item.getScore()>0){
                    item.setScore(item.getScore() - 1);
                }
            }
        });
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
        // Matriz de cores
        String[] colors = {"#FF5733", "#33FF57", "#3357FF", "#FF33A1", "#A133FF", "#FF33B2", "#FF8C33", "#8CFF33"};

        // Sorteia uma cor aleatória
        String color = getRandomUniqueColor(List.of(colors));

        String idPlayer = UUID.randomUUID().toString();

        Player player = new Player(idPlayer, playerName, positionX, positionY, score, color, session);
        playersConnected.put(session.getId(), player);

        // Envia a lista de jogadores atualizada para todos os clientes
        broadcastPlayers();
        broadcastPlayersYou(idPlayer);
    }

    public static String getRandomUniqueColor(List<String> colors) {
        // Verifica se a lista está vazia
        if (colors.isEmpty()) {
            return null; // Retorna null se não houver mais cores disponíveis
        }

        // Garante que a lista seja uma ArrayList mutável para permitir a remoção de elementos
        if (!(colors instanceof ArrayList)) {
            colors = new ArrayList<>(colors); // Converte para ArrayList se não for mutável
        }

        Random random = new Random();
        int randomIndex = random.nextInt(colors.size());  // Gera um índice aleatório
        String selectedColor = colors.get(randomIndex);   // Pega a cor sorteada
        colors.remove(randomIndex);  // Remove a cor sorteada da lista para não ser repetida

        return selectedColor;
    }


    private void broadcastPlayersYou(String idPlayer) throws IOException {
        List<Player> playerList = new ArrayList<>(playersConnected.values());

        String playerListJson = objectMapper.writeValueAsString(playerList);

        for (Player player : playerList) {
            if (player.getSession().isOpen()) {
                if(player.getId().equals(idPlayer)){
                    String playerYou = objectMapper.writeValueAsString(player);
                    player.getSession().sendMessage(new TextMessage("playersYou=" + playerYou));
                }
            }
        }
    }
    // Função para sortear uma cor aleatória
    public static String getRandomColor(String[] colors) {
        Random random = new Random();
        int randomIndex = random.nextInt(colors.length); // Gera um índice aleatório
        return colors[randomIndex]; // Retorna a cor sorteada
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
                player.getSession().sendMessage(new TextMessage("players=" + playerListJson));
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

    // Método para enviar o mapa para o jogador
    private void broadcastMapUpdate() throws IOException {
        String mapUpdate = objectMapper.writeValueAsString( gameMap.getMap());

        // Iterar sobre os jogadores conectados e enviar a mensagem de atualização de mapa
        for (String sessionId : playersConnected.keySet()) {
            WebSocketSession session = playersConnected.get(sessionId).getSession(); // Recuperar a WebSocketSession
            if (session.isOpen()) { // Verificar se a sessão ainda está aberta antes de enviar a mensagem
                session.sendMessage(new TextMessage("map="+mapUpdate));
            }
        }
    }
}
