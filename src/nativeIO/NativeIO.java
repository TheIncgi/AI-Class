package nativeIO;

import java.io.File;

public class NativeIO {
	static {
        System.load(NativeIO.class.getResource("NativeIO.dll").getFile().replace("%20", " "));
    }
	
	public static native int getCh();
}
