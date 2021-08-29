package com.mygdx.game.Gun;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.ChooseScreen;
import com.mygdx.game.FailScreen;
import com.mygdx.game.FOG;
import com.mygdx.game.WinScreen;
import com.mygdx.game.gameUtils.Joystick;
import com.mygdx.game.Gun.Entity.PBullet;


public class GunEasy extends ApplicationAdapter implements Screen {
	//Экран игры режима Gun, сложности Easy, назван Shoot так как при бета тесте был назван так
	Texture  bullet, boss;
	TextureRegion player,  pBullet;
	FOG game;

	//Обьявление кнопок, анимаций, переменных
	Texture animationPic;
	TextureRegion[] planeAnimationFrames;
	Animation<TextureRegion> planeAnimation;
	float elapsedTime;


	int row_height;
	int col_width;
	int screenHeight, screenWidth;

	Array<Rectangle> bullets;
	Array<PBullet> pBullets;

	long lastBulletShotTime;
	long lastPBulletShotTime;
	float reloadDuration = 1;
	float valueShotReload = 1;
	float ratW, ratH;
	int playerStep = 400;
	int bulletStep = 300;
	int pBulletStep = 700;
	Rectangle playerR, bossR;

	int xPlayer , yPlayer;
	float bossX, bossY;
	int HPPLayer = 100, HPBoss = 200;
	double angle;
	int bulletUpgraden;
	boolean canShot = true, bossExist = false;
	int scorePlayer = 0;
	int enemyDestroyed = 0;

	BitmapFont hpBarBP, shotLblBP, bossHPBP;
	Stage stage;
    ProgressBar PBHPPlayer, PBHPBoss, PBReload;
    Label HpLbl, BossHpLbl ,  scoreLbl;
    ImageButton  shootButton, backBtn;
    Joystick joystick;
    Image backgroundImg;
    Skin skin, skinOptional;

    Sound hitSound, shootSound, enemyShootSound;
    Music backgroundMusic;


    private Preferences pref;
    boolean  rightPressed = false, upPressed = false, shootPressed = false;
	//Инициализация переменных
	public GunEasy(final FOG game){
		this.game = game;
		ratH = (float)Gdx.graphics.getHeight() / 1920;
		ratW = (float)Gdx.graphics.getWidth() / 1080;
		playerStep *= ratW;
		bulletStep *= ratW;
		pBulletStep *= ratH;
		initializeStage();
		createNewBullet();
		pref = Gdx.app.getPreferences("Shoot Upgrade");
		int hpUpgraden = pref.getInteger("hp");
		bulletUpgraden = pref.getInteger("bullet");
		HPPLayer = 100 + hpUpgraden * 10;
		createPlayerAnimation();
	}

	@Override
	public void create () {

	}

	@Override
	public void render () {

	}

	@Override
	public void show() {
		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("backgroundmusic.mp3"));
		backgroundMusic.setLooping(true);
		backgroundMusic.play();
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
	//Функция для инициализация всех stage элементов и других
	private void initializeStage(){
		player = new TextureRegion(new Texture(Gdx.files.internal("Shoot/player/playerNoPower.png")));
		boss = new Texture(Gdx.files.internal("Shoot/player/enemy.png"));
		bullet = new Texture(Gdx.files.internal("Shoot/player/bossShot.png"));
		pBullet = new TextureRegion(new Texture(Gdx.files.internal("Shoot/player/playerShot.png")));
		skin = new Skin(Gdx.files.internal("skinHolo/uiskin.json"));
		skinOptional = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);

		row_height = Gdx.graphics.getHeight() / 12;
		col_width = Gdx.graphics.getWidth() / 12;

		playerR = new Rectangle();
		bossR = new Rectangle();

		playerR.setWidth(200 * ratW);
		playerR.setHeight(200 * ratH);

		bossR.setWidth(150 * ratW);
		bossR.setHeight(100 * ratH);

		bullets = new Array<>();
		pBullets = new Array<>();

		screenHeight = Gdx.graphics.getHeight();
		screenWidth = Gdx.graphics.getWidth();

		xPlayer = screenHeight / 2;
		yPlayer = screenWidth / 2;

		hpBarBP = new BitmapFont();
		shotLblBP = new BitmapFont();
		bossHPBP = new BitmapFont();

