package com.elastic.elasticsearchdemo.search;

import cn.hutool.json.JSONUtil;
import com.elastic.elasticsearchdemo.bean.ClassDoc;
import com.elastic.elasticsearchdemo.bean.HotelDoc;
import com.elastic.elasticsearchdemo.response.ResponseBean;
import com.elastic.elasticsearchdemo.util.EsUtils;
import org.apache.lucene.search.highlight.Highlighter;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
@RestController
@RequestMapping("/document/hotel")
public class HotelController {
    @Autowired
    RestHighLevelClient es;

    @Autowired
    EsUtils search;
    @GetMapping("add")
    public String add() throws Exception{
        //CreateIndexRequest hotelIndex = new CreateIndexRequest("hotel");
        //hotelIndex.mapping("{\n" +
        //        "    \"mappings\": {\n" +
        //        "        \"properties\": {\n" +
        //        "            \"id\": {\n" +
        //        "                \"type\": \"long\",\n" +
        //        "                \"index\": \"true\"\n" +
        //        "            },\n" +
        //        "            \"hotelName\": {\n" +
        //        "                \"type\": \"text\",\n" +
        //        "                \"analyzer\":\"ik_smart\"\n" +
        //        "            },\n" +
        //        "            \"address\": {\n" +
        //        "                \"type\": \"text\",\n" +
        //        "                \"analyzer\": \"ik_smart\"\n" +
        //        "            },\n" +
        //        "            \"description\":{\n" +
        //        "                 \"type\": \"text\",\n" +
        //        "                \"analyzer\": \"ik_smart\"\n" +
        //        "            },\n" +
        //        "            \"start\":{\n" +
        //        "                \"type\": \"integer\"\n" +
        //        "            },\n" +
        //        "            \"price\":{\n" +
        //        "                \"type\": \"integer\"\n" +
        //        "            }\n" +
        //        "        }\n" +
        //        "    }\n" +
        //        "}",XContentType.JSON);
        //es.indices().create(hotelIndex, RequestOptions.DEFAULT);
        List<HotelDoc> hotelDocs = generateHotelData(100);
        for (HotelDoc hotelDoc : hotelDocs) {
            IndexRequest request = new IndexRequest("hotel").id(hotelDoc.getId().toString());
            request.source(JSONUtil.toJsonStr(hotelDoc), XContentType.JSON);
            es.index(request, RequestOptions.DEFAULT);
        }
        return "ok";
    }
    @GetMapping("/search/{text}")
    public ResponseBean search(@PathVariable String text) throws Exception {
        ResponseBean bean = new ResponseBean();
        //ResponseBean bean = matchQuery(text);

        //ResponseBean bean = multiMatchQuery(text);

        //boolQuery(text,bean);

        //termQuery(text, bean);

        //rangeQuery(bean);

        //pageQuery(text, bean);

        //sortQuery(text, bean);

        //highlightQuery(text, bean);

        //List<HotelDoc> hotelDocs = search.matchQuery("hotel1", "hotelName", text, HotelDoc.class);

        //List<HotelDoc> hotelDocList = search.boolQuery("hotel", HotelDoc.class, ele -> {
        //    ele.source().query(
        //            QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("hotelName", text))
        //                    .must(QueryBuilders.rangeQuery("price").lte(500))
        //    );
        //});

        SearchRequest request = new SearchRequest("hotel");
        request.source().query(QueryBuilders.matchQuery("hotelName", text)).sort("price", SortOrder.DESC).size(0);
        //聚合函数
        request.source().aggregation(
                //term聚合函数精确分组，按照字段值进行分组
                AggregationBuilders.terms("startAgg").field("start")
                        //基于term分组后进行stats统计聚合（包含avg，max，min等）
                        .subAggregation(AggregationBuilders.stats("priceAgg").field("price")));
        SearchResponse search = es.search(request, RequestOptions.DEFAULT);
        SearchHit[] hits = search.getHits().getHits();
        ArrayList<HotelDoc> hotelDocs = new ArrayList<>();
        for (SearchHit hit : hits) {
            hotelDocs.add(JSONUtil.toBean(hit.getSourceAsString(), HotelDoc.class));
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            for (HighlightField highlightField : highlightFields.values()) {
                String hotelName = highlightField.getFragments()[0].string();
                hotelDocs.get(hotelDocs.size() - 1).setHotelName(hotelName);
            }
        }
        bean.setData(hotelDocs);
        return bean;
    }

