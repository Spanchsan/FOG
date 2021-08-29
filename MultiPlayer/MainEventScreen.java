package com.mygdx.game.MultiPlayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.game.FOG;
import com.mygdx.game.MainScreen.StartScreen;
import com.mygdx.game.utils.PortScanner;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
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
    InetAddress host;
    String hostS;

    int counter = 0;
    TextButton servBtn, connectBtn;
    ImageButton backBtn;
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
        game.getFont().getData().setScale(2 * ratW, 2 * ratH);
        //game.getFont().setColor(1,0,0,1);

        servBtn = new TextButton("Create a server", skin);
        servBtn.setSize((float)(row_height * 2), (float)(col_width * 2));
        servBtn.setPosition(col_width * 6, row_height * 8, Align.center);
        servBtn.getLabel().setFontScale(2.5f * ratW, 2.5f * ratH);
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
        connectBtn = new TextButton("Connect", skin);
        connectBtn.setSize((float)(row_height * 2), (float)(col_width * 2));
        connectBtn.setPosition(col_width * 6, row_height * 5, Align.center);
        connectBtn.getLabel().setFontScale(2.5f * ratW, 2.5f * ratH);
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
                        game.setScreen(new MainEventScreen(game));
                    }
                }, "Input Ip", "", "Server Local IP");
                dispose();
                return false;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            }
        });
        stage.addActor(connectBtn);
        //Back Button
        backBtn = new ImageButton(skin);
        Drawable backBtnBackground = backBtn.getBackground();
        ImageButton.ImageButtonStyle backBtnStyle = new ImageButton.ImageButtonStyle();
        backBtnStyle.imageUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("back.png"))));
        backBtnStyle.imageDown = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("back.png"))));
        backBtnStyle.up = backBtnBackground;
        backBtnStyle.down = backBtnBackground;
        backBtn.setStyle(backBtnStyle);
        backBtn.setSize((float)(col_width * 1.4), (float)(row_height * 0.7));
        backBtn.setPosition((float)(col_width * 0.2), (float)(row_height * 0.2));
        backBtn.addListener(new InputListener(){
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
        stage.addActor(backBtn);
        try {
            Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
            for (; n.hasMoreElements(); ) {
                NetworkInterface e = n.nextElement();
                Enumeration<InetAddress> a = e.getInetAddresses();
                for (; a.hasMoreElements(); ) {
                    InetAddress addr = a.nextElement();
                    String temp = addr.getHostAddress();
                    if ((temp.charAt(0) >= '0' && temp.charAt(0) <= '9') && (temp.charAt(1) >= '6' && temp.charAt(1) <= '9')
                            && (temp.charAt(2) >= '0' && temp.charAt(2) <= '9') && !(temp.equals("127.0.0.1"))) {
                        address = addr.getHostAddress();
                    }
                }
            }
        } catch (Exception ex){}
        hostS = "";
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                hosts = PortScanner.scanPortForServers();
                //host = PortScanner.scanPortForSingleServer();
                for(InetAddress addr: hosts ){
                    if(addr.getHostAddress().isEmpty()) break;
                    hostS += addr.getHostAddress() + "\n";
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
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
        game.getFont().draw(game.getBatch(), "Available:\n" + hostS,
                    col_width * 4, row_height * 10 - row_height * 0.5f * counter);
        /*game.getFont().draw(game.getBatch(), "Available: " + host.getHostAddress(),
                col_width * 3.5f, row_height * 10 - row_height * 0.5f * counter);*/
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
