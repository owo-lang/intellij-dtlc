--
-- Created by Dependently-Typed Lambda Calculus on 2019-05-15
-- records
-- Author: ice10
--

{-# OPTIONS --without-K --safe #-}

record List (A : Set) : Set where
  coinductive
  field
    head : A
    tail : List A
open List

-- | Bisimulation as equality
record _==_ (x : List A) (y : List A) : Set where
  coinductive
  field
    refl-head : head x ≡ head y
    refl-tail : tail x == tail y
open _==_
