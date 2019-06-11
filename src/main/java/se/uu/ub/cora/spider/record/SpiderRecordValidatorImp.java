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

import java.time.LocalDateTime;

import se.uu.ub.cora.beefeater.authentication.User;
import se.uu.ub.cora.bookkeeper.linkcollector.DataRecordLinkCollector;
import se.uu.ub.cora.bookkeeper.validator.DataValidator;
import se.uu.ub.cora.bookkeeper.validator.ValidationAnswer;
import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.spider.authentication.Authenticator;
import se.uu.ub.cora.spider.authorization.SpiderAuthorizator;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.record.storage.RecordNotFoundException;
import se.uu.ub.cora.storage.RecordIdGenerator;

public final class SpiderRecordValidatorImp extends SpiderRecordHandler
		implements SpiderRecordValidator {
	private static final String ERROR_MESSAGES = "errorMessages";
	private static final String VALIDATE = "validate";
	private Authenticator authenticator;
	private SpiderAuthorizator spiderAuthorizator;
	private DataValidator dataValidator;
	private DataRecordLinkCollector linkCollector;
	private String authToken;
	private User user;
	private String metadataToValidate;
	private DataGroup validationResult;
	private RecordIdGenerator idGenerator;

	private SpiderRecordValidatorImp(SpiderDependencyProvider dependencyProvider) {
		this.authenticator = dependencyProvider.getAuthenticator();
		this.spiderAuthorizator = dependencyProvider.getSpiderAuthorizator();
		this.dataValidator = dependencyProvider.getDataValidator();
		this.recordStorage = dependencyProvider.getRecordStorage();
		this.linkCollector = dependencyProvider.getDataRecordLinkCollector();
		this.idGenerator = dependencyProvider.getRecordIdGenerator();
	}

	public static SpiderRecordValidator usingDependencyProvider(
			SpiderDependencyProvider dependencyProvider) {
		return new SpiderRecordValidatorImp(dependencyProvider);
	}

	@Override
	public DataRecord validateRecord(String authToken, String recordType,
			DataGroup validationRecord, DataGroup recordToValidate) {
		this.authToken = authToken;
		this.recordAsSpiderDataGroup = recordToValidate;
		this.recordType = recordType;
		user = tryToGetActiveUser();
		checkValidationRecordIsOkBeforeValidation(validationRecord);
		return validateRecord(validationRecord);
	}

	private User tryToGetActiveUser() {
		return authenticator.getUserForToken(authToken);
	}

	private void checkValidationRecordIsOkBeforeValidation(DataGroup validationRecord) {
		checkUserIsAuthorizedForCreateOnRecordType();
		validateWorkOrderAsSpecifiedInMetadata(validationRecord);
	}

	private String getMetadataIdForWorkOrder(String recordType) {
		RecordTypeHandler recordTypeHandler = RecordTypeHandler
				.usingRecordStorageAndRecordTypeId(recordStorage, recordType);
		return recordTypeHandler.getNewMetadataId();
	}

	private void checkUserIsAuthorizedForCreateOnRecordType() {
		spiderAuthorizator.checkUserIsAuthorizedForActionOnRecordType(user, "create", recordType);
	}

	private void validateWorkOrderAsSpecifiedInMetadata(DataGroup dataGroup) {
		String metadataIdForWorkOrder = getMetadataIdForWorkOrder(recordType);
		ValidationAnswer validationAnswer = dataValidator.validateData(metadataIdForWorkOrder,
				dataGroup);
		if (validationAnswer.dataIsInvalid()) {
			throw new DataException("Data is not valid: " + validationAnswer.getErrorMessages());
		}
	}

	private DataRecord validateRecord(DataGroup validationRecord) {
		String recordTypeToValidate = getRecordTypeToValidate(validationRecord);

		checkUserIsAuthorizedForValidateOnRecordType(recordTypeToValidate);

		createValidationResultDataGroup();
		validateRecordUsingValidationRecord(validationRecord, recordTypeToValidate);
		DataRecord record = DataRecord.withDataGroup(validationResult);
		addReadActionToComplyWithRecordStructure(record);
		return record;
	}

	private String getRecordTypeToValidate(DataGroup validationRecord) {
		DataGroup recordTypeGroup = validationRecord.getFirstGroupWithNameInData("recordType");
		return recordTypeGroup.getFirstAtomicValueWithNameInData(LINKED_RECORD_ID);
	}

	private void checkUserIsAuthorizedForValidateOnRecordType(String recordTypeToValidate) {
		spiderAuthorizator.checkUserIsAuthorizedForActionOnRecordType(user, VALIDATE,
				recordTypeToValidate);
	}

	private void createValidationResultDataGroup() {
		validationResult = DataGroup.withNameInData("validationResult");
		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(
				DataAtomic.withNameInDataAndValue("id", idGenerator.getIdForType(recordType)));

		DataGroup recordTypeGroup = createTypeDataGroup(recordType);
		recordInfo.addChild(recordTypeGroup);

		addCreatedInfoToRecordInfoUsingUserId(recordInfo, user.id);
		addUpdatedInfoToRecordInfoUsingUserId(recordInfo, user.id);
		validationResult.addChild(recordInfo);
	}

	private DataGroup createTypeDataGroup(String recordType) {
		DataGroup type = DataGroup.withNameInData("type");
		type.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "recordType"));
		type.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", recordType));
		return type;
	}

	private void addCreatedInfoToRecordInfoUsingUserId(DataGroup recordInfo, String userId) {
		DataGroup createdByGroup = createLinkToUserUsingUserIdAndNameInData(userId, "createdBy");
		recordInfo.addChild(createdByGroup);
		String currentLocalDateTime = getLocalTimeDateAsString(LocalDateTime.now());
		recordInfo.addChild(DataAtomic.withNameInDataAndValue(TS_CREATED, currentLocalDateTime));
	}

	private void validateRecordUsingValidationRecord(DataGroup validationRecord,
			String recordTypeToValidate) {
		metadataToValidate = validationRecord
				.getFirstAtomicValueWithNameInData("metadataToValidate");

		String recordIdOrNullIfCreate = extractRecordIdIfUpdate();
		ensureRecordExistWhenActionToPerformIsUpdate(recordTypeToValidate, recordIdOrNullIfCreate);

		String metadataId = getMetadataId(recordTypeToValidate);
		possiblyEnsureLinksExist(validationRecord, recordTypeToValidate, recordIdOrNullIfCreate,
				metadataId);

		validateIncomingDataAsSpecifiedInMetadata(metadataId);
	}

	private String getMetadataId(String recordTypeToValidate) {
		RecordTypeHandler recordTypeHandler = RecordTypeHandler
				.usingRecordStorageAndRecordTypeId(recordStorage, recordTypeToValidate);
		return validateNew() ? recordTypeHandler.getNewMetadataId()
				: recordTypeHandler.getMetadataId();
	}

	private boolean validateNew() {
		return "new".equals(metadataToValidate);
	}

	private String extractRecordIdIfUpdate() {
		return "existing".equals(metadataToValidate) ? extractIdFromData() : null;
	}

	private String extractIdFromData() {
		return recordAsSpiderDataGroup.getFirstGroupWithNameInData("recordInfo")
				.getFirstAtomicValueWithNameInData("id");
	}

	private void ensureRecordExistWhenActionToPerformIsUpdate(String recordTypeToValidate,
			String recordIdToUse) {
		if ("existing".equals(metadataToValidate)) {
			checkIfRecordExist(recordTypeToValidate, recordIdToUse);
		}
	}

	private void checkIfRecordExist(String recordTypeToValidate, String recordIdToUse) {
		try {
			recordStorage.read(recordTypeToValidate, recordIdToUse);
		} catch (RecordNotFoundException exception) {
			addErrorToValidationResult(exception.getMessage());
		}
	}

	private void addErrorToValidationResult(String message) {
		DataGroup errorMessages = getErrorMessagesGroup();
		int repeatId = calculateRepeatId(errorMessages);
		DataAtomic error = createErrorWithMessageAndRepeatId(message, repeatId);
		errorMessages.addChild(error);

	}

	private DataGroup getErrorMessagesGroup() {
		ensureErrorMessagesGroupExist();
		return validationResult.getFirstGroupWithNameInData(ERROR_MESSAGES);
	}

	private void ensureErrorMessagesGroupExist() {
		if (!validationResult.containsChildWithNameInData(ERROR_MESSAGES)) {
			validationResult.addChild(DataGroup.withNameInData(ERROR_MESSAGES));
		}
	}

	private int calculateRepeatId(DataGroup errorMessages) {
		return errorMessages.getChildren().isEmpty() ? 0 : errorMessages.getChildren().size();
	}

	private DataAtomic createErrorWithMessageAndRepeatId(String message, int repeatId) {
		DataAtomic error = DataAtomic.withNameInDataAndValue("errorMessage", message);
		error.setRepeatId(String.valueOf(repeatId));
		return error;
	}

	private void possiblyEnsureLinksExist(DataGroup validationRecord, String recordTypeToValidate,
			String recordIdOrNullIfCreate, String metadataId) {
		String validateLinks = validationRecord.getFirstAtomicValueWithNameInData("validateLinks");
		if ("true".equals(validateLinks)) {
			ensureLinksExist(recordTypeToValidate, recordIdOrNullIfCreate, metadataId);
		}
	}

	private void ensureLinksExist(String recordTypeToValidate, String recordIdToUse,
			String metadataId) {
		DataGroup topLevelDataGroup = recordAsSpiderDataGroup;
		DataGroup collectedLinks = linkCollector.collectLinks(metadataId, topLevelDataGroup,
				recordTypeToValidate, recordIdToUse);
		checkIfLinksExist(collectedLinks);
	}

	private void checkIfLinksExist(DataGroup collectedLinks) {
		try {
			checkToPartOfLinkedDataExistsInStorage(collectedLinks);
		} catch (DataException exception) {
			addErrorToValidationResult(exception.getMessage());
		}
	}

	private void validateIncomingDataAsSpecifiedInMetadata(String metadataId) {
		DataGroup dataGroup = recordAsSpiderDataGroup;
		ValidationAnswer validationAnswer = dataValidator.validateData(metadataId, dataGroup);
		possiblyAddErrorMessages(validationAnswer);
		if (validationResult.containsChildWithNameInData(ERROR_MESSAGES)) {
			validationResult.addChild(DataAtomic.withNameInDataAndValue("valid", "false"));
		} else {
			validationResult.addChild(DataAtomic.withNameInDataAndValue("valid", "true"));
		}
	}

	private void possiblyAddErrorMessages(ValidationAnswer validationAnswer) {
		if (validationAnswer.dataIsInvalid()) {
			addErrorMessages(validationAnswer);
		}
	}

	private void addErrorMessages(ValidationAnswer validationAnswer) {
		for (String errorMessage : validationAnswer.getErrorMessages()) {
			addErrorToValidationResult(errorMessage);
		}
	}

	private void addReadActionToComplyWithRecordStructure(DataRecord record) {
		record.addAction(Action.READ);
	}
}
