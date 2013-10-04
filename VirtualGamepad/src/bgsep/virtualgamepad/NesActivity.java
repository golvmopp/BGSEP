package bgsep.virtualgamepad;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import bgsep.model.Button;

public class NesActivity extends Activity implements Observer {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nes);
		
		ImageView 		imageAbutton, imageBbutton;
		imageAbutton = (ImageView)findViewById(R.id.nes_a_button);
		imageBbutton = (ImageView)findViewById(R.id.nes_b_button);
		new Button(imageAbutton, R.drawable.nes_a_button, R.drawable.nes_a_button_pressed,
				45, this);
		new Button(imageBbutton, R.drawable.nes_b_button, R.drawable.nes_b_button_pressed,
				46, this);
		
	}

	@Override
	public void update(Observable o, Object obj) {
		if(o instanceof Button) {
			Button button = (Button)o;
			
			if(button.isPressed())
				button.getButtonView().setImageResource(button.getPressedDrawableID());
			else
				button.getButtonView().setImageResource(button.getUnPressedDrawableID());
				
		}
	}
	
	
}
