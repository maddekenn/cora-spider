package se.uu.ub.cora.spider.record;

import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataResourceLink;

public class DataGroupEnhancer {

	public DataGroup enhance(DataGroup dataGroup) {
		return possiblyConvertAndSetChildren(dataGroup);
	}

	private DataGroup possiblyConvertAndSetChildren(DataGroup dataGroup) {
		DataGroup enhancedGroup = DataGroup.withNameInData(dataGroup.getNameInData());
		for (DataElement dataChild : dataGroup.getChildren()) {
			convertAndSetChild(enhancedGroup, dataChild);
		}
		return enhancedGroup;
	}

	private void convertAndSetChild(DataGroup enhancedGroup, DataElement dataChild) {
		if (isGroup(dataChild)) {
			enhancedGroup.addChild(handleGroup(dataChild));
		} else {
			enhancedGroup.addChild(dataChild);
		}
	}

	private boolean isGroup(DataElement dataChild) {
		return dataChild instanceof DataGroup;
	}

	private DataElement handleGroup(DataElement dataChild) {
		if (isRecordLink(dataChild)) {
			return DataRecordLink.fromDataGroup((DataGroup) dataChild);
		} else if (isResourceLink(dataChild)) {
			return DataResourceLink.fromDataGroup((DataGroup) dataChild);
		}
		return possiblyConvertAndSetChildren((DataGroup) dataChild);
	}

	private boolean isRecordLink(DataElement dataElement) {
		DataGroup dataGroup = (DataGroup) dataElement;
		return dataGroup.containsChildWithNameInData("linkedRecordType")
				&& dataGroup.containsChildWithNameInData("linkedRecordId");
	}

	private boolean isResourceLink(DataElement dataChild) {
		if (dataChild instanceof DataGroup) {
			DataGroup dataChildGroup = (DataGroup) dataChild;
			return dataChildGroup.containsChildWithNameInData("streamId")
					&& dataChildGroup.containsChildWithNameInData("filename")
					&& dataChildGroup.containsChildWithNameInData("filesize")
					&& dataChildGroup.containsChildWithNameInData("mimeType");
		}
		return false;
	}

}
