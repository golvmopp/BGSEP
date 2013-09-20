package bluetooth;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class SdpWriter {

	private Process suProcess;
	private boolean isRooted;

	public SdpWriter() {
		isRooted = checkIfRooted();
	}

	public void modifyService(String code) {
		if (isRooted && suProcess != null) {
			try {
				DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
				os.writeBytes(code);
				os.flush();
				os.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			return;
		}
	}
	
	
	private boolean checkIfRooted() {
		try {
			File f1 = new File("system/bin/su");
			File f2 = new File("system/xbin/su");

			if (f1.exists() || f2.exists()) {

				Process p = Runtime.getRuntime().exec("su");

				p.waitFor();
				int value = p.exitValue();

				if (value == 0) {
					this.suProcess = p;
					return true;
				} else {
					return false;
				}

			} else {
				return false;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

}
