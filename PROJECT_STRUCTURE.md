# Request Line project structure

There is a lot of activity occurring on request line, and in order to
let the project self manage, all developers should understand some things about
how the project is set up

Our general goals with this project are to have a hexagonal architecture 
and clear separation of concerns. To read more about these ideas: 

* https://blog.octo.com/en/hexagonal-architecture-three-principles-and-an-implementation-example/
* https://12factor.net/

### com.whitepages.kotlinproject
* where the project lives

#### com.whitepages.kotlinproject.api
* individual controllers exist per concrete entity
* routes are specified in controllers

#### com.whitepages.kotlinproject.clients
* all outgoing entities are considered clients (yes, even file reading)
* major clients are sometimes broken down to separate subclients (CAA,
 Search Layer) 

#### com.whitepages.kotlinproject.filter
* the internals of request line, this is analogous to middleware
* we can have custom exception handlers that allow for a clean response
* timeout middleware, maybe one day auth middleware

#### com.whitepages.kotlinproject.presenters
* incoming requests to controllers exist in `api.request`
* outgoing requests to clients exist in client based sub packages
* outgoing responses from controllers exist in `api.response`
* outgoing responses from clients exist in client base sub packages
* if you are directly sending the client's response, you dont need to
 make anything in the `api.response` package

#### com.whitepages.kotlinproject.protocols
* metrics and logging are important to the operationalization of request line
* protocols allow us to gather metrics and logs on every incoming and outgoing 
 request so that we dont have to worry about it on every PR.
* __ALL__ clients should use protocols to make requests
