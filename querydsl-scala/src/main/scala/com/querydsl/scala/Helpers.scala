/*
 * Copyright 2011, Mysema Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.querydsl.scala

import com.querydsl.core.Projectable
import com.querydsl.core.ProjectableQuery

import scala.collection.JavaConversions._
import com.querydsl._
import com.querydsl.core.types._
import com.querydsl.scala.GroupBy._

import Projections._

/**
 * Helpers provides implicit conversions for Projectable and SimpleProjectable to be
 * more Scala compatible
 */
object Helpers extends Helpers

trait Helpers extends Projections with GroupBy {
  
  implicit def toRichSimpleProjectable[T](p: Projectable[T]) = new RichSimpleProjectable(p)
  
  implicit def toRichProjectable(p: ProjectableQuery[_,_]) = new RichProjectable(p)
}

/**
 * RichSimpleProjectable provides Scala extensions for SimpleProjectable
 *
 * @author tiwe
 */
class RichSimpleProjectable[T](private val p: Projectable[T]) {
  
  def select: List[T] = p.list.toList
  
  def first: Option[T] = Option(p.firstResult())
  
  def unique: Option[T] = Option(p.uniqueResult())
  
  override def toString: String = p.toString
  
}

/**
 * RichProjectable provides Scala extensions for Projectable
 * 
 * @author tiwe
 *
 */
class RichProjectable(private val p: ProjectableQuery[_,_]) {
  
  def select[A](e: Ex[A]): List[A] = p.select(e).list().toList
  
  def select[A,B](e1: Ex[A], e2: Ex[B]): List[(A,B)] = p.select((e1,e2)).list().toList
  
  def select[A,B,C](e1: Ex[A], e2: Ex[B], e3: Ex[C]): List[(A,B,C)] = p.select((e1,e2,e3)).list().toList
  
  def select[A,B,C,D](e1: Ex[A], e2: Ex[B], e3: Ex[C], e4: Ex[D]): List[(A,B,C,D)] = {
    p.select((e1,e2,e3,e4)).list().toList
  }
  
  def select[A,B,C,D,E](e1: Ex[A], e2: Ex[B], e3: Ex[C], e4: Ex[D], e5: Ex[E]): List[(A,B,C,D,E)] = {
    p.select((e1,e2,e3,e4,e5)).list().toList
  }
  
  def select[A,B,C,D,E,F](e1: Ex[A], e2: Ex[B], e3: Ex[C], e4: Ex[D], e5: Ex[E], e6: Ex[F]): List[(A,B,C,D,E,F)] = {
    p.select((e1,e2,e3,e4,e5,e6)).list()toList
  }
  
  def select[A,B,C,D,E,F,G](e1: Ex[A], e2: Ex[B], e3: Ex[C], e4: Ex[D], e5: Ex[E], e6: Ex[F], 
      e7: Ex[G]): List[(A,B,C,D,E,F,G)] = {
    p.select((e1,e2,e3,e4,e5,e6,e7)).list().toList
  }
  
  def select[A,B,C,D,E,F,G,H](e1: Ex[A], e2: Ex[B], e3: Ex[C], e4: Ex[D], e5: Ex[E], e6: Ex[F], 
      e7: Ex[G], e8: Ex[H]): List[(A,B,C,D,E,F,G,H)] = {
    p.select((e1,e2,e3,e4,e5,e6,e7,e8)).list().toList
  }
  
  def select[A,B,C,D,E,F,G,H,I](e1: Ex[A], e2: Ex[B], e3: Ex[C], e4: Ex[D], e5: Ex[E], e6: Ex[F], 
      e7: Ex[G], e8: Ex[H], e9: Ex[I]): List[(A,B,C,D,E,F,G,H,I)] = {
    p.select((e1,e2,e3,e4,e5,e6,e7,e8,e9)).list().toList
  }
  
  def select[A,B,C,D,E,F,G,H,I,J](e1: Ex[A], e2: Ex[B], e3: Ex[C], e4: Ex[D], e5: Ex[E], e6: Ex[F], 
      e7: Ex[G], e8: Ex[H], e9: Ex[I], e10: Ex[J]): List[(A,B,C,D,E,F,G,H,I,J)] = {
    p.select((e1,e2,e3,e4,e5,e6,e7,e8,e9,e10)).list().toList
  }
  
  def select[A,B,C,D,E,F,G,H,I,J,K](e1: Ex[A], e2: Ex[B], e3: Ex[C], e4: Ex[D], e5: Ex[E], e6: Ex[F], 
      e7: Ex[G], e8: Ex[H], e9: Ex[I], e10: Ex[J], e11: Ex[K]): List[(A,B,C,D,E,F,G,H,I,J,K)] = {
    p.select((e1,e2,e3,e4,e5,e6,e7,e8,e9,e10,e11)).list().toList
  }
  
  def select[A,B,C,D,E,F,G,H,I,J,K,L](e1: Ex[A], e2: Ex[B], e3: Ex[C], e4: Ex[D], e5: Ex[E], 
      e6: Ex[F], e7: Ex[G], e8: Ex[H], e9: Ex[I], e10: Ex[J], e11: Ex[K], 
      e12: Ex[L]): List[(A,B,C,D,E,F,G,H,I,J,K,L)] = {
    p.select((e1,e2,e3,e4,e5,e6,e7,e8,e9,e10,e11,e12)).list().toList
  }
  
