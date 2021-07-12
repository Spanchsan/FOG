package com.mygdx.game.MultiPlayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.game.ChooseScreen;
import com.mygdx.game.FOG;
import com.mygdx.game.MainScreen.StartScreen;
import com.mygdx.game.gameUtils.Joystick;

import java.io.IOException;
import java.net.InetAddress;

public class ServerPlayer implements Screen{

    //Server component
    Server server;
    FOG game;
    int row_height;
    int col_width;
    int screenHeight, screenWidth;
    float playerX, playerY;
    Rectangle playerR, anotherR;
    float  anX, anY;

    float ratW, ratH;
    int playerStep = 400;
    double angle = 0;

    boolean plAv = false;

   Stage stage;
    Texture player, another;
    ImageButton backBtn;
    Joystick joystick;
    Skin skin;

    Preferences pref;
    String name, key;
    InetAddress address;
    Array<String> connectedPl;
    public ServerPlayer(final FOG game) throws IOException {
        pref = Gdx.app.getPreferences("My Preferences");
        key = pref.getString("playerKey");

        server = new Server(){
            protected Connection newConnection(){
                return new PlayerConnection();
            }
        };
        Network.register(server);
        server.bind(Network.port);
        server.start();
        server.addListener(new Listener(){
            @Override
            public void received(Connection c, Object object) {

                PlayerConnection connection = (PlayerConnection)c;
                Player player = connection.player;

                if(object instanceof Network.ChangePlayerPosition) {

                    Network.ChangePlayerPosition msg = (Network.ChangePlayerPosition)object;
                    player.playerX += msg.posX;
                    player.playerY += msg.posY;

                    Network.UpdatePlayer update = new Network.UpdatePlayer();
                    update.id = player.id;
                    update.playerX = player.playerX;
                    update.playerY = player.playerY;
                    server.sendToAllTCP(update);
                    return;
                }

            }

            @Override
            public void connected(Connection connection) {
                System.out.println("Joined to server" + connection.getRemoteAddressTCP().getHostString());
                plAv = true;
            }

            @Override
            public void disconnected(Connection connection) {
            }

        });

        this.game = game;
        ratW = (float)Gdx.graphics.getWidth() / 1080;
        ratH = (float)Gdx.graphics.getHeight() / 1920;
        playerStep *= ratW;
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        col_width = screenWidth / 12;
        row_height = screenHeight / 12;
        //Создания двух stage бля бэкграунда и экрана игрока
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("skinHolo/uiskin.json"));
        Gdx.input.setInputProcessor(stage);
        game.getFont().getData().setScale(3 * ratW, 3 * ratH);

        player = new Texture(Gdx.files.internal("Shoot/player/player.png"));
        another = new Texture(Gdx.files.internal("Shoot/player/player.png"));
        playerR = new Rectangle();
        playerR.setWidth(200 * ratW);
        playerR.setHeight(200 * ratH);
        anotherR = new Rectangle();
        anotherR.setWidth(200 * ratW);
        anotherR.setHeight(200 * ratH);

        //Joystick
        joystick = new Joystick(ratW, ratH);
        joystick.setX(screenWidth - (float)(col_width * 5.8));
        joystick.setY((float)(row_height * 0.8));
        stage.addActor(joystick);
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
                return false;
            }
        });
        stage.addActor(backBtn);
        try {
            address = InetAddress.getLocalHost();
        } catch (Exception e){}
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
        game.getFont().draw(game.getBatch(), "IP:" + address, col_width * 6, row_height * 11);
        if(joystick.getJoystickIsTouched()){
            angle = joystick.getRadians();
            playerX += playerStep * Gdx.graphics.getDeltaTime() * Math.cos(angle);
            playerY += playerStep * Gdx.graphics.getDeltaTime() * Math.sin(angle);
        }
        if(plAv) {
            game.getFont().draw(game.getBatch(), "Player Connected", col_width * 6, row_height * 9.5f);
        }
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
         server.stop();
        try {
            server.dispose();
        } catch (Exception ex){

        }
    }

    static class PlayerConnection extends Connection {
        public Player player;
    }

}

