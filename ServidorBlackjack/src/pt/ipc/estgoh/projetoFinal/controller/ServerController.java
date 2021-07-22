package pt.ipc.estgoh.projetoFinal.controller;

import pt.ipc.estgoh.projetoFinal.model.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.*;

public class ServerController extends UnicastRemoteObject implements InterfaceBlackjack {

    private ControllerCards cards;
    private ConcurrentHashMap<String, RemotePlayer> players;
    private ConcurrentHashMap<String, RemotePlayer> observers;
    private Dealer dealer = new Dealer();
    private int positionInGame;
    private boolean gameisRunning = false;
    private Thread waitingForPlayerToPlay;

    public ServerController() throws RemoteException {
        super();
        this.players = new ConcurrentHashMap<>();
        this.observers = new ConcurrentHashMap<>();
        this.cards = new ControllerCards();
        this.cards.registerCards();
        this.positionInGame = 0;
    }

    @Override
    public synchronized boolean registerPlayer(Player aPlayer, InterfacePlayer aRefPlayer) throws RemoteException {
        int numPlayers = this.players.size();

        //Verify if yet reach the limit of players present in the table of game
        if (numPlayers < 3) {
            if (!verifyNamePlayer(aPlayer.getName()) && !verifyNameObservers(aPlayer.getName())) {

                aPlayer.setPosition(this.positionInGame);    //Assign a position of the table of game to the player

                this.players.put(aPlayer.getName(), new RemotePlayer(aRefPlayer, aPlayer));

                this.positionInGame++;

                HashMap<String, Player> playersToPass = new HashMap<>();

                for (RemotePlayer remotePlayer : players.values())
                    playersToPass.put(remotePlayer.getDataRemotePlayer().getName(), remotePlayer.getDataRemotePlayer());

                for (RemotePlayer remotePlayer : players.values()) {
                    try {
                        remotePlayer.getRemotePlayer().acceptLogin(playersToPass, aPlayer.getName(), dealer.getCards());
                    } catch (RemoteException re) {
                        eliminatePlayer(remotePlayer.getDataRemotePlayer().getName());
                    }
                }

                sendMessage("O jogador " + aPlayer.getName() + " entrou no jogo.");

                if (players.size() == 1)
                    waitingToPlay(10000);

                return true;
            }

            return false;

        } else {
            if (!verifyNamePlayer(aPlayer.getName()) && !verifyNameObservers(aPlayer.getName())) {
                aPlayer.setIsObservable(true);  //The user leave of be player and turn be observer

                this.observers.put(aPlayer.getName(), new RemotePlayer(aRefPlayer, aPlayer));

                HashMap<String, Player> observersToPass = new HashMap<>();
                HashMap<String, Player> playersToPass = new HashMap<>();

                for (RemotePlayer remoteObserver : observers.values())
                    observersToPass.put(remoteObserver.getDataRemotePlayer().getName(), remoteObserver.getDataRemotePlayer());

                for (RemotePlayer player : players.values())
                    playersToPass.put(player.getDataRemotePlayer().getName(), player.getDataRemotePlayer());

                for (RemotePlayer remoteObserver : observers.values()) {
                    try {
                        remoteObserver.getRemotePlayer().acceptLogin(observersToPass, aPlayer.getName(), dealer.getCards());
                        remoteObserver.getRemotePlayer().showPlayersToObservers(playersToPass);
                    } catch (RemoteException re) {
                        eliminateObservers(remoteObserver.getDataRemotePlayer().getName());
                    }
                }

                for (RemotePlayer remotePlayer : players.values()) {
                    try {
                        remotePlayer.getRemotePlayer().showObserversToPlayers(observersToPass);
                    } catch (RemoteException re) {
                        eliminatePlayer(remotePlayer.getDataRemotePlayer().getName());
                    }
                }

                sendMessage("O observador " + aPlayer.getName() + " encontra-se em espera.");

                return true;
            }
        }
        return false;
    }

