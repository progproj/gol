/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gameoflife;

import java.awt.event.ActionEvent;
import java.util.List;
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
    List<Cell> cells;
    private final int GRID_MIN_SIZE = 3;
    private final int GRID_MAX_SIZE = 10;
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
        
        initGrid(GRID_MIN_SIZE);
        
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
        
        Action go = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("generation " + (++GENERATION));
                
                for(int i=0; i<cells.size(); i++) {
                    // reproduction
                    if(cells.get(i).alive == false) {
                        for(int j=1; j<5; j++) {
                            if(i-j > 0 && cells.get(i-j) != null && cells.get(i-j).alive) {
                                cells.get(i).neighboursAlive++;
                                if(i == 3)
                                    System.out.println("neighbous " + (i-j) + "was alive");
                            }
                        }
                        for(int j=1; j<5; j++) {
                            if(i+j < cells.size() && cells.get(i+j) != null && cells.get(i+j).alive) {
                                cells.get(i).neighboursAlive++;
                                if(i == 3)
                                    System.out.println("neighbous " + (i+j) + "was alive");
                            }
                        }
                        
                        if(cells.get(i).neighboursAlive == 3)
                            cells.get(i).willBeAlive = true;
                        else
                            cells.get(i).willBeAlive = false;
                    }
                }
                
                // generation over, do the thing
                for(Cell cell : cells) {
                    if(cell.willBeAlive)
                        cell.live();
                    else
                        cell.die();
                }
            }
        };
        
        
        getRootPane().getInputMap().put(KeyStroke.getKeyStroke("UP"), "grow");
        getRootPane().getActionMap().put("grow", grow);
        
        getRootPane().getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "shrink");
        getRootPane().getActionMap().put("shrink", shrink);
        
        getRootPane().getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "go");
        getRootPane().getActionMap().put("go", go);
                
        setVisible(true);
    }
    
}
