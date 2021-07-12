package com.mygdx.game.Find;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.ChooseScreen;
import com.mygdx.game.FOG;
import com.mygdx.game.FailScreen;
import com.mygdx.game.Find.Entity.Target;
import com.mygdx.game.gameUtils.Joystick;
import com.mygdx.game.WinScreen;

public class FindEasy extends ApplicationAdapter implements Screen {
    //Экран игры с режимом Find и сложностью Easy
    FOG game;
    //Обьявление переменных
    Camera camera;
    Viewport viewport;
    Stage stage, stageBackground;
    float col_width, row_height;
    float screenWidth, screenHeight;
    Skin skin, skinOptional;
    Image backgroundImg;
    TextureRegion player, lootTxt;
    Rectangle playerR, pSmallR, canFindR;
    Polygon playerPoly;
    Joystick joystick;
    ShapeRenderer shapeRenderer;
    Music backgroundMusic;


    Label scoreLbl, timeLbl, rangeLbl;
    ImageButton lootButton, backBtn;
    Target target;

    int lootUpgrade = 0;
    double angle = 0, ratioW, ratioH;
    float xPlayer , yPlayer;
    float yScreen, xScreen;
    float backGndWidth, backGndHeight;
    int playerStep = 300;
    float timer = 35;
    int goalLoot = 70;
    float ratW, ratH;
    double looted = 0, range;
    boolean lootPressed, targetDone = false;
    Sound  shootSound;
    private Preferences pref;

    //Инициализация переменных
    public FindEasy(FOG _game){
        game = _game;
        ratW = (float)Gdx.graphics.getWidth() / 1080;
        ratH = (float)Gdx.graphics.getHeight() / 1920;
        playerStep *= ratW;
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        col_width = screenWidth / 12;
        row_height = screenHeight / 12;
        //Создания двух stage бля бэкграунда и экрана игрока
        stage = new Stage(new ScreenViewport());
        stageBackground = new Stage(new ScreenViewport());

        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("skinHolo/uiskin.json"));
        skinOptional = new Skin(Gdx.files.internal("skin/glassy-ui.json"));
        player = new TextureRegion(new Texture(Gdx.files.internal("Find/FOGAgent.png")));
        lootTxt = new TextureRegion(new Texture(Gdx.files.internal("Find/looting.png")));
        pref = Gdx.app.getPreferences("Find Upgrade");
        lootUpgrade = pref.getInteger("lootSpeed");
        shapeRenderer = new ShapeRenderer();
        backGndWidth = stageBackground.getWidth();
        backGndHeight = stageBackground.getHeight();
        ratioW = 1 / 12;
        ratioH =  1 / 12;

        camera = stageBackground.getViewport().getCamera();
        camera.viewportWidth = col_width;
        camera.viewportHeight = row_height;
        playerR = new Rectangle();
        playerR.setWidth(180 * ratW);
        playerR.setHeight(200 * ratH);
        pSmallR = new Rectangle();
        pSmallR.setWidth((float)(180 * ratioW * ratW));
        pSmallR.setHeight((float)((200 * ratioH * ratH)));
        canFindR = new Rectangle();
        canFindR.set(col_width * 0.7f, row_height * 3.2f, col_width * 1.5f, row_height * 0.7f);
        playerPoly = new Polygon(new float[]{playerR.x, playerR.y,
                playerR.x, playerR.y + playerR.height,
                playerR.x + playerR.width, playerR.y + playerR.height,
                playerR.x + playerR.width, playerR.y});
        playerPoly.setOrigin(playerR.getWidth() / 2, playerR.getHeight() / 2);
        xPlayer = screenWidth / 2;
        yPlayer = screenHeight / 2;
        shootSound = Gdx.audio.newSound(Gdx.files.internal("Shoot/player/sound/shootSound.mp3"));


