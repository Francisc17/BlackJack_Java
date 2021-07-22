package pt.ipc.estgoh.projetoFinal.controller;

import pt.ipc.estgoh.projetoFinal.model.InterfaceBlackjack;
import pt.ipc.estgoh.projetoFinal.model.Player;

import java.rmi.registry.LocateRegistry;

public class LoginController {

    private InterfaceBlackjack serverBlackjack;
    private PlayerController playerBlackjack;

    public LoginController() {
    }

    public Boolean registerPlayer(String aIp,int aPort, String aName){

        Boolean answer = null;

        try {
            serverBlackjack = (InterfaceBlackjack) LocateRegistry.getRegistry(aIp,aPort).lookup("theBestServer");

            playerBlackjack = new PlayerController();    //Obtém a referência remota de um novo jogador blackjack

            answer = serverBlackjack.registerPlayer(new Player(aName), playerBlackjack);	//O jogador deve se registar no servidor
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return answer;
    }

    public PlayerController getPlayerBlackjack() {
        return playerBlackjack;
    }

    public InterfaceBlackjack getServerBlackjack() {
        return serverBlackjack;
    }
}
