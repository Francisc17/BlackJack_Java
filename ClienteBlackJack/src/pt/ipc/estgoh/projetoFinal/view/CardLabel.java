package pt.ipc.estgoh.projetoFinal.view;

import javax.swing.*;
import java.awt.*;

// cada carta é apresentada num JLabel
public class CardLabel extends JLabel {

    public CardLabel() {

    }

    // define o icone do label, que é a imagem da carta
    public void setCardImage(String name) {
        if (name == null) {
            this.setIcon(null);
        } else {

            ImageIcon im = new ImageIcon(getClass().getResource("resources/cards/" + name + ".png"));
            this.setIcon(im);
        }
    }

    // define se a carta vai ser coberta parciamente por outra
    public void setCardCovered(boolean covered) {
        Icon ic = this.getIcon();
        if (ic == null)
            return;
        int cardWidth = ic.getIconWidth();
        int cardHeight = ic.getIconHeight();
        if (covered)
            this.setPreferredSize(new Dimension(20, cardHeight));
        else
            this.setPreferredSize(new Dimension(cardWidth, cardHeight));
    }
}
