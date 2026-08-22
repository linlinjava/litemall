package org.linlinjava.litemall.tests.contract;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FrontendBackendOrderContractTest {

    @Test
    public void mallPayAc07Contract001_mobileOrderApiPathsMatchBackendControllerMappings() throws Exception {
        Path root = Paths.get("..", "..").toAbsolutePath().normalize();
        String frontendApi = read(root.resolve("litemall-vue/src/api/api.js"));
        String backendController = read(root.resolve("litemall-wx-api/src/main/java/org/linlinjava/litemall/wx/web/WxOrderController.java"));
        JsonNode contract = new ObjectMapper().readTree(root.resolve("Tests/contracts/order-api.contract.json").toFile());

        assertTrue(backendController.contains("@RequestMapping(\"/wx/order\")"));

        for (JsonNode endpoint : contract.get("endpoints")) {
            JsonNode frontendPath = endpoint.get("frontendPath");
            if (!frontendPath.isNull()) {
                assertTrue("frontend path missing: " + frontendPath.asText(), frontendApi.contains("'" + frontendPath.asText() + "'"));
            }
            String mapping = endpoint.get("backendMapping").asText();
            assertTrue("backend mapping missing: " + mapping, backendController.contains("\"" + mapping + "\""));
            assertTrue(
                    "backend HTTP method mismatch for " + mapping,
                    hasMethodMapping(backendController, endpoint.get("method").asText(), mapping)
            );
        }
    }

    @Test
    public void mallPayAc00Contract001_contractFixtureKeepsStableCaseIdsAndHttpMethods() throws Exception {
        Path root = Paths.get("..", "..").toAbsolutePath().normalize();
        JsonNode contract = new ObjectMapper().readTree(root.resolve("Tests/contracts/order-api.contract.json").toFile());

        assertTrue(contract.hasNonNull("feature"));
        assertTrue(contract.has("endpoints"));

        for (JsonNode endpoint : contract.get("endpoints")) {
            String id = endpoint.get("id").asText();
            String method = endpoint.get("method").asText();

            assertTrue("invalid test_case_id: " + id, id.matches("MALL-PAY-AC\\d{2}-(FE-IT|CONTRACT)-\\d{3}"));
            assertTrue("unsupported method: " + method, method.matches("GET|POST|PUT|DELETE"));
            assertFalse("backend mapping must not be blank", endpoint.get("backendMapping").asText().trim().isEmpty());
        }
    }

    @Test
    public void mallPayAc00Contract002_openApiSpecKeepsPathsMethodsAndTestCaseIdsInSync() throws Exception {
        Path root = Paths.get("..", "..").toAbsolutePath().normalize();
        JsonNode contract = new ObjectMapper().readTree(root.resolve("Tests/contracts/order-api.contract.json").toFile());
        Map<String, Object> openApi = new Yaml().load(read(root.resolve("Tests/contracts/order-api.openapi.yaml")));
        Map<String, Object> paths = map(openApi.get("paths"));

        for (JsonNode endpoint : contract.get("endpoints")) {
            String mapping = endpoint.get("backendMapping").asText();
            String path = "/order/" + mapping;
            String method = endpoint.get("method").asText().toLowerCase(Locale.ROOT);
            Map<String, Object> pathItem = map(paths.get(path));
            Map<String, Object> operation = map(pathItem.get(method));

            assertFalse("OpenAPI operation missing for " + method + " " + path, operation.isEmpty());
            assertTrue("OpenAPI operationId missing for " + method + " " + path, operation.containsKey("operationId"));
            assertTrue("OpenAPI test case id mismatch for " + method + " " + path,
                    endpoint.get("id").asText().equals(operation.get("x-test-case-id")));

            JsonNode frontendPath = endpoint.get("frontendPath");
            if (!frontendPath.isNull()) {
                assertTrue("OpenAPI frontend path mismatch for " + method + " " + path,
                        frontendPath.asText().equals(operation.get("x-frontend-path")));
            }
            assertTrue("OpenAPI backend mapping mismatch for " + method + " " + path,
                    mapping.equals(operation.get("x-backend-mapping")));

            assertRequiredFieldsMatch(endpoint, operation, openApi);
        }
    }

    @Test
    public void mallPayAc07Contract002_requestMappingMethodMustBelongToTheSameEndpointAnnotation() {
        String controllerSource =
                "@RequestMapping(\"submit\")\n" +
                "public Object submit() { return null; }\n" +
                "@RequestMapping(value = \"cancel\", method = RequestMethod.POST)\n" +
                "public Object cancel() { return null; }\n";

        assertFalse(hasMethodMapping(controllerSource, "POST", "submit"));
        assertTrue(hasMethodMapping(controllerSource, "POST", "cancel"));
    }

    @Test
    public void mallPayAc07Contract003_requestMappingSupportsNamedPathAndMethodArray() {
        String controllerSource =
                "@RequestMapping(path = {\"pay-notify\"}, method = {RequestMethod.POST})\n" +
                "public Object payNotify() { return null; }\n";

        assertTrue(hasMethodMapping(controllerSource, "POST", "pay-notify"));
        assertFalse(hasMethodMapping(controllerSource, "GET", "pay-notify"));
    }

    private String read(Path path) throws Exception {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> map(Object value) {
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return java.util.Collections.emptyMap();
    }

    @SuppressWarnings("unchecked")
    private List<Object> list(Object value) {
        if (value instanceof List) {
            return (List<Object>) value;
        }
        return java.util.Collections.emptyList();
    }

    private void assertRequiredFieldsMatch(JsonNode endpoint, Map<String, Object> operation, Map<String, Object> openApi) {
        if (endpoint.has("requiredPayloadFields")) {
            List<Object> expected = list(new ObjectMapper().convertValue(endpoint.get("requiredPayloadFields"), List.class));
            List<Object> actual = requiredJsonBodyFields(operation, openApi);
            assertTrue("OpenAPI JSON body fields missing: " + expected, actual.containsAll(expected));
        }
        if (endpoint.has("requiredQueryFields")) {
            List<Object> expected = list(new ObjectMapper().convertValue(endpoint.get("requiredQueryFields"), List.class));
            List<Object> actual = requiredQueryFields(operation);
            assertTrue("OpenAPI query fields missing: " + expected, actual.containsAll(expected));
        }
        if (endpoint.has("requiredXmlFields")) {
            List<Object> expected = list(new ObjectMapper().convertValue(endpoint.get("requiredXmlFields"), List.class));
            List<Object> actual = requiredXmlFields(operation, openApi);
            assertTrue("OpenAPI XML fields missing: " + expected, actual.containsAll(expected));
        }
    }

    private List<Object> requiredJsonBodyFields(Map<String, Object> operation, Map<String, Object> openApi) {
        Map<String, Object> schema = resolveSchema(jsonRequestSchema(operation, openApi), openApi);
        return list(schema.get("required"));
    }

    private List<Object> requiredXmlFields(Map<String, Object> operation, Map<String, Object> openApi) {
        Map<String, Object> requestBody = resolveRequestBody(map(operation.get("requestBody")), openApi);
        Map<String, Object> content = map(requestBody.get("content"));
        Map<String, Object> xml = map(content.get("application/xml"));
        Map<String, Object> schema = resolveSchema(map(xml.get("schema")), openApi);
        return list(schema.get("required"));
    }

    private Map<String, Object> jsonRequestSchema(Map<String, Object> operation, Map<String, Object> openApi) {
        Map<String, Object> requestBody = resolveRequestBody(map(operation.get("requestBody")), openApi);
        Map<String, Object> content = map(requestBody.get("content"));
        Map<String, Object> json = map(content.get("application/json"));
        return map(json.get("schema"));
    }

    private Map<String, Object> resolveRequestBody(Map<String, Object> requestBody, Map<String, Object> openApi) {
        if (!requestBody.containsKey("$ref")) {
            return requestBody;
        }
        Map<String, Object> components = map(openApi.get("components"));
        Map<String, Object> requestBodies = map(components.get("requestBodies"));
        return map(requestBodies.get(refName(String.valueOf(requestBody.get("$ref")))));
    }

    private Map<String, Object> resolveSchema(Map<String, Object> schema, Map<String, Object> openApi) {
        if (!schema.containsKey("$ref")) {
            return schema;
        }
        Map<String, Object> components = map(openApi.get("components"));
        Map<String, Object> schemas = map(components.get("schemas"));
        return map(schemas.get(refName(String.valueOf(schema.get("$ref")))));
    }

    private String refName(String ref) {
        return ref.substring(ref.lastIndexOf('/') + 1);
    }

    private List<Object> requiredQueryFields(Map<String, Object> operation) {
        java.util.ArrayList<Object> result = new java.util.ArrayList<>();
        for (Object parameterObject : list(operation.get("parameters"))) {
            Map<String, Object> parameter = map(parameterObject);
            if (Boolean.TRUE.equals(parameter.get("required"))) {
                result.add(parameter.get("name"));
            }
        }
        return result;
    }

    private boolean hasMethodMapping(String controllerSource, String method, String mapping) {
        String normalizedMethod = method.toUpperCase(Locale.ROOT);
        String methodMapping = normalizedMethod.substring(0, 1)
                + normalizedMethod.substring(1).toLowerCase(Locale.ROOT)
                + "Mapping";

        return annotationWithMappingExists(controllerSource, methodMapping, mapping)
                || requestMappingWithMethodExists(controllerSource, normalizedMethod, mapping);
    }

    private boolean annotationWithMappingExists(String source, String annotationName, String mapping) {
        Matcher matcher = Pattern.compile("@" + annotationName + "\\s*\\(([^)]*)\\)", Pattern.DOTALL).matcher(source);
        while (matcher.find()) {
            if (containsStringLiteral(matcher.group(1), mapping)) {
                return true;
            }
        }
        return false;
    }

    private boolean requestMappingWithMethodExists(String source, String method, String mapping) {
        Matcher matcher = Pattern.compile("@RequestMapping\\s*\\(([^)]*)\\)", Pattern.DOTALL).matcher(source);
        while (matcher.find()) {
            String annotationArgs = matcher.group(1);
            if (containsStringLiteral(annotationArgs, mapping)
                    && Pattern.compile("\\bRequestMethod\\s*\\.\\s*" + method + "\\b").matcher(annotationArgs).find()) {
                return true;
            }
        }
        return false;
    }

    private boolean containsStringLiteral(String source, String expected) {
        Matcher matcher = Pattern.compile("\"([^\"]*)\"").matcher(source);
        while (matcher.find()) {
            if (expected.equals(matcher.group(1))) {
                return true;
            }
        }
        return false;
    }
}
