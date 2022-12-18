# URL Shortener

![Kotlin Test Assignment](https://img.shields.io/badge/kotlin-Test%20Assignment-important?style=for-the-badge&logo=kotlin)

Design a URL shortener. You know, like bitly.

## Features

1. Auto-generate short links
2. Let users choose their short links
3. No links modification or deletion

## API

http://localhost:8080/admin/swagger-ui

## Design decisions

1. Auto-generated slugs are 7 characters long Base58 strings. 
It means we can accommodate 58^7 ≈ 2 trillion unambiguous alphanumeric 
random slugs. Going from 7 characters to 8 will dramatically push back 
the point where we run out of random slugs – up to 58^8 ≈ 128 trillion.
2. Slugs are randomly generated, re-rolling already-used character combinations.
As we have more and more slugs in our database, we'll get more and more collisions
and will need more and more re-rolls. At some point it may be a good idea to consider
switching to a base conversion strategy.
3. We use Redis to store slug-destination pairs. It is fast for writes and simple key-value reads. 
We've also added a Caffeine caching layer, so that we can put as much of the data in memory 
as possible and avoid disk seeks.
4. The next optimization steps should probably be sharding our database and setting up multiple 
web server workers and putting them behind a load balancer. 

## Further reading

https://www.interviewcake.com/question/java/url-shortener
