package com.myapp.tools.media.renamer.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.Test;

import com.myapp.tools.media.renamer.config.IConstants.ISysConstants;
import com.myapp.tools.media.renamer.controller.Log;
import com.myapp.tools.media.renamer.model.DummyRenamable;
import com.myapp.tools.media.renamer.model.IRenamable;
import com.myapp.tools.media.renamer.model.IRenamer;
import com.myapp.tools.media.renamer.model.Renamer;


@SuppressWarnings("serial")
public class RenamerTest {
    private static final Logger L = Log.defaultLogger();
    private static final boolean verbose = false;
    
    private static List<IRenamable> FILES = Collections.unmodifiableList(
        new ArrayList<IRenamable>() {{
            for (int i = 0; i < 10; i++)
                add(new DummyRenamable());
        }}
    );

    private static final int TEST_FILE_SIZE = FILES.size();

    static { // suppress log output
        AbstractRenamer.L.setFilter(new Filter() {
            public boolean isLoggable(LogRecord record) {
                return false;
            }
        });
    }

    @ Test
    public void testAdd() {
        try {
            final IRenamer r = Renamer.getInstance();
            DummyRenamable fritz = new DummyRenamable("fritz");

            {
                saveState();
                assertTrue(r.isEmpty());
                assertEquals(savedState().size(), r.getSize());

                r.add(999, true, getNewTestFileList());
                assertTrue(TEST_FILE_SIZE == r.getSize());
                for (IRenamable f : r)
                    assertEquals(r.indexOf(f), FILES.indexOf(f));
            }

            {
                saveState();
                r.clear();
                assertTrue(r.isEmpty());

                saveState();
                r.add(-5, true, getNewTestFileList());//add 10 files
                assertEquals(TEST_FILE_SIZE, r.getSize());

                saveState();
                r.add(0, true, getNewTestFileList());//same 10, replace=true
                assertEquals(FILES.size(), r.getSize());

                saveState();
                r.add(0, false, getNewTestFileList());//same 10, replace=false
                assertEquals(r.getSize(), FILES.size() * 2);

                saveState();
                r.add(0, true, fritz); // add fritz at 0
                assertEquals(r.indexOf(fritz), 0);
                assertEquals(r.getElementAt(0), fritz);
                assertEquals( ((DummyRenamable)r.getElementAt(0)).number, fritz.number);
            }

            {
                r.clear();
                saveState();

                r.add(0, false, getNewTestFileList()); // add 10 files

                saveState();
                r.add(0, false, fritz);// add fritz at 0

                assertEquals(FILES.size() + 1, r.getSize());
                assertEquals(r.getElementAt(0), fritz);
                assertEquals(r.indexOf(fritz), 0);

                saveState();
                r.append(true, fritz); // add fritz to end, replace=true

                assertEquals(FILES.size() + 1, r.getSize());
                assertEquals(r.getElementAt(r.getSize() - 1), fritz);
                assertEquals(r.indexOf(fritz), r.getSize() - 1);

                int size = FILES.size() -1;
                for (int i = 0; i < size - 1; i++)
                    assertEquals(FILES.get(i), r.getElementAt(i));
            }

            {
                r.clear();
                add(0,  false, new DummyRenamable("a"));     
                add(1,  false, new DummyRenamable("LAST"));
                add(1,  false, new DummyRenamable("a"));
                add(0,  false, new DummyRenamable("FIRST"));
                assertEquals(r.getElementAt(0), new DummyRenamable("FIRST"));
                assertEquals(r.getElementAt(r.getSize() - 1),
                                                        new DummyRenamable("LAST"));

                add(99, true, new DummyRenamable("a")); //replace files "a"
                assertEquals(3, r.getSize());
                add(99,
                    true,
                    new DummyRenamable("a"),
                    new DummyRenamable("a"),
                    new DummyRenamable("a")); //replace files "a"

                assertEquals(3, r.getSize());
            }

            {
                r.clear();
                add(0,  true, new DummyRenamable("ALONE"));
                add(1,  true, new DummyRenamable());
                add(2,  true, new DummyRenamable("ALONE"));
                assertEquals(2, r.getSize());
            }

            {
                r.clear();
                add(0,  false, new DummyRenamable("foo")); // foo 1
                add(1,  false, new DummyRenamable("FIRST"));
                add(2,  false, new DummyRenamable("foo")); // foo 2
                assertEquals(3, r.getSize());

                add(2,  false, new DummyRenamable(), new DummyRenamable(), new DummyRenamable());
                assertEquals(6, r.getSize());
                assertEquals(r.getElementAt(0), new DummyRenamable("foo"));
                assertEquals(r.getElementAt(r.getSize() - 1), new DummyRenamable("foo"));

                add(99, false, new DummyRenamable("foo"), new DummyRenamable("foo")); // foo 3, 4
                assertEquals(8, r.getSize());

                // replace all 4 foo through the one in the param and add fritz
                add(99, true, new DummyRenamable("foo"), new DummyRenamable("fritz"));
                assertEquals(6, r.getSize());
                assertEquals(r.getElementAt(0), new DummyRenamable("FIRST"));
                assertEquals(r.getElementAt(r.getSize() - 1),
                                                new DummyRenamable("fritz"));
            }

            {
                r.clear();
                List<IRenamable> l = getNewTestFileList();
                Collections.reverse(l);

                add(0, false, getNewTestFileList().toArray()); // add 10 files

                //append same 10 in reverse order
                add(99, false, l.toArray());
                assertEquals(r.getElementAt(r.getSize()-1), FILES.get(0));
                assertEquals(r.getSize(), 20);

                add(999, true, l.toArray());//add reverse list with replace=true
                assertEquals(r.getElementAt(0), FILES.get(FILES.size()-1));
                assertEquals(r.getSize(), 10);
                assertArrayEquals(r.subList(0,
                                            r.getSize()).toArray(),
                                            l.toArray());
            }           

            if (verbose) L.info("--OK--");
        } catch (AssertionError ae) {
            throwCustomAssertionError(ae);
        }
    }

