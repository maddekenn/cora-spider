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

package se.uu.ub.cora.spider.testdata;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.spider.record.storage.RecordStorageInMemory;

public class TestDataAppTokenStorage {

	public static RecordStorageInMemory createRecordStorageInMemoryWithTestData() {
		RecordStorageInMemory recordsInMemory = new RecordStorageInMemory();
		addRecordType(recordsInMemory);
		addRecordTypeRecordType(recordsInMemory);
		addRecordTypeSystemOneUser(recordsInMemory);
		addRecordTypeSystemTwoUser(recordsInMemory);
		addRecordTypeAppToken(recordsInMemory);
		addRecordTypeImage(recordsInMemory);

		DataGroup dummyUser1 = DataGroup.withNameInData("user");
		recordsInMemory.create("systemOneUser", "dummy1", dummyUser1,
				DataGroup.withNameInData("collectedLinksList"), "systemOne");

		DataGroup dummyUser2 = DataGroup.withNameInData("user");
		recordsInMemory.create("systemTwoUser", "dummy2", dummyUser2,
				DataGroup.withNameInData("collectedLinksList"), "systemOne");

		// DataGroup dummyUser2 = DataGroup.withNameInData("user");
		// String inactiveUserJson =
		// "{\"name\":\"user\",\"children\":[{\"name\":\"recordInfo\",\"children\":[{\"name\":\"id\",\"value\":\"inactiveUser\"},{\"name\":\"type\",\"value\":\"systemTwoUser\"},{\"name\":\"createdBy\",\"value\":\"131313\"},{\"name\":\"dataDivider\",\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"systemOne\"}]}]},{\"name\":\"userId\",\"value\":\"dummy@ub.uu.se\"},{\"name\":\"userFirstname\",\"value\":\"Dummy\"},{\"name\":\"userLastname\",\"value\":\"Dumsson\"},{\"name\":\"userRole\",\"children\":[{\"name\":\"userRole\",\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"permissionRole\"},{\"name\":\"linkedRecordId\",\"value\":\"nothing\"}]},{\"name\":\"userRoleRulePart\",\"children\":[{\"name\":\"permissionRulePart\",\"children\":[{\"name\":\"permissionRulePartValue\",\"value\":\"system.\",\"repeatId\":\"0\"}],\"attributes\":{\"type\":\"organisation\"}}]}],\"repeatId\":\"0\"},{\"name\":\"activeStatus\",\"value\":\"inactive\"},{\"name\":\"userAppTokenGroup\",\"children\":[{\"name\":\"appTokenLink\",\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"appToken\"},{\"name\":\"linkedRecordId\",\"value\":\"appTokenJson\"}]},{\"name\":\"note\",\"value\":\"My
		// phone\"}],\"repeatId\":\"1\"}]}";
		// DataGroup inactiveUser =
		// convertJsonStringToDataGroup(inactiveUserJson);
		// recordsInMemory.create("systemTwoUser", "inactiveUser", inactiveUser,
		// DataGroup.withNameInData("collectedLinksList"), "systemTwo");

		// String noAppTokenUserJson =
		// "{\"name\":\"user\",\"children\":[{\"name\":\"recordInfo\",\"children\":[{\"name\":\"id\",\"value\":\"noAppTokenUser\"},{\"name\":\"type\",\"value\":\"systemTwoUser\"},{\"name\":\"createdBy\",\"value\":\"131313\"},{\"name\":\"dataDivider\",\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"systemOne\"}]}]},{\"name\":\"userId\",\"value\":\"dummy@ub.uu.se\"},{\"name\":\"userFirstname\",\"value\":\"Dummy\"},{\"name\":\"userLastname\",\"value\":\"Dumsson\"},{\"name\":\"userRole\",\"children\":[{\"name\":\"userRole\",\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"permissionRole\"},{\"name\":\"linkedRecordId\",\"value\":\"nothing\"}]},{\"name\":\"userRoleRulePart\",\"children\":[{\"name\":\"permissionRulePart\",\"children\":[{\"name\":\"permissionRulePartValue\",\"value\":\"system.\",\"repeatId\":\"0\"}],\"attributes\":{\"type\":\"organisation\"}}]}],\"repeatId\":\"0\"},{\"name\":\"activeStatus\",\"value\":\"inactive\"}]}";
		// DataGroup noAppTokenUser =
		// convertJsonStringToDataGroup(noAppTokenUserJson);
		// recordsInMemory.create("systemTwoUser", "noAppTokenUser",
		// noAppTokenUser,
		// DataGroup.withNameInData("collectedLinksList"), "systemTwo");

		return recordsInMemory;
	}

	private static void addRecordType(RecordStorageInMemory recordsInMemory) {
		String recordType = "recordType";
		DataGroup dataGroup = DataGroup.withNameInData(recordType);

		DataGroup recordInfo = DataCreator.createRecordInfoWithRecordTypeAndRecordId(recordType,
				"metadata");
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withNameInDataAndValue("abstract", "false"));
		recordsInMemory.create(recordType, "metadata", dataGroup,
				DataGroup.withNameInData("collectedLinksList"), "cora");
	}

	private static void addRecordTypeRecordType(RecordStorageInMemory recordsInMemory) {
		String recordType = "recordType";
		DataGroup dataGroup = DataCreator
				.createRecordTypeWithIdAndUserSuppliedIdAndAbstract("recordType", "true", "false");
		recordsInMemory.create(recordType, "recordType", dataGroup,
				DataGroup.withNameInData("collectedLinksList"), "cora");
	}

	private static void addRecordTypeImage(RecordStorageInMemory recordsInMemory) {
		String recordType = "recordType";
		DataGroup dataGroup = DataCreator
				.createRecordTypeWithIdAndUserSuppliedIdAndParentId("image", "true", "binary");
		recordsInMemory.create(recordType, "image", dataGroup,
				DataGroup.withNameInData("collectedLinksList"), "cora");
	}

	private static void addRecordTypeSystemOneUser(RecordStorageInMemory recordsInMemory) {
		String recordType = "recordType";
		DataGroup dataGroup = DataCreator.createRecordTypeWithIdAndUserSuppliedIdAndParentId(
				"systemOneUser", "true", "user");
		recordsInMemory.create(recordType, "systemOneUser", dataGroup,
				DataGroup.withNameInData("collectedLinksList"), "cora");
	}

	private static void addRecordTypeSystemTwoUser(RecordStorageInMemory recordsInMemory) {
		String recordType = "recordType";
		DataGroup dataGroup = DataCreator.createRecordTypeWithIdAndUserSuppliedIdAndParentId(
				"systemTwoUser", "true", "user");
		recordsInMemory.create(recordType, "systemTwoUser", dataGroup,
				DataGroup.withNameInData("collectedLinksList"), "cora");
	}

	private static void addRecordTypeAppToken(RecordStorageInMemory recordsInMemory) {
		String recordType = "recordType";
		DataGroup dataGroup = DataCreator
				.createRecordTypeWithIdAndUserSuppliedIdAndAbstract("appToken", "false", "false");
		recordsInMemory.create(recordType, "appToken", dataGroup,
				DataGroup.withNameInData("collectedLinksList"), "cora");

	}
}