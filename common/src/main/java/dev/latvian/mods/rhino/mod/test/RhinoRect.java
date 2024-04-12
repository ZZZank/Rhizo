package dev.latvian.mods.rhino.mod.test;

import dev.latvian.mods.rhino.util.remapper.HideFromJS;
import dev.latvian.mods.rhino.util.remapper.RemapForJS;

public class RhinoRect {
	public final int width;
	public final transient int height;

	public static String rectId = "3adwwad";

	@HideFromJS
	public RhinoRect(int w, int h) {
		width = w;
		height = h;
	}

	public RhinoRect(int w, int h, int d) {
		this(w, h);
		System.out.println("Depth: " + d);
	}

	@RemapForJS("createRect")
	public static RhinoRect construct(int w, int h) {
		return new RhinoRect(w, h);
	}
}