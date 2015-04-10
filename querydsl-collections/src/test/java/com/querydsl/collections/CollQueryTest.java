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

import static com.querydsl.collections.CollQueryFactory.from;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

public class CollQueryTest extends AbstractQueryTest {

    @Test
    public void CustomTemplates() {
        CollQueryTemplates templates = new CollQueryTemplates() {{
            add(Ops.DateTimeOps.MONTH, "{0}.getMonthOfYear()");
            add(Ops.DateTimeOps.YEAR, "{0}.getYear()");
        }};
        new CollQuery(templates);
    }
    
    @Test
    public void InstanceOf() {
        assertEquals(
                Arrays.asList(c1, c2),
                query().from(cat, Arrays.asList(c1, c2)).where(cat.instanceOf(Cat.class))
                       .select(cat).list());
    }

    @Test
    public void After_And_Before() {
        query().from(cat, Arrays.asList(c1, c2))
            .where(
                cat.birthdate.lt(new Date()),
                cat.birthdate.loe(new Date()), 
                cat.birthdate.gt(new Date()),
                cat.birthdate.goe(new Date()))
            .select(cat).list();
    }

    @Test
    public void ArrayProjection() {
        // select pairs of cats with different names
        query().from(cat, cats).from(otherCat, cats).where(cat.name.ne(otherCat.name))
               .select(cat.name, otherCat.name).list();
        assertEquals(4*3, last.res.size());
    }

    @Test
    public void Cast() {
        NumberExpression<?> num = cat.id;
        Expression<?>[] expr = new Expression[] { num.byteValue(), num.doubleValue(),
                num.floatValue(), num.intValue(), num.longValue(),
                num.shortValue(), num.stringValue() };

        for (Expression<?> e : expr) {
            query().from(cat, Arrays.asList(c1, c2)).select(e).list();
        }

    }
    
    @Test
    public void Clone() {
        CollQuery<?> query = new CollQuery<Void>().from(cat, Collections.<Cat>emptyList()).where(cat.isNotNull()).clone();
        assertEquals("cat is not null", query.getMetadata().getWhere().toString());
    }

    @Test
    public void Primitives() {
        // select cats with kittens
        query().from(cat, cats).where(cat.kittens.size().ne(0)).select(cat.name).list();
        assertTrue(last.res.size() == 4);

        // select cats without kittens
        query().from(cat, cats).where(cat.kittens.size().eq(0)).select(cat.name).list();
        assertTrue(last.res.size() == 0);
    }

    @Test
    public void SimpleCases() {
        // select all cat names
        query().from(cat, cats).select(cat.name).list();
        assertTrue(last.res.size() == 4);

        // select all kittens
        query().from(cat, cats).select(cat.kittens).list();
        assertTrue(last.res.size() == 4);

        // select cats with kittens
        query().from(cat, cats).where(cat.kittens.size().gt(0)).select(cat.name).list();
        assertTrue(last.res.size() == 4);

        // select cats named Kitty
        query().from(cat, cats).where(cat.name.eq("Kitty")).select(cat.name).list();
        assertTrue(last.res.size() == 1);

        // select cats named Kitt%
        query().from(cat, cats).where(cat.name.matches("Kitt.*")).select(cat.name).list();
        assertTrue(last.res.size() == 1);

        query().from(cat, cats).select(cat.bodyWeight.add(cat.weight)).list();
    }

    @Test
    public void Various() {
        StringPath a = Expressions.stringPath("a");
        StringPath b = Expressions.stringPath("b");
        for (Tuple strs : from(a, "aa", "bb", "cc")
                .from(b, Arrays.asList("a","b"))
                .where(a.startsWith(b)).select(a, b).list()) {
            System.out.println(strs);
        }

        query().from(cat, cats).select(cat.mate).list();

        query().from(cat, cats).select(cat.kittens).list();

        query().from(cat, cats).where(cat.kittens.isEmpty()).select(cat).list();

        query().from(cat, cats).where(cat.kittens.isNotEmpty()).select(cat).list();

        query().from(cat, cats).where(cat.name.matches("fri.*")).select(cat.name).list();

    }

    @Test
    public void BigDecimals() {
        NumberPath<BigDecimal> a = Expressions.numberPath(BigDecimal.class, "cost");
        List<BigDecimal> nums = from(a, new BigDecimal("2.1"), new BigDecimal("20.21"),
                new BigDecimal("44.4")).where(a.lt(new BigDecimal("35.1"))).select(a).list();

        for (BigDecimal num : nums) {
            System.out.println(num);
        }
        assertEquals(2, nums.size());
        for (BigDecimal num : nums) {
            assertEquals(-1, num.compareTo(new BigDecimal("35")));
        }
    }

    @Test(expected=UnsupportedOperationException.class)
    public void GroupBy() {
        query().from(cat, cats).groupBy(cat.name);
    }

    @Test(expected=UnsupportedOperationException.class)
    public void Having() {
        query().from(cat, cats).having(cat.name.isNull());
    }

}
