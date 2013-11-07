import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public class Main {

	private static long TIMEOUT = 14000;
	private static BigInteger ZERO = new BigInteger("0");
	private static BigInteger ONE = new BigInteger("1");
	private static BigInteger TWO = new BigInteger("2");
	
	
	public static BigInteger pollardRho(BigInteger value) {
		return pollardRho(value, 0);
	}
	
	public static BigInteger pollardRho(BigInteger value, int i) {
		if(value.mod(TWO).equals(ZERO))
			return TWO;
		
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
//			System.out.println("derp1");
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
	
	private static long timeout_when = 0;
	private static Queue<BigInteger> factors = new LinkedList<BigInteger>(); 
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		ArrayList<BigInteger> values = new ArrayList<BigInteger>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			String line = reader.readLine();
			if(line == null || line.equals(""))
				break;
			
			values.add(new BigInteger(line));
		}
		
//		long start_time = System.currentTimeMillis();
//		long stop_time = start_time + TIMEOUT;
		
//		int values_to_go = values.size();
		int iWin = 0;
		for(BigInteger value : values) {
			factors.clear();
			timeout_when =  System.currentTimeMillis() + (TIMEOUT / values.size());
//			System.out.println(timeout_when);
			factor(value);
			
			if(timeout_when <= System.currentTimeMillis()) {
				System.out.println("fail");
			} else {
				for(BigInteger factor : factors)
					System.out.println(factor);
				iWin++;
			}
			System.out.println();
			
		}
		
		//System.err.println(iWin);
		
		
		

	}

}
