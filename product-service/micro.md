##### test container #####




http://localhost:8080/api/order    --  gateway with order service 

http://localhost:8082/actuator/health   - actuator of order service 
http://localhost:8761/    - eureka
http://localhost:8082/actuator     --  tell us how many times the service is called 


docker run -d -p 9411:9411 openzipkin/zipkin  -- zipkin docker 