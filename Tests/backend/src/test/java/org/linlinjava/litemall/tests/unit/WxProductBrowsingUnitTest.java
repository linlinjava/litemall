package org.linlinjava.litemall.tests.unit;

import org.junit.Before;
import org.junit.Test;
import org.linlinjava.litemall.core.system.SystemConfig;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.service.*;
import org.linlinjava.litemall.wx.service.HomeCacheManager;
import org.linlinjava.litemall.wx.service.WxGrouponRuleService;
import org.linlinjava.litemall.wx.web.WxGoodsController;
import org.linlinjava.litemall.wx.web.WxHomeController;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class WxProductBrowsingUnitTest {
    private WxHomeController homeController;
    private WxGoodsController goodsController;
    private LitemallAdService adService;
    private LitemallGoodsService goodsService;
    private LitemallBrandService brandService;
    private LitemallTopicService topicService;
    private LitemallCategoryService categoryService;
    private WxGrouponRuleService grouponRuleService;
    private LitemallCouponService couponService;
    private LitemallGoodsProductService productService;
    private LitemallIssueService issueService;
    private LitemallGoodsAttributeService attributeService;
    private LitemallCommentService commentService;
    private LitemallUserService userService;
    private LitemallCollectService collectService;
    private LitemallFootprintService footprintService;
    private LitemallSearchHistoryService searchHistoryService;
    private LitemallGoodsSpecificationService specificationService;
    private LitemallGrouponRulesService grouponRulesService;

    @Before
    public void setUp() {
        HomeCacheManager.clearAll();
        Map<String, String> configs = new HashMap<>();
        configs.put(SystemConfig.LITEMALL_WX_INDEX_NEW, "2");
        configs.put(SystemConfig.LITEMALL_WX_INDEX_HOT, "2");
        configs.put(SystemConfig.LITEMALL_WX_INDEX_BRAND, "1");
        configs.put(SystemConfig.LITEMALL_WX_INDEX_TOPIC, "1");
        configs.put(SystemConfig.LITEMALL_WX_INDEX_CATLOG_LIST, "2");
        configs.put(SystemConfig.LITEMALL_WX_INDEX_CATLOG_GOODS, "4");
        configs.put(SystemConfig.LITEMALL_WX_SHARE, "false");
        SystemConfig.setConfigs(configs);

        homeController = new WxHomeController();
        goodsController = new WxGoodsController();
        adService = mock(LitemallAdService.class);
        goodsService = mock(LitemallGoodsService.class);
        brandService = mock(LitemallBrandService.class);
        topicService = mock(LitemallTopicService.class);
        categoryService = mock(LitemallCategoryService.class);
        grouponRuleService = mock(WxGrouponRuleService.class);
        couponService = mock(LitemallCouponService.class);
        productService = mock(LitemallGoodsProductService.class);
        issueService = mock(LitemallIssueService.class);
        attributeService = mock(LitemallGoodsAttributeService.class);
        commentService = mock(LitemallCommentService.class);
        userService = mock(LitemallUserService.class);
        collectService = mock(LitemallCollectService.class);
        footprintService = mock(LitemallFootprintService.class);
        searchHistoryService = mock(LitemallSearchHistoryService.class);
        specificationService = mock(LitemallGoodsSpecificationService.class);
        grouponRulesService = mock(LitemallGrouponRulesService.class);

        ReflectionTestUtils.setField(homeController, "adService", adService);
        ReflectionTestUtils.setField(homeController, "goodsService", goodsService);
        ReflectionTestUtils.setField(homeController, "brandService", brandService);
        ReflectionTestUtils.setField(homeController, "topicService", topicService);
        ReflectionTestUtils.setField(homeController, "categoryService", categoryService);
        ReflectionTestUtils.setField(homeController, "grouponService", grouponRuleService);
        ReflectionTestUtils.setField(homeController, "couponService", couponService);

        ReflectionTestUtils.setField(goodsController, "goodsService", goodsService);
        ReflectionTestUtils.setField(goodsController, "productService", productService);
        ReflectionTestUtils.setField(goodsController, "goodsIssueService", issueService);
        ReflectionTestUtils.setField(goodsController, "goodsAttributeService", attributeService);
        ReflectionTestUtils.setField(goodsController, "brandService", brandService);
        ReflectionTestUtils.setField(goodsController, "commentService", commentService);
        ReflectionTestUtils.setField(goodsController, "userService", userService);
        ReflectionTestUtils.setField(goodsController, "collectService", collectService);
        ReflectionTestUtils.setField(goodsController, "footprintService", footprintService);
        ReflectionTestUtils.setField(goodsController, "categoryService", categoryService);
        ReflectionTestUtils.setField(goodsController, "searchHistoryService", searchHistoryService);
        ReflectionTestUtils.setField(goodsController, "goodsSpecificationService", specificationService);
        ReflectionTestUtils.setField(goodsController, "rulesService", grouponRulesService);
    }

    @Test
    public void mallBrowseAc01BeUt001_homeIndexAggregatesMerchandisingBlocksForGuest() {
        LitemallGoods newGoods = goods(100, "青瓷杯", 20);
        LitemallGoods hotGoods = goods(101, "手冲壶", 20);
        LitemallCategory parent = category(10, 0, "茶器");
        LitemallCategory child = category(20, 10, "茶杯");
        List<LitemallGoods> floorGoods = Collections.singletonList(newGoods);

        when(adService.queryIndex()).thenReturn(Collections.singletonList(new LitemallAd()));
        when(categoryService.queryChannel()).thenReturn(Collections.singletonList(parent));
        when(couponService.queryList(0, 3)).thenReturn(Collections.singletonList(new LitemallCoupon()));
        when(goodsService.queryByNew(0, 2)).thenReturn(Collections.singletonList(newGoods));
        when(goodsService.queryByHot(0, 2)).thenReturn(Collections.singletonList(hotGoods));
        when(brandService.query(0, 1)).thenReturn(Collections.singletonList(new LitemallBrand()));
        when(topicService.queryList(0, 1)).thenReturn(Collections.singletonList(new LitemallTopic()));
        when(grouponRuleService.queryList(0, 5)).thenReturn(Collections.emptyList());
        when(categoryService.queryL1WithoutRecommend(0, 2)).thenReturn(Collections.singletonList(parent));
        when(categoryService.queryByPid(10)).thenReturn(Collections.singletonList(child));
        when(goodsService.queryByCategory(Collections.singletonList(20), 0, 4)).thenReturn(floorGoods);

        Map<String, Object> response = responseMap(homeController.index(null));
        Map<String, Object> data = responseData(response);
        List<Map<String, Object>> floorGoodsList = cast(data.get("floorGoodsList"));

        assertEquals(0, response.get("errno"));
        assertEquals(Collections.singletonList(newGoods), data.get("newGoodsList"));
        assertEquals(Collections.singletonList(hotGoods), data.get("hotGoodsList"));
        assertEquals(Collections.singletonList(parent), data.get("channel"));
        assertEquals("茶器", floorGoodsList.get(0).get("name"));
        assertEquals(floorGoods, floorGoodsList.get(0).get("goodsList"));
        verify(couponService).queryList(0, 3);
        verify(couponService, never()).queryAvailableList(anyInt(), anyInt(), anyInt());
    }

    @Test
    public void mallBrowseAc01BeUt002_homeIndexUsesUserAvailableCouponsWhenLoggedIn() {
        stubEmptyHomeBlocks();

        Map<String, Object> response = responseMap(homeController.index(7));

        assertEquals(0, response.get("errno"));
        verify(couponService).queryAvailableList(7, 0, 3);
        verify(couponService, never()).queryList(0, 3);
    }

    @Test
    public void mallBrowseAc02BeUt001_goodsListKeepsFiltersPaginationAndSearchHistory() {
        LitemallGoods goods = goods(100, "青瓷杯", 20);
        LitemallCategory filterCategory = category(20, 10, "茶杯");
        List<Integer> categoryIds = Collections.singletonList(20);

        when(goodsService.querySelective(10, 5, "青瓷", false, true, 3, 8, "name", "asc"))
                .thenReturn(Collections.singletonList(goods));
        when(goodsService.getCatIds(5, "青瓷", false, true)).thenReturn(categoryIds);
        when(categoryService.queryL2ByIds(categoryIds)).thenReturn(Collections.singletonList(filterCategory));

        Map<String, Object> response = responseMap(
                goodsController.list(10, 5, "青瓷", true, false, 7, 3, 8, "name", "asc")
        );
        Map<String, Object> data = responseData(response);
        ArgumentCaptor<LitemallSearchHistory> searchCaptor = ArgumentCaptor.forClass(LitemallSearchHistory.class);

        assertEquals(0, response.get("errno"));
        assertEquals(Collections.singletonList(goods), data.get("list"));
        assertEquals(Collections.singletonList(filterCategory), data.get("filterCategoryList"));
        verify(searchHistoryService).save(searchCaptor.capture());
        assertEquals(Integer.valueOf(7), searchCaptor.getValue().getUserId());
        assertEquals("青瓷", searchCaptor.getValue().getKeyword());
        assertEquals("wx", searchCaptor.getValue().getFrom());
    }

    @Test
    public void mallBrowseAc03BeUt001_goodsDetailReturnsMerchandisingDataAndTracksLoggedInFootprint() {
        LitemallGoods goods = goods(100, "青瓷杯", 20);
        goods.setBrandId(5);
        goods.setShareUrl("https://cdn.example.test/share/goods-100.png");
        LitemallGoodsProduct product = product(9001, 100, "天青色", "88.00", 12);
        LitemallGoodsAttribute attribute = attribute("材质", "瓷");
        LitemallIssue issue = issue("如何保养?");
        LitemallBrand brand = brand(5, "器物集");
        LitemallComment comment = comment(301, 17, "手感很好");
        LitemallUser user = user(17, "summer");
        Map<String, Object> specification = new HashMap<>();
        specification.put("name", "颜色");
        specification.put("valueList", Collections.singletonList("天青色"));

        when(goodsService.findById(100)).thenReturn(goods);
        when(attributeService.queryByGid(100)).thenReturn(Collections.singletonList(attribute));
        when(specificationService.getSpecificationVoList(100)).thenReturn(Collections.singletonList(specification));
        when(productService.queryByGid(100)).thenReturn(Collections.singletonList(product));
        when(issueService.querySelective("", 1, 4, "", "")).thenReturn(Collections.singletonList(issue));
        when(brandService.findById(5)).thenReturn(brand);
        when(commentService.queryGoodsByGid(100, 0, 2)).thenReturn(Collections.singletonList(comment));
        when(userService.findById(17)).thenReturn(user);
        when(grouponRulesService.queryByGoodsId(100)).thenReturn(Collections.emptyList());
        when(collectService.count(7, (byte) 0, 100)).thenReturn(1);

        Map<String, Object> response = responseMap(goodsController.detail(7, 100));
        Map<String, Object> data = responseData(response);
        ArgumentCaptor<LitemallFootprint> footprintCaptor = ArgumentCaptor.forClass(LitemallFootprint.class);

        assertEquals(0, response.get("errno"));
        assertEquals(goods, data.get("info"));
        assertEquals(1, data.get("userHasCollect"));
        assertEquals(Collections.singletonList(product), data.get("productList"));
        assertEquals(Collections.singletonList(attribute), data.get("attribute"));
        assertEquals(brand, data.get("brand"));
        assertEquals("https://cdn.example.test/share/goods-100.png", data.get("shareImage"));
        verify(footprintService, timeout(1000)).add(footprintCaptor.capture());
        assertEquals(Integer.valueOf(7), footprintCaptor.getValue().getUserId());
        assertEquals(Integer.valueOf(100), footprintCaptor.getValue().getGoodsId());
    }

    @Test
    public void mallBrowseAc04BeUt001_relatedRejectsUnknownGoodsAndUsesCategoryRecommendations() {
        LitemallGoods goods = goods(100, "青瓷杯", 20);
        LitemallGoods related = goods(101, "手冲壶", 20);
        when(goodsService.findById(404)).thenReturn(null);
        when(goodsService.findById(100)).thenReturn(goods);
        when(goodsService.queryByCategory(20, 0, 6)).thenReturn(Collections.singletonList(related));

        Map<String, Object> missingResponse = responseMap(goodsController.related(404));
        Map<String, Object> relatedResponse = responseMap(goodsController.related(100));
        Map<String, Object> relatedData = responseData(relatedResponse);

        assertEquals(402, missingResponse.get("errno"));
        assertEquals(0, relatedResponse.get("errno"));
        assertEquals(Collections.singletonList(related), relatedData.get("list"));
        verify(goodsService).queryByCategory(20, 0, 6);
    }

    @Test
    public void mallBrowseAc05BeUt001_goodsCategoryPromotesParentToFirstChildAndReturnsSiblingFilters() {
        LitemallCategory parent = category(10, 0, "茶器");
        LitemallCategory firstChild = category(20, 10, "茶杯");
        LitemallCategory secondChild = category(21, 10, "茶壶");
        List<LitemallCategory> children = Arrays.asList(firstChild, secondChild);

        when(categoryService.findById(10)).thenReturn(parent);
        when(categoryService.queryByPid(10)).thenReturn(children);

        Map<String, Object> response = responseMap(goodsController.category(10));
        Map<String, Object> data = responseData(response);

        assertEquals(0, response.get("errno"));
        assertEquals(firstChild, data.get("currentCategory"));
        assertEquals(parent, data.get("parentCategory"));
        assertEquals(children, data.get("brotherCategory"));
    }

    @Test
    public void mallBrowseAc05BeUt002_goodsCategoryKeepsLeafAsCurrentAndFindsParentSiblings() {
        LitemallCategory parent = category(10, 0, "茶器");
        LitemallCategory current = category(20, 10, "茶杯");
        LitemallCategory sibling = category(21, 10, "茶壶");
        List<LitemallCategory> siblings = Arrays.asList(current, sibling);

        when(categoryService.findById(20)).thenReturn(current);
        when(categoryService.findById(10)).thenReturn(parent);
        when(categoryService.queryByPid(10)).thenReturn(siblings);

        Map<String, Object> response = responseMap(goodsController.category(20));
        Map<String, Object> data = responseData(response);

        assertEquals(0, response.get("errno"));
        assertEquals(current, data.get("currentCategory"));
        assertEquals(parent, data.get("parentCategory"));
        assertEquals(siblings, data.get("brotherCategory"));
    }

    @Test
    public void mallBrowseAc06BeUt001_goodsCountReturnsOnSaleGoodsTotal() {
        when(goodsService.queryOnSale()).thenReturn(42);

        Map<String, Object> response = responseMap(goodsController.count());

        assertEquals(0, response.get("errno"));
        assertEquals(42, response.get("data"));
        verify(goodsService).queryOnSale();
    }

    private void stubEmptyHomeBlocks() {
        when(adService.queryIndex()).thenReturn(Collections.emptyList());
        when(categoryService.queryChannel()).thenReturn(Collections.emptyList());
        when(couponService.queryAvailableList(anyInt(), anyInt(), anyInt())).thenReturn(Collections.emptyList());
        when(goodsService.queryByNew(anyInt(), anyInt())).thenReturn(Collections.emptyList());
        when(goodsService.queryByHot(anyInt(), anyInt())).thenReturn(Collections.emptyList());
        when(brandService.query(anyInt(), anyInt())).thenReturn(Collections.emptyList());
        when(topicService.queryList(anyInt(), anyInt())).thenReturn(Collections.emptyList());
        when(grouponRuleService.queryList(anyInt(), anyInt())).thenReturn(Collections.emptyList());
        when(categoryService.queryL1WithoutRecommend(anyInt(), anyInt())).thenReturn(Collections.emptyList());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> responseMap(Object response) {
        return (Map<String, Object>) response;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> responseData(Map<String, Object> response) {
        return (Map<String, Object>) response.get("data");
    }

    @SuppressWarnings("unchecked")
    private <T> T cast(Object value) {
        return (T) value;
    }

    private LitemallGoods goods(Integer id, String name, Integer categoryId) {
        LitemallGoods goods = new LitemallGoods();
        goods.setId(id);
        goods.setName(name);
        goods.setCategoryId(categoryId);
        goods.setBrandId(0);
        goods.setRetailPrice(new BigDecimal("88.00"));
        goods.setPicUrl("https://cdn.example.test/goods-" + id + ".png");
        goods.setDetail("<p>" + name + "</p>");
        return goods;
    }

    private LitemallCategory category(Integer id, Integer pid, String name) {
        LitemallCategory category = new LitemallCategory();
        category.setId(id);
        category.setPid(pid);
        category.setName(name);
        return category;
    }

    private LitemallGoodsProduct product(Integer id, Integer goodsId, String specification, String price, Integer number) {
        LitemallGoodsProduct product = new LitemallGoodsProduct();
        product.setId(id);
        product.setGoodsId(goodsId);
        product.setSpecifications(new String[]{specification});
        product.setPrice(new BigDecimal(price));
        product.setNumber(number);
        product.setUrl("https://cdn.example.test/product-" + id + ".png");
        return product;
    }

    private LitemallGoodsAttribute attribute(String name, String value) {
        LitemallGoodsAttribute attribute = new LitemallGoodsAttribute();
        attribute.setAttribute(name);
        attribute.setValue(value);
        return attribute;
    }

    private LitemallIssue issue(String question) {
        LitemallIssue issue = new LitemallIssue();
        issue.setQuestion(question);
        return issue;
    }

    private LitemallBrand brand(Integer id, String name) {
        LitemallBrand brand = new LitemallBrand();
        brand.setId(id);
        brand.setName(name);
        return brand;
    }

    private LitemallComment comment(Integer id, Integer userId, String content) {
        LitemallComment comment = new LitemallComment();
        comment.setId(id);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setPicUrls(new String[0]);
        return comment;
    }

    private LitemallUser user(Integer id, String nickname) {
        LitemallUser user = new LitemallUser();
        user.setId(id);
        user.setNickname(nickname);
        user.setAvatar("https://cdn.example.test/avatar-" + id + ".png");
        return user;
    }
}
