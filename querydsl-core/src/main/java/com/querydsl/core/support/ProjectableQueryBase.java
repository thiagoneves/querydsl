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
package com.querydsl.core.support;

import java.util.List;

import javax.annotation.Nullable;

import com.mysema.commons.lang.CloseableIterator;
import com.mysema.commons.lang.IteratorAdapter;
import com.querydsl.core.*;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.Visitor;

/**
 * ProjectableQuery extends the {@link QueryBase} class to provide default
 * implementations of the methods of the {@link Projectable} interface
 *
 * @author tiwe
 */
public abstract class ProjectableQueryBase<T, Q extends ProjectableQueryBase<T, Q>>
        extends QueryBase<Q> implements SubQueryExpression<T>, Projectable<T> {

    public ProjectableQueryBase(QueryMixin<Q> queryMixin) {
        super(queryMixin);
    }

    @Override
    public List<T> list() {
        return IteratorAdapter.asList(iterate());
    }

    public final boolean notExists() {
        return !exists();
    }

    @Override
    public final T firstResult() {
        return limit(1).uniqueResult();
    }

    public <T> T transform(ResultTransformer<T> transformer) {
        return transformer.transform((ProjectableQuery)this);
    }
    
    @Nullable
    protected <T> T uniqueResult(CloseableIterator<T> it) {
        try{
            if (it.hasNext()) {
                T rv = it.next();
                if (it.hasNext()) {
                    throw new NonUniqueResultException();
                }
                return rv;
            } else {
                return null;
            }
        }finally{
            it.close();
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof SubQueryExpression) {
            SubQueryExpression<?> s = (SubQueryExpression<?>)o;
            return s.getMetadata().equals(queryMixin.getMetadata());
        } else {
            return false;
        }
    }

    @Override
    public final int hashCode() {
        return queryMixin.getMetadata().hashCode();
    }

    @Override
    public final QueryMetadata getMetadata() {
        return queryMixin.getMetadata();
    }

    @Override
    public final <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this, context);
    }

    @Override
    public Class<T> getType() {
        Expression<?> projection = queryMixin.getMetadata().getProjection();
        return (Class) (projection != null ? projection.getType() : Void.class);
    }


}
