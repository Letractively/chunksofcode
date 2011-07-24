package com.myapp.tools.media.renamer.view.swing;

import static com.myapp.tools.media.renamer.controller.Msg.msg;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.myapp.tools.media.renamer.controller.IApplication;
import com.myapp.tools.media.renamer.model.IRenamable;

/**
 * a simple swing gui control allowing the selection of an element in the
 * renamer list
 * 
 * @author andre
 * 
 */
@SuppressWarnings("serial")
class IndexControlPanel extends JPanel implements ActionListener {
    
    private final IApplication app;
    private JComboBox comboBox;
    private JRadioButton first, choose, last;

    /**
     * creates a new IndexControlPanel with the given application context
     * 
     * @param app
     *            the application context
     */
    IndexControlPanel(IApplication app) {
        super(new GridLayout(0, 1));
        this.app = app;
        
        // setup ///////////////////////////////////
        
        comboBox = new JComboBox();
        comboBox.setPreferredSize(new Dimension(200, 20));
        
        ButtonGroup grp = new ButtonGroup();
        JRadioButton[] buttons = new JRadioButton[] {
           first = new JRadioButton(msg("FileChooser.indexControlPanel.first")),
           choose = new JRadioButton(),
           last = new JRadioButton(msg("FileChooser.indexControlPanel.last"))
        };
        
        for (JRadioButton b : buttons) {
            grp.add(b);
            b.addActionListener(this);
        }
        
        refresh();
        
        // layout //////////////////////////////
        
        JPanel radioBtns = new JPanel(new GridLayout(0, 1));
        
        radioBtns.add(first);
        radioBtns.add(new JPanel(new BorderLayout()) {{
            add(choose, BorderLayout.WEST);
            add(comboBox, BorderLayout.CENTER);
        }});
        radioBtns.add(last);
        
        add(radioBtns);
    }



@Override
public void actionPerformed(ActionEvent e) {
    comboBox.setEnabled(choose.isSelected());
}



/**
 * returns the index chosen by the user
 * 
 * @return the index chosen by the user
 */
int getSelectedIndex() {
    int i = Integer.MAX_VALUE; //default is end of list

    if (first.isSelected()) {
        i = 0;

    } else if (last.isSelected()) {
        i = Integer.MAX_VALUE;

    } else if (choose.isSelected()) {
        Object item = comboBox.getModel().getSelectedItem();

        assert item != null && (item instanceof IRenamable);

        //FIXME: how should this work when elemenst may occur multiple ?
        i = ((DefaultComboBoxModel)comboBox.getModel()).getIndexOf(item);

    } else {
        assert false : "" + i;
    }
    
    return i;
}



/**
 * sets the combobox model to the given one. user may choose elements from
 * that combobox to specify a position.
 * 
 */
public void refresh() {
    ComboBoxModel m = new DefaultComboBoxModel(app.getRenamer().toArray());
    comboBox.setModel(m);

    int size = app.getRenamer().getSize();

    if (size <= 0) {
        choose.setEnabled(false);
        first.doClick();
        last.setEnabled(false);

    } else {
        choose.setEnabled(true);
        last.setEnabled(true);
        last.doClick();
    }

    comboBox.setEnabled(false);
}

}