package com.tmg.es.model;

import org.elasticsearch.index.query.QueryBuilder;

/**
 * @author chensl [cookchensl@gmail.com]
 * @date 2018/7/20 9:49
 * @description
 * @since 2.8.1
 */
public class MultiSearchInfo {
    private String indexName;
    private String typeName;
    private QueryBuilder queryBuilder;
    private int size;
    private int from;

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public QueryBuilder getQueryBuilder() {
        return queryBuilder;
    }

    public void setQueryBuilder(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public MultiSearchInfo(String indexName, String typeName, QueryBuilder queryBuilder, int size, int from) {
        this.indexName = indexName;
        this.typeName = typeName;
        this.queryBuilder = queryBuilder;
        this.size = size;
        this.from = from;
    }
}
