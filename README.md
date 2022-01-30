# GSR Java Software Engineer Development Exercise - Stewart Young

## Project Overview
This project contains a number of key components
* An order book of 10 levels: 10 bid prices below and 10 offer prices above current market price
* Level II market data: Level II shows you who the market participant is that is making a trade,
  whether they are buying or selling, the size of the order, and the price offered.
* Coinbase Pro API
    * Trading data: require authentication and provide access to placing orders and other account information
    * Market data (what we're using): provide market data and are public

## Java Implementation Specifics
* Java 11

## Running instructions
This project includes a dockerfile, meaning if you have docker installed in your machine
you can run this code from within a docker container on any device.
```shell
./gradlew clean build
./gradlew docker
docker run coinbaseorderbook:1.0
```