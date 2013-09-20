package bgsep.virtualgamepad;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class NesActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nes);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}
	
	
}
