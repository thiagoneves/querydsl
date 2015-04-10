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
package com.querydsl.jpa;

import javax.persistence.Entity;

import com.querydsl.core.support.CollectionAnyVisitor;
import com.querydsl.core.support.Context;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.jpa.impl.JPAQuery;

/**
 * JPACollectionAnyVisitor extends the {@link CollectionAnyVisitor} class with module specific
 * extensions
 *
 * @author tiwe
 *
 */
public final class JPACollectionAnyVisitor extends CollectionAnyVisitor {

    @SuppressWarnings("unchecked")
    @Override
    protected Predicate exists(Context c, Predicate condition) {
        JPAQuery<Void> query = new JPAQuery<Void>();
        for (int i = 0; i < c.paths.size(); i++) {
            Path<?> child = c.paths.get(i).getMetadata().getParent();
            EntityPath<Object> replacement = (EntityPath<Object>) c.replacements.get(i);
            if (c.paths.get(i).getType().isAnnotationPresent(Entity.class)) {
                query.from(Expressions.listPath((Class)c.paths.get(i).getType(), SimplePath.class, child.getMetadata()), replacement);
            } else {
                // join via parent
                Path<?> parent = child.getMetadata().getParent();
                EntityPathBase<Object> newParent = new EntityPathBase<Object>(parent.getType(),
                        ExpressionUtils.createRootVariable(parent, Math.abs(condition.hashCode())));
                EntityPath<Object> newChild = new EntityPathBase<Object>(child.getType(),
                        PathMetadataFactory.forProperty(newParent, child.getMetadata().getName()));
                query.from(newParent).innerJoin(newChild, replacement);
                query.where(ExpressionUtils.eq(newParent, parent));
            }
        }
        c.clear();
        query.where(condition);
        //return query.exists();
        return ExpressionUtils.predicate(Ops.EXISTS, query);
    }

}
