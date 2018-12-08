package com.tinyparty.game.model.parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BulletAmountConfiguration {
	public static final HashMap<Integer, List<Float>> configuration = new HashMap<Integer, List<Float>>() {{
		put(1, new ArrayList<Float>() {{
			add(0f);
		}});
		put(3, new ArrayList<Float>() {{
			add(0f);
			add(-12.5f);
			add(12.5f);
		}});
		put(7, new ArrayList<Float>() {{
			add(0f);
			add(-10f);
			add(-20f);
			add(-30f);
			add(10f);
			add(20f);
			add(30f);
		}});
	}};
}
