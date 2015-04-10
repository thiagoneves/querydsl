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
package com.querydsl.collections;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mysema.commons.lang.CloseableIterator;
import com.mysema.commons.lang.IteratorAdapter;
import com.querydsl.core.*;
import com.querydsl.core.support.ProjectableQueryBase;
import com.querydsl.core.types.*;

/**
 * AbstractCollQuery provides a base class for Collection query implementations.
 *
 *
 * @see CollQuery
 *
 * @author tiwe
 */
public abstract class AbstractCollQuery<T, Q extends AbstractCollQuery<T, Q>> extends ProjectableQueryBase<T, Q>
        implements ProjectableQuery<T, Q> {

    private final Map<Expression<?>, Iterable<?>> iterables = new HashMap<Expression<?>, Iterable<?>>();

    private final QueryEngine queryEngine;

    @SuppressWarnings("unchecked")
    public AbstractCollQuery(QueryMetadata metadata, QueryEngine queryEngine) {
        super(new CollQueryMixin<Q>(metadata));
        this.queryMixin.setSelf((Q) this);
        this.queryEngine = queryEngine;
    }

    @Override
    public long count() {
        try {
            return queryEngine.count(getMetadata(), iterables);
        } catch (Exception e) {
            throw new QueryException(e.getMessage(), e);
        } finally {
            reset();
        }
    }

    @Override
    public boolean exists() {
        try {
            return queryEngine.exists(getMetadata(), iterables);
        } catch (Exception e) {
            throw new QueryException(e.getMessage(), e);
        } finally {
            reset();
        }
    }

    private <D> Expression<D> createAlias(Path<? extends Collection<D>> target, Path<D> alias) {
        return ExpressionUtils.operation(alias.getType(), Ops.ALIAS, target, alias);
    }

    private <D> Expression<D> createAlias(MapExpression<?,D> target, Path<D> alias) {
        return ExpressionUtils.operation(alias.getType(), Ops.ALIAS, target, alias);
    }

    /**
     * Add a query source
     *
     * @param <A>
     * @param entity Path for the source
     * @param col content of the source
     * @return
     */
    public <A> Q from(Path<A> entity, Iterable<? extends A> col) {
        iterables.put(entity, col);
        getMetadata().addJoin(JoinType.DEFAULT, entity);
        return (Q)this;
    }

    /**
     * Bind the given collection to an already existing query source
     *
     * @param <A>
     * @param entity Path for the source
     * @param col content of the source
     * @return
     */
    public <A> Q bind(Path<A> entity, Iterable<? extends A> col) {
        iterables.put(entity, col);
        return (Q)this;
    }

    @Override
    public Q groupBy(Expression<?> e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Q groupBy(Expression<?>... o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Q having(Predicate e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Q having(Predicate... e) {
        throw new UnsupportedOperationException();
    }

    protected QueryEngine getQueryEngine() {
        return queryEngine;
    }

    /**
     * Define an inner join from the Collection typed path to the alias
     *
     * @param <P>
     * @param target
     * @param alias
     * @return
     */
    public <P> Q innerJoin(Path<? extends Collection<P>> target, Path<P> alias) {
        getMetadata().addJoin(JoinType.INNERJOIN, createAlias(target, alias));
        return (Q)this;
    }

    /**
     * Define an inner join from the Map typed path to the alias
     *
     * @param <P>
     * @param target
     * @param alias
     * @return
     */
    public <P> Q innerJoin(MapExpression<?,P> target, Path<P> alias) {
        getMetadata().addJoin(JoinType.INNERJOIN, createAlias(target, alias));
        return (Q)this;
    }

    /**
     * Define a left join from the Collection typed path to the alias
     *
     * @param <P>
     * @param target
     * @param alias
     * @return
     */
    public <P> Q leftJoin(Path<? extends Collection<P>> target, Path<P> alias) {
        getMetadata().addJoin(JoinType.LEFTJOIN, createAlias(target, alias));
        return (Q)this;
    }

    /**
     * Define a left join from the Map typed path to the alias
     *
     * @param <P>
     * @param target
     * @param alias
     * @return
     */
    public <P> Q leftJoin(MapExpression<?,P> target, Path<P> alias) {
        getMetadata().addJoin(JoinType.LEFTJOIN, createAlias(target, alias));
        return (Q)this;
    }

    @Override
    public CloseableIterator<T> iterate() {
        try {
            Expression<T> projection = (Expression<T>)queryMixin.getMetadata().getProjection();
            return new IteratorAdapter<T>(queryEngine.list(getMetadata(), iterables, projection).iterator());
        } finally {
            reset();
        }
    }

    @Override
    public List<T> list() {
        try {
            Expression<T> projection = (Expression<T>)queryMixin.getMetadata().getProjection();
            return queryEngine.list(getMetadata(), iterables, projection);
        } finally {
            reset();
        }
    }

    @Override
    public QueryResults<T> listResults() {
        Expression<T> projection = (Expression<T>)queryMixin.getMetadata().getProjection();
        long count = queryEngine.count(getMetadata(), iterables);
        if (count > 0l) {
            List<T> list = queryEngine.list(getMetadata(), iterables, projection);
            reset();
            return new QueryResults<T>(list, getMetadata().getModifiers(), count);
        } else {
            reset();
            return QueryResults.<T>emptyResults();
        }

    }

    @Override
    public T uniqueResult() {
        queryMixin.setUnique(true);
        if (queryMixin.getMetadata().getModifiers().getLimit() == null) {
            limit(2l);
        }
        return uniqueResult(iterate());
    }

    private void reset() {
        getMetadata().reset();
    }

}
