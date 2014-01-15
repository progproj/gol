/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gameoflife;

import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author darth
 */
public class Grid extends JPanel {
    private List<Cell> cells;
    
    public List<Cell> getCells() {
        return cells;
    }

    public Grid(int GRID_SIZE) {
        cells = new ArrayList<>(GRID_SIZE * GRID_SIZE);
        setLayout(new GridLayout(GRID_SIZE, GRID_SIZE));

        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                final Cell cell = new Cell(x, y);
                cells.add(cell);
                add(cell);
                
                cell.addMouseListener(new MouseListener() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if(e.getButton() == MouseEvent.BUTTON1) {
                            cell.willBeAlive = true;
                            cell.live();
                        }
                        else if(e.getButton() == MouseEvent.BUTTON3) {
                            cell.willBeAlive = false;
                            cell.die();
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
    
}
