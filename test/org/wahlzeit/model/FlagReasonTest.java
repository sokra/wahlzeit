/*
 * Copyright (c) 2006-2009 by Dirk Riehle, http://dirkriehle.com
 *
 * This file is part of the Wahlzeit photo rating application.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.wahlzeit.model;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public class FlagReasonTest extends TestCase {

	public void testGetFromInt() {
		assertSame(FlagReason.COPYRIGHT, FlagReason.getFromInt(FlagReason.COPYRIGHT.asInt()));
		assertSame(FlagReason.MISMATCH, FlagReason.getFromInt(FlagReason.MISMATCH.asInt()));
		assertSame(FlagReason.OFFENSIVE, FlagReason.getFromInt(FlagReason.OFFENSIVE.asInt()));
		assertSame(FlagReason.OTHER, FlagReason.getFromInt(FlagReason.OTHER.asInt()));
	}
	
	public void testGetFromIntPreconditions() {
		Throwable thrown = null;
		try {
			FlagReason.getFromInt(-1);
		} catch(Throwable t) {
			thrown = t;
		}
		assertTrue(thrown instanceof IllegalArgumentException);
		
		thrown = null;
		try {
			int last = Math.max(Math.max(FlagReason.COPYRIGHT.asInt(), FlagReason.MISMATCH.asInt()), Math.max(FlagReason.OFFENSIVE.asInt(), FlagReason.OTHER.asInt()));
			FlagReason.getFromInt(last + 1);
		} catch(Throwable t) {
			thrown = t;
		}
		assertTrue(thrown instanceof IllegalArgumentException);
	}

	public void testGetFromString() {
		assertSame(FlagReason.COPYRIGHT, FlagReason.getFromString("copyright"));
		assertSame(FlagReason.MISMATCH, FlagReason.getFromString("mismatch"));
		assertSame(FlagReason.OFFENSIVE, FlagReason.getFromString("offensive"));
		assertSame(FlagReason.OTHER, FlagReason.getFromString("other"));
	}
	
	public void testGetFromStringPreconditions() {
		for(String testText: new String[]{"", null, "copyright1", "...", "blabla"}) {
			Throwable thrown = null;
			try {
				FlagReason.getFromString(testText);
			} catch(Throwable t) {
				thrown = t;
			}
			assertTrue(thrown instanceof IllegalArgumentException);
		}
	}

	public void testAsString() {
		assertEquals("copyright", FlagReason.COPYRIGHT.asString());
		assertEquals("mismatch", FlagReason.MISMATCH.asString());
		assertEquals("offensive", FlagReason.OFFENSIVE.asString());
		assertEquals("other", FlagReason.OTHER.asString());
	}

	public void testGetAllValues() {
		List<FlagReason> allValues = Arrays.asList(FlagReason.OTHER.getAllValues());
		assertEquals(4, allValues.size());
		assertTrue(allValues.contains(FlagReason.COPYRIGHT));
		assertTrue(allValues.contains(FlagReason.MISMATCH));
		assertTrue(allValues.contains(FlagReason.OFFENSIVE));
		assertTrue(allValues.contains(FlagReason.OTHER));
	}

	public void testGetTypeName() {
		assertEquals("FlagReason", FlagReason.OTHER.getTypeName());
	}

}
