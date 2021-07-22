package pt.ipc.estgoh.projetoFinal.view;

import pt.ipc.estgoh.projetoFinal.controller.PlayerController;
import pt.ipc.estgoh.projetoFinal.model.Card;
import pt.ipc.estgoh.projetoFinal.model.InterfaceBlackjack;
import pt.ipc.estgoh.projetoFinal.model.Player;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;


public class Game extends JFrame {

    InterfaceBlackjack serverRemote;
    PlayerController playerController;

    // ********Panels******** //
    JPanel gamePanel = new JPanel();
    JPanel observerLogPanel = new JPanel();
    JPanel playerPanel = new JPanel();
    JPanel player1cardsPanel = new JPanel();
    JPanel player2cardsPanel = new JPanel();
    JPanel player3cardsPanel = new JPanel();
    JPanel dealerPanel = new JPanel();
    JPanel dealerCardsPanel = new JPanel();
    JPanel playerBtnPanel = new JPanel();
    JPanel exitBtnPanel = new JPanel();


    // *******Labels******* //
    //dealer
    private JLabel dealer = new JLabel();
    private JLabel dealerPoints = new JLabel();

    //game
    private JLabel tittle = new JLabel();

    //cards arrays
    private ArrayList<CardLabel> jgd1CardsList = new ArrayList<CardLabel>();
    private ArrayList<CardLabel> jgd2CardsList = new ArrayList<CardLabel>();
    private ArrayList<CardLabel> jgd3CardsList = new ArrayList<CardLabel>();
    private ArrayList<CardLabel> dealerCardsList = new ArrayList<CardLabel>();

    //Array of Strings with name of the observers
    private String[] elementsTable;

    //indication if is player or observer
    private JLabel playerOrObserver = new JLabel();


    //player1
    private JLabel jgd1 = new JLabel();
    private JLabel jgd1Currency = new JLabel();
    private JLabel jgd1Points = new JLabel();

    //player2
    private JLabel jgd2 = new JLabel();
    private JLabel jgd2Currency = new JLabel();
    private JLabel jgd2Points = new JLabel();

    //player3
    private JLabel jgd3 = new JLabel();
    private JLabel jgd3Currency = new JLabel();
    private JLabel jgd3Points = new JLabel();


    //font
    private Font tittleFont = new Font(Font.SANS_SERIF, Font.BOLD, 32);
    private Font currencyPoints = new Font(Font.SANS_SERIF, Font.PLAIN, 20);


    //Buttons
    private JButton hitBtn = new JButton();
    private JButton standBtn = new JButton();
    private JButton exitBtn = new JButton();


    //Table
    JTable observersTable;
    JScrollPane observersTableScroll;
    JScrollPane logTextAreaScroll;

    DefaultTableModel model;

    //JTextArea
    JTextArea logTextArea = new JTextArea();


