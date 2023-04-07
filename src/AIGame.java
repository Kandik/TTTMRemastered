package tictactoemaster;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
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


File: AIGame.java
Main panel for playing against the AI

*/


class AIGameFrame extends JFrame {

    AIGamePanel panel = new AIGamePanel();

    AIGameFrame() {
        TTTM.setFrame(this, panel, "Hra s AI");
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                panel.exitToMenu();
            }
        });
    }
}

class AIGamePanel extends JPanel implements ActionListener, KeyListener {

    boolean accept = false, failed = false, logged = false;

    int player = 0, AIPlayer = 1;

    Timer t = new Timer(100, this);

    String AI1 = "xwin", AI2 = "owin", name = "";

    JButton register = new JButton(), 
            change = new JButton(), 
            unregister = new JButton();

    AIGamePanel() {
        this.add(register);
        TTTM.setButton(register, 20, 520, 
                180, 30, "Register as a tester", 12);

        this.add(change);
        TTTM.setButton(change, 20, 515, 
                90, 25, "Change", 11);

        this.add(unregister);
        TTTM.setButton(unregister, 120, 515, 
                90, 25, "Cancel", 11);

        register.setFocusable(false);
        change.setFocusable(false);
        unregister.setFocusable(false);

        try {
            TTTM.rs = TTTM.stmt.executeQuery("SELECT COUNT(*) FROM testers "
                    + "WHERE publicIP=\"" + TTTM.getPublicIP() + "\" "
                    + "AND localIP=\"" + TTTM.getLocalIP() + "\"");
            TTTM.rs.next();
            if (TTTM.rs.getInt(1) == 0) {
                Object[] options = {"Yes", "No"};
                int reply = JOptionPane.showOptionDialog(
                        TTTM.asf, 
                        ("I agree that in case of beating the AI, my local "
                        + "and public IP\n"
                        + "will be sent to the database to the administrators "
                        + "(used for identification purposes)\n"
                        + "(If you do not want to receive this message "
                        + "next time, register as a tester)"),
                        "User agreement", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE, null, 
                        options, options[0]);
                switch (reply) {
                    case 0:
                        accept = true;
                        break;
                    case 1:
                        accept = false;
                        break;
                    default:
                        accept = false;
                        break;
                }
                logged = false;
                change.setVisible(false);
                unregister.setVisible(false);
                register.setVisible(true);
            } else {
                TTTM.rs = TTTM.stmt.executeQuery("SELECT nickname FROM testers "
                        + "WHERE publicIP=\"" + TTTM.getPublicIP() + "\" "
                        + "AND localIP=\"" + TTTM.getLocalIP() + "\"");
                TTTM.rs.next();
                name = TTTM.rs.getString("nickname");
                accept = true;
                logged = true;
                change.setVisible(true);
                unregister.setVisible(true);
                register.setVisible(false);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AIGamePanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        register.addActionListener((al) -> {
            String name = JOptionPane.showInputDialog(TTTM.AI, 
                    "Enter your nickname:\n"
                    + "By registering you agree with storing your\n"
                    + "local and public IP in the database",
                    "Tester registration", JOptionPane.QUESTION_MESSAGE);
            if (name.length() > 1) {
                try {
                    TTTM.stmt.executeUpdate("INSERT INTO testers "
                            + "VALUES (\"" + name + "\", "
                            + "\"" + TTTM.getPublicIP() + "\", "
                            + "\"" + TTTM.getLocalIP() + "\")");
                    register.setVisible(false);
                    change.setVisible(true);
                    unregister.setVisible(true);
                    this.name = name;
                    logged = true;
                    failed = false;
                    accept = true;
                } catch (SQLException ex) {
                    Logger.getLogger(AIGamePanel.class.getName()).log(Level.SEVERE, null, ex);
                    failed = true;
                    logged = false;
                }
            } else {
                failed = true;
            }
        });

        change.addActionListener((al) -> {
            String name = JOptionPane.showInputDialog(TTTM.AI, 
                    "Enter new nickname:",
                    "Nickname change", JOptionPane.QUESTION_MESSAGE);
            if (name.length() > 1) {
                try {
                    TTTM.stmt.executeUpdate("UPDATE testers "
                            + "SET nickname=\"" + name + "\" "
                            + "WHERE publicIP=\"" + TTTM.getPublicIP() + "\" "
                            + "AND localIP=\"" + TTTM.getLocalIP() + "\"");
                    this.name = name;
                    failed = false;
                    accept = true;
                } catch (SQLException ex) {
                    Logger.getLogger(AIGamePanel.class.getName()).log(Level.SEVERE, null, ex);
                    failed = true;
                    logged = false;
                }
            } else {
                failed = true;
            }
        });

        unregister.addActionListener((al) -> {
            try {
                TTTM.stmt.executeUpdate("DELETE FROM testers "
                        + "WHERE publicIP=\"" + TTTM.getPublicIP() + "\" "
                        + "AND localIP=\"" + TTTM.getLocalIP() + "\"");
                register.setVisible(true);
                change.setVisible(false);
                unregister.setVisible(false);
                logged = false;
                name = null;
                accept = false;
            } catch (SQLException ex) {
                Logger.getLogger(AIGamePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        addKeyListener(this);
        TTTM.addButtons(this);
        t.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        TTTM.drawField(g);

        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        if (accept) {
            g.drawString("If you beat the AI,", 700, 510);
            g.drawString("write to s.kando@azet.sk", 700, 530);
            g.drawString(("for getting the bounty (" + TTTM.bounty + "€)"), 700, 550);
        }
        if (logged) {
            g.drawString(("Logged in as " + name), 20, 555);
        }
        g.setColor(Color.RED);
        if (!accept) {
            g.drawString("Without sharing your IP", 700, 535);
            g.drawString("you are not eligible for a bounty", 700, 555);
        }
        if (failed) {
            g.drawString("Account creation or nickname change failed", 20, 490);
            g.drawString("Nickname may be occupied or too short", 20, 510);
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {

        switch (player) {
            case 0:
                AI1 = "xwin";
                AI2 = "owin";
                AIPlayer = 1;
                break;
            case 1:
                AI1 = "owin";
                AI2 = "xwin";
                AIPlayer = 0;
                break;
        }

        repaint();

        TTTM.checkInput();
        TTTM.winCheck();

        String comb = "";
        for (int i = 0; i < TTTM.turn; i++) {
            comb += Integer.toString(TTTM.combination[i]);
        }

        if (TTTM.winner == -1) {
            if (TTTM.turn % 2 == player) {
                setButtons(true);
            } else {
                setButtons(false);
                try {
                    TTTM.rs = TTTM.stmt.executeQuery("SELECT COUNT(combination) "
                            + "FROM decisions "
                            + "WHERE combination LIKE \"" + comb + "_\" "
                            + "AND " + AI2 + "=100");
                    TTTM.rs.next();

                    if (TTTM.rs.getInt(1) != 0) {
                        TTTM.rs = TTTM.stmt.executeQuery("SELECT combination "
                                + "FROM decisions "
                                + "WHERE combination LIKE \"" + comb + "_\" "
                                + "AND " + AI2 + "=100");
                        TTTM.rs.next();
                    } else {
                        TTTM.rs = TTTM.stmt.executeQuery("SELECT combination "
                                + "FROM decisions "
                                + "WHERE combination "
                                + "LIKE \"" + comb + "_\" "
                                + "ORDER BY " + AI1 + " ASC");
                        TTTM.rs.next();
                    }
                    String branch = TTTM.rs.getString("combination");
                    TTTM.rs = TTTM.stmt.executeQuery("SELECT COUNT(*) "
                            + "FROM exceptions "
                            + "WHERE combination=\"" + branch + "\"");
                    TTTM.rs.next();
                    if (TTTM.rs.getInt(1) != 0) {
                        TTTM.rs = TTTM.insert.executeQuery("SELECT * "
                                + "FROM exceptions "
                                + "WHERE combination=\"" + branch + "\"");
                        TTTM.rs.next();
                        branch = TTTM.rs.getString("exchange");
                    }
                    char[] dec = branch.toCharArray();
                    TTTM.input = Character.getNumericValue(dec[dec.length - 1]);
                    TTTM.checkInput();
                } catch (SQLException ex) {
                    Logger.getLogger(AIGamePanel.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }

        if (TTTM.winner == player) {
            try {
                TTTM.rs = TTTM.stmt.executeQuery("SELECT COUNT(mistake) "
                        + "FROM mistakes WHERE mistake=\"" + comb + "\"");
                TTTM.rs.next();
                if (TTTM.rs.getInt(1) == 0) {
                    if (accept) {
                        TTTM.stmt.executeUpdate("INSERT INTO mistakes "
                                + "VALUES(\"" + comb + "\", " + AIPlayer + ", "
                                + "NOW(), \"" + TTTM.getPublicIP() + "\", "
                                + "\"" + TTTM.getLocalIP() + "\", FALSE)");
                    } else {
                        TTTM.stmt.executeUpdate("INSERT INTO mistakes "
                                + "VALUES(\"" + comb + "\", " + AIPlayer + ", "
                                + "NOW(), null, null, FALSE)");
                    }

                }
            } catch (SQLException ex) {
                Logger.getLogger(AIGamePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (TTTM.AI.isVisible()) {
            TTTM.returnToMenu.addActionListener((al) -> {
                exitToMenu();
            });
        }
    }

    void setButtons(boolean on) {
        if (on) {
            for (int i = 0; i < 9; i++) {
                if (TTTM.occupancy[i] == -1) {
                    TTTM.buttons[i].setEnabled(true);
                    TTTM.buttons[i].setFocusable(false);
                } else {
                    TTTM.buttons[i].setEnabled(false);
                }
            }
        }
        if (!on) {
            for (int i = 0; i < 9; i++) {
                TTTM.buttons[i].setEnabled(false);
            }
        }

    }

    void exitToMenu() {
        TTTM.reset();
        TTTM.gm.setVisible(true);
        TTTM.AI.setVisible(false);
        TTTM.AI = null;
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (TTTM.turn % 2 == player || TTTM.winner != -1) {
            TTTM.setKeys(ke);
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }
}