/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gameoflife;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

/**
 *
 * @author darth
 */
public class GameOfLife extends JFrame {
    Grid grid;
    Cell[][] cells, check;
    private final int GRID_MIN_SIZE = 3;
    private final int GRID_MAX_SIZE = 20;
    private int GRID_SIZE = GRID_MIN_SIZE;
    private int GENERATION = 0;
    
    public static void main(String[] args) {
        new GameOfLife();
    }
    
    private void initGrid(int size) {
        grid = new Grid(size);
        add(grid);
        cells = grid.getCells();
        check = grid.getCheck();
    }
    
    public GameOfLife() {
        super("Game Of Life reversed");
        setSize(400, 400);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // initialize the grid to 3x3 in the beginning
        initGrid(GRID_MIN_SIZE);
        
        // set actions to keys
        getRootPane().getInputMap().put(KeyStroke.getKeyStroke('r'), "reset");
        getRootPane().getActionMap().put("reset", reset);
        
        getRootPane().getInputMap().put(KeyStroke.getKeyStroke("UP"), "grow");
        getRootPane().getActionMap().put("grow", grow);
        
        getRootPane().getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "shrink");
        getRootPane().getActionMap().put("shrink", shrink);
        
        getRootPane().getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "go");
        getRootPane().getActionMap().put("go", go);
        
        getRootPane().getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "back");
        getRootPane().getActionMap().put("back", back);
                
        setVisible(true);
    }
    
    /**
     * Reset game with same grid size.
     */
    Action reset = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            resetGame();
        }
    };
    
    /**
     * Grow the grid by one.
     */
    Action grow = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {            
            if(GRID_SIZE < GRID_MAX_SIZE) {
                GRID_SIZE++;
                resetGame();
            }
        }
    };
    
    /**
     * Shrink the grid by one.
     */
    Action shrink = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(GRID_SIZE > GRID_MIN_SIZE) {
                GRID_SIZE--;
                resetGame();
            }
        }
    };
    
    /**
     * Step one generation forward.
     */
    Action go = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("generation " + (++GENERATION));
            stepForward(cells);
        }
    };
    
    /**
     * Step one generation back.
     */
    Action back = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // no way back from empty grid
            if(grid.isEmpty()) {
                errorMessage("Grid is empty! Returning!");
                return;
            }
            
            // take a step back
            stepBack();
        }
    };
    
    private void errorMessage(String msg) {
        JOptionPane.showMessageDialog(grid,
            msg,
            "No luck",
            JOptionPane.ERROR_MESSAGE);
    }
    
    private void found(String binStr) {
        System.out.println("equal");
        System.out.println(binStr);
        int m = 0;

        // copy matching layout to main view
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                if(cells[x][y].alive != (binStr.charAt(m) == '1')) {
                    if(binStr.charAt(m) == '1')
                        cells[x][y].live();
                    else
                        cells[x][y].die();

                    grid.recountNeighbours(cells, cells[x][y], cells[x][y].alive);
                }
                m++;
            }
        }
    }
    
    /**
     * Creates and returns a fixed sized binary string with prepending zeroes.
     * @param in Not fixed sized binary string
     * @return Binary string with constant length of GRID_SIZE * GRID_SIZE
     */
    private String fixedLenBinStr(String in) {
        String out = "";
        int left = GRID_SIZE * GRID_SIZE - in.length();
        
        for(int i = 0; i < left; i++)
            out += '0';
        out += in;
        
        return out;
    }
    
    /**
     * Start new game. 'r' button triggers this.
     */
    private void resetGame() {
        //reset generation count
        GENERATION = 0;

        remove(grid);

        initGrid(GRID_SIZE);
        validate();
        repaint();
        System.out.println("New game with grid " + GRID_SIZE + "x" + GRID_SIZE);
    }
    
    /**
     * One generation forward in an array.
     * @param array The array in which generation is incremented.
     */
    private void stepForward(Cell[][] array) {
        // calculate next generation
        for(int y=0; y<GRID_SIZE; y++) {
            for(int x=0; x<GRID_SIZE; x++) {
                Cell currCell = array[x][y];
                int n = currCell.neighboursAlive;

                // living cells
                if(currCell.alive) {
                    if(n < 2 || n > 3)
                        currCell.willBeAlive = false;
                    else
                        currCell.willBeAlive = true;
                }
                // dead cells
                else {
                    if(n == 3)
                        currCell.willBeAlive = true;
                }
            }
        }

        // generation over, show results
        for(int i=0; i<GRID_SIZE; i++) {
            for(int j=0; j<GRID_SIZE; j++) {
                Cell currCell = array[i][j];

                if(!currCell.alive && currCell.willBeAlive) {
                    currCell.live();
                    grid.recountNeighbours(array, currCell, true);
                }
                else if(currCell.alive && !currCell.willBeAlive) {
                    currCell.die();
                    grid.recountNeighbours(array, currCell, false);
                }                    
            }
        }
    }
    
    private void stepBack() {
        // store fixed length binary string here
        String binStr = "";
        int n = 0;

        // generate every possible combination of the matrix in form of binary string
        mainLoop: for(int i = 0; i <= Math.pow(2, GRID_SIZE*GRID_SIZE) - 1; i++) {
            binStr = fixedLenBinStr(Integer.toBinaryString(i));
            n = 0;

            // go through the matrix and fill it with the binary string's values
            for (int y = 0; y < GRID_SIZE; y++) {
                for (int x = 0; x < GRID_SIZE; x++) {

                    // if current value of the cell differs from the char
                    // in the string, replace it and recount neighbours
                    if(check[x][y].alive != (binStr.charAt(n) == '1')) {
                        if(binStr.charAt(n) == '1')
                            check[x][y].live();
                        else
                            check[x][y].die();

                        grid.recountNeighbours(check, check[x][y], check[x][y].alive);
                    }
                    n++;
                }
            }

            // check next stepForward
            stepForward(check);

            // see if next stepForward matches our current stepForward to determine if this
            // combination was our previous stepForward
            for(int y=0; y<GRID_SIZE; y++)
                for(int x=0; x<GRID_SIZE; x++)
                    if(cells[x][y].alive != check[x][y].alive)
                        continue mainLoop;

            // we are here if we found a matching case
            found(binStr);

            System.out.println("generation " + (--GENERATION));
            return;
        }

        // no previous stepForward can be found
        errorMessage("No previous steps can be found.");
    }
}
