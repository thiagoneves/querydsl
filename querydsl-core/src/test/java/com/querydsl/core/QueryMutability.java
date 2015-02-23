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
package com.querydsl.core;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.querydsl.core.types.Expression;

public final class QueryMutability<Q extends ProjectableQuery<?,?>> {

    private final Q query;

    private final QueryMetadata metadata;

    public QueryMutability(Q query) throws SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        this.query = query;
        this.metadata = (QueryMetadata) query.getClass().getMethod("getMetadata").invoke(query);
    }

    public void test(Expression<?> p1, Expression<?> p2) throws IOException {
        System.err.println("count");
        query.select(p1).count();

        System.err.println("countDistinct");
        query.select(p1).distinct().count();

        System.err.println("iterate");
        query.select(p1).iterate();
        query.select(p1, p2).iterate();

        System.err.println("iterateDistinct");
        query.select(p1).distinct().iterate();
        query.select(p1, p2).distinct().iterate();

        System.err.println("list");
        query.select(p1).list();
        query.select(p1, p2).list();

        System.err.println("distinct list");
        query.select(p1).distinct().list();
        query.select(p2).distinct().list();

        System.err.println("listResults");
        query.select(p1).listResults();

        System.err.println("distinct listResults");
        query.select(p1).distinct().listResults();

        System.err.println("uniqueResult");
        query.select(p1).uniqueResult();
        query.select(p1,p2).uniqueResult();
    }

}
