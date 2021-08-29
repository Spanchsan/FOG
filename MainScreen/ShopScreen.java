package com.mygdx.game.MainScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.mygdx.game.FOG;

public class ShopScreen implements Screen {
    //Экран покупки апгрейдов
    //Обьявление переменных
    FOG game;
    Stage stage;
    Image hpImage, bulletImage, shopImage, drillImage, lootImage;
    ImageButton hpUpgradeBtn, bulletUpgradeBtn, drillUpgradeBtn, lootUpgradeBtn,  homeBtn;
    ImageButton hpInfoBtn, bulletInfoBtn, drillInfoBtn, lootInfoBtn;
    Dialog dialogInfo;
    Label pointsLbl, hpCostLbl, bulletCostLbl, drillCostLbl, lootCostLbl, dialogLbl;
    Texture redDotTxt;
    Skin skin, skinOptional;
    int row_height, col_width;
    int screenWidth, screenHeight;
    List<SpriteBatch> hpRedUpgrades;
    List<SpriteBatch> bulletRedUpgrades;
    List<SpriteBatch> drillRedUpgrades;
    List<SpriteBatch> lootRedUpgrades;
    private Preferences prefShoot, prefObject, prefFind, prefAll;
    private int hpUpgrade, bulletUpgrade, drillUpgrade, lootUpgrade;
    private int points;
    float ratW, ratH;
    boolean dialogOpened = false;
    //Инициализация
    public ShopScreen(final FOG game){
        this.game = game;
        //Переменные для масшатабирования
        ratW = (float)Gdx.graphics.getWidth() / 1080;
        ratH = (float)Gdx.graphics.getHeight() / 1920;
        row_height = Gdx.graphics.getHeight() / 12;
        col_width = Gdx.graphics.getWidth() / 12;
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("skinHolo/uiskin.json"));
        skinOptional = new Skin(Gdx.files.internal("skin/glassy-ui.json"));
        //Открытие Preferences файлы для апгрейдов
        prefShoot= Gdx.app.getPreferences("Shoot Upgrade");
        prefAll = Gdx.app.getPreferences("My Preferences");
        prefObject = Gdx.app.getPreferences("Object Upgrade");
        prefFind = Gdx.app.getPreferences("Find Upgrade");
        points = prefAll.getInteger("points");

        dialogInfo = new Dialog("Info", skin);

        hpUpgrade = prefShoot.getInteger("hp");
        hpRedUpgrades = new List<SpriteBatch>(skin);
        bulletUpgrade = prefShoot.getInteger("bullet");
        bulletRedUpgrades = new List<SpriteBatch>(skin);
        drillUpgrade = prefObject.getInteger("drillSpeed");
        drillRedUpgrades = new List<SpriteBatch>(skin);
        lootUpgrade = prefFind.getInteger("lootSpeed");
        lootRedUpgrades = new List<SpriteBatch>(skin);
        //Инициализация диалога для последущиего использования
        //Dialog Screen
        dialogInfo.getContentTable().defaults().width(col_width * 10);
        dialogInfo.getContentTable().setPosition(col_width, row_height * 3);
        dialogInfo.getTitleLabel().setFontScale(2 * ratW, 2 * ratH);
        dialogLbl = new Label("Default", skin);
        dialogLbl.setFontScale(1.4f * ratW, 1.4f * ratH);
        dialogInfo.getContentTable().defaults().align(Align.left);
        dialogInfo.text(dialogLbl);