  def select[A,B,C,D,E,F,G,H,I,J,K,L,M](e1: Ex[A], e2: Ex[B], e3: Ex[C], e4: Ex[D], e5: Ex[E], 
      e6: Ex[F], e7: Ex[G], e8: Ex[H], e9: Ex[I], e10: Ex[J], e11: Ex[K], e12: Ex[L],
      e13: Ex[M]): List[(A,B,C,D,E,F,G,H,I,J,K,L,M)] = {
    p.select((e1,e2,e3,e4,e5,e6,e7,e8,e9,e10,e11,e12,e13)).list().toList
  }
  
  def select[A,B,C,D,E,F,G,H,I,J,K,L,M,N](e1: Ex[A], e2: Ex[B], e3: Ex[C], e4: Ex[D], 
      e5: Ex[E], e6: Ex[F], e7: Ex[G], e8: Ex[H], e9: Ex[I], e10: Ex[J], e11: Ex[K], e12: Ex[L], 
      e13: Ex[M], e14: Ex[N]): List[(A,B,C,D,E,F,G,H,I,J,K,L,M,N)] = {
    p.select((e1,e2,e3,e4,e5,e6,e7,e8,e9,e10,e11,e12,e13,e14)).list().toList
  }
  
  def select[A,B,C,D,E,F,G,H,I,J,K,L,M,N,O](e1: Ex[A], e2: Ex[B], e3: Ex[C], e4: Ex[D], 
      e5: Ex[E], e6: Ex[F], e7: Ex[G], e8: Ex[H], e9: Ex[I], e10: Ex[J], e11: Ex[K], e12: Ex[L], 
      e13: Ex[M], e14: Ex[N], e15: Ex[O]): List[(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O)] = {
    p.select((e1,e2,e3,e4,e5,e6,e7,e8,e9,e10,e11,e12,e13,e14,e15)).list().toList
  }
  
  def select[A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P](e1: Ex[A], e2: Ex[B], e3: Ex[C], e4: Ex[D], 
      e5: Ex[E], e6: Ex[F], e7: Ex[G], e8: Ex[H], e9: Ex[I], e10: Ex[J], e11: Ex[K], e12: Ex[L], 
      e13: Ex[M], e14: Ex[N], e15: Ex[O], e16: Ex[P]): List[(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P)] = {
    p.select((e1,e2,e3,e4,e5,e6,e7,e8,e9,e10,e11,e12,e13,e14,e15,e16)).list().toList
  }
  
  def select[A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q](e1: Ex[A], e2: Ex[B], e3: Ex[C], e4: Ex[D], 
      e5: Ex[E], e6: Ex[F], e7: Ex[G], e8: Ex[H], e9: Ex[I], e10: Ex[J], e11: Ex[K], e12: Ex[L], 
      e13: Ex[M], e14: Ex[N], e15: Ex[O], e16: Ex[P], 
      e17: Ex[Q]): List[(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q)] = {
    p.select((e1,e2,e3,e4,e5,e6,e7,e8,e9,e10,e11,e12,e13,e14,e15,e16,e17)).list().toList
  }
  
  def select[A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R](e1: Ex[A], e2: Ex[B], e3: Ex[C], e4: Ex[D], 
      e5: Ex[E], e6: Ex[F], e7: Ex[G], e8: Ex[H], e9: Ex[I], e10: Ex[J], e11: Ex[K], e12: Ex[L], 
      e13: Ex[M], e14: Ex[N], e15: Ex[O], e16: Ex[P], e17: Ex[Q], 
      e18: Ex[R]): List[(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R)] = {
    p.select((e1,e2,e3,e4,e5,e6,e7,e8,e9,e10,e11,e12,e13,e14,e15,e16,e17,e18)).list().toList
  }
  
  def select[A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S](e1: Ex[A], e2: Ex[B], e3: Ex[C], e4: Ex[D], 
      e5: Ex[E], e6: Ex[F], e7: Ex[G], e8: Ex[H], e9: Ex[I], e10: Ex[J], e11: Ex[K], e12: Ex[L], 
      e13: Ex[M], e14: Ex[N], e15: Ex[O], e16: Ex[P], e17: Ex[Q], e18: Ex[R], 
      e19: Ex[S]): List[(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S)] = {
    p.select((e1,e2,e3,e4,e5,e6,e7,e8,e9,e10,e11,e12,e13,e14,e15,e16,e17,e18,e19)).list().toList
  }
  
  def select[A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T](e1: Ex[A], e2: Ex[B], e3: Ex[C], e4: Ex[D], 
      e5: Ex[E], e6: Ex[F], e7: Ex[G], e8: Ex[H], e9: Ex[I], e10: Ex[J], e11: Ex[K], e12: Ex[L], 
      e13: Ex[M], e14: Ex[N], e15: Ex[O], e16: Ex[P], e17: Ex[Q], e18: Ex[R], e19: Ex[S], 
      e20: Ex[T]): List[(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T)] = {
    p.select((e1,e2,e3,e4,e5,e6,e7,e8,e9,e10,e11,e12,e13,e14,e15,e16,e17,e18,e19,e20)).list().toList
  }
  
  // TODO : generalize this
  def selectGrouped[K,T,V](key: Ex[K], parent: Ex[T], child: Ex[V]): List[(T,Set[V])] = {
    p.transform(groupBy(key).as((parent, set(child)))).values.toList    
  }  
  
  def first[T](expr: Ex[T]): Option[T] = Option(p.select(expr).firstResult())
  
  // TODO : single variants for multiple arguments
  
  def unique[T](expr: Ex[T]): Option[T] = Option(p.select(expr).uniqueResult)
  
  // TODO : unique variants for multiple arguments
  
  override def toString: String = p.toString
  
}


