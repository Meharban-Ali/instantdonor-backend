package com.blood_donor.config.redisconfig;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PageImplDeserializer extends JsonDeserializer<Page<?>> {

    @Override
    public Page<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);

        List<Object> content = new ArrayList<>();
        JsonNode contentNode = node.get("content");
        if (contentNode != null) {
            if (contentNode.isArray()) {
                // If polymorphic type wrapper: ["java.util.Collections$UnmodifiableRandomAccessList", [...]]
                if (contentNode.size() == 2 && contentNode.get(0).isTextual() && contentNode.get(1).isArray()) {
                    for (JsonNode item : contentNode.get(1)) {
                        content.add(mapper.treeToValue(item, Object.class));
                    }
                } else {
                    for (JsonNode item : contentNode) {
                        content.add(mapper.treeToValue(item, Object.class));
                    }
                }
            } else {
                Object val = mapper.treeToValue(contentNode, Object.class);
                if (val instanceof List<?> list) {
                    content.addAll(list);
                }
            }
        }

        int number = 0;
        int size = 10;
        if (node.has("number")) {
            number = node.get("number").asInt();
        }
        if (node.has("size")) {
            size = node.get("size").asInt();
        }
        if (size <= 0) {
            size = 10;
        }

        long totalElements = content.size();
        if (node.has("totalElements")) {
            totalElements = node.get("totalElements").asLong();
        }

        return new PageImpl<>(content, PageRequest.of(number, size), totalElements);
    }
}
