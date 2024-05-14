# Trade Enrichment Service

This service allows to map product id to product name in `csv` files containing trades.

#### Input format

`date,product_id,currency,price`<br>
`20160101,1,EUR,10.0`<br>
`20160102,2,EUR,15.0`<br>

#### Output format

`date,product_name,currency,price`<br>
`20160101,Treasury Bills Domestic,EUR,10.0`<br>
`20160102,Corporate Bonds Domestic,EUR,15.0`<br>

#### Input errors handling

* If the `product_id` is unknown to the service, it uses `Missing Product Name` as the product name.
  <br>The list of known products is specified in file `src/main/resources/product.csv`
* If the `date` is not in the format of `yyyyMMdd` or the `date` is invalid (for example `20200231` - no such day in
  February),
  the record will be ignored and not returned in the result file.
* The service is sensitive to column names and order.

### Accessing the service

Once the service is started (explained later in this document), `csv` files can be  
sent to it over an HTTP connection via the service endpoint `/api/v1/enrich`.<br>

The endpoint returns the result file over the same HTTP connection.

#### Usage

`curl -v -F file=@src/test/resources/input_trades.csv  http://localhost:8080/api/v1/enrich -o enriched.csv`

There is no limit on the input file size other than the amount of free space in the server <br> temporary directory
(explained in the "Building and Running" section)
and the <br>
configured maximum size of the file and request (in file `src/main/resources/application.properties`):<br><br>
`spring.servlet.multipart.max-file-size=20GB` <br>
`spring.servlet.multipart.max-request-size=20GB` <br><br>
which can be also overridden when running the server.

### Building and running the server

`mvn clean install`<br>
`java -jar target/trade-enrichment-service-0.0.1-SNAPSHOT.jar`<br>

To override the temporary directory location use option  `java -Djava.io.tmpdir=<your_tmp_dir> ...`<br>
Make sure that `<your_tmp_dir>` has enough space to host the csv files, otherwise the requests will fail
with `No space left` exception.

## Design

The design is simple. It takes advantage of `InputStream` and `OutputStream` that allows
to handle big files without the need to load them into the RAM in full at once.
Every request is processes by a single thread, as the bottleneck of the processing is
the IO, not the CPU. <br>Multiple requests can be processed at once safely, since the state
that is shared between the server threads (the products map) is immutable.

### Performance

Load testing on a 10GB input file (500 millions records) has shown that the
noop API endpoint (returning the input csv as was sent in the request) performs at the rate of 150MB/s
on a desktop computer.

The transformation of this input file (mapping product ids) performed at 31MB/s, which could certainly be improved, but
perhaps at
the expense of code readability and cleanness (reading the input stream as bytes
instead of String lines, not using CsvParser etc.)

### Discussion

Processing large files, taking many minutes should probably be done in an asynchronous manner - the
endpoint could get the location of the file on some storage device, save the result to this storage device and let the
requester know via a queue or a webhook that the file has been processed.  
<br>
If the input files is bigger (hundreds of GB, TB) perhaps it would be more optimal to use an engine like Apache
Spark, as for now a single host would take too much time (hours) and in case of a failure, the request would have to be
restarted.

### Further improvements

* Configurability (location of product.csv etc.)
* Insensitivity to columns order
* Performance

