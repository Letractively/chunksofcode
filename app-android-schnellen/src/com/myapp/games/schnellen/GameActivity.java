package com.myapp.games.schnellen;



import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;

import com.myapp.games.schnellen.frontend.IPlayerFrontend;
import com.myapp.games.schnellen.frontend.PlayerFrontendWrapper;
import com.myapp.games.schnellen.model.Card;
import com.myapp.games.schnellen.model.Card.Color;
import com.myapp.games.schnellen.model.IColors;
import com.myapp.games.schnellen.model.IGameContext;
import com.myapp.games.schnellen.model.IRound;
import static com.myapp.games.schnellen.SchnellenApplication.GAME_FRONTEND;




public final class GameActivity extends Activity implements IPlayerFrontend {

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        
        SchnellenApplication app = (SchnellenApplication) getApplication();
        frontend = (PlayerFrontendWrapper) app.getAttribute(GAME_FRONTEND);
        frontend.setDelegate(this);
        Toast.makeText(this, 
                       "GameActivity.onCreate() game received! players: "+game().players(),
                       Toast.LENGTH_LONG).show();
        GuiHelper.setupGui(this);
        
        game().playGame();
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (Event.getEvent(id)) {
        case WELI_HIT_AT_DECKSPLIT :
        }
        return super.onCreateDialog(id);
    }
    
    IGameContext game() {
        return frontend.getGame();
    }
    
    List<Card> hand() {
        return frontend.getHand();
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
        throw new UnsupportedOperationException("not yet implemented: int offerPunch()");//TODO
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
            }
            default: {
                throw new RuntimeException(id + "");
            }
        }

        throw new UnsupportedOperationException("not yet implemented: void fireGameEvent(Event id)");//TODO
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
