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
            step(cells);
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
                System.out.println("Grid is empty! Returning!");
                return;
            }
                
            
            resetArray();
            check[1][0].live();
            grid.recountNeighbours(check, check[1][0], true);
            check[2][0].live();
            grid.recountNeighbours(check, check[2][0], true);
            check[2][1].live();
            grid.recountNeighbours(check, check[2][1], true);
            step(check);
            
            if(match()) {
                System.out.println("equal");
                
                // TODO: make previous step appear on the grid!
            }
            else
                System.out.println("not equal");
        }
    };
    
    private void resetGame() {
        //reset generation count
        GENERATION = 0;

        remove(grid);

        initGrid(GRID_SIZE);
        validate();
        repaint();
        System.out.println("New game with grid " + GRID_SIZE + "x" + GRID_SIZE);
    }
    
    private boolean match() {
        for(int i=0; i<GRID_SIZE; i++)
            for(int j=0; j<GRID_SIZE; j++)
                if(cells[i][j].alive != check[i][j].alive)
                    return false;
        
        return true;
    }
    
    private void resetArray() {
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                check[x][y].die();
                grid.recountNeighbours(check, check[x][y], false);
            }
        }
    }
    
    private void step(Cell[][] array) {
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
}
