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
package com.querydsl.jpa.impl;

import javax.annotation.Nullable;
import javax.inject.Provider;
import javax.persistence.EntityManager;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.jpa.JPQLQueryFactory;
import com.querydsl.jpa.JPQLTemplates;

/**
 * Factory class for query and DML clause creation
 *
 * @author tiwe
 *
 */
public class JPAQueryFactory implements JPQLQueryFactory  {

    @Nullable
    private final JPQLTemplates templates;

    private final Provider<EntityManager> entityManager;

    public JPAQueryFactory(Provider<EntityManager> entityManager) {
        this.entityManager = entityManager;
        this.templates = null;
    }

    public JPAQueryFactory(JPQLTemplates templates, Provider<EntityManager> entityManager) {
        this.entityManager = entityManager;
        this.templates = templates;
    }

    @Override
    public JPADeleteClause delete(EntityPath<?> path) {
        if (templates != null) {
            return new JPADeleteClause(entityManager.get(), path, templates);    
        } else {
            return new JPADeleteClause(entityManager.get(), path);
        }        
    }

    @Override
    public <T> JPAQuery<T> select(Expression<T> expr) {
        return query().select(expr);
    }

    @Override
    public JPAQuery<Tuple> select(Expression<?>... exprs) {
        return query().select(exprs);
    }

    @Override
    public JPAQuery<Void> from(EntityPath<?> from) {
        return query().from(from);
    }

    @Override
    public JPAUpdateClause update(EntityPath<?> path) {
        if (templates != null) {
            return new JPAUpdateClause(entityManager.get(), path, templates);    
        } else {
            return new JPAUpdateClause(entityManager.get(), path);
        }        
    }

    @Override
    public JPAQuery<Void> query() {
        if (templates != null) {
            return new JPAQuery<Void>(entityManager.get(), templates);
        } else {
            return new JPAQuery<Void>(entityManager.get());
        }        
    }

}
