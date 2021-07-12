package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.MainScreen.StartScreen;

public class FOG extends Game {
    // Основной игровой класс, для перемещению между экранами и отрисовки текстов, фото

    protected SpriteBatch batch;
    protected BitmapFont font;
    //Инициализация элементов
    @Override
    public void create() {
      batch = new SpriteBatch();
      font = new BitmapFont();
      this.setScreen(new StartScreen(this));
    }


    public SpriteBatch getBatch() {
        return batch;
    }

    public BitmapFont getFont() {
        return font;
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        font.dispose();
    }
}
