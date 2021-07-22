package pt.ipc.estgoh.projetoFinal;

import pt.ipc.estgoh.projetoFinal.controller.PlayerController;
import pt.ipc.estgoh.projetoFinal.model.InterfaceBlackjack;
import pt.ipc.estgoh.projetoFinal.view.Game;
import pt.ipc.estgoh.projetoFinal.view.Login;

public class Main {

    public static Login loginScreen = new Login();
    public static Game gameScreen = new Game();

    public static void main(String[] args) {
        loginScreen.setVisible(true);
    }

    public static void loginToGame(InterfaceBlackjack serverRemote, PlayerController playerController){
        loginScreen.setVisible(false);
        gameScreen.setServerRemote(serverRemote);
        gameScreen.setPlayerController(playerController);
        gameScreen.setVisible(true);
    }
}
