package tictactoemaster;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
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


File: AIvsAI.java
Panel for trying out what would happen if two AIs would go against each other

*/


class AIvsAIframe extends JFrame {

    AIvsAIpanel panel = new AIvsAIpanel();

    AIvsAIframe() {
        TTTM.setFrame(this, panel, "AI vs AI");
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                panel.exitToMenu();
            }
        });
    }
}

class AIvsAIpanel extends JPanel implements ActionListener {

    Timer t = new Timer(100, this);

    String[] settings = {"Random", "Don't loose", "Try to win", "Try to draw"};

    int observedelay = 10, state = 0;

    boolean startb = false;

    JToggleButton autorepeat = new JToggleButton();
    JButton start = new JButton(), menu = new JButton();
    JComboBox P1 = new JComboBox(settings), P2 = new JComboBox(settings);
    JSlider delay = new JSlider(1, 1500, 100);

    AIvsAIpanel() {
        this.add(autorepeat);
        autorepeat.setBounds(250, 520, 100, 30);
        autorepeat.setText("Autorestart");
        autorepeat.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));

        this.add(start);
        TTTM.setButton(start, 375, 520, 100, 30, "START", 12);

        this.add(menu);
        TTTM.setButton(menu, 500, 520, 100, 30, "Menu", 12);

        this.add(P1);
        P1.setBounds(20, 520, 200, 30);

        this.add(P2);
        P2.setBounds(630, 520, 200, 30);

        this.add(delay);
        delay.setBounds(300, 490, 250, 30);

        start.addActionListener((ae) -> {
            if (start.getText().equals("START")) {
                TTTM.reset();
                t.start();
                startb = true;
                start.setText("STOP");
            } else {
                startb = false;
                start.setText("START");
            }

        });

        menu.addActionListener((ae) -> {
            exitToMenu();
        });

        delay.addChangeListener((ae) -> {
            t.setDelay(delay.getValue());
        });

        t.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        TTTM.drawField(g);

        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        g.drawString("Settings for X:", 20, 505);
        g.drawString("Settings for O:", 630, 505);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        g.drawString("Tick speed: 1ms", 180, 510);
        g.drawString("1500ms", 555, 510);

    }

    @Override
    public void actionPerformed(ActionEvent ae) {

        repaint();

        TTTM.checkInput();
        TTTM.winCheck();

        if (startb && TTTM.winner == -1) {

            String comb = "";
            for (int i = 0; i < TTTM.turn; i++) {
                comb += Integer.toString(TTTM.combination[i]);
            }
            String x = "";
            if (TTTM.turn % 2 == 0) {
                if (P1.getSelectedIndex() == 0) {
                    x = randMove(comb, 0);
                } else {
                    x = xselect(comb, P1.getSelectedIndex());
                }
            } else {
                if (P2.getSelectedIndex() == 0) {
                    x = randMove(comb, 1);
                } else {
                    x = oselect(comb, P2.getSelectedIndex());
                }
            }
            try {
                TTTM.rs = TTTM.stmt.executeQuery(x);
                TTTM.rs.next();
                String branch = TTTM.rs.getString("combination");
                char[] dec = branch.toCharArray();
                TTTM.input = Character.getNumericValue(dec[dec.length - 1]);
                TTTM.checkInput();
                TTTM.winCheck();
            } catch (SQLException ex) {
                Logger.getLogger(AIvsAIpanel.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        if (autorepeat.isSelected()) {
            if (state < observedelay) {
                state++;
            } else {
                if (TTTM.winner != -1) {
                    TTTM.reset();
                    state = 0;
                }
            }
        } else if (TTTM.winner != -1) {
            start.setText("START");
            startb = false;
        }

    }

    String xselect(String comb, int i) {
        String x = "";
        switch (i) {
            case 1:
                x = "SELECT combination FROM decisions "
                        + "WHERE combination LIKE \"" + comb + "_\" "
                        + "ORDER BY owin ASC";
                break;
            case 2:
                x = "SELECT combination FROM decisions "
                        + "WHERE combination LIKE \"" + comb + "_\" "
                        + "ORDER BY xwin DESC";
                break;
            case 3:
                x = "SELECT combination FROM decisions "
                        + "WHERE combination LIKE \"" + comb + "_\" "
                        + "ORDER BY draw DESC";
                break;
        }
        return x;
    }

    String oselect(String comb, int i) {
        String x = "";
        switch (i) {
            case 1:
                x = "SELECT combination FROM decisions "
                        + "WHERE combination LIKE \"" + comb + "_\" "
                        + "ORDER BY xwin ASC";
                break;
            case 2:
                x = "SELECT combination FROM decisions "
                        + "WHERE combination LIKE \"" + comb + "_\" "
                        + "ORDER BY owin DESC";
                break;
            case 3:
                x = "SELECT combination FROM decisions "
                        + "WHERE combination LIKE \"" + comb + "_\" "
                        + "ORDER BY draw DESC";
                break;
        }
        return x;
    }

    String randMove(String comb, int player) {
        String x = "";
        int a = TTTM.rand.nextInt(3) + 1;
        if (player == 0) {
            x = xselect(comb, a);
        } else {
            x = oselect(comb, a);
        }
        return x;
    }

    void exitToMenu() {
        TTTM.gm.setVisible(true);
        t.stop();
        TTTM.AIvAI.setVisible(false);
        autorepeat.setSelected(false);
        TTTM.reset();
        TTTM.AIvAI = null;
    }

}