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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.querydsl.core.types.dsl.BooleanExpression;

public class CompilationOverheadTest {

    private static final QCat cat = QCat.cat;

    @Test
    public void test() {
        List<BooleanExpression> conditions = Arrays.asList(
            cat.mate.isNull(),
            cat.mate.isNotNull(),
            cat.mate.name.eq("Kitty"),
            cat.mate.name.ne("Kitty"),
            cat.mate.isNotNull().and(cat.mate.name.eq("Kitty")),
            cat.mate.isNotNull().and(cat.mate.name.eq("Kitty")).and(cat.kittens.isEmpty())
        );

        // 1st
        for (BooleanExpression condition : conditions) {
            query(condition);
        }
        System.err.println();

        // 2nd
        for (BooleanExpression condition : conditions) {
            query(condition);
        }
    }

    private void query(BooleanExpression condition) {
        long start = System.currentTimeMillis();
        CollQueryFactory.from(cat, Collections.<Cat>emptyList()).where(condition).list();
        long duration = System.currentTimeMillis() - start;
        System.out.println(condition + " : " + duration + "ms");
    }

}
