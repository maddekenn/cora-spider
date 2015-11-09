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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.beefeater.Authorizator;
import se.uu.ub.cora.beefeater.AuthorizatorImp;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.linkcollector.DataRecordLinkCollector;
import se.uu.ub.cora.bookkeeper.validator.DataValidator;
import se.uu.ub.cora.spider.data.Action;
import se.uu.ub.cora.spider.data.SpiderDataAtomic;
import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.spider.data.SpiderDataRecord;
import se.uu.ub.cora.spider.data.SpiderDataRecordLink;
import se.uu.ub.cora.spider.record.storage.RecordConflictException;
import se.uu.ub.cora.spider.record.storage.RecordIdGenerator;
import se.uu.ub.cora.spider.record.storage.RecordNotFoundException;
import se.uu.ub.cora.spider.record.storage.RecordStorage;
import se.uu.ub.cora.spider.record.storage.TimeStampIdGenerator;
import se.uu.ub.cora.spider.testdata.TestDataRecordInMemoryStorage;

public class SpiderRecordCreatorTest {
	private RecordStorage recordStorage;
	private Authorizator authorization;
	private RecordIdGenerator idGenerator;
	private PermissionKeyCalculator keyCalculator;
	private SpiderRecordCreator recordCreator;
	private DataValidator dataValidator;
	private DataRecordLinkCollector linkCollector;

	@BeforeMethod
	public void beforeMethod() {
		authorization = new AuthorizatorImp();
		dataValidator = new DataValidatorAlwaysValidSpy();
		recordStorage = TestDataRecordInMemoryStorage.createRecordStorageInMemoryWithTestData();
		idGenerator = new TimeStampIdGenerator();
		keyCalculator = new RecordPermissionKeyCalculator();
		linkCollector = new DataRecordLinkCollectorSpy();
		recordCreator = SpiderRecordCreatorImp
				.usingAuthorizationAndDataValidatorAndRecordStorageAndIdGeneratorAndKeyCalculatorAndLinkCollector(
						authorization, dataValidator, recordStorage, idGenerator, keyCalculator,
						linkCollector);

	}

	@Test
	public void testExternalDependenciesAreCalled() {
		authorization = new AuthorizatorAlwaysAuthorizedSpy();
		dataValidator = new DataValidatorAlwaysValidSpy();
		recordStorage = new RecordStorageSpy();
		idGenerator = new IdGeneratorSpy();
		keyCalculator = new KeyCalculatorSpy();
		linkCollector = new DataRecordLinkCollectorSpy();

		SpiderRecordCreator recordCreator = SpiderRecordCreatorImp
				.usingAuthorizationAndDataValidatorAndRecordStorageAndIdGeneratorAndKeyCalculatorAndLinkCollector(
						authorization, dataValidator, recordStorage, idGenerator, keyCalculator,
						linkCollector);
		SpiderDataGroup spiderDataGroup = SpiderDataGroup.withNameInData("nameInData");
		recordCreator.createAndStoreRecord("userId", "spyType", spiderDataGroup);

		assertTrue(((AuthorizatorAlwaysAuthorizedSpy) authorization).authorizedWasCalled);
		assertTrue(((DataValidatorAlwaysValidSpy) dataValidator).validateDataWasCalled);
		assertTrue(((RecordStorageSpy) recordStorage).createWasCalled);
		assertTrue(((IdGeneratorSpy) idGenerator).getIdForTypeWasCalled);
		assertTrue(((KeyCalculatorSpy) keyCalculator).calculateKeysWasCalled);
		assertTrue(((DataRecordLinkCollectorSpy) linkCollector).collectLinksWasCalled);

	}

	@Test(expectedExceptions = DataException.class)
	public void testCreateRecordInvalidData() {
		DataValidator dataValidator = new DataValidatorAlwaysInvalidSpy();
		SpiderRecordCreator recordCreator = SpiderRecordCreatorImp
				.usingAuthorizationAndDataValidatorAndRecordStorageAndIdGeneratorAndKeyCalculatorAndLinkCollector(
						authorization, dataValidator, recordStorage, idGenerator, keyCalculator,
						linkCollector);
		SpiderDataGroup spiderDataGroup = SpiderDataGroup.withNameInData("nameInData");
		recordCreator.createAndStoreRecord("userId", "recordType", spiderDataGroup);
	}

