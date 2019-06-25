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
import se.uu.ub.cora.spider.spy.DataRecordLinkCollectorSpy;

public final class DataCreator {
	private static final String SELF_PRESENTATION_VIEW_ID = "selfPresentationViewId";
	private static final String USER_SUPPLIED_ID = "userSuppliedId";
	private static final String SEARCH_PRESENTATION_FORM_ID = "searchPresentationFormId";
	private static final String SEARCH_METADATA_ID = "searchMetadataId";
	private static final String LIST_PRESENTATION_VIEW_ID = "listPresentationViewId";
	private static final String NEW_PRESENTATION_FORM_ID = "newPresentationFormId";
	private static final String PRESENTATION_FORM_ID = "presentationFormId";
	private static final String PRESENTATION_VIEW_ID = "presentationViewId";
	private static final String METADATA_ID = "metadataId";
	private static final String NEW_METADATA_ID = "newMetadataId";
	private static final String RECORD_TYPE = "recordType";

	public static DataGroup createRecordTypeWithIdAndUserSuppliedIdAndAbstractAndPublicRead(
			String id, String userSuppliedId, String abstractValue, String publicRead) {
		DataGroup dataGroup = createRecordTypeWithIdAndUserSuppliedIdAndAbstractAndParentIdAndPublicRead(
				id, userSuppliedId, abstractValue, null, publicRead);
		dataGroup.addChild(getFilterChild("someFilterId"));
		return dataGroup;
	}

