package ru.vocalize.test.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class DownloadReport {
  @GetMapping(value = "/files/{filename}")
  public void getFile(@PathVariable("filename") String fileName, HttpServletResponse response) {
    log.info("file downloads {}", fileName);
    Path file = Paths.get(fileName);
    if (Files.exists(file)) {
      response.setHeader("Content-disposition", "attachment;filename=" + fileName);
      try {
        Files.copy(file, response.getOutputStream());
        response.getOutputStream()
                .flush();
      } catch (IOException e) {
        log.info("Error writing file to output stream. Filename was '{}'" + fileName, e);
        throw new RuntimeException("IOError writing file to output stream");
      }
    }
  }
}
