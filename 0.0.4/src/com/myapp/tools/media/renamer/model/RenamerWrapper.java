package com.myapp.tools.media.renamer.model;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.filechooser.FileFilter;

import com.myapp.tools.media.renamer.config.IRenamerConfiguration;

/**
 * simply takes a renamer and passes all methods from the irenamer interface
 * to the wrapped renamer. this should be extended to write wrapper classes.
 * 
 * @author andre
 *
 */
public class RenamerWrapper implements IRenamer {

    private final IRenamer wrapped;


    protected RenamerWrapper(IRenamer beingWrapped) {
        if (beingWrapped == null) throw new NullPointerException();

        this.wrapped = beingWrapped;
    }

    /**
     * access the irenamer being wrapped by this Plainwrapper instance.
     * 
     * @return the wrapped renamer object.
     */
    protected final IRenamer unwrap() {
        return wrapped;
    }


    public List<IRenamable> add(int offset,
                                    boolean excludeDuplicates,
                                    IRenamable... newFiles) 
                    {return wrapped.add(offset, excludeDuplicates, newFiles);}
    public List<IRenamable> add(int offset,
                                    boolean excludeDuplicates,
                                    List<IRenamable> newFiles) 
                    {return wrapped.add(offset, excludeDuplicates, newFiles);}
    public List<IRenamable> append(boolean excludeDuplicates,
                                        IRenamable... filesToAppend)
                    {return wrapped.append(excludeDuplicates, filesToAppend);}
    public List<IRenamable> append(boolean excludeDuplicates, 
                                       List<IRenamable> filesToAppend) 
                    {return wrapped.append(excludeDuplicates, filesToAppend);}
    public List<IRenamable> subList(int fromIndex, int toIndex) 
                    {return wrapped.subList(fromIndex, toIndex);}
    public String previewNewAbsolutePath(IRenamable file) 
                                {return wrapped.previewNewAbsolutePath(file);}
    public void setNummerierungStart(int start)
                                        {wrapped.setNummerierungStart(start);}
    public void calculateNames()        {wrapped.calculateNames();}
    public void clear()                 {wrapped.clear();}
    public void setDatum(Date d)        {wrapped.setDatum(d);}
    public void addRenameProcessListener(IRenameProcessListener l) 
                                        {wrapped.addRenameProcessListener(l);}
    public void applyFilter(FileFilter ft)      {wrapped.applyFilter(ft);}
//    public void setDestinationDir(File d)       {wrapped.setDestinationDir(d);}
    public void move(int from, int to, int at)  {wrapped.move(from, to, at);}
    public void remove(int from, int to)        {wrapped.remove(from, to);}
    public void renameFiles() throws Exception  {wrapped.renameFiles();}
    
    
    public Object[] toArray()           {return wrapped.toArray();}
    public boolean contains(Object o)   {return wrapped.contains(o);}
    public Date getDatum()              {return wrapped.getDatum();}
    public File getDestinationDir()     {return wrapped.getDestinationDir();}
    public int getNummerierungStart()   {return wrapped.getNummerierungStart();}
    public int getSize()                {return wrapped.getSize();}
    public int indexOf(Object o)        {return wrapped.indexOf(o);}
    public boolean isEmpty()            {return wrapped.isEmpty();}
    public int lastIndexOf(Object o)    {return wrapped.lastIndexOf(o);}
    public String printActualState()    {return wrapped.printActualState();}
    public boolean removeEvery(Object o){return wrapped.removeEvery(o);}
    public boolean removeFirst(Object o){return wrapped.removeFirst(o);}
    public IRenamerConfiguration getConfig(){return wrapped.getConfig();}
    public IRenamable getElementAt(int i)   {return wrapped.getElementAt(i);}
    public Iterator<IRenamable> iterator()  {return wrapped.iterator();}
    public IRenamable remove(int index)     {return wrapped.remove(index);}

}
