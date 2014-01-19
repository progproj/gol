/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gameoflife;

import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;

/**
 *
 * @author darth
 */
public class Grid extends JPanel {
    private Cell[][] cells;
    private final int GRID_SIZE;
    
    public Cell[][] getCells() {
        return cells;
    }

    public Grid(int GRID_SIZE) {
        this.GRID_SIZE = GRID_SIZE;
        cells = new Cell[GRID_SIZE][GRID_SIZE];
        setLayout(new GridLayout(GRID_SIZE, GRID_SIZE));

        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                final Cell cell = new Cell(x, y);
                cells[x][y] = cell;
                add(cell);
                
                cell.addMouseListener(new MouseListener() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if(e.getButton() == MouseEvent.BUTTON1) {
                            cell.live();
                            recountNeighbours(cell, true);
                        }
                        else if(e.getButton() == MouseEvent.BUTTON3) {
                            cell.die();
                            recountNeighbours(cell, false);
                        }
                    }

                    @Override
                    public void mouseReleased(MouseEvent me) {
                    }

                    @Override
                    public void mouseEntered(MouseEvent me) {
                    }

                    @Override
                    public void mouseExited(MouseEvent me) {
                    }

                    @Override
                    public void mouseClicked(MouseEvent me) {
                    }
                });
            }
        }
    }
    
    public void recountNeighbours(Cell cell, boolean alive) {
        int row;
        int col;
        
        for(int y=-1; y<2; y++) {
            row = cell.y + y;
            for(int x=-1; x<2; x++) {
                col = cell.x + x;
                
                if(row < 0 ||  row >= GRID_SIZE || col < 0 || col >= GRID_SIZE || cells[col][row] == cell)
                    continue;
                
                if(alive)
                    cells[col][row].neighboursAlive++;
                else
                    cells[col][row].neighboursAlive--;
            }
        }
    }
    
}
