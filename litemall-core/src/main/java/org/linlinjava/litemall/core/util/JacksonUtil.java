package org.linlinjava.litemall.core.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

public class JacksonUtil {
    public static String parseString(String body, String field) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if(leaf != null)
                return leaf.asText();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Integer parseInteger(String body, String field) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if(leaf != null)
                return leaf.asInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Integer> parseIntegerList(String body, String field) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);

            if(leaf != null)
                return mapper.convertValue(leaf, new TypeReference<List<String>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static Boolean parseBoolean(String body, String field) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if(leaf != null)
                return leaf.asBoolean();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Short parseShort(String body, String field) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if(leaf != null) {
                Integer value = leaf.asInt();
                return value.shortValue();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Byte parseByte(String body, String field) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if(leaf != null) {
                Integer value = leaf.asInt();
                return value.byteValue();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T parseObject(String body, String field, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = mapper.readTree(body);
            node = node.get(field);
            return mapper.treeToValue(node, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object toNode(String json) {
        if(json == null){
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(json);
            return jsonNode;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
