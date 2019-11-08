package ru.bio4j.spring.common;

import java.util.ArrayList;
import java.util.List;


public class stringHelper {

	public static boolean isNullOrEmpty(String str) {
		return (str == null) || (str.length() == 0);
	}

	public static String[] split(String str, String ... delimiters) {
		if (!isNullOrEmpty(str)) {
			if ((delimiters != null) && (delimiters.length > 0)) {
				String line = str;
				String dlmtr = null;
				if (delimiters.length > 1) {
					final String csDlmtrPG = "#inner_pg_delimeter_str#";
					for (String delimeter : delimiters)
						line = line.replace(delimeter, csDlmtrPG);
					dlmtr = csDlmtrPG;
				} else
					dlmtr = delimiters[0];
				List<String> lst = new ArrayList<String>();
				int item_bgn = 0;
				while (item_bgn <= line.length()) {
					String line2Add = "";
					int dlmtr_pos = line.indexOf(dlmtr, item_bgn);
					if (dlmtr_pos == -1)
						dlmtr_pos = line.length();
					line2Add = line.substring(item_bgn, dlmtr_pos);
					lst.add(line2Add);
					item_bgn += line2Add.length() + dlmtr.length();
				}
				return lst.toArray(new String[lst.size()]);
			} else
				return new String[] { str };
		} else
			return new String[] {};
	}

	public static String[] split(String str, char ... delimiters) {
		String[] d = new String[delimiters.length];
		for(int i=0; i<delimiters.length; i++){
			d[i] = ""+delimiters[i];
		}
		return split(str, d);
	}

	public static boolean compare(String str1, String str2, Boolean ignoreCase) {
		if ((str1 == null) && (str2 == null))
			return true;
		else if ((str1 == null) || (str2 == null))
			return false;
		else {
			if (ignoreCase)
				return str1.equalsIgnoreCase(str2);
			else
				return str1.equals(str2);
		}
	}

	public static String append(String line, String str, String delimiter) {
		if (isNullOrEmpty(line))
			line = ((str == null) ? "" : str);
		else
			line += delimiter + ((str == null) ? "" : str);
		return line;
	}

	public static void append(StringBuilder stringBuilder, String str, String delimiter) {
		if (stringBuilder.length() == 0)
			stringBuilder.append((str == null) ? "" : str);
		else
			stringBuilder.append(delimiter + ((str == null) ? "" : str));
	}

	public static <T> String combineArray(T[] array, String delimiter) {
		StringBuilder sb  = new StringBuilder();
		for (T item : array)
			sb.append(sb.length() == 0 ? item.toString() : delimiter+item.toString());
		return sb.toString();
	}

	public static String combineArray(int[] array, String delimiter) {
		StringBuilder sb  = new StringBuilder();
		for (int item : array)
			sb.append(sb.length() == 0 ? item : delimiter+item);
		return sb.toString();
	}
	public static String combineArray(byte[] array, String delimiter) {
		StringBuilder sb  = new StringBuilder();
		for (byte item : array)
			sb.append(sb.length() == 0 ? item : delimiter+item);
		return sb.toString();
	}

}