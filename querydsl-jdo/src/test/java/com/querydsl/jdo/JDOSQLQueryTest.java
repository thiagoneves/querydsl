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
package com.querydsl.jdo;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jdo.sql.JDOSQLQuery;
import com.querydsl.jdo.test.domain.Product;
import com.querydsl.jdo.test.domain.sql.SProduct;
import com.querydsl.sql.HSQLDBTemplates;
import com.querydsl.sql.SQLTemplates;

public class JDOSQLQueryTest extends AbstractJDOTest{
    
    private final SQLTemplates sqlTemplates = new HSQLDBTemplates();
    
    private final SProduct product = SProduct.product;
    
    protected JDOSQLQuery<Void> sql() {
        return new JDOSQLQuery<Void>(pm, sqlTemplates);
    }

    @Test
    public void Count() {        
        assertEquals(30l, sql().from(product).count());        
    }
    
    @Test(expected=NonUniqueResultException.class)
    public void UniqueResult() {
        sql().from(product).select(product.name).uniqueResult();
    }
    
    @Test
    public void SingleResult() {
        sql().from(product).select(product.name).firstResult();
    }
    
    @Test
    public void SingleResult_With_Array() {
        sql().from(product).select(new Expression[]{product.name}).firstResult();
    }
    
    @Test    
    public void StartsWith_Count() {
        assertEquals(10l, sql().from(product).where(product.name.startsWith("A")).count());
        assertEquals(10l, sql().from(product).where(product.name.startsWith("B")).count());
        assertEquals(10l, sql().from(product).where(product.name.startsWith("C")).count());
        
    }
    
    @Test
    public void Eq_Count() {
        for (int i = 0; i < 10; i++) {
            assertEquals(1l, sql().from(product).where(product.name.eq("A"+i)).count());
            assertEquals(1l, sql().from(product).where(product.name.eq("B"+i)).count());
            assertEquals(1l, sql().from(product).where(product.name.eq("C"+i)).count());
        }
    }
    
    @Test
    public void ScalarQueries() {
        BooleanExpression filter = product.name.startsWith("A");
        
        // count
        assertEquals(10l, sql().from(product).where(filter).count());

        // countDistinct
        assertEquals(10l, sql().from(product).where(filter).distinct().count());

        // list
        assertEquals(10, sql().from(product).where(filter).select(product.name).list().size());

        // list with limit
        assertEquals(3, sql().from(product).limit(3).select(product.name).list().size());

        // list with offset
//        assertEquals(7, sql().from(product).offset(3).list(product.name).size());

        // list with limit and offset
        assertEquals(3, sql().from(product).offset(3).limit(3).select(product.name).list().size());

        // list multiple
        for (Tuple row : sql().from(product).select(product.productId, product.name, product.amount).list()) {
            assertNotNull(row.get(0, Object.class));
            assertNotNull(row.get(1, Object.class));
            assertNotNull(row.get(2, Object.class));
        }

        // listResults
        QueryResults<String> results = sql().from(product).limit(3).select(product.name).listResults();
        assertEquals(3, results.getResults().size());
        assertEquals(30l, results.getTotal());

    }
    
    @Ignore
    @Test
    @SuppressWarnings("unchecked")
    public void Union() throws SQLException {
        SubQueryExpression<Integer> sq1 = sql().from(product).select(product.amount.max());
        SubQueryExpression<Integer> sq2 = sql().from(product).select(product.amount.min());
        List<Integer> list = sql().union(sq1, sq2).list();
        assertFalse(list.isEmpty());
    }
    
    @Ignore
    @Test
    @SuppressWarnings("unchecked")
    public void Union_All() {
        SubQueryExpression<Integer> sq1 = sql().from(product).select(product.amount.max());
        SubQueryExpression<Integer> sq2 = sql().from(product).select(product.amount.min());
        List<Integer> list = sql().unionAll(sq1, sq2).list();
        assertFalse(list.isEmpty());
    }

    @Test
    public void EntityProjections() {
        List<Product> products = sql()
            .from(product)
            .select(ConstructorExpression.create(Product.class,
                    product.name, product.description, product.price, product.amount)).list();
        assertEquals(30, products.size());
        for (Product p : products) {
            assertNotNull(p.getName());
            assertNotNull(p.getDescription());
            assertNotNull(p.getPrice());
            assertNotNull(p.getAmount());
        }
    }

    @BeforeClass
    public static void doPersist() {
        // Persistence of a Product and a Book.
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();
            for (int i = 0; i < 10; i++) {
                pm.makePersistent(new Product("C" + i, "F", 200.00, 2));
                pm.makePersistent(new Product("B" + i, "E", 400.00, 4));
                pm.makePersistent(new Product("A" + i, "D", 600.00, 6));
            }
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }
        System.out.println("");

    }

}
