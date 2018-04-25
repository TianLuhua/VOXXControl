package com.sat.mirrorcontorl.utils;

import java.io.File;
import java.io.FileInputStream;

public class DeviceUtils {

	public static String getSerialNumber() {
		FileInputStream is;
		String serial = "";
		byte[] buffer = new byte[16];
		try {
			is = new FileInputStream(new File(
					"/sys/devices/platform/cpu-id/chip_id"));
			is.read(buffer);
			is.close();
			serial = new String(buffer);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (serial.length() > 11) {
			String substring = serial.substring(4, 11);
			return "RSE_" + substring;
		}

		return serial;
	}

}
