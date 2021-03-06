/*
 * Copyright 2017, 2019 Uppsala University Library
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
import static org.testng.Assert.assertNotNull;

import java.util.Collections;
import java.util.HashMap;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.linkcollector.DataRecordLinkCollector;
import se.uu.ub.cora.bookkeeper.validator.DataValidator;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.spider.authentication.Authenticator;
import se.uu.ub.cora.spider.authentication.AuthenticatorSpy;
import se.uu.ub.cora.spider.authorization.PermissionRuleCalculator;
import se.uu.ub.cora.spider.authorization.SpiderAuthorizator;
import se.uu.ub.cora.spider.data.DataAtomicSpy;
import se.uu.ub.cora.spider.data.DataGroupSpy;
import se.uu.ub.cora.spider.dependency.RecordStorageProviderSpy;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProviderSpy;
import se.uu.ub.cora.spider.dependency.SpiderInstanceFactorySpy2;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.log.LoggerFactorySpy;
import se.uu.ub.cora.spider.record.SpiderRecordUpdaterSpy;
import se.uu.ub.cora.spider.spy.RecordStorageSpy;
import se.uu.ub.cora.spider.testdata.DataCreator2;
import se.uu.ub.cora.storage.RecordStorage;

public class UserUpdaterForAppTokenAsExtendedFunctionalityTest {

	private UserUpdaterForAppTokenAsExtendedFunctionality extendedFunctionality;

	private RecordStorage recordStorage;
	private Authenticator authenticator;
	private SpiderAuthorizator spiderAuthorizator;
	private PermissionRuleCalculator ruleCalculator;
	private DataValidator dataValidator;
	private DataRecordLinkCollector linkCollector;
	private SpiderDependencyProviderSpy dependencyProvider;
	private ExtendedFunctionalityProviderSpy extendedFunctionalityProvider;

	private SpiderInstanceFactorySpy2 spiderInstanceFactory;
	private LoggerFactorySpy loggerFactorySpy;

	@BeforeMethod
	public void setUp() {
		setUpFactoriesAndProviders();

		spiderInstanceFactory = new SpiderInstanceFactorySpy2();
		SpiderInstanceProvider.setSpiderInstanceFactory(spiderInstanceFactory);

		dependencyProvider = new SpiderDependencyProviderSpy(Collections.emptyMap());
		authenticator = new AuthenticatorSpy();
		recordStorage = new RecordStorageSpy();
		setUpDependencyProvider();
		extendedFunctionality = UserUpdaterForAppTokenAsExtendedFunctionality
				.usingSpiderDependencyProvider(dependencyProvider);
	}

	private void setUpFactoriesAndProviders() {
		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
	}

	private void setUpDependencyProvider() {
		dependencyProvider = new SpiderDependencyProviderSpy(new HashMap<>());
		dependencyProvider.authenticator = authenticator;
		dependencyProvider.spiderAuthorizator = spiderAuthorizator;
		dependencyProvider.dataValidator = dataValidator;
		RecordStorageProviderSpy recordStorageProviderSpy = new RecordStorageProviderSpy();
		recordStorageProviderSpy.recordStorage = recordStorage;
		dependencyProvider.setRecordStorageProvider(recordStorageProviderSpy);

		dependencyProvider.ruleCalculator = ruleCalculator;
		dependencyProvider.linkCollector = linkCollector;
		dependencyProvider.extendedFunctionalityProvider = extendedFunctionalityProvider;
	}

	@Test
	public void init() {
		assertNotNull(extendedFunctionality);
	}

	@Test
	public void useExtendedFunctionality() {
		DataGroup minimalAppTokenGroup = new DataGroupSpy("appToken");
		minimalAppTokenGroup.addChild(
				DataCreator2.createRecordInfoWithRecordTypeAndRecordIdAndDataDivider("appToken",
						"someAppTokenId", "cora"));
		minimalAppTokenGroup.addChild(new DataAtomicSpy("note", "my device!"));

		extendedFunctionality.useExtendedFunctionality("dummy1Token", minimalAppTokenGroup);
		SpiderRecordUpdaterSpy spiderRecordUpdaterSpy = spiderInstanceFactory.createdUpdaters
				.get(0);
		DataGroup updatedUserDataGroup = spiderRecordUpdaterSpy.record;
		DataGroup userAppTokenGroup = (DataGroup) updatedUserDataGroup
				.getFirstChildWithNameInData("userAppTokenGroup");
		assertEquals(userAppTokenGroup.getFirstAtomicValueWithNameInData("note"), "my device!");
		DataGroup apptokenLink = userAppTokenGroup.getFirstGroupWithNameInData("appTokenLink");
		assertEquals(apptokenLink.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"appToken");
		assertEquals(apptokenLink.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"someAppTokenId");
		assertNotNull(userAppTokenGroup.getRepeatId());
	}

}
