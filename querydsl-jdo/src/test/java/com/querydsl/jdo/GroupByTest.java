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

import static org.junit.Assert.assertEquals;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.junit.BeforeClass;
import org.junit.Test;

import com.querydsl.jdo.test.domain.Product;
import com.querydsl.jdo.test.domain.QProduct;

public class GroupByTest extends AbstractJDOTest {

    private QProduct product = QProduct.product;

    @Test
    public void Distinct() {
        assertEquals(3, query().from(product).distinct().select(product.description).list().size());
        assertEquals(3, query().from(product).distinct().select(product.price).list().size());
    }

    @Test
    public void GroupBy() {
        assertEquals(3, query().from(product).groupBy(product.description).select(product.description).list().size());
        assertEquals(3, query().from(product).groupBy(product.price).select(product.price).list().size());
    }
    
    @Test
    public void Having() {
        assertEquals(3, query().from(product)
                .groupBy(product.description).having(product.description.ne("XXX"))
                .select(product.description).list().size());
        assertEquals(3, query().from(product)
                .groupBy(product.price).having(product.price.gt(0))
                .select(product.price).list().size());
    }

    @BeforeClass
    public static void doPersist() {
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
