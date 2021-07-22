package pt.ipc.estgoh.projetoFinal.model;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.CopyOnWriteArrayList;

public interface InterfaceBlackjack extends Remote {

	public boolean registerPlayer(Player aPlayer, InterfacePlayer aRefPlayer) throws RemoteException;
	public void sendCommandHit(String aNamePlayer) throws RemoteException;
	public void sendCommandStand(String aNamePlayer) throws RemoteException;
	public void verifyWinner() throws RemoteException;
	public void over21OrBlackJack(String aPlayerName) throws RemoteException;
	public void sendCommandExit(String aNamePlayer) throws RemoteException;
}