package bgsep.virtualgamepad;

import java.util.Observable;
import java.util.Observer;

import bgsep.model.Button;
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
		
		new Button(imageAbutton, Button.BUTTON_A, this);
		new Button(imageBbutton, Button.BUTTON_B, this);
		
	}

	@Override
	public void update(Observable o, Object obj) {
		if(o instanceof Button && obj instanceof Integer) {
			Button button = (Button)o;
			int buttonIdentifier = (Integer)obj;

			switch(buttonIdentifier) {
			case Button.BUTTON_A:
				if(button.isPressed())
					imageAbutton.setImageResource(R.drawable.a_button_pressed);
				else
					imageAbutton.setImageResource(R.drawable.a_button);
				break;
				
			case Button.BUTTON_B:
				if(button.isPressed()) 
					imageBbutton.setImageResource(R.drawable.b_button_pressed);
				else
					imageBbutton.setImageResource(R.drawable.b_button);
				break;
				
			default:
				break;
			}
		}
	}
	
	
}
