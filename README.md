# MiniBank

MiniBank is a console banking application developed one practical at a time for
Part B of the OOPJ practical list. The current version contains the Part B
menu shell from Practical 1.

## Requirements

- JDK 17 or later

## Compile and run

From the project root:

```sh
mkdir -p out
javac -d out src/MiniBank.java
java -cp out MiniBank
```

Choose options 1–4 to see the feature placeholders. Choose 5 to exit. Invalid
input is rejected and the menu is shown again.
