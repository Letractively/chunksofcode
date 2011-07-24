package com.myapp.tools.media.renamer.model;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.filechooser.FileFilter;

import com.myapp.tools.media.renamer.config.IRenamerConfiguration;

/**
 * a irenamer is a list of files that will be renamed in a special manner.
 * objects of this type have several methods to manipulate the list, and some
 * methods to manipulate the name of the renamed files. <br>
 * a renamer is responsible to rename files, calculate their names, setting the
 * file's names to unique paths when duplicate files are allowed. <br>
 * IRenamer instances will also serve as ListModel instances to provide a cool
 * gui api.
 * 
 * @author andre
 */
public interface IRenamer extends Iterable<IRenamable> {

    // TODO: DATUM FLEXIBEL MACHEN

    /**
     * returns the nummerierung index of the first item in the list.
     * 
     * @return the nummerierung index of the first item in the list.
     */
    int getNummerierungStart();

    /**
     * returns the directory where the renamed files will be stored in.
     * 
     * @return the directory where the renamed files will be stored in.
     */
    File getDestinationDir();

    /**
     * returns the date that will be inserted into the name of the renamed files
     * 
     * @return the date that will be inserted into the name of the renamed files
     */
    Date getDatum();

    /**
     * calculates a preview of the new absolute path of a file in the list.
     * 
     * @param file
     *            the file which the caller wants a preview of
     * @return the new absolute path of a the file
     */
    String previewNewAbsolutePath(IRenamable file);

    /**
     * sets the nummerierung index of the first item in the list. the
     * nummerierung of the files will start at this number.
     * 
     * @param start
     *            the new start index of the list.
     */
    void setNummerierungStart(int start);

    /**
     * sets the date that will be inserted into the name of the renamed files
     * 
     * @param d
     *            the new date to set
     */
    void setDatum(Date d);

//    /**
//     * sets the destination where the files will be stored after their renaming.
//     * 
//     * @param dest
//     *            the destination directory.
//     */
//    void setDestinationDir(File dest);

    /**
     * all IRenamableFiles in the list will get their name as preview as they
     * would be renamed at the moment.
     */
    void calculateNames();

    /**
     * creates a string representation of the actual state of the renamer. that
     * string will show the files in the list in their current order.
     * 
     * @return a string describing the list structure.
     */
    String printActualState();

    /**
     * returns the current count of files in the list of the renamer
     * 
     * @return the current count of files in the list of the renamer
     */
    int getSize();

    /**
     * returns the file at this position
     * 
     * @param index
     *            the index where the file is
     * @return the file at this index
     */
    IRenamable getElementAt(int index);

    /**
     * returns the configuration for this renamer object.
     * 
     * @return the configuration for this renamer object.
     */
    IRenamerConfiguration getConfig();

    /**
     * adds files to the list, returns files that were replaced if wanted
     * 
     * @param newFiles
     *            being added to the current files list
     * @param excludeDuplicates
     *            if true, files already contained won't be added.
     * @param offset
     *            the position in the list where new files will be inserted.
     * @return null if no elements were duplicates, a list containing the
     *         duplicate items which have not been inserted.
     */
    List<IRenamable> add(int offset, boolean excludeDuplicates,
            List<IRenamable> newFiles);

    /**
     * adds files to the list, returns files that were replaced if wanted
     * 
     * @param newFiles
     *            being added to the current files list
     * @param excludeDuplicates
     *            if true, files already contained won't be added.
     * @param offset
     *            the position in the list where new files will be inserted.
     * @return null if no elements were duplicates, a list containing the
     *         duplicate items which have not been inserted.
     */
    List<IRenamable> add(int offset, boolean excludeDuplicates,
            IRenamable... newFiles);

    /**
     * adds the files to the end of the list
     * 
     * @param excludeDuplicates
     *            if duplicates should be removed
     * @param filesToAppend
     *            the new file(s) to append
     * @return null if no elements were duplicates, a list containing the
     *         duplicate items which have not been inserted.
     */
    List<IRenamable> append(boolean excludeDuplicates,
            IRenamable... filesToAppend);

    /**
     * adds the files to the end of the list
     * 
     * @param excludeDuplicates
     *            if duplicates should be removed
     * @param filesToAppend
     *            the new file(s) to append
     * @return null if no elements were duplicates, a list containing the
     *         duplicate items which have not been inserted.
     */
    List<IRenamable> append(boolean excludeDuplicates,
            List<IRenamable> filesToAppend);

    /**
     * moves files in the list to another position.
     * 
     * @param from
     *            the index of the first item that is being moved
     * @param count
     *            the count of items that will be moved
     * @param insertAtIndex
     *            the position where the first item will be inserted; the other
     *            items of the move operation will be inserted after this item.
     */
    void move(int from, int count, int insertAtIndex);

    /**
     * removes the item at the given index
     * 
     * @param index
     *            the index of the item to remove
     * @return the removed file
     */
    IRenamable remove(int index);

