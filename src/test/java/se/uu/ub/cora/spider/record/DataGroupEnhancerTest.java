/*
 * Copyright 2019 Uppsala University Library
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataResourceLink;

public class DataGroupEnhancerTest {

	@Test
	public void testEnhanceOneAtomicChild() {
		DataGroup dataGroup = createDataGroupWithOneAtomicChildUsingNameInData("someDataGroup");
		DataGroupEnhancer enhancer = new DataGroupEnhancer();

		DataGroup enhancedGroup = enhancer.enhance(dataGroup);
		assertEquals(enhancedGroup.getNameInData(), "someDataGroup");
		assertEquals(enhancedGroup.getChildren().size(), 1);

		assertCorrectFirstAtomicChild(enhancedGroup);
	}

	private DataGroup createDataGroupWithOneAtomicChildUsingNameInData(String nameInData) {
		DataGroup dataGroup = DataGroup.withNameInData(nameInData);
		DataAtomic dataAtomicChild = DataAtomic.withNameInDataAndValue("someChildNameInData",
				"someValue");
		dataGroup.addChild(dataAtomicChild);
		return dataGroup;
	}

	private void assertCorrectFirstAtomicChild(DataGroup enhancedGroup) {
		String atomicChildValue = enhancedGroup
				.getFirstAtomicValueWithNameInData("someChildNameInData");
		assertEquals(atomicChildValue, "someValue");
	}

	@Test
	public void testEnhanceTwoAtomicChildren() {
		DataGroup dataGroup = createDataGroupWithOneAtomicChildUsingNameInData(
				"someOtherDataGroup");
		dataGroup
				.addChild(DataAtomic.withNameInDataAndValue("secondAtomicChild", "someOtherValue"));
		DataGroupEnhancer enhancer = new DataGroupEnhancer();

		DataGroup enhancedGroup = enhancer.enhance(dataGroup);
		assertEquals(enhancedGroup.getNameInData(), "someOtherDataGroup");
		assertEquals(enhancedGroup.getChildren().size(), 2);
		assertCorrectFirstAtomicChild(enhancedGroup);
		String secondAomicChildValue = enhancedGroup
				.getFirstAtomicValueWithNameInData("secondAtomicChild");
		assertEquals(secondAomicChildValue, "someOtherValue");
	}

	@Test
	public void testEnhanceOneAtomicChildOneGroupChild() {
		DataGroup dataGroup = createDataGroupWithOneAtomicChildUsingNameInData(
				"someOtherDataGroup");
		createAndAddGroupChild(dataGroup);

		DataGroupEnhancer enhancer = new DataGroupEnhancer();

		DataGroup enhancedGroup = enhancer.enhance(dataGroup);
		assertEquals(enhancedGroup.getNameInData(), "someOtherDataGroup");
		assertEquals(enhancedGroup.getChildren().size(), 2);

		assertCorrectFirstAtomicChild(enhancedGroup);
		assertCorrectGroupChild(enhancedGroup);
	}

	private void createAndAddGroupChild(DataGroup dataGroup) {
		DataGroup groupChild = DataGroup.withNameInData("someGroupChildNameInData");
		groupChild
				.addChild(DataAtomic.withNameInDataAndValue("secondLevelChild", "someOtherValue"));
		dataGroup.addChild(groupChild);
	}

	private void assertCorrectGroupChild(DataGroup enhancedGroup) {
		DataGroup groupChildFromEnhanced = enhancedGroup
				.getFirstGroupWithNameInData("someGroupChildNameInData");
		String secondLevelChildValue = groupChildFromEnhanced
				.getFirstAtomicValueWithNameInData("secondLevelChild");
		assertEquals(secondLevelChildValue, "someOtherValue");
	}

	@Test
	public void testEnhanceOneAtomicChildOneGroupChildOneRecordLinkChild() {
		DataGroup dataGroup = createDataGroupWithOneAtomicChildUsingNameInData(
				"someOtherDataGroup");
		createAndAddGroupChild(dataGroup);

		DataGroup linkeChild = DataGroup.asLinkWithNameInDataAndTypeAndId("someLinkChild",
				"someRecordType", "someRecordId");
		dataGroup.addChild(linkeChild);

		DataGroupEnhancer enhancer = new DataGroupEnhancer();

		DataGroup enhancedGroup = enhancer.enhance(dataGroup);
		assertEquals(enhancedGroup.getNameInData(), "someOtherDataGroup");
		assertEquals(enhancedGroup.getChildren().size(), 3);

		assertCorrectFirstAtomicChild(enhancedGroup);
		assertCorrectGroupChild(enhancedGroup);

		DataElement linkChildFromEnhanced = enhancedGroup
				.getFirstChildWithNameInData("someLinkChild");
		assertTrue(linkChildFromEnhanced instanceof DataRecordLink);
	}

	@Test
	public void testEnhanceOneNonCompleteRecordLinkChildIsGroup() {
		DataGroup dataGroup = DataGroup.withNameInData("someOtherDataGroup");

		DataGroup linkChild = DataGroup.withNameInData("someLinkChild");
		linkChild.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "someRecordType"));
		dataGroup.addChild(linkChild);

		DataGroupEnhancer enhancer = new DataGroupEnhancer();

		DataGroup enhancedGroup = enhancer.enhance(dataGroup);
		assertEquals(enhancedGroup.getNameInData(), "someOtherDataGroup");
		assertEquals(enhancedGroup.getChildren().size(), 1);

		DataElement linkChildFromEnhanced = enhancedGroup
				.getFirstChildWithNameInData("someLinkChild");
		assertTrue(linkChildFromEnhanced instanceof DataGroup);
		assertEquals(((DataGroup) linkChildFromEnhanced)
				.getFirstAtomicValueWithNameInData("linkedRecordType"), "someRecordType");
	}

	@Test
	public void testEnhanceOneRecordLinkChildOneLevelDown() {
		DataGroup dataGroup = DataGroup.withNameInData("someOtherDataGroup");

		DataGroup groupChild = DataGroup.withNameInData("groupChild");
		DataGroup linkChild = DataGroup.withNameInData("someSecondLevelLinkChild");
		linkChild.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "someRecordType"));
		linkChild.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "someRecordId"));
		groupChild.addChild(linkChild);
		dataGroup.addChild(groupChild);

		DataGroupEnhancer enhancer = new DataGroupEnhancer();

		DataGroup enhancedGroup = enhancer.enhance(dataGroup);
		assertEquals(enhancedGroup.getNameInData(), "someOtherDataGroup");
		assertEquals(enhancedGroup.getChildren().size(), 1);

		DataGroup enhancedGroupChild = enhancedGroup.getFirstGroupWithNameInData("groupChild");

		DataElement secondLevelLink = enhancedGroupChild
				.getFirstChildWithNameInData("someSecondLevelLinkChild");
		assertTrue(secondLevelLink instanceof DataRecordLink);
	}

	@Test
	public void testEnhanceOneNonCompleteResourceLinkMissingMimeTypeChildIsGroup() {
		DataGroup dataGroup = DataGroup.withNameInData("someOtherDataGroup");

		DataGroup resourceLinkChild = createAndAddResourceLinkChildWithStreamId(dataGroup);
		resourceLinkChild.addChild(DataAtomic.withNameInDataAndValue("fileName", "someFileName"));
		resourceLinkChild.addChild(DataAtomic.withNameInDataAndValue("filesize", "678"));

		DataGroupEnhancer enhancer = new DataGroupEnhancer();

		DataGroup enhancedGroup = enhancer.enhance(dataGroup);
		assertEquals(enhancedGroup.getNameInData(), "someOtherDataGroup");
		assertEquals(enhancedGroup.getChildren().size(), 1);

		DataElement linkChildFromEnhanced = enhancedGroup
				.getFirstChildWithNameInData("someResourceLinkChild");
		assertTrue(linkChildFromEnhanced instanceof DataGroup);
		assertEquals(
				((DataGroup) linkChildFromEnhanced).getFirstAtomicValueWithNameInData("streamId"),
				"someStreamId");
		assertEquals(
				((DataGroup) linkChildFromEnhanced).getFirstAtomicValueWithNameInData("fileName"),
				"someFileName");
		assertEquals(
				((DataGroup) linkChildFromEnhanced).getFirstAtomicValueWithNameInData("filesize"),
				"678");
	}

	private DataGroup createAndAddResourceLinkChildWithStreamId(DataGroup dataGroup) {
		DataGroup resourceLinkChild = DataGroup.withNameInData("someResourceLinkChild");
		resourceLinkChild.addChild(DataAtomic.withNameInDataAndValue("streamId", "someStreamId"));
		dataGroup.addChild(resourceLinkChild);
		return resourceLinkChild;
	}

	@Test
	public void testEnhanceOneNonCompleteResourceLinkMissingFileSizeChildIsGroup() {
		DataGroup dataGroup = DataGroup.withNameInData("someOtherDataGroup");

		DataGroup resourceLinkChild = createAndAddResourceLinkChildWithStreamId(dataGroup);
		resourceLinkChild.addChild(DataAtomic.withNameInDataAndValue("fileName", "someFileName"));
		resourceLinkChild.addChild(DataAtomic.withNameInDataAndValue("mimeType", "someMimeType"));

		DataGroupEnhancer enhancer = new DataGroupEnhancer();

		DataGroup enhancedGroup = enhancer.enhance(dataGroup);
		assertEquals(enhancedGroup.getNameInData(), "someOtherDataGroup");
		assertEquals(enhancedGroup.getChildren().size(), 1);

		DataElement linkChildFromEnhanced = enhancedGroup
				.getFirstChildWithNameInData("someResourceLinkChild");
		assertTrue(linkChildFromEnhanced instanceof DataGroup);
		assertEquals(
				((DataGroup) linkChildFromEnhanced).getFirstAtomicValueWithNameInData("streamId"),
				"someStreamId");
		assertEquals(
				((DataGroup) linkChildFromEnhanced).getFirstAtomicValueWithNameInData("fileName"),
				"someFileName");
		assertEquals(
				((DataGroup) linkChildFromEnhanced).getFirstAtomicValueWithNameInData("mimeType"),
				"someMimeType");
	}

	@Test
	public void testEnhanceOneNonCompleteResourceLinkMissingFileNameChildIsGroup() {
		DataGroup dataGroup = DataGroup.withNameInData("someOtherDataGroup");

		DataGroup resourceLinkChild = createAndAddResourceLinkChildWithStreamId(dataGroup);
		resourceLinkChild.addChild(DataAtomic.withNameInDataAndValue("mimeType", "someMimeType"));
		resourceLinkChild.addChild(DataAtomic.withNameInDataAndValue("filesize", "678"));

		DataGroupEnhancer enhancer = new DataGroupEnhancer();

		DataGroup enhancedGroup = enhancer.enhance(dataGroup);
		assertEquals(enhancedGroup.getNameInData(), "someOtherDataGroup");
		assertEquals(enhancedGroup.getChildren().size(), 1);

		DataElement linkChildFromEnhanced = enhancedGroup
				.getFirstChildWithNameInData("someResourceLinkChild");
		assertTrue(linkChildFromEnhanced instanceof DataGroup);
		assertEquals(
				((DataGroup) linkChildFromEnhanced).getFirstAtomicValueWithNameInData("streamId"),
				"someStreamId");
		assertEquals(
				((DataGroup) linkChildFromEnhanced).getFirstAtomicValueWithNameInData("mimeType"),
				"someMimeType");
		assertEquals(
				((DataGroup) linkChildFromEnhanced).getFirstAtomicValueWithNameInData("filesize"),
				"678");
	}

	@Test
	public void testEnhanceOneCompleteResourceLinkChildIsResourceLink() {
		DataGroup dataGroup = DataGroup.withNameInData("someOtherDataGroup");

		DataGroup resourceLinkChild = createAndAddResourceLinkChildWithStreamId(dataGroup);
		resourceLinkChild.addChild(DataAtomic.withNameInDataAndValue("filename", "someFileName"));
		resourceLinkChild.addChild(DataAtomic.withNameInDataAndValue("filesize", "678"));
		resourceLinkChild.addChild(DataAtomic.withNameInDataAndValue("mimeType", "someMimeType"));

		DataGroupEnhancer enhancer = new DataGroupEnhancer();

		DataGroup enhancedGroup = enhancer.enhance(dataGroup);
		assertEquals(enhancedGroup.getNameInData(), "someOtherDataGroup");
		assertEquals(enhancedGroup.getChildren().size(), 1);

		DataElement linkChildFromEnhanced = enhancedGroup
				.getFirstChildWithNameInData("someResourceLinkChild");
		assertTrue(linkChildFromEnhanced instanceof DataResourceLink);
		// assertEquals(
		// ((DataGroup) linkChildFromEnhanced).getFirstAtomicValueWithNameInData("streamId"),
		// "someStreamId");
		// assertEquals(
		// ((DataGroup) linkChildFromEnhanced).getFirstAtomicValueWithNameInData("fileName"),
		// "someFileName");
		// assertEquals(
		// ((DataGroup) linkChildFromEnhanced).getFirstAtomicValueWithNameInData("filesize"),
		// "678");
	}
}
