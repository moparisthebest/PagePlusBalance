package org.moparisthebest.pageplus.dto;

import java.util.Date;

public class Balance {
	private static final String[] names = new String[]{"Balance", "Plan", "Minutes", "SMS", "Data"};
	private static String compactFormatDelim = "\n";

	public final String[] info = new String[names.length];
	public String error = null;
	public Date successDate = null;

	public Balance() {
	}

	public Balance(final String compactFormat) {
		if (compactFormat == null || compactFormat.isEmpty())
			return;
		String[] split = compactFormat.split(compactFormatDelim);
		//System.out.println("compactFormat: " + compactFormat);
		//System.out.println("split: " + java.util.Arrays.toString(split));
		int x = 0;
		for (; x < info.length; ++x)
			info[x] = nullForEmpty(split[x]);
		if (x < split.length)
			error = nullForEmpty(split[x++]);
		if (x < split.length)
			try {
				String date = nullForEmpty(split[x++]);
				if (date != null)
					successDate = new Date(Long.parseLong(date));
			} catch (Throwable e) {
				// do nothing
			}
	}

	public String compactFormat() {
		StringBuilder sb = new StringBuilder();
		for (String str : info) {
			if (str != null)
				sb.append(str);
			sb.append(compactFormatDelim);
		}
		if (error != null)
			sb.append(error);
		sb.append(compactFormatDelim);
		if (successDate != null)
			sb.append(successDate.getTime());
		return sb.toString();
	}

	private static String nullForEmpty(String s) {
		return (s == null || s.isEmpty()) ? null : s;
	}

	public Balance copyFrom(Balance lastSuccessful) {
		System.arraycopy(lastSuccessful.info, 0, this.info, 0, this.info.length);
		this.successDate = lastSuccessful.successDate;
		return this;
	}

	public Balance setError(String error) {
		this.error = error;
		successDate = null;
		return this;
	}

	public Balance success() {
		successDate = new Date();
		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (error != null)
			sb.append("Error: ").append(error).append("\nPrevious data:\n");
		for (int x = 0; x < info.length; ++x) {
			sb.append(names[x]).append(": ");
			if (info[x] != null)
				sb.append(info[x]);
			sb.append("\n");
		}
		sb.append("Last Updated: ");
		if (successDate != null)
			sb.append(successDate.toString());
		sb.append("\n");
		return sb.toString();
	}
}
