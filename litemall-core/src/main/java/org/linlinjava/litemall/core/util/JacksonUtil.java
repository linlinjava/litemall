package org.linlinjava.litemall.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * JSON 解析与序列化工具类，基于 Jackson 实现。
 */
public class JacksonUtil {

    private static final Log logger = LogFactory.getLog(JacksonUtil.class);

    /**
     * 解析 JSON 字符串中指定字段的字符串值。
     *
     * @param body  JSON 字符串
     * @param field 字段名
     * @return 字段对应的字符串值，不存在或解析失败时返回 null
     */
    public static String parseString(String body, String field) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node;
        try {
            /* 将 JSON 字符串解析为树形结构 */
            node = mapper.readTree(body);
            /* 获取指定字段对应的节点 */
            JsonNode leaf = node.get(field);
            if (leaf != null)
                return leaf.asText();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 解析 JSON 字符串中指定字段的字符串列表。
     *
     * @param body  JSON 字符串
     * @param field 字段名
     * @return 字段对应的字符串列表，不存在或解析失败时返回 null
     */
    public static List<String> parseStringList(String body, String field) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node;
        try {
            /* 将 JSON 字符串解析为树形结构 */
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);

            if (leaf != null)
                /* 将字段节点转换为字符串列表 */
                return mapper.convertValue(leaf, new TypeReference<List<String>>() {
                });
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 解析 JSON 字符串中指定字段的整数值。
     *
     * @param body  JSON 字符串
     * @param field 字段名
     * @return 字段对应的整数值，不存在或解析失败时返回 null
     */
    public static Integer parseInteger(String body, String field) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node;
        try {
            /* 将 JSON 字符串解析为树形结构 */
            node = mapper.readTree(body);
            /* 获取指定字段对应的节点 */
            JsonNode leaf = node.get(field);
            if (leaf != null)
                return leaf.asInt();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 解析 JSON 字符串中指定字段的整数列表。
     *
     * @param body  JSON 字符串
     * @param field 字段名
     * @return 字段对应的整数列表，不存在或解析失败时返回 null
     */
    public static List<Integer> parseIntegerList(String body, String field) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node;
        try {
            /* 将 JSON 字符串解析为树形结构 */
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);

            if (leaf != null)
                /* 将字段节点转换为整数列表 */
                return mapper.convertValue(leaf, new TypeReference<List<Integer>>() {
                });
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 解析 JSON 字符串中指定字段的布尔值。
     *
     * @param body  JSON 字符串
     * @param field 字段名
     * @return 字段对应的布尔值，不存在或解析失败时返回 null
     */
    public static Boolean parseBoolean(String body, String field) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node;
        try {
            /* 将 JSON 字符串解析为树形结构 */
            node = mapper.readTree(body);
            /* 获取指定字段对应的节点 */
            JsonNode leaf = node.get(field);
            if (leaf != null)
                return leaf.asBoolean();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 解析 JSON 字符串中指定字段的短整数值。
     *
     * @param body  JSON 字符串
     * @param field 字段名
     * @return 字段对应的短整数值，不存在或解析失败时返回 null
     */
    public static Short parseShort(String body, String field) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node;
        try {
            /* 将 JSON 字符串解析为树形结构 */
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if (leaf != null) {
                /* 先按 int 取值再强转为 short */
                Integer value = leaf.asInt();
                return value.shortValue();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 解析 JSON 字符串中指定字段的字节值。
     *
     * @param body  JSON 字符串
     * @param field 字段名
     * @return 字段对应的字节值，不存在或解析失败时返回 null
     */
    public static Byte parseByte(String body, String field) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node;
        try {
            /* 将 JSON 字符串解析为树形结构 */
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if (leaf != null) {
                /* 先按 int 取值再强转为 byte */
                Integer value = leaf.asInt();
                return value.byteValue();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 解析 JSON 字符串中指定字段为指定类型的对象。
     *
     * @param body  JSON 字符串
     * @param field 字段名
     * @param clazz 目标类型
     * @param <T>   目标类型泛型
     * @return 字段对应的对象，不存在或解析失败时返回 null
     */
    public static <T> T parseObject(String body, String field, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node;
        try {
            /* 将 JSON 字符串解析为树形结构 */
            node = mapper.readTree(body);
            node = node.get(field);
            /* 将字段节点转换为指定类型的对象 */
            return mapper.treeToValue(node, clazz);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 将 JSON 字符串解析为 JSON 节点。
     *
     * @param json JSON 字符串
     * @return JSON 节点，解析失败时返回 null
     */
    public static Object toNode(String json) {
        if (json == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            /* 解析 JSON 字符串为 JSON 节点 */
            return mapper.readTree(json);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    /**
     * 将 JSON 字符串解析为 Map。
     *
     * @param data JSON 字符串
     * @return Map 对象，解析失败时返回 null
     */
    public static Map<String, String> toMap(String data) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            /* 将 JSON 字符串反序列化为 Map */
            return objectMapper.readValue(data, new TypeReference<Map<String, String>>() {
            });
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 将对象序列化为 JSON 字符串。
     *
     * @param data 待序列化的对象
     * @return JSON 字符串，序列化失败时返回 null
     */
    public static String toJson(Object data) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            /* 将对象序列化为 JSON 字符串 */
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
