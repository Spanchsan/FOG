package com.mygdx.game.Object;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.ChooseScreen;
import com.mygdx.game.FOG;
import com.mygdx.game.FailScreen;
import com.mygdx.game.gameUtils.Joystick;
import com.mygdx.game.Object.Entity.Crystal;
import com.mygdx.game.WinScreen;

public class ObjectHard extends ApplicationAdapter implements Screen {
    //Экран игры режима Object сложности Hard
    FOG game;
    //Обьявление перемменных
    Texture player, crystal;

    int row_height;
    int col_width;
    int screenHeight, screenWidth;

    long lastCrystalSpawnTime;

    int xPlayer , yPlayer;

    Rectangle playerR;
    Polygon playerPoly;
    Music backgroundMusic;

    Array<Crystal> crystals;

    int drillSpeedUpgrade = 0;
    float drillPoint = 0.1f;

    double angle;
    int playerStep = 280;
    int crystalHPMax = 350;
    int goalPoint = 150;
    int crystalDestroyed = 0;
    float points = 0;
    float timer = 35;
    float ratW, ratH;

    ImageButton drillButton, backBtn;
    Label scoreLbl, timeLbl;
    Skin skin, skinOptional;
    Image backgroundImg;

    Joystick joystick;

    Texture animationPicLeft, animationPicRight, partPic;
    TextureRegion[] roverAnimationFrames, roverAnimationFrames1, particleFrames;
    Animation<TextureRegion> roverAnimation, roverAnimationRight,  particleAnimation;
    float elapsedTime;

    Stage stage;

    boolean drillPressed = false;

