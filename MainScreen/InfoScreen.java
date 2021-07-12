package com.mygdx.game.MainScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.FOG;


public class InfoScreen implements Screen {
    //Экран с обьяснением режимов
    //Обьявление переменных
    FOG game;
    float screenWidth, screenHeight;
    float col_width, row_height;
    Label GMLbl, textLbl;
    ImageButton leftBtn, rightBtn, homeBtn;
    byte selectedGameMode = 1;
    Skin skin;
    Stage stage;
    float ratW, ratH;

    public InfoScreen(FOG _game){
        this.game = _game;
        //Инициализация переменных для масштабирования
        ratW = (float)Gdx.graphics.getWidth() / 1080;
        ratH = (float)Gdx.graphics.getHeight() / 1920;
        screenHeight = Gdx.graphics.getHeight();
        screenWidth = Gdx.graphics.getWidth();
        col_width = screenWidth / 12;
        row_height = screenHeight / 12;
        skin = new Skin(Gdx.files.internal("skinHolo/uiskin.json"));
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        //Инициализация кнопок и рисунков
        //rightImageBtn
        rightBtn = new ImageButton(skin, "colored");
        rightBtn.setChecked(false);
        Drawable rightBtnBackground = rightBtn.getBackground();
        ImageButton.ImageButtonStyle rightBtnStyle = new ImageButton.ImageButtonStyle();
        rightBtnStyle.imageUp =  new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("rightarrow.png"))));
        rightBtnStyle.imageDown = new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("rightarrow.png"))));
        rightBtnStyle.up = rightBtnBackground;
        rightBtnStyle.down = rightBtnBackground;
        rightBtn.setStyle(rightBtnStyle);
        rightBtn.setSize((float)(col_width * 1.4), (float)(row_height * 1.5));
        rightBtn.setPosition((float)(screenWidth - col_width * 3.4), (float)(row_height ));
        rightBtn.addListener(new InputListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(selectedGameMode == 3){
                    selectedGameMode = 1;
                } else {
                    selectedGameMode ++;
                }
                changeText();
                return false;
            }
        });
        stage.addActor(rightBtn);
        //leftImageBtn
        leftBtn = new ImageButton(skin, "colored");
        leftBtn.setChecked(false);
        Drawable leftBtnBackground = leftBtn.getBackground();
        ImageButton.ImageButtonStyle leftBtnStyle = new ImageButton.ImageButtonStyle();
        leftBtnStyle.imageUp = new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("leftarrow.png"))));
        leftBtnStyle.imageDown =  new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("leftarrow.png"))));
        leftBtnStyle.up = leftBtnBackground;
        leftBtnStyle.down = leftBtnBackground;
        leftBtn.setStyle(leftBtnStyle);
        leftBtn.setSize((float)(col_width * 1.4), (float)(row_height * 1.5));
        leftBtn.setPosition((float)(screenWidth - col_width * 10), (float)( row_height ));
        leftBtn.addListener(new InputListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(selectedGameMode == 1){
                    selectedGameMode = 3;
                } else {
                    selectedGameMode--;
                }
                changeText();
                return false;
            }
        });
        stage.addActor(leftBtn);
        //Home Button
        homeBtn = new ImageButton(skin);
        Drawable homeBtnBackground = homeBtn.getBackground();
        ImageButton.ImageButtonStyle homeBtnStyle = new ImageButton.ImageButtonStyle();
        homeBtnStyle.imageUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("home.png"))));
        homeBtnStyle.imageDown = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("home.png"))));
        homeBtnStyle.up = homeBtnBackground;
        homeBtnStyle.down = homeBtnBackground;
        homeBtn.setStyle(homeBtnStyle);
        homeBtn.setSize((float)(col_width * 1.4), (float)(row_height * 1.4));
        homeBtn.setPosition((float)(col_width * 0.2), (float)(row_height * 0.2));
        homeBtn.addListener(new InputListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new StartScreen(game));
                dispose();
                return false;
            }
        });
        stage.addActor(homeBtn);
        //Game Mode Label
        GMLbl = new Label("Gun", skin);
        GMLbl.setFontScale(5 * ratW, 5 * ratH);
        GMLbl.setAlignment(Align.center);
        GMLbl.setSize(col_width, row_height);
        GMLbl.setPosition(col_width * 5.5f, screenHeight - row_height * 2.5f);
        stage.addActor(GMLbl);
        //Description of game mode Label
        textLbl = new Label("", skin);
        textLbl.setFontScale(2.8f * ratW, 2.8f * ratH);
        textLbl.setAlignment(Align.top);
        textLbl.setSize(col_width * 12, textLbl.getPrefHeight());
        textLbl.setPosition(col_width * 0.1f, screenHeight - row_height * 3.6f);
        stage.addActor(textLbl);
        changeText();
    }
    //Фукнция для изменение текста при изменении режимов
    private void changeText(){
        if(selectedGameMode == 1){
            GMLbl.setText("GUN");
            textLbl.setText("Gun is game mode about shooting enemies\n" +
                    "In Easy difficulty there is a one \n enemy that shoots, so you need to destroy\n" +
                    "this enemies to defeat this invade\n" +
                    "In Normal difficulty there is a two\n row of enemies that shoots you\n" +
                    "One vertical and horizontal, you need to\n evade from their bullets and destroy them\n" +
                    "In Hard difficulty there is like in \nNormal difficulty two rows of enemies\n" +
                    "But it harder and more complicated\n" +
                    "GOOD LUCK!");
        } else if(selectedGameMode == 2){
            GMLbl.setText("Object");
            textLbl.setText("Object is game mode about \ndestroying crystals and achieving goal\n" +
                    "In Easy difficulty there is fast cooldown \n of respawning of crystals \n" +
                    "and large margin before melting of crystals\n so it's easy to mine \n" +
                    "In Normal difficulty there is a harder\n version of easy difficulty\n" +
                    "Slower cooldown of respawn\n faster melting of crystals before it mined\n" +
                    "Hard difficulty it's more \nharder version of Normal\n" +
                    "GOOD LUCK!");
        } else {
            GMLbl.setText("FIND");
            textLbl.setText("Find is game mode about \nfinding Nexus and saving information from it\n" +
                     "There is text that shows range \nfor the Nexus from your position\n" +
                            "In Easy, Normal, Hard difficulties\n Different time and goal\n" +
                    "GOOD LUCK!");
        }
    }

    @Override
    public void show() {

    }
    //Отрисовка
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1,1,1,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
        game.getBatch().begin();
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

    }
}
