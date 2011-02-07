package com.myapp.mines.view.swing;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.myapp.mines.model.Field;

/**
mouse listener implementation for fieldgui objects.
@author andre
 */
class SwingFieldViewMouseListener implements MouseListener {

    private SwingFieldView fieldGui;
    private boolean leftButtonHoldDown = false;
    private boolean middleButtonHoldDown = false;
    private boolean rightButtonHoldDown = false;
    private static final int LEFT_BUTTON = 1;
    private static final int MIDDLE_BUTTON = 2;
    private static final int RIGHT_BUTTON = 3;

    /**
    creates a new mouselistener for the specified field swing view
     */
    public SwingFieldViewMouseListener(SwingFieldView fieldGui) {
        this.fieldGui = fieldGui;
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        if (gameWonOrOver())
            return;

        switch (event.getButton()) {
            case LEFT_BUTTON:
                if (isRightOrMiddlePressedDown())
                    fieldGui.solveNeighbors();
                else
                    fieldGui.enterField();
                break;

            case MIDDLE_BUTTON:
            case RIGHT_BUTTON:
                if (leftButtonHoldDown)
                    return;

                fieldGui.getModel().toggleMarked();
                fieldGui.repaintFieldView();
                fieldGui.panel.setBackground(Color.yellow);
                break;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (gameWonOrOver())
            return;

        int button = e.getButton();

        if (button == LEFT_BUTTON)
            leftButtonHoldDown = true;

        if (button == MIDDLE_BUTTON)
            middleButtonHoldDown = true;

        if (button == RIGHT_BUTTON)
            rightButtonHoldDown = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (gameWonOrOver())
            return;

        int button = e.getButton();

        if (button == LEFT_BUTTON)
            leftButtonHoldDown = false;

        if (button == MIDDLE_BUTTON)
            middleButtonHoldDown = false;

        if (button == RIGHT_BUTTON)
            rightButtonHoldDown = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (gameWonOrOver())
            return;

        fieldGui.panel.setBackground(Color.YELLOW);

        for (Field n : fieldGui.getModel().getNeighbours()) {

            SwingFieldView fg = guiOf(n);

            if (!n.isEntered() && !n.isMarked())
                fg.panel.setBackground(fg.panel.getBackground().darker());
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (gameWonOrOver())
            return;

        fieldGui.repaintFieldView();

        for (Field n : fieldGui.getModel().getNeighbours()) {
            SwingFieldView fg = guiOf(n);
            fg.repaintFieldView();
        }
    }

    /**
    returns the gui component associated with a given field object.
    @param f the field which is mapped to the wanted view object.
    @return the gui component associated with a given field object.
     */
    private static SwingFieldView guiOf(Field f) {
        return (SwingFieldView) f.getGame().getAssociatedView(f);
    }

    /**
    if the game is over or won, we do not listen to the mouse any more.
    @return if the game is over or won.
     */
    private boolean gameWonOrOver() {
        return fieldGui.getModel().getGame().isGameOver() ||
               fieldGui.getModel().getGame().isGameWon();
    }

    /**
    returns true if the middle or right mouse button is being hold down
    at the moment.
    @return if the middle or right mouse button is being hold down
     */
    private boolean isRightOrMiddlePressedDown() {
        return rightButtonHoldDown || middleButtonHoldDown;
    }

}
