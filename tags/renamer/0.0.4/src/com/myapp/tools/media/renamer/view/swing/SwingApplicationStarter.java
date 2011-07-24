package com.myapp.tools.media.renamer.view.swing;

import com.myapp.tools.media.renamer.controller.IApplication;

/**
 * starts the swing application
 * 
 * @author andre
 * 
 */
public final class SwingApplicationStarter {
    
    /**
     * starts the swing application
     */
    public static void startSwingApplication() {
        @SuppressWarnings("unused")
        final IApplication app = new SwingApplication();

        
        
        // TODO REMOVE THIS STATEMENT
//        app.getRenamer().add(
//            0, 
//            true,
//            new ArrayList<IRenamable>() {{
//                for (File f : new File(
//                        "/home/andre/Desktop/testfiles/pictures/" +
//                       "20090222-ausgehen-praterdome").listFiles())
//                   add(new RenamableFile(f, app.getRenamer()));
//            }});
    }
}
