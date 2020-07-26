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


## Details for MasterThesis
* tess4j vs tesseract-platfomr vs default c++/c code
    * looks like tess4j is weaker than t-platform
* problem with image's dpi may need to be fixed after receiving image
* does it makes sense to use Async connection? Storing data in memory?
    * that would require some intelligent logic, to delete, store data as to avoid overloading
* Every language of tesseract API should have its own istance, however its not sure
if they are separeted, when executed. In that case it would be smarter, just to reinit every time 
tesserat API with same/different language
    * that would also help with leaking of undestroyed tesseract APIs
* 
* Api should take multiple Files
* Async method looks like required rather than wait unknown amount of time for OCR processing...
* what about separting controller, for every file format new one and same for services??
  * could decrease coupling
  * but will increase amount of code
* OCR mode not yet checked, init - containes multiple overload methods but is missing documentation ..
## 11.7.2020
* actual trouble decision between storing files:
    * right to memory - memory demanding
    * storing to filesystem - decrease performance
* another trouble with instance of TessBaseApi
    * Every thread may need new instance of TessBaseApi
    * With everyLanguage it gets more tricky as I would need to take care of available instances of everyLanguage and not to train all the time new instance...
    * maybe to use ConcuncurrentMap with Queue? That would require new object that will take care of problems to be thread-safe manager
### Future stuff
* config passed through api
    * bind them to user
* work effectively with TessBaseApi instances

### Docker stuff
* create jar with all dependencies: `mvn clean compile assembly:single`
* build image: `docker build -t sulikdan/java-rest-api-ocr .`
* start it: `docker run -t -i -p 8080:8080 sulikdan/java-rest-api-ocr -tessdata /usr/share/tessdata`
`mvn clean package spring-boot:repackage`

* delete containers: `docker rm -vf $(docker ps -a -q)`
* delete images: `docker rmi -f $(docker images -a -q)`