    /**
     * 高亮查询
     * @param text
     * @param bean
     * @throws IOException
     */
    private void highlightQuery(String text, ResponseBean bean) throws IOException {
        SearchRequest request = new SearchRequest("hotel");
        request.source().query(QueryBuilders.matchQuery("hotelName", text)).sort("price", SortOrder.DESC);
        request.source().highlighter(SearchSourceBuilder.highlight().field("hotelName"));
        SearchResponse search = es.search(request, RequestOptions.DEFAULT);
        SearchHit[] hits = search.getHits().getHits();
        ArrayList<HotelDoc> hotelDocs = new ArrayList<>();
        for (SearchHit hit : hits) {
            hotelDocs.add(JSONUtil.toBean(hit.getSourceAsString(), HotelDoc.class));
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            for (HighlightField highlightField : highlightFields.values()) {
                String hotelName = highlightField.getFragments()[0].string();
                hotelDocs.get(hotelDocs.size() - 1).setHotelName(hotelName);
            }
        }
        bean.setData(hotelDocs);
    }

    /**
     * 排序查询
     * @param text
     * @param bean
     * @throws IOException
     */
    private void sortQuery(String text, ResponseBean bean) throws IOException {
        SearchRequest request = new SearchRequest("hotel");
        request.source().query(QueryBuilders.matchQuery("hotelName", text)).sort("price", SortOrder.DESC);
        SearchResponse search = es.search(request, RequestOptions.DEFAULT);
        SearchHit[] hits = search.getHits().getHits();
        ArrayList<HotelDoc> hotelDocs = new ArrayList<>();
        for (SearchHit hit : hits) {
            hotelDocs.add(JSONUtil.toBean(hit.getSourceAsString(), HotelDoc.class));
        }
        bean.setData(hotelDocs);
    }

    /**
     * 分页查询
     * @param text
     * @param bean
     * @throws IOException
     */
    private void pageQuery(String text, ResponseBean bean) throws IOException {
        SearchRequest request = new SearchRequest("hotel");
        request.source().query(QueryBuilders.matchQuery("hotelName", text)).from(0).size(3);
        SearchResponse search = es.search(request, RequestOptions.DEFAULT);
        SearchHit[] hits = search.getHits().getHits();
        ArrayList<HotelDoc> hotelDocs = new ArrayList<>();
        for (SearchHit hit : hits) {
            hotelDocs.add(JSONUtil.toBean(hit.getSourceAsString(), HotelDoc.class));
        }
        bean.setData(hotelDocs);
    }

    /**
     * 范围查询
     * @param bean
     * @throws IOException
     */
    private void rangeQuery(ResponseBean bean) throws IOException {
        SearchRequest hotel = new SearchRequest("hotel");
        hotel.source().query(QueryBuilders.rangeQuery("price").gt("400").lt("600"));
        SearchResponse search = es.search(hotel, RequestOptions.DEFAULT);
        SearchHit[] hits = search.getHits().getHits();
        ArrayList<HotelDoc> list = new ArrayList<>();
        for (SearchHit hit : hits) {
            list.add(JSONUtil.toBean(hit.getSourceAsString(), HotelDoc.class));
        }
        bean.setData(list);
    }

    /**
     * 精确查询
     * @param text
     * @param bean
     * @throws IOException
     */
    private void termQuery(String text, ResponseBean bean) throws IOException {
        SearchRequest request = new SearchRequest("hotel");
        request.source().query(
                QueryBuilders.termQuery("start", text)
        );
        SearchResponse search = es.search(request, RequestOptions.DEFAULT);
        SearchHit[] hits = search.getHits().getHits();
        ArrayList<HotelDoc> list = new ArrayList<>();
        for (SearchHit hit : hits) {
            list.add(JSONUtil.toBean(hit.getSourceAsString(), HotelDoc.class));
        }
        bean.setData(list);
    }

