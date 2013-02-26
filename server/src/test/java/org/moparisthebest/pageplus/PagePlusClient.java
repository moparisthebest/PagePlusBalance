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

package org.moparisthebest.pageplus;

import org.moparisthebest.pageplus.plugins.PPInfo;

public class PagePlusClient {

	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.err.println("Usage: PagePlusClient pp_email pp_password pp_phonename");
			return;
		}
		PPInfo pp;
		pp = new org.moparisthebest.pageplus.plugins.PagePlusHTTP();
		//pp = new org.moparisthebest.pageplus.plugins.PPServer();

		pp.grabData(args);

		if (pp.info != null)
			for (int x = 0; x < PPInfo.names.length; ++x)
				System.out.println(PPInfo.names[x] + ": " + pp.info[x]);
	}

}
