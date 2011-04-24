package com.myapp.games.schnellen;

import static android.view.ViewGroup.LayoutParams.FILL_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myapp.games.schnellen.model.IColors;
import com.myapp.games.schnellen.model.IGameContext;


final class Gui {

    private final GameActivity context;
    private final LinearLayout statusArea, playedArea, handArea;
    
    Gui(GameActivity activity) {
        context = activity;
        statusArea = (LinearLayout) activity.findViewById(R.id.game_status_area_container);
        playedArea = (LinearLayout) activity.findViewById(R.id.game_playedcards_container);
        handArea = (LinearLayout) activity.findViewById(R.id.game_hand_container);
    }
    
    
    public LinearLayout getStatusArea() {
        return statusArea;
    }
    public LinearLayout getPlayedArea() {
        return playedArea;
    }
    public LinearLayout getHandArea() {
        return handArea;
    }
    
    public void prepareGameStart() {
        Log.d(getClass().getSimpleName(), "start() ENTERING");
        statusArea.removeAllViews();
        Log.d(getClass().getSimpleName(), "statusArea:"+statusArea);
        
        TextView text = new TextView(context);
        text.setLayoutParams(new LayoutParams(FILL_PARENT, WRAP_CONTENT));
        text.setText("New game Players: "+context.game().players());
        
        Button continu = new Button(context);
        continu.setText("continue");
        continu.setOnClickListener(context.getStartGameCallBack());
        continu.setLayoutParams(new LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        
        statusArea.addView(text);
        statusArea.addView(continu);
        statusArea.bringChildToFront(continu);
        statusArea.invalidate();
        statusArea.forceLayout();
        Log.d(getClass().getSimpleName(), "start() EXITING");
    }
    
    public int showOfferPunchMenu() {
        IGameContext g = context.game();
        IColors cs = g.colors();
        int promise = cs.getSpellersPromise();
        int minimumOffer = cs.getMinimumOfferValue(context.frontend().getName());
        
        List<CharSequence> itemList = new ArrayList<CharSequence>();
        itemList.add(context.getString(R.string.game_offerpunch_skip)); // skip
        
        // dealer may say bei mir:
        String dealer = context.game().round().getDealer();
        if (context.frontend().getName().equals(dealer)) {
            itemList.add(context.getString(R.string.game_offerpunch_beimir, minimumOffer));
        } else {
            itemList.add(context.getString(R.string.game_offerpunch_minimum, minimumOffer));
        }
        
        while (minimumOffer <= 5) {
            itemList.add(Integer.toString(minimumOffer++));
        }
        
        final CharSequence[] items = itemList.toArray(new CharSequence[0]);
        final int[] offer = {-1};
        
//        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
//        final EditText input = new EditText(context);
//        input.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
//        alert.setView(input);
//        alert.setPositiveButton(
//            "Ok", 
//            new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                    String value = input.getText().toString().trim();
//                    Log.d("GAME", "offerPunch: user chose:"+value);
//                    offer[0] = Integer.parseInt(value);
//                    dialog.cancel();
//                }
//            }
//        );
//
//        alert.setNegativeButton(
//            "Cancel",
//            new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                    dialog.cancel();
//                }
//            }
//        );
//        
//        alert.show();
        return offer[0];
    }
}