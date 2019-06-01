package se.uu.ub.cora.spider.record;

import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;

public class DataGroupEnhancer {

	public DataGroup enhance(DataGroup dataGroup) {
		DataGroup enhancedGroup = DataGroup.withNameInData(dataGroup.getNameInData());

		for (DataElement dataChild : dataGroup.getChildren()) {
			if (dataChild instanceof DataGroup) {
				if (isRecordLink((DataGroup) dataChild)) {
					// TODO: gör en dataRecordLink.fromGroup
				}

			}
			enhancedGroup.addChild(dataChild);
		}
		return enhancedGroup;
	}

	private boolean isRecordLink(DataGroup dataGroup) {
		return dataGroup.containsChildWithNameInData("linkedRecordType")
				&& dataGroup.containsChildWithNameInData("linkedRecordId");
	}

}
