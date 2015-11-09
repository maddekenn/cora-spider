/*
 * Copyright 2015 Uppsala University Library
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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.beefeater.Authorizator;
import se.uu.ub.cora.beefeater.AuthorizatorImp;
import se.uu.ub.cora.spider.data.Action;
import se.uu.ub.cora.spider.data.SpiderDataRecord;
import se.uu.ub.cora.spider.data.SpiderRecordList;
import se.uu.ub.cora.spider.record.storage.RecordStorage;
import se.uu.ub.cora.spider.testdata.TestDataRecordInMemoryStorage;

/**
 *
 * @author <a href="mailto:madeleine.kennback@ub.uu.se">Madeleine Kennbäck</a>
 * @version $Revision$, $Date$, $Author$
 */
public class SpiderRecordListReaderTest {

	private RecordStorage recordStorage;
	private Authorizator authorization;
	private PermissionKeyCalculator keyCalculator;
	private SpiderRecordListReader recordListReader;

	@BeforeMethod
	public void beforeMethod() {
		authorization = new AuthorizatorImp();
		recordStorage = TestDataRecordInMemoryStorage.createRecordStorageInMemoryWithTestData();
		keyCalculator = new RecordPermissionKeyCalculator();
		recordListReader = SpiderRecordListReaderImp
				.usingAuthorizationAndRecordStorageAndKeyCalculator(authorization, recordStorage,
						keyCalculator);
	}

	@Test
	public void testReadListAuthorized() {
		String userId = "userId";
		String type = "place";
		SpiderRecordList readRecordList = recordListReader.readRecordList(userId, type);
		assertEquals(readRecordList.getTotalNumberOfTypeInStorage(), "2",
				"Total number of records should be 2");
		assertEquals(readRecordList.getFromNo(), "0");
		assertEquals(readRecordList.getToNo(), "1");
		List<SpiderDataRecord> records = readRecordList.getRecords();
		SpiderDataRecord spiderDataRecord = records.iterator().next();
		assertNotNull(spiderDataRecord);
	}

	@Test
	public void testReadListAbstractRecordType() {
		RecordStorageSpy recordStorageListReaderSpy = new RecordStorageSpy();
		SpiderRecordListReader recordReader = SpiderRecordListReaderImp
				.usingAuthorizationAndRecordStorageAndKeyCalculator(authorization,
						recordStorageListReaderSpy, keyCalculator);
		recordReader.readRecordList("userId", "abstract");

		Assert.assertTrue(recordStorageListReaderSpy.readLists.contains("child1"));
		Assert.assertTrue(recordStorageListReaderSpy.readLists.contains("child2"));
	}

	@Test(expectedExceptions = AuthorizationException.class)
	public void testReadListUnauthorized() {
		recordListReader.readRecordList("unauthorizedUserId", "place");
	}

	@Test
	public void testActionsOnReadRecord() {
		SpiderRecordList recordList = recordListReader.readRecordList("userId", "place");
		assertEquals(recordList.getRecords().get(0).getActions().size(), 4);
		assertTrue(recordList.getRecords().get(0).getActions().contains(Action.DELETE));
	}

	@Test
	public void testActionsOnReadRecordNoIncomingLinks() {
		SpiderRecordList recordList = recordListReader.readRecordList("userId", "place");
		assertEquals(recordList.getRecords().get(1).getActions().size(), 3);
		assertFalse(
				recordList.getRecords().get(1).getActions().contains(Action.READ_INCOMING_LINKS));
	}

	@Test
	public void testReadRecordWithDataRecordLinkHasReadActionTopLevel() {
		SpiderRecordListReader recordListReader = createRecordListReaderWithTestDataForLinkedData();

		SpiderRecordList recordList = recordListReader.readRecordList("userId", "dataWithLinks");
		SpiderDataRecord record = recordList.getRecords().get(0);
		RecordLinkTestsAsserter.assertTopLevelLinkContainsReadActionOnly(record);
	}

	private SpiderRecordListReader createRecordListReaderWithTestDataForLinkedData() {
		recordStorage = new RecordLinkTestsRecordStorage();
		return SpiderRecordListReaderImp.usingAuthorizationAndRecordStorageAndKeyCalculator(
				authorization, recordStorage, keyCalculator);
	}

	@Test
	public void testReadRecordWithDataRecordLinkHasReadActionOneLevelDown() {
		SpiderRecordListReader recordListReader = createRecordListReaderWithTestDataForLinkedData();

		SpiderRecordList recordList = recordListReader.readRecordList("userId", "dataWithLinks");
		SpiderDataRecord record = recordList.getRecords().get(1);

		RecordLinkTestsAsserter.assertOneLevelDownLinkContainsReadActionOnly(record);
	}

}
