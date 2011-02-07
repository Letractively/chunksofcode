package com.myapp.mines.view.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.myapp.mines.controller.FieldController;
import com.myapp.mines.controller.GameController;
import com.myapp.mines.model.Field;

/**
a concrete view/controller implementation using swing.
@author andre
 */
public class SwingFieldView extends FieldController {

    private JLabel label;
    JPanel panel;

    /**
    creates a new swingfieldview for the specified model and
    the specified gamecontrollers
    @param model the field object to represent
    @param gameViewCtrl the gamecontroller to interact with
     */
    public SwingFieldView(Field model, GameController viewController) {
        super(model, viewController);

        panel = new JPanel(new GridLayout(1, 1));
        label = new JLabel(model.getStatus());

        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);

        panel.add(label, BorderLayout.CENTER);
        panel.addMouseListener(new SwingFieldViewMouseListener(this));

        panel.setPreferredSize(new Dimension(40, 30));
        repaintFieldView();
    }

    @Override
    public Object getGuiObject() {
        return panel;
    }

    @Override
    public void repaintFieldView() {
        panel.setBackground(calculateBackgroundColor(model));
        label.setForeground(calculateForegroundColor(model));
        label.setText(model.getStatus());
    }

}