    @ Test
    public void testMove() {
        final IRenamer r = Renamer.getInstance();
        final DummyRenamable d0 = new DummyRenamable("0"),
                    d1 = new DummyRenamable("1"),
                    d2 = new DummyRenamable("2"),
                    d3 = new DummyRenamable("3"),
                    d4 = new DummyRenamable("4"),
                    d5 = new DummyRenamable("5");
        try {

            {
                r.clear();
                add(0, false, d0,d1,d2,d3,d4,d5); // add 6 files
                saveState();

                r.move(1, 2, 4); // item d1,d2 to pos where d4 is
                assertEquals(d0, r.getElementAt(0));
                assertEquals(d3, r.getElementAt(1));
                assertEquals(d1, r.getElementAt(2));
                assertEquals(d2, r.getElementAt(3));
                assertEquals(d4, r.getElementAt(4));
                assertEquals(d5, r.getElementAt(5));
            }

            {
                r.clear();
                add(0, false, d0,d1,d2,d3,d4,d5); // add 6 files
                saveState();

                r.move(3, 2, 1); // item d3,d4 to pos where d1 is
                assertEquals(d0, r.getElementAt(0));
                assertEquals(d3, r.getElementAt(1));
                assertEquals(d4, r.getElementAt(2));
                assertEquals(d1, r.getElementAt(3));
                assertEquals(d2, r.getElementAt(4));
                assertEquals(d5, r.getElementAt(5));
            }

            {
                r.clear();
                add(0, false, d0,d1,d2,d3,d4,d5); // add 6 files
                saveState();

                r.move(3, 3, 0); // item d3,d4,d5 to pos where d0 is
                assertEquals(d3, r.getElementAt(0));
                assertEquals(d4, r.getElementAt(1));
                assertEquals(d5, r.getElementAt(2));
                assertEquals(d0, r.getElementAt(3));
                assertEquals(d1, r.getElementAt(4));
                assertEquals(d2, r.getElementAt(5));
            }

            {
                r.clear();
                add(0, false, d0,d1,d2,d3,d4,d5); // add 6 files
                saveState();

                r.move(0, 3, 999); // item d0,d1,d2 to end
                assertEquals(d3, r.getElementAt(0));
                assertEquals(d4, r.getElementAt(1));
                assertEquals(d5, r.getElementAt(2));
                assertEquals(d0, r.getElementAt(3));
                assertEquals(d1, r.getElementAt(4));
                assertEquals(d2, r.getElementAt(5));
            }

            {
                r.clear();
                add(0, false, getNewTestFileList().toArray()); // add 10 files
                add(99, false, d0,d1,d2,d3,d4,d5); // add 6 files
                saveState();

                r.move(0, 10, 999); // first 10 to end
                assertEquals(d0, r.getElementAt(0));
                assertEquals(d3, r.getElementAt(3));
                assertEquals(d5, r.getElementAt(5));
                assertEquals(FILES.get(0), r.getElementAt(6));
                assertEquals(FILES.get(FILES.size()-1),
                             r.getElementAt(r.getSize()-1));
                assertArrayEquals(r.subList(6, r.getSize()).toArray(),
                                  getNewTestFileList().toArray());
                assertEquals(16, r.getSize());
            }

            {
                r.clear();
                add(0, false, getNewTestFileList().toArray()); // add 10 files
                add(99, false, d0,d1,d2,d3,d4,d5); // add 6 files
                saveState();

                r.move(10, 6, 0); // last 6 items to top
                assertEquals(d0, r.getElementAt(0));
                assertEquals(d3, r.getElementAt(3));
                assertEquals(d5, r.getElementAt(5));
                assertEquals(FILES.get(0), r.getElementAt(6));
                assertEquals(FILES.get(FILES.size()-1),
                             r.getElementAt(r.getSize()-1));
                assertArrayEquals(r.subList(6, r.getSize()).toArray(),
                                  getNewTestFileList().toArray());
            }

            {
                r.clear();
                add(99, false, getNewTestFileList().toArray()); // add 10 files


                saveState();
                try {
                    r.move(2, 3, 3); // move 2,3,4 to position 3 (illegal)
                    fail();
                } catch (RuntimeException e) {/*expected*/}
                assertArrayEquals(r.toArray(), getNewTestFileList().toArray());


                saveState();
                try {
                    r.move(-1, 3, 3); // invalid "from" parameter
                    fail();
                } catch (RuntimeException e) {/*expected*/}
                assertArrayEquals(r.toArray(), getNewTestFileList().toArray());


                saveState();
                try {
                    r.move(99, 3, 3); // invalid "from" parameter
                    fail();
                } catch (RuntimeException e) {/*expected*/}
                assertArrayEquals(r.toArray(), getNewTestFileList().toArray());


                saveState();
                r.move(0, 3, 0); // should do nothing
                assertArrayEquals(r.toArray(), getNewTestFileList().toArray());
            }
            if (verbose) L.info("--OK--");
        } catch (AssertionError ae) {
            throwCustomAssertionError(ae);
        }
    }

