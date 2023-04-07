package tictactoemaster;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
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


File: Feedback.java
Panel for providing user feedback and sending it to database

*/


class FeedbackFrame extends JFrame {

    FeedbackPanel panel = new FeedbackPanel();

    FeedbackFrame() {
        TTTM.setFrame(this, panel, "Feedback");
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                panel.exitToMenu();
            }
        });
    }
}

class FeedbackPanel extends JPanel {

    JTextField name = new JTextField(), email = new JTextField();
    JTextArea text = new JTextArea();
    JToggleButton important = new JToggleButton();
    JButton send = new JButton(), menu = new JButton();

    boolean failed = false, spam = false, exists = false, successful = false, emailbad = false;

    int antispam = 0, delay = 30;

    SimpleDateFormat parser = new SimpleDateFormat("ss");

    FeedbackPanel() {

        this.add(important);
        important.setBounds(250, 520, 100, 30);
        important.setText("Important");
        important.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));

        this.add(send);
        TTTM.setButton(send, 375, 520, 100, 30, "Send", 12);

        this.add(menu);
        TTTM.setButton(menu, 500, 520, 100, 30, "Menu", 12);

        this.add(name);
        name.setBounds(175, 80, 200, 30);

        this.add(email);
        email.setBounds(630, 80, 200, 30);

        this.add(text);
        text.setBounds(50, 170, 780, 200);
        text.setBorder(BorderFactory.createLineBorder(Color.black));

        try {
            TTTM.rs = TTTM.stmt.executeQuery("SELECT COUNT(*) FROM testers "
                    + "WHERE publicIP=\"" + TTTM.getPublicIP() + "\" "
                    + "AND localIP=\"" + TTTM.getLocalIP() + "\"");
            TTTM.rs.next();
            if (TTTM.rs.getInt(1) != 0) {
                TTTM.rs = TTTM.stmt.executeQuery("SELECT nickname FROM testers "
                        + "WHERE publicIP=\"" + TTTM.getPublicIP() + "\" "
                        + "AND localIP=\"" + TTTM.getLocalIP() + "\"");
                TTTM.rs.next();
                name.setText(TTTM.rs.getString("nickname"));
                name.setEditable(false);
            }
        } catch (SQLException ex) {
            Logger.getLogger(FeedbackPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        send.addActionListener((ae) -> {
            failed = false;
            spam = false;
            exists = false;
            successful = false;
            emailbad = false;

            if (isValid(email.getText()) || email.getText().equals("")) {

                if (name.getText().length() > 1 && text.getText().length() > 1) {
                    try {
                        TTTM.rs = TTTM.stmt.executeQuery("SELECT COUNT(*) FROM feedback "
                                + "WHERE text=\"" + text.getText() + "\"");
                        TTTM.rs.next();
                        if (TTTM.rs.getInt(1) != 0) {
                            exists = true;
                        } else {
                            TTTM.rs = TTTM.stmt.executeQuery("SELECT COUNT(*) FROM feedback "
                                    + "WHERE nickname=\"" + name.getText() + "\" "
                                    + "AND TIMEDIFF(happenedAt, NOW())>-" + Integer.toString(delay));
                            TTTM.rs.next();
                            if (TTTM.rs.getInt(1) != 0) {
                                spam = true;
                                TTTM.rs = TTTM.stmt.executeQuery("SELECT (TIMEDIFF(happenedAt, NOW())*-1) "
                                        + "FROM feedback WHERE nickname=\"" + name.getText() + "\" "
                                        + "AND TIMEDIFF(happenedAt, NOW())>-" + Integer.toString(delay));
                                TTTM.rs.next();
                                antispam = delay - TTTM.rs.getInt(1);
                            } else {
                                TTTM.stmt.executeUpdate("INSERT INTO feedback "
                                        + "VALUES(\"" + name.getText() + "\", "
                                        + "\"" + email.getText() + "\", "
                                        + "\"" + text.getText() + "\", "
                                        + important.isSelected() + ", "
                                        + "NOW(), FALSE)");
                                successful = true;
                                name.setEditable(false);
                            }
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(FeedbackPanel.class.getName()).log(Level.SEVERE, null, ex);
                        failed = true;
                    }
                } else {
                    failed = true;
                }
            } else {
                emailbad = true;
            }
            repaint();
        });

        menu.addActionListener((ae) -> {
            exitToMenu();
        });

        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));
        g.drawString("Feedback", 365, 50);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        g.drawString("Name:", 50, 100);
        g.drawString("Email:", 560, 100);
        g.drawString("Message:", 50, 150);

        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        if (!failed && !exists && !spam && !successful && !emailbad) {
            g.setColor(Color.RED);
            g.drawString("Feedback through this form is sent to the MySQL database.", 255, 430);
            g.drawString("If you are using localhost database, submit feedback to s.kando@azet.sk instead.", 170, 450);
            
            g.setColor(Color.BLACK);
            g.drawString("Please mark all bugs as \"Important\"", 310, 490);
            g.drawString("Winnings over AI are sent to the database automatically", 255, 510);
        }

        g.setColor(Color.GREEN);
        if (successful) {
            g.drawString("Message was sent successfully", 330, 510);
        }

        g.setColor(Color.RED);
        if (failed) {
            g.drawString("Message sending unsuccessful", 330, 510);
        } else if (exists) {
            g.drawString("Message was sent already", 350, 510);
        } else if (spam) {
            g.drawString("Your message was blocked by an antispam", 310, 490);
            g.drawString("Try again in " + antispam + " seconds", 325, 510);
        } else if (emailbad) {
            g.drawString("Invalid email", 370, 490);
            g.drawString("If you do not want to share your email, "
                    + "leave the textfield empty", 220, 510);
        }
    }

    public static boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."
                + "[a-zA-Z0-9_+&*-]+)*@"
                + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
                + "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null) {
            return false;
        }
        return pat.matcher(email).matches();
    }

    void exitToMenu() {
        TTTM.gm.setVisible(true);
        TTTM.FF.setVisible(false);
        name.setEditable(true);
        failed = false;
        spam = false;
        exists = false;
        successful = false;
        TTTM.FF = null;
    }
}