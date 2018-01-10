/* NFCard is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

NFCard is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Wget.  If not, see <http://www.gnu.org/licenses/>.

Additional permission under GNU GPL version 3 section 7 */

package com.card;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.nfc.tech.NfcV;

final class NfcVCard {

	public static final int STX = 0x02;
	public static final int ETX = 0x03;

	@SuppressLint("NewApi")
	static String load(NfcV tech, Resources res) {
		String data = null;
		try {
			tech.connect();

			byte ID[];// , sak, ATQA;

			ID = tech.getTag().getId();
			if (ID == null)
				throw new Exception();

			data = toHexString(ID);

		} catch (Exception e) {
			data = null;
		}

		try {
			tech.close();
		} catch (Exception e) {
		}

		return data;
	}

	public static String toHexString(byte[] b) {
		StringBuffer buffer = new StringBuffer();
		for (int i = b.length - 1; i >= 0; i--) {
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
