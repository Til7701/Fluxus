# Fluxus

Fluxus is an absolutely serious, low performance, interpreted programming language.

## Language Overview

This gives a very small overview. A full description can be found on [my website](https://til7701.de/projects/fluxus)
in a few days (hopefully).

### Primitives

- `Bool`: `true` and `false`
- `Char`: e.g. `'a'`
- `I#`: signed integer with `#` bits precision
- `U#`: unsigned integer with `#` bits precision
- `F#`: float with `#` bits precision where only 32 and 64 are supported (more may come in the future)
- `I8[I32]`: array instantiated with `[42I8, 10I8, 0I32]` as a literal or `[0I8; 10I32]` as an array with 10 elements
  containing the value `0I8` (`I8` can be replaced with any other type)

### Variables

Variables can be initialized using the keyword `let` and must start with a lower case letter.

```rust
let x = 5I32;
let b = true;
```

Literals can also be reassigned.

```rust
0I32 = 1I32;
System.println(0I32 as Str); // prints 1
```

Initializing a variable with a literal also couples that variable to the literal.

```rust
let a = 0U32;
a = 1I32;
System.println(0U32 as Str); // prints 1
```

This can be avoided by using the `new` keyword.
This copies the value to a new location in memory so subsequent changes to `a` do not modify the literal.

```rust
let a = new 0U32;
a = 1I32;
System.println(0U32 as Str); // prints 0
```

### Conditionals

There are not `if`-statements or loops. Both are very inferior and therefore replaced with `when` statements.
Whenever a variable in the condition changes, it is reevaluated and the body is executed if the condition is `true`.

```rust
when x < 10I32 {
    //...
}
```

To evaluate the condition without another reassignment, you can prefix the `when`-statement with `do`.
The following prints the numbers from 0 to 9.

```rust
let i = 0I32;
do when i < 10I32 {
    System.println(i as Str);
    i = i + 1I32;
}
```

## Build

To build Fluxus you need Java 27 or newer installed.
Then you can run:

```shell
./mvnw clean package
```

Then you can run the CLI using:

```shell
java -jar cli/target/cli-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### DEB

You can build and install a deb package by running:

```bash
./build-deb.sh
sudo apt install ./cli/target/fluxus_0.0.1_amd64.deb
```
