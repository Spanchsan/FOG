package com.mygdx.game.Gun.Entity;

import com.badlogic.gdx.math.Rectangle;
//Класс для пулей игрока, нужен был для сохранение угла наклона выстрела
public class PBullet extends Rectangle {
    double angle;

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }
}
