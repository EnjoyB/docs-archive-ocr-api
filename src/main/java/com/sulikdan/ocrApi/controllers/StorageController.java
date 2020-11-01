package com.sulikdan.ocrApi.controllers;

import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.services.FileStorageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Daniel Šulik on 02-Jul-20
 * <p>
 * Class Storage controller created for debugging or information reason, to be aware how many files are available.
 * For now it's not needed -> therefore disabled.
 */
@ConditionalOnExpression("${storage.controller.enabled:false}")
@RestController
@RequestMapping("storage")
public class StorageController {

    FileStorageService fileStorageService;

    public StorageController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @ResponseBody
    @PostMapping(path = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file) {
        String message = "";
        try {
            fileStorageService.saveFile(file, SharedControllerLogic.generateNamePrefix());

            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new String(message));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new String(message));
        }
    }


    @GetMapping("/files")
    public ResponseEntity<List<Document>> getListFiles() {
        List<Document> documents = fileStorageService.loadAllFiles().map(path -> {
            String filename = path.getFileName().toString();
            String url = MvcUriComponentsBuilder
                    .fromMethodName(StorageController.class, "getFile", path.getFileName().toString()).build().toString();

            return new Document(filename, url, null);
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(documents);
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = fileStorageService.loadFile(filename);
        return ResponseEntity.ok()
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

}