    public void sendDataToObserversNewRound(HashMap<String, Player> aPlayers, CopyOnWriteArrayList<Card> aCardsDealer) {
        if (observers.size() > 0) {

            HashMap<String, Player> newPlayers = new HashMap<>();

            for (RemotePlayer remotePlayer : players.values())
                newPlayers.put(remotePlayer.getDataRemotePlayer().getName(), remotePlayer.getDataRemotePlayer());


            for (RemotePlayer remotePlayerEntry : observers.values()) {
                try {
                    remotePlayerEntry.getRemotePlayer().getCardsOnBeginGame(aPlayers, aCardsDealer);
                    remotePlayerEntry.getRemotePlayer().showPlayersToObservers(aPlayers);
                } catch (RemoteException re) {
                    eliminateObservers(remotePlayerEntry.getDataRemotePlayer().getName());
                }
            }
        }
    }

    public void sendDataToObservers() {
        if (observers.size() > 0) {
            HashMap<String, Player> newPlayers = new HashMap<>();

            for (RemotePlayer remotePlayer : players.values()) {
                newPlayers.put(remotePlayer.getDataRemotePlayer().getName(), remotePlayer.getDataRemotePlayer());
            }

            for (RemotePlayer remoteObserver : observers.values()) {
                try {
                    remoteObserver.getRemotePlayer().showPlayersToObservers(newPlayers);
                } catch (RemoteException e) {
                   eliminateObservers(remoteObserver.getDataRemotePlayer().getName());
                }
            }
        }
    }

    public boolean verifyNameObservers(String aNameObserver) {

        if (this.observers.size() > 0) {
            if (players.get(aNameObserver) != null)
                return true;
        }

        return false;
    }

    public boolean verifyNamePlayer(String aNamePlayer) {

        if (this.players.size() > 0) {
            if (players.get(aNamePlayer) != null)
                return true;
        }

        return false;
    }

    public synchronized void sendMessage(String aMessage) {

        if (this.players != null && this.players.size() > 0) {
            for (Map.Entry<String, RemotePlayer> entryPlayer : this.players.entrySet()) {
                try {
                    this.players.get(entryPlayer.getKey()).getRemotePlayer().sendMessage(new Log(aMessage, new Date().getTime()));
                } catch (RemoteException re) {
                    eliminatePlayer(entryPlayer.getKey());
                }
            }
        }

        if (this.observers != null && this.observers.size() > 0) {
            for (Map.Entry<String, RemotePlayer> entryObserver : this.observers.entrySet()) {
                try {
                    this.observers.get(entryObserver.getKey()).getRemotePlayer().sendMessage(new Log(aMessage, new Date().getTime()));

                } catch (RemoteException re) {
                    eliminateObservers(entryObserver.getKey());
                }
            }
        }
    }

    public void eliminatePlayer(String aNamePlayer) {
        int position = 0;

        if (this.players != null && this.players.size() > 0) {

            for (RemotePlayer playerRemote : this.players.values()) {
                if (playerRemote.getDataRemotePlayer().getName().equalsIgnoreCase(aNamePlayer))
                    position = playerRemote.getDataRemotePlayer().getPosition();
            }


            RemotePlayer player = players.get(aNamePlayer);
            
            if (player.getDataRemotePlayer().getChipsPlayer() != 0) {
                this.players.remove(aNamePlayer);
            }

            try {
                exchangePlayerObserver(position);
            } catch (RemoteException re) {
                //
            }

        }
    }

    public void eliminateObservers(String aNamePlayer) {
        if (this.observers != null && this.observers.size() > 0) {
            this.observers.remove(aNamePlayer);
        }
    }

