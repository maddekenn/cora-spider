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

package se.uu.ub.cora.spider.testdata;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataResourceLink;

public class RecordLinkTestsDataCreator {

	private static final String DATA_WITH_LINKS = "dataWithLinks";

	public static DataGroup createDataGroupWithLink() {
		DataGroup dataGroup = DataGroup.withNameInData(DATA_WITH_LINKS);

		DataRecordLink spiderRecordLink = createLink();

		dataGroup.addChild(spiderRecordLink);
		return dataGroup;
	}

	private static DataRecordLink createLink() {
		DataRecordLink spiderRecordLink = DataRecordLink.withNameInData("link");
		DataAtomic linkedRecordType = DataAtomic.withNameInDataAndValue("linkedRecordType",
				"toRecordType");
		spiderRecordLink.addChild(linkedRecordType);
		DataAtomic linkedRecordId = DataAtomic.withNameInDataAndValue("linkedRecordId",
				"toRecordId");
		spiderRecordLink.addChild(linkedRecordId);
		return spiderRecordLink;
	}

	public static DataGroup createDataGroupWithLinkNotAuthorized() {
		DataGroup dataGroup = DataGroup.withNameInData(DATA_WITH_LINKS);

		DataRecordLink spiderRecordLink = DataRecordLink.withNameInData("link");
		DataAtomic linkedRecordType = DataAtomic.withNameInDataAndValue("linkedRecordType",
				"toRecordType");
		spiderRecordLink.addChild(linkedRecordType);
		DataAtomic linkedRecordId = DataAtomic.withNameInDataAndValue("linkedRecordId",
				"recordLinkNotAuthorized");
		spiderRecordLink.addChild(linkedRecordId);

		dataGroup.addChild(spiderRecordLink);
		return dataGroup;
	}

	public static DataGroup createDataGroupWithLinkOneLevelDown() {
		DataGroup dataGroup = DataGroup.withNameInData(DATA_WITH_LINKS);
		DataGroup oneLevelDown = DataGroup.withNameInData("oneLevelDown");
		dataGroup.addChild(oneLevelDown);

		DataRecordLink spiderRecordLink = DataRecordLink.withNameInData("link");

		DataAtomic linkedRecordType = DataAtomic.withNameInDataAndValue("linkedRecordType",
				"toRecordType");
		spiderRecordLink.addChild(linkedRecordType);

		DataAtomic linkedRecordId = DataAtomic.withNameInDataAndValue("linkedRecordId",
				"toRecordId");
		spiderRecordLink.addChild(linkedRecordId);

		oneLevelDown.addChild(spiderRecordLink);
		return dataGroup;
	}

	public static DataGroup createDataGroupWithLinkOneLevelDownTargetDoesNotExist() {
		DataGroup dataGroup = DataGroup.withNameInData(DATA_WITH_LINKS);
		DataGroup oneLevelDown = DataGroup.withNameInData("oneLevelDownTargetDoesNotExist");
		dataGroup.addChild(oneLevelDown);

		DataRecordLink spiderRecordLink = DataRecordLink.withNameInData("link");

		DataAtomic linkedRecordType = DataAtomic.withNameInDataAndValue("linkedRecordType",
				"toRecordType");
		spiderRecordLink.addChild(linkedRecordType);

		DataAtomic linkedRecordId = DataAtomic.withNameInDataAndValue("linkedRecordId",
				"nonExistingRecordId");
		spiderRecordLink.addChild(linkedRecordId);

		oneLevelDown.addChild(spiderRecordLink);
		return dataGroup;
	}

	public static DataGroup createDataGroupWithRecordInfoAndLink() {
		DataGroup dataGroup = createDataGroupWithLink();
		dataGroup
				.addChild(SpiderDataCreator.createRecordInfoWithRecordTypeAndRecordIdAndDataDivider(
						DATA_WITH_LINKS, "oneLinkTopLevel", "cora"));
		return dataGroup;
	}

