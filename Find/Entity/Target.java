package com.mygdx.game.Find.Entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Target extends Image {
    // Класс для создания цели с нужными переменнами
    Texture img;
    private float lootSize;
    private Rectangle helpRec, selfRec;
    private Polygon poly;
    public Target(Texture image, float recWidth, float recHeight){
        super(image);
        helpRec = new Rectangle();
        helpRec.setSize(recWidth, recHeight);
        img = image;
        lootSize = 45;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }


    public float getLootSize() {
        return lootSize;
    }

    public void setLootSize(float lootSize) {
        this.lootSize = lootSize;
    }

    public Rectangle getHelpRec() {
        return helpRec;
    }

    public Polygon getPoly() {
        return poly;
    }

    public void setPoly(Polygon poly) {
        this.poly = poly;
    }

    public Rectangle getSelfRec() {
        return selfRec;
    }

    public void setSelfRec(Rectangle selfRec) {
        this.selfRec = selfRec;
        poly = new Polygon(new float[]{selfRec.x, selfRec.y,
                selfRec.x, selfRec.y + selfRec.height,
                selfRec.x + selfRec.width, selfRec.y + selfRec.height,
                selfRec.x + selfRec.width, selfRec.y});
        poly.setOrigin(this.getWidth() / 2, this.getHeight() / 2);
    }

    public float getCenterX(){
        return getX() + getWidth() / 2;
    }

    public float getCenterY(){
        return getY() + getHeight() / 2;
    }
}