    @Test
    public void testRemove() {
        final IRenamer r = Renamer.getInstance();
        try {

            {
                r.clear();
                r.getConfig().setCustomProperty(ISysConstants.REPLACE_ORIGINAL_FILES, "true");

                add(0, true, getNewTestFileList().toArray());
                add(0, false, getNewTestFileList().toArray());
                add(0, false, getNewTestFileList().toArray());
                assertEquals(30, r.getSize());

                saveState();
                r.removeEvery(FILES.get(0));

                assertEquals(27, r.getSize());
                assertEquals(-1, r.indexOf(FILES.get(0)));
                assertFalse(r.contains(FILES.get(0)));
            }

            {
                r.clear();
                add(0, true, getNewTestFileList().toArray());
                add(0, false, getNewTestFileList().toArray());
                add(0, false, getNewTestFileList().toArray());          
                saveState();

                r.removeFirst(FILES.get(2));
                assertEquals(29, r.getSize());
                assertEquals(FILES.get(3), r.getElementAt(2));
                // i was 2 before, should now be 10 -1 further 
                assertEquals( (2 + 10 - 1), r.indexOf(FILES.get(2)));
            }

            {
                r.clear();
                List<IRenamable> expected = new ArrayList<IRenamable>();
                for (int i = 0; i < 30; i++) {
                    DummyRenamable d = new DummyRenamable("testdummy-" + i);
                    r.append((i % 2 == 0), d); //try true/false : files are uniq
                    expected.add(d);
                }
                expected = Collections.unmodifiableList(expected);
                assertEquals(30, r.getSize());
                saveState();

                r.remove(9, 10); // rm 10th element - 19th element (10 elements)

                assertEquals(20, r.getSize());
                for (int i = 9; i < 18; i++) {
                    assertFalse(r.contains(expected.get(i)));   
                }
            }

            if (verbose) L.info("--OK--");
        } catch (AssertionError ae) {
            throwCustomAssertionError(ae);
        }
    }






