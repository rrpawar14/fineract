package org.apache.fineract.infrastructure.creditbureau.serialization;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class CreditBureauTokenDataCommandFromApiJsonDeserializer {

    private final Set<String> supportedParameters = new HashSet<>(
            Arrays.asList("username", "password", "subscriptionId", "subscriptionKey"));
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public CreditBureauTokenDataCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {
        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("tokendata");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final String username = this.fromApiJsonHelper.extractStringNamed("username", element);
        baseDataValidator.reset().parameter("username").value(username).notBlank().notExceedingLengthOf(100);

        final String password = this.fromApiJsonHelper.extractStringNamed("password", element);
        baseDataValidator.reset().parameter("password").value(password).notBlank().notExceedingLengthOf(100);

        final String subscriptionId = this.fromApiJsonHelper.extractStringNamed("subscriptionId", element);
        baseDataValidator.reset().parameter("subscriptionId").value(subscriptionId).notBlank().notExceedingLengthOf(100);

        final String subscriptionKey = this.fromApiJsonHelper.extractStringNamed("subscriptionKey", element);
        baseDataValidator.reset().parameter("subscriptionKey").value(subscriptionKey).notBlank().notExceedingLengthOf(100);

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) {
            throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist", "Validation errors exist.",
                    dataValidationErrors);
        }
    }
}
