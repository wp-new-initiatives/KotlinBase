# Code reviews

Please read our [definition of DONE (and done done)](https://whitepages.atlassian.net/wiki/spaces/ENG/pages/689309/Our+definition+of+DONE+and+done+done)

for each PR that:
* has a ticket should include the link to the ticket
* adds an environment variable
1. updates the helm-coresite project to include the environment 
variables for request line 
1. adds it to the [READ.ME](/README.me)
1. Notifies the developers about it, and its appropriate value

## Things to keep in mind:
### general
* prefer `objects` over `maps`
* prefer `val` over `var`
* let kotlin or spring do the heavy lifting
* `camelCase` over `snake_case`
* avoid mutable classes whenever possible
* avoid null types whenever possible

#### controllers
* prefer `${method}Mapping` over `RequestMapping`
* think resources when considering the route
* do not specify the http method in the route
* prefer `@ResponseBody` over `ResponseEntity<*>` (they do the same thing)  
```
GET /v1/get_user_by_user_id?user_id={user_id} # is bad
GET /v1/user/{userId} # is good
```

#### clients
* Use a class from `protocol` to do the heavy lifting
* If you see a lot of duplication of effort, consider convenience methods, delegates, or wrappers
* client functions should accept a custom query object as input

#### filters
* Always use `FilterOrder` to make sure your code gets executed properly

#### presenter
* These should primarily be `data class`'s
* read PROJECT_STRUCTURE.md for more information
* there should be no `Array` classes here, use `List`. Arrays implement `.equals()` not how you'd expect 

#### protocols
* Because Request Line acts mainly as an orchestrator, this is the
 most sensitive area of the codebase. 