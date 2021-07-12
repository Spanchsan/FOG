package com.mygdx.game.MainScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.FOG;
import com.mygdx.game.gameUtils.DataLoader;
import com.mygdx.game.gameUtils.OnDataLoaderListener;
import com.mygdx.game.gameUtils.User;

public class SettingsScreen implements Screen {

    final FOG game;
    int screenWidth, screenHeight;

    int row_height;
    int col_width;
    float ratW, ratH;

    String name, oldName;

    TextButton changeNameBtn;
    ImageButton homeBtn;
    Label nameLbl, settingsLbl;

    Stage stage;
    Skin skin;
    private Preferences pref;

    DataLoader dl;

    public SettingsScreen(final FOG game){
        this.game = game;
        screenHeight = Gdx.graphics.getHeight();
        screenWidth = Gdx.graphics.getWidth();
        //Перменные предназначение которых масштабирование фото и кнопок
        ratW = (float)screenWidth / 1080;
        ratH = (float)screenHeight / 1920;
        row_height = Gdx.graphics.getHeight() / 12;
        col_width = Gdx.graphics.getWidth() / 12;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("skinHolo/uiskin.json"));
        pref = Gdx.app.getPreferences("My Preferences");
        name = pref.getString("playerName", "None");
        //Settings Label
        settingsLbl = new Label("Settings", skin);
        settingsLbl.setPosition(col_width * 4, row_height * 10.5f, Align.center);
        settingsLbl.setSize(col_width * 4, row_height * 2);
        settingsLbl.setFontScale(5 * ratW, 5 * ratH);
        stage.addActor(settingsLbl);
        //Name Label
        nameLbl = new Label("Your name:" + name, skin);
        nameLbl.setPosition(col_width * 4, row_height * 8.5f);
        nameLbl.setSize(col_width * 4, row_height * 2);
        nameLbl.setFontScale(3 * ratW, 3 * ratH);
        stage.addActor(nameLbl);
        //Change name button
        changeNameBtn = new TextButton("Change Name", skin);
        changeNameBtn.setPosition(col_width * 4, row_height * 7);
        changeNameBtn.setSize(col_width * 4, row_height * 2);
        changeNameBtn.getLabel().setFontScale(3 * ratW, 3 * ratH);
        changeNameBtn.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.input.getTextInput(new Input.TextInputListener() {
                    @Override
                    public void input(String text) {
                        oldName = name;
                        name = text;
                        pref.putString("playerName", name);
                        pref.putString("oldPlayerName", oldName);
                        pref.flush();
                        nameLbl.setText("Your name:" + name);
                    }

                    @Override
                    public void canceled() {
                        game.setScreen(new LeaderboardScreen(game));
                        dispose();
                    }
                }, "Set Your Name", "", "Your Name");
                return false;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            }
        });
        stage.addActor(changeNameBtn);
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
                dl = DataLoader.getInstance();
                OnDataLoaderListener listener = new OnDataLoaderListener() {
                    @Override
                    public void getData(Array<User> players) {
                        for(int i =0; i < players.size; i++){
                            if(players.get(i).getName().equals(oldName)){
                                players.get(i).setName(name);
                            }
                        }
                    }
                };
                dl.getPlayers(listener);
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
