package tictactoemaster;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

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


File: TTTM.java
Main class containing main game logic and main function

 */


public class TTTM {

    static int input = -1, turn = 0, winner = -1, maxcomb = 255168,
            maxdec = 549945, bounty = 100, batchsize = 1000;

    static int[] combination = new int[10], branch = {0, 0, 0, 0, 0, 0, 0, 0, 0};

    static boolean hasPrivileges = true, hasAllPrivileges = false, combDBcomplete = true, decDBcomplete = true;

    static MenuFrame mf = new MenuFrame();
    static MPGameFrame mp;
    static DBConnectFrame dbcf;
    static GenMenu gm;
    static ComGenFrame cgf;
    static DecGenFrame dgf;
    static AISelectFrame asf;
    static AIGameFrame AI;
    static AIvsAIframe AIvAI;
    static DBPreviewFrame DBP;
    static FeedbackFrame FF;

    static JButton[] buttons;
    static JButton resetB = new JButton();
    static JButton returnToMenu = new JButton();

    static Color backgroundColor = Color.WHITE;

    static String IP, port, databasename, username, password;
    static String[] requiredPrivileges = {"INSERT", "DELETE", "CREATE"};
    static String[] exchange = {"0482", "2460", "6420", "8402"};
    static String[] to = {"0485", "2465", "6425", "8405"};

    static Connection MySQL;
    static Statement stmt;
    static Statement insert;
    static ResultSet rs;

    static DecimalFormat formatter = new DecimalFormat("#0.000");
    static Random rand = new Random();

    public static void main(String[] args) {
        setPlayingButtons();
        TTTM tttm = new TTTM();
        mf.setVisible(true);
    }

