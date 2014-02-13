/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gameoflife;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
    private boolean found = false;
    
    // grey out glass pane
    JPanel glass = new JPanel(new GridBagLayout());
    
    // stop all calculating threads
    private volatile boolean stop = true;
    
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
        
        // glass pane
        JLabel label = new JLabel("Calculating... Press r to reset game.");
        glass.add(label);
        label.setForeground(Color.red);
        Color transparent = new Color(128, 128, 128, 200);
        glass.setBackground(transparent);
        
        // trap mouse events.
        glass.addMouseListener(new MouseAdapter() {});
        
        setGlassPane(glass);
        
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
     * Stop calculating threads if necessary and reset game with the same grid size.
     */
    Action reset = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            stop = true; // stop calculating threads
            resetGame();
        }
    };
    
    /**
     * Grow the grid by one.
     */
    Action grow = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // do something only if we stopped calculations
            if(stop) {
                if(GRID_SIZE < GRID_MAX_SIZE) {
                    GRID_SIZE++;
                    resetGame();
                }
            }
        }
    };
    
    /**
     * Shrink the grid by one.
     */
    Action shrink = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // do something only if we stopped calculations
            if(stop) {
                if(GRID_SIZE > GRID_MIN_SIZE) {
                    GRID_SIZE--;
                    resetGame();
                }
            }
        }
    };
    
    /**
     * Step one generation forward.
     */
    Action go = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // do something only if we stopped calculations
            if(stop) {
                System.out.println("generation " + (++GENERATION));
                stepForward(cells, true);
            }
        }
    };
    
    /**
     * Step one generation back.
     */
    Action back = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // do something only if we stopped calculations
            if(stop) {
                // no way back from empty grid
                if(grid.isEmpty()) {
                    errorMessage("Grid is empty! Returning!");
                    return;
                }

                setTitle("Calculating...");

                // take a step back
                new Thread() {
                    @Override
                    public void run() {
                        stop = false;
                        glass.setVisible(true);
                        
                        try {
                            stepBack();
                        } catch (InterruptedException ex) {
                            System.out.println(ex.getMessage());
                        }
                        
                        glass.setVisible(false);
                        stop = true;
                        setTitle("Game Of Life reversed");
                        
                        if(!found)
                            errorMessage("No previous steps can be found.");
                    }
                }.start();

                //setTitle("Game Of Life reversed");
            }
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
                        cells[x][y].live(true);
                    else
                        cells[x][y].die(true);

                    grid.recountNeighbours(cells, cells[x][y], cells[x][y].alive);
                }
                m++;
            }
        }
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
    private void stepForward(Cell[][] array, boolean paintCells) {
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
                    currCell.live(paintCells);
                    grid.recountNeighbours(array, currCell, true);
                }
                else if(currCell.alive && !currCell.willBeAlive) {
                    currCell.die(paintCells);
                    grid.recountNeighbours(array, currCell, false);
                }                    
            }
        }
        
    }
    
    private void stepBack() throws InterruptedException {
        // get available CPU cores, should be 8 on my i7
        int processors = Runtime.getRuntime().availableProcessors();
        // the number of permutations we will be processing in one separate thread
        long permCount = (long) (Math.pow(2, GRID_SIZE*GRID_SIZE) - 1);
        // nothing found yet
        found = false;
        
        if(GRID_SIZE > 4) {
            ExecutorService executor = Executors.newFixedThreadPool(processors);
            long end = 0;

            for(int t = 0; t < processors; t++) {
                final long START = end;
                end += (long)Math.ceil((double)permCount / (double)processors);
                final long END = end;

                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        count(START, END);
                    }
                });
            }
            
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        }
        else {
            count(0, permCount);
        }
    }
    
    private void count(long start, long end) {
        // store fixed length binary string here
        String binStr, tmp;
        // counter
        int n;
        // testing grid
        Cell[][] check = grid.getCheck();
        
        // generate every possible combination of the matrix in form of binary string
        mainLoop: for(long i = start; i <= end; i++) {
            // if game is reseted during calculations
            if(stop)
                return;
            
            // make binary string of fixed length
            tmp = Long.toBinaryString(i);
            binStr = "";
            for(int j = 0; j < GRID_SIZE * GRID_SIZE - tmp.length(); j++)
                binStr += '0';
            binStr += tmp;
            
            /**************** PARALLEL STUFF ******************************************/
                    // go through the matrix and fill it with the binary string's values
                    n = 0;
                    for (int y = 0; y < GRID_SIZE; y++) {
                        for (int x = 0; x < GRID_SIZE; x++) {

                            // if current value of the cell differs from the char
                            // in the string, replace it and recount neighbours
                            if(check[x][y].alive != (binStr.charAt(n) == '1')) {
                                if(binStr.charAt(n) == '1')
                                    check[x][y].live(false);
                                else
                                    check[x][y].die(false);

                                grid.recountNeighbours(check, check[x][y], check[x][y].alive);
                            }
                            n++;
                        }
                    }

                    // check next stepForward
                    stepForward(check, false);

                    // see if next stepForward matches our current stepForward to determine if this
                    // combination was our previous stepForward
                    for(int y=0; y<GRID_SIZE; y++)
                        for(int x=0; x<GRID_SIZE; x++)
                            if(cells[x][y].alive != check[x][y].alive)
                                continue mainLoop;
            /***************************************************************************/

            // we are here if we found a matching case
            stop = true;
            found(binStr);
            found = true;

            System.out.println("generation " + (--GENERATION));
            return;
        }
    }
}
