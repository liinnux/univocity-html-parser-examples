package com.univocity.parsers.examples;

import com.univocity.parsers.annotations.*;

import java.util.*;

/**
 * Created by anthony on 24/06/16.
 */
public class Items {


	@Parsed(field = "name")
	@LowerCase
	public String name;
	@Parsed
	public String desc;
	@Parsed(index = 3)
	public String price;

	@Override
	public String toString() {
		return "Item{" +
				"name='" + name + '\'' +
				", desc='" + desc + '\'' +
				", price=" + price +
				'}';
	}
}
