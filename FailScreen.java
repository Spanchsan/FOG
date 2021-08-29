package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.Find.FindEasy;
import com.mygdx.game.Find.FindHard;
import com.mygdx.game.Find.FindNormal;
import com.mygdx.game.Object.ObjectEasy;
import com.mygdx.game.Object.ObjectHard;
import com.mygdx.game.Object.ObjectNormal;
import com.mygdx.game.Gun.GunEasy;
import com.mygdx.game.Gun.GunHard;
import com.mygdx.game.Gun.GunNormal;

public class FailScreen implements Screen {
    //Экран при проигрише
    FOG game;
    //Обьявление переменных
    int playerScore;
    int row_height, col_width;
    Skin skin, skinOptional;
    Label scoreLbl;
    Music loseSound;
    int screenWidth, screenHeight;
    Stage stage;
    TextButton backBtn, retryBtn;
    private Preferences preferences;
    float ratW, ratH;
    //Инициализация переменных
    public FailScreen(final FOG _game, int score, final int gameMode, int destroyed, final int dif){
        game = _game;
        //Переменные для масштабирования
        ratW = (float)Gdx.graphics.getWidth() / 1080;
        ratH = (float)Gdx.graphics.getHeight() / 1920;
        playerScore = score;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        //Изменение поинтов игрока
        preferences = Gdx.app.getPreferences("My Preferences");
        int pastPoints = preferences.getInteger("points");
        preferences.putInteger("points", pastPoints + playerScore);
        //Изменение данных в зависимости от режима, данные для стаистика
        if(gameMode == 0){
            int lastDestroyed = preferences.getInteger("enemyDestroyed");
            preferences.putInteger("enemyDestroyed", lastDestroyed + destroyed);
        } else if(gameMode == 1){
            int lastDestroyed = preferences.getInteger("crystalDestroyed");
            preferences.putInteger("crystalDestroyed", lastDestroyed + destroyed);
        } else if(gameMode == 2){
            int pastFogsLooted = preferences.getInteger("fogsLooted");
            preferences.putInteger("fogsLooted", pastFogsLooted + destroyed);
        }
        preferences.flush();
        //Инициализация кнопок
        row_height = Gdx.graphics.getHeight() / 12;
        col_width = Gdx.graphics.getWidth() / 12;
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        skin = new Skin(Gdx.files.internal("skinHolo/uiskin.json"));
        skinOptional = new Skin(Gdx.files.internal("skin/glassy-ui.json"));
        //Score lbl
        scoreLbl = new Label("You Failed\nYour Score:\n      " + playerScore, skinOptional, "black");
        scoreLbl.setSize(col_width * 2, row_height * 2);
        scoreLbl.setFontScale(3 * ratW, 3 * ratH);
        scoreLbl.setPosition(screenWidth - col_width * 9, screenHeight - row_height * 3);
        stage.addActor(scoreLbl);
        //Retry Button
        retryBtn = new TextButton("Retry", skin);
        retryBtn.setSize(col_width * 6, row_height * 2);
        retryBtn.getLabel().setFontScale(3 * ratW, 3 * ratH);
        retryBtn.getLabel().setColor(0,0,0,1);
        retryBtn.setPosition(screenWidth - col_width * 9, screenHeight - row_height * 6.8f);
        retryBtn.addListener(new InputListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(gameMode == 0){
                    if(dif == 0) {
                        game.setScreen(new GunEasy(game));
                        dispose();
                    } else if(dif == 1){
                        game.setScreen(new GunNormal(game));
                        dispose();
                    } else if(dif == 2){
                        game.setScreen(new GunHard(game));
                        dispose();
                    }
                } else if(gameMode == 1){
                    if(dif == 0) {
                        game.setScreen(new ObjectEasy(game));
                        dispose();
                    } else if(dif == 1){
                        game.setScreen(new ObjectNormal(game));
                        dispose();
                    } else if(dif == 2){
                        game.setScreen(new ObjectHard(game));
                        dispose();
                    }
                } else if(gameMode == 2){
                    if(dif == 0) {
                        game.setScreen(new FindEasy(game));
                        dispose();
                    } else if(dif == 1){
                        game.setScreen(new FindNormal(game));
                        dispose();
                    } else if(dif == 2){
                        game.setScreen(new FindHard(game));
                        dispose();
                    }

                }
                return false;
            }
        });
        stage.addActor(retryBtn);

        //Back Button
        backBtn = new TextButton("Back", skin);
        backBtn.setSize(col_width * 6, row_height * 2);
        Label backBtnLabel = new Label("Back", skinOptional, "black");
        backBtn.getLabel().getStyle().font = backBtnLabel.getStyle().font;
        backBtn.getLabel().setFontScale(3 * ratW, 3 * ratH);
        backBtn.getLabel().setColor(0,0,0,1);
        backBtn.setPosition(screenWidth - col_width * 9, screenHeight - row_height * 9);
        backBtn.addListener(new InputListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new ChooseScreen(game));
                dispose();
                return false;
            }
        });
        stage.addActor(backBtn);
    }
    @Override
    public void show() {
        //Инициализация музыки при проигрыше
        loseSound = Gdx.audio.newMusic(Gdx.files.internal("loseSound.wav"));
        loseSound.setLooping(false);
        loseSound.play();
    }
    //Отрисовка
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        stage.act();
        stage.draw();
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
      loseSound.dispose();
    }
}