    //////////// HELPER METHODS //////////////////////////////////////





    private static void throwCustomAssertionError(AssertionError ae) {
        AssertionError e =  new AssertionError("CAUSE='"+ae+"'" + 
                                      System.getProperty("line.separator") + 
                                      "[" + stateToString() + "]");
        e.setStackTrace(ae.getStackTrace());
        throw e;
    }

    private static String add(int pos,
                              boolean exclude,
                              final Object... files) {
        StringBuilder bui = new StringBuilder();
        bui.append("add(")
            .append(pos).append(", ")
            .append(exclude).append(", ")
            .append(Arrays.toString(files)).append(");");

        for (int i = bui.length(); i < 50; i++)
            bui.append(" ");

        // save state and add the files
        saveState();
        
        IRenamer ir = Renamer.getInstance();
        List<IRenamable> flist = new ArrayList<IRenamable>(){{
            for (Object o : files)
                add((IRenamable) o);
        }};
        
        ir.add(pos, exclude, flist);

        bui.append(((Renamer)Renamer.getInstance()).filesToString());

        return bui.toString();
    }


    private static List<IRenamable> getNewTestFileList() {
        return new ArrayList<IRenamable>() {{
            for (IRenamable f : FILES)
                add(((DummyRenamable) f).clone());
        }};
    }

    private static void saveState() {
        SAVEDSTATE = new ArrayList<IRenamable>() {{
            for (IRenamable f : Renamer.getInstance())
                add(f);
        }};
    }



    private static List<IRenamable> savedState() {return SAVEDSTATE;}
    private static List<IRenamable> SAVEDSTATE = 
                                            new ArrayList<IRenamable>();

    private static String stateToString() {
        IRenamer actual = Renamer.getInstance();
        StringBuilder bui = new StringBuilder();
        int pos = 0;
        final int blanks1 = 5, blanks2 = 35;

        bui.append(System.getProperty("line.separator"));
        bui.append("actual state:");
        bui.append(System.getProperty("line.separator"));

        pos = bui.length();
        bui.append("i.");
        fillBlanks(bui, blanks1 - (bui.length() - pos));
        bui.append(" | ");

        bui.append("actual");
        fillBlanks(bui, blanks2 - (bui.length() - pos));
        bui.append(" | ");

        bui.append("last saved state");
        bui.append(System.getProperty("line.separator"));
        bui.append("------+-----------------------------+-----------------");
        bui.append(System.getProperty("line.separator"));

        int loopcount = SAVEDSTATE.size();
        if (loopcount < actual.getSize()) 
            loopcount = actual.getSize();

        for (int i = 0; i < loopcount; i++) {
            pos = bui.length();
            bui.append(i + ". ");
            fillBlanks(bui, blanks1 - (bui.length() - pos));
            bui.append(" | ");

            if (i < actual.getSize()) 
                bui.append(actual.getElementAt(i));
            else                    
                bui.append("-----------");

            fillBlanks(bui, blanks2 - (bui.length() - pos));
            bui.append(" | ");

            if (i < SAVEDSTATE.size())  
                bui.append(SAVEDSTATE.get(i));
            else                        
                bui.append("---------- ");

            bui.append(System.getProperty("line.separator"));
        }

        String s = bui.toString();
        if (verbose) L.info(s);
        return s;
    }

    private static void fillBlanks(StringBuilder bui, int count) {
        for (int i = 0; i < count; i++)
            bui.append(' ');
    }
}
