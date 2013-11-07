package main

import "math/big"
import "math/rand"
//~ import "time"

var (
	ZERO = big.NewInt(0)
	ONE = big.NewInt(1)
	TWO = big.NewInt(2)	
	
	rng = rand.New(rand.NewSource(1337))
	prime_precision = 20
)
