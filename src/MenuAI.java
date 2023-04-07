package tictactoemaster;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
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


File: MenuAI.java
Menu regarding the AI after connecting to the database

*/


class GenMenu extends JFrame {

    GenMenuPanel panel = new GenMenuPanel();

    GenMenu() {
        TTTM.setFrame(this, panel, "Unbeatable AI - menu");
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                panel.exitToMenu();
            }
        });
    }

}

class GenMenuPanel extends JPanel {

    JButton PlayButton = new JButton();
    JButton AIvAIButton = new JButton();
    JButton GenCombButton = new JButton();
    JButton GenDecButton = new JButton();
    JButton DBVButton = new JButton();
    JButton BackButton = new JButton();
    JButton FeedbackButton = new JButton();
    JButton TacticsButton = new JButton();
    JButton SeenButton = new JButton();

    int unreadMessages = 0;

    GenMenuPanel() {

        this.add(PlayButton);
        TTTM.setButton(PlayButton, 310, 150, 120, 75, "Play", 18);

        this.add(AIvAIButton);
        TTTM.setButton(AIvAIButton, 440, 150, 120, 75, "AI vs AI", 18);

        this.add(GenCombButton);
        TTTM.setButton(GenCombButton, 310, 250, 120, 75,
                ("<html><p align=\"center\">Generating<br>combinations</p></html>"), 14);

        this.add(GenDecButton);
        TTTM.setButton(GenDecButton, 440, 250, 120, 75,
                ("<html><p align=\"center\">Generating<br>decisions</p></html>"), 14);

        this.add(DBVButton);
        TTTM.setButton(DBVButton, 310, 350, 250, 40, "Database visualization", 18);

        this.add(BackButton);
        TTTM.setButton(BackButton, 360, 415, 150, 40, "Back", 12);

        this.add(FeedbackButton);
        TTTM.setButton(FeedbackButton, 20, 520, 150, 30, "Feedback", 12);

        this.add(TacticsButton);
        TTTM.setButton(TacticsButton, 720, 520, 150, 30, "Tactics", 12);

        this.add(SeenButton);
        TTTM.setButton(SeenButton, 20, 480, 150, 30, "Seen", 12);
        SeenButton.setVisible(false);

        PlayButton.addActionListener((ae) -> {
            TTTM.asf = new AISelectFrame();
            TTTM.asf.setVisible(true);
            TTTM.gm.setVisible(false);
        });

        AIvAIButton.addActionListener((ae) -> {
            TTTM.AIvAI = new AIvsAIframe();
            TTTM.AIvAI.setVisible(true);
            TTTM.gm.setVisible(false);
        });

        GenCombButton.addActionListener((ae) -> {
            TTTM.cgf = new ComGenFrame();
            TTTM.cgf.setVisible(true);
            TTTM.gm.setVisible(false);
        });

        GenDecButton.addActionListener((ae) -> {
            TTTM.dgf = new DecGenFrame();
            TTTM.dgf.setVisible(true);
            TTTM.gm.setVisible(false);
        });

        DBVButton.addActionListener((ae) -> {
            TTTM.DBP = new DBPreviewFrame();
            TTTM.DBP.setVisible(true);
            TTTM.gm.setVisible(false);
        });

        BackButton.addActionListener((ae) -> {
            exitToMenu();
        });

        FeedbackButton.addActionListener((ae) -> {
            TTTM.FF = new FeedbackFrame();
            TTTM.FF.setVisible(true);
            TTTM.gm.setVisible(false);
        });
        
        TacticsButton.addActionListener((ae) -> {
            try {
                Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
                if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(new URI("https://www.wikihow.com/Win-at-Tic-Tac-Toe"));
                }
            } catch (URISyntaxException | IOException ex) {
                Logger.getLogger(GenMenuPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        SeenButton.addActionListener((ae) -> {
            try {
                TTTM.stmt.executeUpdate("UPDATE feedback "
                        + "SET beenRead=TRUE "
                        + "WHERE beenRead=FALSE");
            } catch (SQLException ex) {
                Logger.getLogger(GenMenuPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            unreadMessages = 0;
            SeenButton.setVisible(false);
            repaint();
        });

        if (TTTM.hasAllPrivileges) {
            int errors, important, reply;
            boolean warning = false;
            String message = "";
            try {
                TTTM.rs = TTTM.stmt.executeQuery("SELECT COUNT(*) FROM feedback "
                        + "WHERE beenRead=FALSE");
                TTTM.rs.next();
                unreadMessages = TTTM.rs.getInt(1);
                if (unreadMessages > 0) {
                    SeenButton.setVisible(true);
                }
                TTTM.rs = TTTM.stmt.executeQuery("SELECT COUNT(*) FROM mistakes "
                        + "WHERE fixed=FALSE");
                TTTM.rs.next();
                errors = TTTM.rs.getInt(1);
                TTTM.rs = TTTM.stmt.executeQuery("SELECT COUNT(*) FROM feedback "
                        + "WHERE important=TRUE AND beenRead=false");
                TTTM.rs.next();
                important = TTTM.rs.getInt(1);

                if (errors != 0) {
                    message += ("There are new errors in table 'mistakes'\n"
                            + "Number of unfixed errors: " + errors + "\n");
                    warning = true;
                }
                if (important != 0) {
                    message += ("There are important messages in 'feedback'\n"
                            + "Number of unread important messages: " + important);
                    warning = true;
                }

                if (warning) {
                    Object[] options = {"Go to phpmyadmin", "Mark as solved", "Cancel"};
                    reply = JOptionPane.showOptionDialog(TTTM.dbcf, message,
                            "Administrator notification", JOptionPane.DEFAULT_OPTION,
                            JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                    switch (reply) {
                        case 0:
                            openPHPMyAdmin();
                            break;
                        case 1:
                            TTTM.stmt.executeUpdate("UPDATE mistakes "
                                    + "SET fixed=TRUE WHERE fixed=FALSE");
                            TTTM.stmt.executeUpdate("UPDATE feedback "
                                    + "SET beenRead=TRUE WHERE important=TRUE");
                            break;
                        default:
                            break;
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(GenMenuPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 48));
        g.setColor(Color.BLACK);
        g.drawString("Unbeatable AI", 245, 120);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        if (TTTM.hasAllPrivileges && unreadMessages > 0) {
            g.drawString(("Unread messages: " + unreadMessages), 20, 465);
        }

        g.setColor(Color.RED);
        if (!TTTM.hasPrivileges) {
            g.drawString("You don't have MySQL privileges for generating", 262, 550);
        }
        if (!TTTM.combDBcomplete || !TTTM.decDBcomplete) {
            g.drawString("Databases are incomplete, corrupted or non-existent", 230, 20);
        }
    }

    void exitToMenu() {
        TTTM.mf.setVisible(true);
        try {
            TTTM.MySQL.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        PlayButton.setEnabled(true);
        GenCombButton.setEnabled(true);
        GenDecButton.setEnabled(true);
        TTTM.hasPrivileges = true;
        TTTM.combDBcomplete = true;
        TTTM.decDBcomplete = true;
        TTTM.gm.setVisible(false);
        TTTM.hasAllPrivileges = false;
        TTTM.reset();
        TTTM.gm = null;
    }

    void openPHPMyAdmin() {
        try {
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(new URI("http://" + TTTM.IP + "/phpmyadmin/"));
            }
        } catch (URISyntaxException | IOException ex) {
            Logger.getLogger(GenMenuPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}