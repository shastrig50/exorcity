package com.exorcity.photo;

import com.exorcity.common.exception.BadRequestException;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.util.HexFormat;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

/**
 * Signed uploads to Cloudinary's REST API when CLOUDINARY_URL (cloudinary://key:secret@cloud) is set.
 * Without it, clients submit an image URL instead.
 */
@Service
public class PhotoService {

    private static final String FOLDER = "exorcity";

    private final Credentials credentials;
    private final RestClient restClient = RestClient.create();
    private final Clock clock;

    public PhotoService(@Value("${exorcity.cloudinary-url:}") String cloudinaryUrl, Clock clock) {
        this.credentials = Credentials.parse(cloudinaryUrl);
        this.clock = clock;
    }

    public boolean uploadsEnabled() {
        return credentials != null;
    }

    public String upload(MultipartFile file) {
        if (!uploadsEnabled()) {
            throw new BadRequestException("Photo upload is not configured; paste an image URL instead");
        }
        if (file == null || file.isEmpty()) throw new BadRequestException("Choose a photo to upload");
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("Only image files can be uploaded");
        }
        long timestamp = clock.instant().getEpochSecond();
        String signature = sha1("folder=" + FOLDER + "&timestamp=" + timestamp + credentials.apiSecret());

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        try {
            byte[] bytes = file.getBytes();
            String filename = file.getOriginalFilename() == null ? "photo" : file.getOriginalFilename();
            body.add("file", new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    return filename;
                }
            });
        } catch (IOException e) {
            throw new BadRequestException("Could not read the uploaded photo");
        }
        body.add("api_key", credentials.apiKey());
        body.add("timestamp", String.valueOf(timestamp));
        body.add("folder", FOLDER);
        body.add("signature", signature);

        try {
            Map<?, ?> result = restClient.post()
                    .uri("https://api.cloudinary.com/v1_1/{cloud}/image/upload", credentials.cloudName())
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(Map.class);
            Object url = result == null ? null : result.get("secure_url");
            if (url == null) throw new BadRequestException("Photo upload failed");
            return url.toString();
        } catch (RestClientException e) {
            throw new BadRequestException("Photo upload failed, please try again or paste a URL");
        }
    }

    private static String sha1(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-1").digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    record Credentials(String apiKey, String apiSecret, String cloudName) {
        static Credentials parse(String url) {
            if (url == null || url.isBlank()) return null;
            URI uri = URI.create(url.trim());
            if (!"cloudinary".equals(uri.getScheme()) || uri.getUserInfo() == null) {
                throw new IllegalStateException("CLOUDINARY_URL must look like cloudinary://key:secret@cloud");
            }
            String[] parts = uri.getUserInfo().split(":", 2);
            return new Credentials(parts[0], parts.length > 1 ? parts[1] : "", uri.getHost());
        }
    }
}