    /**
     * removes a range of fields from the renamer. the removed range is
     * inclusive "from" and exclusive "to"
     * 
     * @param from
     *            the index of the first element to be removed
     * @param count
     *            the count of elements to remove
     */
    void remove(int from, int count);

    /**
     * removes every object that is equal to the given object from the list.
     * 
     * @param o
     *            the item to remove
     * @return true if any items were removed
     */
    boolean removeEvery(Object o);

    /**
     * removes the first object that is equal to the given object from the list.
     * 
     * @param o
     *            the item to remove
     * @return true if any objects were removed
     */
    boolean removeFirst(Object o);

    /**
     * Returns <tt>true</tt> if this list contains the specified element. More
     * formally, returns <tt>true</tt> if and only if this list contains at
     * least one element <tt>e</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
     * 
     * @param o
     *            element whose presence in this list is to be tested
     * @return <tt>true</tt> if this list contains the specified element
     * @throws ClassCastException
     *             if the type of the specified element is incompatible with
     *             this list (optional)
     * @throws NullPointerException
     *             if the specified element is null and this list does not
     *             permit null elements (optional)
     */
    boolean contains(Object o);

    /**
     * Returns <tt>true</tt> if this list contains no elements.
     * 
     * @return <tt>true</tt> if this list contains no elements
     */
    boolean isEmpty();

    /**
     * Returns an array containing all of the elements in this list in proper
     * sequence (from first to last element).
     * 
     * <p>
     * The returned array will be "safe" in that no references to it are
     * maintained by this list. (In other words, this method must allocate a new
     * array even if this list is backed by an array). The caller is thus free
     * to modify the returned array.
     * 
     * <p>
     * This method acts as bridge between array-based and collection-based APIs.
     * 
     * @return an array containing all of the elements in this list in proper
     *         sequence
     * @see Arrays#asList(Object[])
     */
    Object[] toArray();

    /**
     * Removes all of the elements from this list (optional operation). The list
     * will be empty after this call returns.
     */
    void clear();

    /**
     * Returns the index of the first occurrence of the specified element in
     * this list, or -1 if this list does not contain the element. More
     * formally, returns the lowest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     * 
     * @param o
     *            element to search for
     * @return the index of the first occurrence of the specified element in
     *         this list, or -1 if this list does not contain the element
     */
    int indexOf(Object o);

    /**
     * Returns the index of the last occurrence of the specified element in this
     * list, or -1 if this list does not contain the element. More formally,
     * returns the highest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     * 
     * @param o
     *            element to search for
     * @return the index of the last occurrence of the specified element in this
     *         list, or -1 if this list does not contain the element
     */
    int lastIndexOf(Object o);

    /**
     * Returns a view of the portion of this list between the specified
     * <tt>fromIndex</tt>, inclusive, and <tt>toIndex</tt>, exclusive. (If
     * <tt>fromIndex</tt> and <tt>toIndex</tt> are equal, the returned list is
     * empty.) The returned list is backed by this list, so non-structural
     * changes in the returned list are reflected in this list, and vice-versa.
     * The returned list supports all of the optional list operations supported
     * by this list.
     * <p>
     * 
     * This method eliminates the need for explicit range operations (of the
     * sort that commonly exist for arrays). Any operation that expects a list
     * can be used as a range operation by passing a subList view instead of a
     * whole list. For example, the following idiom removes a range of elements
     * from a list:
     * 
     * <pre>
     * list.subList(from, to).clear();
     * </pre>
     * 
     * Similar idioms may be constructed for <tt>indexOf</tt> and
     * <tt>lastIndexOf</tt>, and all of the algorithms in the
     * <tt>Collections</tt> class can be applied to a subList.
     * <p>
     * 
     * The semantics of the list returned by this method become undefined if the
     * backing list (i.e., this list) is <i>structurally modified</i> in any way
     * other than via the returned list. (Structural modifications are those
     * that change the size of this list, or otherwise perturb it in such a
     * fashion that iterations in progress may yield incorrect results.)
     * 
     * @param fromIndex
     *            low endpoint (inclusive) of the subList
     * @param toIndex
     *            high endpoint (exclusive) of the subList
     * @return a view of the specified range within this list
     * @throws IndexOutOfBoundsException
     *             for an illegal endpoint index value (
     *             <tt>fromIndex &lt; 0 || toIndex &gt; size ||
     *         fromIndex &gt; toIndex</tt>)
     */
    List<IRenamable> subList(int fromIndex, int toIndex);

    /**
     * applies a filename-extension filter to the files in the list.
     * 
     * @param filter
     *            the filter object to apply
     */
    void applyFilter(FileFilter filter);

    /**
     * starts the renaming process for the files in the list.
     * 
     * @throws Exception
     *             if sth. went wrong during the rename process, e.g. io-stuff
     */
    void renameFiles() throws Exception;

    /**
     * adds a processlistener to this renamer that will be notified during the
     * renaming process.
     * 
     * @param l
     *            the processlistener to add.
     */
    void addRenameProcessListener(IRenameProcessListener l);
}
