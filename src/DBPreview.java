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


File: DBPreview.java
Panel for visualisating the database (what the AI sees when it's deciding)

*/


class DBPreviewFrame extends JFrame {

    DBPreviewPanel panel = new DBPreviewPanel();

    DBPreviewFrame() {
        TTTM.setFrame(this, panel, "Database visualisation");
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                panel.exitToMenu();
            }
        });
    }
}

class DBPreviewPanel extends JPanel implements ActionListener, KeyListener {

    double[] xwin = new double[10], owin = new double[10], draw = new double[10];
    double[][] minmax = new double[3][2];

    JButton phpmyadmin = new JButton();

    Timer t = new Timer(100, this);

    DBPreviewPanel() {
        addKeyListener(this);
        TTTM.addButtons(this);

        this.add(phpmyadmin);
        TTTM.setButton(phpmyadmin, 20, 520, 150, 30, "phpmyadmin", 12);
        phpmyadmin.setFocusable(false);

        phpmyadmin.addActionListener((ae) -> {
            TTTM.gm.panel.openPHPMyAdmin();
        });

        t.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        TTTM.drawField(g);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        g.setColor(new Color(120, 0, 0));
        g.drawString("Min xwin", 30, TTTM.id[4][1] - 25);
        g.setColor(new Color(0, 0, 120));
        g.drawString("Min owin", 30, TTTM.id[4][1]);
        g.setColor(new Color(150, 75, 0));
        g.drawString("Min draw", 30, TTTM.id[4][1] + 25);
        g.setColor(Color.RED);
        g.drawString("Max xwin", 130, TTTM.id[4][1] - 25);
        g.setColor(new Color(0, 0, 255));
        g.drawString("Max owin", 130, TTTM.id[4][1]);
        g.setColor(new Color(255, 128, 0));
        g.drawString("Max draw", 130, TTTM.id[4][1] + 25);
        g.setColor(Color.BLACK);
        if (TTTM.winner == -1) {
            g.drawString("Current % of win X: " + TTTM.formatter.format(xwin[9]) + "%", 610, TTTM.id[4][1] - 25);
            g.drawString("Current % of win O: " + TTTM.formatter.format(owin[9]) + "%", 610, TTTM.id[4][1]);
            g.drawString("Current % of draw: " + TTTM.formatter.format(draw[9]) + "%", 610, TTTM.id[4][1] + 25);
        }

        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        for (int i = 0; i < 9; i++) {
            if (TTTM.occupancy[i] == -1) {
                if (xwin[i] != minmax[0][0] && xwin[i] != minmax[0][1]) {
                    g.setColor(Color.BLACK);
                } else if (xwin[i] == minmax[0][0]) {
                    g.setColor(new Color(120, 0, 0));
                } else if (xwin[i] == minmax[0][1]) {
                    g.setColor(Color.RED);
                }

                drawRow(g, Double.toString(xwin[i]) + "%", i, 0);

                if (owin[i] != minmax[1][0] && owin[i] != minmax[1][1]) {
                    g.setColor(Color.BLACK);
                } else if (owin[i] == minmax[1][0]) {
                    g.setColor(new Color(0, 0, 120));
                } else if (owin[i] == minmax[1][1]) {
                    g.setColor(new Color(0, 0, 255));
                }

                drawRow(g, Double.toString(owin[i]) + "%", i, 1);

                if (draw[i] != minmax[2][0] && draw[i] != minmax[2][1]) {
                    g.setColor(Color.BLACK);
                } else if (draw[i] == minmax[2][0]) {
                    g.setColor(new Color(150, 75, 0));
                } else if (draw[i] == minmax[2][1]) {
                    g.setColor(new Color(255, 128, 0));
                }
                drawRow(g, Double.toString(draw[i]) + "%", i, 2);
            }
        }

    }

    @Override
    public void actionPerformed(ActionEvent ae) {

        String comb = "";
        for (int i = 0; i < TTTM.turn; i++) {
            comb += Integer.toString(TTTM.combination[i]);
        }

        try {
            TTTM.rs = TTTM.stmt.executeQuery("SELECT * FROM decisions "
                    + "WHERE combination LIKE \"" + comb + "_\"");
            while (TTTM.rs.next()) {
                String branch = TTTM.rs.getString("combination");
                char[] chars = branch.toCharArray();
                xwin[Character.getNumericValue(chars[chars.length - 1])] = TTTM.rs.getDouble("xwin");
                owin[Character.getNumericValue(chars[chars.length - 1])] = TTTM.rs.getDouble("owin");
                draw[Character.getNumericValue(chars[chars.length - 1])] = TTTM.rs.getDouble("draw");
            }
            TTTM.rs = TTTM.stmt.executeQuery("SELECT AVG(xwin), AVG(owin), AVG(draw) "
                    + "FROM decisions WHERE combination LIKE \"" + comb + "_\"");
            TTTM.rs.next();
            xwin[9] = TTTM.rs.getDouble("AVG(xwin)");
            owin[9] = TTTM.rs.getDouble("AVG(owin)");
            draw[9] = TTTM.rs.getDouble("AVG(draw)");
        } catch (SQLException ex) {
            Logger.getLogger(DBPreviewPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        minmax[0][0] = getMin(xwin);
        minmax[0][1] = getMax(xwin);
        minmax[1][0] = getMin(owin);
        minmax[1][1] = getMax(owin);
        minmax[2][0] = getMin(draw);
        minmax[2][1] = getMax(draw);

        repaint();

        TTTM.checkInput();
        TTTM.winCheck();

        if (TTTM.DBP.isVisible()) {
            TTTM.returnToMenu.addActionListener((al) -> {
                exitToMenu();
            });
        }
    }

    double getMin(double[] array) {
        double x = 101;
        for (int i = 0; i < 9; i++) {
            if (array[i] < x && TTTM.occupancy[i] == -1) {
                x = array[i];
            }
        }
        return x;
    }

    double getMax(double[] array) {
        double x = 0;
        for (int i = 0; i < 9; i++) {
            if (array[i] > x && TTTM.occupancy[i] == -1) {
                x = array[i];
            }
        }
        return x;
    }

    void drawRow(Graphics g, String draw, int id, int row) {
        switch (row) {
            case 0:
                g.drawString(draw, TTTM.id[id][0] - 33, TTTM.id[id][1] - 25);
                break;
            case 1:
                g.drawString(draw, TTTM.id[id][0] - 33, TTTM.id[id][1]);
                break;
            case 2:
                g.drawString(draw, TTTM.id[id][0] - 33, TTTM.id[id][1] + 25);
                break;
        }
    }

    void exitToMenu() {
        TTTM.reset();
        TTTM.gm.setVisible(true);
        TTTM.DBP.setVisible(false);
        TTTM.DBP = null;
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