	@Test
	public void testCreateRecordAutogeneratedId() {
		RecordStorageCreateUpdateSpy recordStorage = new RecordStorageCreateUpdateSpy();
		recordCreator = SpiderRecordCreatorImp
				.usingAuthorizationAndDataValidatorAndRecordStorageAndIdGeneratorAndKeyCalculatorAndLinkCollector(
						authorization, dataValidator, recordStorage, idGenerator, keyCalculator,
						linkCollector);

		SpiderDataGroup record = SpiderDataGroup.withNameInData("typeWithAutoGeneratedId");

		SpiderDataRecord recordOut = recordCreator.createAndStoreRecord("userId",
				"typeWithAutoGeneratedId", record);
		SpiderDataGroup groupOut = recordOut.getSpiderDataGroup();
		SpiderDataGroup recordInfo = groupOut.extractGroup("recordInfo");
		String recordId = recordInfo.extractAtomicValue("id");

		assertNotNull(recordId, "A new record should have an id");

		assertEquals(recordInfo.extractAtomicValue("createdBy"), "userId");
		assertEquals(recordInfo.extractAtomicValue("type"), "typeWithAutoGeneratedId");

		DataGroup groupCreated = recordStorage.createRecord;
		assertEquals(groupOut.getNameInData(), groupCreated.getNameInData(),
				"Returned and read record should have the same nameInData");
	}

	@Test
	public void testCreateRecordUserSuppliedId() {
		RecordStorageCreateUpdateSpy recordStorage = new RecordStorageCreateUpdateSpy();
		recordCreator = SpiderRecordCreatorImp
				.usingAuthorizationAndDataValidatorAndRecordStorageAndIdGeneratorAndKeyCalculatorAndLinkCollector(
						authorization, dataValidator, recordStorage, idGenerator, keyCalculator,
						linkCollector);

		SpiderDataGroup record = SpiderDataGroup.withNameInData("typeWithUserGeneratedId");
		SpiderDataGroup createRecordInfo = SpiderDataGroup.withNameInData("recordInfo");
		createRecordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("id", "place"));
		record.addChild(createRecordInfo);

		SpiderDataRecord recordOut = recordCreator.createAndStoreRecord("userId",
				"typeWithUserGeneratedId", record);
		SpiderDataGroup groupOut = recordOut.getSpiderDataGroup();
		SpiderDataGroup recordInfo = groupOut.extractGroup("recordInfo");
		String recordId = recordInfo.extractAtomicValue("id");
		assertNotNull(recordId, "A new record should have an id");

		assertEquals(recordInfo.extractAtomicValue("createdBy"), "userId");
		assertEquals(recordInfo.extractAtomicValue("type"), "typeWithUserGeneratedId");

