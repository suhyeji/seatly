package com.seatly.seatly.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.seatly.seatly.service.ImageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

  private final ImageService imageService;

  @PostMapping("/upload")
  public String upload(@RequestParam MultipartFile file) {
    return imageService.upload(file);
  }

  @GetMapping("/{imageId}")
  public ResponseEntity<Resource> getImage(@PathVariable String imageId) {
    return imageService.load(imageId);
  }

  @DeleteMapping("/{imageId}")
  public void deleteImage(@PathVariable String imageId) {
    imageService.delete(imageId);
  }
}
