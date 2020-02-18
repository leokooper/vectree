package GOL;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.*;

public class GameOfLife {

    final String NAME_OF_GAME = "Conway's Game of Life";
    final int START_LOCATION = 200;
    final int LIFE_SIZE = 50;
    final int POINT_RADIUS = 10;
    final int FIELD_SIZE = (LIFE_SIZE + 1) * POINT_RADIUS -3;
    final int BTN_PANEL_HEIGHT = 58;
    boolean[][] curGeneration = new boolean[LIFE_SIZE][LIFE_SIZE];
    boolean[][] nextGeneration = new boolean[LIFE_SIZE][LIFE_SIZE];
    volatile boolean goNextGeneration = false;
    int showDelay = 200;
    JFrame frame;
    Canvas canvasPanel;
    Random random = new Random();

    void go() {
        frame = new JFrame(NAME_OF_GAME);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FIELD_SIZE,FIELD_SIZE + BTN_PANEL_HEIGHT);
        frame.setLocation(START_LOCATION,START_LOCATION);
        frame.setResizable(false);

        canvasPanel = new Canvas();
        canvasPanel.setBackground(Color.white);
        //

        JButton fillButton = new JButton("Fill");
        fillButton.addActionListener(new FillButtonListener());

        JButton stepButton = new JButton("Step");
        stepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processOfLife();
                canvasPanel.repaint();
            }
        });

        JButton playButton = new JButton("Play");
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goNextGeneration = !goNextGeneration;
                playButton.setText(goNextGeneration ? "Stop" : "Play");
            }
        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(fillButton);
        btnPanel.add(stepButton);
        btnPanel.add(playButton);
        //

        frame.getContentPane().add(BorderLayout.CENTER, canvasPanel);
        frame.getContentPane().add(BorderLayout.SOUTH, btnPanel);
        frame.setVisible(true);

        while (true) {
            if (goNextGeneration) {
                processOfLife();
                canvasPanel.repaint();
                try {
                    Thread.sleep(showDelay);
                } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }
    }

    public class FillButtonListener implements ActionListener{
        public void actionPerformed (ActionEvent ev) {
            //countGeneration = 1;
            for (int x = 0; x < LIFE_SIZE; x++) {
                for (int y = 0; y < LIFE_SIZE; y++) {
                    curGeneration[x][y] = random.nextBoolean();
                }
            }
            canvasPanel.repaint();
        }
    }

    int countNeighbors(int x, int y) {
        int count = 0;
        for (int dx = -1; dx < 2; dx++) {
            for (int dy = -1; dy < 2; dy++) {
                int nX = x + dx;
                int nY = y + dy;
                nX = (nX < 0) ? LIFE_SIZE - 1 : nX;
                nY = (nY < 0) ? LIFE_SIZE - 1 : nY;
                nX = (nX > LIFE_SIZE - 1) ? 0 : nX;
                nY = (nY > LIFE_SIZE - 1) ? 0 : nY;
                count += (curGeneration[nX][nY]) ? 1 : 0;
            }
        }
        if (curGeneration[x][y]) { count--; }
        return count;
    }

    void processOfLife() {
        for (int x = 0; x < LIFE_SIZE; x++) {
            for (int y = 0; y < LIFE_SIZE; y++) {
                int count = countNeighbors(x, y);
                nextGeneration[x][y] = curGeneration[x][y];
                // if are 3 live neighbors around empty cells - the cell becomes alive
                nextGeneration[x][y] = (count == 3) ? true : nextGeneration[x][y];
                // if cell has less than 2 or greater than 3 neighbors - it will be die
                nextGeneration[x][y] = ((count < 2) || (count > 3)) ? false : nextGeneration[x][y];
            }
        }
        for (int x = 0; x < LIFE_SIZE; x++) {
            System.arraycopy(nextGeneration[x], 0, curGeneration[x], 0, LIFE_SIZE);
        }
    }

    public class Canvas extends JPanel {
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            for (int x = 0; x < LIFE_SIZE; x++) {
                for (int y = 0; y < LIFE_SIZE; y++) {
                    if(curGeneration[x][y]){
                        g.fillOval(x * POINT_RADIUS, y * POINT_RADIUS, POINT_RADIUS, POINT_RADIUS);
                    }
                }
            }
        }
    }
}
