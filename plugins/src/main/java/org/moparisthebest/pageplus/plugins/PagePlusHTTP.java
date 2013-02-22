/*
 * PagePlusBalance retrieves your balance from PagePlusCellular.com, currently for android phones.
 * Copyright (C) 2013 Travis Burtrum (moparisthebest)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.moparisthebest.pageplus.plugins;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * This currently uses 80,760 bytes in the worst-case scenario (grabbing 3
 * pages) and 38,680 bytes in the best-case scenario (grabbing 2 pages) If you
 * only have one phone registered on your account, it will be best-case each
 * time. if you have more than one, it appears to be random which will appear on
 * first grab.
 *
 * @author mopar
 */
public class PagePlusHTTP extends PPInfo {

	// private static final String userAgent =
	// "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.3) Gecko/20100423 Ubuntu/10.04 (lucid) Firefox/3.6.3";
	private static final String userAgent = "Mozilla/5.0 PagePlusAndroidWidget/0.1";

	@Override
	public void grabData(String user, String pass, String phone) throws Exception {
		user = URLEncoder.encode(user, "UTF-8");
		pass = URLEncoder.encode(pass, "UTF-8");
		String submit = URLEncoder.encode("Sign In", "UTF-8");

		// HttpURLConnection.setFollowRedirects(true);
		HttpURLConnection uc1 = (HttpURLConnection) new URL("https://www.pagepluscellular.com/login.aspx").openConnection();

		uc1.setRequestMethod("POST");
		uc1.setDoInput(true);
		uc1.setDoOutput(true);
		uc1.setUseCaches(false);
		uc1.setAllowUserInteraction(false);
		uc1.setInstanceFollowRedirects(false);
		uc1.setRequestProperty("User-Agent", userAgent);
		uc1.setRequestProperty("Referer", "http://www.pagepluscellular.com/default.aspx");
		uc1.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		DataOutputStream out = new DataOutputStream(uc1.getOutputStream());
		String content = "username=" + user + "&password=" + pass + "&submit=" + submit;
		// System.out.println(content + "\n" +
		// "sending form to HTTP server ...");
		// System.out.println("Logging in...");
		out.writeBytes(content);
		out.flush();
		out.close();
		// debug(uc1);

		String cookie = uc1.getHeaderField("Set-Cookie").split(";")[0];
		// System.out.println("cookie: "+cookie);
		String location = uc1.getHeaderField("Location");
		uc1.disconnect();
		// System.out.println("location: "+location);
		// System.exit(0);

		// detect if we are going to Error.aspx
		if (location.contains("Error"))
			throw new Exception("Page Plus sent us to the error page! (probably bad username/password).");

		HttpURLConnection uc2 = (HttpURLConnection) new URL("https://www.pagepluscellular.com" + location).openConnection();

		uc2.setRequestMethod("GET");
		uc2.setDoInput(true);
		uc2.setDoOutput(true);
		uc2.setUseCaches(false);
		uc2.setAllowUserInteraction(false);
		uc2.setInstanceFollowRedirects(false);
		uc2.setRequestProperty("User-Agent", userAgent);
		uc2.setRequestProperty("Referer", "http://www.pagepluscellular.com/default.aspx");
		uc2.setRequestProperty("Cookie", cookie);

		// debug(uc2);

		BufferedReader in = new BufferedReader(new InputStreamReader(uc2.getInputStream()));
		String line = "", id = "";
		content = "";
		boolean notFirst = false, doAnotherRequest = true, phoneExists = false;
		HttpURLConnection uc3 = null;
		while ((line = in.readLine()) != null) {
			if (line.contains("type=\"hidden\"")) {
				// it has a hidden form variable, add it into the next POST
				// request (content)
				String name = line.substring(line.indexOf("name=\"") + 6);
				name = name.substring(0, name.indexOf("\""));
				// System.out.println("name: "+name);
				content += (notFirst ? "&" : "") + name + "=";

				String value = line.substring(line.indexOf("value=\"") + 7);
				value = value.substring(0, value.indexOf("\""));
				value = URLEncoder.encode(value, "UTF-8");
				// System.out.println("value: "+value);
				content += value;
				notFirst = true;
			} else if (line.contains("<option")) {
				String name = line.substring(line.indexOf(">") + 1);
				name = name.substring(0, name.indexOf("<"));
				// System.out.println("name: " + name);
				if (line.contains("selected") && name.equalsIgnoreCase(phone)) {
					// then we already have the correct phone info on the page
					doAnotherRequest = false;
					uc3 = uc2;
					phoneExists = true;
					break;
				} else if (name.equalsIgnoreCase(phone)) {
					// then our phone isn't selected, must request page again,
					// boohoo :(
					id = line.substring(line.indexOf("value=\"") + 7);
					id = id.substring(0, id.indexOf("\""));
					phoneExists = true;
					break;
					// System.out.println("id: " + id);
				}
			}
		}

		if (!phoneExists)
			throw new Exception("Phone name entered does not exist.");

		// System.out.println("post: "+content);
		// System.exit(0);

		if (doAnotherRequest) {
			uc2.disconnect();
			uc3 = (HttpURLConnection) new URL("https://www.pagepluscellular.com" + location).openConnection();

			uc3.setRequestMethod("POST");
			uc3.setDoInput(true);
			uc3.setDoOutput(true);
			uc3.setUseCaches(false);
			uc3.setAllowUserInteraction(false);
			uc3.setInstanceFollowRedirects(false);
			uc3.setRequestProperty("User-Agent", userAgent);
			uc3.setRequestProperty("Referer", "https://www.pagepluscellular.com" + location);
			uc3.setRequestProperty("Cookie", cookie);

			uc3.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			out = new DataOutputStream(uc3.getOutputStream());

			// build content
			content = content.replaceFirst("__EVENTTARGET=", "__EVENTTARGET=ctl07%24Registred1%24DrpAccounts");
			content += "&ctl07%24Registred1%24DrpAccounts=" + id;

			// System.out.println(content + "\n" +
			// "sending form to HTTP server ...");
			System.out.println("Wrong phone given first, requesting correct one...");
			out.writeBytes(content);
			out.flush();
			out.close();

			// debug(uc3); System.exit(0);
		}

		String divId = "ctl07_Registred1_divBundleDetails";
		String balanceId = "ctl07_Registred1_lblBalance";
		in = new BufferedReader(new InputStreamReader(uc3.getInputStream()));
		line = "";
		while ((line = in.readLine()) != null)
			if (line.contains(balanceId))
				break;
		// System.out.println("bad line: "+line);
		// System.out.println("try again: "+in.readLine());
		info = new String[5];
		info[0] = "Unknown";
		if (line != null) {
			info[0] = line.substring(line.indexOf('$'));
			info[0] = info[0].substring(0, info[0].indexOf('<'));
		}
		// System.out.println("balance: "+info[0]);
		while ((line = in.readLine()) != null)
			if (line.contains(divId))
				break;
		in.close();
		uc3.disconnect();

		// System.out.println("plan: "+line);
		int count = 0, index = 0;
		while (line.contains("td") && count != 8) {
			line = line.substring(line.indexOf("<td"));
			line = line.substring(line.indexOf(">") + 1);
			if (count == 1 || count > 4)
				info[++index] = line.substring(0, line.indexOf("</td")).replaceAll("<[/]?b>", "");
			line = line.substring(line.indexOf(">") + 1);
			++count;
		}
	}

/*	public static void debug(HttpURLConnection urlConn) throws IOException {
		System.out.println("-------------------Response-------------------");
		System.out.println(urlConn.getResponseCode() + " " + urlConn.getResponseMessage());
		System.out.println("-------------------Headers-------------------");
		for (Entry<String, List<String>> header : urlConn.getHeaderFields().entrySet())
			for (String val : header.getValue())
				System.out.println(header.getKey() + ": " + val);
		// get input connection
		BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
		String line = null;
		System.out.println("-------------------Content-------------------");
		while ((line = in.readLine()) != null)
			System.out.println(line);

	}
*/
}
