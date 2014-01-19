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
    Cell[][] cells;
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
    }
    
    public GameOfLife() {
        super("Game Of Life reversed");
        setSize(400, 400);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // initialize the grid to 3x3 in the beginning
        initGrid(GRID_MIN_SIZE);
        
        // set actions to keys
        getRootPane().getInputMap().put(KeyStroke.getKeyStroke("UP"), "grow");
        getRootPane().getActionMap().put("grow", grow);
        
        getRootPane().getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "shrink");
        getRootPane().getActionMap().put("shrink", shrink);
        
        getRootPane().getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "go");
        getRootPane().getActionMap().put("go", go);
                
        setVisible(true);
    }
    
    /**
     * Grow the grid by one.
     */
    Action grow = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(GRID_SIZE < GRID_MAX_SIZE) {
                remove(grid);

                initGrid(++GRID_SIZE);
                validate();
                repaint();
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
                remove(grid);

                initGrid(--GRID_SIZE);
                validate();
                repaint();
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

            // calculate next generation
            for(int y=0; y<GRID_SIZE; y++) {
                for(int x=0; x<GRID_SIZE; x++) {
                    Cell currCell = cells[x][y];
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
                        if(currCell.neighboursAlive == 3)
                            currCell.willBeAlive = true;
                    }
                }
            }

            // generation over, show results
            for(int i=0; i<GRID_SIZE; i++) {
                for(int j=0; j<GRID_SIZE; j++) {
                    Cell currCell = cells[i][j];
                    
                    if(!currCell.alive && currCell.willBeAlive) {
                        currCell.live();
                        grid.recountNeighbours(currCell, true);
                    }
                    else if(currCell.alive && !currCell.willBeAlive) {
                        currCell.die();
                        grid.recountNeighbours(currCell, false);
                    }
                }
            }
        }
    };
}
