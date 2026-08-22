package org.linlinjava.litemall.tests.contract;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProductBrowsingContractTest {

    @Test
    public void mallBrowseAc00Contract001_productBrowsingPathsMatchFrontendAndBackendMappings() throws Exception {
        Path root = Paths.get("..", "..").toAbsolutePath().normalize();
        JsonNode contract = new ObjectMapper().readTree(root.resolve("Tests/contracts/product-browsing-api.contract.json").toFile());
        String mobileFrontend = read(root.resolve(contract.get("basePaths").get("mobileFrontend").asText()));
        String miniProgramApi = read(root.resolve(contract.get("basePaths").get("miniProgramApi").asText()));
        String homeController = read(root.resolve(contract.get("basePaths").get("homeController").asText()));
        String goodsController = read(root.resolve(contract.get("basePaths").get("goodsController").asText()));

        assertTrue(homeController.contains("@RequestMapping(\"/wx/home\")"));
        assertTrue(goodsController.contains("@RequestMapping(\"/wx/goods\")"));

        for (JsonNode endpoint : contract.get("endpoints")) {
            String frontendPath = endpoint.get("frontendPath").asText();
            String controllerSource = "home".equals(endpoint.get("controller").asText()) ? homeController : goodsController;
            String mapping = endpoint.get("backendMapping").asText();

            assertTrue("frontend path missing: " + frontendPath,
                    mobileFrontend.contains("'" + frontendPath + "'")
                            || miniProgramApi.contains("'" + stripLeadingSlash(frontendPath) + "'"));
            assertTrue("backend mapping missing: " + mapping, controllerSource.contains("\"" + mapping + "\""));
            assertTrue("backend HTTP method mismatch for " + mapping,
                    hasMethodMapping(controllerSource, endpoint.get("method").asText(), mapping));
        }
    }

    @Test
    public void mallBrowseAc00Contract002_contractFixtureKeepsStableCaseIdsAndMethods() throws Exception {
        Path root = Paths.get("..", "..").toAbsolutePath().normalize();
        JsonNode contract = new ObjectMapper().readTree(root.resolve("Tests/contracts/product-browsing-api.contract.json").toFile());

        assertTrue(contract.hasNonNull("feature"));
        assertTrue(contract.has("endpoints"));

        for (JsonNode endpoint : contract.get("endpoints")) {
            String id = endpoint.get("id").asText();
            String method = endpoint.get("method").asText();
            assertTrue("invalid test_case_id: " + id,
                    id.matches("MALL-BROWSE-AC\\d{2}-(BE-UT|FE-IT|E2E|CONTRACT)-\\d{3}"));
            assertTrue("unsupported method: " + method, method.matches("GET|POST|PUT|DELETE"));
            assertFalse("backend mapping must not be blank", endpoint.get("backendMapping").asText().trim().isEmpty());
        }
    }

    @Test
    public void mallBrowseAc03Contract001_requiredGoodsDetailQueryFieldIsStillId() throws Exception {
        Path root = Paths.get("..", "..").toAbsolutePath().normalize();
        JsonNode contract = new ObjectMapper().readTree(root.resolve("Tests/contracts/product-browsing-api.contract.json").toFile());
        String goodsController = read(root.resolve(contract.get("basePaths").get("goodsController").asText()));

        JsonNode detailEndpoint = null;
        for (JsonNode endpoint : contract.get("endpoints")) {
            if ("MALL-BROWSE-AC03-CONTRACT-001".equals(endpoint.get("id").asText())) {
                detailEndpoint = endpoint;
                break;
            }
        }

        assertTrue("detail endpoint missing", detailEndpoint != null);
        assertTrue("contract must require id", detailEndpoint.get("requiredQueryFields").toString().contains("\"id\""));
        assertTrue("WxGoodsController.detail must keep @NotNull Integer id",
                Pattern.compile("detail\\s*\\([^)]*@NotNull\\s+Integer\\s+id", Pattern.DOTALL)
                        .matcher(goodsController)
                        .find());
    }

    private String read(Path path) throws Exception {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    private String stripLeadingSlash(String value) {
        return value.startsWith("/") ? value.substring(1) : value;
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