        Drawable heartDr = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("heart.png"))));
        hpImage = new Image(heartDr);
        hpImage.setSize((float)(col_width * 2) ,(float)( row_height * 1));
        hpImage.setPosition((float)(col_width * 2.3), (float)(screenHeight - row_height * 3.3), Align.center);
        stage.addActor(hpImage);
        redDotTxt = new Texture(Gdx.files.internal("redDot.png"));
            for (int i = 0; i < hpUpgrade; i++) {
                SpriteBatch spr = new SpriteBatch();
                hpRedUpgrades.setItems(spr);

            }
        //Инициализация кнопок с внутриннеми диалоговоми окошками
        //hp Upgrade Button
        hpUpgradeBtn = new ImageButton(skin, "colored");
        Drawable hpUgradeBackground = hpUpgradeBtn.getBackground();
        ImageButton.ImageButtonStyle hpUgradeStyle = new ImageButton.ImageButtonStyle();
        hpUgradeStyle.imageUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("upgrade.png"))));
        hpUgradeStyle.imageDown = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("upgrade.png"))));
        hpUgradeStyle.up = hpUgradeBackground;
        hpUgradeStyle.down = hpUgradeBackground;
        hpUpgradeBtn.setStyle(hpUgradeStyle);
        hpUpgradeBtn.setSize((float)(col_width * 1.5), (float)(row_height * 0.75));
        hpUpgradeBtn.setPosition((float)(col_width * 0.4), screenHeight - row_height * 5);
        hpUpgradeBtn.addListener(new InputListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(hpUpgrade == 7){
                    hpUpgradeBtn.getStyle().imageUp = new TextureRegionDrawable(new TextureRegion(
                      new Texture(Gdx.files.internal("greenheart.png"))));
                    hpUpgradeBtn.getStyle().imageDown = new TextureRegionDrawable(new TextureRegion(
                            new Texture(Gdx.files.internal("greenheart.png"))));
                    hpCostLbl.setText("MAX");
                }
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                dialogOpened = true;
                dialogInfo.getButtonTable().clear();
                dialogInfo.getContentTable().clear();
                dialogLbl.setText("Increase the HP of the plane\n" +
                        "In game mode GUN");
                dialogInfo.text(dialogLbl);
                TextButton buttonUpgrade = new TextButton("Upgrade!", skin);
                if(hpUpgrade == 7){
                    buttonUpgrade.setText("MAX");
                }
                buttonUpgrade.setSize(col_width * 1.5f, row_height * 1.5f);
                buttonUpgrade.getLabel().setFontScale(1.5f * ratW, 1.5f * ratH);
                buttonUpgrade.addListener(new InputListener(){
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        if(points >= 200 + 200 * hpUpgrade && hpUpgrade < 7){
                            //Изменения данных
                            points = points - (200 + 200 * hpUpgrade);
                            prefAll.putInteger("points", points);
                            prefAll.flush();
                            hpUpgrade++;
                            prefShoot.putInteger("hp", hpUpgrade);
                            if(hpUpgrade == 7){
                                hpCostLbl.setText("MAX");
                            }
                            prefShoot.flush();
                            for (int i = 0; i < hpUpgrade; i++) {
                                SpriteBatch spr = new SpriteBatch();
                                hpRedUpgrades.setItems(spr);

                            }
                            pointsLbl.setText("Your points: " + points);
                            hpCostLbl.setText("Plane HP\nCost:" + (200 + 200 * hpUpgrade));
                            prefShoot.flush();
                        } else if(hpUpgrade >= 7){
                            hpCostLbl.setText("MAX");
                        }
                        return false;
                    }
                });
               dialogInfo.button(buttonUpgrade);

               TextButton buttonCancel = new TextButton("Cancel", skin);
               buttonCancel.setSize(col_width * 1.5f, row_height * 1.5f);
               buttonCancel.getLabel().setFontScale(1.5f * ratW, 1.5f * ratH);
               buttonCancel.addListener(new InputListener(){
                   @Override
                   public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                       dialogInfo.hide();
                       dialogOpened = false;
                       return false;
                   }
               });
               dialogInfo.button(buttonCancel);
                dialogInfo.show(stage);
                return false;
            }
            });

        stage.addActor(hpUpgradeBtn);
        //Hp Cost Label
        if(hpUpgrade < 7) {
            hpCostLbl = new Label("Plane HP\nCost:" + (200 + 200 * hpUpgrade), skinOptional, "black");
        } else {
            hpCostLbl = new Label("MAX", skinOptional, "black");
        }
        hpCostLbl.setFontScale(1.35f * ratW, 1.35f * ratH);
        hpCostLbl.setSize(col_width * 4, row_height * 2);
        hpCostLbl.setPosition((float)(col_width * 2), (float)(screenHeight - row_height * 5.65));
        stage.addActor(hpCostLbl);
        //Bullet Image
        Drawable bulletDr = new TextureRegionDrawable(new TextureRegion(new Texture(
                Gdx.files.internal("Shoot/player/playerShot.png"))));
        bulletImage = new Image(bulletDr);
        bulletImage.setSize((float)(col_width * 3) ,(float)( row_height * 0.5));
        bulletImage.setPosition((float)(screenWidth - col_width * 2.8), (float)(screenHeight - row_height * 3.3), Align.center);
        stage.addActor(bulletImage);
        redDotTxt = new Texture(Gdx.files.internal("redDot.png"));
        for (int i = 0; i < bulletUpgrade; i++) {
            SpriteBatch spr = new SpriteBatch();
            bulletRedUpgrades.setItems(spr);

        }
        //bullet Upgrade Button
        bulletUpgradeBtn = new ImageButton(skin, "colored");
        Drawable bulletUgradeBackground = hpUpgradeBtn.getBackground();
        ImageButton.ImageButtonStyle bulletUgradeStyle = new ImageButton.ImageButtonStyle();
        bulletUgradeStyle.imageUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("upgrade.png"))));
        bulletUgradeStyle.imageDown = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("upgrade.png"))));
        bulletUgradeStyle.up = bulletUgradeBackground;
        bulletUgradeStyle.down = bulletUgradeBackground;
        bulletUpgradeBtn.setStyle(bulletUgradeStyle);
        bulletUpgradeBtn.setSize((float)(col_width * 1.5), (float)(row_height * 0.75));
        bulletUpgradeBtn.setPosition((float)(screenWidth - col_width * 5.4), screenHeight - row_height * 5);
        bulletUpgradeBtn.addListener(new InputListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(bulletUpgrade >= 7){
                    bulletCostLbl.setText("MAX");
                }
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                dialogOpened = true;
                dialogInfo.getButtonTable().clear();
                dialogInfo.getContentTable().clear();
                dialogLbl.setText("Increase bullet Damage of the plane\n" +
                        "In game mode GUN");
                dialogInfo.text(dialogLbl);
                TextButton buttonUpgrade = new TextButton("Upgrade!", skin);
                if(bulletUpgrade == 7){
                    buttonUpgrade.setText("MAX");
                }
                buttonUpgrade.setSize(col_width * 1.5f, row_height * 1.5f);
                buttonUpgrade.getLabel().setFontScale(1.5f * ratW, 1.5f * ratH);
                buttonUpgrade.addListener(new InputListener(){
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        if(points >= 100 + 150 * bulletUpgrade && bulletUpgrade < 7){
                            //Изменение данных
                            points = points - (100 + 150 * bulletUpgrade);
                            prefAll.putInteger("points", points);
                            prefAll.flush();
                            bulletUpgrade++;
                            prefShoot.putInteger("bullet", bulletUpgrade);
                            if(bulletUpgrade == 7){
                                bulletCostLbl.setText("MAX");
                            }
                            prefShoot.flush();
                            for (int i = 0; i < bulletUpgrade; i++) {
                                SpriteBatch spr = new SpriteBatch();
                                bulletRedUpgrades.setItems(spr);

                            }
                            pointsLbl.setText("Your points: " + points);
                            bulletCostLbl.setText("Bullet Damage\nCost:" + (100 + 150 * bulletUpgrade));
                            prefShoot.flush();
                        } else if(bulletUpgrade >= 7){
                            bulletCostLbl.setText("MAX");
                        }
                        return false;
                    }
                });
                dialogInfo.button(buttonUpgrade);

                TextButton buttonCancel = new TextButton("Cancel", skin);
                buttonCancel.setSize(col_width * 1.5f, row_height * 1.5f);
                buttonCancel.getLabel().setFontScale(1.5f * ratW, 1.5f * ratH);
                buttonCancel.addListener(new InputListener(){
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        dialogInfo.hide();
                        dialogOpened = false;
                        return false;
                    }
                });
                dialogInfo.button(buttonCancel);
                dialogInfo.show(stage);
                return false;
            }
        });
        stage.addActor(bulletUpgradeBtn);
        //bullet Cost Label
        if(bulletUpgrade < 7) {
            bulletCostLbl = new Label("Bullet Damage\nCost:" + (100 + 150 * bulletUpgrade), skinOptional, "black");
        }else {
            bulletCostLbl = new Label("MAX", skinOptional, "black");
        }
        bulletCostLbl.setFontScale(1.35f * ratW, 1.35f * ratH);
        bulletCostLbl.setSize(col_width * 4, row_height * 2);
        bulletCostLbl.setPosition((float)(screenWidth - col_width * 3.8), (float)(screenHeight - row_height * 5.65));
        stage.addActor(bulletCostLbl);
        //Drill Image
        Drawable drillDr = new TextureRegionDrawable(new TextureRegion(new Texture(
                Gdx.files.internal("Object/player/pickdrill.png"))));
        drillImage = new Image(drillDr);
        drillImage.setSize((float)(col_width * 2) ,(float)( row_height * 1));
        drillImage.setPosition((float)(col_width * 2), (float)(screenHeight - row_height * 5.85   ), Align.center);
        stage.addActor(drillImage);
        redDotTxt = new Texture(Gdx.files.internal("redDot.png"));
        for (int i = 0; i < drillUpgrade; i++) {
            SpriteBatch spr = new SpriteBatch();
            drillRedUpgrades.setItems(spr);

        }
        //Drill Upgrade Button
        drillUpgradeBtn = new ImageButton(skin, "colored");
        Drawable drillUpgradeBackground = drillUpgradeBtn.getBackground();
        ImageButton.ImageButtonStyle drillUpgradeStyle = new ImageButton.ImageButtonStyle();
        drillUpgradeStyle.imageUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("upgrade.png"))));
        drillUpgradeStyle.imageDown = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("upgrade.png"))));
        drillUpgradeStyle.up = drillUpgradeBackground;
        drillUpgradeStyle.down = drillUpgradeBackground;
        drillUpgradeBtn.setStyle(drillUpgradeStyle);
        drillUpgradeBtn.setSize((float)(col_width * 1.5), (float)(row_height * 0.75));
        drillUpgradeBtn.setPosition((float)(col_width * 0.4), (float)(screenHeight - row_height * 7.6));
        drillUpgradeBtn.addListener(new InputListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(drillUpgrade >= 7){
                    drillCostLbl.setText("MAX");
                }
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                dialogOpened = true;
                dialogInfo.getButtonTable().clear();
                dialogInfo.getContentTable().clear();
                dialogLbl.setText("Increase drill Speed of the rover\n" +
                        "In game mode Object");
                dialogInfo.text(dialogLbl);
                TextButton buttonUpgrade = new TextButton("Upgrade!", skin);
                if(drillUpgrade == 7){
                    buttonUpgrade.setText("MAX");
                }
                buttonUpgrade.setSize(col_width * 1.5f, row_height * 1.5f);
                buttonUpgrade.getLabel().setFontScale(1.5f * ratW, 1.5f * ratH);
                buttonUpgrade.addListener(new InputListener(){
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        if(points >= 100 + 120 * drillUpgrade && drillUpgrade < 7){
                            //Изменение данных
                            points = points - (100 + 120 * drillUpgrade);
                            prefAll.putInteger("points", points);
                            prefAll.flush();
                            drillUpgrade++;
                            prefObject.putInteger("drillSpeed", drillUpgrade);
                            if(drillUpgrade == 7){
                                drillCostLbl.setText("MAX");
                            }
                            prefObject.flush();
                            for (int i = 0; i < drillUpgrade; i++) {
                                SpriteBatch spr = new SpriteBatch();
                                drillRedUpgrades.setItems(spr);

                            }
                            pointsLbl.setText("Your points: " + points);
                            drillCostLbl.setText("Drill Speed\nCost:" + (100 + 120 * drillUpgrade));
                            prefObject.flush();
                        } else if(drillUpgrade >= 7){
                            drillCostLbl.setText("MAX");
                        }
                        return false;
                    }
                });
                dialogInfo.button(buttonUpgrade);

                TextButton buttonCancel = new TextButton("Cancel", skin);
                buttonCancel.setSize(col_width * 1.5f, row_height * 1.5f);
                buttonCancel.getLabel().setFontScale(1.5f * ratW, 1.5f * ratH);
                buttonCancel.addListener(new InputListener(){
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        dialogInfo.hide();
                        dialogOpened = false;
                        return false;
                    }
                });
                dialogInfo.button(buttonCancel);
                dialogInfo.show(stage);
                return false;
            }

        });
        stage.addActor(drillUpgradeBtn);
        //Drill Cost Label
        if(drillUpgrade < 7) {
            drillCostLbl = new Label("Drill Speed\nCost:" + (100 + 120 * drillUpgrade), skinOptional, "black");
        }else {
            drillCostLbl = new Label("MAX", skinOptional, "black");
        }
        drillCostLbl.setFontScale(1.35f * ratW, 1.35f * ratH);
        drillCostLbl.setSize(col_width * 4, row_height * 2);
        drillCostLbl.setPosition((float)(col_width * 2), (float)(screenHeight - row_height * 8.25));
        stage.addActor(drillCostLbl);
        //Drill Image
        Drawable lootDr = new TextureRegionDrawable(new TextureRegion(new Texture(
                Gdx.files.internal("Find/lootHand.png"))));
        lootImage = new Image(lootDr);
        lootImage.setSize((float)(col_width * 2) ,(float)( row_height * 1));
        lootImage.setPosition((float)(screenWidth - col_width * 2.8), (float)(screenHeight - row_height * 5.85   ), Align.center);
        stage.addActor(lootImage);
        redDotTxt = new Texture(Gdx.files.internal("redDot.png"));
        for (int i = 0; i < lootUpgrade; i++) {
            SpriteBatch spr = new SpriteBatch();
            lootRedUpgrades.setItems(spr);

        }
        //Loot Upgrade Button
        lootUpgradeBtn = new ImageButton(skin, "colored");
        Drawable lootUpgradeBackground = lootUpgradeBtn.getBackground();
        ImageButton.ImageButtonStyle lootUpgradeStyle = new ImageButton.ImageButtonStyle();
        lootUpgradeStyle.imageUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("upgrade.png"))));
        lootUpgradeStyle.imageDown = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("upgrade.png"))));
        lootUpgradeStyle.up = lootUpgradeBackground;
        lootUpgradeStyle.down = lootUpgradeBackground;
        lootUpgradeBtn.setStyle(drillUpgradeStyle);
        lootUpgradeBtn.setSize((float)(col_width * 1.5), (float)(row_height * 0.75));
        lootUpgradeBtn.setPosition((float)(screenWidth - col_width * 5.4), (float)(screenHeight - row_height * 7.6));
        lootUpgradeBtn.addListener(new InputListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(lootUpgrade >= 7){
                    lootCostLbl.setText("MAX");
                }
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                dialogOpened = true;
                dialogInfo.getButtonTable().clear();
                dialogInfo.getContentTable().clear();
                dialogLbl.setText("Increase looting speed of Agent\n" +
                        "In game mode Find");
                dialogInfo.text(dialogLbl);
                TextButton buttonUpgrade = new TextButton("Upgrade!", skin);
                if(lootUpgrade == 7){
                    buttonUpgrade.setText("MAX");
                }
                buttonUpgrade.setSize(col_width * 1.5f, row_height * 1.5f);
                buttonUpgrade.getLabel().setFontScale(1.5f * ratW, 1.5f * ratH);
                buttonUpgrade.addListener(new InputListener(){
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        if(points >= 120 + 125 * lootUpgrade && lootUpgrade < 7){
                            //Изменение данных
                            points = points - (120 + 125 * lootUpgrade);
                            prefAll.putInteger("points", points);
                            prefAll.flush();
                            lootUpgrade++;
                            prefFind.putInteger("lootSpeed", lootUpgrade);
                            if(lootUpgrade == 7){
                                lootCostLbl.setText("MAX");
                            }
                            prefFind.flush();
                            for (int i = 0; i < lootUpgrade; i++) {
                                SpriteBatch spr = new SpriteBatch();
                                lootRedUpgrades.setItems(spr);

                            }
                            pointsLbl.setText("Your points: " + points);
                            lootCostLbl.setText("Loot Speed\nCost:" + (120 + 125 * lootUpgrade));
                            prefObject.flush();
                        } else if(lootUpgrade >= 7){
                            lootCostLbl.setText("MAX");
                        }
                        return false;
                    }
                });
                dialogInfo.button(buttonUpgrade);

                TextButton buttonCancel = new TextButton("Cancel", skin);
                buttonCancel.setSize(col_width * 1.5f, row_height * 1.5f);
                buttonCancel.getLabel().setFontScale(1.5f * ratW, 1.5f * ratH);
                buttonCancel.addListener(new InputListener(){
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        dialogInfo.hide();
                        dialogOpened = false;
                        return false;
                    }
                });
                dialogInfo.button(buttonCancel);
                dialogInfo.show(stage);
                return false;
            }

        });
        stage.addActor(lootUpgradeBtn);
        //Drill Cost Label
        if(lootUpgrade < 7) {
            lootCostLbl = new Label("Loot Speed\nCost:" + (120 + 125 * lootUpgrade), skinOptional, "black");
        }else {
            lootCostLbl = new Label("MAX", skinOptional, "black");
        }
        lootCostLbl.setFontScale(1.35f * ratW, 1.35f * ratH);
        lootCostLbl.setSize(col_width * 4, row_height * 2);
        lootCostLbl.setPosition((float)(screenWidth - col_width * 3.8), (float)(screenHeight - row_height * 8.25));
        stage.addActor(lootCostLbl);
        //Your points Label
        pointsLbl = new Label("Your points:" + points, skinOptional, "black");
        pointsLbl.setFontScale(2 * ratW, 2 * ratH);
        pointsLbl.setSize(col_width * 2, pointsLbl.getPrefHeight());
        pointsLbl.setPosition(screenWidth / 2 - col_width * 2, screenHeight - row_height * 2, Align.center);
        stage.addActor(pointsLbl);
        //Shop Image
        Drawable shopDr = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("upgradeshopCircled.png"))));
        shopImage = new Image(shopDr);
        shopImage.setSize((float)(col_width * 4) ,(float)( row_height * 2));
        shopImage.setPosition((float)(screenWidth / 2 ), (float)(screenHeight - row_height ), Align.center);
        stage.addActor(shopImage);
        //Home Button
        homeBtn = new ImageButton(skin);
        Drawable homeBtnBackground = homeBtn.getBackground();
        ImageButton.ImageButtonStyle homeBtnStyle = new ImageButton.ImageButtonStyle();
        homeBtnStyle.imageUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("home.png"))));
        homeBtnStyle.imageDown = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("home.png"))));
        homeBtnStyle.up = homeBtnBackground;
        homeBtnStyle.down = homeBtnBackground;
        homeBtn.setStyle(homeBtnStyle);
        homeBtn.setSize((float)(col_width * 1.4), (float)(row_height * 0.7));
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
    //Отрисовка элементов
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1,1,1,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.getBatch().begin();
        stage.act();
        stage.draw();
        //Отрисовка рисок прокачки
             for (int i = 0; i < hpUpgrade; i++) {
                 hpRedUpgrades.getItemAt(i).begin();
                 hpRedUpgrades.getItemAt(i).draw(redDotTxt, (float) (col_width * 0.8 + col_width * (i * 0.7)), (float) (screenHeight - row_height * 4),
                         (float) (col_width * 0.4), (float) (row_height * 0.2));
                 hpRedUpgrades.getItemAt(i).end();
             }
             for (int i = 0; i < bulletUpgrade; i++) {
                 bulletRedUpgrades.getItemAt(i).begin();
                 bulletRedUpgrades.getItemAt(i).draw(redDotTxt, (float) (screenWidth - col_width * 4.8 + col_width * (i * 0.7)), (float) (screenHeight - row_height * 4),
                         (float) (col_width * 0.4), (float) (row_height * 0.2));
                 bulletRedUpgrades.getItemAt(i).end();
             }
             if(!dialogOpened) {
                 for (int i = 0; i < drillUpgrade; i++) {
                     drillRedUpgrades.getItemAt(i).begin();
                     drillRedUpgrades.getItemAt(i).draw(redDotTxt, (float) (col_width * 0.8 + col_width * (i * 0.7)), (float) (screenHeight - row_height * 6.6),
                             (float) (col_width * 0.4), (float) (row_height * 0.2));
                     drillRedUpgrades.getItemAt(i).end();
                 }
                 for (int i = 0; i < lootUpgrade; i++) {
                     lootRedUpgrades.getItemAt(i).begin();
                     lootRedUpgrades.getItemAt(i).draw(redDotTxt, (float) (screenWidth - col_width * 4.8 + col_width * (i * 0.7)), (float) (screenHeight - row_height * 6.6),
                             (float) (col_width * 0.4), (float) (row_height * 0.2));
                     lootRedUpgrades.getItemAt(i).end();
                 }
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
        prefShoot.flush();
        prefAll.flush();
    }
}
