import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public class Main {

	private static long TIMEOUT = 15000;
	private static BigInteger ZERO = new BigInteger("0");
	private static BigInteger ONE = new BigInteger("1");
	private static BigInteger TWO = new BigInteger("2");
	private static BigInteger THREE = new BigInteger("3");
	
	
	public static BigInteger pollardRho(BigInteger value) {
		return pollardRho(value, 0);
	}
	
	public static BigInteger pollardRho(BigInteger value, int i) {
		if(value.mod(TWO).equals(ZERO))
			return TWO;
		
		if(value.mod(THREE).equals(ZERO))
			return THREE;
		
		BigInteger c =  new BigInteger(Integer.toString(i));
		BigInteger x = new BigInteger("2").add(c);
		BigInteger y = new BigInteger("2").add(c);
		BigInteger d = new BigInteger("1");
		c = new BigInteger("1");
		
		for(;;) {
			x = x.multiply(x).add(c).mod(value);
			y = y.multiply(y).add(c).mod(value);
			y = y.multiply(y).add(c).mod(value);
			
			d = x.subtract(y).abs();
			d = d.gcd(value);
			
			if(d.equals(value)) {
				// Try again
				return null;
			} else if(d.compareTo(ONE) == 1) {
				return d;
			} else if(timeout_when <= System.currentTimeMillis()) {
				return null;
			}
		}
	}
	
	public static void factor(BigInteger value) {
		BigInteger quo = value;
		
		while(!quo.isProbablePrime(20)) {
			BigInteger factor = null;
			for(int i = 0; ; i++) {
				factor = pollardRho(quo, i);				
				if(factor == null && timeout_when > System.currentTimeMillis())
					continue;
				break;
			}			
			
			if(factor == null)
				return;
			
			factor(factor);
			quo = quo.divide(factor);			
		}
		factors.add(quo);
	}
	
	/**
	 * Finds root with exponent k of given value. 
	 * Returns null on failure.
	 * @param value
	 * @param k
	 * @return
	 */
	public static BigInteger getPowRoot(BigInteger value, int k) {
		BigDecimal converted_value = new BigDecimal(value.toString());
		//new BigDecimal(value.toString());
		BigDecimal x = new BigDecimal("2").pow(value.bitLength() / k);		
		BigDecimal fx;
		BigDecimal fprimx;
		BigDecimal xold;
		
		// Newton-Raphson
		// x - f(x) / f'(x)
		// for function: x2 - n = 0

		do {
			xold = x;
			fx = xold.pow(k).subtract(converted_value);				
			fprimx = xold.pow(k-1).multiply(BigDecimal.valueOf(k));
			x = xold.subtract(fx.divide(fprimx, RoundingMode.HALF_DOWN));
//			System.out.println(x + " " + xold + " " + x.subtract(xold) + " " + x.pow(k));
		} while(x.subtract(xold).abs().compareTo(BigDecimal.ONE) >= 0);
		
		if(x.pow(k).equals(converted_value))
			return x.toBigIntegerExact();
		return null;
	}
	
	private static long timeout_when = 0;
	private static Queue<BigInteger> factors = new LinkedList<BigInteger>(); 
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
//		System.out.println(getPowRoot(new BigInteger("63"), 3));
		
		ArrayList<BigInteger> values = new ArrayList<BigInteger>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			String line = reader.readLine();
			if(line == null || line.equals(""))
				break;
			
			values.add(new BigInteger(line));
		}
		
		long start_time = System.currentTimeMillis();
		long stop_time = start_time + TIMEOUT;
			
		int values_to_go = values.size();
		int iWin = 0;
		for(BigInteger value : values) {
			factors.clear();
			Queue<BigInteger> toFactor = new LinkedList<BigInteger>();
			long currtime = System.currentTimeMillis();
			timeout_when =  currtime + ((stop_time - currtime) / values_to_go);

			// Potenser. Sätt k = 6 till något vettigare senare.
			for(int k = 6; k > 1; k--) {
				BigInteger root = getPowRoot(value, k);
				if(root != null) {
					System.err.println(root);
					if(root.isProbablePrime(20))
						factors.add(root);
					else
						toFactor.add(root);
					value = value.divide(root);
					k = 2;
				}
			}
			toFactor.add(value);
			
			for(BigInteger toFac : toFactor)
				factor(toFac);
			
			if(timeout_when <= System.currentTimeMillis()) {
				System.out.println("fail");
			} else {
				for(BigInteger factor : factors)
					System.out.println(factor);
				iWin++;
			}
			System.out.println();
			values_to_go--;
			
		}
		
		System.err.println(iWin);
	}

}
