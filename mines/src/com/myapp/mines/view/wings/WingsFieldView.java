package com.myapp.mines.view.wings;
//
//import java.awt.Color;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//import org.wings.SBorderLayout;
//import org.wings.SButton;
//import org.wings.SDimension;
//import org.wings.SPanel;
//
//import com.myapp.mines.controller.FieldController;
//import com.myapp.mines.controller.GameController;
//import com.myapp.mines.model.Field;
//import com.myapp.mines.model.Game;
//
///**
//a field view/controller implementation using the wings framework.
//@author andre
// */
//public class WingsFieldView extends FieldController
//                              implements ActionListener {
//
//    private SButton enterButton;
//    private SButton contextButton;
//    private SPanel panel;
//
//    /**
//    creates a new WingsFieldView for the specified model and
//    the specified gamecontroller
//    @param f the field object to represent
//    @param gameCtrl the gamecontroller to interact with
//     */
//    public WingsFieldView(Field f, GameController gameCtrl) {
//        super(f, gameCtrl);
//
//        panel = new SPanel(new SBorderLayout());
//        enterButton = new SButton();
//        contextButton = new SButton("mark");
//
//        contextButton.setPreferredSize(new SDimension(40, 10));
//        enterButton.setPreferredSize(new SDimension(40, 25));
//
//        contextButton.addActionListener(this);
//        enterButton.addActionListener(this);
//
//        panel.add(enterButton, SBorderLayout.CENTER);
//        panel.add(contextButton, SBorderLayout.SOUTH);
//
//        repaintFieldView();
//    }
//
//    @Override
//    public void actionPerformed(ActionEvent e) {
//        if (e.getSource() == enterButton)
//            enterField();
//        else if (e.getSource() == contextButton)
//            if (model.isEntered())
//                solveNeighbors();
//            else {
//                model.toggleMarked();
//                repaintFieldView();
//            }
//    }
//
//    @Override
//    public Object getGuiObject() {
//        return panel;
//    }
//
//    @Override
//    public void repaintFieldView() {
//        Color backGround = calculateBackgroundColor(model);
//        Color foreGround = calculateForegroundColor(model);
//
//        enterButton.setBackground(backGround);
//        enterButton.setForeground(foreGround);
//        enterButton.setText(model.getStatus() + " ");
//
//        contextButton.setBackground(backGround);
//        contextButton.setForeground(foreGround);
//
//        panel.setBackground(backGround);
//
//
//        if (model.isEntered()) {
//            if (model.getNeighbourBombs() == 0)
//                contextButton.setText(" ");
//            else
//                contextButton.setText("solve");
//        }
//        else {
//            if (model.isMarked())
//                contextButton.setText("undo");
//            else
//                contextButton.setText("mark");
//        }
//
//        Game g = gameViewCtrl.getModel();
//        if (g.isGameOver() || g.isGameWon()) {
//            enterButton.setEnabled(false);
//            contextButton.setEnabled(false);
//            contextButton.setText(" ");
//        }
//    }
//
//    void setToolTipText(String t) {
//        enterButton.setToolTipText(t);
//    }
//
//}
