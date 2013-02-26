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

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
	private static final String userAgent = "Mozilla/5.0 PagePlusAndroidWidget/0.4";

	@Override
	public void grabData(String user, String pass, String phone) throws Exception {

		Connection.Response res = Jsoup.connect("https://www.pagepluscellular.com/login/")
				.userAgent(userAgent)
				.data("username", user, "password", pass,
						"__VIEWSTATE", "/wEPDwUENTM4MQ9kFgJmD2QWAmYPZBYCZg9kFgJmD2QWAgIFEGRkFgoCAw9kFgJmD2QWBAIHDw8WAh4LTmF2aWdhdGVVcmwFBy9sb2dpbi9kZAIJDw8WAh8ABUhodHRwczovL2N1c3RvbWVyLnBhZ2VwbHVzY2VsbHVsYXIuY29tL215LWFjY291bnQvbXktYWNjb3VudC1zdW1tYXJ5LmFzcHhkZAIFDxYCHglpbm5lcmh0bWwF4QE8cD48YSB0YXJnZXQ9J19ibGFuaycgaHJlZj0naHR0cDovL3d3dy5mYWNlYm9vay5jb20vcGhvdG8ucGhwP2ZiaWQ9NDg4Mjc4NzY3ODg2ODY4JnNldD1hLjEwNDc5NjUxOTU2ODQzMC4yNzgyLjEwMDMzMTc3NjY4MTU3MSZ0eXBlPTEnPlNvbWV0aW1lcywgYSBzbWFydCBtb3ZlIGlzIGFsbCBpdCB0YWtlcy4gDQoNCkdldCB0aGUgSHVhd2VpIEFzY2VuZCBZIEFuZHJvaWQgc21hLi4uPC9hPjwvcD5kAgYPFgIfAQWuATxwPjxhIHRhcmdldD0nX2JsYW5rJyBocmVmPSdodHRwOi8vdHdpdHRlci5jb20vUGFnZVBsdXMvc3RhdHVzZXMvMzA1MDM1Njg2MzI5ODcyMzg0Jz5QYWdlUGx1czogV2lsbCBJdCBTb29uIGJlIExlZ2FsIHRvIFVubG9jayBZb3VyIENlbGwgUGhvbmU/IGh0dHA6Ly90LmNvL2V0NEJzbDJZQUk8L2E+PC9wPmQCBw8WAh8BBe4LPGg1PjxhIHRhcmdldD0nX2JsYW5rJyBocmVmPSdodHRwOi8vYmxvZy5wYWdlcGx1c2NlbGx1bGFyLmNvbS9zYXZlLW1vbmV5LW9uLXlvdXItY2VsbC1waG9uZS1iaWxsLz91dG1fc291cmNlPXJzcyZ1dG1fbWVkaXVtPXJzcyZ1dG1fY2FtcGFpZ249c2F2ZS1tb25leS1vbi15b3VyLWNlbGwtcGhvbmUtYmlsbCc+U2F2ZSBNb25leSBPbiBZb3VyIENlbGwgUGhvbmUgQmlsbCE8c3Bhbj5ieSBCZW5qYW1pbiBMZXd0b24sIE1hcmtldGluZyBDb29yZGluYXRvcjwvc3Bhbj48L2E+PC9oNT48aDU+PGEgdGFyZ2V0PSdfYmxhbmsnIGhyZWY9J2h0dHA6Ly9ibG9nLnBhZ2VwbHVzY2VsbHVsYXIuY29tL2JhdHRlcnktc2F2aW5nLXRpcHMtZm9yLXlvdXItYW5kcm9pZC1zbWFydHBob25lLz91dG1fc291cmNlPXJzcyZ1dG1fbWVkaXVtPXJzcyZ1dG1fY2FtcGFpZ249YmF0dGVyeS1zYXZpbmctdGlwcy1mb3IteW91ci1hbmRyb2lkLXNtYXJ0cGhvbmUnPkJhdHRlcnkgU2F2aW5nIFRpcHMgZm9yIFlvdXIgQW5kcm9pZCBTbWFydHBob25lPHNwYW4+YnkgQmVuamFtaW4gTGV3dG9uLCBNYXJrZXRpbmcgQ29vcmRpbmF0b3I8L3NwYW4+PC9hPjwvaDU+PGg1PjxhIHRhcmdldD0nX2JsYW5rJyBocmVmPSdodHRwOi8vYmxvZy5wYWdlcGx1c2NlbGx1bGFyLmNvbS9zd2l0Y2gtdG8tcGFnZS1wbHVzLWFuZC1rZWVwLXlvdXItcGhvbmUtbnVtYmVyLz91dG1fc291cmNlPXJzcyZ1dG1fbWVkaXVtPXJzcyZ1dG1fY2FtcGFpZ249c3dpdGNoLXRvLXBhZ2UtcGx1cy1hbmQta2VlcC15b3VyLXBob25lLW51bWJlcic+U3dpdGNoIHRvIFBhZ2UgUGx1cyBhbmQgS2VlcCBZb3VyIFBob25lIE51bWJlcjxzcGFuPmJ5IEJlbmphbWluIExld3RvbiwgTWFya2V0aW5nIENvb3JkaW5hdG9yPC9zcGFuPjwvYT48L2g1PjxoNT48YSB0YXJnZXQ9J19ibGFuaycgaHJlZj0naHR0cDovL2Jsb2cucGFnZXBsdXNjZWxsdWxhci5jb20vaG93LXRvLWdldC1tb3JlLWRhdGEtb24teW91ci1tb250aGx5LXBsYW4vP3V0bV9zb3VyY2U9cnNzJnV0bV9tZWRpdW09cnNzJnV0bV9jYW1wYWlnbj1ob3ctdG8tZ2V0LW1vcmUtZGF0YS1vbi15b3VyLW1vbnRobHktcGxhbic+SG93IHRvIEdldCBNb3JlIERhdGEgb24gWW91ciBNb250aGx5IFBsYW48c3Bhbj5ieSBCZW5qYW1pbiBMZXd0b24sIE1hcmtldGluZyBDb29yZGluYXRvcjwvc3Bhbj48L2E+PC9oNT48aDU+PGEgdGFyZ2V0PSdfYmxhbmsnIGhyZWY9J2h0dHA6Ly9ibG9nLnBhZ2VwbHVzY2VsbHVsYXIuY29tLzQtc2ltcGxlLXRpcHMtdG8tcHJvdGVjdC15b3Vyc2VsZi1mcm9tLW1vYmlsZS10aGVmdC8/dXRtX3NvdXJjZT1yc3MmdXRtX21lZGl1bT1yc3MmdXRtX2NhbXBhaWduPTQtc2ltcGxlLXRpcHMtdG8tcHJvdGVjdC15b3Vyc2VsZi1mcm9tLW1vYmlsZS10aGVmdCc+NCBTaW1wbGUgVGlwcyB0byBQcm90ZWN0IFlvdXJzZWxmIEZyb20gTW9iaWxlIFRoZWZ0PHNwYW4+YnkgQmVuamFtaW4gTGV3dG9uLCBNYXJrZXRpbmcgQ29vcmRpbmF0b3I8L3NwYW4+PC9hPjwvaDU+ZAIMDxYCHgdWaXNpYmxlaGQYAQU3Y3RsMDAkY3RsMDAkY3RsMDAkQ29udGVudFBsYWNlSG9sZGVyRGVmYXVsdCRjb250YWN0dmlldw8PZGZkqUZCJgWPdjn7+8v78fspN4En24c="
				)
				.method(Connection.Method.POST).timeout(10 * 1000)
				.execute();

		Document doc = res.parse();
/*
		Connection.Response res = null;
		Document doc = Jsoup.parse(new java.io.File("balance.html"), "utf-8");
		*/

		//System.out.println("doc: "+doc.html());
		//if(true) return;
		//This will get you cookies
		//System.out.println("cookies: "+res.cookies().toString());

		// doesn't support ids with spaces like this, but there is just one select, so just ignore it anyway
		//Elements phones = doc.select("select#ContentPlaceHolderDefault_mainContentArea_Item2_My Account Summary_5_Registred1_DrpAccounts").first();
		final Element select = doc.select("select").first();
		if (select == null)
			throw new Exception("Your username or password is invalid.");

		final Elements phones = select.select("option");
		final Map<String, String> phoneToId = new HashMap<String, String>(phones.size());
		String selectedPhone = null;
		for (final Element phoneOption : phones) {
			//System.out.println("phoneOption: " + phoneOption.toString());
			final String phoneName = phoneOption.text().toLowerCase();
			phoneToId.put(phoneName, phoneOption.attr("value"));
			//System.out.printf("phoneName: '%s' selected: '%s'\n", phoneName, phoneOption.attr("selected"));
			if (selectedPhone == null && "selected".equals(phoneOption.attr("selected")))
				selectedPhone = phoneName;
		}
		System.out.printf("selectedPhone: '%s', registered phones: %s\n", selectedPhone, phoneToId);

		if (!phone.equals(selectedPhone)) {
			final String id = phoneToId.get(phone);
			if (id != null) {
				// we need to request the page with OUR info on it
				Map<String, String> inputs = getFormFields(doc);
				inputs.put("ctl00$ctl00$ctl00$ContentPlaceHolderDefault$mainContentArea$Item2$My Account Summary_5$Registred1$DrpAccounts", id);
				System.out.println("inputs: " + inputs);
				doc = Jsoup.connect("https://customer.pagepluscellular.com/my-account/my-account-summary.aspx").cookies(res.cookies())
						.data(inputs).method(Connection.Method.POST).timeout(10 * 1000)
						.execute().parse();
			} else {
				// phone not found, print error messages
				throw new Exception("Phone not found! Registered phones: " + phoneToId.keySet());
			}
		}

		//System.out.println("doc: " + doc.html());

		info = new String[names.length];
		int index = -1;
		try {
			info[++index] = doc.select("span[id=ContentPlaceHolderDefault_mainContentArea_Item2_My Account Summary_5_Registred1_lblBalance]").first().text();
		} catch (Throwable e) {
			//e.printStackTrace();
		}
		try {
			final Element balance = doc.select("div[id=ContentPlaceHolderDefault_mainContentArea_Item2_My Account Summary_5_Registred1_divBundleDetails]").first();
			info[++index] = balance.select("tr.tableHeading").first().text();
			for (Element row : balance.select("tr.odd").first().select("td"))
				info[++index] = row.text();
			//System.out.println("balance: " + balance);
		} catch (Throwable e) {
			//e.printStackTrace();
		}
	}

	public static Map<String, String> getFormFields(Element form) {
		if (form == null)
			return null;
		if (!"form".equals(form.tagName()))
			form = form.select("form").first();
		Elements inputs = form.select("input");
		final Map<String, String> ret = new LinkedHashMap<String, String>(inputs.size());
		for (Element input : inputs)
			ret.put(input.attr("name"), input.attr("value"));
		return ret;
	}
}
