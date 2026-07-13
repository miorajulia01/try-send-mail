package com.example.demo.exam.controller;

import com.example.demo.exam.model.ImageProcess;
import com.example.demo.exam.repository.ImageProcessRepository;
import com.example.demo.exam.service.ImageAsyncService;
import java.io.File;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class ImageController {

  private final ImageProcessRepository repository;
  private final ImageAsyncService asyncService;

  @GetMapping("/images")
  public List<ImageProcess> findAll() {
    return repository.findAll();
  }

  @PostMapping("/images")
  @SneakyThrows
  public String uploadImage(
      @RequestParam("file") MultipartFile file, @RequestParam("email") String email) {

    // Synchrone : Neon BDD
    ImageProcess imageProcess = new ImageProcess();
    imageProcess.setNomFichier(file.getOriginalFilename());
    imageProcess.setEmail(email);
    repository.save(imageProcess);

    File tempFile =
        new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
    file.transferTo(tempFile);

    // Asynchrone : NB, S3 et Mail
    String bucketKey = "exam-nb/" + imageProcess.getId() + "_" + file.getOriginalFilename();
    asyncService.processImageAndSendEmail(tempFile, email, bucketKey);

    return "Requete validee. ID Persistance : " + imageProcess.getId();
  }
}
