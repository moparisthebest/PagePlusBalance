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

public abstract class PPInfo {

	public static final String[] names = new String[]{"Balance", "Plan", "Minutes", "SMS", "Data"};
	public String[] info;

	public void grabData(String[] userPassPhone) throws Exception {
		this.grabData(userPassPhone[0], userPassPhone[1], userPassPhone[2]);
	}

	public abstract void grabData(String user, String pass, String phone) throws Exception;

}
