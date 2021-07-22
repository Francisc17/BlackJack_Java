package pt.ipc.estgoh.projetoFinal.view;

import pt.ipc.estgoh.projetoFinal.Main;
import pt.ipc.estgoh.projetoFinal.controller.LoginController;
import pt.ipc.estgoh.projetoFinal.model.InterfaceBlackjack;
import pt.ipc.estgoh.projetoFinal.model.Player;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.ConnectException;

public class Login extends JFrame {

    //loginController
    LoginController loginController = new LoginController();


    //labels
    private JLabel playerNameLabel = new JLabel("Nome:");
    private JLabel ipLabel = new JLabel("IP:");
    private JLabel portLabel = new JLabel("Porto:");

    //Buttons
    private JButton loginButton = new JButton("Confirmar");

    //TextFields
    private JTextField userText = new JTextField();
    private JTextField ipText = new JTextField();
    private JTextField portText = new JTextField();

    //Border
    private Border border = BorderFactory.createEmptyBorder(5,30,5,30);


    public Login(){
        this.setSize(250,220);
        this.setTitle("BlackJack Online");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.initComponents();
        this.insertListeners();
    }

    public void initComponents(){
        this.setLayout(null);

        ipLabel.setBounds(10,10,30,25);
        this.add(ipLabel);

        ipText.setBounds(60,10,168,25);
        this.add(ipText);

        portLabel.setBounds(10, 50, 40, 25);
        this.add(portLabel);

        portText.setBounds(60,50,60,25);
        this.add(portText);

        playerNameLabel.setBounds(10,90,50,25);
        this.add(playerNameLabel);

        userText.setBounds(60,90,110,25);
        this.add(userText);

        loginButton.setBounds(120,140,100,30);
        this.add(loginButton);
    }

    public void insertListeners(){
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Boolean validUser =  loginController.registerPlayer(ipText.getText(),Integer.parseInt(portText.getText()),userText.getText());

                if (validUser == null)
                    showInfoMessage("Dados do servidor/porto incorretos");
                else if (validUser) {
                    Main.loginToGame(loginController.getServerBlackjack(),loginController.getPlayerBlackjack());
                }else {
                    showErrorMessage("Nome já existente!");
                }


            }
        });
    }

    public void showErrorMessage(String error){
        JOptionPane.showMessageDialog(null,error,"Ocorreu um erro",JOptionPane.ERROR_MESSAGE);
    }

    public void showInfoMessage(String msg){
        JOptionPane.showMessageDialog(null,msg,"Informação",JOptionPane.INFORMATION_MESSAGE);
    }
}
