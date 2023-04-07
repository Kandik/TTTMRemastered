package tictactoemaster;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

/*

▀█▀ █ █▀▀   ▀█▀ ▄▀█ █▀▀   ▀█▀ █▀█ █▀▀   █▀▄▀█ ▄▀█ █▀ ▀█▀ █▀▀ █▀█
░█░ █ █▄▄   ░█░ █▀█ █▄▄   ░█░ █▄█ ██▄   █░▀░█ █▀█ ▄█ ░█░ ██▄ █▀▄

Tic Tac Toe Master

Java full edition

Created in 2019
Presented at Festival of science and technology AMAVET 2019
Participant of national round of Slovakia
Refactored, optimized and translated to English in 2023

Developed by Štefan Kando
Presented by Štefan Kando and Bára Elisabeth Dočkalová


File: AIPlayerSelect.java
Panel for selecting the player (X or O) for game against the AI

*/


class AISelectFrame extends JFrame {

    AISelectPanel panel = new AISelectPanel();

    AISelectFrame() {
        TTTM.setFrame(this, panel, "Select your player");
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                panel.exitToMenu();
            }
        });
    }

}

class AISelectPanel extends JPanel {

    JButton X = new JButton(), O = new JButton(), menu = new JButton();

    AISelectPanel() {
        this.add(X);
        TTTM.setButton(X, 100, 150, 300, 300, null, 0);
        setButton(X);

        this.add(O);
        TTTM.setButton(O, 465, 150, 300, 300, null, 0);
        setButton(O);

        this.add(menu);
        TTTM.setButton(menu, 370, 500, 120, 50, "Back", 12);

        X.addActionListener((ae) -> {
            TTTM.AI = new AIGameFrame();
            TTTM.AI.panel.player = 0;
            TTTM.AI.setVisible(true);
            TTTM.reset();
            TTTM.AI.panel.setButtons(true);
            TTTM.asf.setVisible(false);
            TTTM.asf = null;
        });

        O.addActionListener((ae) -> {
            TTTM.AI = new AIGameFrame();
            TTTM.AI.panel.player = 1;
            TTTM.AI.setVisible(true);
            TTTM.reset();
            TTTM.AI.panel.setButtons(true);
            TTTM.asf.setVisible(false);
            TTTM.asf = null;
        });

        menu.addActionListener((ae) -> {
            exitToMenu();
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 48));
        g.drawString("Select your player", 252, 75);

        TTTM.drawFancyLine(g, 420, 150, 30, 280);
        int x = 250, y = 300;
        int xwidth = 50;
        int xthickness = 30;

        for (int i = 0; i < xthickness; i++) {
            g.drawLine(x - xwidth - (xthickness / 2) + i, y - xwidth,
                    x + xwidth - (xthickness / 2) + i, y + xwidth);
            g.drawLine(x + xwidth - (xthickness / 2) + i, y - xwidth,
                    x - xwidth - (xthickness / 2) + i, y + xwidth);
        }

        x = 615;
        int owidth = 120;
        int othickness = 40;

        Color color = g.getColor();
        g.fillOval(x - (owidth / 2), y - (owidth / 2),
                owidth, owidth);
        g.setColor(TTTM.backgroundColor);
        g.fillOval(x - ((owidth - othickness) / 2), 
                y - ((owidth - othickness) / 2),
                owidth - othickness, owidth - othickness);
        g.setColor(color);

    }

    void setButton(JButton b) {
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (b.isEnabled()) {
                    b.setBorderPainted(true);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                b.setBorderPainted(false);
            }
        });
    }

    void exitToMenu() {
        TTTM.gm.setVisible(true);
        TTTM.asf.setVisible(false);
        TTTM.asf = null;
    }

}