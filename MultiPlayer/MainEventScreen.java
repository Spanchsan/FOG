package com.mygdx.game.MultiPlayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.game.FOG;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

public class MainEventScreen implements Screen {

    FOG game;
    Client client;
    Server server;

    int row_height;
    int col_width;
    int screenHeight, screenWidth;
    Stage stage;
    float ratW, ratH;
    String address;
    List<InetAddress> hosts;

    int counter = 0;
    TextButton servBtn, connectBtn;
    Skin skin;

    public MainEventScreen(final FOG game){
        this.game = game;
        ratW = (float) Gdx.graphics.getWidth() / 1080;
        ratH = (float)Gdx.graphics.getHeight() / 1920;
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        col_width = screenWidth / 12;
        row_height = screenHeight / 12;
        //Создания двух stage бля бэкграунда и экрана игрока
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("skinHolo/uiskin.json"));
        Gdx.input.setInputProcessor(stage);
        game.getFont().getData().setScale(4 * ratW, 4 * ratH);
        game.getFont().setColor(1,0,0,1);

        servBtn = new TextButton("Create a server", skin);
        servBtn.setSize((float)(row_height * 2), (float)(col_width * 2));
        servBtn.setPosition(col_width * 6, row_height * 8, Align.center);
        servBtn.getLabel().setFontScale(5 * ratW, 5 * ratH);
        servBtn.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                try {
                    game.setScreen(new ServerPlayer(game));
                } catch (IOException ex){

                }
                dispose();
                return false;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            }
        });
        stage.addActor(servBtn);
        try {
            address = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e){}
        connectBtn = new TextButton("Connect", skin);
        connectBtn.setSize((float)(row_height * 2), (float)(col_width * 2));
        connectBtn.setPosition(col_width * 6, row_height * 5, Align.center);
        connectBtn.getLabel().setFontScale(5 * ratW, 5 * ratH);
        connectBtn.addListener(new InputListener(){
            String host;
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.input.getTextInput(new Input.TextInputListener() {
                    @Override
                    public void input(String text) {
                        host = text;
                        try {
                            game.setScreen(new ClientPlayer(game, host));
                        } catch (IOException ex){

                        }
                    }

                    @Override
                    public void canceled() {

                    }
                }, "Input Ip", "127.0.0.1", "Ip Server");
                dispose();
                return false;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            }
        });
        stage.addActor(connectBtn);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.getBatch().begin();
        stage.act();
        stage.draw();
        game.getFont().draw(game.getBatch(), "Your Ip:" + address, col_width * 4, row_height * 11);
        /*for(InetAddress addr: hosts ){
            game.getFont().draw(game.getBatch(), "Available" + addr.getHostAddress(),
                    col_width * 4, row_height * 10 - row_height * 0.5f * counter);
            counter++;
        }*/
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
