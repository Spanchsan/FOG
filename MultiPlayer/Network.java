package com.mygdx.game.MultiPlayer;

import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.mygdx.game.Gun.Entity.PBullet;

import java.util.ArrayList;

public class Network {

    static public final int portTCP = 54777;
    static public final int portUDP = 54777;

    static public void register(EndPoint endPoint){
        Kryo kryo = endPoint.getKryo();
        kryo.register(UpdatePlayer.class);
        kryo.register(BulletPositions.class);
        kryo.register(Array.class);
        kryo.register(Array.ArrayIterable.class);
        kryo.register(ArrayList.class);
        kryo.register(Object[].class);
        kryo.register(PBullet.class);
        kryo.register(EnemyDamaged.class);
        kryo.register(ResumeRound.class);
        kryo.register(RoundLost.class);

    }

    static public class UpdatePlayer {
        public float posX, posY;
        public double angle;
        public int hp;
    }

    static public class BulletPositions {
        public ArrayList<PBullet> bullets;
    }


    static public class EnemyDamaged {}

    static public class RoundLost {}

    static public class ResumeRound {}
}
