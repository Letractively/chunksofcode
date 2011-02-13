package com.myapp.android.celsius2fahrenheit;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class TemperaturCalculatorActivity extends Activity {

    private EditText editText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        editText = (EditText) findViewById(R.id.EditText01);
    }
    
    /** ! method name referenced by strings.xml ! */
    public void buttonAction(View view) {
        if (view.getId() == R.id.Button01) {
            RadioButton celsiusButton = (RadioButton) findViewById(R.id.RadioButton01);
            RadioButton fahrenheitButton = (RadioButton) findViewById(R.id.RadioButton02);

            if (editText.getText().length() == 0) {
                Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_LONG).show();
                return;
            }
            
            float inputValue = Float.parseFloat(editText.getText().toString());
            
            if (celsiusButton.isChecked()) {
                editText.setText(String.valueOf(convertFahrenheitToCelcius(inputValue)));
            } else {
                editText.setText(String.valueOf(convertCelciusToFahrenheit(inputValue)));
            }
            
            // Switch to the other button
            if (fahrenheitButton.isChecked()) {
                fahrenheitButton.setChecked(false);
                celsiusButton.setChecked(true);
            } else {
                fahrenheitButton.setChecked(true);
                celsiusButton.setChecked(false);
            }
        }
    }
    
    // Converts to celcius
    private float convertFahrenheitToCelcius(float fahrenheit) {
        return ((fahrenheit - 32) * 5 / 9);
    }

    // Converts to fahrenheit
    private float convertCelciusToFahrenheit(float celsius) {
        return ((celsius * 9) / 5) + 32;
    }

}