		DataGroup groupCreated = recordStorage.createRecord;
		assertEquals(groupOut.getNameInData(), groupCreated.getNameInData(),
				"Returned and read record should have the same nameInData");

	}

	@Test(expectedExceptions = AuthorizationException.class)
	public void testCreateRecordUnauthorized() {
		SpiderDataGroup record = SpiderDataGroup.withNameInData("authority");
		recordCreator.createAndStoreRecord("unauthorizedUserId", "place", record);
	}

	@Test(expectedExceptions = RecordNotFoundException.class)
	public void testNonExistingRecordType() {
		SpiderDataGroup record = SpiderDataGroup.withNameInData("authority");
		recordCreator.createAndStoreRecord("userId", "recordType_NOT_EXISTING", record);
	}

	@Test(expectedExceptions = MisuseException.class)
	public void testCreateRecordAbstractRecordType() {
		SpiderRecordCreator recordCreator = SpiderRecordCreatorImp
				.usingAuthorizationAndDataValidatorAndRecordStorageAndIdGeneratorAndKeyCalculatorAndLinkCollector(
						authorization, dataValidator, new RecordStorageSpy(), idGenerator,
						keyCalculator, linkCollector);

		SpiderDataGroup record = SpiderDataGroup.withNameInData("abstract");
		recordCreator.createAndStoreRecord("userId", "abstract", record);
	}

	@Test(expectedExceptions = RecordConflictException.class)
	public void testCreateRecordDuplicateUserSuppliedId() {
		SpiderRecordCreator recordCreator = SpiderRecordCreatorImp
				.usingAuthorizationAndDataValidatorAndRecordStorageAndIdGeneratorAndKeyCalculatorAndLinkCollector(
						authorization, dataValidator, recordStorage, idGenerator, keyCalculator,
						linkCollector);
		SpiderDataGroup record = SpiderDataGroup.withNameInData("place");
		SpiderDataGroup createRecordInfo = SpiderDataGroup.withNameInData("recordInfo");
		createRecordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("id", "place"));
		record.addChild(createRecordInfo);

		recordCreator.createAndStoreRecord("userId", "place", record);
		recordCreator.createAndStoreRecord("userId", "place", record);
	}

	@Test
	public void testActionsOnCreatedRecord() {
		RecordStorageCreateUpdateSpy recordStorage = new RecordStorageCreateUpdateSpy();
		recordCreator = SpiderRecordCreatorImp
				.usingAuthorizationAndDataValidatorAndRecordStorageAndIdGeneratorAndKeyCalculatorAndLinkCollector(
						authorization, dataValidator, recordStorage, idGenerator, keyCalculator,
						linkCollector);

		SpiderDataGroup record = SpiderDataGroup.withNameInData("typeWithAutoGeneratedId");

		SpiderDataRecord recordOut = recordCreator.createAndStoreRecord("userId",
				"typeWithAutoGeneratedId", record);
		assertEquals(recordOut.getActions().size(), 3);
	}

	@Test
	public void testCreateRecordWithDataRecordLinkHasReadActionTopLevel() {
		SpiderDataGroup dataGroup = SpiderDataGroup.withNameInData("dataWithLinks");
		SpiderDataRecordLink dataRecordLink = SpiderDataRecordLink
				.withNameInDataAndLinkedRecordTypeAndLinkedRecordId("link", "toRecordType",
						"toRecordId");
		dataGroup.addChild(dataRecordLink);

		SpiderRecordCreator recordCreator = createRecordCreatorWithTestDataForLinkedData();
		SpiderDataRecord record = recordCreator.createAndStoreRecord("userId", "dataWithLinks",
				dataGroup);

		assertTopLevelLinkContainsReadActionOnly(record);
	}

	private SpiderRecordCreator createRecordCreatorWithTestDataForLinkedData() {
		recordStorage = new RecordStorageProvidingDataForDataRecordLinkTests();
		return SpiderRecordCreatorImp
				.usingAuthorizationAndDataValidatorAndRecordStorageAndIdGeneratorAndKeyCalculatorAndLinkCollector(
						authorization, dataValidator, recordStorage, idGenerator, keyCalculator,
						linkCollector);
	}

	private void assertTopLevelLinkContainsReadActionOnly(SpiderDataRecord record) {
		SpiderDataRecordLink link = getLinkFromRecord(record);
		assertTrue(link.getActions().contains(Action.READ));
		assertEquals(link.getActions().size(), 1);
	}

	private SpiderDataRecordLink getLinkFromRecord(SpiderDataRecord record) {
		SpiderDataGroup spiderDataGroup = record.getSpiderDataGroup();
		SpiderDataRecordLink link = (SpiderDataRecordLink) spiderDataGroup
				.getFirstChildWithNameInData("link");
		return link;
	}

	@Test
	public void testCreateRecordWithDataRecordLinkHasReadActionOneLevelDown() {
		SpiderDataGroup dataGroup = SpiderDataGroup.withNameInData("dataWithLinks");
		SpiderDataGroup oneLevelDown = SpiderDataGroup.withNameInData("oneLevelDown");
		dataGroup.addChild(oneLevelDown);
		SpiderDataRecordLink dataRecordLink = SpiderDataRecordLink
				.withNameInDataAndLinkedRecordTypeAndLinkedRecordId("link", "toRecordType",
						"toRecordId");
		oneLevelDown.addChild(dataRecordLink);

		SpiderRecordCreator recordCreator = createRecordCreatorWithTestDataForLinkedData();
		SpiderDataRecord record = recordCreator.createAndStoreRecord("userId", "dataWithLinks",
				dataGroup);

		assertOneLevelDownLinkContainsReadActionOnly(record);
	}

	private void assertOneLevelDownLinkContainsReadActionOnly(SpiderDataRecord record) {
		SpiderDataGroup spiderDataGroup = record.getSpiderDataGroup();
		SpiderDataGroup spiderDataGroupOneLevelDown = (SpiderDataGroup) spiderDataGroup
				.getFirstChildWithNameInData("oneLevelDown");
		SpiderDataRecordLink link = (SpiderDataRecordLink) spiderDataGroupOneLevelDown
				.getFirstChildWithNameInData("link");
		assertTrue(link.getActions().contains(Action.READ));
		assertEquals(link.getActions().size(), 1);
	}
}
