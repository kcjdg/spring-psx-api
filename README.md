# spring-psx-api | Live PSE Stock Data API
[![Build Status](https://api.travis-ci.com/kencrisjohn/spring-psx-api.svg?branch=master)](https://travis-ci.com/kencrisjohn/spring-psx-api) [![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=com.herokuapp.psxapi%3Aspring-psx-api&metric=security_rating)](https://sonarcloud.io/dashboard?id=com.herokuapp.psxapi%3Aspring-psx-api) [![Bugs](https://sonarcloud.io/api/project_badges/measure?project=com.herokuapp.psxapi%3Aspring-psx-api&metric=bugs)](https://sonarcloud.io/dashboard?id=com.herokuapp.psxapi%3Aspring-psx-api)

## About

An API for Philippine Stock Exchange (PSEI) created using Spring Framework and deployed in Heroku. This is a live PSE API data that can be used by other developers testing their system.
Live Version: https://spring-psx-api.herokuapp.com/docu/swagger-ui.html

## API Usage

### List all stocks with it's latest price 
```bash
curl https://spring-psx-api.herokuapp.com/stocks
```
List all companies in Philippine Stock Exchange with it's latest stock price.

### Find specific company by it's stock symbol
```bash
curl https://spring-psx-api.herokuapp.com/stocks/{symbol}
```
You can find all stock symbols in https://edge.pse.com.ph/companyDirectory/form.do

### Look for company details
```bash
curl https://spring-psx-api.herokuapp.com/stocks/{symbol}/details
```
Search for specific stock with full details

### List all stocks by date
```bash
curl https://spring-psx-api.herokuapp.com/stocks/{yyyy-MM-dd}
```
Find previous record using date format {yyyy-MM-dd}


### Find specific company by date and stock symbol
```bash
curl https://spring-psx-api.herokuapp.com/stocks/by/{yyyy-MM-dd}/{symbol}
```
Search specific stock in previous record using date and symbol

