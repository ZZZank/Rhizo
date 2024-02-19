package dev.latvian.mods.rhino.test;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.ScriptableObject;
import dev.latvian.mods.rhino.SharedContextData;
import dev.latvian.mods.rhino.classdata.ClassData;
import dev.latvian.mods.rhino.classdata.ClassDataCache;
import dev.latvian.mods.rhino.classdata.ClassMember;
import net.minecraft.world.entity.player.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@SuppressWarnings("unused")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClassDataTests {
	@Test
	@DisplayName("Class Data")
	public void classData() {
		Context cx = Context.enterWithNewFactory();
		ScriptableObject scope = cx.initStandardObjects();
		SharedContextData contextData = SharedContextData.get(scope);
		ClassDataCache cache = contextData.getClassDataCache();
		ClassData data = cache.of(Player.class);
		ClassMember member = data.getMember("x");
		System.out.println(member);
		Context.exit();
	}
}
