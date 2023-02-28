package com.clique.retire.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NumberUtil {

	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	 
	    BigDecimal bd = new BigDecimal(Double.toString(value));
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	public static double subtract(Double value, double valueSub, int places) {
		return round(value.doubleValue() - valueSub, places);
	}
	
	public static double add(Double value, double valueAdd, int places) {
		return round(value.doubleValue() + valueAdd, places);
	}

}
