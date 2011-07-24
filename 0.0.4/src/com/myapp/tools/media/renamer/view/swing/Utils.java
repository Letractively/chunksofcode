package com.myapp.tools.media.renamer.view.swing;

import static com.myapp.tools.media.renamer.controller.Msg.msg;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;

import com.myapp.tools.media.renamer.config.IConstants;
import com.myapp.tools.media.renamer.config.IRenamerConfiguration;
import com.myapp.tools.media.renamer.controller.IApplication;
import com.myapp.tools.media.renamer.controller.Util;

/**
 * collection of useful swing-specific utils
 * 
 * @author andre
 * 
 */
class Utils implements IConstants.ISysConstants {

    static final String LINE_BREAK = System.getProperty("line.separator");

    /**
     * reads the properties from an app and returns the file content as a string
     * 
     * @param app
     *            the app to read the properties from.
     * @return the props file content as a string
     */
    static final String readProperties(IApplication app) {
        StringBuilder bui = new StringBuilder();
        String aLine = null;
        BufferedInputStream in = (BufferedInputStream)
                            app.getRenamer().getConfig().getAsStream();
        try {
            while (null != (aLine = 
                       com.myapp.tools.media.renamer.config.Utils.readLine(in)))
                bui.append(aLine).append(LINE_BREAK);

        } catch (IOException e) {
            e.printStackTrace();
            showMessageDialog(
                (Component) app.getUIComponent(),
                new JLabel(msg("Dialogs.showErrorMessage.anErrorOccured")
                        .replace("#msg#", msg(
                                    "Dialogs.readProperties.errorWhileLoading"))
                        .replace("#stacktrace#", Util.stackTraceToString(e))), 
                msg("Dialogs.showErrorMessage.errorTitle"),
                ERROR_MESSAGE);
            assert false;
            return "";

        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e1) {
                }
        }

        return bui.toString();
    }


    /**
     * creates a new customized JFilechooser
     * 
     * @param app
     *            the application context to be the chooser for
     * 
     * @return a new customized JFilechooser
     */
    static JFileChooser getCustomJFileChooser(IApplication app) {
        IRenamerConfiguration cfg = app.getRenamer().getConfig();
        JFileChooser jfc = new JFileChooser();

        // hardcoded
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
        jfc.setMultiSelectionEnabled(true);
        jfc.setControlButtonsAreShown(false);
            // hack to set details view as default, look for a abstractButton
            // in the jfc's subcomponents with the same icon as detailsbutton
            // of the JFilechooser. when found, click it programmatically.
        clickOnDetailViewButton(jfc);

        // custom
        jfc.setFileHidingEnabled( ! cfg.getBoolean(SHOW_HIDDEN_FILES));
        String last = cfg.getString(LAST_ACCESSED_FILE_PATH);
        File currentDir = (last == null || last.trim().length() <= 0)
                            ? null
                            : new File(last);
        jfc.setCurrentDirectory(currentDir);
        return jfc;
    }


    /**
     * sets the jfilechooser to "details view" (quick and dirty)
     * 
     * @param chooser
     *            the filechooser to be set to deteilsview
     */
    static void clickOnDetailViewButton(JFileChooser chooser) {
        Component c = null;
        try {
            c = findSubComponent(
                            AbstractButton.class,
                            chooser,
                            "getIcon",
                            UIManager.getIcon("FileChooser.detailsViewIcon"));
            ((AbstractButton)c).doClick();
        } catch (Exception e) {
            e.printStackTrace();
            assert false : "caught " + e;
        }
    }


    /**
     * find a component in the subcomponents of a tree. you may specify a
     * property and a class to match.
     * 
     * @param clazz
     *            the class the component must be an instance of
     * @param parent
     *            the container the component must be child of
     * @param getter
     *            the name of the the getter method
     * @param getValue
     *            the expected return value of the getter
     * @return the component that is the first matching in the component tree
     * @throws Exception
     *             various
     */
    static Component findSubComponent(Class<? extends Component> clazz,
                                      Container parent,
                                      String getter,
                                      Object getValue) throws Exception {
        // collect all subcomponents of type "clazz"
        List<Component> list = new ArrayList<Component>();
        for (Component component : parent.getComponents()) {
            if (clazz.isAssignableFrom(component.getClass()))
                list.add(clazz.cast(component));
            list.addAll(getAllChildren(clazz, (Container) component));
        }

        // compare result of getter of comp
        Method method = clazz.getMethod(getter);
        for (Component component : list) {
            Object testVal = method.invoke(component);
            if (getValue == null ? testVal == null : getValue.equals(testVal)) 
                return component;
        }

        return null;
    }


    /**
     * collects the children of an component tree being instances of given class
     * 
     * @param clazz
     *            the class to match against
     * @param container
     *            the container which children are tested
     * @return a list with all of the matching subcomponents
     */
    private static List<Component> getAllChildren(
                                            Class<? extends Component> clazz,
                                            Container container) {
        List<Component> tList = new ArrayList<Component>();
        for (Component component : container.getComponents()) {
            if (clazz.isAssignableFrom(component.getClass()))
                tList.add(clazz.cast(component));

            tList.addAll(getAllChildren(clazz, (Container) component));
        }
        return tList;
    }

    /**
     * returns the default window size, defined in the config file.
     * 
     * @param app
     *            the application context
     * 
     * @return the dimension of the window, as defined in the config
     */
    static Dimension getDefaultWindowSize(IApplication app) {
        IRenamerConfiguration cfg = app.getRenamer().getConfig();
        return new Dimension(cfg.getInt(WINDOW_DEFAULT_WIDTH),
                             cfg.getInt(WINDOW_DEFAULT_HEIGHT));
    }

    /**
     * calculates the screen-centered position for a frame
     * 
     * @param frame
     *            the frame to be centered
     * @return the position for the frame to be at the center of the screen
     */
    static Point getCenteredPosition(JFrame frame) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        return new Point(dim.width  / 2 - frame.getWidth()  / 2,
                         dim.height / 2 - frame.getHeight() / 2);
    }

    /**
     * returns the last saved position of the application window. if not set,
     * the centered position of the frame is the default position.
     * 
     * @param app
     *            the appFrame to calc the position for.
     * @return the last saved position or center, if not set.
     */
    static Point getDefaultWindowPosition(IApplication app) {
        IRenamerConfiguration cfg = app.getRenamer().getConfig();

        int x = cfg.getInt(WINDOW_POSITION_X);
        int y = cfg.getInt(WINDOW_POSITION_Y);
        
        if (x < 0 || y < 0) return getCenteredPosition(
                                        (JFrame) app.getUIComponent());
        
        return new Point(x, y);
    }

}
