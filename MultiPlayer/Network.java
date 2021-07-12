package com.mygdx.game.MultiPlayer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.mygdx.game.Gun.Entity.PBullet;

public class Network {

    static public final int port = 54777;

    static public void register(EndPoint endPoint){
        Kryo kryo = endPoint.getKryo();
        kryo.register(Network.ChangePlayerPosition.class);
        kryo.register(PBullet.class);
        kryo.register(UpdatePlayer.class);
    }

    static public class ChangePlayerPosition {
        public float posX, posY;
    }

    static public class BulletPositions {
        public PBullet bullets;
    }

    static public class UpdatePlayer{
        public int id, playerX, playerY;
    }
}