        //Background Image
        backgroundImg = new Image(new TextureRegionDrawable(new Texture(Gdx.files.internal("Shoot/player/images/gunBackground.png"))));
        backgroundImg.setPosition(0, 0);
        backgroundImg.setSize(screenWidth, screenHeight);
        stageBackground.addActor(backgroundImg);
        //Add target
        target = new Target(new Texture(Gdx.files.internal("Find/Nexus.png")), col_width / 2, row_height / 4);
        //Find/Nexus.png
        //target.setPosition(xPlayer, yPlayer);
        target.setPosition(xScreen + MathUtils.random(backGndWidth / 4, screenWidth - target.getWidth() - backGndWidth / 4),
               yScreen +  MathUtils.random(backGndHeight / 3, screenHeight - target.getHeight() - backGndHeight / 5));
        target.setSize(col_width, row_height / 2);
        target.getHelpRec().setPosition(target.getX() + target.getHelpRec().getWidth() ,
                target.getY() + target.getHelpRec().getHeight() );
        target.setSelfRec(new Rectangle().set(target.getX() , target.getY() ,
                 target.getWidth(),
                target.getHeight()));
        stageBackground.addActor(target);

        //Time lbl
        timeLbl = new Label("Time remained:\n  " + (int)timer, skinOptional, "default");
        timeLbl.setSize(col_width * 2, row_height * 2);
        timeLbl.setFontScale(4 * ratW,  4 * ratH);
        timeLbl.setAlignment(Align.center);
        timeLbl.setPosition(screenWidth - (float)(col_width * 7), screenHeight - (float)(row_height * 2.2));
        timeLbl.setColor(1,0,0,1);
        stage.addActor(timeLbl);
        //Score lbl
        scoreLbl = new Label(" Goal to save info:\n" + (int) looted + "/" + goalLoot, skinOptional, "default");
        scoreLbl.setSize(col_width * 2, row_height * 2);
        scoreLbl.setFontScale(4 * ratW, 4 * ratH);
        scoreLbl.setAlignment(Align.center);
        scoreLbl.setPosition(screenWidth - (float)(col_width * 7), screenHeight - (float)(row_height * 3.2));
        scoreLbl.setColor(1,0,0,1);
        stage.addActor(scoreLbl);
        // Range Label
        rangeLbl = new Label("Range to target:" + range, skinOptional, "default");
        rangeLbl.setSize(col_width * 2, row_height * 2);
        rangeLbl.setFontScale(3 * ratW, 3 * ratH);
        rangeLbl.setAlignment(Align.center);
        rangeLbl.setPosition(screenWidth - col_width * 6.8f, row_height * 2.8f);
        rangeLbl.setColor(1, 0 , 0, 1);
        stage.addActor(rangeLbl);

