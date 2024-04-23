## Rhino 2.0 -> 2.1

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
