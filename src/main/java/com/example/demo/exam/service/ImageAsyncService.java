package com.example.demo.exam.service;

import com.example.demo.file.BucketComponent;
import com.example.demo.mail.Email;
import com.example.demo.mail.Mailer;
import jakarta.mail.internet.InternetAddress;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ImageAsyncService {

  private final BucketComponent bucketComponent;
  private final Mailer mailer;

  @Async
  @SneakyThrows
  public void processImageAndSendEmail(
      File originalFile, String emailDestination, String bucketKey) {
    // 1. Image en Noir et Blanc
    BufferedImage image = ImageIO.read(originalFile);
    BufferedImage bwImage =
        new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
    var g = bwImage.getGraphics();
    g.drawImage(image, 0, 0, null);
    g.dispose();

    File bwFile = new File("bw_" + originalFile.getName());
    ImageIO.write(bwImage, "png", bwFile);

    // 2. Utilisation des composants natifs de Poja
    bucketComponent.upload(bwFile, bucketKey);

    var email =
        new Email(
            new InternetAddress(emailDestination),
            List.of(),
            List.of(),
            "Image Traitee",
            "Votre image NB a ete stockee avec succes sous le nom : " + bucketKey,
            List.of());
    mailer.accept(email);

    // 3. Nettoyage
    originalFile.delete();
    bwFile.delete();
  }
}
