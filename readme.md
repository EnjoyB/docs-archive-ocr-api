# OCR API
A project that tries to wrap OCR(in this case tesseract) with Java and be
accessed through REST API. The goal is to make it as microservice as docker image.
Any other user can use this microservice themselves with already implemented REST API.

The project supports:
* Uploading
  * File
  * Files
* Get
  * File
  * Files
  * Current status - e.g. if the image is already transformed from image to text via tesseract
* Delete
  * File
  * Files

After receiving data from transformation, the uploaded file will be deleted.
The intention of the project is to wrap tesseract as microservice, to be used, but not
to store files.

| Operation       |  Mapping view  |  Example  |
|:----------------|:---|:---|
| GET file        |    |    |
| GET file-status |    |    |
| GET files       |    |    |
| POST file       |    |    |
| POST files      |    |    |
| Delete file     |    |    |
| Delete files    |    |    |
