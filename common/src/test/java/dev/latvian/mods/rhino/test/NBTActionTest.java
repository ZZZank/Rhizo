package dev.latvian.mods.rhino.test;

import dev.latvian.mods.rhino.mod.util.NBTUtils;
import dev.latvian.mods.rhino.test.impl.base.RhinoTest;
import dev.latvian.mods.rhino.util.wrap.LegacyDynamicWrapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * @author ZZZank
 */
public class NBTActionTest {
    private static final RhinoTest TEST = new RhinoTest("nbt")
        .withBinding("CompoundTag", t -> CompoundTag.class);
    private static final RhinoTest TEST_WITH_WRAPPER = new RhinoTest("nbt")
        .withBinding("CompoundTag", t -> CompoundTag.class)
        .withTypeWrapper(
            Tag.class, new LegacyDynamicWrapper<>(
            (from) -> from instanceof Map<?,?>,
            NBTUtils::toTag
        ));

    @Test
    void oldBehaviour() {
        TEST.test(
            "oldBehaviour", """
                const t = new CompoundTag()
                t.put("wow", {example: 123.4})
                console.log(t)""", """
                {wow:Proxy[{example: 123.4}]}
                """
        );
    }

    @Test
    void oldBehaviourShouldBreak() {
        TEST.test(
            "oldBehaviour", """
                const t = new CompoundTag()
                t.put("wow", {example: 123})
                const got = t.get("wow")
                console.log(got.copy())""", """
                null
                """
        );
    }

    @Test
    void withWrapper() {
        TEST_WITH_WRAPPER.test("withWrapper", """
            const t = new CompoundTag()
            t.put("wow", {example: 123.4})
            const got = t.get("wow")
            console.log(got.copy())""", """
            {example:123.4d}
            """);
    }
}