    public void exchangePlayerObserver(int aPosition) throws RemoteException {

        boolean imPlaying = true;
        RemotePlayer playerToRemove = null;

        for (RemotePlayer remotePlayer : players.values()) {

            int chipsGain = remotePlayer.getDataRemotePlayer().getChipsPlayer();

            if (chipsGain == 0) {
                playerToRemove = remotePlayer;
                remotePlayer.getRemotePlayer().shutdownPlayer();
            }

            if (playerToRemove != null)
                players.remove(playerToRemove.getDataRemotePlayer().getName());

            if (remotePlayer.getRemotePlayer().checkIfPlayerToPlay() || chipsGain == 0)
                imPlaying = false;
        }

        if (this.observers.size() > 0) {

            //this give us the first remote player add to hashmap
            RemotePlayer auxOserver = this.observers.values().iterator().next();

            //remove the object on observers hashmap
            this.observers.remove(auxOserver.getDataRemotePlayer().getName());

            auxOserver.getDataRemotePlayer().setIsObservable(false);    //The observer turn be player
            auxOserver.getDataRemotePlayer().setPosition(aPosition);    //Stands on position of the last player that get out of game

            this.players.put(auxOserver.getDataRemotePlayer().getName(), auxOserver);

            HashMap<String, Player> newPlayers = new HashMap<>();

            for (RemotePlayer player : this.players.values()) {
                newPlayers.put(player.getDataRemotePlayer().getName(), player.getDataRemotePlayer());
            }

            HashMap<String, Player> newObservers = new HashMap<>();

            for (RemotePlayer observer : this.observers.values())
                newObservers.put(observer.getDataRemotePlayer().getName(), observer.getDataRemotePlayer());

            for (RemotePlayer player : this.players.values()) {
                player.getRemotePlayer().reOrderTablePositions(newPlayers);
                player.getRemotePlayer().showObserversToPlayers(newObservers);
            }

            if (imPlaying) {
                for (RemotePlayer player : this.players.values()) {
                    player.getRemotePlayer().indicationPlay(aPosition - 1);
                }
            }

        } else {
            this.updatePlayersPosition();

            HashMap<String, Player> newPlayers = new HashMap<>();

            for (RemotePlayer player : this.players.values()) {
                newPlayers.put(player.getDataRemotePlayer().getName(), player.getDataRemotePlayer());
            }

            for (RemotePlayer player : this.players.values()) {
                player.getRemotePlayer().reOrderTablePositions(newPlayers);
            }

            if (players.size() == 0)
                System.exit(0);

            if (imPlaying) {
                for (RemotePlayer player : this.players.values()) {
                    player.getRemotePlayer().indicationPlay(aPosition - 1);
                }
            }
        }
    }

    public void updatePlayersPosition() {
        int x = 0;

        for (RemotePlayer remotePlayer : players.values()) {
            remotePlayer.getDataRemotePlayer().setPosition(x);
            x++;
        }
    }

    public void sendInfoToObserversHit(int aVerification, Player aNextPlayer, int aNextPosition) {

        for (Map.Entry<String, RemotePlayer> rp : observers.entrySet()) {
            if (aVerification != 0) {
                try {
                    rp.getValue().getRemotePlayer().getCards(aNextPlayer, aVerification);  //Send updated data to the player
                    rp.getValue().getRemotePlayer().indicationPlay(aNextPosition);
                } catch (RemoteException re) {
                    eliminateObservers(rp.getKey());
                }
            } else {
                try {
                    rp.getValue().getRemotePlayer().getCards(aNextPlayer, 0);  //Send updated data to the player
                } catch (RemoteException re) {
                    eliminatePlayer(rp.getValue().getDataRemotePlayer().getName());
                }
            }
        }
    }

