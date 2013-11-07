package main

import "math/big"

func pollardRho(task *Task, toFactor *big.Int, constant int64) (*big.Int, bool) {	
	
	//~ x := new(big.Int).Set(TWO)
	c := big.NewInt(constant)
	y := big.NewInt(2)
	y.Add(y, c)
	x := c.Add(c, TWO)
	//~ 
	//~ dprint(toFactor)
	//~ dprint(ZERO)
	
	//~ sub1 := new(big.Int).Sub(toFactor, ONE)
	//~ x := new(big.Int).Rand(rng, sub1)
	//~ y := new(big.Int).Rand(rng, sub1)
	
	d := big.NewInt(1)
	r := new(big.Int)
	rand_const := ONE //r.Rand(rng, toFactor)
	
	if r.Mod(toFactor, TWO).Cmp(ZERO) == 0 {
		return TWO, false
	}
	
	i := 0
	for(d.Cmp(ONE) == 0) { 
		/*
		*/		
		//~ x = x.Set(y)
		// x = f(x)
		x = x.Mul(x,x).Add(x, rand_const).Mod(x, toFactor)
		
		// y = f(f(y))
		y = y.Mul(y,y).Add(y, rand_const).Mod(y, toFactor)
		y = y.Mul(y,y).Add(y, rand_const).Mod(y, toFactor)	
			
		r.Sub(x,y).Abs(r)		
		d = r.GCD(nil, nil, r, toFactor)
		i++
		if d.Cmp(toFactor) == 0 {
			//~ dprint("failed. retrying")
			return d, true
		} else if task.ShouldStop() {
			return d, true
		}
	}
		
	return d, false
}

func pollardFactoring(task *Task) ([]*big.Int) {	
	return _pollardFactoring(task, task.toFactor)
}

func _pollardFactoring(task *Task, toFactor *big.Int) ([]*big.Int) {
	buffer := make([]*big.Int, 0)
	quo := new(big.Int)
	quo.Set(toFactor)
	
	//~ f := get_f(task.toFactor)
	
	for !quo.ProbablyPrime(prime_precision) {//quo.Cmp(big.NewInt(1)) > 0) {			
		if(task.ShouldStop()) {
			return buffer
		}
		
		var factor *big.Int
		var error bool
		for i := 0; ; i++ {
			factor, error = pollardRho(task, quo, int64(i))				
			
			if(task.ShouldStop()) {
				return buffer
			}			
			if(!error) {
				break
			}
		}

		if(!factor.ProbablyPrime(prime_precision)) {
			sub := _pollardFactoring(task, factor)
			buffer = append(buffer, sub...)		
		} else {	
			buffer = append(buffer, factor)
		}     
		   
        quo.Quo(quo, factor)         
	}
	return append(buffer, quo)
}
