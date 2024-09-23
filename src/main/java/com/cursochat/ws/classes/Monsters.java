package com.cursochat.ws.classes;

public class Monsters {
    private String id;
    private String nome;
    private int vida;
    private int mana;
    private int positionX;
    private int positionY;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public int getVida() {
        return vida;
    }

    public void setVida(int vida) {
        this.vida = vida;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    public Monsters(){
    }
    public Monsters(String id, String nome, int vida, int mana, int positionX, int positionY) {
        this.id = id;
        this.nome = nome;
        this.vida = vida;
        this.mana = mana;
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }
}