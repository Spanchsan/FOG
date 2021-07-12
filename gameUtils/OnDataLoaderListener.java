package com.mygdx.game.gameUtils;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Null;

public interface OnDataLoaderListener {
   @Null
   void getData(Array<User> players);
}
