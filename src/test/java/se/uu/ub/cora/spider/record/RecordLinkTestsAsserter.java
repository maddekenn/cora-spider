/*
 * Copyright 2015, 2019 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.uu.ub.cora.spider.record;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataResourceLink;

public class RecordLinkTestsAsserter {
	public static void assertTopLevelLinkContainsReadActionOnly(DataRecord record) {
		DataRecordLink link = getLinkFromRecord(record);
		assertTrue(link.getActions().contains(Action.READ));
		assertEquals(link.getActions().size(), 1);
	}

	private static DataRecordLink getLinkFromRecord(DataRecord record) {
		DataGroup spiderDataGroup = record.getDataGroup();
		DataRecordLink link = (DataRecordLink) spiderDataGroup.getFirstChildWithNameInData("link");
		return link;
	}

	public static void assertTopLevelTwoLinksContainReadActionOnly(DataRecord record) {
		List<DataRecordLink> links = getLinksFromRecord(record);
		for (DataRecordLink link : links) {
			assertTrue(link.getActions().contains(Action.READ));
			assertEquals(link.getActions().size(), 1);
		}
		assertEquals(links.size(), 2);
	}

	private static List<DataRecordLink> getLinksFromRecord(DataRecord record) {
		DataGroup spiderDataGroup = record.getDataGroup();
		List<DataGroup> links = spiderDataGroup.getAllGroupsWithNameInData("link");
		List<DataRecordLink> links2 = new ArrayList<DataRecordLink>();
		for (DataGroup spiderDataGroup2 : links) {
			links2.add((DataRecordLink) spiderDataGroup2);
		}
		return links2;
	}

	public static void assertOneLevelDownLinkContainsReadActionOnly(DataRecord record) {
		DataGroup spiderDataGroup = record.getDataGroup();
		DataGroup spiderDataGroupOneLevelDown = (DataGroup) spiderDataGroup
				.getFirstChildWithNameInData("oneLevelDown");
		DataRecordLink link = (DataRecordLink) spiderDataGroupOneLevelDown
				.getFirstChildWithNameInData("link");
		assertTrue(link.getActions().contains(Action.READ));
		assertEquals(link.getActions().size(), 1);
	}

	public static void assertTopLevelResourceLinkContainsReadActionOnly(DataRecord record) {
		DataResourceLink link = getResourceLinkFromRecord(record);
		assertTrue(link.getActions().contains(Action.READ));
		assertEquals(link.getActions().size(), 1);
	}

	private static DataResourceLink getResourceLinkFromRecord(DataRecord record) {
		DataGroup spiderDataGroup = record.getDataGroup();
		DataResourceLink link = (DataResourceLink) spiderDataGroup
				.getFirstChildWithNameInData("link");
		return link;
	}

	public static void assertOneLevelDownResourceLinkContainsReadActionOnly(DataRecord record) {
		DataGroup spiderDataGroup = record.getDataGroup();
		DataGroup spiderDataGroupOneLevelDown = (DataGroup) spiderDataGroup
				.getFirstChildWithNameInData("oneLevelDown");
		DataResourceLink link = (DataResourceLink) spiderDataGroupOneLevelDown
				.getFirstChildWithNameInData("link");
		assertTrue(link.getActions().contains(Action.READ));
		assertEquals(link.getActions().size(), 1);
	}

	public static void assertTopLevelLinkDoesNotContainReadAction(DataRecord record) {
		DataRecordLink link = getLinkFromRecord(record);
		assertFalse(link.getActions().contains(Action.READ));
		assertEquals(link.getActions().size(), 0);
	}

	public static void assertRecordStorageWasNOTCalledForReadKey(
			RecordEnhancerTestsRecordStorage recordStorage, String readKey) {
		Map<String, Integer> readNumberMap = recordStorage.readNumberMap;
		assertFalse(readNumberMap.containsKey(readKey));
	}

	public static void assertRecordStorageWasCalledOnlyOnceForReadKey(
			RecordEnhancerTestsRecordStorage recordStorage, String readKey) {
		Map<String, Integer> readNumberMap = recordStorage.readNumberMap;
		Integer actual = readNumberMap.get(readKey);
		assertEquals(actual.intValue(), 1);
	}
}
