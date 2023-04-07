package tictactoemaster;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
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


File: GenerationCombinations.java
Panel for generating possible combinations of the game into 'combinations' table

*/


class ComGenFrame extends JFrame {

    ComGenPanel panel = new ComGenPanel();

    ComGenFrame() {
        TTTM.setFrame(this, panel, "Combinations generation");
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                panel.exitToMenu();
            }
        });
    }
}

class ComGenPanel extends JPanel implements ActionListener {

    boolean start = false, failed = false, done = false, check = true;

    int xwin = 0, owin = 0, drawc = 0, reply = -1, sends = 0;

    Timer t = new Timer(1, this);

    JButton StartStopButton = new JButton();
    JButton BackButton = new JButton();

    ArrayList<String> combinations = new ArrayList<String>();
    
    ComGenPanel() {
        TTTM.xstart -= 175;

        this.add(StartStopButton);
        TTTM.setButton(StartStopButton, 300, 500, 120, 50, "START", 12);

        this.add(BackButton);
        
        TTTM.setButton(BackButton, 430, 500, 120, 50, "Back", 12);

        StartStopButton.addActionListener((ae) -> {
            if (StartStopButton.getText().equals("START")) {
                if (TTTM.getComCount() != 0) {
                    reply = JOptionPane.showConfirmDialog(TTTM.cgf,
                            "Do you really want to clear the database and rerun the generation?",
                            "Generation", JOptionPane.YES_NO_OPTION,
                            JOptionPane.ERROR_MESSAGE);
                }
                if (reply == 0 || TTTM.getComCount() == 0) {
                    TTTM.reset();
                    TTTM.clearComb();
                    Arrays.fill(TTTM.branch, 0);
                    sends = 0;
                    start = true;
                    StartStopButton.setText("STOP");
                    failed = false;
                    BackButton.setEnabled(false);
                    reply = -1;
                    done = false;
                    xwin = 0;
                    owin = 0;
                    drawc = 0;
                }
            } else if (StartStopButton.getText().equals("STOP")) {
                start = false;
                StartStopButton.setText("START");
                TTTM.reset();
                Arrays.fill(TTTM.branch, 0);
                failed = true;
                repaint();
                BackButton.setEnabled(true);
            }
        });

        BackButton.addActionListener((ae) -> {
            exitToMenu();
        });

        repaint();
        t.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        TTTM.drawField(g);

        String comb = "", branch = "";
        for (int i = 0; i < 9; i++) {
            comb += Integer.toString(TTTM.combination[i]);
            branch += Integer.toString(TTTM.branch[i]);
        }

        int comCount = 0;
        if(!start){
            comCount = TTTM.getComCount();
        }
        else{
            comCount = sends*TTTM.batchsize + combinations.size();
        }
        
        int gap = 50;
        g.setColor(Color.BLACK);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        g.drawString(("Generated combination: " + comb), TTTM.xstart + 350, TTTM.ystart);
        g.drawString(("Combination branch: " + branch), TTTM.xstart + 350, TTTM.ystart + gap);
        g.drawString(("Database size: " + Integer.toString(comCount) + "/" + TTTM.maxcomb + " rows"),
                TTTM.xstart + 350, TTTM.ystart + gap * 2);
        g.drawString(("Wins of X: " + xwin), TTTM.xstart + 350, TTTM.ystart + gap * 3);
        g.drawString(("Wins of O: " + owin), TTTM.xstart + 350, TTTM.ystart + gap * 4);
        g.drawString(("Draws: " + drawc), TTTM.xstart + 350, TTTM.ystart + gap * 5);

        if (failed) {
            g.setColor(Color.RED);
            g.drawString("Combination generating cancelled", TTTM.xstart + 350, TTTM.ystart + gap * 6);
        }

        if (done) {
            g.setColor(Color.GREEN);
            g.drawString("Combination generating successful", TTTM.xstart + 350, TTTM.ystart + gap * 6);
        }
    }

    void exitToMenu() {
        TTTM.xstart += 175;
        start = false;
        TTTM.checkDatabases();
        TTTM.gm.setVisible(true);
        TTTM.cgf.setVisible(false);
        TTTM.cgf = null;
    }

    void sendCombinations(){
        String SQL = "INSERT IGNORE INTO combinations VALUES ";
                    
        for(String comb : combinations){
            SQL += "(\"" + comb + "\"),";
        }
        
        SQL = SQL.substring(0, SQL.length() -1);

        try {
            TTTM.insert.executeUpdate(SQL);
        } catch (SQLException e) {
        System.err.println(e);
        }

        sends++;
        combinations.clear();
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        
        if (start && TTTM.branch[0] < 9) {
            for(int iter = 0; iter < TTTM.batchsize; iter++){
                TTTM.sortBranches();
                for (int i = 0; i < TTTM.win.length; i++) {
                    Arrays.fill(TTTM.win[i], false);
                }

                for (int i = 0; i < 9; i++) {
                    if (check) {
                        TTTM.branchToOutput(TTTM.branch[i], i);
                        repaint();
                        if (i > 3) {
                            checkBranch(i);
                        }
                    }
                }

                TTTM.branch[7]++;
                check = true;
                
                if(!(start && TTTM.branch[0] < 9)){
                    break;
                }
            }
        }

        if (TTTM.branch[0] == 9) {
            if(combinations.size() != 0){
                sendCombinations();
            }
            done = true;
            start = false;
            StartStopButton.setText("START");
            BackButton.setEnabled(true);
        }

    }

    String DBcomb = "";

    
    void checkBranch(int branchpos) {
        TTTM.winCheck();
        repaint();

        if (TTTM.winner != -1) {

            for (int i = 0; i < branchpos + 1; i++) {
                DBcomb += Integer.toString(TTTM.combination[i]);
            }

            if (TTTM.winner == 2) {
                DBcomb += Integer.toString(9);
            }

            if (TTTM.doesExist(DBcomb)) {
                if (TTTM.winner < 2) {
                    switch (TTTM.winner) {
                        case 0:
                            xwin++;
                            break;
                        case 1:
                            owin++;
                            break;
                    }
                } else if (TTTM.winner == 2) {
                    drawc++;
                }
                
                if(!combinations.contains(DBcomb)){
                    combinations.add(DBcomb);
                }
                if(combinations.size() >= TTTM.batchsize){
                    sendCombinations();
                }
                
            }

            DBcomb = "";

            Arrays.fill(TTTM.occupancy, -1);

            check = false;
            TTTM.winner = -1;

        }
    }

}