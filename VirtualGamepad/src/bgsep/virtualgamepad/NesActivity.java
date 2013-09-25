package bgsep.virtualgamepad;

import java.util.Observable;
import java.util.Observer;

import bgsep.model.Button;
import bgsep.virtualgamepad.R.drawable;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class NesActivity extends Activity implements Observer {

	private ImageView 	imageAbutton, imageBbutton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nes);
		
		imageAbutton = (ImageView)findViewById(R.id.imageAbutton);
		imageBbutton = (ImageView)findViewById(R.id.imageBbutton);
		new Button(imageAbutton, R.drawable.a_button, R.drawable.a_button_pressed,
				45, this);
		new Button(imageBbutton, R.drawable.b_button, R.drawable.b_button_pressed,
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
