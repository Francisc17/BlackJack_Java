package pt.ipc.estgoh.projetoFinal.controller;

import pt.ipc.estgoh.projetoFinal.Main;
import pt.ipc.estgoh.projetoFinal.model.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerController extends UnicastRemoteObject implements InterfacePlayer {

    public String ownName;
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm");


    public PlayerController() throws RemoteException {
        super();
    }

    @Override
    public void getCards(Player aPlayer, int state) throws RemoteException {
        if (aPlayer != null)
            Main.gameScreen.updatePlayerInfoAfterHit(aPlayer, state);
        
    }

    @Override
    public void changeBtnsState(boolean aState){
        Main.gameScreen.changeStateBtn(aState);
    }

    @Override
    public void changeExitBtn(boolean aState) {
        Main.gameScreen.changeExitState(aState);
    }

    @Override
    public void observerToPlay(Player player) throws RemoteException{
        Main.gameScreen.updatePlayerInfoAfterExit(player);
    }

    @Override
    public boolean checkIfPlayerToPlay() throws RemoteException {

        return Main.gameScreen.checkButtonState();

    }

    @Override
    public void reOrderTablePositions(HashMap<String,Player> aPlayers) throws RemoteException{
        Main.gameScreen.cleanTableAfterExit(aPlayers);
    }

    @Override
    public void sendMessage(Log aMessage) throws RemoteException {
        Main.gameScreen.appendLog("Hora: " + format.format(aMessage.getTime()) + " - " + aMessage.getMesage() + "\n");
    }

    @Override
    public void getWinner(Player aPlayer) throws RemoteException {
        if (aPlayer != null)
            Main.gameScreen.updateWinners(aPlayer);
    }

    @Override
    public void clearRoundData(){
        Main.gameScreen.resetHand(false);
        Main.gameScreen.resetPoints();
    }

    @Override
    public void shutdownPlayer() throws RemoteException{
        System.exit(0);
    }

    @Override
    public void acceptLogin(HashMap<String, Player> playersReceived, String aPlayerName, CopyOnWriteArrayList<Card> aCardPlayer) throws RemoteException {

        if (this.ownName == null)
            this.ownName = aPlayerName;

        Player thisPlayer = playersReceived.get(ownName);

        if (!thisPlayer.getIsObservable()) {
            //We can put the player without cards in the table.
            Main.gameScreen.changePlayerType("Player");
            Main.gameScreen.verifyPlayersOnTable(playersReceived);
            Main.gameScreen.treatCardsDealer(aCardPlayer, true);
        } else {
            Main.gameScreen.changePlayerType("Observer");
            Main.gameScreen.insertObservers(playersReceived);
            Main.gameScreen.treatCardsDealer(aCardPlayer, true);
        }
    }


    @Override
    public void showObserversToPlayers(HashMap<String, Player> aObservers) throws RemoteException {
        Main.gameScreen.insertObservers(aObservers);
    }

    @Override
    public void showPlayersToObservers(HashMap<String, Player> aPlayers) throws RemoteException {
        Main.gameScreen.verifyPlayersOnTable(aPlayers);
    }

    @Override
    public void getCardsOnBeginGame(HashMap<String, Player> playersReceived, CopyOnWriteArrayList<Card> aCardDealer) throws RemoteException {
        //aqui o jogador recebe as cartas
        Main.gameScreen.verifyNewRound(playersReceived);


        //aqui o cliente pede as cartas do dealer
        Main.gameScreen.treatCardsDealer(aCardDealer, true);
    }

    @Override
    public void indicationPlay(int aPositionTable) throws RemoteException {
      
        Main.gameScreen.isYourTimeToPlay(aPositionTable);
        Main.gameScreen.changeStateBtn(true);
    }

    @Override
    public void sendInfoDealer(int aPoints, CopyOnWriteArrayList<Card> aCardsDealer, int aState) throws RemoteException {
        Main.gameScreen.updateDealerEndRound(aCardsDealer, aPoints, aState);
    }


}