	public static DataGroup createDataGroupWithRecordInfoAndTwoLinks() {
		DataGroup dataGroup = DataGroup.withNameInData(DATA_WITH_LINKS);
		dataGroup
				.addChild(SpiderDataCreator.createRecordInfoWithRecordTypeAndRecordIdAndDataDivider(
						DATA_WITH_LINKS, "towLinksTopLevel", "cora"));

		DataRecordLink spiderRecordLink = createLink();
		spiderRecordLink.setRepeatId("one");
		dataGroup.addChild(spiderRecordLink);

		DataRecordLink spiderRecordLink2 = createLink();
		spiderRecordLink2.setRepeatId("two");
		dataGroup.addChild(spiderRecordLink2);
		return dataGroup;

	}

	public static DataGroup createDataGroupWithRecordInfoAndLinkNotAuthorized() {
		DataGroup dataGroup = createDataGroupWithLinkNotAuthorized();
		dataGroup
				.addChild(SpiderDataCreator.createRecordInfoWithRecordTypeAndRecordIdAndDataDivider(
						DATA_WITH_LINKS, "oneLinkTopLevelNotAuthorized", "cora"));
		return dataGroup;
	}

	public static DataGroup createDataGroupWithRecordInfoAndLinkOneLevelDown() {
		DataGroup dataGroup = createDataGroupWithLinkOneLevelDown();
		dataGroup
				.addChild(SpiderDataCreator.createRecordInfoWithRecordTypeAndRecordIdAndDataDivider(
						DATA_WITH_LINKS, "oneLinkOneLevelDown", "cora"));
		return dataGroup;
	}

	public static DataGroup createDataGroupWithRecordInfoAndLinkOneLevelDownTargetDoesNotExist() {
		DataGroup dataGroup = createDataGroupWithLinkOneLevelDownTargetDoesNotExist();
		dataGroup
				.addChild(SpiderDataCreator.createRecordInfoWithRecordTypeAndRecordIdAndDataDivider(
						DATA_WITH_LINKS, "oneLinkOneLevelDownTargetDoesNotExist", "cora"));
		return dataGroup;
	}

	public static DataGroup createDataGroupWithRecordInfoAndResourceLink() {
		DataGroup dataGroup = createDataGroupWithResourceLink();
		dataGroup
				.addChild(SpiderDataCreator.createRecordInfoWithRecordTypeAndRecordIdAndDataDivider(
						DATA_WITH_LINKS, "oneResourceLinkTopLevel", "cora"));
		return dataGroup;
	}

	public static DataGroup createDataGroupWithRecordInfoAndResourceLinkOneLevelDown() {
		DataGroup dataGroup = createDataGroupWithResourceLinkOneLevelDown();
		dataGroup
				.addChild(SpiderDataCreator.createRecordInfoWithRecordTypeAndRecordIdAndDataDivider(
						DATA_WITH_LINKS, "oneResourceLinkOneLevelDown", "cora"));
		return dataGroup;
	}

	public static DataGroup createDataGroupWithResourceLink() {
		DataGroup dataGroup = DataGroup.withNameInData(DATA_WITH_LINKS);
		dataGroup.addChild(createResourceLink());
		return dataGroup;
	}

	private static DataResourceLink createResourceLink() {
		DataResourceLink spiderResourceLink = DataResourceLink.withNameInData("link");

		spiderResourceLink.addChild(DataAtomic.withNameInDataAndValue("streamId", "someStreamId"));
		spiderResourceLink.addChild(DataAtomic.withNameInDataAndValue("filename", "aFileName"));
		spiderResourceLink.addChild(DataAtomic.withNameInDataAndValue("filesize", "12345"));
		spiderResourceLink
				.addChild(DataAtomic.withNameInDataAndValue("mimeType", "application/pdf"));
		return spiderResourceLink;
	}

	public static DataGroup createDataGroupWithResourceLinkOneLevelDown() {
		DataGroup dataGroup = DataGroup.withNameInData(DATA_WITH_LINKS);
		DataGroup oneLevelDown = DataGroup.withNameInData("oneLevelDown");
		dataGroup.addChild(oneLevelDown);

		oneLevelDown.addChild(createResourceLink());

		return dataGroup;
	}

	public static DataGroup createLinkChildAsRecordDataGroup() {
		DataGroup dataGroup = DataGroup.withNameInData("toRecordType");
		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "recordLinkNotAuthorized"));

		DataGroup type = DataGroup.withNameInData("type");
		type.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "recordType"));
		type.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "toRecordType"));
		recordInfo.addChild(type);

		dataGroup.addChild(recordInfo);
		return dataGroup;
	}
}
