package com.myapp.util;

import static java.lang.System.err;
import static java.lang.System.in;
import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class SuperMain {

    private static final List<Class<?>> mainClasses;

    static {
        ArrayList<Class<?>> l = new ArrayList<Class<?>>();

        /* put your classes here: */
        l.add(com.myapp.util.regex.RegexTester.class);
        l.add(Test.class);

        mainClasses = Collections.unmodifiableList(l);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        out.println("Please select main class to start:");
        for (int i = 0; i < mainClasses.size(); i++) {
            out.println(" [" + i + "] " + mainClasses.get(i).getName());
        }

//        System.err.println("main args: " + args.length);
//        System.err.println("main args: " + Arrays.toString(args));

        Class<?> chosen = null;

        for (int tries = 0;; tries++) {
            if (tries > 2)
                throw new RuntimeException("more than " + tries
                        + "invalid tries!");

            out.println();
            out.println("Enter the number of class to start and press enter:");

            try {
                int i = Integer.parseInt(readLineFromStdIn());
                out.println("you chose: " + i);
                chosen = mainClasses.get(i);
                break;
            } catch (Exception e) {
                e.printStackTrace(err);
            }
        }

        invokeMain(chosen, args);
    }

    private static final void invokeMain(Class<?> c, String[] args) {
//        System.err.println("invokeMain args: " + args.length);
//        System.err.println("invokeMain args: " + Arrays.toString(args));
        
        Method[] methods = c.getMethods();
        Method main = null;

        for (Method m : methods) {
            if (m.getName().equals("main")
                    && Modifier.isStatic(m.getModifiers())
                    && m.getParameterTypes()[0] == String[].class
                    && m.getReturnType() == void.class) {
                main = m;
                break;
            }
        }

        if (main == null) {
            out.println("available methods:");
            for (Method m : c.getMethods())
                out.println(m);

            throw new RuntimeException("no main method found for " + c + "!");
        }

        out.println("please enter the arguments and press enter");
        if (args != null && args.length > 0) {
            out.println("press enter to continue with following arguments:");
            out.println(" ("+args.length+" args)  " + Arrays.toString(args));
        }
        
        String fromStdIn = readLineFromStdIn().trim();
        if (fromStdIn.length() > 0) {
            String[] args4main = fromStdIn.split(" ");
            if (args4main.length > 0) {
                System.out.println("read args from std in: " + Arrays.toString(args));
                args = args4main;
            }
        }

//        System.err.println("invoke with args: " + args.length);
//        System.err.println("invoke with args: " + Arrays.toString(args));
        

        try {
            main.invoke(null, (Object) args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String readLineFromStdIn() {
        BufferedReader rdr = null;
        try {
            rdr = new BufferedReader(new InputStreamReader(in));
            return rdr.readLine();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

final class Test {
    public static void main(String... strings) {
        out.println("this is a test class for supermain...");
        out.println("your entered arguments: " + Arrays.deepToString(strings));
    }
}
