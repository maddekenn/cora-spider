/*
 * Copyright 2017 Uppsala University Library
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
package se.uu.ub.cora.spider.spy;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.bookkeeper.termcollector.DataGroupTermCollector;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.spider.data.DataGroupSpy;

public class DataGroupTermCollectorSpy implements DataGroupTermCollector {
	public boolean collectTermsWasCalled = false;
	public String metadataId = null;
	public DataGroup dataGroup;

	public DataGroup collectedTerms = new DataGroupSpy("collectedData");

	public List<DataGroup> dataGroups = new ArrayList<>();

	@Override
	public DataGroup collectTerms(String metadataId, DataGroup dataGroup) {
		this.metadataId = metadataId;
		this.dataGroup = dataGroup;
		collectTermsWasCalled = true;

		dataGroups.add(dataGroup);

		return collectedTerms;
	}
}
