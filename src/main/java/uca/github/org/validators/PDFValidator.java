package uca.github.org.validators;

import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

/**
 * PDFValidator is a utility class that provides a method to validate whether a given MultipartFile is a valid PDF file.
 * The validation process includes checking the file extension, MIME type, and analyzing the file content to ensure it adheres to the PDF format.
 * If the file fails any of the validation checks, an IllegalArgumentException is thrown with a message indicating that only PDF files are allowed.
 */
public class PDFValidator {
    private static final Tika tika = new Tika();
    public static void validatePdf(MultipartFile file) throws IllegalArgumentException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }
        String mimeType = file.getContentType();
        if (!"application/pdf".equalsIgnoreCase(mimeType)) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }
        try (InputStream is = file.getInputStream()) {
            String detectedType = tika.detect(is);

            if (!"application/pdf".equalsIgnoreCase(detectedType)) {
                throw new IllegalArgumentException("Only PDF files are allowed");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid PDF file");
        }
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[4];
            is.read(header);
            String headerStr = new String(header);
            if (!headerStr.startsWith("%PDF")) {
                throw new IllegalArgumentException("Only PDF files are allowed");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid PDF file");
        }
    }
}
