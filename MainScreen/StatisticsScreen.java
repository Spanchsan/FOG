package com.mygdx.game.MainScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.mygdx.game.FOG;
import com.mygdx.game.MainScreen.StartScreen;


public class StatisticsScreen implements Screen {
    //Экран с статистиками с каждого режима
    FOG game;
    //Обьявление переменныз
    int row_height, col_width;
    float screenWidth, screenHeight;
    Image firstImg, statImg;
    Label firstLbl;
    Skin skin;
    Stage stage;
    ImageButton homeBtn, rightBtn, leftBtn;
    int selectedGameMode= 1;
    int enemyDestroyed, crystalDestroyed, fogsLooted;
    private Preferences preferences;
    float ratW, ratH;
    //Инициализация переменных
    public StatisticsScreen(FOG _game){
          game = _game;
        //Переменные для масштабирования
          ratW = (float)Gdx.graphics.getWidth() / 1080;
          ratH = (float)Gdx.graphics.getHeight() / 1920;
          screenWidth = Gdx.graphics.getWidth();
          screenHeight = Gdx.graphics.getHeight();
          row_height = (int)screenHeight / 12;
          col_width = (int)screenWidth / 12;
          skin = new Skin(Gdx.files.internal("skinHolo/uiskin.json"));
          stage = new Stage(new ScreenViewport());
          Gdx.input.setInputProcessor(stage);
          preferences = Gdx.app.getPreferences("My Preferences");
          enemyDestroyed = preferences.getInteger("enemyDestroyed");
          crystalDestroyed = preferences.getInteger("crystalDestroyed");
          fogsLooted = preferences.getInteger("fogsLooted");
        //Инициализация кнопок и рисунков в Stage
        statImg = new Image(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("stat.png")))));
        statImg.setSize(col_width * 2.5f, row_height * 1.5f);
        statImg.setPosition(col_width * 6, screenHeight - row_height * 1f, Align.center);
        stage.addActor(statImg);

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
        rightBtn.setPosition((float)(screenWidth - col_width * 3.4), (float)(row_height * 3));
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
                initialiseStat();

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
        leftBtn.setPosition((float)(screenWidth - col_width * 10), (float)( row_height * 3));
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
                initialiseStat();
                return false;
            }
        });
        stage.addActor(leftBtn);

        firstImg = new Image(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("Shoot/player/enemy.png")))));
        firstImg.setSize(col_width * 3f, row_height * 1.5f);
        firstImg.setPosition(col_width * 6f, screenHeight - row_height * 5, Align.center);

        firstLbl = new Label("Destroyed:" + enemyDestroyed, skin);
        firstLbl.setSize(col_width * 2, row_height * 1);
        firstLbl.setFontScale(1.5f * ratW, 1.5f * ratH);
        firstLbl.setPosition(col_width * 5f, screenHeight - row_height * 6, Align.center);
        stage.addActor(firstImg);
        stage.addActor(firstLbl);
          initialiseStat();


    }
    //Функция для изменение текста и рисунка при изменение режима
    private void initialiseStat(){
           
            if(selectedGameMode == 1){
                firstImg.setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("Shoot/player/enemy.png")))));
                firstLbl.setText("Destroyed:" + enemyDestroyed);
            } else if(selectedGameMode == 2){
                firstImg.setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("Object/player/crystal.png")))));
                firstLbl.setText("Destroyed:" + crystalDestroyed);
            } else if(selectedGameMode == 3){
                firstImg.setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("Find/Nexus.png")))));
                firstLbl.setText("Information saved:" + fogsLooted);
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
           stage.dispose();

    }
}
