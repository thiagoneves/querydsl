/*
 * Copyright 2011, Mysema Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.sql.oracle;

import java.sql.Connection;

import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.QueryFlag.Position;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.AbstractSQLQuery;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.OracleTemplates;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.mysql.MySQLQuery;

/**
 * OracleQuery provides Oracle specific extensions to the base SQL query type
 *
 * @author tiwe
 */
public class OracleQuery<T> extends AbstractSQLQuery<T, OracleQuery<T>> {

    private static final String CONNECT_BY = "\nconnect by ";

    private static final String CONNECT_BY_NOCYCLE_PRIOR = "\nconnect by nocycle prior ";

    private static final String CONNECT_BY_PRIOR = "\nconnect by prior ";

    private static final String ORDER_SIBLINGS_BY = "\norder siblings by ";

    private static final String START_WITH = "\nstart with ";

    public OracleQuery(Connection conn) {
        this(conn, OracleTemplates.DEFAULT, new DefaultQueryMetadata());
    }

    public OracleQuery(Connection conn, SQLTemplates templates) {
        this(conn, templates, new DefaultQueryMetadata());
    }

    public OracleQuery(Connection conn, Configuration configuration) {
        super(conn, configuration, new DefaultQueryMetadata());
    }

    public OracleQuery(Connection conn, Configuration configuration, QueryMetadata metadata) {
        super(conn, configuration, metadata);
    }

    protected OracleQuery(Connection conn, SQLTemplates templates, QueryMetadata metadata) {
        super(conn, new Configuration(templates), metadata);
    }

    /**
     * @param cond
     * @return
     */
    public OracleQuery<T> connectByPrior(Predicate cond) {
        return addFlag(Position.BEFORE_ORDER, CONNECT_BY_PRIOR, cond);
    }

    /**
     * @param cond
     * @return
     */
    public OracleQuery<T> connectBy(Predicate cond) {
        return addFlag(Position.BEFORE_ORDER, CONNECT_BY, cond);
    }

    /**
     * @param cond
     * @return
     */
    public OracleQuery<T> connectByNocyclePrior(Predicate cond) {
        return addFlag(Position.BEFORE_ORDER, CONNECT_BY_NOCYCLE_PRIOR, cond);
    }

    /**
     * @param cond
     * @return
     */
    public <A> OracleQuery<T> startWith(Predicate cond) {
        return addFlag(Position.BEFORE_ORDER, START_WITH, cond);
    }

    /**
     * @param path
     * @return
     */
    public OracleQuery<T> orderSiblingsBy(Expression<?> path) {
        return addFlag(Position.BEFORE_ORDER, ORDER_SIBLINGS_BY, path);
    }
    
    @Override
    public OracleQuery<T> clone(Connection conn) {
        OracleQuery q = new OracleQuery(conn, getConfiguration(), getMetadata().clone());
        q.clone(this);
        return q;
    }

    // TODO : connect by root

    // TODO : connect by iscycle

    // TODO : connect by isleaf (pseudocolumn)

    // TODO : sys connect path

    @Override
    public <U> OracleQuery<U> select(Expression<U> expr) {
        queryMixin.setProjection(expr);
        return (OracleQuery<U>) this;
    }

    @Override
    public OracleQuery<Tuple> select(Expression<?>... exprs) {
        queryMixin.setProjection(exprs);
        return (OracleQuery<Tuple>) this;
    }
}