	private static DataGroup getFilterChild(String filterMetadataId) {
		DataGroup filter = DataGroup.withNameInData("filter");
		filter.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "metadataGroup"));
		filter.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", filterMetadataId));
		return filter;
	}

	public static DataGroup createRecordTypeWithIdAndUserSuppliedId(String id,
			String userSuppliedId) {
		// TODO Auto-generated method stub
		String idWithCapitalFirst = id.substring(0, 1).toUpperCase() + id.substring(1);

		DataGroup dataGroup = DataGroup.withNameInData(RECORD_TYPE);
		dataGroup.addChild(createRecordInfoWithRecordTypeAndRecordId(RECORD_TYPE, id));

		dataGroup.addChild(
				createChildWithNamInDataLinkedTypeLinkedId(METADATA_ID, "metadataGroup", id));

		dataGroup.addChild(createChildWithNamInDataLinkedTypeLinkedId(PRESENTATION_VIEW_ID,
				"presentationGroup", "pg" + idWithCapitalFirst + "View"));

		dataGroup.addChild(createChildWithNamInDataLinkedTypeLinkedId(PRESENTATION_FORM_ID,
				"presentationGroup", "pg" + idWithCapitalFirst + "Form"));
		dataGroup.addChild(createChildWithNamInDataLinkedTypeLinkedId(NEW_METADATA_ID,
				"metadataGroup", id + "New"));

		dataGroup.addChild(createChildWithNamInDataLinkedTypeLinkedId(NEW_PRESENTATION_FORM_ID,
				"presentationGroup", "pg" + idWithCapitalFirst + "FormNew"));
		dataGroup.addChild(createChildWithNamInDataLinkedTypeLinkedId(LIST_PRESENTATION_VIEW_ID,
				"presentationGroup", "pg" + idWithCapitalFirst + "List"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(SEARCH_METADATA_ID, id + "Search"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(SEARCH_PRESENTATION_FORM_ID,
				"pg" + idWithCapitalFirst + "SearchForm"));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(USER_SUPPLIED_ID, userSuppliedId));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(SELF_PRESENTATION_VIEW_ID,
				"pg" + idWithCapitalFirst + "Self"));
		return dataGroup;
	}

	private static DataGroup createRecordTypeWithIdAndUserSuppliedIdAndAbstractAndParentIdAndPublicRead(
			String id, String userSuppliedId, String abstractValue, String parentId,
			String publicRead) {
		DataGroup dataGroup = createRecordTypeWithIdAndUserSuppliedId(id, userSuppliedId);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("abstract", abstractValue));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("public", publicRead));
		if (null != parentId) {
			dataGroup.addChild(
					createChildWithNamInDataLinkedTypeLinkedId("parentId", "recordType", parentId));
		}
		return dataGroup;
	}

	public static DataGroup createChildWithNamInDataLinkedTypeLinkedId(String nameInData,
			String linkedRecordType, String id) {
		DataGroup metadataId = DataGroup.withNameInData(nameInData);
		metadataId
				.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", linkedRecordType));
		metadataId.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", id));
		return metadataId;
	}

	public static DataGroup createRecordTypeWithIdAndUserSuppliedIdAndParentId(String id,
			String userSuppliedId, String parentId) {
		return createRecordTypeWithIdAndUserSuppliedIdAndAbstractAndParentIdAndPublicRead(id,
				userSuppliedId, "false", parentId, "false");
	}

	public static DataGroup createRecordInfoWithRecordTypeAndRecordId(String recordType,
			String recordId) {
		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		DataGroup typeGroup = DataGroup.withNameInData("type");
		typeGroup.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "recordType"));
		typeGroup.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", recordType));
		recordInfo.addChild(typeGroup);
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", recordId));
		return recordInfo;
	}

	public static DataGroup createRecordWithNameInDataAndIdAndLinkedRecordId(String nameInData,
			String id, String linkedRecordId) {
		DataGroup record = DataGroup.withNameInData(nameInData);
		DataGroup createRecordInfo = createRecordInfoWithIdAndLinkedRecordId(id, linkedRecordId);
		record.addChild(createRecordInfo);
		return record;
	}

	public static DataGroup createRecordWithNameInDataAndIdAndTypeAndLinkedRecordIdAndCreatedBy(
			String nameInData, String id, String recordType, String linkedRecordId,
			String createdBy) {
		DataGroup record = DataGroup.withNameInData(nameInData);
		DataGroup createRecordInfo = createRecordInfoWithIdAndTypeAndLinkedRecordId(id, recordType,
				linkedRecordId);
		record.addChild(createRecordInfo);
		DataGroup createdByGroup = DataGroup.withNameInData("createdBy");
		createdByGroup.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "user"));
		createdByGroup.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", createdBy));
		createRecordInfo.addChild(createdByGroup);
		return record;
	}

	public static DataGroup createRecordWithNameInDataAndIdAndTypeAndLinkedRecordId(
			String nameInData, String id, String recordType, String linkedRecordId) {
		DataGroup record = DataGroup.withNameInData(nameInData);
		DataGroup createRecordInfo = createRecordInfoWithIdAndTypeAndLinkedRecordId(id, recordType,
				linkedRecordId);
		record.addChild(createRecordInfo);
		return record;
	}

	public static DataGroup createRecordInfoWithLinkedRecordId(String linkedRecordId) {
		DataGroup createRecordInfo = DataGroup.withNameInData("recordInfo");
		DataGroup dataDivider = createDataDividerWithLinkedRecordId(linkedRecordId);
		createRecordInfo.addChild(dataDivider);
		return createRecordInfo;
	}

	public static DataGroup createDataDividerWithLinkedRecordId(String linkedRecordId) {
		DataGroup dataDivider = DataGroup.withNameInData("dataDivider");
		dataDivider.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "system"));
		dataDivider.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", linkedRecordId));
		return dataDivider;
	}

	public static DataGroup createRecordWithNameInDataAndLinkedRecordId(String nameInData,
			String linkedRecordId) {
		DataGroup record = DataGroup.withNameInData(nameInData);
		DataGroup createRecordInfo = createRecordInfoWithLinkedRecordId(linkedRecordId);
		record.addChild(createRecordInfo);
		return record;
	}

	public static DataGroup createRecordInfoWithIdAndLinkedRecordId(String id,
			String linkedRecordId) {
		DataGroup createRecordInfo = DataGroup.withNameInData("recordInfo");
		createRecordInfo.addChild(DataAtomic.withNameInDataAndValue("id", id));
		DataGroup dataDivider = createDataDividerWithLinkedRecordId(linkedRecordId);
		createRecordInfo.addChild(dataDivider);
		return createRecordInfo;
	}

	public static DataGroup createMetadataGroupWithOneChild() {
		DataGroup dataGroup = DataGroup.withNameInData("metadata");
		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "testNewGroup"));
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("type", "metadataGroup"));
		recordInfo.addChild(createDataDividerWithLinkedRecordId("test"));

		dataGroup.addChild(recordInfo);

		dataGroup.addChild(createChildReference());

		return dataGroup;
	}

	public static DataGroup createSomeDataGroupWithOneChild() {
		DataGroup dataGroup = DataGroup.withNameInData("someData");
		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "testNewGroup"));
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("type", "someData"));
		recordInfo.addChild(createDataDividerWithLinkedRecordId("test"));

		dataGroup.addChild(recordInfo);

		dataGroup.addChild(createChildReference());

		return dataGroup;
	}

	private static DataGroup createChildReference() {
		DataGroup childReferences = DataGroup.withNameInData("childReferences");

		childReferences.addChild(createChildReference("childOne", "1", "1"));

		return childReferences;
	}

	public static DataGroup createRecordInfoWithIdAndTypeAndLinkedRecordId(String id,
			String recordType, String linkedRecordId) {
		DataGroup createRecordInfo = DataGroup.withNameInData("recordInfo");
		createRecordInfo.addChild(DataAtomic.withNameInDataAndValue("id", id));
		DataGroup typeGroup = DataGroup.withNameInData("type");
		typeGroup.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "recordType"));
		typeGroup.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", recordType));
		createRecordInfo.addChild(typeGroup);
		// createRecordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("type",
		// recordType));

		DataGroup dataDivider = createDataDividerWithLinkedRecordId(linkedRecordId);
		createRecordInfo.addChild(dataDivider);
		return createRecordInfo;
	}

	public static DataGroup createMetadataGroupWithTwoChildren() {
		DataGroup dataGroup = DataGroup.withNameInData("metadata");
		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "testNewGroup"));
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("type", "metadataGroup"));
		recordInfo.addChild(createDataDividerWithLinkedRecordId("test"));

		dataGroup.addChild(recordInfo);

		dataGroup.addChild(createChildReferences());

		return dataGroup;
	}

	private static DataGroup createChildReferences() {
		DataGroup childReferences = DataGroup.withNameInData("childReferences");

		childReferences.addChild(createChildReference("childOne", "1", "1"));
		childReferences.addChild(createChildReference("childTwo", "0", "2"));

		return childReferences;
	}

	private static DataGroup createChildReference(String ref, String repeatMin, String repeatMax) {
		DataGroup childReference = DataGroup.withNameInData("childReference");

		DataGroup refGroup = DataGroup.withNameInData("ref");
		DataAtomic linkedRecordType = DataAtomic.withNameInDataAndValue("linkedRecordType",
				"metadataGroup");
		refGroup.addChild(linkedRecordType);
		DataAtomic linkedRecordId = DataAtomic.withNameInDataAndValue("linkedRecordId", ref);
		refGroup.addChild(linkedRecordId);

		refGroup.addAttributeByIdWithValue("type", "group");
		childReference.addChild(refGroup);

		DataAtomic repeatMinAtomic = DataAtomic.withNameInDataAndValue("repeatMin", repeatMin);
		childReference.addChild(repeatMinAtomic);

		DataAtomic repeatMaxAtomic = DataAtomic.withNameInDataAndValue("repeatMax", repeatMax);
		childReference.addChild(repeatMaxAtomic);

		return childReference;
	}

	public static DataGroup createMetadataGroupWithThreeChildren() {
		DataGroup dataGroup = createMetadataGroupWithTwoChildren();
		DataGroup childReferences = (DataGroup) dataGroup
				.getFirstChildWithNameInData("childReferences");
		childReferences.addChild(createChildReference("childThree", "1", "1"));

		return dataGroup;
	}

	public static DataRecordLinkCollectorSpy getDataRecordLinkCollectorSpyWithCollectedLinkAdded() {
		DataGroup recordToRecordLink = createDataForRecordToRecordLink();

		DataRecordLinkCollectorSpy linkCollector = new DataRecordLinkCollectorSpy();
		linkCollector.collectedDataLinks.addChild(recordToRecordLink);
		return linkCollector;
	}

	public static DataGroup createDataForRecordToRecordLink() {
		DataGroup recordToRecordLink = DataGroup.withNameInData("recordToRecordLink");

		DataGroup from = DataGroup.withNameInData("from");
		from.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "dataWithLinks"));
		from.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "someId"));

		recordToRecordLink.addChild(from);

		DataGroup to = DataGroup.withNameInData("to");
		to.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "toRecordType"));
		to.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "toRecordId"));
		to.addChild(to);

		recordToRecordLink.addChild(to);
		return recordToRecordLink;
	}

	public static DataGroup createMetadataGroupWithCollectionVariableAsChild() {
		DataGroup dataGroup = DataGroup.withNameInData("metadata");
		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "testCollectionVar"));
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("type", "collectionVariable"));
		recordInfo.addChild(createDataDividerWithLinkedRecordId("test"));
		dataGroup.addChild(recordInfo);
		dataGroup.addChild(createRefCollectionIdWithLinkedRecordid("testItemCollection"));

		return dataGroup;
	}

	public static DataGroup createRefCollectionIdWithLinkedRecordid(String linkedRecordId) {
		DataGroup refCollection = DataGroup.withNameInData("refCollection");
		refCollection.addChild(
				DataAtomic.withNameInDataAndValue("linkedRecordType", "metadataItemCollection"));
		refCollection.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", linkedRecordId));
		return refCollection;
	}

	public static DataGroup createMetadataGroupWithRecordLinkAsChild() {
		DataGroup dataGroup = DataGroup.withNameInData("metadata");
		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "testRecordLink"));
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("type", "recordLink"));
		recordInfo.addChild(createDataDividerWithLinkedRecordId("test"));
		dataGroup.addChild(recordInfo);

		return dataGroup;
	}

	public static DataGroup createDataGroupWithNameInDataTypeAndId(String nameInData,
			String recordType, String recordId) {
		DataGroup dataGroup = DataGroup.withNameInData(nameInData);
		DataGroup recordInfo = DataCreator.createRecordInfoWithRecordTypeAndRecordId(recordType,
				recordId);
		dataGroup.addChild(recordInfo);
		return dataGroup;
	}

	public static DataGroup createWorkOrderWithIdAndRecordTypeAndRecordIdToIndex(String id,
			String recordType, String recordId) {
		DataGroup workOrder = DataGroup.withNameInData("workOrder");
		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", id));
		workOrder.addChild(recordInfo);

		DataGroup recordTypeLink = DataGroup.withNameInData("recordType");
		recordTypeLink
				.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "recordType"));
		recordTypeLink.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", recordType));
		workOrder.addChild(recordTypeLink);

		workOrder.addChild(DataAtomic.withNameInDataAndValue("recordId", recordId));
		workOrder.addChild(DataAtomic.withNameInDataAndValue("type", "index"));
		return workOrder;
	}

}
