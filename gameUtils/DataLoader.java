package com.mygdx.game.gameUtils;


import com.badlogic.gdx.utils.Array;
//The idea of the class was seen from the video https://www.youtube.com/watch?v=nJ1RB1hfd_M
public class DataLoader {
    private Array<User> players;
    private boolean isLoaded = false;

    public static DataLoader dl = new DataLoader();

    public static DataLoader getInstance(){
        return dl;
    }

    public void requestData(Array<User> players){

    }

    public void preparePlayers(Array<User> pl){
        this.players = pl;
        isLoaded = true;
    }

    public void reset(){
        isLoaded = false;
    }

    public boolean update(){
        return isLoaded;
    }

    public void getPlayers(OnDataLoaderListener listener){
        listener.getData(players);
    }

}
