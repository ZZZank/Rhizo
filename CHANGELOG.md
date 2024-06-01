## Rhizo 2.2 -> 3.0

Enum++ & Full Remapper

-   Enum Type Wrapper
    -   now you can access enum values by their names or indexes.
    -   E.g. `Direction.EAST` can be accessed via `"east"`
    -   Note: name should be in lower case
-   Full Remapper
    -   Remapper is now rewritten, providing more complete and accurate remapping for methods and fields.
    -   Because of this, remapping on Forge no longer fully relies on an unusual uniqueness, and remapping for Fabric is now actually working. 
    -   Note: the remapping file now uses a new storage format, that will make Rhizo jar bigger.
    -   It has been 4 months since the first attempt of bringing remapper to 1.16, and it turns out the first 3 months are completely useless, because writing a new Remapper solution from the ground up is actually faster.

## Rhizo 2.1 -> 2.2

Early Remapper

-   Remapper is now enabled before KubeJS startup script is loaded, so that names used in startup scripts can also be remapped.

## Rhizo 2.0 -> 2.1

Compiler Mode

-   Compiler Mode
    -   you can enable Compiler Mode via `rhino.local.properties` file in the root of your game folder.
    -   Under Compiler Mode, Rhizo will compile JS codes into native codes, providing performance improvement.
    -   You can also change optimization level in `rhino.local.properties`. Higher optimization level can usually provide better performance, at the cost of compile time(which is usually already consumed during startup/reload)
    -   NOTE: enabling Compiler Mode will make error reporting less useful, since the line number will be wrong, and file name will always be "SourceFile"
-   you can also toggle mapping generation in `rhino.local.properties`, but the output file is useless currently
    -   generated mapping file is designed for a more complex remapper implementation, and is scheduled to be available in the next release
-   mapping file used by our temporary remapper solution is now driven by `.csv` file, for readablility.

## Rhino 1605.1.9 -> Rhizo 1605.2.0

First release

-   removed active but unused codes, to improve performance
-   fixed JSON generation for Iterable
-   REMAPPER!
    -   methods/fields can now be called using readable name, instead of SRG name.
    -   this is currently done via a simple String to String map, and will be replaced by a more standard and robust solution in the future
