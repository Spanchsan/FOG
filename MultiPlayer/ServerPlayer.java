package com.mygdx.game.MultiPlayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.game.FOG;
import com.mygdx.game.Gun.Entity.PBullet;
import com.mygdx.game.MainScreen.StartScreen;
import com.mygdx.game.gameUtils.Joystick;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

public class ServerPlayer implements Screen{

    //Server component
    Server server;
    Network.UpdatePlayer update;
    Network.BulletPositions updateB;
    Network.EnemyDamaged updateD;
    FOG game;
    int row_height;
    int col_width;
    int screenHeight, screenWidth;
    Rectangle playerR, anotherR;

    Runnable runUp;
    Thread thrUp;

    Texture animationPic;
    TextureRegion[] planeAnimationFrames;
    Animation<TextureRegion> planeAnimation;
    float elapsedTime;

    float ratW, ratH;
    int playerStep = 400;
    int pBulletStep = 700;
    double angle = 0;
    boolean canShot, shootPressed;
    long lastPBulletShotTime;
    float reloadDuration = 1;
    float valueShotReload = 1;
    float anPosX, anPosY;

    boolean plAv = false, roundLost = false;

    Player player, enemy;
    ArrayList<PBullet> playerB, enemyB, plRatB;

    Stage stage;
    ImageButton shootButton, backBtn;
    TextButton retryBtn, mainMenuBtn;
    ProgressBar PBHPPlayer, PBHPEnemy, PBReload;
    Label HpLbl, EnemyHpLbl, winLbl;
    TextureRegion playerT, another, pBulletT;
    Rectangle allRec;
    PBullet temp;
    Joystick joystick;
    Skin skin, skinOptional;

