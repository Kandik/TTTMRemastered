package tictactoemaster;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

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


File: MenuMain.java
Main menu that shows after starting the application

*/


class MenuFrame extends JFrame {

    MenuPanel panel = new MenuPanel();

    MenuFrame() {
        TTTM.setFrame(this, panel, "Main menu");
    }

}

class MenuPanel extends JPanel {

    JButton AIMenuButton = new JButton();
    JButton MPButton = new JButton();
    JButton ExitButton = new JButton();

    MenuPanel() {
        this.setBackground(Color.white);

        this.add(AIMenuButton);
        TTTM.setButton(AIMenuButton, 310, 200, 250, 75, "Unbeatable AI - MySQL", 18);

        this.add(MPButton);
        TTTM.setButton(MPButton, 310, 300, 250, 75, "Multiplayer", 18);

        this.add(ExitButton);
        TTTM.setButton(ExitButton, 360, 400, 150, 40, "Exit", 14);

        AIMenuButton.addActionListener((ae) -> {
            TTTM.mf.setVisible(false);
            TTTM.dbcf = new DBConnectFrame();
            TTTM.dbcf.setVisible(true);
        });

        MPButton.addActionListener((ae) -> {
            TTTM.mf.setVisible(false);
            TTTM.mp = new MPGameFrame();
            TTTM.mp.setVisible(true);
            TTTM.reset();
        });

        ExitButton.addActionListener((ae) -> {
            System.exit(0);
        });

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 40));
        g.drawString("TicTacToe Master Remastered", 150, 140);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        g.drawString("Made by Štefan Kando", 335, 165);
    }

}