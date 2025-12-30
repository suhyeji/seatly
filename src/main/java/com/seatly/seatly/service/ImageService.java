package com.seatly.seatly.service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.seatly.seatly.global.FileProperties;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {

  private final FileProperties fileProperties;

  @PostConstruct
  public void init() {
    try {
      Files.createDirectories(getUploadDir());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Path getUploadDir() {
    return Paths.get(fileProperties.getUploadDir());
  }

  public String upload(MultipartFile file) {
    if (file.isEmpty()) {
      throw new IllegalArgumentException("빈 파일");
    }

    String imageId = UUID.randomUUID().toString();
    String ext = getExtension(file.getOriginalFilename());
    String filename = imageId + ext;

    Path target = getUploadDir().resolve(filename);

    try {
      Files.copy(file.getInputStream(), target,
          StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return imageId;
  }

  public ResponseEntity<Resource> load(String imageId) {
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(getUploadDir(), imageId + "*")) {

      Iterator<Path> it = stream.iterator();
      if (!it.hasNext()) {
        return ResponseEntity.notFound().build();
      }

      Path path = it.next();
      Resource resource = new UrlResource(path.toUri());

      return ResponseEntity.ok()
          .contentType(getContentType(path))
          .body(resource);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean delete(String imageId) {
    Path dir = getUploadDir().toAbsolutePath().normalize();

    try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir, imageId + "*")) {

      Iterator<Path> it = ds.iterator();
      if (!it.hasNext()) {
        return false;
      }

      Path target = it.next().normalize();

      if (!target.startsWith(dir)) {
        throw new SecurityException("잘못된 경로");
      }

      return Files.deleteIfExists(target);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private MediaType getContentType(Path path) {
    try {
      String type = Files.probeContentType(path);
      return type != null
          ? MediaType.parseMediaType(type)
          : MediaType.APPLICATION_OCTET_STREAM;
    } catch (IOException e) {
      return MediaType.APPLICATION_OCTET_STREAM;
    }
  }

  private String getExtension(String original) {
    if (original == null || !original.contains(".")) {
      return "";
    }
    return original.substring(original.lastIndexOf("."));
  }

}
