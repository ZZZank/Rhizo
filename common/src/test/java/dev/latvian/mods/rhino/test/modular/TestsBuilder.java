package dev.latvian.mods.rhino.test.modular;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author ZZZank
 */
public class TestsBuilder {

    /**
     * example input: [[1, 2, 3], [4, 5, 6]]
     * example output: [[1, 4], [1, 5], [1, 6], [2, 4], [2, 5], [2, 6], [3, 4], [3, 5], [3, 6]]
     */
    public static <T> Stream<Collection<T>> descartes(Iterable<? extends Collection<T>> layers) {
        if (!layers.iterator().hasNext()) {
            return Stream.empty();
        }

        var result = Stream.<Collection<T>>of(List.of());
        for (var layer : layers) {
            result = result.flatMap(last -> layer.stream()
                .map(t -> copyAppend(last, t)));
        }
        return result;
    }

    private static <T> List<T> copyAppend(Collection<T> list, T element) {
        var merged = new ArrayList<T>(list.size() + 1);
        merged.addAll(list);
        merged.add(element);
        return merged;
    }
}
