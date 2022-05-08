Decision 1: Need a starter package, spring boot is an easy start that will provide what we need. Used git clone https://github.com/spring-guides/gs-rest-service.git from https://spring.io/guides/gs/intellij-idea/. Could have used JHipster but this seems like overkill.
- from this starter package I took the "complete" example and moved some directories around to make the project folders adhere to standards
- tests are already running, application as well after moving away from port 8080 as I already have the docker container running at port 8080 (started readme with notes)
- removed maven files in favour of gradle files.

Decision 2: Use git from the start, this way we can look back to previous versions of the code to see what we changed etc.

Decision 3: Added lombok for convenience.

Decision 3: Need some kind of HTTP library. Use OkHTTP its simple and small. Used jackson for deserialisation as it is already available in the spring boot starter and very standard.

Decision 4: Adhere to standard layered architecture. API layer, service layer.

Implementation of AS-1
notes:
- Right now im just doing the requests in a serialised manner. Will fix this in AS-2 (see first commit AS-2)
- No type validation anywhere as the user story does not specify what the type of the fields should be (other than ISO-2 for country codes, but also here not clear if validation is expected). Normally i would expect typing of the fields with some validation as would have been discussed in the refinement.
- AggregationApiTests is an integration test that only works when the xyzassessment/backend-services docker image is locally running. Normally i would expect this test to be in a separate package so that it can be run in that specific environment.
- Normally I would expect to have some type of API documentation in place, such as swagger for example. Skipped to save time.

Implementation of AS-2
- It occurred to me that there are libraries to handle these kind of situations, maybe webflux. However such a library might cover a lot of the total solution and might not show off skills that you would like to see.
  Chose to implement something myself using java concurrency. CyclicBarrier seems it could do the job, we do have the difficulty however that each thread might request a different
  amount of parameters. This could be fixed by spawning a thread per requested parameter key value. I chose this solution as im running out of the amount of time that I am willing to invest.
  I also chose this solution as it neatly fixes the requirement that we should always relay calls that contain exactly five keys. The assignment suggests that it is not allowed
  to send six keys if the last call contained two and we already had four. Also the number of threads in the pool can be limited to 13 without starvartion, and most threads will be waiting all the time anyway.
  CyclicBarrier has the nice feature that you can make the last thread to join execute some work before waking up the other threads and it is also reusable. It also has a timeout feature.

Implementation of AS-3
- Obviously I did not handle exceptions in a nice way anywhere, skipped to save time. 
- Switched to a much simpler solution using a list as a queue and using wait and notifyAll in order to communicate between threads. This way i was able to implement the requirement that any items remaining in the queue are budled into one request tot he underlying APIs.
- Made queue timeout configurable for testing purposes
