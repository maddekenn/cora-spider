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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.spider.data.DataAtomicSpy;
import se.uu.ub.cora.spider.data.DataGroupSpy;
import se.uu.ub.cora.spider.testdata.DataCreator;
import se.uu.ub.cora.spider.testdata.DataCreator2;
import se.uu.ub.cora.spider.testdata.RecordLinkTestsDataCreator;
import se.uu.ub.cora.storage.RecordNotFoundException;
import se.uu.ub.cora.storage.RecordStorage;
import se.uu.ub.cora.storage.StorageReadResult;

public class RecordEnhancerTestsRecordStorage implements RecordStorage {

	public boolean recordIdExistsForRecordType = true;
	public boolean createWasRead = false;
	public String publicReadForToRecordType = "false";

	public List<String> readList = new ArrayList<>();
	public Map<String, Integer> readNumberMap = new TreeMap<>();

	@Override
	public DataGroup read(String type, String id) {
		String readKey = type + ":" + id;
		readList.add(readKey);
		if (!readNumberMap.containsKey(readKey)) {
			readNumberMap.put(readKey, 1);
		} else {
			readNumberMap.put(readKey, readNumberMap.get(readKey) + 1);
		}

		if (type.equals("recordType")) {
			if ("binary".equals(id)) {
				return DataCreator.createRecordTypeWithIdAndUserSuppliedIdAndAbstractAndPublicRead(
						id, "false", "true", "false");
			} else if ("image".equals(id)) {
				return DataCreator.createRecordTypeWithIdAndUserSuppliedIdAndParentId(id, "false",
						"binary");
			} else if ("recordType".equals(id)) {
				DataGroup dataGroup = DataCreator
						.createRecordTypeWithIdAndUserSuppliedIdAndAbstractAndPublicRead(id,
								"false", "true", "false");
				DataGroup search = new DataGroupSpy("search");
				search.addChild(new DataAtomicSpy("linkedRecordType", "search"));
				search.addChild(new DataAtomicSpy("linkedRecordId", "someDefaultSearch"));
				// .asLinkWithNameInDataAndTypeAndId("search", "search",
				// "someDefaultSearch");
				dataGroup.addChild(search);
				return dataGroup;
			} else if (("place".equals(id))) {
				return DataCreator.createRecordTypeWithIdAndUserSuppliedIdAndParentId(id, "false",
						"authority");
			} else if (("toRecordType".equals(id))) {
				return DataCreator.createRecordTypeWithIdAndUserSuppliedIdAndAbstractAndPublicRead(
						id, "false", "true", publicReadForToRecordType);
			}
			return DataCreator.createRecordTypeWithIdAndUserSuppliedIdAndAbstractAndPublicRead(
					"dataWithLinks", "false", "false", "false");
		}
		if (type.equals("dataWithLinks")) {
			if (id.equals("oneLinkTopLevel")) {
				return RecordLinkTestsDataCreator.createDataGroupWithRecordInfoAndLink();
			}
			if (id.equals("twoLinksTopLevel")) {
				return RecordLinkTestsDataCreator.createDataGroupWithRecordInfoAndTwoLinks();
			}
			if (id.equals("oneLinkTopLevelNotAuthorized")) {
				return RecordLinkTestsDataCreator
						.createDataGroupWithRecordInfoAndLinkNotAuthorized();
			}
			if (id.equals("oneLinkOneLevelDown")) {
				return RecordLinkTestsDataCreator
						.createDataDataGroupWithRecordInfoAndLinkOneLevelDown();
			}
			if (id.equals("oneLinkOneLevelDownTargetDoesNotExist")) {
				return RecordLinkTestsDataCreator
						.createDataDataGroupWithRecordInfoAndLinkOneLevelDownTargetDoesNotExist();
			}
		}
		if (type.equals("dataWithResourceLinks")) {
			if (id.equals("oneResourceLinkTopLevel")) {
				return RecordLinkTestsDataCreator.createDataGroupWithRecordInfoAndResourceLink();
			}
			if (id.equals("oneResourceLinkOneLevelDown")) {
				return RecordLinkTestsDataCreator
						.createDataDataGroupWithRecordInfoAndResourceLinkOneLevelDown();
			}
		}
		if (type.equals("toRecordType")) {
			if (id.equals("recordLinkNotAuthorized")) {
				return RecordLinkTestsDataCreator.createLinkChildAsDataRecordDataGroup();
			}
		}
		if (type.equals("search")) {
			if ("aSearchId".equals(id)) {
				return DataCreator2.createSearchWithIdAndRecordTypeToSearchIn("aSearchId", "place");
			} else if ("anotherSearchId".equals(id)) {
				return DataCreator2.createSearchWithIdAndRecordTypeToSearchIn("anotherSearchId",
						"image");
			} else if ("someDefaultSearch".equals(id)) {
				return DataCreator2.createSearchWithIdAndRecordTypeToSearchIn("someDefaultSearch",
						"someRecordType");
			}
		}
		if (type.equals("system")) {
			if (id.equals("cora")) {
				return DataCreator.createDataGroupWithNameInDataTypeAndId("system", type, id);
			}
		}
		if ("image".equals(type)) {
			return DataCreator.createDataGroupWithNameInDataTypeAndId("binary", "image",
					"image:0001");
		}
		if ("place".equals(type)) {
			return DataCreator.createDataGroupWithNameInDataTypeAndId("authority", "place", id);
		}
		if ("nonExistingRecordId".equals(id)) {
			throw new RecordNotFoundException("no record with id " + id + " exists");
		}
		// return null;
		return DataCreator.createDataGroupWithNameInDataTypeAndId("noData", type, id);
	}

	@Override
	public void create(String type, String id, DataGroup record, DataGroup collectedTerms,
			DataGroup linkList, String dataDivider) {
		createWasRead = true;

	}

	@Override
	public void deleteByTypeAndId(String type, String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean linksExistForRecord(String type, String id) {
		if ("place".equals(type)) {
			if (id.equals("place:0001")) {
				return true;
			}
		} else if ("authority".equals(type)) {
			if ("place:0003".equals(id)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void update(String type, String id, DataGroup record, DataGroup collectedTerms,
			DataGroup linkList, String dataDivider) {
		// TODO Auto-generated method stub

	}

	@Override
	public StorageReadResult readList(String type, DataGroup filter) {
		List<DataGroup> list = new ArrayList<>();
		list.add(read(type, "oneLinkTopLevel"));
		list.add(read(type, "oneLinkOneLevelDown"));
		StorageReadResult spiderReadResult = new StorageReadResult();
		spiderReadResult.listOfDataGroups = list;
		return spiderReadResult;
	}

	@Override
	public StorageReadResult readAbstractList(String type, DataGroup filter) {
		return null;
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean recordExistsForAbstractOrImplementingRecordTypeAndRecordId(String type,
			String id) {
		return recordIdExistsForRecordType;
	}

}