    //padding
    private Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);

    //colors
    private Color bgColor = new Color(0, 102, 51);
    private Color btnHitColor = new Color(133, 183, 157);
    private Color btnStandColor = new Color(250, 223, 127);


    public Game() throws HeadlessException {
        initComponents();
        insertListeners();
    }

    public void initComponents() {
        //BlackJack table background image - main Jpanel
        this.setSize(1200, 1000);
        this.setResizable(false);
        this.setTitle("BlackJack");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(bgColor);
        this.setLayout(new BorderLayout());
        //imgLabel = new JLabel(new ImageIcon(getClass().getResource("./resources/images/blackjack_background.jpg")));


        //Tittle label
        tittle = this.generateLabelStyle(tittle, "BlackJack Online", padding);
        tittle.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));


        //Dealer
        dealer = this.generateLabelStyle(dealer, "Dealer", padding);
        dealer.setVerticalAlignment(JLabel.TOP);

        //Deal panel
        dealerPanel.setLayout(new GridLayout(3, 1));
        dealerPanel.setBackground(bgColor);


        //gamePanel
        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(bgColor);

        //exitPanel
        exitBtnPanel.setBackground(bgColor);
        exitBtnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        //observers and log panel
        observerLogPanel.setLayout(new GridLayout(2, 1));

        //players panel
        playerPanel.setLayout(new GridLayout(5, 3));


        //player points
        playerPanel.add(jgd1Points);
        playerPanel.add(jgd2Points);
        playerPanel.add(jgd3Points);


        //cards panels
        player1cardsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        player1cardsPanel.setBackground(bgColor);
        player2cardsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        player2cardsPanel.setBackground(bgColor);
        player3cardsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        player3cardsPanel.setBackground(bgColor);
        dealerCardsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        dealerCardsPanel.setBackground(bgColor);


        //"give cards" - put empty labels on cards panel
        this.resetHand(true);


        //add card panels to their respective position
        playerPanel.add(player1cardsPanel);
        playerPanel.add(player2cardsPanel);
        playerPanel.add(player3cardsPanel);


        //dealer adds
        dealerPoints = generateLabelStyle(dealerPoints, "0", padding);
        dealerPoints.setFont(currencyPoints);
        dealerPanel.add(dealerPoints);
        dealerPanel.add(dealerCardsPanel);
        dealerPanel.add(dealer);


        //player adds
        playerPanel.add(jgd1);
        playerPanel.add(jgd2);
        playerPanel.add(jgd3);
        playerPanel.add(jgd1Currency);
        playerPanel.add(jgd2Currency);
        playerPanel.add(jgd3Currency);
        playerPanel.setBackground(bgColor);
        playerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));


        //buttons
        playerBtnPanel.setBackground(bgColor);
        hitBtn.setText("HIT");
        hitBtn.setFont(tittleFont);
        hitBtn.setBackground(btnHitColor);
        hitBtn.setPreferredSize(new Dimension(150, 50));
        hitBtn.setVerticalAlignment(JButton.TOP);
        hitBtn.setFocusPainted(false);
        standBtn.setText("STAND");
        standBtn.setBackground(btnStandColor);
        standBtn.setFont(tittleFont);
        standBtn.setFocusPainted(false);
        standBtn.setPreferredSize(new Dimension(150, 50));
        standBtn.setVerticalAlignment(JButton.TOP);
        exitBtn.setText("Exit");
        exitBtn.setBackground(Color.gray);
        exitBtn.setFont(tittleFont);
        exitBtn.setFocusPainted(false);
        exitBtn.setPreferredSize(new Dimension(100, 50));

        this.changeExitState(false);
        this.changeStateBtn(false);


        //add buttons
        playerPanel.add(playerOrObserver);
        playerBtnPanel.add(hitBtn);
        playerBtnPanel.add(standBtn);
        playerPanel.add(playerBtnPanel);
        exitBtnPanel.add(exitBtn);
        playerPanel.add(exitBtnPanel);


        //Add to game panel panel
        gamePanel.add(tittle, BorderLayout.NORTH);
        gamePanel.add(dealerPanel, BorderLayout.CENTER);
        gamePanel.add(playerPanel, BorderLayout.SOUTH);


        //create observers Jtable
        observersTable = new JTable();
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{"Observers"});
        observersTable.setModel(model);
        observersTableScroll = new JScrollPane();
        observersTableScroll.setViewportView(observersTable);

        //logTextArea
        logTextArea.setLineWrap(true);
        logTextArea.setWrapStyleWord(true);
        logTextAreaScroll = new JScrollPane(logTextArea);

        observerLogPanel.setPreferredSize(new Dimension(250, 100));
        observerLogPanel.add(observersTableScroll);
        observerLogPanel.add(logTextAreaScroll);


        this.add(gamePanel, BorderLayout.CENTER);
        this.add(observerLogPanel, BorderLayout.EAST);
    }

    public void changePlayerType(String playerType) {
        playerOrObserver = generateLabelStyle(playerOrObserver, playerType, null);
    }

    public JLabel generateLabelStyle(JLabel label, String text, Border padding) {
        label.setText(text);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setFont(tittleFont);
        label.setForeground(Color.white);

        if (padding != null)
            label.setBorder(padding);

        return label;
    }

    public void verifyPlayersOnTable(HashMap<String, Player> players) {
        for (Player player : players.values()) {
            insertPlayers(player.getPosition(), player);
        }
    }

    //Insert observers in table of observers
    public void insertObservers(HashMap<String, Player> aObservers) {

        for (int j = 0; j < model.getRowCount(); j++)
            model.removeRow(j);

        elementsTable = new String[aObservers.size()];

        for (Map.Entry<String, Player> remotePlayer : aObservers.entrySet()) {
            for (int i = 0; i < elementsTable.length; i++)
                elementsTable[i] = remotePlayer.getKey();

            model.addRow(elementsTable);
        }
    }

    //insert player information on table - this do not insert cards!!
    public void insertPlayers(int tablePosition, Player player) {
        switch (tablePosition) {
            case 0: {
                if (!player.getName().equalsIgnoreCase(jgd1.getText())) {
                    //points
                    jgd1Points = this.generateLabelStyle(jgd1Points, "" + player.getGain(), padding);
                    jgd1Points.setFont(currencyPoints);

                    //nome
                    jgd1 = this.generateLabelStyle(jgd1, player.getName(), padding);

                    this.giveCards(0, player);

                    //currency
                    jgd1Currency = this.generateLabelStyle(jgd1Currency, "Fichas: " + player.getChipsPlayer(), null);
                    jgd1Currency.setFont(currencyPoints);
                    jgd1Currency.setVerticalAlignment(JLabel.TOP);
                }
                break;
            }
            case 1: {
                if (!player.getName().equalsIgnoreCase(jgd2.getText())) {

                    //points
                    jgd2Points = this.generateLabelStyle(jgd2Points, "" + player.getGain(), padding);
                    jgd2Points.setFont(currencyPoints);

                    //nome
                    jgd2 = this.generateLabelStyle(jgd2, player.getName(), padding);

                    this.giveCards(1, player);

                    //currency
                    jgd2Currency = this.generateLabelStyle(jgd2Currency, "Fichas: " + player.getChipsPlayer(), null);
                    jgd2Currency.setFont(currencyPoints);
                    jgd2Currency.setVerticalAlignment(JLabel.TOP);
                }
                break;
            }
            case 2: {
                if (!player.getName().equalsIgnoreCase(jgd3.getText())) {

                    //points
                    jgd3Points = this.generateLabelStyle(jgd3Points, "" + player.getGain(), padding);
                    jgd3Points.setFont(currencyPoints);

                    //nome
                    jgd3 = this.generateLabelStyle(jgd3, player.getName(), padding);

                    this.giveCards(2, player);

                    //currency
                    jgd3Currency = this.generateLabelStyle(jgd3Currency, "Fichas: " + player.getChipsPlayer(), null);
                    jgd3Currency.setFont(currencyPoints);
                    jgd3Currency.setVerticalAlignment(JLabel.TOP);
                }
                break;
            }
        }
    }

    public void verifyNewRound(HashMap<String, Player> aPlayers) {
        for (Player player : aPlayers.values()) {
            giveCards(player.getPosition(), player);
            this.updatePoints(player);
            this.updateChipsValue(player);
        }
    }

    public void changeStateBtn(boolean aState) {
        standBtn.setEnabled(aState);
        hitBtn.setEnabled(aState);
    }


    public void giveCards(int tablePosition, Player aPlayer) {

        CopyOnWriteArrayList<Card> cardsPlayer = aPlayer.getCards();
        int y = aPlayer.getCards().size();

        switch (tablePosition) {
            case 0:
          
                for (int x = 0; x < y; x++) {
                    this.jgd1CardsList.get(x).setCardImage(cardsPlayer.get(x).getName());
                    if (x != (y - 1)) {
                        this.jgd1CardsList.get(x).setCardCovered(true);
                    } else {
                        this.jgd1CardsList.get(x).setCardCovered(false);
                    }
                }
                break;
            case 1:
                for (int x = 0; x < y; x++) {
                    this.jgd2CardsList.get(x).setCardImage(cardsPlayer.get(x).getName());
                    if (x != (y - 1))
                        this.jgd2CardsList.get(x).setCardCovered(true);
                    else
                        this.jgd2CardsList.get(x).setCardCovered(false);
                }
                break;
            case 2:
                for (int x = 0; x < y; x++) {
                    this.jgd3CardsList.get(x).setCardImage(cardsPlayer.get(x).getName());
                    if (x != (y - 1))
                        this.jgd3CardsList.get(x).setCardCovered(true);
                    else
                        this.jgd3CardsList.get(x).setCardCovered(false);
                }
                break;
        }
    }

    public void updateChipsValue(Player player) {
        switch (player.getPosition()) {
            case 0:
                jgd1Currency.setText("Fichas: " + player.getChipsPlayer());
                break;
            case 1:
                jgd2Currency.setText("Fichas: " + player.getChipsPlayer());
                break;
            case 2:
                jgd3Currency.setText("Fichas: " + player.getChipsPlayer());
                break;
        }
    }

    public void changeExitState(boolean aState) {
        this.exitBtn.setEnabled(aState);
    }

    public void updatePoints(Player player) {
        switch (player.getPosition()) {
            case 0:
                jgd1Points.setText("" + player.getGain());
                break;
            case 1:
                jgd2Points.setText("" + player.getGain());
                break;
            case 2:
                jgd3Points.setText("" + player.getGain());
                break;
        }
    }

    public void resetHand(boolean firstTime) {
        for (int x = 0; x < 7; x++) {
            if (firstTime) {
                //cardsLists keeps the reference to the object
                CardLabel cardLabel = new CardLabel();
                player1cardsPanel.add(cardLabel);
                jgd1CardsList.add(cardLabel);
                cardLabel = new CardLabel();
                player2cardsPanel.add(cardLabel);
                jgd2CardsList.add(cardLabel);
                cardLabel = new CardLabel();
                player3cardsPanel.add(cardLabel);
                jgd3CardsList.add(cardLabel);
                cardLabel = new CardLabel();
                dealerCardsPanel.add(cardLabel);
                dealerCardsList.add(cardLabel);
            } else {
                //clean previous hand
                jgd1CardsList.get(x).setCardCovered(false);
                jgd2CardsList.get(x).setCardCovered(false);
                jgd3CardsList.get(x).setCardCovered(false);
                jgd1CardsList.get(x).setCardImage(null);
                jgd2CardsList.get(x).setCardImage(null);
                jgd3CardsList.get(x).setCardImage(null);
                dealerCardsList.get(x).setCardImage(null);
            }
        }
    }

    public void resetPoints() {
        if (jgd1.getText().length() > 0)
            this.jgd1Points.setText("0");

        if (jgd2.getText().length() > 0)
            this.jgd2Points.setText("0");

        if (jgd3.getText().length() > 0)
            this.jgd3Points.setText("0");

        if (dealerPoints.getText().length() > 0)
            this.dealerPoints.setText("0");
    }

    public void treatCardsDealer(CopyOnWriteArrayList<Card> cardsDealer, boolean notEndRound) throws RemoteException {
        int y = cardsDealer.size();

        for (int x = 0; x < y; x++) {
            if (x == 0 && notEndRound)
                dealerCardsList.get(x).setCardImage("bv");
            else {
                dealerCardsList.get(x).setCardImage(cardsDealer.get(x).getName());
                dealerPoints.setText("" + cardsDealer.get(x).getValue());
            }
        }
    }

    public void updateDealerPoints(int points) {
        dealerPoints.setText("" + points);
    }

    public void updateDealerEndRound(CopyOnWriteArrayList<Card> cards, int points, int state) throws RemoteException {
        treatCardsDealer(cards, false);
        updateDealerPoints(points);

        switch (state) {
            case 1:
                dealerPoints.setText("Over21");
                break;
            case -1:
                dealerPoints.setText("BlackJack");
                break;
        }
    }

    public void isYourTimeToPlay(int aTablePosition) {
        switch (aTablePosition) {
            case 0:
                jgd1.setForeground(Color.YELLOW);
                jgd2.setForeground(Color.white);
                jgd3.setForeground(Color.white);
                break;
            case 1:
                jgd2.setForeground(Color.YELLOW);
                jgd1.setForeground(Color.white);
                jgd3.setForeground(Color.white);
                break;
            case 2:
                jgd3.setForeground(Color.YELLOW);
                jgd1.setForeground(Color.white);
                jgd2.setForeground(Color.white);
                break;
        }
    }


    //Se não houver um observador para substituir o jogador
    public void cleanTableAfterExit(HashMap<String, Player> aPlayers) {
        cleanInfoPlayers();

        for (Player player : aPlayers.values()) {
            insertPlayers(player.getPosition(), player);
            playerOrObserver.setText("Player");
        }
    }

    public void cleanInfoPlayers() {

        jgd1Points.setText("");
        jgd1Points.setText("");
        jgd1Currency.setText("");
        jgd1.setText("");

        jgd2Points.setText("");
        jgd2Points.setText("");
        jgd2Currency.setText("");
        jgd2.setText("");

        jgd3Points.setText("");
        jgd3Points.setText("");
        jgd3Currency.setText("");
        jgd3.setText("");
    }

    public void updatePlayerInfoAfterExit(Player player) {
        cleanInfoPlayers();
        insertPlayers(player.getPosition(), player);
        changePlayerType("Player");

    }

    public boolean checkButtonState() {
        return hitBtn.isEnabled();
    }

    public void updatePlayerInfoAfterHit(Player aPlayer, int state) throws RemoteException {

        this.updatePoints(aPlayer);
        this.giveCards(aPlayer.getPosition(), aPlayer);

        switch (state) {

            //blackjack
            case -1:
                switch (aPlayer.getPosition()) {
                    case 0:
                        if (aPlayer.getName().equalsIgnoreCase(jgd1.getText())) {
                            jgd1Points.setText(jgd1Points.getText() + " - BlackJack");
                            //round is over
                            serverRemote.over21OrBlackJack(aPlayer.getName());
                        }
                        break;
                    case 1:
                        if (aPlayer.getName().equalsIgnoreCase(jgd2.getText())) {
                            jgd2Points.setText(jgd2Points.getText() + " - BlackJack");
                            serverRemote.over21OrBlackJack(aPlayer.getName());
                        }
                        break;
                    case 2:
                        if (aPlayer.getName().equalsIgnoreCase(jgd3.getText())) {
                            jgd3Points.setText(jgd3Points.getText() + " - BlackJack");
                            serverRemote.over21OrBlackJack(aPlayer.getName());
                        }
                        break;
                }

                break;

            //Over21
            case 1:
                switch (aPlayer.getPosition()) {
                    case 0:
                        if (aPlayer.getName().equalsIgnoreCase(jgd1.getText())) {
                            jgd1Points.setText(jgd1Points.getText() + " - Over 21");
                            serverRemote.over21OrBlackJack(aPlayer.getName());
                        }
                        break;
                    case 1:
                        if (aPlayer.getName().equalsIgnoreCase(jgd2.getText())) {
                            jgd2Points.setText(jgd2Points.getText() + " - Over 21");
                            serverRemote.over21OrBlackJack(aPlayer.getName());
                        }
                        break;
                    case 2:
                        if (aPlayer.getName().equalsIgnoreCase(jgd3.getText())) {
                            jgd3Points.setText(jgd3Points.getText() + " - Over 21");
                            serverRemote.over21OrBlackJack(aPlayer.getName());
                        }
                        break;
                }
                break;
        }

    }

    public void updateWinners(Player aPlayer) {
        updateChipsValue(aPlayer);
    }


    public void setServerRemote(InterfaceBlackjack serverRemote) {
        this.serverRemote = serverRemote;
    }

    public InterfaceBlackjack getServerRemote() {
        return serverRemote;
    }

    public void insertListeners() {

        hitBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    serverRemote.sendCommandHit(playerController.ownName);
                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                }
            }
        });

        standBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    serverRemote.sendCommandStand(playerController.ownName);
                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                }
            }
        });

        exitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    serverRemote.sendCommandExit(playerController.ownName);
                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                }

                System.exit(0);
            }
        });

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(new Game(),
                        "Are you sure you want to close this window?", "Close Window?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){

                    try {
                        serverRemote.sendCommandExit(playerController.ownName);
                    } catch (RemoteException remoteException) {
                        remoteException.printStackTrace();
                    }

                    System.exit(0);
                }
            }
        });
    }

    public void setPlayerController(PlayerController playerController) {
        this.playerController = playerController;
    }

    public void appendLog(String aMsg) {
        logTextArea.append(aMsg);
    }
}
