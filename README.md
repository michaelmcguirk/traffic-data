

## Best Route Through Traffic
 - Michael McGuirk

This application accepts a JSON data file representing the flow of traffic through a city and finds the best route. It uses Djikstra's Algorithm to generate the best path from the start intersection to all intersections. 

The response is a JSON object in the following format:

```
{
  "start" : "A8",
  "end" : "C11",
  "distance" : 614.8311764426272,
  "route" : [
    "A8",
    "B8",
    "C8",
    "D8",
    "E8",
    "F8",
    "F9",
    "F10",
    "F11",
    "F12",
    "E12",
    "D12",
    "C12",
    "C11"
  ]
}
```



### Prerequisites

Compile and get dependencies

  ```
  sbt compile
  ```



## Usage

```
sbt "run Bestroute [start intersection] [end intersection] [path to input file]"

e.g.

sbt "run Bestroute A1 D12 /User/files/city-data.json"
```



## Testing
 ```
 sbt -DtestData=[test data file location] test

 sbt -DtestData=/path/to/file/for/test.json test

 ```



