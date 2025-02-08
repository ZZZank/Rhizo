package dev.latvian.mods.rhino.test.impl;

import com.github.bsideup.jabel.Desugar;

import java.util.HashMap;
import java.util.Map;

@Desugar
public record TestMaterial(String name) {
	public static final Map<String, TestMaterial> MATERIALS = new HashMap<>();

	public static synchronized TestMaterial get(Object o) {
		return MATERIALS.computeIfAbsent(String.valueOf(o), TestMaterial::new);
	}

	@Override
	public String toString() {
		return "M[" + name + "]";
	}
}
