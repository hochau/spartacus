package com.baoxue.spartacus.repository;

import com.baoxue.spartacus.config.BlogBeanConfig;
import com.baoxue.spartacus.globals.Globals;
import com.baoxue.spartacus.pojo.PageEntity;
import com.baoxue.spartacus.utils.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 注意：从6.x开始，一个index只能对应一个type，因此别妄想像5.x那样一个index可以有多个type了
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2018年4月8日 下午5:56:31
 */
@Component
public class ElasticsearchHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchHelper.class);

    @Autowired
    BlogBeanConfig blogBeanConfig;
    
    /**
     * 定义索引的字段属性：
     * 	1、Mapping，就是对索引库中索引的字段名及其数据类型进行定义，类似于关系数据库中表建立时要定义字段名及其数据类型那样，不过es的mapping比数据库灵活很多，它可以动态添加字段
     * 	2、一般不需要要指定mapping都可以，因为es会自动根据数据格式定义它的类型，但是如果需要对某些字段添加特殊属性（如：定义使用其它分词器、是否分词、是否存储等），就必须手动添加mapping
     * 	3、有两种添加mapping的方法，一种是定义在配置文件中，一种是运行时手动提交mapping，两种选一种就行了（需要先建立空索引，然后使用mapping添加各个字段及其属性）
     * 
     * @param index
     * @param type
     * @return
     * @throws IOException 
     */
    public boolean createIndex(String index, String type) throws IOException {
    	// 创建一个空索引
        TransportClient client = null;
        try {
            client = blogBeanConfig.getEsClient();
            CreateIndexResponse response = client.admin().indices().prepareCreate(index).execute().actionGet();
            if(response.isAcknowledged()) {
                // 添加mapping，properties下面的为索引里面的字段，type为数据类型，store为是否true（是否单独存储该字段），index是否true（该字段是否被索引）
                XContentBuilder mapping = null;
                if(index.equals(Globals.ARTICLE_INDEX_NAME)) {
                    mapping = XContentFactory.jsonBuilder()
                               .startObject()
                               .startObject("properties")
                               .startObject("id").field("type", "long").field("index","false").endObject()
                               .startObject("title").field("type", "text").field("analyzer","ik_max_word").endObject()
                               .startObject("author").field("type", "text").field("analyzer","ik_max_word").endObject()
                               .startObject("cname").field("type", "text").field("index","false").endObject()
                               .startObject("fromWhere").field("type", "text").field("index","false").endObject()
                               .startObject("labels").field("type", "text").field("analyzer","ik_max_word").endObject()
                               .startObject("publishTime").field("type", "date").field("format","yyyy-MM-dd HH:mm:ss").field("index","false").endObject()
                               .startObject("commentNumber").field("type", "integer").field("index","false").endObject()
                               .startObject("scanNumber").field("type", "integer").field("index","false").endObject()
                               .startObject("status").field("type", "integer").endObject()
                               .startObject("isTop").field("type", "integer").field("index","false").endObject()
                               .startObject("monthDay").field("type", "text").field("index","false").endObject()
                               .startObject("year").field("type", "text").field("index","false").endObject()
                               .startObject("pictures").field("type", "text").field("index","false").endObject()
                               .startObject("brief").field("type", "text").field("analyzer","ik_max_word").endObject()
                               .startObject("content").field("type", "text").field("index","false").endObject()
                               .endObject()
                               .endObject();
                } else if(index.equals(Globals.COS_RESOURCE_INDEX_NAME)) {
                    mapping = XContentFactory.jsonBuilder()
                               .startObject()
                               .startObject("properties")
                               .startObject("id").field("type", "long").field("index","false").endObject()
                               .startObject("parentId").field("type", "long").field("index","false").endObject()
                               .startObject("fileName").field("type", "text").field("analyzer","ik_max_word").endObject()
                               .startObject("key").field("type", "text").field("index","true").endObject()
                               .startObject("tags").field("type", "text").field("analyzer","ik_max_word").endObject()
                               .startObject("contentType").field("type", "text").field("index","false").endObject()
                               .startObject("cosType").field("type", "integer").field("index","true").endObject()
                               .startObject("rootPath").field("type", "text").field("index","true").endObject()
                               .startObject("region").field("type", "text").field("index","false").endObject()
                               .startObject("bucketName").field("type", "text").field("index","false").endObject()
                               .startObject("status").field("type", "integer").field("index","false").endObject()
                               .startObject("aclFlag").field("type", "integer").field("index","false").endObject()
                               .startObject("lastModified").field("type", "date").field("format","yyyy-MM-dd HH:mm:ss").field("index","false").endObject()
                               .endObject()
                               .endObject();
                }
                PutMappingRequest mappingRequest = Requests.putMappingRequest(index).type(type).source(mapping);
                client.admin().indices().putMapping(mappingRequest).actionGet();
            }
            return response.isAcknowledged();
        } catch (Exception e) {
            return false;
        } finally {
            if(client != null) {
                client.close();
            }
        }
    }
    
    
    
    /**
     * 创建一个空索引
     *
     * @param index
     * @return
     */
    public boolean createIndex(String index) {
        TransportClient client = null;
        try {
            client = blogBeanConfig.getEsClient();
            CreateIndexResponse response = client.admin().indices().prepareCreate(index).execute().actionGet();
            return response.isAcknowledged();
        } catch (Exception e) {
            return false;
        } finally {
            if(client != null) {
                client.close();
            }
        }
    }
    

    
    /**
     * 删除索引
     *
     * @param index
     * @return
     */
    public boolean deleteIndex(String index) {
        TransportClient client = null;
        try {
            client = blogBeanConfig.getEsClient();
            DeleteIndexResponse response = client.admin().indices().prepareDelete(index).execute().actionGet();
            return response.isAcknowledged();
        } catch (Exception e) {
            return false;
        } finally {
            if(client != null) {
                client.close();
            }
        }
    }
    

    /**
     * 判断索引是否存在
     *
     * @param index
     * @return
     */
    public boolean isIndexExist(String index) {
        TransportClient client = null;
        try {
            client = blogBeanConfig.getEsClient();
            IndicesExistsResponse response = client.admin().indices().exists(new IndicesExistsRequest(index)).actionGet();
            return response.isExists();
        } catch (Exception e) {
            return false;
        } finally {
            if(client != null) {
                client.close();
            }
        }
    }

    
    /**
     * 添加数据，指定ID
     *
     * @param map 要增加的数据，必须是json格式
     * @param index      索引，数据库名
     * @param type       类型，表名
     * @param id         数据唯一ID
     * @return
     */
    public boolean insertData(String index, String type, String id, Map<String, Object> map) {
        TransportClient client = null;
        try {
            client = blogBeanConfig.getEsClient();
            IndexResponse response = client.prepareIndex(index, type, id).setSource(map).get();
            return "CREATED".equalsIgnoreCase(response.getResult().toString()) ? true : false;
        } catch (Exception e) {
            return false;
        } finally {
            if(client != null) {
                client.close();
            }
        }
    }

    /**
     * 批量插入
     *
     * @param index
     * @param type
     * @param maps
     * @return
     */
    public boolean batchInsert(String index, String type, List<Map<String, Object>> maps) {
        TransportClient client = null;
        try {
            client = blogBeanConfig.getEsClient();
            BulkRequestBuilder bulk = client.prepareBulk();
            for (Map<String, Object> map : maps) {
                bulk.add(client.prepareIndex(index, type, map.get("id").toString()).setSource(map));
            }
            BulkResponse response = bulk.execute().actionGet();
            return !response.hasFailures();
        } catch(Exception e){
            return false;
        } finally{
            if (client != null) {
                client.close();
            }
        }
    }


    /**
     * 通过ID 删除数据
     *
     * @param index 索引，类似数据库
     * @param type  类型，类似表
     * @param id    数据ID
     */
    public boolean deleteData(String index, String type, String id) {
        TransportClient client = null;
        try {
            client = blogBeanConfig.getEsClient();
            DeleteResponse response = client.prepareDelete(index, type, id).execute().actionGet();
            return "DELETED".equalsIgnoreCase(response.getResult().toString()) ? true : false;
        } catch (Exception e) {
            return false;
        } finally {
            if(client != null) {
                client.close();
            }
        }
    }

    /**
     * 通过id列表，批量删除
     * @param index
     * @param type
     * @param ids
     * @return
     */
    public boolean batchDelete(String index, String type, List<String> ids) {
        TransportClient client = null;
        try {
            client = blogBeanConfig.getEsClient();
            BulkRequestBuilder bulk = client.prepareBulk();
            for (String id : ids) {
                bulk.add(client.prepareDelete().setIndex(index).setType(type).setId(id));
            }
            BulkResponse response = bulk.execute().actionGet();
            return !response.hasFailures();
        } catch (Exception e) {
            return false;
        } finally {
            if(client != null) {
                client.close();
            }
        }
    }

    /**
     * 批量更新
     *
     * @param index
     * @param type
     * @param maps
     * @return
     */
    public boolean batchUpdate(String index, String type, List<Map<String, Object>> maps) {
        TransportClient client = null;
        try {
            client = blogBeanConfig.getEsClient();
            BulkRequestBuilder bulk = client.prepareBulk();
            for (Map<String, Object> map : maps) {
                bulk.add(client.prepareUpdate(index, type, map.get("id").toString()).setDoc(map));
            }
            BulkResponse response = bulk.execute().actionGet();
            return !response.hasFailures();
        } catch(Exception e){
            return false;
        } finally{
            if (client != null) {
                client.close();
            }
        }
    }
    
    /**
     * 通过ID 更新数据
     *
     * @param map 要增加的数据
     * @param index      索引，类似数据库
     * @param type       类型，类似表
     * @param id         数据ID
     * @return
     */
    public boolean updateData(String index, String type, String id, Map<String, Object> map) {
        TransportClient client = null;
        try {
            client = blogBeanConfig.getEsClient();
            UpdateRequest updateRequest = new UpdateRequest();
            updateRequest.index(index).type(type).id(id).doc(map);
            UpdateResponse response  = client.update(updateRequest).actionGet();
            return "UPDATED".equalsIgnoreCase(response.getResult().toString()) ? true : false;
        } catch (Exception e) {
            return false;
        } finally {
            if(client != null) {
                client.close();
            }
        }
    }
    

    /**
     * 通过ID 获取数据
     *
     * @param index  索引，类似数据库
     * @param type   类型，类似表
     * @param id     数据ID
     * @param fields 需要显示的字段，逗号分隔（缺省为全部字段）
     * @return
     */
    public Map<String, Object> searchData(String index, String type, String id, String fields) {
        TransportClient client = null;
        try {
            client = blogBeanConfig.getEsClient();
            GetRequestBuilder getRequestBuilder = client.prepareGet(index, type, id);
            client.close();
            if (StringUtils.isNotEmpty(fields)) {
                getRequestBuilder.setFetchSource(fields.split(","), null);
            }
            GetResponse response =  getRequestBuilder.execute().actionGet();
            return response.getSource();
        } catch (Exception e) {
            return null;
        } finally {
            if(client != null) {
                client.close();
            }
        }
    }


    /**
     * 使用分词查询
     *
     * @param index    索引名称
     * @param type     类型名称
     * @param fields   需要显示的字段，逗号分隔（缺省为全部字段）
     * @param matchStr 过滤条件（field1=text1,field2=text2），The field name、The query text(to be analyzed)
     * @return
     */
    public List<Map<String, Object>> searchListData(String index, String type, String fields, String matchStr) {
        return searchListData(index, type, 0, 0, null, fields, null, false, null, matchStr);
    }
    

    /**
     * 使用分词查询
     *
     * @param index       索引名称
     * @param type        类型名称
     * @param fields      需要显示的字段，逗号分隔（缺省为全部字段）
     * @param sortField   排序字段
     * @param matchPhrase true 使用，短语精准匹配
     * @param matchStr    过滤条件（field1=text1,field2=text2），The field name、The query text(to be analyzed)
     * @return
     */
    public List<Map<String, Object>> searchListData(String index, String type, String fields, String sortField, boolean matchPhrase, String matchStr) {
        return searchListData(index, type, 0, 0, null, fields, sortField, matchPhrase, null, matchStr);
    }


    /**
     * 使用分词查询
     *
     * @param index          索引名称
     * @param type           类型名称
     * @param size           文档大小限制
     * @param fields         需要显示的字段，逗号分隔（缺省为全部字段）
     * @param sortField      排序字段
     * @param matchPhrase    true 使用，短语精准匹配
     * @param highlightField 高亮字段
     * @param matchStr       过滤条件（field1=text1,field2=text2），The field name、The query text(to be analyzed)
     * @return
     */
    public List<Map<String, Object>> searchListData(String index, String type, Integer size, String fields, String sortField, boolean matchPhrase, String highlightField, String matchStr) {
        return searchListData(index, type, 0, 0, size, fields, sortField, matchPhrase, highlightField, matchStr);
    }


    /**
     * 使用分词查询
     *
     * @param index          索引名称
     * @param type           类型名称
     * @param startTime      开始时间
     * @param endTime        结束时间
     * @param size           期望查询数量
     * @param fields         需要显示的字段，逗号分隔（缺省为全部字段）
     * @param sortField      排序字段
     * @param matchPhrase    true 使用，短语精准匹配
     * @param highlightFields 高亮字段，用英文逗号分隔
     * @param matchConditions 过滤条件如"field1=text1,field2=text2"，（The field name、The query text to be analyzed）
     * @return
     */
    public List<Map<String, Object>> searchListData(String index, String type, long startTime, long endTime, Integer size, String fields, String sortField, boolean matchPhrase, String highlightFields, String matchConditions) {
        TransportClient client = null;
        try {
            client = blogBeanConfig.getEsClient();
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index);
            if (StringUtils.isNotEmpty(type)) {
                searchRequestBuilder.setTypes(type.split(","));
            }

            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            if (startTime > 0 && endTime > 0) {
                boolQuery.must(QueryBuilders.rangeQuery("processTime")
                        .format("epoch_millis")
                        .from(startTime)
                        .to(endTime)
                        .includeLower(true)
                        .includeUpper(true));
            }

            /**
             * 组合查询
             * must(QueryBuilders) 		: AND
             * mustNot(QueryBuilders)	: NOT
             * should:                  : OR
             */
            if (StringUtils.isNotEmpty(matchConditions)) {
                for (String condition : matchConditions.split(",")) {
                    if (condition.split("=").length > 1) {
                        if (matchPhrase == Boolean.TRUE) {
                            boolQuery.should(QueryBuilders.matchPhraseQuery(condition.split("=")[0], condition.split("=")[1]));
                        } else {
                            boolQuery.should(QueryBuilders.matchQuery(condition.split("=")[0], condition.split("=")[1]));
                        }
                    }
                }
            }

            // 高亮结果集
            if (StringUtils.isNotEmpty(highlightFields)) {
                HighlightBuilder highlightBuilder = new HighlightBuilder();
                highlightBuilder.preTags("<span style='color:red' >"); // 前、后缀
                highlightBuilder.postTags("</span>");
                for(String highlightField : highlightFields.split(",")) { // 设置高亮字段
                    highlightBuilder.field(highlightField);
                }
                searchRequestBuilder.highlighter(highlightBuilder);
            }

            searchRequestBuilder.setQuery(boolQuery);

            if (StringUtils.isNotEmpty(fields)) {
                searchRequestBuilder.setFetchSource(fields.split(","), null);
            }
            searchRequestBuilder.setFetchSource(true);

            if (StringUtils.isNotEmpty(sortField)) {
                searchRequestBuilder.addSort(sortField, SortOrder.DESC);
            }

            if (size != null && size > 0) {
                searchRequestBuilder.setSize(size);
            }

            //打印的内容 可以在 Elasticsearch head 和 Kibana 上执行查询
    //        LOGGER.info("\n{}", searchRequestBuilder);

            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

            long total = searchResponse.getHits().totalHits;
            long handled = searchResponse.getHits().getHits().length;
    //        LOGGER.info("共查询到[{}]条数据，共处理[{}]条数据", total, handled);

            if (searchResponse.status().getStatus() == 200) {
                // 解析对象
                return highlightSearchResponse(searchResponse, highlightFields);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        } finally {
            if(client != null) {
                client.close();
            }
        }
    }

    
    /**
     * 使用分词查询，并分页
     *
     * @param index          索引名称
     * @param type           类型名称
     * @param currentPage    当前页 从0开始
     * @param pageSize       每页显示条数
     * @param startTime      开始时间
     * @param endTime        结束时间
     * @param fields         需要显示的字段，逗号分隔（缺省为全部字段）
     * @param sortField      排序字段
     * @param highlightFields 高亮字段，用英文逗号分隔
     * @param mustMatchs 	  必须匹配的字段，Map<field, value>
     * @param searchContent  搜索内容
     * @param matchFields 	  搜索内容匹配哪些字段
     * @return
     */
    public PageEntity searchPageData(String index, String type, int currentPage, int pageSize, long startTime, long endTime, String fields, String sortField, String highlightFields, Map<String, Object> mustMatchs, String searchContent, String matchFields) {
        TransportClient client = null;
        try {
            client = blogBeanConfig.getEsClient();
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index);
            if (StringUtils.isNotEmpty(type)) {
                searchRequestBuilder.setTypes(type.split(","));
            }
            searchRequestBuilder.setSearchType(SearchType.QUERY_THEN_FETCH);

            // 需要显示的字段，逗号分隔（缺省为全部字段）
            if (StringUtils.isNotEmpty(fields)) {
                searchRequestBuilder.setFetchSource(fields.split(","), null);
            }

            //排序字段
            if (StringUtils.isNotEmpty(sortField)) {
                searchRequestBuilder.addSort(sortField, SortOrder.DESC);
            }

            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            if (startTime > 0 && endTime > 0) {
                boolQuery.must(QueryBuilders.rangeQuery("processTime")
                        .format("epoch_millis")
                        .from(startTime)
                        .to(endTime)
                        .includeLower(true)
                        .includeUpper(true));
            }

            /**
             * 组合查询
             * must(QueryBuilders) 		: AND
             * mustNot(QueryBuilders)	: NOT
             * should:                  : OR
             */
            // status AND x1 OR x2 OR x3 ...
            /*if (StringUtils.isNotEmpty(matchConditions)) {
                for (String condition : matchConditions.split(",")) {
                    if (matchPhrase == Boolean.TRUE) {
                        if("status".equals(condition.split("=")[0])) {
                            boolQuery.must(QueryBuilders.matchPhraseQuery(condition.split("=")[0], Integer.parseInt(condition.split("=")[1])));
                        } else {
                            boolQuery.should(QueryBuilders.matchPhraseQuery(condition.split("=")[0], condition.split("=")[1]));
                        }
                    } else {
                        if("status".equals(condition.split("=")[0])) {
                            boolQuery.must(QueryBuilders.matchQuery(condition.split("=")[0], Integer.parseInt(condition.split("=")[1])));
                        } else {
                            boolQuery.should(QueryBuilders.matchQuery(condition.split("=")[0], condition.split("=")[1]));
                        }
                    }
                }
            }*/
            // mustMatchs AND (x1 OR x2 OR x3 ...)
            if (!CommonUtils.isNull(mustMatchs)) {
                for(Map.Entry<String, Object> en : mustMatchs.entrySet()) {
                    boolQuery.must(QueryBuilders.matchQuery(en.getKey(), en.getValue()).operator(Operator.AND));
                }
            }

            if (!CommonUtils.isNull(searchContent) && !CommonUtils.isNull(matchFields)) {
                boolQuery.must(QueryBuilders.multiMatchQuery(searchContent, matchFields.split(",")).operator(Operator.OR));
            }

            // 高亮结果集
            if (StringUtils.isNotEmpty(highlightFields)) {
                HighlightBuilder highlightBuilder = new HighlightBuilder();
                highlightBuilder.preTags("<span style='color:red' >"); // 前、后缀
                highlightBuilder.postTags("</span>");
                for(String highlightField : highlightFields.split(",")) { // 设置高亮字段
                    highlightBuilder.field(highlightField);
                }
                searchRequestBuilder.highlighter(highlightBuilder);
            }

            searchRequestBuilder.setQuery(QueryBuilders.matchAllQuery());
            searchRequestBuilder.setQuery(boolQuery);

            // 分页应用
            searchRequestBuilder.setFrom(currentPage).setSize(pageSize);

            // 设置是否按查询匹配度排序
            searchRequestBuilder.setExplain(true);

            //打印的内容 可以在 Elasticsearch head 和 Kibana  上执行查询
            LOGGER.info("\n{}", searchRequestBuilder);

            // 执行搜索,返回搜索响应信息
            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

            long total = searchResponse.getHits().totalHits;
            long handled = searchResponse.getHits().getHits().length;
    //        LOGGER.info("共查询到[{}]条数据，共处理[{}]条数据", total, handled);

            if (searchResponse.status().getStatus() == 200) {
                // 解析对象
                List<Map<String, Object>> sourceList = highlightSearchResponse(searchResponse, highlightFields);
                return new PageEntity(currentPage, pageSize, (int) total, sourceList);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(client != null) {
                client.close();
            }
        }
    }
    

    /**
     * 高亮结果集 特殊处理
     *
     * @param searchResponse
     * @param highlightFields
     */
    private List<Map<String, Object>> highlightSearchResponse(SearchResponse searchResponse, String highlightFields) {
        List<Map<String, Object>> sourceList = new ArrayList<Map<String, Object>>();

        for (SearchHit searchHit : searchResponse.getHits().getHits()) {
            searchHit.getSourceAsMap().put("id", searchHit.getId());
			if (StringUtils.isNotEmpty(highlightFields)) {
				for(String field : highlightFields.split(",")) {
					if (StringUtils.isNotEmpty(field)) {
						
						if(searchHit.getHighlightFields().get(field) != null) {
							Text[] hitTexts = searchHit.getHighlightFields().get(field).getFragments();
							if (hitTexts != null) {
								StringBuffer sb = new StringBuffer();
								for (Text text : hitTexts) {
									sb.append(text.string());
								}
								//使用高亮结果集，覆盖正常结果集
								searchHit.getSourceAsMap().put(field, sb.toString());
							}
						}
					}
				}
			}
			sourceList.add(searchHit.getSourceAsMap());
		}
        return sourceList;
    }

}