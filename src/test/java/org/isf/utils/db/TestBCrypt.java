/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.utils.db;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class TestBCrypt {

	String testVectors[][] = {   // [0] = plain;  [1] = salt; [2] = expected
			{ "",
					"$2a$06$DCq7YPn5Rq63x1Lad4cll.",
					"$2a$06$DCq7YPn5Rq63x1Lad4cll.TV4S6ytwfsfvkgY8jIucDrjc8deX1s." },
			{ "a",
					"$2a$06$m0CrhHm10qJ3lXRY.5zDGO",
					"$2a$06$m0CrhHm10qJ3lXRY.5zDGO3rS2KdeeWLuGmsfGlMfOxih58VYVfxe" },
			{ "abc",
					"$2a$06$If6bvum7DFjUnE9p2uDeDu",
					"$2a$06$If6bvum7DFjUnE9p2uDeDu0YHzrHM6tf.iqN8.yx.jNN1ILEf7h0i" },
			{ "abcdefghijklmnopqrstuvwxyz",
					"$2a$06$.rCVZVOThsIa97pEDOxvGu",
					"$2a$06$.rCVZVOThsIa97pEDOxvGuRRgzG64bvtJ0938xuqzv18d3ZpQhstC" },
			{ "~!@#$%^&*()      ~!@#$%^&*()PNBFRD",
					"$2a$06$fPIsBO8qRqkjj273rfaOI.",
					"$2a$06$fPIsBO8qRqkjj273rfaOI.HtSV9jLDpTbZn782DC6/t7qT67P6FfO" },
	};

	@Test
	public void testHashpw() throws Exception {
		for (int idx = 0; idx < testVectors.length; idx++) {
			String plain = testVectors[idx][0];
			String salt = testVectors[idx][1];
			String expected = testVectors[idx][2];
			String hashed = BCrypt.hashpw(plain, salt);
			assertThat(hashed).isEqualTo(expected);
		}
	}

	@Test
	public void testGensaltInt() throws Exception {
		for (int idx = 4; idx <= testVectors.length; idx++) {
			for (int jdx = 0; jdx < testVectors.length; jdx += 4) {
				String plain = testVectors[jdx][0];
				String salt = BCrypt.gensalt(idx);
				String hashed1 = BCrypt.hashpw(plain, salt);
				String hashed2 = BCrypt.hashpw(plain, hashed1);
				assertThat(hashed1).isEqualTo(hashed2);
			}
		}
	}

	@Test
	public void testGensalt() {
		for (int idx = 0; idx < testVectors.length; idx += 4) {
			String plain = testVectors[idx][0];
			String salt = BCrypt.gensalt();
			String hashed1 = BCrypt.hashpw(plain, salt);
			String hashed2 = BCrypt.hashpw(plain, hashed1);
			assertThat(hashed1).isEqualTo(hashed2);
		}
	}

	@Test
	public void testCheckpwSuccess() {
		for (int idx = 0; idx < testVectors.length; idx++) {
			String plain = testVectors[idx][0];
			String expected = testVectors[idx][2];
			assertThat(BCrypt.checkpw(plain, expected)).isTrue();
		}
	}

	@Test
	public void testCheckpwFailure() {
		for (int idx = 0; idx < testVectors.length; idx++) {
			int broken_index = (idx + 4) % testVectors.length;
			String plain = testVectors[idx][0];
			String expected = testVectors[broken_index][2];
			assertThat(BCrypt.checkpw(plain, expected)).isFalse();
		}
	}

	@Test
	public void testInternationalChars() {
		String pw1 = "\u2605\u2605\u2605\u2605\u2605\u2605\u2605\u2605";
		String pw2 = "????????";

		String h1 = BCrypt.hashpw(pw1, BCrypt.gensalt());
		assertThat(BCrypt.checkpw(pw2, h1)).isFalse();

		String h2 = BCrypt.hashpw(pw2, BCrypt.gensalt());
		assertThat(BCrypt.checkpw(pw1, h2)).isFalse();
	}
}
