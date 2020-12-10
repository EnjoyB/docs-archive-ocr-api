# OCR API
Ocr-API is another part of a project called DocsArchive that tries to wrap OCR(in this case tesseract) with Java and be
accessed through REST API. Moreover, as separate part, it can be use by others just as it is, if they just need server for
ocr-scanning documents.

The project supports:
* Uploading
  * File
  * Files
* Get
  * Scanned document
  * Current document status - e.g. if the image is already transformed from image to text via tesseract
* Delete
  * File
  * Files

## DocsArchive
As mentioned in the beginning, it is one part of the whole project. Better desception can be found at the part of the
[backends repository](https://github.com/EnjoyB/docs-archive-backend)

## Docker
The Ocr-API is dockerized and available at docker-hub [repository](https://hub.docker.com/repository/docker/madgyver/docs-archive-ocr-api).
To run it, I would recommend to use docker-compose script:
```
version: "3.8"

services:
ocr-api:
    container_name: ocr-api
    image: madgyver/docs-archive-ocr-api:latest
    command:
      --tesseract.path=/usr/share/tessdata/
      --server.address=0.0.0.0
    ports:
      - "8086:8086"
```
Information:
 * tesseract.path - is a path to training sets for tesseract
 * server.address - is address for th ocr-pai server
 
To execute it (dont forget to save the earlier script to file and execute
it the following commands in the same path):
 * first download the latest image - ```docker-compose pull```
 * start it - ```docker-compose up```

## HTTP APIs
To see prepared/existing APIs see an available [page](localhost:8086/api/ocr/swagger-ui.html) of the swagger(after starting application):
```localhost:8086/api/ocr/swagger-ui.html```