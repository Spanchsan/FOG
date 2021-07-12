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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.FOG;
import com.mygdx.game.gameUtils.DataLoader;
import com.mygdx.game.gameUtils.OnDataLoaderListener;
import com.mygdx.game.gameUtils.User;



public class LeaderboardScreen implements Screen {

    DataLoader dl;
    final FOG game;
    int screenWidth, screenHeight;

    int row_height;
    int col_width;
    float ratW, ratH;

    Array<User> users, tempArray;
    int pointsG, pointsO, pointsF;
    byte selectedGameMode = 1;
    String name, oldName, key;
    boolean done = false;
    ImageButton leftBtn, rightBtn, homeBtn;
    Label titleLbl;
    Stage stage;
    Skin skin;
    private Preferences pref;

    public LeaderboardScreen(final FOG game){
        this.game = game;
        screenHeight = Gdx.graphics.getHeight();
        screenWidth = Gdx.graphics.getWidth();
        //Перменные предназначение которых масштабирование фото и кнопок
        ratW = (float)screenWidth / 1080;
        ratH = (float)screenHeight / 1920;
        row_height = Gdx.graphics.getHeight() / 12;
        col_width = Gdx.graphics.getWidth() / 12;
        game.getFont().getData().setScale(4 * ratW, 4 * ratH);
        game.getFont().setColor(0,0,0,1);
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("skinHolo/uiskin.json"));
        pref = Gdx.app.getPreferences("My Preferences");
        name = pref.getString("playerName", "def0");
        oldName = pref.getString("oldPlayerName", "def3");
        key = pref.getString("playerKey", "def1");
        users = new Array<>();
        dl = DataLoader.getInstance();
        OnDataLoaderListener listener = new OnDataLoaderListener() {
            @Override
            public void getData(Array<User> players) {
                boolean check = false;
                if (!(players == null)) {
                    for (int i = 0; i < players.size; i++) {
                        if (players.get(i).getName().equals(name) || players.get(i).getName().equals(oldName)) {
                            check = true;
                            break;
                        }
                    }
                    if (!check && !name.equals("def0")) {
                        User user = new User();
                        pointsG = pref.getInteger("enemyDestroyed");
                        pointsO = pref.getInteger("crystalDestroyed");
                        pointsF = pref.getInteger("fogsLooted");
                        user.setPointsG(pointsG);
                        user.setPointsO(pointsO);
                        user.setPointsF(pointsF);
                        user.setName(name);
                        players.add(user);
                        users = players;
                    } else if (name.equals("def0") && key.equals("def1")) {
                        Gdx.input.getTextInput(new Input.TextInputListener() {
                            @Override
                            public void input(String text) {
                                name = text;
                                pref.putString("playerName", name);
                                pref.flush();
                            }

                            @Override
                            public void canceled() {
                                game.setScreen(new StartScreen(game));
                                dispose();
                            }
                        }, "Set Your Name", "", "Your Name");
                    }
                    users = players;
                } else {
                    if (name.equals("def0") && key.equals("def1")) {
                        Gdx.input.getTextInput(new Input.TextInputListener() {
                            @Override
                            public void input(String text) {
                                name = text;
                                pref.putString("playerName", name);
                                pref.flush();
                            }

                            @Override
                            public void canceled() {
                                game.setScreen(new LeaderboardScreen(game));
                                dispose();
                            }
                        }, "Set Your Name", "", "Your Name");
                    }
                    User user = new User();
                    pointsG = pref.getInteger("enemyDestroyed");
                    pointsO = pref.getInteger("crystalDestroyed");
                    pointsF = pref.getInteger("fogsLooted");
                    user.setPointsG(pointsG);
                    user.setPointsO(pointsO);
                    user.setPointsF(pointsF);
                    user.setName(name);
                    users.add(user);

                }
                //Sort players by top points
                sortArray();
                done = true;
            }
        };
        dl.getPlayers(listener);
        //Title Label
        titleLbl = new Label("Gun Points", skin);
        titleLbl.setPosition(col_width * 5, screenHeight - row_height, Align.center);
        titleLbl.setSize(col_width * 2, row_height);
        titleLbl.setFontScale(4 * ratW, 4 * ratH);
        stage.addActor(titleLbl);
        //rightImageBtn
        rightBtn = new ImageButton(skin, "colored");
        rightBtn.setChecked(false);
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
        rightBtn.setPosition((float)(screenWidth - col_width * 3.4), (float)(row_height ));
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
                changeText();
                sortArray();
                return false;
            }
        });
        stage.addActor(rightBtn);
        //leftImageBtn
        leftBtn = new ImageButton(skin, "colored");
        leftBtn.setChecked(false);
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
        leftBtn.setPosition((float)(screenWidth - col_width * 10), (float)( row_height ));
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
                changeText();
                sortArray();
                return false;
            }
        });
        stage.addActor(leftBtn);
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
        //checkForNew();
    }

    private void changeText(){
        if(selectedGameMode == 1){
            titleLbl.setText("Gun Points");
        } else if(selectedGameMode == 2){
            titleLbl.setText("Object Points");
        } else {
            titleLbl.setText("Find Points");
        }
    }

    private void sortArray(){
        tempArray = users;
        if(selectedGameMode == 1){
            for(int i = 0; i < tempArray.size; i++){
                for(int j = 0; j < tempArray.size - i - 1 ; j++){
                    if(tempArray.get(j).getPointsG() < tempArray.get(j + 1).getPointsG()){
                        tempArray.swap(j, j + 1);
                    }
                }
            }
        } else if(selectedGameMode == 2){
            for(int i = 0; i < tempArray.size; i++){
                for(int j = 0; j < tempArray.size - i - 1 ; j++){
                    if(tempArray.get(j).getPointsO() < tempArray.get(j + 1).getPointsO()){
                        tempArray.swap(j, j + 1);
                    }
                }
            }
        } else {
            for(int i = 0; i < tempArray.size; i++){
                for(int j = 0; j < tempArray.size - i - 1 ; j++){
                    if(tempArray.get(j).getPointsF() < tempArray.get(j + 1).getPointsF()){
                        tempArray.swap(j, j + 1);
                    }
                }
            }
        }
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
           if(done){
               for(int i = 0; i <= tempArray.size - 1; i++){
                   User temp = tempArray.get(i);
                   if(selectedGameMode == 1)
                   game.getFont().draw(game.getBatch(),  temp.getName() + ":" + temp.getPointsG(), col_width * 2f,
                           screenHeight - (row_height * 1f + row_height * 0.8f * i));
                   else if(selectedGameMode == 2)
                       game.getFont().draw(game.getBatch(),  temp.getName() + ":" + temp.getPointsO(), col_width * 2f,
                               screenHeight - (row_height * 1f + row_height * 0.8f * i));
                   else
                       game.getFont().draw(game.getBatch(),  temp.getName() + ":" + temp.getPointsF(), col_width * 2f,
                               screenHeight - (row_height * 1f + row_height * 0.8f * i));
               }
           } else {
               game.getFont().draw(game.getBatch(), "Loading", col_width * 5, row_height * 6);
           }
        game.getBatch().end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
        dl.preparePlayers(users);
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dl.preparePlayers(users);
    }

    @Override
    public void dispose() {
        dl.preparePlayers(users);
    }
}
