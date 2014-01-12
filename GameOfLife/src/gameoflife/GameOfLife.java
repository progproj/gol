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
    private final int GRID_MIN_SIZE = 3;
    private final int GRID_MAX_SIZE = 10;
    private int GRID_SIZE = GRID_MIN_SIZE;
    
    public static void main(String[] args) {
        new GameOfLife();
    }
    
    public GameOfLife() {
        super("Game Of Life reversed");
        setSize(400, 400);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        grid = new Grid(GRID_SIZE);
        add(grid);
        
        Action grow = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(GRID_SIZE < GRID_MAX_SIZE) {
                    remove(grid);

                    grid = new Grid(++GRID_SIZE);
                    add(grid);

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

                    grid = new Grid(--GRID_SIZE);
                    add(grid);

                    validate();
                    repaint();
                }
            }
        };
        
        
        getRootPane().getInputMap().put(KeyStroke.getKeyStroke("UP"), "grow");
        getRootPane().getActionMap().put("grow", grow);
        
        getRootPane().getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "shrink");
        getRootPane().getActionMap().put("shrink", shrink);
                
        setVisible(true);
    }
    
}
