package com.myapp.tools.web.httpproxy;


/**
 *
 * @author andre
 */
public class Main {

    /** @param args the command line arguments */
    public static void main(String[] args) {
        if (args.length <= 0)
            ProxyStarter.main("12345", "www.google.at", "80");
        
        else if (args.length == 3)
            ProxyStarter.main(args[0], args[1], args[3]);
        
        else
            System.out.println("arg 1 must be the port to listen to.\n" +
            		"arg 2 must be the host name to redirect to\n" +
            		"arg 3 must be the port to redirect to");
            
    }

}