    private Preferences pref;
    //Инициализация Анимации игрока в двух направлениях
    private void createPlayerAnimation(){
        animationPicLeft = new Texture(Gdx.files.internal("animations/roveranimation.png"));
        animationPicRight = new Texture(Gdx.files.internal("animations/roveranimationRight.png"));
        TextureRegion[][] tmpFrames = TextureRegion.split(animationPicLeft, 481,160);
        TextureRegion[][] tmpFrames1 = TextureRegion.split(animationPicRight, 481, 160);
        roverAnimationFrames = new TextureRegion[9];
        roverAnimationFrames1 = new TextureRegion[9];
        int index = 0;

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j ++){
                roverAnimationFrames1[index] = tmpFrames1[i][j];
                roverAnimationFrames[index++] = tmpFrames[i][j];
            }
        }

        roverAnimationRight = new Animation(1f/9f, roverAnimationFrames1);
        roverAnimation = new Animation(1f/9f, roverAnimationFrames);


    }

    private void createParticleDrill(){
        partPic = new Texture(Gdx.files.internal("animations/particleanimation.png"));

        TextureRegion[][] tmpFrames = TextureRegion.split(partPic, 100,100);
        particleFrames = new TextureRegion[15];
        int index = 0;

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 5; j ++){
                particleFrames[index++] = tmpFrames[i][j];
            }
        }

        particleAnimation = new Animation(1f/15f, particleFrames);
    }
    //Инициализация данных
    public ObjectHard(FOG _game){
        game = _game;
        ratW = (float)Gdx.graphics.getWidth() / 1080;
        ratH = (float)Gdx.graphics.getHeight() / 1920;
        playerStep *= ratW;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("skinHolo/uiskin.json"));
        skinOptional = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

        pref = Gdx.app.getPreferences("Object Upgrade");
        drillSpeedUpgrade = pref.getInteger("drillSpeed");
        drillPoint = 0.1f + drillSpeedUpgrade * 0.02f;

        crystals = new Array<>();
        crystal = new Texture(Gdx.files.internal("Object/player/crystal.png"));
        playerR = new Rectangle();
        playerR.setWidth(120 * ratW);
        playerR.setHeight(360 * ratH);
        playerPoly = new Polygon(new float[]{playerR.x, playerR.y,
                playerR.x, playerR.y + playerR.height,
                playerR.x + playerR.width, playerR.y + playerR.height,
                playerR.x + playerR.width, playerR.y});
        playerPoly.setOrigin(playerR.getWidth() / 2, playerR.getHeight() / 2);

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        col_width = screenWidth / 12;
        row_height = screenHeight / 12;
        //Background Image
        backgroundImg = new Image(new TextureRegionDrawable(new Texture(Gdx.files.internal("Object/player/objectbackground.png"))));
        backgroundImg.setPosition(0, 0);
        backgroundImg.setSize(screenWidth, screenHeight);
        stage.addActor(backgroundImg);
        //Time lbl
        timeLbl = new Label("Time remained:\n  " + (int)timer, skinOptional, "default");
        timeLbl.setSize(col_width * 2, row_height * 2);
        timeLbl.setFontScale(4 * ratW, 4 * ratH);
        timeLbl.setAlignment(Align.center);
        timeLbl.setPosition(screenWidth - (float)(col_width * 7), screenHeight - (float)(row_height * 2.2));
        timeLbl.setColor(0,0,1,1);
        stage.addActor(timeLbl);
        //Score lbl
        scoreLbl = new Label("Your Score:\n  " + (int)points + "->"
                + " Goal:" + goalPoint, skinOptional, "default");
        scoreLbl.setSize(col_width * 2, row_height * 2);
        scoreLbl.setFontScale(4 * ratW, 4 * ratH);
        scoreLbl.setAlignment(Align.center);
        scoreLbl.setPosition(screenWidth - (float)(col_width * 7), screenHeight - (float)(row_height * 3.2));
        scoreLbl.setColor(0,0,2,1);
        stage.addActor(scoreLbl);
        //Joystick
        joystick = new Joystick(ratW, ratH);
        joystick.setX(screenWidth - (float)(col_width * 5.8));
        joystick.setY((float)(row_height * 0.8));
        stage.addActor(joystick);
        //Shoot button
        drillButton = new ImageButton(skin, "colored");
        Drawable shootButtonBackground = drillButton.getBackground();
        ImageButton.ImageButtonStyle drillButtonStyle = new ImageButton.ImageButtonStyle();
        drillButtonStyle.imageUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("Object/player/pickdrill.png"))));
        drillButtonStyle.imageDown = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("Object/player/pickdrill.png"))));
        drillButtonStyle.up = shootButtonBackground;
        drillButtonStyle.down = drillButton.getStyle().down;
        drillButton.setStyle(drillButtonStyle);
        drillButton.setSize(col_width * 3f,row_height * 1.7f);
        drillButton.setPosition((float)(col_width * 0.5), (float)(row_height * 1.3));
        drillButton.addListener(new InputListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                drillPressed = false;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                drillPressed = true;
                return true;
            }
        });
        stage.addActor(drillButton);
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
                game.setScreen(new ChooseScreen(game));
                dispose();
                return false;
            }
        });
        stage.addActor(backBtn);
        createPlayerAnimation();
        createParticleDrill();
        createCrystal();
    }
    //Инициализация музыки
    @Override
    public void show() {
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("objectBackMusic.mp3"));
        backgroundMusic.play();
        backgroundMusic.setLooping(true);
    }
    //Отрисовка текстур
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.getBatch().begin();
        elapsedTime += Gdx.graphics.getDeltaTime();
        timer -= Gdx.graphics.getDeltaTime();
        timeLbl.setText("Time remained:\n"  + (int)timer);
        stage.act();
        stage.draw();
        for(Crystal cr: crystals){
            game.getBatch().draw(crystal, cr.getX(), cr.getY(), cr.getWidth(), cr.getHeight());
            if(Intersector.overlapConvexPolygons(playerPoly, cr.getPoly()) && drillPressed  ){
                cr.setHp(cr.getHp() - 2);
                Gdx.input.vibrate(10);
                game.getBatch().draw(particleAnimation.getKeyFrame(elapsedTime, true), cr.getX() - 20 * ratW, cr.getY() - cr.getWidth() / 2 + 20,
                        80 * ratW, 80 * ratH);
                points += drillPoint;
                scoreLbl.setText("Your Score:\n  " + (int)points + "->"
                        + " Goal:" + goalPoint);
            }

            if(cr.getHp() <= 0){
                crystalDestroyed++;
            }

            if(joystick.getJoystickIsTouched()) {
                angle = joystick.getRadians();
                xPlayer += playerStep * Gdx.graphics.getDeltaTime() * Math.cos(angle);
                yPlayer += playerStep * Gdx.graphics.getDeltaTime() * Math.sin(angle);
            }
            playerPoly.setRotation((float)Math.toDegrees(angle) - 90);
            playerPoly.setPosition(xPlayer, yPlayer);

            if (Math.toDegrees(angle) > 90 || Math.toDegrees(angle) < -90) {
                game.getBatch().draw(roverAnimation.getKeyFrame(elapsedTime, true), xPlayer, yPlayer,
                        playerR.getWidth() / 2, playerR.getHeight() / 2,
                        playerR.getWidth(), playerR.getHeight(), 1, 1, (float) Math.toDegrees(angle) - 90, true);
            } else {
                game.getBatch().draw(roverAnimationRight.getKeyFrame(elapsedTime, true), xPlayer, yPlayer,
                        playerR.getWidth() / 2, playerR.getHeight() / 2 ,
                        playerR.getWidth(), playerR.getHeight(), 1, 1, (float)Math.toDegrees(angle) - 270 , true);
            }
        }
        game.getBatch().end();

        playerR.setX(xPlayer);
        playerR.setY(yPlayer);


        if(timer < 0){
            if(goalPoint <= points){
                game.setScreen(new WinScreen(game, (int) points, 2, crystalDestroyed, 1));
                dispose();
            } else {
                game.setScreen(new FailScreen(game, (int) points, 1, crystalDestroyed, 2));
                dispose();
            }
        }

        for(Crystal cr: crystals){
            if(System.currentTimeMillis() - cr.getSpawnTime() > 8000){
                crystals.removeValue(cr, true);
            }

            if(cr.getHp() < 0){
                crystals.removeValue(cr, true);
            }
        }

        //top
        if(yPlayer >= screenHeight - playerR.getHeight() - (float)(row_height * 2.7)){
            yPlayer = screenHeight - (int)playerR.getHeight() - (int)(row_height * 2.7);
        }

        //right
        if(xPlayer >= screenWidth - playerR.getWidth()){
            xPlayer = screenWidth - (int)playerR.getWidth();
        }

        //bottom
        if(yPlayer < row_height * 3.2){
            yPlayer = (int)(row_height * 3.2);
        }

        //left
        if(xPlayer < 0){
            xPlayer = 0;
        }

        if(System.currentTimeMillis() - lastCrystalSpawnTime > 2000){
            createCrystal();
        }
    }

    @Override
    public void hide() {

    }
    //Функция для создания кристала
    private void createCrystal(){
        Crystal crystal = new Crystal(crystalHPMax);
        crystal.setSpawnTime(System.currentTimeMillis());
        crystal.setWidth(70 * ratW);
        crystal.setHeight(70 * ratH);
        crystal.setX(MathUtils.random(0, screenWidth - crystal.getWidth() - playerR.getWidth()));
        crystal.setY(MathUtils.random(row_height * 4, screenHeight - crystal.getHeight() - row_height * 3.5f));
        crystal.getPoly().setPosition(crystal.getX(), crystal.getY());
        crystals.add(crystal);
        lastCrystalSpawnTime = System.currentTimeMillis();
    }

    @Override
    public void dispose() {
        backgroundMusic.stop();
        backgroundMusic.dispose();
        stage.dispose();
    }
}
