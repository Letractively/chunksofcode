package com.myapp.tools.media.renamer.model;

import static com.myapp.tools.media.renamer.controller.Util.arrayToList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Logger;

import javax.swing.filechooser.FileFilter;

import com.myapp.tools.media.renamer.config.Config;
import com.myapp.tools.media.renamer.config.IConstants;
import com.myapp.tools.media.renamer.config.IRenamerConfiguration;
import com.myapp.tools.media.renamer.controller.Log;
import com.myapp.tools.media.renamer.controller.Msg;

/**
 * encapsulates the list logic of the renamer and implements the listmodel
 * and tablemodel interface
 * 
 * @author andre
 */
public abstract class AbstractRenamer implements IRenamer, IConstants.ISysConstants {

    static final Logger L = Log.defaultLogger();

    private final List<IRenamable> files; 
    private final IRenamerConfiguration config;
    private Set<IRenameProcessListener> listeners;

    /**
     * default constructor, protected access.
     */
    protected AbstractRenamer() {
        files = new ArrayList<IRenamable>();
        config = Config.getInstance();
    }



    @Override
    public final List<IRenamable> add(int offset,
                                      boolean excludeDuplicates,
                                      List<IRenamable> newFiles) {
        return addImpl(offset, excludeDuplicates, newFiles);
    }

    @Override
    public final List<IRenamable> add(int offset,
                                      boolean excludeDuplicates,
                                      final IRenamable... filesToAppend) {
        return addImpl(offset, excludeDuplicates, arrayToList(filesToAppend));
    }

    @Override
    public final List<IRenamable> append(boolean excludeDuplicates,
                                         IRenamable... filesToAppend) {
        return addImpl( getSize(),
                        excludeDuplicates,
                        arrayToList(filesToAppend)  );
    }

    @Override
    public final List<IRenamable> append(boolean excludeDuplicates,
                                             List<IRenamable> fileList) {
        return addImpl(getSize(), excludeDuplicates, fileList);
    }

    /**
     * the implementation of all add actions.
     * 
     * @param offset
     *            the index of the first element to be inserted
     * @param excludeDuplicates
     *            if true, there will be no duplicate elements in the list after
     *            inserting
     * @param newFiles
     *            the list of files to be added
     * @return null if no elements were duplicates, a list containing the
     *         duplicate items which have not been inserted.
     */
    private synchronized List<IRenamable> addImpl(int offset,
                                                  boolean excludeDuplicates,
                                                  List<IRenamable> newFiles) {
        synchronized (files) {
            List<IRenamable> tmpList = new ArrayList<IRenamable>(0);

            if (excludeDuplicates) {
                Iterator<IRenamable> i;

                //make sure there are no duplicates in the existing list
                Set<IRenamable> uniqueCheck = new HashSet<IRenamable>();
                for (i = files.iterator(); i.hasNext();)
                    if ( ! uniqueCheck.add(i.next())) i.remove();


                //make sure there are no duplicates in the parameter list
                uniqueCheck.clear();
                for (i = newFiles.iterator(); i.hasNext();)
                    if ( ! uniqueCheck.add(i.next())) i.remove();


                //remove files from existing list which will be added
                for (i = newFiles.iterator(); i.hasNext();) {
                    IRenamable f = i.next();
                    if (removeEvery(f) && ( ! tmpList.contains(f)))
                        tmpList.add(f);
                }
            }

            /*
             * so far the duplicate files have been removed from the newFiles
             * list and from the existing files list, if there were any AND
             * excludeDuplicates is true.
             */

            // validate offset and add to existing list
            if (offset < 0)
                offset = 0;
            else if (offset > getSize())
                offset = getSize();

            files.addAll(offset, newFiles);

            L.info(Msg.msg("AbstractRenamer.elementsAdded")
                        .replace("#atPos#", Integer.toString(offset))
                        .replace("#allowDuplicates#",  Boolean.toString( ! excludeDuplicates))
                        .replace("#count#", Integer.toString(newFiles.size())));
            
            return tmpList.isEmpty() ? null : tmpList;
        }
    }

    @Override
    public void move(int from, final int count, int insertAt) {
        synchronized (files) {
            if (count == 0) return; // nothing to do

            // on a list: a,b,c,d,e,f,g we cannot move b,c,d to position c
            if (insertAt > from && insertAt < (from + count))
                throw new IllegalArgumentException("from='" + from + "', " + 
                                               "count='" + count + "', " + 
                                               "insertAt='" + insertAt + "'");      
            if (insertAt > files.size()) insertAt = files.size();
            
            else if (insertAt < 0) insertAt = 0;

            if (insertAt == from) return; // nothing to do

            Stack<IRenamable> stack = new Stack<IRenamable>();

            // grab files, note lists leftshifting
            for (int i = from, n = from + count; i < n; i++)
                stack.push(files.remove(from));

            // calculate insert position
            if (insertAt > from) insertAt -= count;

            // insert files, note lists rightshifting
            while ( ! stack.empty())
                files.add(insertAt, stack.pop());
            
            L.info(Msg.msg("AbstractRenamer.elementsMoved")
                            .replace("#fromPos#", Integer.toString(from))
                            .replace("#count#", Integer.toString(count))
                            .replace("#toPos#", Integer.toString(insertAt)));
        }
    }

