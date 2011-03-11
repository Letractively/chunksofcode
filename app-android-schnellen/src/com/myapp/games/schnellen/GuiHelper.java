package com.myapp.games.schnellen;

import java.util.List;

import com.myapp.games.schnellen.model.IGameContext;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class GuiHelper {
    
    final class PlayerRow extends ViewGroup {
        
        private String name;
        private TextView nameLabel, scoreLabel, punchesLabel;
        
        public PlayerRow(GameActivity a, String name) {
            this.name = name;
            nameLabel = new TextView(a);
            scoreLabel = new TextView(a);
            punchesLabel = new TextView(a);
        }
        
        public void update(GameActivity a) {
            nameLabel.setText(name);
        }
    }

    public static void setupGui(GameActivity a) {
        TableLayout table = (TableLayout) a.findViewById(R.id.game_players_list);
        IGameContext gc = a.game();
        List<String> players = gc.players();
        
        for (String s : players) {
            TableRow row = new TableRow(a);
            TextView nameLabel = new TextView(a);
            nameLabel.setText(s);
            TextView scoreLabel = new TextView(a);
            scoreLabel.setText(gc.scorings().ge);
            
        }
    }
}