    // 生成酒店数据的方法
    public static List<HotelDoc> generateHotelData(int numberOfHotels) {
        List<HotelDoc> hotels = new ArrayList<>();
        Random random = new Random();
        String[] realHotelNames = {
                "希尔顿酒店", "万豪酒店", "洲际酒店", "凯悦酒店", "香格里拉酒店",
                "四季酒店", "半岛酒店", "瑞吉酒店", "柏悦酒店", "文华东方酒店",
                "华龙酒店", "喜来登酒店", "锦江酒店", "温江酒店", "龙龙酒店",
                "7天连锁酒店","汉庭酒店"
        };
        String[] cities = {
                "北京", "上海", "广州", "深圳", "成都",
                "杭州", "武汉", "重庆", "西安", "天津"
        };

        for (long i = 1; i <= numberOfHotels; i++) {
            String hotelName = realHotelNames[(int) (i % realHotelNames.length)];
            String city = cities[random.nextInt(cities.length)];
            String address = city + "市" + "街道" + i;
            String description = "这家" + hotelName + "位于" + city + "，提供高品质的服务和设施，确保您的住宿舒适愉快。" + i;
            Integer start = random.nextInt(5) + 1; // 生成1到5的随机星级
            Integer price = random.nextInt(1000) + 100; // 生成100到1100的随机价格

            HotelDoc hotel = new HotelDoc(i, hotelName, address, description, start, price);
            hotels.add(hotel);
        }

        return hotels;
    }

    /**
     * 根据多条字段进行Boolean查询
     * @param text
     * @param bean
     * @throws IOException
     */
    private void boolQuery(String text, ResponseBean bean) throws IOException {
        SearchRequest request = new SearchRequest("hotel");
        request.source().query(
                QueryBuilders.boolQuery()
                        //必须匹配
                        .must(QueryBuilders.matchQuery("hotelName", text))
                        //不能匹配
                        .mustNot(QueryBuilders.matchQuery("address", "北京市"))
                        //过滤，必须匹配，不参与算分
                        .filter(QueryBuilders.matchQuery("start", 1))
        );
        SearchResponse search = es.search(request, RequestOptions.DEFAULT);
        SearchHit[] hits = search.getHits().getHits();
        ArrayList<HotelDoc> list = new ArrayList<>();
        for (SearchHit hit : hits) {
            list.add(JSONUtil.toBean(hit.getSourceAsString(), HotelDoc.class));
        }
        bean.setData(list);
    }

    /**
     * 多字段匹配检索查询
     * @param text
     * @return
     * @throws IOException
     */
    private ResponseBean multiMatchQuery(String text) throws IOException {
        SearchRequest request = new SearchRequest("hotel");

        request.source().query(
                QueryBuilders.multiMatchQuery(text, "hotelName", "description")
        );
        SearchResponse search = es.search(request, RequestOptions.DEFAULT);
        ArrayList<HotelDoc> list = new ArrayList<>();
        for (SearchHit hit : search.getHits().getHits()) {
            list.add(JSONUtil.toBean(hit.getSourceAsString(), HotelDoc.class));
        }
        ResponseBean bean = new ResponseBean();
        bean.setData(list);
        return bean;
    }

    /**
     * 单个匹配查询，默认查询十条数据
     * 如果连续.query()构造查询条件，最后一个query()中构造的条件会把前面的条件覆盖掉
     * 最终查询出来的结果是以最后一个query()为准
     */
    private ResponseBean matchQuery(String text) throws IOException {
        //1.构建查询请求，查哪个索引库
        SearchRequest request = new SearchRequest("hotel");
        //2.构建查询条件
        request.source().query(
                QueryBuilders.matchQuery("hotelName", text)
        );
        //3.查询
        SearchResponse search = es.search(request, RequestOptions.DEFAULT);
        //4.获取查询结果集
        SearchHit[] hits = search.getHits().getHits();
        //5.遍历封装
        ArrayList<HotelDoc> list = new ArrayList<>();
        for (SearchHit documentFields : hits) {
            list.add(JSONUtil.toBean(documentFields.getSourceAsString(), HotelDoc.class));
        }
        ResponseBean bean = new ResponseBean();
        bean.setData(list);
        return bean;
    }
}
