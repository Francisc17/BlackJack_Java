package pt.ipc.estgoh.projetoFinal.model;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public interface InterfacePlayer extends Remote {

	public void getCards(Player aPlayer, int aState) throws RemoteException;
	public void sendMessage(Log aMessage) throws RemoteException;
	public void getWinner(Player aPlayer) throws RemoteException;
	public void shutdownPlayer() throws RemoteException;
	public void acceptLogin(HashMap<String,Player> aPlayers, String aPlayerName, CopyOnWriteArrayList<Card> aCardDealer) throws RemoteException;
	public void showPlayersToObservers(HashMap<String, Player> aPlayers) throws RemoteException;
	public void getCardsOnBeginGame(HashMap<String,Player> aPlayers, CopyOnWriteArrayList<Card> aCardDealer) throws RemoteException;
	public void indicationPlay(int aPositionInTable) throws RemoteException;
	public void sendInfoDealer(int aPoints, CopyOnWriteArrayList<Card> aCardsDealer, int aState) throws RemoteException;
	public void clearRoundData() throws RemoteException;
	public void changeBtnsState(boolean state) throws RemoteException;
	public void showObserversToPlayers(HashMap<String,Player> aObservers) throws RemoteException;
	public void reOrderTablePositions(HashMap<String,Player> aPlayers) throws RemoteException;
	public void changeExitBtn(boolean aState) throws RemoteException;
	public void observerToPlay(Player player) throws RemoteException;
	public boolean checkIfPlayerToPlay ()  throws RemoteException;
}