        //Shoot button
        lootButton = new ImageButton(skin, "colored");
        Drawable lootButtonBackground = lootButton.getBackground();
        ImageButton.ImageButtonStyle lootButtonStyle = new ImageButton.ImageButtonStyle();
        lootButtonStyle.imageUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("Find/lootHand.png"))));
        lootButtonStyle.imageDown = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("Find/lootHand.png"))));
        lootButtonStyle.up = lootButtonBackground;
        lootButtonStyle.down = lootButton.getStyle().down;
        lootButton.setStyle(lootButtonStyle);
        lootButton.setSize(col_width * 3f,row_height * 1.7f);
        lootButton.setPosition((float)(col_width * 0.5), (float)(row_height * 1.3));
        lootButton.addListener(new InputListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                lootPressed = false;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                lootPressed = true;
                return true;
            }
        });
        stage.addActor(lootButton);

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
        //Joystick
        joystick = new Joystick(ratW, ratH);
        joystick.setX(screenWidth - (float)(col_width * 5.8));
        joystick.setY((float)(row_height * 0.8));
        stage.addActor(joystick);


    }
    //Инициализация музыки
    @Override
    public void show() {
           backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("findBackMusic.mp3"));
           backgroundMusic.play();
           backgroundMusic.setLooping(true);
    }
    //Отрисовка элементов
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.getBatch().setProjectionMatrix(camera.combined);
        timer -= Gdx.graphics.getDeltaTime();
        timeLbl.setText("Time remained:\n" + (int) timer);
        game.getBatch().begin();
        stageBackground.act();
        stageBackground.draw();
        stage.act();
        stage.draw();

        if (joystick.getJoystickIsTouched()) {
            //double x = joystick.getValueX();
            //double y = joystick.getValueY();
            angle = joystick.getRadians();
            xPlayer += (float) (playerStep * Gdx.graphics.getDeltaTime() * Math.cos(angle));
            yPlayer += (float) (playerStep * Gdx.graphics.getDeltaTime() * Math.sin(angle));
        }
        range = Math.sqrt(Math.pow(xPlayer - target.getCenterX(), 2)
                + Math.pow(yPlayer -  target.getCenterY(), 2));
        rangeLbl.setText("Range to target:" + (int) range);
        playerPoly.setRotation((float) Math.toDegrees(angle) - 90);
        playerPoly.setPosition(xPlayer + xScreen, yPlayer + yScreen);
        game.getBatch().draw(player, xPlayer, yPlayer,
                xScreen + playerR.getWidth() / 2, yScreen + playerR.getHeight() / 2,
                playerR.getWidth(), playerR.getHeight(), 1, 1, (float) Math.toDegrees(angle) - 90, true);
        game.getBatch().end();
         shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //shapeRenderer.setColor(0, 1, 0 , 1);
        //shapeRenderer.rect(target.getHelpRec().getX(), target.getHelpRec().getY(),
        //        target.getHelpRec().getWidth(), target.getHelpRec().getHeight());
        //shapeRenderer.rect(target.getSelfRec().getX(), target.getSelfRec().getY(),
        //      target.getSelfRec().getWidth(), target.getSelfRec().getHeight());
        if(range < 120 * ratW){
            shapeRenderer.setColor(0, 1, 0, 1);
        } else {
            shapeRenderer.setColor(1, 0, 0 ,1);
        }
        shapeRenderer.rect(canFindR.getX(), canFindR.getY(), canFindR.getWidth(), canFindR.getHeight());
        shapeRenderer.end();
        camera.position.set(xPlayer, yPlayer, 0);
        camera.update();
        playerR.setX(xPlayer);
        playerR.setY(yPlayer);
        pSmallR.setPosition(xPlayer, yPlayer);
        yScreen = stageBackground.getViewport().getScreenY();
        xScreen = stageBackground.getViewport().getScreenX();

        //right
        if (xScreen + stageBackground.getViewport().getScreenWidth() - col_width < playerR.getX() + playerR.getWidth()) {
            xPlayer = xScreen + stageBackground.getViewport().getScreenWidth() - playerR.getWidth() - col_width;
        }

        //bottom
        if (yScreen + row_height * 3 > playerR.getY()) {
            yPlayer = yScreen + row_height * 3;
        }

        //left
        if (xScreen + col_width > playerR.getX()) {
            xPlayer = xScreen + col_width;
        }

        //top
        if (xScreen + stageBackground.getViewport().getScreenHeight() - playerR.getHeight() < playerR.getY()) {
            yPlayer = xScreen + stageBackground.getViewport().getScreenHeight() - playerR.getHeight();
        }


        if (range <= 120 * ratW
                && lootPressed) {
            looted += 0.2f + lootUpgrade * 0.05f;
            target.setLootSize(target.getLootSize() - (0.2f + lootUpgrade * 0.05f));
            if (target.getLootSize() <= 0) {
                target.setPosition(xScreen + MathUtils.random(backGndWidth  * 0.4f, screenWidth -
                                target.getWidth() - backGndWidth * 0.2f),
                        yScreen + MathUtils.random(backGndHeight  * 0.33f, screenHeight - target.getHeight() - backGndHeight * 0.2f));

                target.getHelpRec().setPosition(target.getX() + target.getHelpRec().getWidth(),
                        target.getY() + target.getHelpRec().getHeight());
                target.getSelfRec().setPosition(target.getX(), target.getY());
                target.setLootSize(30);
            }
                if (goalLoot <= looted) {
                    scoreLbl.setText("All information saved\n" + "Extra material: " + (int) (looted - goalLoot));
                    targetDone = true;
                } else {
                    scoreLbl.setText(" Goal to save info:\n" + (int) looted + "/" + goalLoot);
                }

        }

        if (timer <= 0) {
            if (targetDone) {
                game.setScreen(new WinScreen(game, (int) looted, 0, (int) looted, 2));
                dispose();
            } else {
                game.setScreen(new FailScreen(game, (int) looted, 2, (int) looted, 0));
                dispose();
            }
        }


    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        backgroundMusic.stop();
        backgroundMusic.dispose();
        stage.dispose();
    }
}
