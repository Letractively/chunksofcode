package com.myapp.games.schnellen.frontend;

import com.myapp.games.schnellen.model.GameFactory;
import com.myapp.games.schnellen.model.IGameContext;

class Main {
    private static void failWhenAssertionsDisabled() {
//        List<String> l = new ArrayList<String>();
//        l.add("0");
//        l.add("1");
//        l.add("2");
//        l.add("3");
//        l.add("4");
//        l.add("5");
//        l.add("6");
//        l.add("7");
//        System.out.println("initial:      "+l);
//        
//        List<String> sub = l.subList(2, 5);
//        System.out.println("sub:          "+sub);
//        
//        Collections.rotate(sub, 2);
//        System.out.println("sub:          "+sub);
//        
//        System.out.println("after:        "+l);
        
        try {
            assert false;
            throw new RuntimeException("assertions disabled!");
        } catch (AssertionError e) {
            // expected
        }
    }

    public static void main(String[] args) {
        failWhenAssertionsDisabled();

        String name = "YOU";
        if (args != null && args.length == 1 && ! args[0].trim().isEmpty())
            name = args[0].trim();
        else
            System.out.println("You did not enter a name, using "+name+" instead.");

        GameFactory factory = new GameFactory();

        factory.putPlayer(new CommandLinePlayer(name));
        factory.putPlayer(new Bot("Harald"));
        factory.putPlayer(new Bot("Dolly"));

        IGameContext game = factory.createGame();
        game.playGame();
    }
}
