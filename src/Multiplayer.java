package tictactoemaster;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
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


File: Multiplayer.java
Game for two human players to play locally against each other

*/


class MPGameFrame extends JFrame {

    MPGamePanel panel = new MPGamePanel();

    MPGameFrame() {
        TTTM.setFrame(this, panel, "Multiplayer");
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                panel.exitToMenu();
            }
        });

        TTTM.returnToMenu.addActionListener((ae) -> {
            panel.exitToMenu();
        });

    }

}

class MPGamePanel extends JPanel implements ActionListener, KeyListener {

    Timer t = new Timer(100, this);

    MPGamePanel() {
        addKeyListener(this);
        TTTM.addButtons(this);
        t.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        TTTM.drawField(g);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {

        repaint();

        TTTM.checkInput();
        TTTM.winCheck();

        if (TTTM.mp.isVisible()) {
            TTTM.returnToMenu.addActionListener((al) -> {
                exitToMenu();
            });
        }
    }

    void exitToMenu() {
        TTTM.mf.setVisible(true);
        TTTM.mp.setVisible(false);
        TTTM.reset();
        TTTM.mp = null;
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        TTTM.setKeys(ke);
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }

}