		hitSound = Gdx.audio.newSound(Gdx.files.internal("Shoot/player/sound/hitsound.mp3"));
		shootSound = Gdx.audio.newSound(Gdx.files.internal("Shoot/player/sound/shootSound.mp3"));
		enemyShootSound = Gdx.audio.newSound(Gdx.files.internal("Shoot/player/sound/enemyShootSound.mp3"));
		//Background Image
        backgroundImg = new Image(new TextureRegionDrawable(new Texture(Gdx.files.internal("Shoot/player/images/gunBackground.png"))));
        backgroundImg.setPosition(0, 0);
        backgroundImg.setSize(screenWidth, screenHeight);
        stage.addActor(backgroundImg);
		//Progress bar PlayerHP
		PBHPPlayer = new ProgressBar(0, HPPLayer ,5,  false, skinOptional, "default-horizontal");
		PBHPPlayer.setPosition(screenWidth - (float)(col_width * 5.3), screenHeight - (float)(row_height * 0.7));
		PBHPPlayer.setSize(col_width * 5, PBHPPlayer.getPrefHeight());
		PBHPPlayer.setAnimateDuration((float)0.5);
		stage.addActor(PBHPPlayer);
		//Score lbl
		scoreLbl = new Label("Your Score:\n  " + scorePlayer, skinOptional, "default");
		scoreLbl.setSize(col_width * 2, row_height * 2);
		scoreLbl.setFontScale(2 * ratW, 2 * ratH);
		scoreLbl.setAlignment(Align.center);
		scoreLbl.setPosition(screenWidth - (float)(col_width * 7), screenHeight - (float)(row_height * 3));
		scoreLbl.setColor(1,0,0,1);
		stage.addActor(scoreLbl);
		//HP lbl
		HpLbl = new Label("YOUR HP", skinOptional, "black");
		HpLbl.setFontScale(1.5f * ratW, 1.5f * ratH);
		HpLbl.setSize(col_width, row_height);
		HpLbl.setPosition(screenWidth - (float)(col_width * 5.3), screenHeight - (float)(row_height * 1.7));
		HpLbl.setColor(1, 0 , 0, 1);
		stage.addActor(HpLbl);
		//Progress bar BossHP
		PBHPBoss = new ProgressBar(0, HPBoss, 5, false, skinOptional, "default-horizontal");
		PBHPBoss.setPosition( (float)(col_width * 0.8), screenHeight - (float)(row_height * 0.7));
		PBHPBoss.setSize(col_width * 5, PBHPPlayer.getPrefHeight());
		PBHPBoss.setAnimateDuration((float)0.5);
		stage.addActor(PBHPBoss);
		//Boss HP lbl
		BossHpLbl = new Label("BOSS HP", skinOptional, "black");
		BossHpLbl.setFontScale(1.5f * ratW, 1.5f * ratH);
		BossHpLbl.setSize(col_width, row_height);
		BossHpLbl.setPosition((float)(col_width * 0.8), screenHeight - (float)(row_height * 1.7));
		BossHpLbl.setColor(0, 0 , 1, 1);
		stage.addActor(BossHpLbl);
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
		stage.addActor(shootButton);
		//Reload Progress Bar
		PBReload = new ProgressBar(0 , reloadDuration, 0.05f, false, skinOptional, "default-horizontal" );
		PBReload.setPosition(col_width * 0.5f, row_height * 3.3f);
		PBReload.setSize(col_width * 3f, PBReload.getPrefHeight());
		PBReload.setAnimateDuration(0.05f);
		stage.addActor(PBReload);
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
				game.setScreen(new ChooseScreen(game));
				dispose();
				return false;
			}
		});
		stage.addActor(backBtn);
	}
	//Фукнция для изменение пропорция при изменение положения экрана, в последствии такая функция была убрана
	@Override
	public void resize(int width, int height) {
		game.getBatch().getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		screenWidth = Gdx.graphics.getWidth();
		screenHeight  = Gdx.graphics.getHeight();
		row_height = Gdx.graphics.getHeight() / 12;
		col_width = Gdx.graphics.getWidth() / 12;
		initializeStage();
	}
	//Отрисовка игрока и противника
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.getBatch().begin();
		stage.act();
		stage.draw();
		PBHPPlayer.setValue(HPPLayer);
		PBHPBoss.setValue(HPBoss);
		PBReload.setValue(valueShotReload);
		elapsedTime += Gdx.graphics.getDeltaTime();
		valueShotReload += Gdx.graphics.getDeltaTime();
		//game.getBatch().draw(planeAnimation.getKeyFrame(elapsedTime, true), xPlayer, yPlayer, playerR.getWidth(), playerR.getHeight());
		for(Rectangle r: bullets){
			game.getBatch().draw(bullet,r.getX(),r.getY(), r.getWidth(), r.getHeight());
			if(r.getX() < col_width * 7.5f) {
				game.getBatch().draw(boss, 0, r.getY(), bossR.getWidth(), bossR.getHeight());
				bossX = 0;
				bossY = r.getY();
				bossExist = true;
			} else {
				bossExist = false;
			}
		}

		for(PBullet r: pBullets){
			game.getBatch().draw(pBullet, r.getX(), r.getY(), r.getWidth() / 2, r.getHeight()/ 2,
					r.getWidth(), r.getHeight(),1, 1, (float)Math.toDegrees(r.getAngle()));
		}

		if(joystick.getJoystickIsTouched()) {
			angle = joystick.getRadians();
			game.getBatch().draw(planeAnimation.getKeyFrame(elapsedTime, true), xPlayer, yPlayer,
					playerR.getWidth() / 2, playerR.getHeight() / 2 ,
					playerR.getWidth(), playerR.getHeight(), 1, 1, (float)Math.toDegrees(angle) - 90, true);
			xPlayer += playerStep * Gdx.graphics.getDeltaTime() * Math.cos(angle);
			yPlayer += playerStep * Gdx.graphics.getDeltaTime() * Math.sin(angle);
		} else {
			game.getBatch().draw(player, xPlayer, yPlayer,
					playerR.getWidth() / 2, playerR.getHeight() / 2 ,
					playerR.getWidth(), playerR.getHeight(), 1, 1, (float)Math.toDegrees(angle) - 90, true);
		}

		game.getBatch().end();

		if(HPPLayer <= 0){
			game.setScreen(new FailScreen(game, scorePlayer, 0, enemyDestroyed, 0));
			dispose();
		}

		if(HPBoss <= 0){
			game.setScreen(new WinScreen(game, scorePlayer, 0, enemyDestroyed, 0));
			dispose();
		}


		if(Gdx.input.isKeyPressed(Input.Keys.ENTER) || shootPressed ){
			if(canShot){
				createPlayerBullet();
				canShot = false;
			}
		}

		playerR.setY(yPlayer);
		playerR.setX(xPlayer);
		bossR.setY(bossY);
		bossR.setX(bossX);
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

		for(Rectangle r:bullets){
			r.setX(r.getX() + bulletStep * Gdx.graphics.getDeltaTime());
			if(r.getX() - r.getWidth() > screenWidth){
				bullets.removeValue(r, true);
			}
			if(r.overlaps(playerR)){
				//player = new Texture(Gdx.files.internal("sadplayer.png"));
				bullets.removeValue(r, true);
				HPPLayer -= 10;
				bossExist = false;
				hitSound.play();
			}
		}

		for(PBullet r: pBullets){
			r.setX(r.getX() + (float)(pBulletStep * Gdx.graphics.getDeltaTime() *  Math.cos(r.getAngle())));
			r.setY(r.getY() + (float)(pBulletStep * Gdx.graphics.getDeltaTime() * Math.sin(r.getAngle())));
			if(r.getX() < 0){
				pBullets.removeValue(r, true);
			}
			if(r.overlaps(bossR)){
				pBullets.removeValue(r,true);
				//player = new Texture(Gdx.files.internal("pogplayer.png"));
				HPBoss -= 10 + bulletUpgraden * 5;
				scorePlayer += 15;
				enemyDestroyed ++;
				scoreLbl.setText("Your Score:\n  " + scorePlayer);
			}
		}



		if(bossR.overlaps(playerR)){
			HPPLayer -= 1;
		}

		if(!bossExist){
			createNewBullet();
		}

		if(System.currentTimeMillis() - lastPBulletShotTime > 1000){
			canShot = true;
		}

	}

	@Override
	public void hide() {

	}

	/*private void movementApply(){
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || !rightPressed ){
			xPlayer -= playerStep * Gdx.graphics.getDeltaTime();
		}

		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || rightPressed){
			xPlayer += playerStep * Gdx.graphics.getDeltaTime();
		}

		if(Gdx.input.isKeyPressed(Input.Keys.UP) || upPressed){
			yPlayer += playerStep * Gdx.graphics.getDeltaTime();
		}

		if(Gdx.input.isKeyPressed(Input.Keys.DOWN) || !upPressed){
			yPlayer -= playerStep * Gdx.graphics.getDeltaTime();
		}
	}

	/*double x = joystick.getValueX();
			double y = joystick.getValueY();
			if(x > 0.2){
				xPlayer += playerStep * Gdx.graphics.getDeltaTime();
				rightGo = 2;
			} else if(x < - 0.2){
				xPlayer -= playerStep * Gdx.graphics.getDeltaTime();
				rightGo = 1;
			} else {
				rightGo = 0;
			}
			if(y > 0.2 ){
				yPlayer += playerStep * Gdx.graphics.getDeltaTime();
				upGo = 2;
			} else if(y < -0.2) {
				yPlayer -= playerStep * Gdx.graphics.getDeltaTime();
				upGo = 1;
			} else {
				upGo = 0;
			}
			if(rightGo == 2){
				if(upGo == 2){
					game.getBatch().draw(planeAnimation.getKeyFrame(elapsedTime, true), xPlayer, yPlayer,
							playerR.getWidth() / 2, playerR.getHeight() / 2 ,
							playerR.getWidth(), playerR.getHeight(), 1, 1, 225);
				} else if(upGo == 1){
					game.getBatch().draw(planeAnimation.getKeyFrame(elapsedTime, true), xPlayer, yPlayer,
							playerR.getWidth() / 2, playerR.getHeight() / 2 ,
							playerR.getWidth(), playerR.getHeight(), 1, 1, 135);
				} else {
					game.getBatch().draw(planeAnimation.getKeyFrame(elapsedTime, true), xPlayer, yPlayer,
							playerR.getWidth() / 2, playerR.getHeight() / 2,
							playerR.getWidth(), playerR.getHeight(), 1, 1, 180);
				}
			} else if(rightGo == 1){
				if(upGo == 2){
					game.getBatch().draw(planeAnimation.getKeyFrame(elapsedTime, true), xPlayer, yPlayer,
							playerR.getWidth() / 2, playerR.getHeight() / 2,
							playerR.getWidth(), playerR.getHeight(), 1, 1, 315);
				} else if(upGo == 1){
					game.getBatch().draw(planeAnimation.getKeyFrame(elapsedTime, true), xPlayer, yPlayer,
							playerR.getWidth() / 2, playerR.getHeight() / 2 ,
							playerR.getWidth(), playerR.getHeight(), 1, 1, 45);
				} else {
					game.getBatch().draw(planeAnimation.getKeyFrame(elapsedTime, true), xPlayer, yPlayer,
							playerR.getWidth() / 2, playerR.getHeight() / 2 ,
							playerR.getWidth(), playerR.getHeight(), 1, 1, 0);
				}
			} else {
				if(upGo == 2){
					game.getBatch().draw(planeAnimation.getKeyFrame(elapsedTime, true), xPlayer, yPlayer,
							playerR.getWidth() / 2, playerR.getHeight() / 2,
							playerR.getWidth(), playerR.getHeight(), 1, 1, 270);

				} else if(upGo == 1){
					game.getBatch().draw(planeAnimation.getKeyFrame(elapsedTime, true), xPlayer, yPlayer,
							playerR.getWidth() / 2, playerR.getHeight() / 2,
							playerR.getWidth(), playerR.getHeight(), 1, 1, 90);
				}
			}
		} else {
			game.getBatch().draw(planeAnimation.getKeyFrame(elapsedTime, true), xPlayer, yPlayer,
					playerR.getWidth() / 2, playerR.getHeight() / 2,
					playerR.getWidth(), playerR.getHeight(), 1, 1, 0);

		}*/
	//Функции для создания пуль игрока и противника
	private void createNewBullet(){
		Rectangle bullet = new Rectangle();
		bullet.setWidth(40 * ratW);
		bullet.setHeight(50 * ratH);
		bullet.setY(MathUtils.random((int)(row_height * 3.3), screenHeight - bullet.getHeight() - bossR.getHeight() - row_height * 3));
		bullet.setX(0);
		bullets.add(bullet);
		lastBulletShotTime = System.currentTimeMillis();
		enemyShootSound.play();
	}

	public void createPlayerBullet(){
		PBullet bulletP = new PBullet();
		bulletP.setWidth(35 * ratW);
		bulletP.setHeight(35 * ratH);
		bulletP.setY(yPlayer + playerR.getHeight() / 2);
		bulletP.setX(xPlayer);
		bulletP.setAngle(angle);
		pBullets.add(bulletP);
		lastPBulletShotTime = System.currentTimeMillis();
		shootSound.play();
		valueShotReload = 0;
	}

	@Override
	public void dispose () {
		    backgroundMusic.setLooping(false);
			backgroundMusic.stop();
			backgroundMusic.dispose();
		bullet.dispose();
		hpBarBP.dispose();
		shotLblBP.dispose();
		bossHPBP.dispose();
	}


}
