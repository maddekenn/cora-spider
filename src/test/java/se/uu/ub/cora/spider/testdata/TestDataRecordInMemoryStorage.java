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

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
//import se.uu.ub.cora.bookkeeper.data.DataRecordLink;
import se.uu.ub.cora.spider.record.storage.RecordStorage;
import se.uu.ub.cora.spider.record.storage.RecordStorageInMemoryStub;

public class TestDataRecordInMemoryStorage {

	private static String dataDivider = "cora";

	public static RecordStorageInMemoryStub createRecordStorageInMemoryWithTestData() {
		RecordStorageInMemoryStub recordsInMemory = new RecordStorageInMemoryStub();
		addPlace(recordsInMemory);
		addSecondPlace(recordsInMemory);
		addThirdPlace(recordsInMemory);
		addFourthPlace(recordsInMemory);
		addMetadata(recordsInMemory);
		addMetadataForBinary(recordsInMemory);
		addPresentation(recordsInMemory);
		addText(recordsInMemory);
		addRecordType(recordsInMemory);
		addImageOne(recordsInMemory);
		addRecordTypeRecordType(recordsInMemory);
		addRecordTypeBinary(recordsInMemory);
		addRecordTypeUser(recordsInMemory);
		addRecordTypeImage(recordsInMemory);
		addRecordTypeRecordTypeAutoGeneratedId(recordsInMemory);
		addRecordTypePlace(recordsInMemory);
		addRecordTypeSearch(recordsInMemory);
		addRecordTypeSystem(recordsInMemory);
		addSearch(recordsInMemory);
		addSystem(recordsInMemory);
		addSearchWithTwoRecordTypeToSearchIn(recordsInMemory);
		addRecordTypeAbstractAuthority(recordsInMemory);
		addImage(recordsInMemory);

		DataGroup dummy = DataGroup.withNameInData("dummy");
		recordsInMemory.create("metadataCollectionVariable", "dummy1", dummy, null,
				DataGroup.withNameInData("collectedLinksList"), dataDivider);
		recordsInMemory.create("metadataCollectionVariableChild", "dummy1", dummy, null,
				DataGroup.withNameInData("dummy"), dataDivider);
		recordsInMemory.create("metadataItemCollection", "dummy1", dummy, null,
				DataGroup.withNameInData("collectedLinksList"), dataDivider);
		recordsInMemory.create("metadataCollectionItem", "dummy1", dummy, null,
				DataGroup.withNameInData("collectedLinksList"), dataDivider);
		recordsInMemory.create("metadataTextVariable", "dummy1", dummy, null,
				DataGroup.withNameInData("collectedLinksList"), dataDivider);
		recordsInMemory.create("metadataRecordLink", "dummy1", dummy, null,
				DataGroup.withNameInData("collectedLinksList"), dataDivider);
		recordsInMemory.create("metadataRecordRelation", "dummyRecordRelation", dummy, null,
				DataGroup.withNameInData("collectedLinksList"), dataDivider);
		recordsInMemory.create("permissionRole", "dummyPermissionRole", dummy, null,
				DataGroup.withNameInData("collectedLinksList"), dataDivider);
		return recordsInMemory;
	}

	private static void addPlace(RecordStorageInMemoryStub recordsInMemory) {
		DataGroup recordInfo = DataCreator.createRecordInfoWithRecordTypeAndRecordId("place",
				"place:0001");
		DataGroup dataGroup = DataGroup.withNameInData("authority");
		dataGroup.addChild(recordInfo);
		recordsInMemory.create("place", "place:0001", dataGroup, null,
				DataGroup.withNameInData("collectedLinksList"), dataDivider);
	}

	private static void addSecondPlace(RecordStorage recordsInMemory) {
		DataGroup dataGroup = DataCreator.createRecordWithNameInDataAndIdAndTypeAndLinkedRecordId(
				"authority", "place:0002", "place", "cora").toDataGroup();

		DataGroup dataRecordLink = DataGroup.withNameInData("link");
		dataGroup.addChild(dataRecordLink);
		addLinkedRecordTypeAndLinkedRecordIdToRecordLink("place", "place:0001", dataRecordLink);

		DataGroup collectedLinksList = createLinkList();
		recordsInMemory.create("place", "place:0002", dataGroup, null, collectedLinksList, "cora");
	}

	private static void addThirdPlace(RecordStorage recordsInMemory) {
		DataGroup dataGroup = DataCreator.createRecordWithNameInDataAndIdAndTypeAndLinkedRecordId(
				"authority", "place:0003", "place", "cora").toDataGroup();

		DataGroup collectedLinksList = DataGroup.withNameInData("collectedLinksList");
		recordsInMemory.create("place", "place:0003", dataGroup, null, collectedLinksList, "cora");
	}

