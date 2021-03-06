/*
 * Copyright 2018, 2019 Uppsala University Library
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
package se.uu.ub.cora.spider.extended;

import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.spider.authentication.AuthenticatorSpy;
import se.uu.ub.cora.spider.authorization.AlwaysAuthorisedExceptStub;
import se.uu.ub.cora.spider.data.DataAtomicSpy;
import se.uu.ub.cora.spider.data.DataGroupSpy;
import se.uu.ub.cora.spider.dependency.RecordStorageProviderSpy;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProviderSpy;
import se.uu.ub.cora.spider.log.LoggerFactorySpy;
import se.uu.ub.cora.spider.spy.DataGroupTermCollectorSpy;
import se.uu.ub.cora.spider.spy.RecordIndexerSpy;
import se.uu.ub.cora.spider.spy.RecordStorageSpy;
import se.uu.ub.cora.spider.spy.SpiderRecordDeleterSpy;
import se.uu.ub.cora.spider.testdata.DataCreator2;

public class WorkOrderDeleterAsExtendedFunctionalityTest {

	SpiderDependencyProviderSpy dependencyProvider;
	WorkOrderDeleterAsExtendedFunctionality extendedFunctionality;
	DataGroupTermCollectorSpy termCollector;
	AlwaysAuthorisedExceptStub authorizer;
	AuthenticatorSpy authenticator;
	SpiderRecordDeleterSpy recordDeleter;
	private LoggerFactorySpy loggerFactorySpy;

	@BeforeMethod
	public void setUp() {
	setUpFactoriesAndProviders();

		dependencyProvider = new SpiderDependencyProviderSpy(new HashMap<>());
		dependencyProvider.recordIndexer = new RecordIndexerSpy();
		dependencyProvider.searchTermCollector = new DataGroupTermCollectorSpy();
		dependencyProvider.authenticator = new AuthenticatorSpy();

		RecordStorageProviderSpy recordStorageProviderSpy = new RecordStorageProviderSpy();
		recordStorageProviderSpy.recordStorage = new RecordStorageSpy();
		dependencyProvider.setRecordStorageProvider(recordStorageProviderSpy);

		dependencyProvider.spiderAuthorizator = new AlwaysAuthorisedExceptStub();
		setUpDependencyProvider();
	}

	private void setUpFactoriesAndProviders() {
		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
	}

	private void setUpDependencyProvider() {
		recordDeleter = new SpiderRecordDeleterSpy();
		extendedFunctionality = WorkOrderDeleterAsExtendedFunctionality.usingDeleter(recordDeleter);
		termCollector = (DataGroupTermCollectorSpy) dependencyProvider.getDataGroupTermCollector();
		authorizer = (AlwaysAuthorisedExceptStub) dependencyProvider.getSpiderAuthorizator();
		authenticator = (AuthenticatorSpy) dependencyProvider.getAuthenticator();
	}

	@Test
	public void testDeleteData() {
		DataGroup workOrder = createWorkOrderUsingId("someGeneratedId");

		extendedFunctionality.useExtendedFunctionality("someToken", workOrder);
		assertEquals(recordDeleter.deletedTypes.size(), 1);
		assertEquals(recordDeleter.deletedTypes.get(0), "workOrder");
		assertEquals(recordDeleter.deletedIds.get(0), "someGeneratedId");
	}

	private DataGroup createWorkOrderUsingId(String id) {
		DataGroup workOrder = DataCreator2.createWorkOrderWithIdAndRecordTypeAndRecordIdToIndex(id,
				"book", "book1");
		addTypeToRecordInfo(workOrder);
		return workOrder;
	}

	private void addTypeToRecordInfo(DataGroup workOrder) {
		DataGroup recordInfo = workOrder.getFirstGroupWithNameInData("recordInfo");
		DataGroup type = new DataGroupSpy("type");
		type.addChild(new DataAtomicSpy("linkedRecordType", "recordType"));
		type.addChild(new DataAtomicSpy("linkedRecordId", "workOrder"));
		recordInfo.addChild(type);
	}

	@Test
	public void testDeleteDataWithNoRightToDeleteRecordType() {
		Set<String> actions = new HashSet<>();
		actions.add("delete");

		DataGroup workOrder = createWorkOrderUsingId("someGeneratedIdDeleteNotAllowed");
		extendedFunctionality.useExtendedFunctionality("someToken", workOrder);
		assertEquals(recordDeleter.deletedTypes.size(), 0);
	}

	@Test
	public void testDeleteDataWhenNoRecordExists() {
		Set<String> actions = new HashSet<>();
		actions.add("delete");

		DataGroup workOrder = createWorkOrderUsingId("nonExistingId");
		extendedFunctionality.useExtendedFunctionality("someToken", workOrder);
		assertEquals(recordDeleter.deletedTypes.size(), 0);
	}

}
