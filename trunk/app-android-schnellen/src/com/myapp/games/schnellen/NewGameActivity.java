package com.myapp.games.schnellen;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.myapp.games.schnellen.frontend.Bot;
import com.myapp.games.schnellen.frontend.IPlayerFrontend;
import com.myapp.games.schnellen.frontend.PlayerFrontendWrapper;
import com.myapp.games.schnellen.model.GameFactory;
import com.myapp.games.schnellen.model.IGameContext;

public class NewGameActivity extends Activity {

    private Button go2gameButton;
    private RadioButton singleRBtn, hostRBtn, joinRBtn;
    private EditText playerName, playerCount;
    private RadioGroup radioGroup;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    Log.d(getClass().getSimpleName(), "onCreate() ENTERING");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_game);
        
        radioGroup = (RadioGroup) findViewById(R.id.new_game_type);
        singleRBtn = (RadioButton) findViewById(R.id.new_game_type_single);
        singleRBtn.setChecked(true);
        hostRBtn = (RadioButton) findViewById(R.id.new_game_type_network_host);
        joinRBtn = (RadioButton) findViewById(R.id.new_game_type_network_join);
        
        playerName = (EditText) findViewById(R.id.new_game_player_name);
        playerCount = (EditText) findViewById(R.id.new_game_playercount);
        playerCount.setText("2");
        
        go2gameButton = (Button) findViewById(R.id.new_game_go2game);
        Log.d(getClass().getSimpleName(), "onCreate() EXITING");
	}

	/** ! method name referenced by strings.xml ! */
	public void buttonAction(View view) {
        Log.d(getClass().getSimpleName(), "buttonAction() ENTERING");
        try {
    		switch (view.getId()) {
        		case R.id.new_game_go2game: {
        			String error = validateBeforeContinue();
        			
        			if (error != null) {
        				Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        				return;
        			}
        			createAndStartGame();
        			return;
        		}
        		case R.id.new_game_type_single: {
                    playerCount.setEnabled(true);
                    return;
        		}
        		case R.id.new_game_type_network_host: { 
                    playerCount.setEnabled(true);
        			// remember to go to start network view...
                    showTodo();//TODO implement me
                    return;
        		}
        		case R.id.new_game_type_network_join: { 
        		    playerCount.setEnabled(false);
        			// remember to go to connect to game view...
        		    showTodo();//TODO implement me
        		    return;
        		}
    		}
            throw new RuntimeException(""+Util.fromR(view.getId()));
        } finally {
            Log.d(getClass().getSimpleName(), "buttonAction() EXITING");
        }
	}
	
	private void createAndStartGame() {
	    try {
            final int count = Integer.parseInt(playerCount.getText().toString());
            String name = playerName.getText().toString().trim();
            GameFactory gf = new GameFactory();
            IPlayerFrontend wrapper = new PlayerFrontendWrapper(name);
            gf.putPlayer(wrapper);
            
            String[] botNames = {"Dolly", "Holly", "Polly", "Hacker"};
            
            for (int i = 0; gf.getPlayerCount() < count; i++) {
                Bot bot = new Bot(botNames[i]);
                gf.putPlayer(bot);
            }
            
            IGameContext game = gf.createGame();
            SchnellenApplication app = (SchnellenApplication) getApplication();
            app.setAttribute(SchnellenApplication.GAME_CONTEXT, game);
            app.setAttribute(SchnellenApplication.GAME_FRONTEND, wrapper);
            startActivity(new Intent(this, GameActivity.class));
	    
	    } catch (Throwable t) {
	        Log.e("NewGameActivity", Log.getStackTraceString(t));
	        throw new RuntimeException("error during createAndStartGame", t);
	    }
	}
	
	private void showTodo() {
        Toast.makeText(this, 
                   getString(R.string.TODO),
                   Toast.LENGTH_LONG).show();
	}

    private String validateBeforeContinue() {
        String name = playerName.getText().toString().trim();
        
        if (! name.matches("[-._a-zA-Z0-9]{1,}")) {
            return getString(R.string.new_game_error_invalid_name);
        }
        
        int count = getPlayersCount();
        
        if (count < 2 || count > 4) {
            playerCount.setText("2");
            return getString(R.string.new_game_error_invalid_number);
        }
        
        return null;
    }
    
    private int getPlayersCount() {
        String count = playerCount.getText().toString().trim();
        
        try {
            return Integer.parseInt(count);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
