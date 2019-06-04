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

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataGroup;

public final class SpiderDataCreator {

	public static DataGroup createRecordInfoWithRecordType(String recordType) {
		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		DataGroup typeGroup = DataGroup.withNameInData("type");
		typeGroup.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "recordType"));
		typeGroup.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", recordType));
		recordInfo.addChild(typeGroup);
		// recordInfo.addChild(DataAtomic.withNameInDataAndValue("type",
		// recordType));
		return recordInfo;
	}

	public static DataGroup createRecordInfoWithRecordTypeAndRecordIdAndDataDivider(
			String recordType, String recordId, String dataDivider) {
		DataGroup recordInfo = createRecordInfoWithRecordType(recordType);
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", recordId));
		recordInfo.addChild(createDataDividerWithLinkedRecordId(dataDivider));
		return recordInfo;
	}

	public static DataGroup createDataDividerWithLinkedRecordId(String linkedRecordId) {
		DataGroup dataDivider = DataGroup.withNameInData("dataDivider");
		dataDivider.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "system"));
		dataDivider.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", linkedRecordId));
		return dataDivider;
	}

	public static DataGroup createSearchWithIdAndRecordTypeToSearchIn(String id,
			String idRecordTypeToSearchIn) {
		DataGroup search = DataGroup.withNameInData("search");
		DataGroup recordInfo = SpiderDataCreator.createRecordInfoWithRecordType("search");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", id));
		search.addChild(recordInfo);

		DataGroup recordTypeToSearchIn = DataGroup.withNameInData("recordTypeToSearchIn");
		recordTypeToSearchIn
				.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "recordType"));
		recordTypeToSearchIn.addChild(
				DataAtomic.withNameInDataAndValue("linkedRecordId", idRecordTypeToSearchIn));
		search.addChild(recordTypeToSearchIn);
		return search;
	}
}
