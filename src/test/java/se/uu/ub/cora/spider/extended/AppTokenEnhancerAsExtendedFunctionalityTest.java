package se.uu.ub.cora.spider.extended;

import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataGroup;

public class AppTokenEnhancerAsExtendedFunctionalityTest {

	private AppTokenEnhancerAsExtendedFunctionality extendedFunctionality;

	@BeforeMethod
	public void setUp() {
		extendedFunctionality = new AppTokenEnhancerAsExtendedFunctionality();
	}

	@Test
	public void useExtendedFunctionality() {
		assertNotNull(extendedFunctionality);
	}

	@Test
	public void generateAndAddAppToken() {
		DataGroup minimalGroup = DataGroup.withNameInData("appToken");
		extendedFunctionality.useExtendedFunctionality("someToken", minimalGroup);
		DataAtomic token = (DataAtomic) minimalGroup.getFirstChildWithNameInData("token");
		assertTrue(token.getValue().length() > 30);
	}

	@Test
	public void generateAndAddAppTokenDifferentTokens() {
		DataGroup minimalGroup = DataGroup.withNameInData("appToken");
		extendedFunctionality.useExtendedFunctionality("someToken", minimalGroup);
		DataAtomic token = (DataAtomic) minimalGroup.getFirstChildWithNameInData("token");

		DataGroup minimalGroup2 = DataGroup.withNameInData("appToken");
		extendedFunctionality.useExtendedFunctionality("someToken", minimalGroup2);
		DataAtomic token2 = (DataAtomic) minimalGroup2.getFirstChildWithNameInData("token");

		assertNotEquals(token.getValue(), token2.getValue());
	}
}