    @Override
    public synchronized void sendCommandHit(String aNamePlayer) throws RemoteException {
        Player auxPlayer = null;
        if (this.players != null && this.players.size() > 0) {

            RemotePlayer remoteAuxPlayer = this.players.get(aNamePlayer);

            if (remoteAuxPlayer != null) {

                remoteAuxPlayer.getDataRemotePlayer().setCards(cards.getCard());  //Add a new card to the set of cards that player own
                auxPlayer = verifyValuesCards(remoteAuxPlayer.getDataRemotePlayer());

                int points = auxPlayer.getGain();
                int choose = 0;

                if (points > 21) {
                    choose = 1;
                } else if (points == 21) {
                    choose = -1;
                }

                RemotePlayer nexPlayerAux = null;
                int nextPlayerPosition = verifyNextPlayer(auxPlayer);
                for (Map.Entry<String, RemotePlayer> playersRemote : players.entrySet()) {

                    if (choose != 0) {
                        if (playersRemote.getValue().getDataRemotePlayer().getPosition() == nextPlayerPosition)
                            nexPlayerAux = playersRemote.getValue();

                        try {
                            playersRemote.getValue().getRemotePlayer().getCards(auxPlayer, choose);  //Send updated data to the player
                            playersRemote.getValue().getRemotePlayer().indicationPlay(nextPlayerPosition);
                        } catch (RemoteException re) {
                            eliminatePlayer(playersRemote.getKey());
                        }
                    } else {
                        try {
                            playersRemote.getValue().getRemotePlayer().getCards(auxPlayer, 0);  //Send updated data to the player
                        } catch (RemoteException re) {
                            eliminatePlayer(playersRemote.getValue().getDataRemotePlayer().getName());
                        }
                    }
                }

                sendInfoToObserversHit(choose, auxPlayer, nextPlayerPosition); //This method go update the information about the game, in the observers

                for (RemotePlayer remotePlayer : players.values()) {
                    try {
                        remotePlayer.getRemotePlayer().changeBtnsState(false);
                    } catch (RemoteException re) {
                        eliminatePlayer(remotePlayer.getDataRemotePlayer().getName());
                    }
                }

                //Disable buttons of observers
                for (RemotePlayer rp : observers.values()) {
                    try {
                        rp.getRemotePlayer().changeBtnsState(false);
                    } catch (RemoteException re) {
                        eliminateObservers(rp.getDataRemotePlayer().getName());
                    }
                }

                waitingForPlayerToPlay.interrupt();

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }

                try {
                    if (choose == 0) {
                        waiting20SecToPlay(remoteAuxPlayer);
                        remoteAuxPlayer.getRemotePlayer().changeBtnsState(true);
                        sendDataToObservers();
                    } else if (auxPlayer.getPosition() != 0) {

                        waiting20SecToPlay(nexPlayerAux);
                        nexPlayerAux.getRemotePlayer().changeBtnsState(true);
                        sendDataToObservers();
                    } else {
                        sendRoundInfoDealer();
                        verifyWinner();
                    }
                } catch (RemoteException re) {
                    eliminatePlayer(nexPlayerAux.getDataRemotePlayer().getName());
                }

                overTwentyOne(auxPlayer);
            }
        }
    }

    public synchronized int verifyNextPlayer(Player aPLayer) throws RemoteException {

        int position = aPLayer.getPosition(); //Get a position that the player if find in the table of game

        //Verify if is the last player in the table to can play in the order of blackjack
        if (position == 0) {
            return this.players.size() - 1;
        } else
            return position - 1;    //Get the position of the next player
    }

    @Override
    public synchronized void sendCommandStand(String aNamePlayer) throws RemoteException {
        waitingForPlayerToPlay.interrupt();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {

        }

        if (this.players != null && this.players.size() > 0) {

            RemotePlayer auxPlayer = this.players.get(aNamePlayer);

            if (auxPlayer != null) {

                int position = auxPlayer.getDataRemotePlayer().getPosition(); //Get a position that the player if find in the table of game
                RemotePlayer nextPlayerRemote = null;
                //Verify if is the last player in the table to can play in the order of blackjack
                if (position == 0) {
                    sendRoundInfoDealer();
                    verifyWinner();
                } else {
                    int i = position - 1;    //Get the position of the next player

                    //Iterate HashMap to find position of the next player
                    for (Map.Entry<String, RemotePlayer> entry : this.players.entrySet()) {


                        if (entry.getValue().getDataRemotePlayer().getPosition() == i) {
                            nextPlayerRemote = entry.getValue();
                            waiting20SecToPlay(entry.getValue());
                        }

                        try {

                            //Turn to the next player
                            entry.getValue().getRemotePlayer().indicationPlay(i);

                        } catch (RemoteException re) {
                            eliminatePlayer(entry.getValue().getDataRemotePlayer().getName());

                        }
                    }

                    for (RemotePlayer rp : this.observers.values()) {

                        try {
                            //Show to observers the next player to play
                            rp.getRemotePlayer().indicationPlay(i);
                        } catch (RemoteException re) {
                            eliminateObservers(rp.getDataRemotePlayer().getName());
                        }
                    }

                }
                for (RemotePlayer remotePlayer : players.values()) {
                    try {
                        remotePlayer.getRemotePlayer().changeBtnsState(false);
                    } catch (RemoteException re) {
                        eliminatePlayer(remotePlayer.getDataRemotePlayer().getName());
                    }
                }

                //Disable buttons of observers
                for (RemotePlayer rp : observers.values()) {
                    try {
                        rp.getRemotePlayer().changeBtnsState(false);
                    } catch (RemoteException re) {
                        eliminateObservers(rp.getDataRemotePlayer().getName());
                    }
                }

                if (nextPlayerRemote != null) {
                    try {
                        nextPlayerRemote.getRemotePlayer().changeBtnsState(true);
                    } catch (RemoteException re) {
                        eliminatePlayer(nextPlayerRemote.getDataRemotePlayer().getName());
                    }
                }
            }
        }
    }

    public Player changeValuesAs(Player aPlayer) {

        int newValue = 0;

        for (Card card : aPlayer.getCards())
            newValue += card.getValue();

        for (Card card : aPlayer.getCards()) {
            if (newValue > 21 && (card.getName().equals("c1") || card.getName().equals("h1")
                    || card.getName().equals("d1") || card.getName().equals("s1") && card.getValue() == 11)) {
                newValue -= 10;
            }
        }

        aPlayer.setGain(newValue);

        return aPlayer;
    }

    public void sendRoundInfoDealer() throws RemoteException {

        int newValue = 0;
        int state = 0;

        for (Card card : dealer.getCards())
            newValue += card.getValue();

        while (newValue < 17) {


            atributeCardsDealer(1);


            //reset value and re-do new counting
            newValue = 0;
            for (Card card : dealer.getCards())
                newValue += card.getValue();


            for (Card card : dealer.getCards()) {
                if (newValue > 21 && (card.getName().equals("c1") || card.getName().equals("h1") || card.getName().equals("d1")
                        || card.getName().equals("s1"))) {

                    newValue -= 10;

                } else if (newValue == 21) {
                    state = -1;
                }
            }
        }

        if (newValue > 21)
            state = 1;


        dealer.setPoints(newValue);

        for (Map.Entry<String, RemotePlayer> entryPlayers : this.players.entrySet()) {
            try {
                entryPlayers.getValue().getRemotePlayer().sendInfoDealer(dealer.getPoints(), dealer.getCards(), state);
            } catch (RemoteException re) {
                eliminatePlayer(entryPlayers.getValue().getDataRemotePlayer().getName());
            }
        }

        for (Map.Entry<String, RemotePlayer> rp : this.observers.entrySet()) {
            try {
                rp.getValue().getRemotePlayer().sendInfoDealer(dealer.getPoints(), dealer.getCards(), state);
            } catch (RemoteException re) {
                eliminateObservers(rp.getValue().getDataRemotePlayer().getName());
            }
        }
    }

    public void verifyWinner() throws RemoteException {

        RemotePlayer auxPlayer = null;
        boolean noWinners = true;

        if (players != null && players.size() > 0) {
            for (Map.Entry<String, RemotePlayer> entry : players.entrySet()) {

                if ((entry.getValue().getDataRemotePlayer().getGain() == 21) ||
                        ((entry.getValue().getDataRemotePlayer().getGain() > dealer.getPoints()) && (entry.getValue().getDataRemotePlayer().getGain() < 21)) ||
                        ((dealer.getPoints() > 21) && (entry.getValue().getDataRemotePlayer().getGain() < 21))) {

                    auxPlayer = players.get(entry.getKey());
                    auxPlayer.getDataRemotePlayer().setChipsPlayer(auxPlayer.getDataRemotePlayer().getChipsPlayer() + 4); //In case of victory is add more 4 chips

                    sendMessage("O jogador " + entry.getKey() + " é vencedor.");

                    noWinners = false;
                }

                try {
                    if (auxPlayer != null)
                        entry.getValue().getRemotePlayer().getWinner(auxPlayer.getDataRemotePlayer());
                    else
                        entry.getValue().getRemotePlayer().getWinner(null);
                } catch (RemoteException re) {
                    eliminatePlayer(auxPlayer.getDataRemotePlayer().getName());
                }
            }

            if (noWinners) {
                sendMessage("Não houve vencedores!");
            }
        }

        newRoundBegin();
    }

    public void newRoundBegin() throws RemoteException {

        new Thread(new Runnable() {
            @Override
            public void run() {

                for (Map.Entry<String, RemotePlayer> entryPlayer : players.entrySet()) {


                    Player player = players.get(entryPlayer.getKey()).getDataRemotePlayer();

                    if (player == null)
                        return;

                    player.resetCards();
                    player.setGain(0);
                }

                dealer.resetCards();
                dealer.setPoints(0);
                cards = new ControllerCards();

                try {
                    Thread.sleep(1000);
                    sendMessage("final da rodada!");
                    Thread.sleep(5000);
                    sendMessage("Próxima rodada começa em 10 segundos");

                    for (Map.Entry<String, RemotePlayer> entryPlayer : players.entrySet()) {
                        InterfacePlayer player = players.get(entryPlayer.getKey()).getRemotePlayer();
                        player.clearRoundData();
                        player.changeExitBtn(true);
                    }

                    for (Map.Entry<String, RemotePlayer> rp : observers.entrySet()) {
                        rp.getValue().getRemotePlayer().clearRoundData();
                        rp.getValue().getRemotePlayer().changeExitBtn(false);
                    }


                    Thread.sleep(10000);
                    for (RemotePlayer remotePlayer : players.values())
                        remotePlayer.getRemotePlayer().changeExitBtn(false);
                    sendMessage("Nova rodada começou");


                    atributeCardsDealer(2);
                    sendInitialCards();
                } catch (InterruptedException | RemoteException e) {

                }
            }

        }).start();
    }

    public void waiting20SecToPlay(RemotePlayer aPLayer) throws RemoteException {

        waitingForPlayerToPlay = new Thread(new Runnable() {
            boolean go = true;

            @Override
            public void run() {

                while (go) {
                    try {
                        sendMessage("Tem 20 segundos para jogar");
                        Thread.sleep(20000);
                        go = false;

                        sendCommandStand(aPLayer.getDataRemotePlayer().getName());

                    } catch (RemoteException | InterruptedException e) {
                        go = false;
                        break;
                    }
                }
            }
        });

        waitingForPlayerToPlay.start();
    }

    public void betNewChips(Player aPlayer) throws RemoteException {

        //Verify if the player still has chips to bet
        if (aPlayer.getChipsPlayer() != 0) {
            aPlayer.setChipsPlayer(aPlayer.getChipsPlayer() - 2);  //Each player bet new two chips in next round

        } else {
            sendMessage("O jogador " + aPlayer.getName() + " não tem fichas suficientes.");

            //If the player hasn't chips sufficiently is obtained the position in that him it is in the table of game
            this.players.remove(aPlayer.getName());

            eliminatePlayer(aPlayer.getName());
        }

    }

    public void updatePointsDealer() {
        int totalPoints = 0;

        if (dealer.getCards() != null && dealer.getCards().size() > 0) {

            Iterator<Card> cardDealer = dealer.getCards().iterator();

            while (cardDealer.hasNext()) {
                totalPoints += cardDealer.next().getValue();
            }

            dealer.setPoints(totalPoints);
        }
    }

    public void atributeCardsDealer(int aNumCards) {

        if (aNumCards == 2) {


            dealer.setCards(cards.getCard());
            dealer.setCards(cards.getCard());

        } else if (aNumCards == 1)
            dealer.setCards(cards.getCard());

        updatePointsDealer();
    }

    public void sendInitialCards() {

        int size = players.size();

        //hasmap to send to client
        HashMap<String, Player> playersToUpdate = new HashMap<>();
        List<RemotePlayer> mainList = new ArrayList<>();

        for (int x = size - 1; x >= 0; x--) {

            mainList.addAll(players.values());
            RemotePlayer auxPlayer = mainList.get(x);

            if (auxPlayer.getDataRemotePlayer().getCards().size() == 0) {
                //Assign cards to the player
                auxPlayer.getDataRemotePlayer().setCards(cards.getCard());
                auxPlayer.getDataRemotePlayer().setCards(cards.getCard());

                try {
                    betNewChips(auxPlayer.getDataRemotePlayer());
                } catch (RemoteException re) {
                    eliminatePlayer(auxPlayer.getDataRemotePlayer().getName());
                }
                playersToUpdate.put(auxPlayer.getDataRemotePlayer().getName(), changeValuesAs(updatePointsPlayer(auxPlayer.getDataRemotePlayer())));
            }
        }

        for (RemotePlayer remotePlayer : players.values()) {
            try {
                remotePlayer.getRemotePlayer().getCardsOnBeginGame(playersToUpdate, dealer.getCards());
                remotePlayer.getRemotePlayer().indicationPlay(players.size() - 1);
                remotePlayer.getRemotePlayer().changeBtnsState(false);

                if (remotePlayer.getDataRemotePlayer().getPosition() == players.size() - 1) {
                    if (gameisRunning) {
                        waitingForPlayerToPlay.interrupt();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {

                        }
                    }
                    waiting20SecToPlay(remotePlayer);
                    remotePlayer.getRemotePlayer().changeBtnsState(true);
                }
            } catch (RemoteException re) {
                eliminatePlayer(remotePlayer.getDataRemotePlayer().getName());
            }
        }
        sendDataToObserversNewRound(playersToUpdate, dealer.getCards());
        sendMessage("É a vez do jogador " + mainList.get(players.size() - 1).getDataRemotePlayer().getName() + " jogar.");
    }

    public void overTwentyOne(Player aPLayer) throws RemoteException {

        int points = aPLayer.getGain();

        if (points > 21)
            sendMessage("O jogador " + aPLayer.getName() + " utrapassou os 21 pontos e rebentou.");

        else if (points == 21)
            sendMessage("O jogador " + aPLayer.getName() + " fez blackjack.");
    }

    @Override
    public void over21OrBlackJack(String aPlayerName) throws RemoteException {

        List<RemotePlayer> mainList = new ArrayList<RemotePlayer>(players.values());

        int i = players.get(aPlayerName).getDataRemotePlayer().getPosition();

        for (RemotePlayer rp : mainList) {
            if (i != 0) {
                try {
                    rp.getRemotePlayer().indicationPlay(i - 1);
                } catch (RemoteException re) {
                    eliminatePlayer(rp.getDataRemotePlayer().getName());
                }

                if (rp.getDataRemotePlayer().getPosition() == i - 1) {

                    waitingForPlayerToPlay.interrupt();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {

                    }
                    waiting20SecToPlay(rp);
                }
            } else {
                sendRoundInfoDealer();
            }
        }
    }


    public void waitingToPlay(int time) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                long initialTime = new Date().getTime();

                sendMessage("O jogo vai começar em 10 segundos");

                while (true) {
                    if (new Date().getTime() - initialTime >= time) {

                        sendMessage("O jogo começou!");
                        atributeCardsDealer(2);
                        sendInitialCards();

                        break;
                    }
                }

            }
        }).start();
    }

    public Player updatePointsPlayer(Player aPlayer) {
        int totalPoints = 0;

        if (aPlayer.getCards() != null && aPlayer.getCards().size() > 0) {

            Iterator<Card> cardPlayer = aPlayer.getCards().iterator();

            while (cardPlayer.hasNext()) {
                totalPoints += cardPlayer.next().getValue();
            }

            aPlayer.setGain(totalPoints);
        }

        return aPlayer;
    }

    public Player verifyValuesCards(Player aPlayer) {
        int newValue = 0;

        for (Card card : aPlayer.getCards())
            newValue += card.getValue();

        for (Card card : aPlayer.getCards()) {
            if (newValue > 21 && (card.getName().equals("c1") || card.getName().equals("h1")
                    || card.getName().equals("d1") || card.getName().equals("s1"))) {
                newValue -= 10;
            }
        }

        aPlayer.setGain(newValue);

        return aPlayer;
    }

    @Override
    public void sendCommandExit(String aNamePlayer) throws RemoteException {
        this.eliminatePlayer(aNamePlayer);
    }
}
