package com.example;

import java.awt.Rectangle;

public abstract class Form extends Rectangle {
    
    public Form(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public abstract void deplacer(int dx, int dy);
    
    public boolean collisionAvec(Form autreForm) {
        return this.intersects(autreForm);
    }
}