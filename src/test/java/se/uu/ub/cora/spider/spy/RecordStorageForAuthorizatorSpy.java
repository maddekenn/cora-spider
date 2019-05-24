/*
 * Copyright 2015, 2017, 2019 Uppsala University Library
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

package se.uu.ub.cora.spider.spy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.spider.data.SpiderReadResult;
import se.uu.ub.cora.spider.record.storage.RecordNotFoundException;
import se.uu.ub.cora.spider.record.storage.RecordStorage;
import se.uu.ub.cora.spider.testdata.DataCreator;

public class RecordStorageForAuthorizatorSpy implements RecordStorage {

	public Collection<String> readLists = new ArrayList<>();
	public boolean readWasCalled = false;
	public boolean deleteWasCalled = false;
	public boolean createWasCalled = false;
	public boolean updateWasCalled = false;
	public boolean linksExist = false;
	public DataGroup createRecord;
	public String type;
	public String id;
	public int numOfTimesReadWasCalled = 0;
	public List<DataGroup> filters = new ArrayList<>();
	public boolean readListWasCalled = false;

	@Override
	public DataGroup read(String type, String id) {
		numOfTimesReadWasCalled++;
		this.type = type;
		this.id = id;
		readWasCalled = true;

		if ("collectPermissionTerm".equals(type) && "publishedStatusPermissionTerm".equals(id)) {
			return createCollectPermissionTermWIthKey("PUBLISHED_STATUS");
		}
		if ("collectPermissionTerm".equals(type) && "deletedStatusPermissionTerm".equals(id)) {
			return createCollectPermissionTermWIthKey("DELETED_STATUS");
		}
		if ("collectPermissionTerm".equals(type) && "organisationPermissionTerm".equals(id)) {
			return createCollectPermissionTermWIthKey("OWNING_ORGANISATION");
		}
		if ("collectPermissionTerm".equals(type) && "journalPermissionTerm".equals(id)) {
			return createCollectPermissionTermWIthKey("JOURNAL_ACCESS");
		}

		if ("user".equals(type)) {
			ArrayList<DataGroup> records = new ArrayList<>();
			if ("inactiveUserId".equals(id)) {
				DataGroup inactiveUser = createUserWithIdAndActiveStatus("inactiveUserId",
						"inactive");
				return inactiveUser;
			}

			if ("someUserId".equals(id)) {
				DataGroup user = createActiveUserWithIdAndAddDefaultRoles("someUserId");
				return user;
			}

			if ("userWithPermissionTerm".equals(id)) {
				DataGroup userWithPermissionTerm = createUserWithOneRoleWithOnePermission();
				return userWithPermissionTerm;
			}

			if ("userWithTwoRolesPermissionTerm".equals(id)) {
				DataGroup userWithTwoRolesAndTwoPermissionTerm = createActiveUserWithIdAndAddDefaultRoles(
						"userWithTwoRolesPermissionTerm");
				addRoleToUser("admin", userWithTwoRolesAndTwoPermissionTerm);

				List<DataGroup> userRoles = userWithTwoRolesAndTwoPermissionTerm
						.getAllGroupsWithNameInData("userRole");

				DataGroup permissionTerm = createPermissionTermWithIdAndValues(
						"organisationPermissionTerm", "system.*");
				DataGroup userRole = userRoles.get(0);
				userRole.addChild(permissionTerm);

				DataGroup permissionTerm2 = createPermissionTermWithIdAndValues(
						"journalPermissionTerm", "system.abc", "system.def");
				DataGroup userRole2 = userRoles.get(1);
				userRole2.addChild(permissionTerm2);

				DataGroup permissionTerm2_role2 = createPermissionTermWithIdAndValues(
						"organisationPermissionTerm", "system.*");
				userRole2.addChild(permissionTerm2_role2);

				return userWithTwoRolesAndTwoPermissionTerm;
			}
			if ("nonExistingUserId".equals(id)) {
				throw new RecordNotFoundException("No record exists with recordId: " + id);
			}
			// spiderReadResult.listOfDataGroups = records;
			// return spiderReadResult;
		}

		// if ("inactiveUserId".equals(id)) {
		// DataGroup user = DataGroup.withNameInData("user");
		// user.addChild(DataAtomic.withNameInDataAndValue("activeStatus", "inactive"));
		// return user;
		// }
		// if ("someUserId".equals(id)) {
		// DataGroup user = DataGroup.withNameInData("user");
		// user.addChild(DataAtomic.withNameInDataAndValue("activeStatus", "active"));
		// addRoleToUser("guest", user);
		//
		// return user;
		// }
		//
		// if ("user".equals(type) && "dummy1".equals(id)) {
		// DataGroup dataGroup = DataCreator.createRecordTypeWithIdAndUserSuppliedIdAndParentId(
		// "systemOneUser", "true", "user");
		// return dataGroup;
		// }
		//
		// if ("abstractAuthority".equals(type)) {
		// return authorityPlace0001;
		//
		// }
		// if ("place".equals(type)) {
		// return authorityPlace0001;
		// }
		// if ("child1".equals(type) && "place:0002".equals(id)) {
		//
		// return child1Place0002;
		// }

		DataGroup dataGroupToReturn = DataGroup.withNameInData("someNameInData");
		dataGroupToReturn.addChild(DataGroup.withNameInData("recordInfo"));
		return dataGroupToReturn;
	}

	private DataGroup createCollectPermissionTermWIthKey(String key) {
		DataGroup collectTerm = DataGroup.withNameInData("collectTerm");
		DataGroup extraData = DataGroup.withNameInData("extraData");
		collectTerm.addChild(extraData);
		extraData.addChild(DataAtomic.withNameInDataAndValue("permissionKey", key));

		return collectTerm;
	}

	private DataGroup createPermissionTermRulePart(String permissionTermId, String... value) {
		DataGroup permissionTermRulePart = DataGroup.withNameInData("permissionTermRulePart");
		DataGroup internalRule = DataGroup.withNameInData("rule");
		permissionTermRulePart.addChild(internalRule);
		permissionTermRulePart.setRepeatId("12");
		internalRule.addChild(
				DataAtomic.withNameInDataAndValue("linkedRecordType", "collectPermissionTerm"));
		internalRule
				.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", permissionTermId));
		for (int idx = 0; idx < value.length; idx++) {
			permissionTermRulePart.addChild(DataAtomic.withNameInDataAndValueAndRepeatId("value",
					value[idx], String.valueOf(idx)));
		}
		return permissionTermRulePart;
	}

	private DataGroup createPermissionRulePart(String permissionType, String... value) {
		DataGroup permissionRulePart = DataGroup.withNameInData("permissionRulePart");
		for (int idx = 0; idx < value.length; idx++) {
			permissionRulePart.addChild(DataAtomic.withNameInDataAndValueAndRepeatId(
					"permissionRulePartValue", value[idx], String.valueOf(idx)));
		}
		permissionRulePart.addAttributeByIdWithValue("type", permissionType);
		return permissionRulePart;
	}

	private DataGroup createPermissionRuleLink(String linkedId) {
		DataGroup permissionRuleLink = DataGroup.withNameInData("permissionRuleLink");
		permissionRuleLink
				.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "permissionRule"));
		permissionRuleLink.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", linkedId));
		return permissionRuleLink;
	}

	private DataGroup createRoleForGuest() {
		DataGroup permissionRole = DataGroup.withNameInData("permissionRole");

		DataGroup permissionRuleLink = DataGroup.withNameInData("permissionRuleLink");
		permissionRuleLink
				.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "permissionRule"));
		permissionRuleLink
				.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "authorityReader"));
		permissionRole.addChild(permissionRuleLink);

		DataGroup permissionRuleLink2 = DataGroup.withNameInData("permissionRuleLink");
		permissionRuleLink2
				.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "permissionRule"));
		permissionRuleLink2
				.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "metadataReader"));
		permissionRole.addChild(permissionRuleLink2);

		DataGroup permissionRuleLink3 = DataGroup.withNameInData("permissionRuleLink");
		permissionRuleLink3
				.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "permissionRule"));
		permissionRuleLink3
				.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "inactive"));
		permissionRole.addChild(permissionRuleLink3);

		permissionRole.addChild(DataAtomic.withNameInDataAndValue("activeStatus", "active"));

		return permissionRole;
	}

	@Override
	public void create(String type, String id, DataGroup record, DataGroup collectedTerms,
			DataGroup linkList, String dataDivider) {
		createWasCalled = true;
		createRecord = record;
	}

	@Override
	public void deleteByTypeAndId(String type, String id) {
		deleteWasCalled = true;
	}

	@Override
	public boolean linksExistForRecord(String type, String id) {
		if ("place".equals(type)) {
			if (id.equals("place:0001")) {
				return false;
			}
		} else if ("authority".equals(type)) {
			if ("place:0003".equals(id)) {
				return true;
			}
		}
		return linksExist;
	}

	@Override
	public void update(String type, String id, DataGroup record, DataGroup collectedTerms,
			DataGroup linkList, String dataDivider) {
		updateWasCalled = true;
	}

	@Override
	public SpiderReadResult readList(String type, DataGroup filter) {
		readListWasCalled = true;
		readLists.add(type);
		filters.add(filter);
		// if ("recordType".equals(type)) {
		// ArrayList<DataGroup> recordTypes = new ArrayList<>();
		// recordTypes.add(read("recordType", "abstract"));
		// recordTypes.add(read("recordType", "child1"));
		// recordTypes.add(read("recordType", "child2"));
		// recordTypes.add(read("recordType", "abstract2"));
		// recordTypes.add(read("recordType", "child1_2"));
		// recordTypes.add(read("recordType", "child2_2"));
		// recordTypes.add(read("recordType", "otherType"));
		// SpiderReadResult spiderReadResult = new SpiderReadResult();
		// spiderReadResult.listOfDataGroups = recordTypes;
		// return spiderReadResult;
		// }
		// if ("child1_2".equals(type)) {
		// throw new RecordNotFoundException("No records exists with recordType: " + type);
		// }
		SpiderReadResult spiderReadResult = new SpiderReadResult();
		spiderReadResult.listOfDataGroups = new ArrayList<>();
		spiderReadResult.totalNumberOfMatches = 199;
		return spiderReadResult;
	}

	@Override
	public SpiderReadResult readAbstractList(String type, DataGroup filter) {
		SpiderReadResult spiderReadResult = new SpiderReadResult();
		spiderReadResult.totalNumberOfMatches = 199;
		spiderReadResult.listOfDataGroups = new ArrayList<>();
		readLists.add(type);
		// if ("abstract".equals(type)) {
		// ArrayList<DataGroup> records = new ArrayList<>();
		// records.add(createChildWithRecordTypeAndRecordId("implementing1", "child1_2"));
		//
		// records.add(createChildWithRecordTypeAndRecordId("implementing2", "child2_2"));
		// spiderReadResult.listOfDataGroups = records;
		// return spiderReadResult;
		// }
		// if ("abstract2".equals(type)) {
		// ArrayList<DataGroup> records = new ArrayList<>();
		//
		// records.add(createChildWithRecordTypeAndRecordId("implementing2", "child2_2"));
		// spiderReadResult.listOfDataGroups = records;
		// return spiderReadResult;
		// }
		if ("user".equals(type)) {
			ArrayList<DataGroup> records = new ArrayList<>();

			DataGroup inactiveUser = createUserWithIdAndActiveStatus("inactiveUserId", "inactive");
			records.add(inactiveUser);

			DataGroup user = createActiveUserWithIdAndAddDefaultRoles("someUserId");
			records.add(user);

			DataGroup userWithPermissionTerm = createUserWithOneRoleWithOnePermission();
			records.add(userWithPermissionTerm);

			DataGroup userWithTwoRolesAndTwoPermissionTerm = createActiveUserWithIdAndAddDefaultRoles(
					"userWithTwoRolesPermissionTerm");
			addRoleToUser("admin", userWithTwoRolesAndTwoPermissionTerm);

			List<DataGroup> userRoles = userWithTwoRolesAndTwoPermissionTerm
					.getAllGroupsWithNameInData("userRole");

			DataGroup permissionTerm = createPermissionTermWithIdAndValues(
					"organisationPermissionTerm", "system.*");
			DataGroup userRole = userRoles.get(0);
			userRole.addChild(permissionTerm);

			DataGroup permissionTerm2 = createPermissionTermWithIdAndValues("journalPermissionTerm",
					"system.abc", "system.def");
			DataGroup userRole2 = userRoles.get(1);
			userRole2.addChild(permissionTerm2);

			DataGroup permissionTerm2_role2 = createPermissionTermWithIdAndValues(
					"organisationPermissionTerm", "system.*");
			userRole2.addChild(permissionTerm2_role2);

			records.add(userWithTwoRolesAndTwoPermissionTerm);

			spiderReadResult.listOfDataGroups = records;
			return spiderReadResult;
		}
		return spiderReadResult;
	}

	private DataGroup createUserWithOneRoleWithOnePermission() {
		DataGroup userWithPermissionTerm = createActiveUserWithIdAndAddDefaultRoles(
				"userWithPermissionTerm");
		DataGroup permissionTerm = createPermissionTermWithIdAndValues("organisationPermissionTerm",
				"system.*");

		DataGroup userRole = userWithPermissionTerm.getFirstGroupWithNameInData("userRole");
		userRole.addChild(permissionTerm);
		return userWithPermissionTerm;
	}

	private DataGroup createActiveUserWithIdAndAddDefaultRoles(String userId) {
		DataGroup userWithPermissionTerm = createUserWithIdAndActiveStatus(userId, "active");
		addRoleToUser("guest", userWithPermissionTerm);
		return userWithPermissionTerm;
	}

	private DataGroup createPermissionTermWithIdAndValues(String permissionTermId,
			String... value) {
		DataGroup permissionTerm = DataGroup.withNameInData("permissionTermRulePart");
		DataGroup rule = createLinkWithNameInDataRecordtypeAndRecordId("rule",
				"collectPermissionTerm", permissionTermId);
		permissionTerm.addChild(rule);

		for (int i = 0; i < value.length; i++) {
			permissionTerm.addChild(DataAtomic.withNameInDataAndValueAndRepeatId("value", value[i],
					String.valueOf(i)));
		}
		return permissionTerm;
	}

	private DataGroup createLinkWithNameInDataRecordtypeAndRecordId(String nameInData,
			String linkedRecordType, String linkedRecordId) {
		DataGroup link = DataGroup.withNameInData(nameInData);
		link.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", linkedRecordType));
		link.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", linkedRecordId));
		return link;
	}

	private void addRoleToUser(String roleId, DataGroup user) {
		DataGroup outerUserRole = createUserRoleWithId(roleId);
		user.addChild(outerUserRole);
	}

	private DataGroup createUserRoleWithId(String roleId) {
		DataGroup outerUserRole = DataGroup.withNameInData("userRole");
		DataGroup innerUserRole = DataGroup.withNameInData("userRole");
		innerUserRole
				.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "permissionRole"));
		innerUserRole.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", roleId));
		outerUserRole.addChild(innerUserRole);
		return outerUserRole;
	}

	private DataGroup createUserWithIdAndActiveStatus(String userId, String activeStatus) {
		DataGroup inactiveUser = DataGroup.withNameInData("user");
		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", userId));
		inactiveUser.addChild(recordInfo);
		inactiveUser.addChild(DataAtomic.withNameInDataAndValue("activeStatus", activeStatus));
		return inactiveUser;
	}

	private DataGroup createChildWithRecordTypeAndRecordId(String recordType, String recordId) {
		DataGroup child1 = DataGroup.withNameInData(recordId);
		child1.addChild(
				DataCreator.createRecordInfoWithRecordTypeAndRecordId(recordType, recordId));
		return child1;
	}

	@Override
	public DataGroup readLinkList(String type, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<DataGroup> generateLinkCollectionPointingToRecord(String type, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean recordsExistForRecordType(String type) {
		if ("child1_2".equals(type)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean recordExistsForAbstractOrImplementingRecordTypeAndRecordId(String type,
			String id) {
		return false;
	}

}
