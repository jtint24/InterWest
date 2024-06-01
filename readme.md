<center><h1> InterWest </h1></center>

---------

[![iw-logo.png](https://i.postimg.cc/Z52nYfpy/iw-logo.png)](https://postimg.cc/K17xJD9Z)

---------

InterWest is an experimental language built around powerful, statically checked refinement types without the need for a solver.
InterWest's type system lets you write expressive, safe, understandable code with just a few constructs.

```InterWest
let NonZero = type regular {Int i -> i != 0}
let divide = {Int i, NonZero nz -> i / nz}
```

## Install

Install the latest build from the releases tab.

Note that InterWest is currently in pre-0.1.0, so we can't offer much in the way of stability. If you do find a bug or error, please report it to the Issues tab.

The project can also be built from source. Clone this repository, then navigate to `src` and run the following command:
```bash
javac -classpath . Main/Main.java
```

## Learn

If you're interested in learning more about InterWest, check out the tutorial [here](tutorial.md)!

