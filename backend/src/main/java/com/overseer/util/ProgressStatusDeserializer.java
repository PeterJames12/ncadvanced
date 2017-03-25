package com.overseer.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.overseer.model.enums.ProgressStatus;
import com.overseer.service.impl.ProgressStatusUtil;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * Demoralizes json object to {@link ProgressStatus}.
 */
@RequiredArgsConstructor
public class ProgressStatusDeserializer extends JsonDeserializer<ProgressStatus> {

//    public ProgressStatusDeserializer() {
//        super(ProgressStatus.class);
//    }

    private final ProgressStatusUtil progressStatusUtil;

    @Override
    public ProgressStatus deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        final JsonNode jsonNode = jsonParser.readValueAsTree();
        int id = jsonNode.get("id").asInt();
//        String value = jsonNode.get("name").asText();

        ProgressStatus progressStatus = progressStatusUtil.getProgressById(id);

        if (progressStatus != null) {
            return progressStatus;
        } else {
            throw deserializationContext.mappingException("Cannot deserialize ProgressStatus from id " + id);
        }
    }
}
