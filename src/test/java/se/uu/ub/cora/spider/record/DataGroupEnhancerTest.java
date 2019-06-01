package se.uu.ub.cora.spider.record;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordLink;

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
}
