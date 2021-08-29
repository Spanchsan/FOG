package com.mygdx.game.utils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.mygdx.game.MultiPlayer.Network;

import java.net.InetAddress;
import java.util.List;

public class PortScanner {
    public static List<InetAddress> scanPortForServers(){
        Client client = new Client();
        return client.discoverHosts(Network.portUDP, 5000);
    }

    public static InetAddress scanPortForSingleServer(){
        Client client = new Client();
        return client.discoverHost(Network.portUDP, 2000);
    }
}
