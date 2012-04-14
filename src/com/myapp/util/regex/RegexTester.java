/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myapp.util.regex;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author andre
 */
public class RegexTester {

    private JFrame window;
    private JTextArea text;
    private JTextArea regex;
    private JTextArea result;
    private JButton find;

    public RegexTester() {
        window = new JFrame("regex tester");
        text = new JTextArea();
        regex = new JTextArea();
        result = new JTextArea();
        find = new JButton("find");
        find.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                result.setText("");
                String rslt = findAll();
                result.setText(rslt);
            }

        });

        JPanel grid = new JPanel(new GridLayout(0, 1));

        grid.add(new JScrollPane(text));
        grid.add(new JScrollPane(regex));
        grid.add(new JScrollPane(result));

        text.setBorder(BorderFactory.createTitledBorder("text"));
        regex.setBorder(BorderFactory.createTitledBorder("regex"));
        result.setBorder(BorderFactory.createTitledBorder("result"));


        window.getContentPane().add(grid, BorderLayout.CENTER);
        window.getContentPane().add(find, BorderLayout.NORTH);

        initText();

        window.setPreferredSize(new Dimension(800,600));
        window.pack();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }

    public static void main(String... args) {
        new RegexTester();
    }

    private String findAll() {
        String regx = regex.getText();
        String txt = text.getText();

        Pattern p = Pattern.compile(regx, Pattern.COMMENTS);
        Matcher m = p.matcher(txt);


        if (!m.find()) {
            return "nothing found!";
        }

        StringBuilder rslt = new StringBuilder();
        int i = 0;

        while (m.find()) {
            rslt.append(i++ + ". " + m.group());
            rslt.append("\n");
        }

        return rslt.toString();
    }

    private void initText() {
        try {
            BufferedReader rdr =
                    new BufferedReader(
                    new InputStreamReader(
                    new FileInputStream(
                    new File("/home/andre/Desktop/jqueryorig.js"))));

            StringBuilder bui=new StringBuilder();
            String line = null;
            while((line = rdr.readLine())!=null) {
                bui.append(line);
            }
            text.setText(bui.toString());

        } catch (IOException ex) {
            return;
        }
    }

}
