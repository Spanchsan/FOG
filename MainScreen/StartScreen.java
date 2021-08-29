package com.mygdx.game.MainScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.ChooseScreen;
import com.mygdx.game.FOG;
import com.mygdx.game.MainScreen.InfoScreen;
import com.mygdx.game.MultiPlayer.MainEventScreen;

public class StartScreen implements Screen {
    //Главный экран, который видит пользователь
    //Со всеми кнопками перемещения
    //Обьявление переменных
    FOG game;
    OrthographicCamera camera;
    int screenWidth, screenHeight;

    int row_height;
    int col_width;
    float ratW, ratH;

    ImageButton startBtn, shopBtn, statBtn, infoBtn, ledBtn, settingsBtn, multiBtn;
    Label pointLbl;
    Stage stage;
    Skin skin;

    int points;

    Image backgroundImg;
    private Preferences pref;
    //Инициализация переменных
    public StartScreen(final FOG game){
        this.game = game;
        screenHeight = Gdx.graphics.getHeight();
        screenWidth = Gdx.graphics.getWidth();
        //Перменные предназначение которых масштабирование фото и кнопок
        ratW = (float)screenWidth / 1080;
        ratH = (float)screenHeight / 1920;
        row_height = Gdx.graphics.getHeight() / 12;
        col_width = Gdx.graphics.getWidth() / 12;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth, screenHeight);
        pref = Gdx.app.getPreferences("My Preferences");
        points = pref.getInteger("points");
        pref.flush();
        skin = new Skin(Gdx.files.internal("skinHolo/uiskin.json"));
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        //Конструирование всех кнопок и фото и добавление их в Stage
        //Background Image
        backgroundImg = new Image(new TextureRegionDrawable(new Texture(Gdx.files.internal("Shoot/player/images/startBackground.png"))));
        backgroundImg.setPosition(0, 0);
        backgroundImg.setSize(screenWidth, screenHeight);
        stage.addActor(backgroundImg);
        //Start Button
        startBtn = new ImageButton(skin);
        Drawable startBtnBackground = startBtn.getBackground();
        ImageButton.ImageButtonStyle startBtnStyle = new ImageButton.ImageButtonStyle();
        startBtnStyle.imageUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("start.png"))));
        startBtnStyle.imageDown = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("start.png"))));
        startBtnStyle.up = startBtnBackground;
        startBtnStyle.down = startBtnBackground;
        startBtn.setStyle(startBtnStyle);
        startBtn.setSize((float)(row_height * 2), (float)(col_width * 2));
        startBtn.setPosition(screenWidth / 2, screenHeight - (float)(row_height * 5.2), Align.center);
        startBtn.addListener(new InputListener(){
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
        stage.addActor(startBtn);
        //Points Label
        pointLbl = new Label("Points:" + points, skin, "default");
        pointLbl.setSize(col_width * 2, row_height * 1);
        pointLbl.setAlignment(Align.center);
        pointLbl.setPosition(screenWidth / 2 - col_width * 1, screenHeight - (float)(row_height * 4.5));
        pointLbl.setFontScale(4 * ratW, 4 * ratH);
        stage.addActor(pointLbl);
        //Shop Button
        Drawable shopImg = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("upgradeshop.jpg"))));
        shopBtn = new ImageButton(shopImg);
        shopBtn.setSize((float)(col_width * 1.5) ,(float)( row_height * 1.5));
        shopBtn.setPosition(col_width * 3, screenHeight - row_height * 6, Align.center);
        shopBtn.addListener(new InputListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new ShopScreen(game));
                dispose();
                return false;
            }
        });
        stage.addActor(shopBtn);

        statBtn = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("stat.png")))));
        statBtn.setSize((float)(col_width * 1.5) ,(float)( row_height * 1.5));
        statBtn.setPosition(col_width * 9, screenHeight - row_height * 6, Align.center);
        statBtn.addListener(new InputListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new StatisticsScreen(game));
                dispose();
                return false;
            }
        });
        stage.addActor(statBtn);
        //Info button
        infoBtn = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("info.png")))));
        infoBtn.setSize(col_width * 1.5f, row_height * 1.5f);
        infoBtn.setPosition(col_width * 6, screenHeight - row_height * 6.7f, Align.center);
        infoBtn.addListener(new InputListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
               game.setScreen(new InfoScreen(game));
               dispose();
                return false;
            }
        });
        stage.addActor(infoBtn);
        //Info button
        multiBtn = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("multiplayer.png")))));
        multiBtn.setSize(col_width * 2f, row_height * 2f);
        multiBtn.setPosition(col_width * 6, screenHeight - row_height * 8f, Align.center);
        multiBtn.addListener(new InputListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new MainEventScreen(game));
                dispose();
                return false;
            }
        });
        stage.addActor(multiBtn);
        //LeaderBoard show button
        ledBtn = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("trophy.png")))));
        ledBtn.setSize(col_width * 1.5f, row_height * 1.5f);
        ledBtn.setPosition(col_width * 3, screenHeight - row_height * 7.1f, Align.center);
        ledBtn.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
              game.setScreen(new LeaderboardScreen(game));
              dispose();
              return false;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            }
        });
        stage.addActor(ledBtn);
        //Settings Button
        settingsBtn = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("settings.png")))));
        settingsBtn.setSize(col_width * 1.5f, row_height * 1.5f);
        settingsBtn.setPosition(col_width * 9, screenHeight - row_height * 7f, Align.center);
        settingsBtn.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new SettingsScreen(game));
                dispose();
                return false;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            }
        });
        stage.addActor(settingsBtn);
    }
    @Override
    public void show() {

    }
    //Отрисовка
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1,1,1,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        stage.act();
        stage.draw();
        game.getBatch().setProjectionMatrix(camera.combined);
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