	private static void addFourthPlace(RecordStorage recordsInMemory) {
		DataGroup dataGroup = DataCreator.createRecordWithNameInDataAndIdAndTypeAndLinkedRecordId(
				"authority", "place:0004", "place", "cora").toDataGroup();

		DataGroup dataRecordLink = DataGroup.withNameInData("link");
		dataGroup.addChild(dataRecordLink);
		addLinkedRecordTypeAndLinkedRecordIdToRecordLink("authority", "place:0003", dataRecordLink);

		DataGroup collectedLinksList = DataGroup.withNameInData("collectedLinksList");
		DataGroup recordToRecordLink = DataGroup.withNameInData("recordToRecordLink");

		DataGroup from = DataGroup.withNameInData("from");
		recordToRecordLink.addChild(from);
		addLinkedRecordTypeAndLinkedRecordIdToRecordLink("place", "place:0004", from);
		DataGroup to = DataGroup.withNameInData("to");
		recordToRecordLink.addChild(to);
		addLinkedRecordTypeAndLinkedRecordIdToRecordLink("authority", "place:0003", to);

		collectedLinksList.addChild(recordToRecordLink);

		recordsInMemory.create("place", "place:0004", dataGroup, null, collectedLinksList, "cora");
	}

	private static void addLinkedRecordTypeAndLinkedRecordIdToRecordLink(
			String linkedRecordTypeString, String linkedRecordIdString, DataGroup dataRecordLink) {
		DataAtomic linkedRecordType = DataAtomic.withNameInDataAndValue("linkedRecordType",
				linkedRecordTypeString);
		dataRecordLink.addChild(linkedRecordType);

		DataAtomic linkedRecordId = DataAtomic.withNameInDataAndValue("linkedRecordId",
				linkedRecordIdString);
		dataRecordLink.addChild(linkedRecordId);
	}

	private static DataGroup createLinkList() {
		DataGroup collectedLinksList = DataGroup.withNameInData("collectedLinksList");
		DataGroup recordToRecordLink = DataGroup.withNameInData("recordToRecordLink");

		DataGroup from = DataGroup.withNameInData("from");
		recordToRecordLink.addChild(from);
		addLinkedRecordTypeAndLinkedRecordIdToRecordLink("place", "place:0002", from);
		DataGroup to = DataGroup.withNameInData("to");
		recordToRecordLink.addChild(to);
		addLinkedRecordTypeAndLinkedRecordIdToRecordLink("place", "place:0001", to);

		collectedLinksList.addChild(recordToRecordLink);
		return collectedLinksList;
	}

	private static void addMetadata(RecordStorageInMemoryStub recordsInMemory) {
		String metadata = "metadataGroup";
		DataGroup dataGroup = DataGroup.withNameInData("metadata");

		DataGroup recordInfo = DataCreator.createRecordInfoWithRecordTypeAndRecordId(metadata,
				"place");
		dataGroup.addChild(recordInfo);
		recordsInMemory.create(metadata, "place", dataGroup, null,
				DataGroup.withNameInData("collectedLinksList"), dataDivider);
	}

	private static void addMetadataForBinary(RecordStorageInMemoryStub recordsInMemory) {
		String metadata = "metadataGroup";
		DataGroup dataGroup = DataGroup.withNameInData("metadata");

		DataGroup recordInfo = DataCreator.createRecordInfoWithRecordTypeAndRecordId(metadata,
				"binary");
		dataGroup.addChild(recordInfo);
		recordsInMemory.create(metadata, "binary", dataGroup, null,
				DataGroup.withNameInData("collectedLinksList"), dataDivider);
	}

	private static void addPresentation(RecordStorageInMemoryStub recordsInMemory) {
		String presentation = "presentation";
		DataGroup dataGroup = DataGroup.withNameInData(presentation);

		DataGroup recordInfo = DataCreator.createRecordInfoWithRecordTypeAndRecordId(presentation,
				"placeView");
		dataGroup.addChild(recordInfo);

		recordsInMemory.create(presentation, "placeView", dataGroup, null,
				DataGroup.withNameInData("collectedLinksList"), dataDivider);
	}

	private static void addText(RecordStorageInMemoryStub recordsInMemory) {
		String text = "text";
		DataGroup dataGroup = DataGroup.withNameInData("text");

		DataGroup recordInfo = DataCreator.createRecordInfoWithRecordTypeAndRecordId(text,
				"placeText");
		dataGroup.addChild(recordInfo);
		recordsInMemory.create(text, "placeText", dataGroup, null,
				DataGroup.withNameInData("collectedLinksList"), dataDivider);
	}

