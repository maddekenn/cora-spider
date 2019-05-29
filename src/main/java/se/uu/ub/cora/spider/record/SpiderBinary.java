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
package se.uu.ub.cora.spider.record;

import se.uu.ub.cora.beefeater.authentication.User;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.spider.authentication.Authenticator;
import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.spider.record.storage.RecordStorage;

public abstract class SpiderBinary {

	protected RecordStorage recordStorage;
	protected String recordType;
	protected Authenticator authenticator;
	protected String authToken;
	protected User user;

	public void checkRecordTypeIsChildOfBinary() {
		DataGroup recordTypeDefinition = getRecordTypeDefinition();
		if (!recordTypeIsChildOfBinary(recordTypeDefinition)) {
			throw new MisuseException(
					"It is only possible to upload files to recordTypes that are children of binary");
		}
	}

	private DataGroup getRecordTypeDefinition() {
		return recordStorage.read("recordType", recordType);
	}

	private boolean recordTypeIsChildOfBinary(DataGroup dataGroup) {
		return dataGroup.containsChildWithNameInData("parentId")
				&& "binary".equals(getParentId(dataGroup));
	}

	private String getParentId(DataGroup recordTypeDefinition) {
		DataGroup parentIdGroup = recordTypeDefinition.getFirstGroupWithNameInData("parentId");
		return parentIdGroup.getFirstAtomicValueWithNameInData("linkedRecordId");
	}

	protected String extractDataDividerFromData(SpiderDataGroup spiderDataGroup) {
		SpiderDataGroup recordInfo = spiderDataGroup.extractGroup("recordInfo");
		SpiderDataGroup dataDivider = recordInfo.extractGroup("dataDivider");
		return dataDivider.extractAtomicValue("linkedRecordId");
	}

	protected void tryToGetActiveUser() {
		user = authenticator.getUserForToken(authToken);
	}
}
