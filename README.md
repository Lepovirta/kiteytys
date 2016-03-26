# Kiteytys ("crystallize")

## Development
Dependencies:
- scala
- [sbt](http://www.scala-sbt.org/)
- JDK
- local STMP server ([example](https://djfarrelly.github.io/MailDev/))
- PostgreSQL
- wkhtmltopdf

Run:
```
sbt "run user.conf"
```

Deploy:
```
./script/deploy.sh
```

Import card data to production:
```
./script/import_cards.sh file.csv
```

## Production
Dependencies:
- JRE
- PostgreSQL
- wkhtmltopdf

