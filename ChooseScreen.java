package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.Find.FindEasy;
import com.mygdx.game.Find.FindHard;
import com.mygdx.game.Find.FindNormal;
import com.mygdx.game.MainScreen.StartScreen;
import com.mygdx.game.Object.ObjectEasy;
import com.mygdx.game.Object.ObjectHard;
import com.mygdx.game.Object.ObjectNormal;
import com.mygdx.game.Gun.GunEasy;
import com.mygdx.game.Gun.GunHard;
import com.mygdx.game.Gun.GunNormal;

public class ChooseScreen implements Screen {
    //Экран выбора мини - режимов
    FOG game;
    //Обьявление переменных
    OrthographicCamera camera;
    int screenWidth, screenHeight;
    TextButton selectBtn, startBtn;
    ImageButton rightBtn, leftBtn, homeBtn;
    Image imgGM;
    SelectBox<String> difficulties;
    String[] difTypes = new String[]{"Easy", "Normal", "Hard"};
    Stage stage;
    Label chooseDif, chooseGM ,check, difNameLbl;
    Skin skin, skinOptional;

    int row_height = Gdx.graphics.getHeight() / 12;
    int col_width = Gdx.graphics.getWidth() / 12;
    int selectedDifIndex = 0;
    int selectedGameMode = 1;
    float ratW, ratH;
    //Инициализация переменных
    public ChooseScreen(final FOG _game){
        this.game = _game;
        screenHeight = Gdx.graphics.getHeight();
        screenWidth = Gdx.graphics.getWidth();
        ratW = (float)screenWidth / 1080;
        ratH = (float)screenHeight / 1920;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth, screenHeight);
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("skinHolo/uiskin.json"));
        skinOptional = new Skin(Gdx.files.internal("skin/glassy-ui.json"));
        //Добавление фото и кнопок в Stage
        //Choose game mode Label
        chooseGM = new Label("Choose Game Mode", skin, "default");
        chooseGM.setPosition(0, screenHeight - row_height * 1);
        chooseGM.setSize(screenWidth, row_height * 1);
        chooseGM.setFontScale(2.5f * ratW, 2.5f * ratH);
        chooseGM.setAlignment(Align.center);
        stage.addActor(chooseGM);
        //Choose difficulty Label
        chooseDif = new Label("Choose Difficulty", skin ,"default");
        chooseDif.setSize(screenWidth,row_height*1);
        chooseDif.setPosition(0,(float)(screenHeight-row_height * 2));
        chooseDif.setFontScale(2 * ratW, 2 * ratH);
        chooseDif.setAlignment(Align.center);
        stage.addActor(chooseDif);
        //EASY,NORMAL, HARD SelectBox
        difficulties = new SelectBox(skinOptional, "default");
        difficulties.setSize(col_width * 6, (float)(row_height * 0.8));
        difficulties.getStyle().listStyle.font.getData().setScale(1.9f * ratW, 1.9f * ratH);
        difficulties.setPosition(col_width * 3, screenHeight-row_height * 2.6f);
        difficulties.setAlignment(Align.center);
        difficulties.getList().setAlignment(Align.right);
        difficulties.setItems(difTypes);
        difficulties.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedDifIndex = difficulties.getSelectedIndex();

            }
        });
        stage.addActor(difficulties);

        //rightImageBtn
        rightBtn = new ImageButton(skin, "colored");
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
        rightBtn.setPosition((float)(screenWidth - col_width * 3.4), (float)(screenHeight - row_height * 5.6));
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
                if(selectedGameMode == 1){
                    imgGM.setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(
                            "Shoot/player/images/gunPreview.png")))));
                    difNameLbl.setText("GUN");
                } else if(selectedGameMode == 2){
                    imgGM.setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture(
                            Gdx.files.internal("Object/player/objectpreview.jpg")))));
                    difNameLbl.setText("OBJECT");
                } else {
                    imgGM.setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture(
                            Gdx.files.internal("Find/FindPreview.png")))));
                    difNameLbl.setText("FIND");
                }
                return false;
            }
        });
        stage.addActor(rightBtn);
        //leftImageBtn
        leftBtn = new ImageButton(skin, "colored");
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
        leftBtn.setPosition((float)(screenWidth - col_width * 10), (float)(screenHeight - row_height * 5.6));
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
                if(selectedGameMode == 1){
                    imgGM.setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(
                            "Shoot/player/images/gunPreview.png")))));
                    difNameLbl.setText("GUN");
                } else if(selectedGameMode == 2){
                    imgGM.setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture(
                            Gdx.files.internal("Object/player/objectpreview.jpg")))));
                    difNameLbl.setText("OBJECT");
                } else {
                    imgGM.setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture(
                            Gdx.files.internal("Find/FindPreview.png")))));
                    difNameLbl.setText("FIND");
                }
                return false;
            }
        });
        stage.addActor(leftBtn);
        //Image of Game Mode
        Texture txtImgGm = new Texture(Gdx.files.internal("Shoot/player/images/gunPreview.png"));
        imgGM = new Image(txtImgGm);
        imgGM.setAlign(Align.center);
        imgGM.setPosition(screenWidth / 2 - (float)(col_width * 2), screenHeight - (float)(row_height * 5.6));
        imgGM.setSize(col_width * 4, row_height * 1.5f);
        stage.addActor(imgGM);
        //Difficulty Name Label
        difNameLbl = new Label("GUN", skinOptional, "black");
        difNameLbl.setAlignment(Align.center);
        difNameLbl.setPosition(screenWidth / 2 - (float)(col_width * 0.5) , screenHeight - (float)(row_height * 4));
        difNameLbl.setFontScale(2.7f * ratW, 2.7f * ratH);
        difNameLbl.setSize(col_width, row_height);
        stage.addActor(difNameLbl);
        //Start Button
        startBtn = new TextButton("START", skin, "colored" );
        startBtn.getLabel().setFontScale(2 * ratW, 2 * ratH);
        startBtn.setSize((float)(col_width * 5),(float)(row_height * 1.8));
        startBtn.setPosition(screenWidth / 2 - (float)(col_width * 2.45), (float)(screenHeight - row_height * 7.7));
        startBtn.addListener(new InputListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if(selectedGameMode == 1){
                        if(selectedDifIndex == 0) {
                            game.setScreen(new GunEasy(game));
                            dispose();
                        } else if(selectedDifIndex == 1){
                            game.setScreen(new GunNormal(game));
                            dispose();
                        } else if(selectedDifIndex == 2){
                            game.setScreen(new GunHard(game));
                            dispose();
                        }
                    } else if(selectedGameMode == 2){
                        if(selectedDifIndex == 0) {
                            game.setScreen(new ObjectEasy(game));
                            dispose();
                        } else if(selectedDifIndex == 1){
                            game.setScreen(new ObjectNormal(game));
                            dispose();
                        } else if(selectedDifIndex == 2){
                            game.setScreen(new ObjectHard(game));
                            dispose();
                        }
                    } else if(selectedGameMode == 3){
                        if(selectedDifIndex == 0) {
                            game.setScreen(new FindEasy(game));
                            dispose();
                        } else if(selectedDifIndex == 1){
                            game.setScreen(new FindNormal(game));
                            dispose();
                        } else if(selectedDifIndex == 2){
                            game.setScreen(new FindHard(game));
                            dispose();
                        }

                    }
                    return false;
                }
            });
            stage.addActor(startBtn);
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
    }

    @Override
    public void show() {

    }
    //Отрисовка
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1,  1,1,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
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
    }
}
