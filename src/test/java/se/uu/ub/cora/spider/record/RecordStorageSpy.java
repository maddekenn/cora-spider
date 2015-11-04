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

import java.util.ArrayList;
import java.util.Collection;

import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.spider.record.storage.RecordStorage;
import se.uu.ub.cora.spider.testdata.DataCreator;

public class RecordStorageSpy implements RecordStorage {

	public Collection<String> readLists = new ArrayList<>();
	public boolean deleteWasCalled = false;
	public boolean createWasCalled = false;

	@Override
	public DataGroup read(String type, String id) {

		if ("abstract".equals(id)) {
			return DataCreator.createRecordTypeWithIdAndUserSuppliedIdAndAbstract(id, "false",
					"true");
		}
		if ("child1".equals(id)) {
			return DataCreator.createRecordTypeWithIdAndUserSuppliedIdAndParentId(id, "true",
					"abstract");
		}
		if ("child2".equals(id)) {
			return DataCreator.createRecordTypeWithIdAndUserSuppliedIdAndParentId(id, "true",
					"abstract");
		}
		if ("otherType".equals(id)) {
			return DataCreator.createRecordTypeWithIdAndUserSuppliedIdAndParentId(id, "true",
					"NOT_ABSTRACT");
		}
		if ("spyType".equals(id)) {
			return DataCreator.createRecordTypeWithIdAndUserSuppliedIdAndAbstract(id, "false",
					"false");
		}
		return null;
	}

	@Override
	public void create(String type, String id, DataGroup record, DataGroup linkList) {
		createWasCalled = true;

	}

	@Override
	public void deleteByTypeAndId(String type, String id) {
		deleteWasCalled = true;
	}

	@Override
	public boolean linksExistForRecord(String type, String id) {
		return false;
	}

	@Override
	public void update(String type, String id, DataGroup record) {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<DataGroup> readList(String type) {
		readLists.add(type);
		if ("recordType".equals(type)) {
			ArrayList<DataGroup> recordTypes = new ArrayList<>();
			recordTypes.add(read("recordType", "abstract"));
			recordTypes.add(read("recordType", "child1"));
			recordTypes.add(read("recordType", "child2"));
			recordTypes.add(read("recordType", "otherType"));
			return recordTypes;
		}
		return new ArrayList<DataGroup>();
	}

	@Override
	public DataGroup readLinkList(String type, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataGroup generateLinkCollectionPointingToRecord(String type, String id) {
		// TODO Auto-generated method stub
		return null;
	}

}