	private static void addRecordType(RecordStorageInMemoryStub recordsInMemory) {
		String recordType = "recordType";
		DataGroup dataGroup = DataGroup.withNameInData(recordType);

		DataGroup recordInfo = DataCreator.createRecordInfoWithRecordTypeAndRecordId(recordType,
				"metadata");
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withNameInDataAndValue("abstract", "false"));
		recordsInMemory.create(recordType, "metadata", dataGroup, null,
				DataGroup.withNameInData("collectedLinksList"), dataDivider);
	}

	private static void addRecordTypeRecordType(RecordStorageInMemoryStub recordsInMemory) {
		String recordType = "recordType";
		DataGroup dataGroup = DataCreator
				.createRecordTypeWithIdAndUserSuppliedIdAndAbstractAndPublicRead("recordType",
						"true", "false", "false");
		recordsInMemory.create(recordType, "recordType", dataGroup, null,
				DataGroup.withNameInData("collectedLinksList"), dataDivider);
	}

	private static void addRecordTypeRecordTypeAutoGeneratedId(
			RecordStorageInMemoryStub recordsInMemory) {
		String recordType = "recordType";
		DataGroup dataGroup = DataCreator
				.createRecordTypeWithIdAndUserSuppliedIdAndAbstractAndPublicRead(
						"recordTypeAutoGeneratedId", "false", "false", "false");
		recordsInMemory.create(recordType, "recordTypeAutoGeneratedId", dataGroup, null,
				DataGroup.withNameInData("collectedLinksList"), dataDivider);

	}

	private static void addRecordTypeBinary(RecordStorageInMemoryStub recordsInMemory) {
		String recordType = "recordType";
		DataGroup dataGroup = DataCreator
				.createRecordTypeWithIdAndUserSuppliedIdAndAbstractAndPublicRead("binary", "true",
						"true", "false");
		recordsInMemory.create(recordType, "binary", dataGroup, null,
				DataGroup.withNameInData("collectedLinksList"), dataDivider);
	}

	private static void addRecordTypeUser(RecordStorageInMemoryStub recordsInMemory) {
		String recordType = "recordType";
		DataGroup dataGroup = DataCreator
				.createRecordTypeWithIdAndUserSuppliedIdAndAbstractAndPublicRead("user", "true",
						"false", "false");
		recordsInMemory.create(recordType, "user", dataGroup, null,
				DataGroup.withNameInData("collectedLinksList"), dataDivider);
	}

	private static void addRecordTypeImage(RecordStorageInMemoryStub recordsInMemory) {
		String recordType = "recordType";
		DataGroup dataGroup = DataCreator
				.createRecordTypeWithIdAndUserSuppliedIdAndParentId("image", "true", "binary");
		recordsInMemory.create(recordType, "image", dataGroup, null,
				DataGroup.withNameInData("collectedLinksList"), dataDivider);
	}

	private static void addImageOne(RecordStorageInMemoryStub recordsInMemory) {
		DataGroup dataGroup = DataCreator.createRecordWithNameInDataAndIdAndTypeAndLinkedRecordId(
				"image", "image:123456789", "image", "cora").toDataGroup();
		DataGroup resourceInfo = DataGroup.withNameInData("resourceInfo");
		dataGroup.addChild(resourceInfo);
		DataGroup master = DataGroup.withNameInData("master");
		resourceInfo.addChild(master);
		DataAtomic streamId = DataAtomic.withNameInDataAndValue("streamId", "678912345");
		master.addChild(streamId);
		DataAtomic uploadedFileName = DataAtomic.withNameInDataAndValue("filename", "adele.png");
		master.addChild(uploadedFileName);
		DataAtomic size = DataAtomic.withNameInDataAndValue("filesize", "123");
		master.addChild(size);
		DataAtomic mimeType = DataAtomic.withNameInDataAndValue("mimeType",
				"application/octet-stream");
		master.addChild(mimeType);
		recordsInMemory.create("image", "image:123456789", dataGroup, null,
				DataGroup.withNameInData("collectedLinksList"), dataDivider);
	}

	private static void addRecordTypePlace(RecordStorageInMemoryStub recordsInMemory) {
		String recordType = "recordType";
		DataGroup dataGroup = DataCreator
				.createRecordTypeWithIdAndUserSuppliedIdAndParentId("place", "false", "authority");

		DataGroup filter = DataGroup.withNameInData("filter");
		filter.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "metadataGroup"));
		filter.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "placeFilterGroup"));
		dataGroup.addChild(filter);

		recordsInMemory.create(recordType, "place", dataGroup, null,
				DataGroup.withNameInData("collectedLinksList"), dataDivider);
	}

	private static void addRecordTypeSearch(RecordStorageInMemoryStub recordsInMemory) {
		String recordType = "recordType";
		DataGroup dataGroup = DataCreator
				.createRecordTypeWithIdAndUserSuppliedIdAndAbstractAndPublicRead("search", "true",
						"false", "false");

		recordsInMemory.create(recordType, "search", dataGroup, null,
				DataGroup.withNameInData("collectedLinksList"), dataDivider);
	}

	private static void addSearch(RecordStorageInMemoryStub recordsInMemory) {
		DataGroup recordInfo = DataCreator.createRecordInfoWithRecordTypeAndRecordId("search",
				"aSearchId");
		DataGroup dataGroup = DataGroup.withNameInData("search");
		dataGroup.addChild(recordInfo);

		DataGroup metadataId = DataGroup.withNameInData("metadataId");
		metadataId.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "metadataGroup"));
		metadataId.addChild(
				DataAtomic.withNameInDataAndValue("linkedRecordId", "searchResourcesGroup"));
		dataGroup.addChild(metadataId);

		DataGroup recordTypeToSearchInGroup = DataGroup.withNameInData("recordTypeToSearchIn");
		dataGroup.addChild(recordTypeToSearchInGroup);
		recordTypeToSearchInGroup
				.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "recordType"));
		recordTypeToSearchInGroup
				.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "place"));
		recordsInMemory.create("search", "aSearchId", dataGroup, null,
				DataGroup.withNameInData("collectedLinksList"), dataDivider);
	}

	private static void addSearchWithTwoRecordTypeToSearchIn(
			RecordStorageInMemoryStub recordsInMemory) {
		DataGroup recordInfo = DataCreator.createRecordInfoWithRecordTypeAndRecordId("search",
				"anotherSearchId");

		DataGroup dataGroup = DataGroup.withNameInData("search");
		dataGroup.addChild(recordInfo);
		DataGroup metadataId = DataGroup.withNameInData("metadataId");
		metadataId.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "metadataGroup"));
		metadataId.addChild(
				DataAtomic.withNameInDataAndValue("linkedRecordId", "searchResourcesGroup"));
		dataGroup.addChild(metadataId);

		DataGroup recordTypeToSearchInGroup = DataGroup.withNameInData("recordTypeToSearchIn");
		dataGroup.addChild(recordTypeToSearchInGroup);
		recordTypeToSearchInGroup
				.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "recordType"));
		recordTypeToSearchInGroup
				.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "place"));

		DataGroup recordTypeToSearchInGroup2 = DataGroup.withNameInData("recordTypeToSearchIn");
		dataGroup.addChild(recordTypeToSearchInGroup2);
		recordTypeToSearchInGroup2
				.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "recordType"));
		recordTypeToSearchInGroup2
				.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "image"));

		recordsInMemory.create("search", "anotherSearchId", dataGroup, null,
				DataGroup.withNameInData("collectedLinksList"), dataDivider);
	}

	private static void addRecordTypeAbstractAuthority(RecordStorageInMemoryStub recordsInMemory) {
		String recordType = "recordType";

		DataGroup dataGroup = DataCreator
				.createRecordTypeWithIdAndUserSuppliedIdAndAbstractAndPublicRead(
						"abstractAuthority", "false", "true", "false");

		recordsInMemory.create(recordType, "abstractAuthority", dataGroup, null,
				DataGroup.withNameInData("collectedLinksList"), dataDivider);
	}

	private static void addImage(RecordStorageInMemoryStub recordsInMemory) {
		DataGroup recordInfo = DataCreator.createRecordInfoWithRecordTypeAndRecordId("image",
				"image:0001");
		DataGroup dataGroup = DataGroup.withNameInData("binary");
		dataGroup.addChild(recordInfo);
		recordsInMemory.create("image", "image:0001", dataGroup, null,
				DataGroup.withNameInData("collectedLinksList"), dataDivider);
	}

	private static void addRecordTypeSystem(RecordStorageInMemoryStub recordsInMemory) {
		String recordType = "recordType";
		DataGroup dataGroup = DataCreator
				.createRecordTypeWithIdAndUserSuppliedIdAndAbstractAndPublicRead("system", "true",
						"false", "false");

		recordsInMemory.create(recordType, "system", dataGroup, null,
				DataGroup.withNameInData("collectedLinksList"), dataDivider);
	}

	private static void addSystem(RecordStorageInMemoryStub recordsInMemory) {
		DataGroup dataGroup = DataGroup.withNameInData("system");
		DataGroup recordInfo = DataCreator.createRecordInfoWithRecordTypeAndRecordId("system",
				"cora");
		dataGroup.addChild(recordInfo);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("systemName", "cora"));

		recordsInMemory.create("system", "cora", dataGroup, null,
				DataGroup.withNameInData("collectedLinksList"), dataDivider);
	}
}
