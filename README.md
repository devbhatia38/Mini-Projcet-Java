# MiniBank

MiniBank is a console banking application developed for Part B of the OOPJ
practical list. It supports account creation, deposits, withdrawals, transfers,
statements, batch deposits, reporting, and saving account data between runs.

## Requirements

- JDK 17 or later

## Compile and run

From the project root, compile the Java sources and start the application:

```sh
mkdir -p out
javac -d out MiniBank.java $(find model service util -name '*.java')
java -cp out MiniBank
```

Account data and transaction logs are written under `data/`. Choose Exit to
save the latest account state.
