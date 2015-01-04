package net.philippschardt.panelinlamp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.ToggleButton;


public class MainActivity extends ActionBarActivity {


    short currentServo = 1;
    float currRotation = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onRadioButtonServoClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_servo1:
                if (checked)
                    currentServo = 1;
                    break;
            case R.id.radio_servo2:
                if (checked)
                    currentServo = 2;
                break;
            case R.id.radio_servo3:
                if (checked)
                    currentServo = 3;
                break;
            case R.id.radio_servo4:
                if (checked)
                    currentServo = 4;
                break;
            case R.id.radio_servo5:
                if (checked)
                    currentServo = 5;
                break;

        }
    }


    public void onRadioButtonRotationClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButton_1R:
                if (checked)
                    currRotation = 1;
                break;
            case R.id.radioButton_1_2r:
                if (checked)
                    currRotation = 0.5f;
                break;
        }
    }

    public void togglePower (View view) {
        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            // lamp is on
            // TODO send command to arduino
        } else {
            // lamp is off
            // TODO send command to arduino
        }
    }


    public void controlUPDOWN(View view) {
        switch(view.getId()) {
            case R.id.button_down:
                //TODO send command to arduino servo
                break;
            case R.id.button_up:
                //TODO send command to arduino servo
                break;
        }
    }


}
