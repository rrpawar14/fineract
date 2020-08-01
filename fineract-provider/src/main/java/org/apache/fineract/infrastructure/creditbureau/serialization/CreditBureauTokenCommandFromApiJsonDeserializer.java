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
public class CreditBureauTokenCommandFromApiJsonDeserializer {

    private final Set<String> supportedParameters = new HashSet<>(
            Arrays.asList("access_token", "token_type", "expires_in", "userName", ".issued", ".expires"));
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public CreditBureauTokenCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {
        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("tokens");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        /*
         * final String username =
         * this.fromApiJsonHelper.extractStringNamed("userName", element);
         * baseDataValidator.reset().parameter("userName").value(username).
         * notBlank().notExceedingLengthOf(100);
         */

        final String access_token = this.fromApiJsonHelper.extractStringNamed("access_token", element);
        baseDataValidator.reset().parameter("access_token").value(access_token).notBlank().notExceedingLengthOf(1000);

        final String token_type = this.fromApiJsonHelper.extractStringNamed("token_type", element);
        baseDataValidator.reset().parameter("token_type").value(token_type).notBlank().notExceedingLengthOf(100);

        final String expires_in = this.fromApiJsonHelper.extractStringNamed("expires_in", element);
        baseDataValidator.reset().parameter("expires_in").value(expires_in).notBlank().notExceedingLengthOf(100);

        final String userName = this.fromApiJsonHelper.extractStringNamed("userName", element);
        baseDataValidator.reset().parameter("userName").value(userName).notBlank().notExceedingLengthOf(100);

        final String issued = this.fromApiJsonHelper.extractStringNamed(".issued", element);
        baseDataValidator.reset().parameter(".issued").value(issued).notBlank().notExceedingLengthOf(100);

        final String expires = this.fromApiJsonHelper.extractStringNamed(".expires", element);
        baseDataValidator.reset().parameter(".expires").value(expires).notBlank().notExceedingLengthOf(100);

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) {
            throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist", "Validation errors exist.",
                    dataValidationErrors);
        }
    }

}
