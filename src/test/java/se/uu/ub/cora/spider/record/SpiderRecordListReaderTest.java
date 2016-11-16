/*
 * Copyright 2015, 2016 Uppsala University Library
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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.spider.authentication.AuthenticationException;
import se.uu.ub.cora.spider.authentication.Authenticator;
import se.uu.ub.cora.spider.authentication.AuthenticatorSpy;
import se.uu.ub.cora.spider.authorization.AuthorizationException;
import se.uu.ub.cora.spider.authorization.NeverAuthorisedStub;
import se.uu.ub.cora.spider.authorization.PermissionRuleCalculator;
import se.uu.ub.cora.spider.authorization.SpiderAuthorizator;
import se.uu.ub.cora.spider.data.SpiderData;
import se.uu.ub.cora.spider.data.SpiderDataList;
import se.uu.ub.cora.spider.data.SpiderDataRecord;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProviderSpy;
import se.uu.ub.cora.spider.record.storage.RecordStorage;
import se.uu.ub.cora.spider.spy.AuthorizatorAlwaysAuthorizedSpy;
import se.uu.ub.cora.spider.spy.NoRulesCalculatorStub;
import se.uu.ub.cora.spider.spy.RecordStorageSpy;
import se.uu.ub.cora.spider.testdata.TestDataRecordInMemoryStorage;

public class SpiderRecordListReaderTest {

	private RecordStorage recordStorage;
	private Authenticator authenticator;
	private SpiderAuthorizator authorizator;
	private PermissionRuleCalculator keyCalculator;
	private SpiderDependencyProviderSpy dependencyProvider;
	private SpiderRecordListReader recordListReader;
	private DataGroupToRecordEnhancerSpy dataGroupToRecordEnhancer;

	@BeforeMethod
	public void beforeMethod() {
		authenticator = new AuthenticatorSpy();
		authorizator = new AuthorizatorAlwaysAuthorizedSpy();
		recordStorage = TestDataRecordInMemoryStorage.createRecordStorageInMemoryWithTestData();
		keyCalculator = new NoRulesCalculatorStub();
		setUpDependencyProvider();
	}

	private void setUpDependencyProvider() {
		dependencyProvider = new SpiderDependencyProviderSpy();
		dependencyProvider.authenticator = authenticator;
		dependencyProvider.spiderAuthorizator = authorizator;
		dependencyProvider.recordStorage = recordStorage;
		dependencyProvider.keyCalculator = keyCalculator;
		// SpiderInstanceFactory factory = SpiderInstanceFactoryImp
		// .usingDependencyProvider(dependencyProvider);
		// SpiderInstanceProvider.setSpiderInstanceFactory(factory);
		dataGroupToRecordEnhancer = new DataGroupToRecordEnhancerSpy();
		recordListReader = SpiderRecordListReaderImp
				.usingDependencyProviderAndDataGroupToRecordEnhancer(dependencyProvider,
						dataGroupToRecordEnhancer);
	}

	@Test(expectedExceptions = AuthenticationException.class)
	public void testAuthenticationNotAuthenticated() {
		recordStorage = new RecordStorageSpy();
		setUpDependencyProvider();
		recordListReader.readRecordList("dummyNonAuthenticatedToken", "spyType");
	}

	@Test
	public void testReadListAuthorized() {
		String userId = "someToken78678567";
		String type = "place";
		SpiderDataList readRecordList = recordListReader.readRecordList(userId, type);
		assertEquals(readRecordList.getTotalNumberOfTypeInStorage(), "2",
				"Total number of records should be 2");
		assertEquals(readRecordList.getFromNo(), "1");
		assertEquals(readRecordList.getToNo(), "2");
		List<SpiderData> records = readRecordList.getDataList();
		SpiderDataRecord spiderDataRecord = (SpiderDataRecord) records.iterator().next();
		assertNotNull(spiderDataRecord);
	}

	@Test
	public void testRecordEnhancerCalled() {
		recordListReader.readRecordList("someToken78678567", "place");
		assertEquals(dataGroupToRecordEnhancer.user.id, "12345");
		assertEquals(dataGroupToRecordEnhancer.recordType, "place");
		assertEquals(dataGroupToRecordEnhancer.dataGroup.getFirstGroupWithNameInData("recordInfo")
				.getFirstAtomicValueWithNameInData("id"), "place:0002");
	}

	@Test
	public void testReadListAbstractRecordType() {
		recordStorage = new RecordStorageSpy();
		setUpDependencyProvider();
		recordListReader.readRecordList("someToken78678567", "abstract");

		assertTrue(((RecordStorageSpy) recordStorage).readLists.contains("child1"));
		assertTrue(((RecordStorageSpy) recordStorage).readLists.contains("child2"));
	}

	@Test
	public void testReadListAbstractRecordTypeNoDataForOneRecordType() {
		recordStorage = new RecordStorageSpy();
		setUpDependencyProvider();

		recordListReader.readRecordList("someToken78678567", "abstract2");
		assertFalse(((RecordStorageSpy) recordStorage).readLists.contains("child1_2"));
		assertTrue(((RecordStorageSpy) recordStorage).readLists.contains("child2_2"));
	}

	@Test(expectedExceptions = AuthorizationException.class)
	public void testReadListUnauthorized() {
		authorizator = new NeverAuthorisedStub();
		setUpDependencyProvider();
		recordListReader.readRecordList("someToken78678567", "place");
	}
}
