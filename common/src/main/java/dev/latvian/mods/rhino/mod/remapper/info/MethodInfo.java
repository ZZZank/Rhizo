package dev.latvian.mods.rhino.mod.remapper.info;

import com.github.bsideup.jabel.Desugar;

/**
 * @author ZZZank
 */
@Desugar
public record MethodInfo(String original, String paramDescriptor, String remapped) {
}
