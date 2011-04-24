package com.myapp.games.schnellen;



import static com.myapp.games.schnellen.SchnellenApplication.GAME_FRONTEND;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.myapp.games.schnellen.frontend.IPlayerFrontend;
import com.myapp.games.schnellen.frontend.PlayerFrontendWrapper;
import com.myapp.games.schnellen.model.Card;
import com.myapp.games.schnellen.model.Card.Color;
import com.myapp.games.schnellen.model.IColors;
import com.myapp.games.schnellen.model.IGameContext;
import com.myapp.games.schnellen.model.IRound;



public final class GameActivity extends Activity implements IPlayerFrontend {
    
	private static final String TAG = "GameActivity";
    
    private Gui gui;

    /**
     * this frontend was registered to the game.
     * it will delegate all "business" calls to this {@link IPlayerFrontend}
     */
    private PlayerFrontendWrapper frontend;

    /** 
     * Called when the activity is first created. 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() ENTERING");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        gui = new Gui(this);
        SchnellenApplication app = (SchnellenApplication) getApplication();
        frontend = (PlayerFrontendWrapper) app.getAttribute(GAME_FRONTEND);
        
        try {
            frontend.setDelegate(this);
            IGameContext game = game();
            Log.d(TAG, "onStart() game received! players: "+game.players());
            gui.prepareGameStart();
            
        } catch (Throwable t) {
            Log.e("NewGameActivity", Log.getStackTraceString(t));
            throw new RuntimeException("error during onCreate", t);
        }
        
        Log.d(TAG, "onCreate() EXITING");
    }
    
    OnClickListener getStartGameCallBack() {
        return new OnClickListener() {
        	// @Override
            public void onClick(View v) {
                startGame();
            }
        };
    }
    
    void startGame() {
        Thread t = new Thread(new Runnable() {
        	// @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    game().playGame();
                } catch (Exception e) {
                    Log.e("GameActivity",Log.getStackTraceString(e));
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        });
        
        t.start();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	return super.onKeyDown(keyCode, event);
    }
    
    protected void onStart() {
        Log.d(TAG, "onStart() ENTERING");
        super.onStart();
        Log.d(TAG, "onStart() EXITING");
    }
    
    IGameContext game() {
        return frontend.getGame();
    }
    
    List<Card> hand() {
        return frontend.getHand();
    }
    
    PlayerFrontendWrapper frontend() {
        return frontend;
    }
    
    
    public boolean wantToBeUntermHund()    {throw new UnsupportedOperationException("not yet implemented: boolean wantToBeUntermHund()     ");}
    public boolean askForSkipRound()       {throw new UnsupportedOperationException("not yet implemented: boolean askForSkipRound()        ");}
    public List<Card> askCardsForExchange(){throw new UnsupportedOperationException("not yet implemented: List<Card> askCardsForExchange() ");}
    public Card askDropOneOfSixCards()     {throw new UnsupportedOperationException("not yet implemented: Card askDropOneOfSixCards()      ");}
    public int askSplitDeckAt(int s)       {throw new UnsupportedOperationException("not yet implemented: int askSplitDeckAt(int s)        ");}
    public void notifyCardLifted(Card c)   {throw new UnsupportedOperationException("not yet implemented: void notifyCardLifted(Card c)    ");}
    public Card playNextCard(List<Card> p) {throw new UnsupportedOperationException("not yet implemented: Card playNextCard(List<Card> p)  ");}
    public Color spellTrumpSuit()          {throw new UnsupportedOperationException("not yet implemented: Color spellTrumpSuit()           ");}

    
    
    public int offerPunch() {
        IColors cs = game().colors();
        int promise = cs.getSpellersPromise();
        int minimumOffer = cs.getMinimumOfferValue(frontend.getName());
        
        List<CharSequence> itemList = new ArrayList<CharSequence>();
        itemList.add(getString(R.string.game_offerpunch_skip)); // skip
        
        // dealer may say bei mir:
        itemList.add(Integer.toString(minimumOffer));
        
        final CharSequence[] items = itemList.toArray(new CharSequence[0]);
        int punchesOffered = gui.showOfferPunchMenu();
        return punchesOffered;
    }  

    public void fireGameEvent(Event id) {
        IRound cr = game().round();
        IColors cs = game().colors();
        
        switch (id) {
            case SCORE_FACTOR_CHANGED: {
                // IO.println("The score factor changed: points in this round will be"+
                // " multiplied by "+game().scorings().getScoreFactor()+" !");
                break;
    
            }
            case TRUMP_SUIT_DETERMINED: {
                // String ts = cs.getSpeller();
                // String whom = (ts == null ? "." : " by "+ts+".");
                // IO.println("The trump suit was set to "+cs.getTrumpSuit()+whom);
                break;
    
            }
            case HAS_WELI: {
                // String owner = cr.getPublicWeliOwner();
                // assert owner != null;
                // IO.println("This player still has the weli card: "+owner);
                break;
    
            }
            case WELI_HIT_AT_DECKSPLIT: {
                // String owner = cr.getPublicWeliOwner();
                // assert owner != null;
                // IO.println((owner.equals(name)
                // ?"Cool. You":owner)+" hit the weli card!");
                break;
    
            }
            case SMASH_CARD_PLAYED: // fall through
            case CARD_WAS_PLAYED: {
                // String msg = cr.lastPlayer()+" played card: "+cr.lastCard();
                // if (id == Event.SMASH_CARD_PLAYED)
                // msg += " (Currently the higest card)";
                // IO.println(msg);
                break;
            }
            case CARD_EXCHANGE_COMPLETED: {
                // for (String p : game().players())
                // if (p != name)
                // IO.println(p+" exchanged "+cr.getExchangeCount(p)+" cards.");
                break;
            }
            case ROUND_FINISHED: {
                // IO.println("This punch was hit by "+cr.getHighestPlayer()+NL);
                break;
            }
            case SCORE_CHANGED: {
                // List<Map.Entry<String, Integer>> rankings =
                // game().scorings().getRankings();
                // IO.println("Current score ranking:");
                // int i = 1;
                //
                // for (Iterator<Entry<String, Integer>> itr =
                // rankings.iterator(); itr.hasNext(); ) {
                // Entry<String, Integer> next = itr.next();
                // IO.println(i+++" .) "+next.getKey()+"    "+next.getValue());
                // }
                break;
            }
            case CARDS_RECEIVED: {
                // IO.println("You received new card(s). Your hand: "+hand());
                break;
            }
            case CARDS_DROPPED: {
                // IO.println("You dropped card(s). Your hand: "+hand());
                break;
            }
            case UNTERM_HUND: {
                // IO.println(NL+"Unterm hund! Cards will be redealt!");
                break;
            }
            case SHELL_ROUND_CANNOT_LEAVE_ROUND: {
                // throw new UnsupportedOperationException("not yet implemented");
                break;
            }
            default: {
                throw new RuntimeException(id + "");
            }
        }

        Log.d("TODO", "not yet implemented: void fireGameEvent(Event "+id+")");
    }
    public String getName() {
        return frontend.getName();
    }

    public String toString() {
        return frontend.toString();
    }

    public final void setGameContext(IGameContext c) {
        throw new RuntimeException("should not be called: void setGameContext(IGameContext c)");
    }  
}
