package com.mygdx.game.Object.Entity;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

public class Crystal extends Rectangle {
    //Класс для кристалов с нужными переменнами
    float hp;
    long spawnTime;
    Polygon poly;

    public long getSpawnTime() {
        return spawnTime;
    }

    public void setSpawnTime(long spawnTime) {
        this.spawnTime = spawnTime;
    }

    public Crystal(int hpMax){
        hp = hpMax;
        poly = new Polygon(new float[]{this.x, this.y,
                this.x, this.y +this.height,
                this.x + this.width, this.y +this.height,
                this.x + this.width, this.y});
        poly.setOrigin(this.getWidth() / 2, this.getHeight() / 2);

    }

    public float getHp() {
        return hp;
    }

    public void setHp(float hp) {
        this.hp = hp;
    }

    public Polygon getPoly() {
        return poly;
    }

    public void setPoly(Polygon poly) {
        this.poly = poly;
    }
}
