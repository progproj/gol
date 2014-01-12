/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gameoflife;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 *
 * @author darth
 */
public class Cell extends JPanel {
    public static final int CELL_SIZE = 1;

    public Cell (int x, int y) {
        setOpaque(true);
        setBorder(BorderFactory.createBevelBorder(CELL_SIZE));
        setBackground(new Color(105, 120, 105));
        setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
    }
}
