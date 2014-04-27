Zemscript
=========

A simple scripting language for learning basic techniques in writing an interpreter.

This project implements a simple script interpreter in Java. Its purpose is to provide a simple code base that is easy to understand and be used for learning basic techniques in writing an interpreter.

The project contains the following:

* Hand written Lexer
* Parser that process tokens from the Lexer to check the syntax and generate an Abstract Syntax Tree
* An Interpreter that evaluates the abstract syntax tree by visiting the nodes. It also performs some semantic checking.

Here is an example script it successfully runs:

```javascript
/*
 * @author Cameron Zemek <grom@zeminvaders.net>
 */

// This script is an example of a simple language.
// There are only three basic data types: numbers, strings, boolean
a_number = 3 ^ 2;
a_string = "hello";
a_bool = true;

// Operators
concat_string = "hello" ~ " " ~ "world.";
fav_string = 3 ~ " is my favourite number";
result_number = 1 + 2 - 1 * 5 / 3.2 + 2^2;
isEmail = true;
someOtherBoolean = false;
condition = isEmail && (len(a_string) > 0) || someOtherBoolean;

// if control flow
if (a_number < 3) {
    println("number is less then 3");
} else if (a_number == 3) {
    println("number is 3");
} else {
    println("number is greater then 3");
}

// while loop
counter = 1;
length = 9;
while (counter <= length) {
    counter = counter + 1;
}

// Arrays
my_list = ["apple", "banana", "orange"];
my_list[1] = "pear";
println(my_list[1]); // pear

// foreach loop
foreach (my_list as item) {
    println(item);
}

// Dictonary
dict = {"fruit" : "apple", "veggie" : "pumpkin"};
dict["fruit"] = "orange";
println(dict["fruit"]); // orange

foreach (dict as key : value) {
    println(key ~ ": " ~ value);
}

// Defining functions
sum = function(list_of_numbers) {
    total = 0;
    foreach (list_of_numbers as number) {
        total = total + number;
    }
};

range = function(start_number, end_number, step = 1) {
    result = [];
    number = start_number;
    while (number <= end_number) {
        array_push(result, number);
        number = number + step;
    }
    return result;
};

// Calling functions
println(sum(range(1, 9)));
```
