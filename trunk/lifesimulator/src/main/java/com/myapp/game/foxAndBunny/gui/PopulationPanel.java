package com.myapp.game.foxAndBunny.gui;

import static com.myapp.util.swing.Util.title;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.myapp.game.foxAndBunny.Logger;
import com.myapp.game.foxAndBunny.model.Population;
import com.myapp.game.foxAndBunny.model.Population.PopulationState;
import com.myapp.game.foxAndBunny.model.Population.StateHistory;
import com.myapp.game.foxAndBunny.model.World;

@SuppressWarnings("serial")
final class PopulationPanel extends JPanel {

    private static final String stepPrefix        = "Step: ", 
                                populationPrefix  = "Population: ",
                                bunniesPrefix     = "Bunnies: ", 
                                foxesPrefix       = "Foxes: ";
    
    private JLabel step       = new JLabel(stepPrefix), 
                   population = new JLabel(populationPrefix), 
                   foxes      = new JLabel(bunniesPrefix), 
                   bunnies    = new JLabel(foxesPrefix);


    private final World model;
    private AbstractGraphPanel graphAbsolute;
    private AbstractGraphPanel graphRelative;
    
    PopulationPanel(World model) {
        super(new BorderLayout());
        this.model = model;
        graphAbsolute = new AbsoluteGraphPanel(model);
        graphRelative = new RelativeGraphPanel(model);
        
        JPanel grid = new JPanel(new GridLayout(2,2));
        grid.add(step);
        grid.add(foxes);
        grid.add(population);
        grid.add(bunnies);
        add(grid, BorderLayout.CENTER);
        

        JPanel graphs = new JPanel(new GridLayout(2,1));

        JPanel absWrap = new JPanel(new BorderLayout());
        JPanel relWrap = new JPanel(new BorderLayout());
        relWrap.add(graphRelative);
        absWrap.add(graphAbsolute);
        graphs.add(absWrap);
        graphs.add(relWrap);

        title(absWrap, "foxes/bunnies/free");
        title(relWrap, "foxes/bunnies");
        add(graphs, BorderLayout.SOUTH);


        if (Logger.IS_DEBUG) {
            title(this, SimulatorFrame.class.getSimpleName()+".populationPanel");
            title(graphs, getClass().getSimpleName() + ".graphs");
            title(grid, getClass().getSimpleName() + ".grid");
        }
    }
    
    @Override
    public void repaint() {
        super.repaint();
        
        if (model == null)
            return;
        
        Population pop = model.getPopulation();
        PopulationState state = pop.getState();
        int count = state.getPopulation();
        int foxesPercent;
        int bunnyPercent;
        
        if (count <= 0) { // avoid division by zero
            assert count == 0 : count;
            foxesPercent = bunnyPercent = 0;
            
        } else {
            foxesPercent = (state.getFoxes() * 100) / count;
            bunnyPercent = (state.getBunnies() * 100) / count;
        }
        
        step.setText(stepPrefix+state.getSteps());
        population.setText(populationPrefix+count);
        foxes.setText(foxesPrefix+state.getFoxes()+" ("+foxesPercent+" %)");
        bunnies.setText(bunniesPrefix+state.getBunnies()+" ("+bunnyPercent+" %)");
        
        graphAbsolute.repaint();
        graphRelative.repaint();
    }
}



@SuppressWarnings("serial")
abstract class AbstractGraphPanel extends JPanel {

    private final World world;

    AbstractGraphPanel(World world) {
        super(new GridLayout(0, 1));
        this.world = world;
        setMinimumSize(new Dimension(50, 20));
        setPreferredSize(new Dimension(50, 30));
        setMaximumSize(new Dimension(50, 40));
    }
    
    @Override
    public void paint(Graphics g) {
        // start with most recent state:
        StateHistory history = world.getPopulation().getHistory();
        int currentX = getWidth();         // mostright pixel
        int stateIdx = history.size() - 1; // newest state
        
        try {
        
            // from the right to the left:
            // until left edge of panel is reached || all available states are painted
            for (; stateIdx >= 0 && currentX >= 0; currentX--, stateIdx--) {
                PopulationState currentState = history.get(stateIdx);
                paintState(currentX, currentState, g);
            }
        
        } catch (IndexOutOfBoundsException e) {
            // happens when calling history.get(stateIdx); because
            // each 500 steps the history of the population shrinks down.
            // this is not a mayor issue, since 100 frames per seconds are drawn
        }
    }


    /**
     * paint one vertical line at currentX that represents the state.
     * each pixel of width represents 1 step.
     * paints one relative to GraphPanel.this's coordinates
     * 
     * @param currentX
     *            the current x offset: leftmost = 0, rightmost =
     *            getHeight()
     * @param model
     *            the state to paint
     * @param g
     */
    protected abstract void paintState(int currentX, PopulationState model, Graphics g);
    
}



@SuppressWarnings("serial")
final class AbsoluteGraphPanel extends AbstractGraphPanel {

    public AbsoluteGraphPanel(World world) {
        super(world);
    }
    
    protected void paintState(int currentX, PopulationState model, Graphics g) {
        int height = getHeight();
        double foxRel =   ((double)model.getFoxes())   / model.getFields();
        double bunnyRel = ((double)model.getBunnies()) / model.getFields();
        
        int foxPixel =   new Double(foxRel   * height).intValue();
        int bunnxPixel = new Double(bunnyRel * height).intValue();
       
        // begin at bottom (zero = top of panel, height = bottom of panel)
        
        g.setColor(WorldPanel.FOX_COLOR);
        int y1 = height; // bottom
        int y2 = height-foxPixel;
        g.drawLine(currentX, y1, currentX, y2);

        g.setColor(WorldPanel.BUNNY_COLOR);
        y1 = height-foxPixel;
        y2 = height-(foxPixel+bunnxPixel);
        g.drawLine(currentX, y1, currentX, y2);
        
        g.setColor(WorldPanel.FREE_COLOR);
        y1 = height-(foxPixel+bunnxPixel);
        y2 = 0; // top
        g.drawLine(currentX, y1, currentX, y2);
    }

}


@SuppressWarnings("serial") 
final class RelativeGraphPanel extends AbstractGraphPanel {

    RelativeGraphPanel(World w) {
        super(w);
    }

    /**
     * paint one vertical line at currentX that represents the state.
     * each pixel of width represents 1 step.
     * paints one relative to GraphPanel.this's coordinates
     * 
     * @param currentX
     *            the current x offset: leftmost = 0, rightmost =
     *            getHeight()
     * @param state
     *            the state to paint
     * @param g
     */
    protected void paintState(int currentX, PopulationState state, Graphics g) {
        int height = getHeight();

        double foxRel = ((double)state.getFoxes()) / state.getPopulation();
        int foxPixel = new Double(foxRel * height).intValue();
       
        // begin at bottom (zero = top of panel, height = bottom of panel)
        g.setColor(WorldPanel.FOX_COLOR);
        int y1 = height; // bottom
        int y2 = height-foxPixel;
        g.drawLine(currentX, y1, currentX, y2);

        g.setColor(WorldPanel.BUNNY_COLOR);
        y1 = height-foxPixel-1;
        y2 = 0; // top
        g.drawLine(currentX, y1, currentX, y2);
    }
}


