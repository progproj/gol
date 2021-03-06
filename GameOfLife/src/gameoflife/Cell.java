/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gameoflife;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JLabel;

/**
 *
 * @author darth
 */
public class Cell extends JLabel {
    public static final int CELL_SIZE = 1;
    public final int x;
    public final int y;
    public boolean alive = false;
    public boolean willBeAlive = false;
    public short neighboursAlive = 0;

    public Cell (int x, int y) {
        setOpaque(true);
        setBorder(BorderFactory.createBevelBorder(CELL_SIZE));
        setBackground(new Color(105, 120, 105));
        setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        
        //setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        //setText(Short.toString(neighboursAlive));
        
        this.x = x;
        this.y = y;
    }
    
    public void live(boolean paint) {
        // color the cell
        if(paint)
            setBackground(Color.blue);
        
        alive = true;
        willBeAlive = false;
    }
    
    public void die(boolean paint) {
        // color the cell
        if(paint)
            setBackground(new Color(105, 120, 105));
        
        alive = false;
        willBeAlive = false;
    }
}
