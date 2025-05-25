package com.example.treasurehuntgame.game;

public class Trap {
    public int x, y;
    public boolean triggered;

    public Trap(int x, int y) {
        this.x = x;
        this.y = y;
        this.triggered = false;
    }
}