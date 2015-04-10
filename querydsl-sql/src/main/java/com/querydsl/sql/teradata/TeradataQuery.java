/*
 * Copyright 2013, Mysema Ltd
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
package com.querydsl.sql.teradata;

import java.sql.Connection;

import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.QueryFlag;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.*;

/**
 * TeradataQuery provides Teradata related extensions to SQLQuery
 *
 * @author tiwe
 *
 */
public class TeradataQuery<T> extends AbstractSQLQuery<T, TeradataQuery<T>> {

    public TeradataQuery(Connection conn) {
        this(conn, new Configuration(TeradataTemplates.DEFAULT), new DefaultQueryMetadata());
    }

    public TeradataQuery(Connection conn, SQLTemplates templates) {
        this(conn, new Configuration(templates), new DefaultQueryMetadata());
    }

    public TeradataQuery(Connection conn, Configuration configuration) {
        this(conn, configuration, new DefaultQueryMetadata());
    }

    public TeradataQuery(Connection conn, Configuration configuration, QueryMetadata metadata) {
        super(conn, configuration, metadata);
    }

    /**
     * Adds a qualify expression
     *
     * @param predicate
     * @return
     */
    public TeradataQuery<T> qualify(Predicate predicate) {
        predicate = ExpressionUtils.predicate(SQLOps.QUALIFY, predicate);
        return queryMixin.addFlag(new QueryFlag(QueryFlag.Position.BEFORE_ORDER, predicate));
    }

    @Override
    public TeradataQuery<T> clone(Connection conn) {
        TeradataQuery<T> q = new TeradataQuery<T>(conn, getConfiguration(), getMetadata().clone());
        q.clone(this);
        return q;
    }

    @Override
    public <U> TeradataQuery<U> select(Expression<U> expr) {
        queryMixin.setProjection(expr);
        return (TeradataQuery<U>) this;
    }

    @Override
    public TeradataQuery<Tuple> select(Expression<?>... exprs) {
        queryMixin.setProjection(exprs);
        return (TeradataQuery<Tuple>) this;
    }

}
