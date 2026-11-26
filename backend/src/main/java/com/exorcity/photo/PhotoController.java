package com.exorcity.photo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Photos")
public class PhotoController {

    private final PhotoService photoService;

    @GetMapping("/config")
    @Operation(summary = "Client feature flags (e.g. whether photo upload is available)")
    public Map<String, Object> config() {
        return Map.of("photoUploadsEnabled", photoService.uploadsEnabled());
    }

    @PostMapping(value = "/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a photo to Cloudinary (only when CLOUDINARY_URL is set)")
    public Map<String, String> upload(@RequestParam("file") MultipartFile file) {
        return Map.of("url", photoService.upload(file));
    }
}
