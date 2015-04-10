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

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.querydsl.core.*;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.Param;

public class CollQueryStandardTest {

    private final Date birthDate = new Date();

    private final java.sql.Date date = new java.sql.Date(birthDate.getTime());

    private final java.sql.Time time = new java.sql.Time(birthDate.getTime());

    private final QCat cat = new QCat("cat");

    private final QCat otherCat = new QCat("otherCat");

    private final List<Cat> data = Arrays.asList(
            new Cat("Bob", 1, birthDate),
            new Cat("Ruth", 2, birthDate),
            new Cat("Felix", 3, birthDate),
            new Cat("Allen", 4, birthDate),
            new Cat("Mary", 5, birthDate)
    );

    private static final Expression<?>[] NO_EXPRESSIONS = new Expression[0];
    
    private QueryExecution standardTest = new QueryExecution(Module.COLLECTIONS, Target.MEM) {
        @Override
        protected Projectable<?> createQuery() {
            return CollQueryFactory.from(cat, data).from(otherCat, data);
        }
        @Override
        protected Projectable<?> createQuery(Predicate filter) {
            return CollQueryFactory.from(cat, data).from(otherCat, data)
                    .where(filter).select(cat.name);
        }
    };

    @Test
    public void test() {
        Cat kitten = data.get(0).getKittens().get(0);
        standardTest.runArrayTests(cat.kittenArray, otherCat.kittenArray, kitten, new Cat());
        standardTest.runBooleanTests(cat.name.isNull(), otherCat.kittens.isEmpty());
        standardTest.runCollectionTests(cat.kittens, otherCat.kittens, kitten, new Cat());
        standardTest.runDateTests(cat.dateField, otherCat.dateField, date);
        standardTest.runDateTimeTests(cat.birthdate, otherCat.birthdate, birthDate);
        standardTest.runListTests(cat.kittens, otherCat.kittens, kitten, new Cat());
        standardTest.runMapTests(cat.kittensByName, otherCat.kittensByName, "Kitty", kitten, "NoName", new Cat());

        // int
        standardTest.runNumericCasts(cat.id, otherCat.id, 1);
        standardTest.runNumericTests(cat.id, otherCat.id, 1);

        standardTest.runStringTests(cat.name, otherCat.name, "Bob");
        standardTest.runTimeTests(cat.timeField, otherCat.timeField, time);
        standardTest.report();
    }

    @Test
    public void TupleProjection() {
        List<Tuple> tuples = CollQueryFactory.from(cat, data)
            .select(cat.name, cat.birthdate).list();
        for (Tuple tuple : tuples) {
            assertNotNull(tuple.get(cat.name));
            assertNotNull(tuple.get(cat.birthdate));
        }
    }
    
    @Test
    public void Nested_TupleProjection() {
        Concatenation concat = new Concatenation(cat.name, cat.name);
        List<Tuple> tuples = CollQueryFactory.from(cat, data)
            .select(concat, cat.name, cat.birthdate).list();
        for (Tuple tuple : tuples) {
            assertNotNull(tuple.get(cat.name));
            assertNotNull(tuple.get(cat.birthdate));
            assertEquals(tuple.get(cat.name) + tuple.get(cat.name), tuple.get(concat));
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void ArrayProjection() {
        List<String[]> results =  CollQueryFactory.from(cat, data)
            .select(new ArrayConstructorExpression<String>(String[].class, cat.name)).list();
        assertFalse(results.isEmpty());
        for (String[] result : results) {
            assertNotNull(result[0]);
        }
    }

    @Test
    public void ConstructorProjection() {
        List<Projection> projections =  CollQueryFactory.from(cat, data)
            .select(ConstructorExpression.create(Projection.class, cat.name, cat)).list();
        assertFalse(projections.isEmpty());
        for (Projection projection : projections) {
            assertNotNull(projection);
        }
    }
    
    @Test
    public void Params() {
        Param<String> name = new Param<String>(String.class,"name");
        assertEquals("Bob", CollQueryFactory.from(cat, data).where(cat.name.eq(name)).set(name,"Bob").select(cat.name).uniqueResult());
    }

    @Test
    public void Params_anon() {
        Param<String> name = new Param<String>(String.class);
        assertEquals("Bob", CollQueryFactory.from(cat, data).where(cat.name.eq(name)).set(name,"Bob").select(cat.name).uniqueResult());
    }

    @Test(expected=ParamNotSetException.class)
    public void Params_not_set() {
        Param<String> name = new Param<String>(String.class,"name");
        assertEquals("Bob", CollQueryFactory.from(cat, data).where(cat.name.eq(name)).select(cat.name).uniqueResult());
    }
    
    @Test
    public void Limit() {
        CollQueryFactory.from(cat, data).limit(Long.MAX_VALUE).list();
    }
   
}
