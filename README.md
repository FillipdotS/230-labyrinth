# 230-labyrinth

Use Java 8 to work with this.

## Style Guide
Style guide taken from lecture 9 in 230.

#### Tabs or Spaces?
Use tabs, specifically tab characters.

#### General Styling
Rules marked with _(Soft Rule)_ are soft rules and can be **sometimes** broken.
<hr>

* **Classes** (and **Interfaces**) must be written in **camel case style** and start with an upper-case letter. e.g., SalesOrder.

* **Constants** must be written in block capital letters with underscores between words e.g., VAT_RATE.

* **All other** identifiers, including (but not limited to) fields, local variables, methods and parameters must be written in camel case style and start with a lower-case letter. e.g., totalCost.

* _(Soft Rule)_<br>
No line of code shall exceed **80 characters** in length.

* _(Soft Rule)_<br>
No method should exceed **75 lines**. The method should be visible on a single screen/page.

* _(Soft Rule)_<br>
Methods shall use **no more than 5 levels** of indentation.

* _(Soft Rule)_<br>
Methods should not require more than **5 parameters**.

* Never use ```continue``` statements.<br>
Never use ```break``` other than in a ```switch``` statement.

#### No magic-numbers
Do not use numbers in your code, but rather constants.<br>
**Exceptions**:  0 or 1 (sometimes 2)

#### Javadoc
**All classes** require a class level Javadoc comment that describes the overall purpose of the class. The ```@author``` tag must be used to specify the author.

**All methods** must have a Javadoc comment.  The parameters must be correctly described using ```@param``` tags.  You must also document the return values using the ```@return``` tag.

#### Placement of variables
Always declare variables with the minimum scope to get the job done. i.e. if a variable is used in only one method, maybe it should be declared there?

**and** 

_(Soft Rule)_<br>
Declare variables as close as possible to where they are used.

#### Classes
* No blank space between a method name and the parenthesis “(“ starting its parameter list.
* Open brace “{“ appears at the end of the same line as the declaration statement with a space before it.
* A single blank space before the opening brace.
* Closing brace “}” starts a line by itself indented to match its corresponding opening statement.
* Methods are separated by a single blank line.

```java
public class Sample extends AnotherClass {
    private int ivar1;
    private int ivar2;
    
    Sample(int i, int j) {
        ivar1 = i;
        ivar2 = j;
    }

    public int aMethod() {
        return 42;
    }
}
```
#### General Code
##### if-else statements
* Blank space after keywords, e.g., ``if``, ``else`` and ``else if``.
* No space after opening parenthesis and no space before closing.
```java
if (condition) {
    statement;
} else if (condition) {
    statement;
} else if (condition) {
    statement;
} else {
    statement;
}
```

##### Loops
* For loops: Space after the semi-colons.
* Space after keywords, e.g., while and do.
```java
for (initialisation; condition; update) {
    statements;
}
```
```java
while (condition) {
    statements;
}
```
```java
do {
    statements;
} while (condition);
```

##### Optional braces
All ``if``, ``while`` and ``for`` statements must always use braces even if they control just one statement. Basically don't do this:
```java
if (condition)
    statement;
otherThings;
```

##### Arrays and Casts
All array access should be immediately followed by a left square bracket.
```java
x = myArray[0];
```

All casts should be written with a single space.
```java
x = (int) foo(42);
```

#### One declaration per line
Only one declaration per line is allowed.<br>
Wrong:
```java
int height, weight;
```

Correct:
```java
int height // in cm;
int weight // in grams;
```

#### Encapsulation
All class variables (instance variables and static variables) must be **private**.<br>
**Exception**:  **Constants**.  Constants can be publicly available.

All class attributes are accessed using get/set methods when accessed externally.

#### Order within a java source files
Java source files must have the following ordering:
1. Import statements
2. Javadoc for the class
3. Class declaration

An example:
    
```java
import java.util.ArrayList;
import java.util.Scanner;

/**
* A menu that is displayed on a console
* @author Liam O'Reilly
* @version 1.0
*/
public class Menu {
    ...
```

Each class must be stored in its own file.<br>
Exception:  inner classes (e.g., used for GUI event handlers) may be declared within the class that is using it.

#### Order within a class
Order of a class must be:
1. Javadoc for the class
2. Class declaration
3. Class constants (static & final) variables
4. Class (static) variables
5. Instance variables
6. Constructors
7. Methods

```java
/**
 * A menu that is displayed on a console
 * @author Liam O'Reilly
 * @version 1.0
 */
public class Menu {
    public static final int someFinalConstant = 123;
    
    public static int someStatic = 234;
    
    private int instanceVariable = 435;
    
    public Menu(...) {
        ...
    }
    
    public int getANumber() {
        ...
    }
}
```

Within each of the above the items must appear in the order:
* First the **public**, then **protected**, then **package** level (no access modifier), and then **private**.

#### Order of modifiers 
The modifiers in a single constructor/method declaration must appear in the following order:
* public / protected / private
* abstract
* static
* final

```java
public static void main(String args[]) {
    ...
```