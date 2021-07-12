package com.mygdx.game.gameUtils;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
//Слушатель при нажатия на джойстик
public class JoystickInputListener extends InputListener {

    Joystick joystick;
    public JoystickInputListener(Joystick joystick){

        this.joystick = joystick;
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        joystick.isTouchingByUser();
        joystick.cursorChanged(x, y);
        return true;
    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        joystick.isNotTouchingByUser();
    }

    @Override
    public void touchDragged(InputEvent event, float x, float y, int pointer) {
        joystick.cursorChanged(x, y);
        if(joystick.getJoystickIsTouched()){
            joystick.handleChangedListener();
        }
    }
}
