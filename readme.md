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