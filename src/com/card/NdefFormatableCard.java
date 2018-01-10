/* NdefFormatablerd is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

NdefFormatablerd is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Wget.  If not, see <http://www.gnu.org/licenses/>.

Additional permission under GNU GPL version 3 section 7 */

package com.card;

import android.content.res.Resources;
import android.nfc.tech.NdefFormatable;

final class NdefFormatableCard {

	public static final int STX = 0x02;
	public static final int ETX = 0x03;

	static String load(NdefFormatable tech, Resources res) {
		String data = null;
		try {
			tech.connect();

			byte ID[];//, sak, ATQA;

			ID = tech.getTag().getId();
			if (ID == null || ID.length != 8)
				throw new Exception();

			data = toHexString(ID);

		} catch (Exception e) {
			data = null;
			// data = e.getMessage();
		}

		try {
			tech.close();
		} catch (Exception e) {
		}

		return data;
	}

	public static String toHexString(byte[] b) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < b.length; ++i) {
			buffer.append(toHexString(b[i]));
		}
		return buffer.toString();
	}

	public static String toHexString(byte b) {
		String s = Integer.toHexString(b & 0xFF);
		if (s.length() == 1) {
			return "0" + s;
		} else {
			return s;
		}
	}

}