    Preferences pref;
    String name, key;
    String address;
    public ServerPlayer(final FOG game) throws IOException {
        pref = Gdx.app.getPreferences("My Preferences");
        key = pref.getString("playerKey");
        player = new Player();
        player.hp = 100;
        player.playerX = (float)screenWidth / 2;
        player.playerY = (float)screenHeight / 2;
        enemy = new Player();
        enemy.hp = 100;
        enemy.playerX = (float)screenWidth / 2;
        enemy.playerY = (float)screenHeight / 2;
        anPosX = 0;
        anPosY = 0;
        server = new Server();
        Network.register(server);
        server.bind(Network.portTCP, Network.portUDP);
        server.addListener(new Listener(){
            @Override
            public void received(Connection c, Object object) {


                if(object instanceof Network.UpdatePlayer) {

                    Network.UpdatePlayer msg = (Network.UpdatePlayer)object;
                    enemy.playerX = msg.posX * screenWidth;
                    enemy.playerY = msg.posY * screenHeight;
                    enemy.angle = msg.angle;
                    enemy.hp = msg.hp;

                    return;
                }

                if(object instanceof  Network.BulletPositions){
                    Network.BulletPositions msg = (Network.BulletPositions)object;
                    enemyB = msg.bullets;

                    return;
                }

                if(object instanceof Network.EnemyDamaged){

                    player.hp -= 10;
                    if(player.hp <= 0){
                        update.posX = player.playerX / screenWidth;
                        update.posY = player.playerY / screenHeight;
                        update.angle = player.angle;
                        update.hp = player.hp;
                        server.sendToAllTCP(update);
                        plAv = false;
                        game.getFont().getData().setScale(1.5f * ratW, 1.5f * ratH);
                        joystick.setVisible(false);
                        PBReload.setVisible(false);
                        PBHPEnemy.setVisible(false);
                        PBHPPlayer.setVisible(false);
                        HpLbl.setVisible(false);
                        EnemyHpLbl.setVisible(false);
                        shootButton.setVisible(false);
                        retryBtn.setVisible(true);
                        mainMenuBtn.setVisible(true);
                        winLbl.setVisible(true);
                        winLbl.setText("You Lost, Try Again!");
                        server.sendToAllTCP(new Network.RoundLost());
                        roundLost = true;
                    }

                    return;
                }

                if(object instanceof  Network.RoundLost){
                    plAv = false;
                    game.getFont().getData().setScale(2 * ratW, 2 * ratH);
                    joystick.setVisible(false);
                    PBReload.setVisible(false);
                    PBHPEnemy.setVisible(false);
                    PBHPPlayer.setVisible(false);
                    HpLbl.setVisible(false);
                    EnemyHpLbl.setVisible(false);
                    shootButton.setVisible(false);
                    retryBtn.setVisible(true);
                    mainMenuBtn.setVisible(true);
                    winLbl.setVisible(true);
                    winLbl.setText("You won, Good Job!");
                    roundLost = true;

                    return;
                }

                if(object instanceof  Network.ResumeRound){
                    plAv = true;
                    retryBtn.setVisible(false);
                    mainMenuBtn.setVisible(false);
                    winLbl.setVisible(false);
                    joystick.setVisible(true);
                    PBReload.setVisible(true);
                    PBHPEnemy.setVisible(true);
                    PBHPPlayer.setVisible(true);
                    HpLbl.setVisible(true);
                    EnemyHpLbl.setVisible(true);
                    shootButton.setVisible(true);
                    game.getFont().getData().setScale(1.5f * ratW, 1.5f * ratH);

                    return;
                }

            }

            @Override
            public void connected(Connection connection) {
                System.out.println("Joined to server" + connection.getRemoteAddressTCP().getHostString());
                plAv = true;
                joystick.setVisible(true);
                PBReload.setVisible(true);
                PBHPEnemy.setVisible(true);
                PBHPPlayer.setVisible(true);
                HpLbl.setVisible(true);
                EnemyHpLbl.setVisible(true);
                shootButton.setVisible(true);
                game.getFont().getData().setScale(1.5f * ratW, 1.5f * ratH);
                player.playerX = (float)screenWidth / 2;
                player.playerY = (float)screenHeight / 2;
            }

            @Override
            public void disconnected(Connection connection) {
                plAv = false;
                game.getFont().getData().setScale(2 * ratW, 2 * ratH);
                joystick.setVisible(false);
                PBReload.setVisible(false);
                PBHPEnemy.setVisible(false);
                PBHPPlayer.setVisible(false);
                HpLbl.setVisible(false);
                EnemyHpLbl.setVisible(false);
                shootButton.setVisible(false);
                retryBtn.setVisible(false);
                mainMenuBtn.setVisible(false);
                winLbl.setVisible(false);
                roundLost = false;

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
        skinOptional = new Skin(Gdx.files.internal("skin/glassy-ui.json"));
        Gdx.input.setInputProcessor(stage);
        game.getFont().getData().setScale(2 * ratW, 2 * ratH);
        playerB = new ArrayList<>();
        enemyB = new ArrayList<>();
        plRatB = new ArrayList<>();

        playerT = new TextureRegion(new Texture(Gdx.files.internal("Shoot/player/playerNoPower.png")));
        another = new TextureRegion(new Texture(Gdx.files.internal("Shoot/player/playerNoPower.png")));
        pBulletT = new TextureRegion(new Texture(Gdx.files.internal("Shoot/player/playerShot.png")));
        playerR = new Rectangle();
        playerR.setWidth(200 * ratW);
        playerR.setHeight(200 * ratH);
        anotherR = new Rectangle();
        anotherR.setWidth(200 * ratW);
        anotherR.setHeight(200 * ratH);
        temp = new PBullet();
        allRec = new Rectangle();
        allRec.set(0,0,screenWidth,screenHeight);

        //Shoot button
        shootButton = new ImageButton(skin, "colored");
        Drawable shootButtonBackground = shootButton.getBackground();
        ImageButton.ImageButtonStyle shootButtonStyle = new ImageButton.ImageButtonStyle();
        shootButtonStyle.imageUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("shoot.png"))));
        shootButtonStyle.imageDown = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("shoot.png"))));
        shootButtonStyle.up = shootButtonBackground;
        shootButtonStyle.down = shootButtonBackground;
        shootButton.setStyle(shootButtonStyle);
        shootButton.setSize(col_width * 3f,row_height * 1.7f);
        shootButton.setPosition((float)(col_width * 0.5), (float)(row_height * 1.3));
        shootButton.addListener(new InputListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                shootPressed = false;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                shootPressed = true;
                return true;
            }
        });
        shootButton.setVisible(false);
        stage.addActor(shootButton);
        //Reload Progress Bar
        PBReload = new ProgressBar(0 , reloadDuration, 0.05f, false, skinOptional, "default-horizontal" );
        PBReload.setPosition(col_width * 0.5f, row_height * 3.3f);
        PBReload.setSize(col_width * 3f, PBReload.getPrefHeight());
        PBReload.setAnimateDuration(0.05f);
        PBReload.setVisible(false);
        stage.addActor(PBReload);
        //HP lbl
        HpLbl = new Label("YOUR HP", skinOptional, "black");
        HpLbl.setFontScale(1.5f * ratW, 1.5f * ratH);
        HpLbl.setSize(col_width, row_height);
        HpLbl.setPosition((float)(col_width * 0.8), screenHeight - (float)(row_height * 1.7));
        HpLbl.setColor(1, 0 , 0, 1);
        HpLbl.setVisible(false);
        stage.addActor(HpLbl);
        //Progress bar PlayerHP
        PBHPPlayer = new ProgressBar(0,
                100,5,  false, skinOptional, "default-horizontal");
        PBHPPlayer.setPosition((float)(col_width * 0.8), screenHeight - (float)(row_height * 0.7));
        PBHPPlayer.setSize(col_width * 5, PBHPPlayer.getPrefHeight());
        PBHPPlayer.setAnimateDuration((float)0.5);
        PBHPPlayer.setVisible(false);
        stage.addActor(PBHPPlayer);
        //Enemy HP lbl
        EnemyHpLbl = new Label("ENEMY HP", skinOptional, "black");
        EnemyHpLbl.setFontScale(1.5f * ratW, 1.5f * ratH);
        EnemyHpLbl.setSize(col_width, row_height);
        EnemyHpLbl.setPosition(screenWidth - (float)(col_width * 5.3), screenHeight - (float)(row_height * 1.7));
        EnemyHpLbl.setColor(0, 0 , 1, 1);
        EnemyHpLbl.setVisible(false);
        stage.addActor(EnemyHpLbl);
        //Progress bar EnemyHP
        PBHPEnemy = new ProgressBar(0, 100, 5, false, skinOptional, "default-horizontal");
        PBHPEnemy.setPosition( screenWidth - (float)(col_width * 5.3), screenHeight - (float)(row_height * 0.7));
        PBHPEnemy.setSize(col_width * 5, PBHPPlayer.getPrefHeight());
        PBHPEnemy.setAnimateDuration((float)0.5);
        PBHPEnemy.setVisible(false);
        stage.addActor(PBHPEnemy);
        //Retry Button
        retryBtn = new TextButton("Retry", skin);
        retryBtn.setSize(col_width * 6, row_height * 2);
        retryBtn.getLabel().setFontScale(3 * ratW, 3 * ratH);
        retryBtn.getLabel().setColor(0,0,0,1);
        retryBtn.setPosition(screenWidth - col_width * 9, screenHeight - row_height * 6.8f);
        retryBtn.setVisible(false);
        retryBtn.addListener(new InputListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                server.sendToAllTCP(new Network.ResumeRound());
                plAv = true;
                retryBtn.setVisible(false);
                mainMenuBtn.setVisible(false);
                winLbl.setVisible(false);
                joystick.setVisible(true);
                PBReload.setVisible(true);
                PBHPEnemy.setVisible(true);
                PBHPPlayer.setVisible(true);
                HpLbl.setVisible(true);
                EnemyHpLbl.setVisible(true);
                shootButton.setVisible(true);
                game.getFont().getData().setScale(1.5f * ratW, 1.5f * ratH);
                player.hp = 100;
                player.playerX = screenWidth / 2;
                player.playerY = screenHeight / 2;
                roundLost = false;
                return false;
            }
        });
        stage.addActor(retryBtn);
        //Back Button
        mainMenuBtn = new TextButton("Back", skin);
        mainMenuBtn.setSize(col_width * 6, row_height * 2);
        Label backBtnLabel = new Label("Back", skinOptional, "black");
        mainMenuBtn.getLabel().getStyle().font = backBtnLabel.getStyle().font;
        mainMenuBtn.getLabel().setFontScale(3 * ratW, 3 * ratH);
        mainMenuBtn.getLabel().setColor(0,0,0,1);
        mainMenuBtn.setPosition(screenWidth - col_width * 9, screenHeight - row_height * 9);
        mainMenuBtn.setVisible(false);
        mainMenuBtn.addListener(new InputListener(){
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
        stage.addActor(mainMenuBtn);
        //Score lbl
        winLbl = new Label("You Lost, Try Again!", skinOptional, "black");
        winLbl.setSize(col_width * 2, row_height * 2);
        winLbl.setFontScale(2 * ratW, 2 * ratH);
        winLbl.setPosition(screenWidth - col_width * 9, screenHeight - row_height * 3);
        winLbl.setVisible(false);
        stage.addActor(winLbl);
        //Joystick
        joystick = new Joystick(ratW, ratH);
        joystick.setX(screenWidth - (float)(col_width * 5.8));
        joystick.setY((float)(row_height * 0.8));
        joystick.setVisible(false);
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
                game.setScreen(new MainEventScreen(game));
                dispose();
                return false;
            }
        });
        stage.addActor(backBtn);
        update = new Network.UpdatePlayer();
        updateB = new Network.BulletPositions();
        updateD = new Network.EnemyDamaged();
        runUp = new Runnable() {
            @Override
            public void run() {
                update.posX = player.playerX / screenWidth;
                update.posY = player.playerY / screenHeight;
                update.angle = player.angle;
                update.hp = player.hp;
                server.sendToAllUDP(update);
                updateB.bullets = plRatB;
                server.sendToAllUDP(updateB);
            }
        };
        thrUp = new Thread(runUp);
        Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
        for (; n.hasMoreElements();) {
            NetworkInterface e = n.nextElement();
            Enumeration<InetAddress> a = e.getInetAddresses();
            for (; a.hasMoreElements();)
            {
                InetAddress addr = a.nextElement();
                String temp = addr.getHostAddress();
                if((temp.charAt(0) >= '0' && temp.charAt(0) <= '9') && (temp.charAt(1) >= '6' && temp.charAt(1) <= '9')
                && (temp.charAt(2) >= '0' && temp.charAt(2) <= '9') && !(temp.equals("127.0.0.1"))){
                    address = addr.getHostAddress();
                    System.out.println(address);
                }
            }
        }
        server.start();
        createPlayerAnimation();
    }

    //Создания анимации игрока
    private void createPlayerAnimation(){
        animationPic = new Texture(Gdx.files.internal("animations/planeanimation.png"));

        TextureRegion[][] tmpFrames = TextureRegion.split(animationPic, 200,200);
        planeAnimationFrames = new TextureRegion[12];
        int index = 0;

        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 3; j ++){
                planeAnimationFrames[index++] = tmpFrames[i][2 - j];
            }
        }

        planeAnimation = new Animation(1f/12f, planeAnimationFrames);


    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        PBHPPlayer.setValue(player.hp);
        PBHPEnemy.setValue(enemy.hp);
        PBReload.setValue(valueShotReload);
        valueShotReload += Gdx.graphics.getDeltaTime();
        playerR.setPosition(player.playerX, player.playerY);
        anotherR.setPosition(enemy.playerX, enemy.playerY);
        elapsedTime += Gdx.graphics.getDeltaTime();
        game.getBatch().begin();
        stage.act();
        stage.draw();
        if(plAv) {
            if(!plRatB.isEmpty())
                for(PBullet r: plRatB){
                    r.setX(r.getX() + (float)(pBulletStep * Gdx.graphics.getDeltaTime()
                            *  Math.cos(r.getAngle())) / screenWidth);
                    r.setY(r.getY() + (float)(pBulletStep * Gdx.graphics.getDeltaTime()
                            * Math.sin(r.getAngle())) / screenHeight);
                    if(!(0 < r.x * screenWidth + r.width && 0 + screenWidth > r.x * screenWidth
                            && 0 < r.y * screenHeight + r.height && 0 + screenHeight > r.y * screenHeight)){
                        plRatB.remove(r);
                    }

                    if(anotherR.x < r.x * screenWidth + r.width && anotherR.x + anotherR.width > r.x * screenWidth
                            && anotherR.y < r.y * screenHeight + r.height && anotherR.y + anotherR.height > r.y * screenHeight){
                        server.sendToAllTCP(updateD);
                        plRatB.remove(r);
                    }
                    /*temp = r;
                    temp.setPosition(r.getX() * screenWidth, r.getY() * screenHeight);
                    if(temp.overlaps(anotherR)){
                        server.sendToAllTCP(updateD);
                    }

                    if(!temp.overlaps(allRec)){
                        plRatB.remove(r);
                    }*/
                }

            if(anPosX == enemy.playerX && anPosY == enemy.playerY) {
                game.getBatch().draw(another, enemy.playerX, enemy.playerY, anotherR.getWidth() / 2, anotherR.getHeight() / 2,
                        anotherR.getWidth(), anotherR.getHeight(), 1, 1, (float) Math.toDegrees(enemy.angle) - 180);
            } else {
                game.getBatch().draw(planeAnimation.getKeyFrame(elapsedTime, true), enemy.playerX, enemy.playerY, anotherR.getWidth() / 2, anotherR.getHeight() / 2,
                        anotherR.getWidth(), anotherR.getHeight(), 1, 1, (float) Math.toDegrees(enemy.angle) - 180);
            }
            anPosX = enemy.playerX;
            anPosY = enemy.playerY;
            if (joystick.getJoystickIsTouched()) {
                angle = joystick.getRadians();
                player.playerX += playerStep * Gdx.graphics.getDeltaTime() * Math.cos(angle);
                player.playerY += playerStep * Gdx.graphics.getDeltaTime() * Math.sin(angle);
                player.angle = angle;
                game.getBatch().draw(planeAnimation.getKeyFrame(elapsedTime, true), player.playerX, player.playerY, playerR.getWidth() / 2, playerR.getHeight() / 2,
                        playerR.getWidth(), playerR.getHeight(), 1, 1, (float)Math.toDegrees(player.angle) - 180);
            } else {
                game.getBatch().draw(playerT, player.playerX, player.playerY,
                        playerR.getWidth() / 2, playerR.getHeight() / 2 ,
                        playerR.getWidth(), playerR.getHeight(), 1, 1, (float)Math.toDegrees(angle) - 90, true);
            }
            for(PBullet r: enemyB){
                game.getBatch().draw(pBulletT , r.getX() * screenWidth, r.getY() * screenHeight,
                        r.getWidth() / 2, r.getHeight()/ 2,
                        r.getWidth(), r.getHeight(),1, 1, (float)Math.toDegrees(r.getAngle()));
            }

            for(PBullet r: plRatB){
                game.getBatch().draw(pBulletT , r.getX() * screenWidth, r.getY() * screenHeight,
                        r.getWidth() / 2, r.getHeight()/ 2,
                        r.getWidth(), r.getHeight(),1, 1, (float)Math.toDegrees(r.getAngle()));
            }
            /*update.posX = player.playerX / screenWidth;
            update.posY = player.playerY / screenHeight;
            update.angle = player.angle;
            update.hp = player.hp;
            server.sendToAllUDP(update);
            updateB.bullets = plRatB;
            server.sendToAllUDP(updateB);*/
            if(!thrUp.isAlive())
            thrUp.run();

        } else {
            if(!roundLost)
            game.getFont().draw(game.getBatch(), "  IP:" + address + "\nWaiting for 2-nd Player",
                    col_width * 1.5f, row_height * 6);
        }
        game.getBatch().end();

        if(Gdx.input.isKeyPressed(Input.Keys.ENTER) || shootPressed ){
            if(canShot){
                createPlayerBullet();
                canShot = false;
            }
        }

        //top
        if(player.playerY >= screenHeight - playerR.getHeight() - row_height * 0.7f){
            player.playerY = screenHeight - playerR.getHeight() - row_height * 0.7f;
        }

        //right
        if(player.playerX >= screenWidth - playerR.getWidth()){
            player.playerX = screenWidth - playerR.getWidth();
        }

        //bottom
        if(player.playerY < row_height * 3.2){
            player.playerY = row_height * 3.2f;
        }

        //left
        if(player.playerX < 0){
            player.playerX = 0;
        }


        if(System.currentTimeMillis() - lastPBulletShotTime > 1000){
            canShot = true;
        }
    }

    private void createPlayerBullet(){
        PBullet bulletP = new PBullet();
        bulletP.setWidth(35 * ratW);
        bulletP.setHeight(35 * ratH);
        bulletP.setAngle(angle);
        bulletP.setPosition(player.playerX / screenWidth, (player.playerY + playerR.getHeight() / 2) / screenHeight);
        plRatB.add(bulletP);
        lastPBulletShotTime = System.currentTimeMillis();
        valueShotReload = 0;
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
        try {
            server.close();
        } catch (Exception ex){

        }
        stage.dispose();
    }


}

