package com.mygdx.game.gameUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;
import java.util.List;

public class Joystick extends Actor {
    //Класс для Джойстика, главная структура была взята с видеоурока
    private Texture circle;
    private Texture cursorCircle;
    private boolean isTouched = false;
    private float radius = 200;
    private static final float RADIUS_CURSOR = 40;
    private float valueX, valueY;
    private float inverseRadius;

    private float cursorX = 0, cursorY = 0;

    private List<JoystickChangedListener> listeners = new ArrayList<>();

    public void addJoystickChangedListener(JoystickChangedListener listener){
        listeners.add(listener);
    }

    public void removeJoystickChangedListener(JoystickChangedListener listener){
        listeners.remove(listener);
    }

    public void clearJoystickChangedListener(JoystickChangedListener listener){
        listeners.clear();
    }

    public void handleChangedListener(){
        for(JoystickChangedListener listener: listeners){
            listener.changed(valueX, valueY);
        }
    }
    //Инициализация фото для джйостика и слушателей при изменение положения
    public Joystick(){
        circle = new Texture(Gdx.files.internal("joystickCircle.png"));
        cursorCircle = new Texture(Gdx.files.internal("innerCircle.png"));
        setDefaultSize();
        addListener(new JoystickInputListener(this));
    }

    public Joystick(float ratioWidth, float ratioHeight){
        this();
        setHeight(400 * ratioHeight);
        setWidth(400 * ratioWidth);
    }

    public void isTouchingByUser(){
        isTouched = true;
    }

    public void isNotTouchingByUser(){
        isTouched = false;
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        Actor actor = super.hit(x,y,touchable);
        if(actor == null){
            return null;
        } else {
            float marginX = x - radius;
            float marginY = y - radius;

            if(marginX * marginX + marginY * marginY <= radius * radius){
                return this;
            } else {
                return null;
            }
        }
    }

    public double getRadians(){
        return Math.atan2(valueY, valueX);
    }
    public void cursorChanged(float x, float y){
        float marginX = x - radius;
        float marginY = y - radius;
        float centreFromCentre = (float)Math.sqrt(marginX * marginX + marginY * marginY);
        if(centreFromCentre > radius){
            cursorX = radius / centreFromCentre * marginX;
            cursorY = radius / centreFromCentre * marginY;
        } else {
            cursorX = marginX;
            cursorY = marginY;
        }
        valueX = cursorX * inverseRadius;
        valueY = cursorY * inverseRadius;
    }

    public boolean getJoystickIsTouched(){
        return isTouched;
    }

    public float getValueX() {
        return valueX;
    }

    public double getDegrees(){
        double degree = Math.toDegrees(Math.atan(valueY / valueX));
        return degree;
    }

    public float getValueY() {
        return valueY;
    }

    public void resetCursor(){
        cursorX = 0;
        cursorY = 0;
    }
    private void setDefaultSize(){
        setHeight(400);
        setWidth(400);
        inverseRadius = 1 / radius;
    }

    public void setRadius(float radius){
        this.radius = radius;
        setWidth(radius * 2);
        inverseRadius = 1 / radius;
    }
    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        super.setHeight(width);
        radius = width / 2;
        inverseRadius = 1 / radius;
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        super.setWidth(height);
        radius = height / 2;
        inverseRadius = 1 / radius;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(circle, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        if(isTouched){
           batch.draw(cursorCircle, this.getX() + radius + cursorX - RADIUS_CURSOR,
                   this.getY() + radius - RADIUS_CURSOR + cursorY,
                   2 * RADIUS_CURSOR, 2 * RADIUS_CURSOR );
        } else {
            batch.draw(cursorCircle, this.getX() + radius - RADIUS_CURSOR,
                    this.getY() + radius - RADIUS_CURSOR ,
                    2 * RADIUS_CURSOR, 2 * RADIUS_CURSOR );
        }
    }
}
