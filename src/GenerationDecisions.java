package tictactoemaster;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
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


File: GenerationDecisions.java
Panel for generating decisions for each branch into 'decisions' table

 */
class DecGenFrame extends JFrame {

    DecGenPanel panel = new DecGenPanel();

    DecGenFrame() {
        TTTM.setFrame(this, panel, "Decisions generation");
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                panel.exitToMenu();
            }
        });
    }
}

class DecGenPanel extends JPanel implements ActionListener {

    boolean start = false, failed = false, done = false, check = true, generating = false;

    double xwin = 0, owin = 0, draw = 0;

    int stage = 0, reply = -1, delay = 0, sends = 0;

    Timer t = new Timer(1, this);

    JButton StartStopButton = new JButton();
    JButton BackButton = new JButton();

    String comb = "", branches = "";

    ArrayList<String[]> decisions = new ArrayList<String[]>();
    ArrayList<String[]> branchData = new ArrayList<String[]>();
    
    DecGenPanel() {

        TTTM.formatter.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));

        this.add(StartStopButton);
        TTTM.setButton(StartStopButton, 300, 500, 120, 50, "START", 12);

        this.add(BackButton);
        TTTM.setButton(BackButton, 430, 500, 120, 50, "Back", 12);

        StartStopButton.addActionListener((ae) -> {
            if (StartStopButton.getText().equals("START")) {
                if (TTTM.getDecCount() != 0) {
                    reply = JOptionPane.showConfirmDialog(TTTM.dgf,
                            "Do you really want to clear the database and rerun the generation?",
                            "Generation", JOptionPane.YES_NO_OPTION,
                            JOptionPane.ERROR_MESSAGE);
                }
                if (reply == 0 || TTTM.getDecCount() == 0) {
                    TTTM.reset();
                    TTTM.clearDec();
                    sends = 0;
                    generating = true;
                    start = true;
                    StartStopButton.setText("STOP");
                    failed = false;
                    BackButton.setEnabled(false);
                    reply = -1;
                }
            } else if (StartStopButton.getText().equals("STOP")) {
                start = false;
                StartStopButton.setText("START");
                TTTM.reset();
                Arrays.fill(TTTM.branch, 0);
                failed = true;
                //stage=0;
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

    int x = TTTM.xstart - 190;

    void drawX(Graphics g, int y) {
        int xwidth = 9;
        int xthickness = 5;

        for (int i = 0; i < xthickness; i++) {
            g.drawLine(x - xwidth - (xthickness / 2) + i, y - xwidth, x + xwidth - (xthickness / 2) + i, y + xwidth);
            g.drawLine(x + xwidth - (xthickness / 2) + i, y - xwidth, x - xwidth - (xthickness / 2) + i, y + xwidth);
        }

    }

    void drawO(Graphics g, int y) {
        int owidth = 21;
        int othickness = 6;

        Color color = g.getColor();
        g.fillOval(x - (owidth / 2), y - (owidth / 2), owidth, owidth);
        g.setColor(TTTM.backgroundColor);
        g.fillOval(x - ((owidth - othickness) / 2), y - ((owidth - othickness) / 2), owidth - othickness, owidth - othickness);
        g.setColor(color);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int gap = 40;

        g.setColor(Color.BLACK);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        g.drawString("State of the generation", TTTM.xstart - 170, TTTM.ystart - 30);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        for (int i = 0; i < 9; i++) {
            if (i == 0) {
                g.drawString("Generating of definitive branches", TTTM.xstart - 170, TTTM.ystart + 20);
            } else {
                g.drawString("Generating of " + (9 - i) + " level branches", TTTM.xstart - 170, TTTM.ystart + 20 + gap * i);
            }
            if (stage == i) {
                if (i % 2 == 0) {
                    drawX(g, TTTM.ystart + 12 + gap * i);
                } else {
                    drawO(g, TTTM.ystart + 12 + gap * i);
                }
            }
        }

        gap = 50;
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        g.drawString(("Generated combination: " + comb), TTTM.xstart + 175, TTTM.ystart + 20);
        g.drawString(("Generated branch: " + branches), TTTM.xstart + 175, TTTM.ystart + 20 + gap);

        int decCount = 0;
        if (!start) {
            decCount = TTTM.getDecCount();
        } else {
            decCount = sends * TTTM.batchsize + decisions.size();
        }

        g.drawString(("Database size: " + Integer.toString(decCount)) + "/" + TTTM.maxdec + " rows",
                TTTM.xstart + 175, TTTM.ystart + 20 + gap * 2);
        g.drawString(("Probability of X win: " + xwin + "%"), TTTM.xstart + 175, TTTM.ystart + 20 + gap * 3);
        g.drawString(("Probability of O win: " + owin + "%"), TTTM.xstart + 175, TTTM.ystart + 20 + gap * 4);
        g.drawString(("Probability of draw: " + draw + "%"), TTTM.xstart + 175, TTTM.ystart + 20 + gap * 5);

        if (failed) {
            g.setColor(Color.RED);
            g.drawString("Decision generating cancelled", TTTM.xstart + 175, TTTM.ystart + 20 + gap * 6);
        }

        if (done) {
            g.setColor(Color.GREEN);
            g.drawString("Decision generating successful", TTTM.xstart + 175, TTTM.ystart + 20 + gap * 6);
        }

        if (generating) {
            g.setColor(Color.RED);
            g.drawString("Generating of definitive branches ongoing", TTTM.xstart + 175, TTTM.ystart + 10 + gap * 6);
            g.drawString("Program will not redraw", TTTM.xstart + 175, TTTM.ystart - 10 + gap * 7);
            g.drawString("Check the functionality in phpmyadmin", TTTM.xstart + 175, TTTM.ystart - 30 + gap * 8);
        }

    }

    void sendDecisions() {
        if(decisions.size() > 0){
            String SQL = "INSERT IGNORE INTO decisions VALUES ";

            for (String[] decision : decisions) {
                SQL += "(\"" + decision[0] + "\", " + decision[1] + ", "
                        + decision[2] + ", " + decision[3] + "),";
            }

            SQL = SQL.substring(0, SQL.length() - 1);
            try {
                TTTM.insert.executeUpdate(SQL);
            } catch (SQLException e) {
                System.err.println(e);
            }

            sends++;
            decisions.clear();
        }
    }

    void insert(String combination) {
        decisions.add(new String[]{
            combination,
            TTTM.formatter.format(xwin),
            TTTM.formatter.format(owin),
            TTTM.formatter.format(draw)
        });

        if (decisions.size() >= TTTM.batchsize) {
            sendDecisions();
        }
    }

    void getDefinitiveBranches() {
        try {
            TTTM.rs = TTTM.stmt.executeQuery("SELECT * FROM combinations");
            while (TTTM.rs.next()) {
                String combination = TTTM.rs.getString("combination");
                char[] chars = combination.toCharArray();
                if (chars.length % 2 != 0) {
                    xwin = 100;
                    owin = 0;
                    draw = 0;
                    insert(combination);
                } else if (chars.length % 2 == 0 && chars.length != 10) {
                    xwin = 0;
                    owin = 100;
                    draw = 0;
                    insert(combination);
                } else if (chars.length == 10) {
                    xwin = 0;
                    owin = 0;
                    draw = 100;
                    insert(TTTM.cutString(combination, 9));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DecGenPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {

        if (start) {
            if (stage == 0) {
                if (generating && delay == 100) {
                    getDefinitiveBranches();
                    sendDecisions();
                    generating = false;
                }
                if (TTTM.getDecCount() == TTTM.maxcomb) {
                    stage++;
                }
            }

            if (stage > 0 && stage < 9) {
                for (int iter = 0; iter < TTTM.batchsize; iter++) {
                    TTTM.sortBranches();

                    if (TTTM.branch[0] < 9) {

                        for (int i = 0; i < 9 - stage; i++) {
                            TTTM.branchToOutput(TTTM.branch[i], i);
                        }
                        combToString();
                        String s = TTTM.cutString(comb, 9 - stage);
                        if (TTTM.doesExist(s)) {
                            try {
                                TTTM.rs = TTTM.stmt.executeQuery("SELECT AVG(xwin) "
                                        + "FROM decisions "
                                        + "WHERE combination LIKE \"" + s + "_\"");
                                TTTM.rs.next();
                                xwin = TTTM.rs.getDouble(1);

                                TTTM.rs = TTTM.stmt.executeQuery("SELECT AVG(owin) "
                                        + "FROM decisions "
                                        + "WHERE combination LIKE \"" + s + "_\"");
                                TTTM.rs.next();
                                owin = TTTM.rs.getDouble(1);

                                TTTM.rs = TTTM.stmt.executeQuery("SELECT AVG(draw) "
                                        + "FROM decisions "
                                        + "WHERE combination LIKE \"" + s + "_\"");
                                TTTM.rs.next();
                                draw = TTTM.rs.getDouble(1);

                                insert(s);
                            } catch (SQLException ex) {
                                Logger.getLogger(DecGenPanel.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        Arrays.fill(TTTM.occupancy, -1);
                        TTTM.branch[8 - stage]++;
                    }

                    if (TTTM.branch[0] == 9) {
                        Arrays.fill(TTTM.branch, 0);
                        stage++;
                        sendDecisions();
                    }
                    
                    if(stage == 9){
                        break;
                    }
                }
            }

            if (stage == 9) {
                done = true;
                StartStopButton.setText("START");
                BackButton.setEnabled(true);
                start = false;
            }

            if (delay < 100) {
                delay++;
            }
        }

        repaint();
    }

    void exitToMenu() {
        start = false;
        TTTM.checkDatabases();
        TTTM.gm.setVisible(true);
        TTTM.dgf.setVisible(false);
        TTTM.dgf = null;
    }

    void combToString() {
        comb = "";
        branches = "";
        for (int i = 0; i < 9; i++) {
            comb += Integer.toString(TTTM.combination[i]);
            branches += Integer.toString(TTTM.branch[i]);
        }
    }

}