    static void setFrame(JFrame frame, JPanel panel, String title) {
        frame.setTitle("TicTacToe Master Remastered - " + title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        panel.setFocusable(true);
        panel.setFocusTraversalKeysEnabled(false);
        frame.setSize(900, 600);
        frame.add(panel);
        panel.setLayout(null);
        panel.setBackground(backgroundColor);
    }

    static void drawFancyLine(Graphics g, int x, int y, int width, int length) {
        g.fillRect(x, y, width, length);
        if (width < length) {
            g.fillOval(x, y - (width / 2), width, width);
            g.fillOval(x, y + length - (width / 2), width, width);
        } else if (width > length) {
            g.fillOval(x - (length / 2), y, length, length);
            g.fillOval(x + width - (length / 2), y, length, length);
        }
    }

    static int xstart = 250;
    static int ystart = 100;
    static int fieldwidth = 15;

    static int gap = 115;
    static int[][] id = new int[9][2];

    static void resetId() {
        for (int i = 0; i < 3; i++) {
            id[i][0] = 50 + i * gap + xstart;
            id[3 + i][0] = 50 + i * gap + xstart;
            id[6 + i][0] = 50 + i * gap + xstart;
            id[i][1] = 50 + ystart;
            id[3 + i][1] = 50 + 1 * gap + ystart;
            id[6 + i][1] = 50 + 2 * gap + ystart;
        }
    }

    static void setButton(JButton button, int x, int y, int width, int height, String text, int fontsize) {
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontsize));
        button.setBounds(x, y, width, height);
        button.setText(text);
    }

    static void setPlayingButtons() {
        buttons = new JButton[9];

        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton();
            buttons[i].setOpaque(false);
            buttons[i].setContentAreaFilled(false);
            buttons[i].setBorderPainted(false);
            buttons[i].setFocusable(false);

            final int x = i;

            buttons[i].addActionListener((ae) -> {
                input = x;
                buttons[x].setEnabled(false);
            });

            buttons[i].addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    if (buttons[x].isEnabled()) {
                        buttons[x].setBorderPainted(true);
                    }
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    buttons[x].setBorderPainted(false);
                }
            });

            if (i <= 2) {
                buttons[i].setBounds(xstart + i * 115, ystart, 100, 100);
            } else if (i > 2 && i <= 5) {
                buttons[i].setBounds(xstart + (i - 3) * 115, ystart + 115, 100, 100);
            } else if (i > 5 && i <= 8) {
                buttons[i].setBounds(xstart + (i - 6) * 115, ystart + 230, 100, 100);
            }
        }

        resetB.setVisible(false);
        setButton(resetB, xstart + 60, ystart + 370, 100, 40, "Reset", 16);
        resetB.addActionListener((ae) -> {
            reset();
        });

        returnToMenu.setVisible(false);
        setButton(returnToMenu, xstart + 180, ystart + 370, 100, 40, "Menu", 16);
    }

    static void setKeys(KeyEvent ke) {
        int kp = ke.getKeyCode();
        if (winner == -1) {
            switch (kp) {
                case KeyEvent.VK_NUMPAD1:
                    if (occupancy[6] == -1) {
                        input = 6;
                    }
                    break;
                case KeyEvent.VK_NUMPAD2:
                    if (occupancy[7] == -1) {
                        input = 7;
                    }
                    break;
                case KeyEvent.VK_NUMPAD3:
                    if (occupancy[8] == -1) {
                        input = 8;
                    }
                    break;
                case KeyEvent.VK_NUMPAD4:
                    if (occupancy[3] == -1) {
                        input = 3;
                    }
                    break;
                case KeyEvent.VK_NUMPAD5:
                    if (occupancy[4] == -1) {
                        input = 4;
                    }
                    break;
                case KeyEvent.VK_NUMPAD6:
                    if (occupancy[5] == -1) {
                        input = 5;
                    }
                    break;
                case KeyEvent.VK_NUMPAD7:
                    if (occupancy[0] == -1) {
                        input = 0;
                    }
                    break;
                case KeyEvent.VK_NUMPAD8:
                    if (occupancy[1] == -1) {
                        input = 1;
                    }
                    break;
                case KeyEvent.VK_NUMPAD9:
                    if (occupancy[2] == -1) {
                        input = 2;
                    }
                    break;
                default:
                    break;
            }
        }
        if (kp == KeyEvent.VK_R) {
            reset();
        }
    }

    static void addButtons(JPanel panel) {
        for (int i = 0; i < 9; i++) {
            panel.add(buttons[i]);
        }
        panel.add(resetB);
        panel.add(returnToMenu);

    }

    static int xwidth = 35;
    static int xthickness = 18;

    static void drawX(Graphics g, int a) {

        int x = id[a][0];
        int y = id[a][1];

        for (int i = 0; i < xthickness; i++) {
            g.drawLine(x - xwidth - (xthickness / 2) + i, y - xwidth, x + xwidth - (xthickness / 2) + i, y + xwidth);
            g.drawLine(x + xwidth - (xthickness / 2) + i, y - xwidth, x - xwidth - (xthickness / 2) + i, y + xwidth);
        }

    }

    static int owidth = 85;
    static int othickness = 25;

    static void drawO(Graphics g, int a) {

        int x = id[a][0];
        int y = id[a][1];

        Color color = g.getColor();
        g.fillOval(x - (owidth / 2), y - (owidth / 2), owidth, owidth);
        g.setColor(backgroundColor);
        g.fillOval(x - ((owidth - othickness) / 2), y - ((owidth - othickness) / 2), owidth - othickness, owidth - othickness);
        g.setColor(color);

    }

    static Color winColor = Color.RED;
    static int winwidth = (int) (fieldwidth * 0.75);

    static void horizontalWin(Graphics g, int rowid) {
        Color color = g.getColor();
        g.setColor(winColor);
        switch (rowid) {
            case 0:
                drawFancyLine(g, xstart, ystart + 50 - (winwidth / 2), 330, winwidth);
                break;
            case 1:
                drawFancyLine(g, xstart, ystart + 165 - (winwidth / 2), 330, winwidth);
                break;
            case 2:
                drawFancyLine(g, xstart, ystart + 280 - (winwidth / 2), 330, winwidth);
                break;
            default:
                System.err.println("Invalid row ID");
        }

        g.setColor(color);

    }

    static void verticalWin(Graphics g, int columnid) {
        Color color = g.getColor();
        g.setColor(winColor);
        switch (columnid) {
            case 0:
                drawFancyLine(g, xstart + 50 - (winwidth / 2), ystart, winwidth, 330);
                break;
            case 1:
                drawFancyLine(g, xstart + 165 - (winwidth / 2), ystart, winwidth, 330);
                break;
            case 2:
                drawFancyLine(g, xstart + 280 - (winwidth / 2), ystart, winwidth, 330);
                break;
            default:
                System.err.println("Invalid column ID");
        }
        g.setColor(color);
    }

    static void crossWin(Graphics g, int id) {
        Color color = g.getColor();
        g.setColor(winColor);
        switch (id) {
            case 0:
                for (int i = 0; i < fieldwidth; i++) {
                    g.drawLine(xstart - (fieldwidth / 2) + i, ystart, xstart + 330 - (fieldwidth / 2) + i, ystart + 330);
                }
                break;
            case 1:
                for (int i = 0; i < fieldwidth; i++) {
                    g.drawLine(xstart + 330 - (fieldwidth / 2) + i, ystart, xstart - (fieldwidth / 2) + i, ystart + 330);
                }
                break;
            default:
                System.err.println("Invalid cross ID");
        }
        g.setColor(color);
    }

    static void checkInput() {
        if (input != -1) {
            combination[turn] = input;
            turn++;
            input = -1;
        }
        for (int i = 0; i < 9; i++) {
            occupancy[i] = getArrayIndex(i) % 2;
        }
    }

    static boolean[][] win = {{false, false, false}, {false, false, false}, {false, false}};
    static int[] occupancy = {-1, -1, -1, -1, -1, -1, -1, -1, -1};

    static void winCheck() {
        if (turn > 4) {

            if (occupancy[0] >= 0) {
                if ((occupancy[0] == occupancy[1]) && (occupancy[0] == occupancy[2])) {
                    win[0][0] = true;
                    winner = occupancy[0];
                }
                if ((occupancy[0] == occupancy[3]) && (occupancy[0] == occupancy[6]) && occupancy[0] >= 0) {
                    win[1][0] = true;
                    winner = occupancy[0];
                }
            }

            if (occupancy[4] >= 0) {
                if ((occupancy[3] == occupancy[4]) && (occupancy[3] == occupancy[5])) {
                    win[0][1] = true;
                    winner = occupancy[3];
                }
                if ((occupancy[1] == occupancy[4]) && (occupancy[1] == occupancy[7])) {
                    win[1][1] = true;
                    winner = occupancy[1];
                }
                if ((occupancy[0] == occupancy[4]) && (occupancy[0] == occupancy[8])) {
                    win[2][0] = true;
                    winner = occupancy[0];
                }
                if ((occupancy[2] == occupancy[4]) && (occupancy[2] == occupancy[6])) {
                    win[2][1] = true;
                    winner = occupancy[2];
                }
            }

            if (occupancy[8] >= 0) {
                if ((occupancy[6] == occupancy[7]) && (occupancy[6] == occupancy[8])) {
                    win[0][2] = true;
                    winner = occupancy[6];
                }
                if ((occupancy[2] == occupancy[5]) && (occupancy[2] == occupancy[8])) {
                    win[1][2] = true;
                    winner = occupancy[2];
                }
            }

            if (turn == 9 && winner == -1) {
                winner = 2;
            }

            if (winner != -1) {
                endGame();
            }
        }
    }

    static void endGame() {
        for (int i = 0; i < 9; i++) {
            buttons[i].setEnabled(false);

        }
        resetB.setVisible(true);
        returnToMenu.setVisible(true);

    }

    static void reset() {
        for (int i = 0; i < 9; i++) {
            buttons[i].setEnabled(true);
        }
        resetB.setVisible(false);
        returnToMenu.setVisible(false);

        Arrays.fill(combination, 0);
        turn = 0;
        winner = -1;
        Arrays.fill(occupancy, -1);
        Arrays.fill(win[0], false);
        Arrays.fill(win[1], false);
        Arrays.fill(win[2], false);

    }

    static void drawField(Graphics g) {
        resetId();
        drawFancyLine(g, 100 + xstart, ystart, fieldwidth, 330);
        drawFancyLine(g, 215 + xstart, ystart, fieldwidth, 330);
        drawFancyLine(g, xstart, 100 + ystart, 330, fieldwidth);
        drawFancyLine(g, xstart, 215 + ystart, 330, fieldwidth);

        if (turn > 0) {
            for (int i = 0; i < turn; i++) {
                if (i % 2 == 0) {
                    drawX(g, combination[i]);
                } else {
                    drawO(g, combination[i]);
                }
            }
        }

        if (turn > 4) {
            for (int i = 0; i < 3; i++) {
                if (win[0][i]) {
                    horizontalWin(g, i);
                }
            }

            for (int i = 0; i < 3; i++) {
                if (win[1][i]) {
                    verticalWin(g, i);
                }
            }

            for (int i = 0; i < 2; i++) {
                if (win[2][i]) {
                    crossWin(g, i);
                }
            }
        }

        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));

        Color temp = g.getColor();

        g.setColor(winColor);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));

        if (winner >= 0) {
            switch (winner) {
                case 0:
                    g.drawString("X wins", xstart + 105, ystart - 40);
                    break;
                case 1:
                    g.drawString("O wins", xstart + 105, ystart - 40);
                    break;
                case 2:
                    g.drawString("Draw", xstart + 120, ystart - 40);
                    break;
            }
            g.clearRect(xstart + 110, ystart + 350, 250, 75);
        }
        g.setColor(temp);
    }

    static void connect() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        MySQL = DriverManager.getConnection(
                "jdbc:mysql://" + IP + ":" + port + "/" + databasename, username, password);
        stmt = MySQL.createStatement();
        insert = MySQL.createStatement();
    }

    static int getComCount() {
        int x = 0;
        try {
            rs = stmt.executeQuery("select count(*) from combinations");
            rs.next();
            x = rs.getInt(1);
        } catch (SQLException e) {
            System.err.println(e);
        }
        return x;
    }

    static int getDecCount() {
        int x = 0;
        try {
            TTTM.rs = TTTM.stmt.executeQuery("select count(*) from decisions");
            TTTM.rs.next();
            x = rs.getInt(1);
        } catch (SQLException e) {
            System.err.println(e);
        }
        return x;
    }

    static int arrayToInt(int[] array, int length) {
        String s = "";
        for (int i = 0; i < length; i++) {
            s += array[i];
        }
        return Integer.parseInt(s);
    }

    static int[] intToArray(int x) {
        String s = Integer.toString(x);
        char[] chars = s.toCharArray();
        int[] array = new int[chars.length];
        for (int i = 0; i < chars.length; i++) {
            array[i] = Character.getNumericValue(chars[i]);
        }
        return array;
    }

    static void checkDatabases() {
        if (getComCount() != maxcomb) {
            combDBcomplete = false;
            gm.panel.GenCombButton.setBorder(new LineBorder(Color.RED, 3));
        } else {
            combDBcomplete = true;
            gm.panel.GenCombButton.setBorder(new LineBorder(Color.GREEN, 3));
        }
        if (getDecCount() != maxdec) {
            decDBcomplete = false;
            gm.panel.GenDecButton.setBorder(new LineBorder(Color.RED, 3));
        } else {
            decDBcomplete = true;
            gm.panel.GenDecButton.setBorder(new LineBorder(Color.GREEN, 3));
        }
        if (!combDBcomplete || !decDBcomplete) {
            gm.panel.PlayButton.setEnabled(false);
            gm.panel.AIvAIButton.setEnabled(false);
            gm.panel.DBVButton.setEnabled(false);
        } else {
            gm.panel.PlayButton.setEnabled(true);
            gm.panel.AIvAIButton.setEnabled(true);
            gm.panel.DBVButton.setEnabled(true);
        }
        gm.panel.repaint();
    }

    static int getArrayIndex(int num) {
        int pos = -1;
        for (int i = 0; i < turn; i++) {
            if (num == combination[i]) {
                pos = i;
            }
        }
        return pos;
    }

    static void sortBranches() {
        for (int i = 0; i < 7; i++) {
            if (branch[7 - i] > i + 1) {
                branch[6 - i]++;
                branch[7 - i] = 0;
            }
        }
    }

    static void clearComb() {
        try {
            TTTM.stmt.executeUpdate("TRUNCATE combinations");
        } catch (SQLException e) {
            System.err.println(e);
        }
    }

    static void clearDec() {
        try {
            TTTM.stmt.executeUpdate("TRUNCATE decisions");
        } catch (SQLException e) {
            System.err.println(e);
        }
    }

    static String cutString(String s, int length) {
        char[] chars = s.toCharArray();
        String x = "";
        for (int i = 0; i < length; i++) {
            x += chars[i];
        }
        return x;
    }

    static void branchToOutput(int branch, int pos) {
        int x = 0;
        for (int i = 0; i < 9; i++) {
            if (occupancy[i] == -1) {
                if (x == branch) {
                    input = i;
                    turn = pos;
                    checkInput();
                    break;
                } else {
                    x++;
                }
            }
        }
    }

    static boolean doesExist(String inputComb) {
        char[] chars = inputComb.toCharArray();
        if (chars.length >= 5) {
            reset();
                for (int a = 0; a < chars.length; a++) {

                    input = Integer.parseInt(String.valueOf(chars[a]));
                    turn = a;
                    checkInput();
                    winCheck();

                    if(winner == 2){
                        break;
                    }
                    if(winner != -1 && a != chars.length - 1){
                        reset();
                        return false;
                    }             
                }

        }
        
        return true;
    }

    static String getPublicIP() {
        String PublicIP = "";
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            PublicIP = in.readLine();
        } catch (MalformedURLException ex) {
            Logger.getLogger(TTTM.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TTTM.class.getName()).log(Level.SEVERE, null, ex);
        }
        return PublicIP;
    }

    static String getLocalIP() {
        String LocalIP = "";
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            LocalIP = socket.getLocalAddress().getHostAddress();
        } catch (SocketException ex) {
            Logger.getLogger(TTTM.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(TTTM.class.getName()).log(Level.SEVERE, null, ex);
        }
        return LocalIP;
    }

}
