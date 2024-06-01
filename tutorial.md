# Welcome to InterWest

Welcome to InterWest! We're a language focused on bringing static safety and verifiability with minimal overhead.

Here's a program in InterWest. It may look a little strange now, but by the time you're finished with this tutorial, you'll understand what it does, and how it makes InterWest so unique.

```
let zero = 0

let isNonZero = regular {Int i -> i != zero}

let NonZero = type isNonZero

let divide = {Int n, Int d -> n / d}

print(divide(10, 5))

print(divide(10, 0))
```

## Starting with `let`

In InterWest, `let` is how you introduce a new constant. Any time you want to give a name to a value, you use `let`, no matter what kind of value it is. The syntax of a let statement looks like this:

```
let <name> = <expression>
```

InterWest doesn't have mutability---once you've declared a constant, it's set on that value for good.

So when we write...

```
let zero = 0
```

...we're simply declaring a new constant called `zero`, with a value equal to 0.

## Writing Functions

InterWest has two ways of writing functions, implicit lambdas and explicit lambdas. They both look like this:

```
<modifiers> {<type> <argument name...> -> <expressions>}
```

If the lambda has only one expression after the arrow, InterWest treats it as implicit. Otherwise, it's explicit.

Explicit lambdas are so-called because they explicitly declare their return type. The first expression that you give will be evaluated as the function's return type. The remaining expressions will be evaluated as the function's body. An implicit lambda only has one expression, and its return type is derived implicitly from the type of the expression.

So when we write:
```
let isNonZero = {Int i -> i != zero}
```
We're creating a new implicit lambda which takes an argument called `i` and evaluates whether it's equal to 0. 

Lambdas can also have modifiers. The only modifier right now is called `regular`, and it enforces that your function can be described with a regular language. You can learn more about [regular languages](https://en.wikipedia.org/wiki/Regular_language), but if you're not familiar with them, the modifier essentially enforces that your function can be described with a relatively simple set of computations.

InterWest uses a small internal engine to verify that your functions are regular. Using regular functions lets InterWest figure out properties about them that would otherwise be impossible; regular functions can be used to do special things that other functions can't.

But when we write:
```
let isNonZero = regular {Int i -> i != zero}
```
We're not changing the value of `isNonZero`, we're just asking InterWest to confirm that the function is regular. 

# Writing Your Own Types

Now, we get to the most exciting part of InterWest: writing your own types. Plenty of languages give you some control over the type system, but InterWest gives you a more powerful set of tools than, say, object-orientation or sum types. InterWest uses what's called a "refinement type" system.

Refinement types subsets of other types, offset by a function that you declare. For instance, we might make a new type `EvenInteger`, that is a subtype of `Int`, but only contains even numbers. The way we make refinement types in InterWest is with the `type` keyword:

```
type <membership function>
```

Simply apply this keyword to a function and InterWest will use it to create a new type. The function must take a single value, and return a boolean value representing whether the value is in the type or not. However, this membership function has to be declared as regular, otherwise it may be too complex for InterWest to use in the type system.

So when we write:

```
let NonZero = type isNonZero
```

Using the same basic principles we learned earlier, we can make a new function wrapping InterWest's own built-in division operator:

```
let divide = {Int n, NonZero d -> n / d}
```

Note that this takes a nonzero denominator--no more division by zero errors! InterWest has a static typing system, and thanks to the superpowers of refinement types powered by regular functions, the language can actually verify the arguments of the function are valid *before your code even runs*.

So if we were to test it with the following code:

```
print(divide(10, 5))

print(divide(10, 0))
```

We would get a validation error on `print(divide(10, 0))`.

This is what we mean when we say that InterWest gives you new tools for safety--you can now completely eliminate whole classes of runtime errors, simply by letting the type system do your work for you. There are still many improvements to be made to the language's regularity engine and other features, but this core functionality lets you write code with peace of mind. We hope that you'll find your own new ways to use InterWest's unique features!
