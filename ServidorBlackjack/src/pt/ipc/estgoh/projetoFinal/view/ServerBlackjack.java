package pt.ipc.estgoh.projetoFinal.view;

import pt.ipc.estgoh.projetoFinal.controller.ServerController;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ServerBlackjack {

	private static Scanner scanner = new Scanner(System.in);


	public static void main(String[] args) {

		try {
			Registry reg = LocateRegistry.createRegistry(lerInteiro("Porto do servidor: "));
			ServerController serverBlackjack = new ServerController();

			reg.rebind("theBestServer", serverBlackjack);

			System.out.println("!! Server started with successfully  !!");

		} catch (RemoteException re) {
			System.out.println("Exception: "+re.getMessage());
		}

	}

	public static int lerInteiro(String aMsg) {
		do {
			System.out.println(aMsg);
			try {
				int i = scanner.nextInt();
				scanner.nextLine();
				return i;
			} catch (InputMismatchException error) {
				scanner.nextLine();
				System.out.println("Introduzir um valor válido!.");
			}
		} while (true);
	}
}