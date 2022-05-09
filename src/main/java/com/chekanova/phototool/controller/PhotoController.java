package com.chekanova.phototool.controller;

import com.chekanova.phototool.enums.ImageProcessorType;
import com.chekanova.phototool.enums.MultithreadingStrategy;
import com.chekanova.phototool.service.PhotoService;
import com.chekanova.phototool.validation.FileValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class PhotoController {
    private final PhotoService photoService;
    private static final String JPG = "jpg";

    @ModelAttribute("allTypes")
    public ImageProcessorType[] populateTypes() {
        return ImageProcessorType.values();
    }

    @ModelAttribute("allStrategies")
    public MultithreadingStrategy[] populateStrategies() {
        return MultithreadingStrategy.values();
    }

    @GetMapping("/upload")
    public String upload() {
        return "upload";
    }

    @PostMapping(value = "/upload",
            produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> uploadFile(@RequestParam MultipartFile file,
                                             @RequestParam ImageProcessorType type,
                                             @RequestParam MultithreadingStrategy strategy,
                                             @RequestParam int numberOfThreads,
                                             RedirectAttributes attributes) throws IOException, InterruptedException {
        FileValidationUtil.validateFile(file, attributes);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(file.getBytes());
        BufferedImage originalImage = ImageIO.read(byteArrayInputStream);
        BufferedImage resultImage = photoService.reprocessImage(originalImage, type, strategy, numberOfThreads);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(resultImage, JPG, byteArrayOutputStream);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(byteArrayOutputStream.toByteArray());
    }
}
