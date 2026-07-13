package com.example.demo.exam.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ImageProcess {
  @Id private String id = UUID.randomUUID().toString();
  private String nomFichier;
  private String email;
  private Instant creationDate = Instant.now();
}
