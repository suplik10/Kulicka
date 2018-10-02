package cz.kulicka.util;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import org.springframework.stereotype.Component;

@Component
public class WindowsSetSystemTime {

	public boolean SetLocalTime(int wYear, int wMonth, int wDay, int wHour, int wMinute, int wSecond) {
		Kernel32 kernel = Kernel32.INSTANCE;
		WinBase.SYSTEMTIME newTime = new WinBase.SYSTEMTIME();
		newTime.wYear = (short) wYear;
		newTime.wMonth = (short) wMonth;
		newTime.wDay = (short) wDay;
		newTime.wHour = (short) wHour;
		newTime.wMinute = (short) wMinute;
		newTime.wSecond = (short) wSecond;
		kernel.SetSystemTime(newTime);
		return true;
	}
}