    @Override
    public final boolean removeEvery(Object o) {
        synchronized (files) {
            boolean removedAtLeastOne = false;

            for (boolean yes = removeFirst(o); yes; yes = removeFirst(o))
                removedAtLeastOne = true;

            return removedAtLeastOne;
        }
    }

    @Override
    public final boolean removeFirst(Object obj) {
        synchronized (files) {
            int i = files.indexOf(obj);
            if (i < 0) return false;

            files.remove(obj);
            return true;
        }
    }

    @Override
    public final void remove(final int from, final int count) {
        if (from > getSize())
            throw new RuntimeException(
                           "from(" + from + ") > getSize()(" + getSize() + ")");
        if (0 == count)
            return; //nothing to do

        synchronized (files) {
            for (int i = 0; i < count; i++)
                remove(from); // using leftshifting
        }
    }

    @Override
    public void applyFilter(FileFilter filterText) {
        synchronized (files) {
            List<Integer> toRm = new ArrayList<Integer>();
            
            for (int i = 0, s = getSize(); i < s; i++) {
                IRenamable f =  getElementAt(i);
                if (filterText.accept(f.getSourceObject())) continue;
                
                toRm.add(new Integer(i));
            }
            
            Collections.reverse(toRm); // from behind, because of leftshifting
            
            for (Integer i : toRm) files.remove(i.intValue());
        }
    }

    /* *********************** list delegate methods ************************ */




    @Override
    public final Object[] toArray() {
        return files.toArray();
    }

    @Override
    public final int indexOf(Object o) {
        return files.indexOf(o);
    }

    @Override
    public final boolean isEmpty() {
        return files.isEmpty();
    }

    @Override
    public final int lastIndexOf(Object o) {
        return files.lastIndexOf(o);
    }

    @Override
    public final boolean contains(Object o) {
        return files.contains(o);
    }

    @Override
    public final List<IRenamable> subList(int from, int to) {
        return files.subList(from, to);
    }

    @Override
    public final Iterator<IRenamable> iterator() {
        return new Iterator<IRenamable>() {
            private int position = 0;
            public boolean hasNext() {return position < files.size();}
            public IRenamable next() {return files.get(position++);}
            public void remove() {throw new UnsupportedOperationException();}
        };
    }

    @Override
    public final void clear() {
        files.clear();
        L.info("All elements were removed.");
    }

    @Override
    public final IRenamable remove(int index) {
        IRenamable f = files.remove(index);
        L.info(Msg.msg("AbstractRenamer.elementRemoved")
                  .replace("#element#", f.getSourceObject().getAbsolutePath()));
        return f;
    }

    @Override
    public final IRenamable getElementAt(int index) {
        return files.get(index);
    }

    @Override
    public final int getSize() {
        return files.size();
    }

    @Override
    public IRenamerConfiguration getConfig() {
        return config;
    }
    
    public void renameFiles() {
        if (isEmpty()) return; // nothing to do
        
        synchronized (files) {
            validateRenameProcess();
            
            notifyStart();
            
            for (IRenamable f : files) {
                notifyFileStart(f);
                
                try {
                    boolean ok = f.renameFile(true);
                    
                    if (ok)
                        notifyFileSuccess();
                    
                } catch (Exception t) {
                    notifyFailed(t, f);
                    return;
                }
                
            }

            notifyFinished();
            files.clear();
        }
    }

    private void validateRenameProcess() {
        synchronized (files) {
        for (IRenamable f : files) {
            String currentName = f.getOldName();
            String currentParentPath = f.getOldParentAbsolutePath();
            System.out.println("AbstractRenamer.validateRenameProcess() "+currentParentPath + File.separator + currentName);    
        }
        }
    }



    public void addRenameProcessListener(IRenameProcessListener l) {
        if (listeners == null)
            synchronized (this) {
                if (listeners == null)
                    listeners = new HashSet<IRenameProcessListener>();
            }
        listeners.add(l);
    }

    
    private void notifyFileStart(IRenamable f) {
        for (IRenameProcessListener l : listeners) l.processFileStart(f);
    }
    
    private void notifyFinished() {
        for (IRenameProcessListener l : listeners) l.processFinished();
    }
    
    private void notifyStart() {
        for (IRenameProcessListener l : listeners) l.processStarting(this);
    }
    
    private void notifyFileSuccess() {
        for (IRenameProcessListener l : listeners) l.processFileSuccess();
    }
    
    private void notifyFailed(Throwable t, IRenamable f) {
        for (IRenameProcessListener l : listeners) l.processFailed(t, f);
    }

    /* ************************* utils, helper methods ********************** */



    @Override
    public final String printActualState() {
        int i = 0;
        String ls = System.getProperty("line.separator");
        StringBuilder bui = new StringBuilder();
        bui.append(ls);

        for (IRenamable f : this) {
            bui.append(i);
            bui.append(". ");
            bui.append(f);
            bui.append(ls);
            i++;
        }

        return bui.toString();
    }

    /**
     * returns the internal files list as string
     * 
     * @return the internal files list as string
     */
    String filesToString() {
        return files.toString();
    }
}
