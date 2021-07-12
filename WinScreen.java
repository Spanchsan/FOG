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
import com.badlogic.gdx.utils.Align;
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

public class WinScreen implements Screen {
    FOG game;
    int playerScore;
    int row_height, col_width;
    Skin skin, skinOptional;
    Label scoreLbl, winLbl;
    Music winSound;
    int screenWidth, screenHeight;
    Stage stage;
    TextButton backBtn, retryBtn;
    private Preferences preferences;
    private int difficulty;
    private int additionalPoints;
    float ratW, ratH;
    public WinScreen(final FOG _game, int score, final int dif, int enemyDestroyed, final int gameMode){
        game = _game;
        ratW = (float)Gdx.graphics.getWidth() / 1080;
        ratH = (float)Gdx.graphics.getHeight() / 1920;
        playerScore = score;
        difficulty = dif;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        preferences = Gdx.app.getPreferences("My Preferences");
        int pastPoints = preferences.getInteger("points");
        if(gameMode == 0) {
             additionalPoints = (difficulty + 1) * 70;
        } else if(gameMode == 1){
             additionalPoints = (difficulty + 1) * 20;
        } else if(gameMode == 2){
            additionalPoints = (difficulty + 1) * 50;
        }
        preferences.putInteger("points", pastPoints + playerScore + additionalPoints);
        if(gameMode == 0) {
            int pastEnemyDestroyed = preferences.getInteger("enemyDestroyed");
            preferences.putInteger("enemyDestroyed", pastEnemyDestroyed + enemyDestroyed);
        } else if(gameMode == 1){
            int pastCrystalDestroyed = preferences.getInteger("crystalDestroyed");
            preferences.putInteger("crystalDestroyed", pastCrystalDestroyed + enemyDestroyed);
        } else if(gameMode == 2){
            int pastFogsLooted = preferences.getInteger("fogsLooted");
            preferences.putInteger("fogsLooted", pastFogsLooted + enemyDestroyed);
        }
        preferences.flush();
        row_height = Gdx.graphics.getHeight() / 12;
        col_width = Gdx.graphics.getWidth() / 12;
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        skin = new Skin(Gdx.files.internal("skinHolo/uiskin.json"));
        skinOptional = new Skin(Gdx.files.internal("skin/glassy-ui.json"));
        //Win Lbl
        Label textWinLbl = new Label("WIN", skinOptional, "black");
        textWinLbl.setSize(col_width * 1, row_height * 1);
        textWinLbl.setFontScale(6 * ratW, 6 * ratH);
        textWinLbl.setAlignment(Align.center);
        textWinLbl.setPosition(screenWidth - col_width * 6.5f, screenHeight - row_height * 1);
        stage.addActor(textWinLbl);
        if(gameMode == 0) {
            winLbl = new Label("You Destroyed " + enemyDestroyed + " enemy",
                    skinOptional, "black");
        } else if(gameMode == 1){
            winLbl = new Label("You Drilled " + enemyDestroyed + " crystal",
                    skinOptional, "black");
        } else if(gameMode == 2){
            winLbl = new Label("You looted " + enemyDestroyed + " fogs",
                    skinOptional, "black");
        }
        winLbl.setSize(col_width * 3, row_height * 3);
        winLbl.setFontScale(4 * ratW, 4 * ratH);
        winLbl.setPosition(screenWidth - col_width * 11, screenHeight - row_height * 4);
        stage.addActor(winLbl);
        //Score lbl
        scoreLbl = new Label("Your Game Score:\n      " + playerScore + "(+" + additionalPoints + ")", skinOptional, "black");
        scoreLbl.setSize(col_width * 2, row_height * 2);
        scoreLbl.setFontScale(5 * ratW, 5 * ratH);
        scoreLbl.setPosition(screenWidth - col_width * 11, screenHeight - row_height * 5);
        stage.addActor(scoreLbl);
        //Retry Button
        retryBtn = new TextButton("Retry", skin);
        retryBtn.setSize(col_width * 6, row_height * 2);
        retryBtn.getLabel().setFontScale(6 * ratW, 6 * ratH);
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
        backBtn.getLabel().setFontScale(6 * ratW, 6 * ratH);
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
        winSound = Gdx.audio.newMusic(Gdx.files.internal("winsound.mp3"));
        winSound.setLooping(false);
        winSound.play();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.getBatch().begin();
        stage.act();
        stage.draw();
        game.getBatch().end();
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
         winSound.dispose();
    }

}
