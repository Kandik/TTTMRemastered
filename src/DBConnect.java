package tictactoemaster;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
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


File: DBConnect.java
Panel for connecting to the database

*/


class DBConnectFrame extends JFrame {

    DBConnectPanel panel = new DBConnectPanel();

    DBConnectFrame() {
        TTTM.setFrame(this, panel, "Connect to the database");
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                panel.exitToMenu();
            }
        });

    }

}

class DBConnectPanel extends JPanel {

    JTextField IPtf = new JTextField(), porttf = new JTextField(),
            DBnametf = new JTextField(), usernametf = new JTextField();
    JPasswordField passwordtf = new JPasswordField();
    JButton connect = new JButton(), back = new JButton(), next = new JButton(),
            defaultb = new JButton();
    JToggleButton showpassword = new JToggleButton();

    int xstart = 245, ystart = 175, gap = 70, tries = 0;
    boolean successful = false;

    DBConnectPanel() {

        this.add(IPtf);
        IPtf.setBounds(xstart + 30, ystart, 200, 30);

        this.add(porttf);
        porttf.setBounds(xstart + 290, ystart, 70, 30);

        this.add(DBnametf);
        DBnametf.setBounds(xstart + 160, ystart + gap, 200, 30);

        this.add(usernametf);
        usernametf.setBounds(xstart + 180, ystart + gap * 2, 180, 30);

        this.add(passwordtf);
        passwordtf.setBounds(xstart + 120, ystart + gap * 3, 150, 30);

        this.add(showpassword);
        showpassword.setBounds(xstart + 280, ystart + gap * 3, 80, 30);
        showpassword.setText("Show");
        showpassword.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));

        this.add(defaultb);
        TTTM.setButton(defaultb, xstart + 370, ystart, 80, 30, "Default", 12);

        this.add(back);
        TTTM.setButton(back, 250, 520, 100, 30, "Back", 12);

        this.add(connect);
        TTTM.setButton(connect, 375, 520, 100, 30, "Connect", 12);

        this.add(next);
        TTTM.setButton(next, 500, 520, 100, 30, "Next", 12);
        next.setEnabled(false);

        defaultb.addActionListener((ae) -> {
            IPtf.setText("localhost");
            porttf.setText("3306");
            DBnametf.setText("tttm");
            usernametf.setText("root");
        });

        showpassword.addActionListener((ae) -> {
            if (showpassword.isSelected()) {
                passwordtf.setEchoChar((char) 0);
            } else {
                passwordtf.setEchoChar('\u2022');
            }
        });

        back.addActionListener((ae) -> {
            exitToMenu();
        });

        connect.addActionListener((ae) -> {
            connect();
        });

        next.addActionListener((ae) -> {
            TTTM.gm = new GenMenu();
            if (!TTTM.hasPrivileges) {
                TTTM.gm.panel.GenCombButton.setEnabled(false);
                TTTM.gm.panel.GenDecButton.setEnabled(false);
            }
            TTTM.checkDatabases();
            TTTM.gm.setVisible(true);
            TTTM.gm.panel.repaint();
            TTTM.dbcf.setVisible(false);
        });

        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));
        g.drawString("Database connection", xstart + 10, ystart - gap);

        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        g.drawString("IP:", xstart, ystart + 20);
        g.drawString("Port:", xstart + 240, ystart + 20);
        g.drawString("Database name:", xstart, ystart + 20 + gap);
        g.drawString("MySQL username:", xstart, ystart + gap * 2 + 20);
        g.drawString("Password:", xstart, ystart + gap * 3 + 20);

        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        g.drawString("Default for MySQL: 3306", xstart + 258, ystart + 40);

        g.setColor(Color.RED);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        if (!successful && tries > 0) {
            g.drawString("Connection to the database failed", 290, 490);
        }
        if (successful) {
            g.setColor(Color.GREEN);
            g.drawString("Connection successful", 310, 490);
        }
    }

    void connect() {
        TTTM.IP = IPtf.getText();
        TTTM.port = porttf.getText();
        TTTM.databasename = DBnametf.getText();
        TTTM.username = usernametf.getText();
        TTTM.password = passwordtf.getText();

        try {
            TTTM.connect();
            if (!TTTM.MySQL.isClosed()) {
                successful = true;
                next.setEnabled(true);
                TTTM.stmt = TTTM.MySQL.createStatement();
                TTTM.rs = TTTM.stmt.executeQuery("SHOW GRANTS");
                TTTM.rs.next();

                for (int i = 0; i < TTTM.requiredPrivileges.length; i++) {
                    if (!TTTM.rs.getString(1).contains(TTTM.requiredPrivileges[i])) {
                        TTTM.hasPrivileges = false;
                    }
                }
                if (TTTM.rs.getString(1).contains("ALL PRIVILEGES")) {
                    TTTM.hasPrivileges = true;
                    TTTM.hasAllPrivileges = true;
                }

                if (TTTM.hasPrivileges) {
                    TTTM.stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "
                            + "combinations(combination varchar(10) PRIMARY KEY)");
                    TTTM.stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "
                            + "decisions(combination VARCHAR(9) PRIMARY KEY,"
                            + "xwin DOUBLE(6,3) NOT NULL, "
                            + "owin DOUBLE(6,3) NOT NULL, "
                            + "draw DOUBLE(6,3) NOT NULL)");
                    TTTM.stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "
                            + "mistakes(mistake VARCHAR(9) NOT NULL, "
                            + "AIPlayer TINYINT(1) NOT NULL, "
                            + "happenedAt DATETIME NOT NULL, "
                            + "publicIP VARCHAR(15), "
                            + "localIP VARCHAR(15), "
                            + "fixed BOOLEAN NOT NULL)");
                    TTTM.stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "
                            + "exceptions(combination varchar(9) PRIMARY KEY, "
                            + "exchange varchar(9) NOT NULL)");
                    TTTM.stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "
                            + "testers(nickname VARCHAR(30) PRIMARY KEY, "
                            + "publicIP VARCHAR(15) NOT NULL, "
                            + "localIP VARCHAR(15) NOT NULL)");
                    TTTM.stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "
                            + "feedback(nickname VARCHAR(30) NOT NULL, "
                            + "email VARCHAR(30) NOT NULL, "
                            + "text VARCHAR(200) PRIMARY KEY,"
                            + "important BOOLEAN NOT NULL, "
                            + "happenedAt DATETIME NOT NULL, "
                            + "beenRead BOOLEAN NOT NULL)");
                    TTTM.rs = TTTM.stmt.executeQuery("SELECT COUNT(*) FROM exceptions");
                    TTTM.rs.next();
                    if (TTTM.rs.getInt(1) < TTTM.exchange.length) {
                        for (int i = 0; i < TTTM.exchange.length; i++) {
                            TTTM.insert.executeUpdate("INSERT INTO exceptions "
                                    + "VALUES(\"" + TTTM.exchange[i] + "\", "
                                            + "\"" + TTTM.to[i] + "\")");
                        }
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            successful = false;
            tries++;
            next.setEnabled(false);
            System.err.println(ex);
        } catch (SQLException ex) {
            Logger.getLogger(DBConnectPanel.class.getName()).log(Level.SEVERE, null, ex);
            successful = false;
            tries++;
            next.setEnabled(false);
        }
        

        repaint();
    }

    void exitToMenu() {
        reset();
        TTTM.mf.setVisible(true);
        TTTM.dbcf.setVisible(false);
        TTTM.dbcf = null;
    }

    void reset() {
        IPtf.setText("");
        porttf.setText("");
        DBnametf.setText("");
        usernametf.setText("");
        passwordtf.setText("");
        next.setEnabled(false);
        showpassword.setSelected(false);
    }

}
