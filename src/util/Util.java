package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Util {

	public static String getDateAsString(String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		return sdf.format(cal.getTime());
	}

	public static String removeTrailingZeros(String val) {
		return val.replaceAll("[0]*$", "").replaceAll(".$", "");
	}

	public static String getFillerSpace(int fillerSpaceLimit) {
		StringBuffer fillerSpace = new StringBuffer();
		for (int i = 0; i < fillerSpaceLimit; i++)
			fillerSpace.append(" ");
		return fillerSpace.toString();
	}

	public static String getFillerSpaceWithValueAtStart(String value, int fillerSpaceLimit) {
		StringBuffer fillerSpace = new StringBuffer();
		fillerSpace.append(value);
		fillerSpaceLimit -= value.length();
		for (int i = 0; i < fillerSpaceLimit; i++) {
			fillerSpace.append(" ");
		}
		return fillerSpace.toString();
	}

	public static String getFillerSpaceWithValueAtEnd(String value, int fillerSpaceLimit) {
		StringBuffer fillerSpace = new StringBuffer();
		fillerSpaceLimit -= value.length();
		for (int i = 0; i < fillerSpaceLimit; i++) {
			fillerSpace.append(" ");
		}
		fillerSpace.append(value);
		return fillerSpace.toString();
	}

	public static String getMonthAndDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		return sdf.format(cal.getTime());
	}

	public static double round2(double num) {
		double result = num * 100;
		result = Math.round(result);
		result = result / 100;
		return result;
	}

	public static String convertDoubleToPlainString(Double val) {
		String str = val.toString();
		String text = Double.toString(Math.abs(val));
		int integerPlaces = text.indexOf('.');
		int decimalPlaces = text.length() - integerPlaces - 1;

		if (decimalPlaces < 2) {
			str = str + "0";
		}

		str = str.replace(".", "");
		return str;
	}

	public static void appendDataToFile(String fileName, String content) {
		FileWriter fstream = null;
		BufferedWriter out = null;
		try {
			fstream = new FileWriter(fileName, true);
			out = new BufferedWriter(fstream);
			out.write(content);
			out.write("\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				fstream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void deleteFile(String fileName) {
		try {
			File file = new File(fileName);
			if (file.delete()) {
				System.out.println(file.getName() + " is deleted!");
			} else {
				System.out.println("Delete operation is failed.");
			}

		} catch (Exception e) {

			e.printStackTrace();

		}
	}
}
