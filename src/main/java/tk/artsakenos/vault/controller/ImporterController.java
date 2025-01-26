package tk.artsakenos.vault.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import tk.artsakenos.vault.service.ParserFileService;
import tk.artsakenos.vault.service.ParserWikiService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings("unused")
@Controller
public class ImporterController {

    @Autowired
    private ParserWikiService parserWikiService;

    @Autowired
    private ParserFileService parserFileService;

    public static final String UPLOAD_PATH = "db/uploads/";

    @GetMapping("/import")
    public String showImporterPage(Model model) {
        String status = parserWikiService.getParserStatus();
        if (!status.startsWith("#idle")) {
            model.addAttribute("message", status);
        }
        return "importer";
    }

    @PostMapping("/import")
    public String importDocument(
            @RequestParam("source") String source,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "file", required = false) MultipartFile file,
            Model model) {

        final StringBuilder message = new StringBuilder();
        switch (source) {
            case "wiki":
                String status = parserWikiService.getParserStatus();
                if (!status.startsWith("#idle")) {
                    message.append("Please wait until another import process will finish.");
                } else {
                    message.append("Importing Wikipedia dump from folder: ").append(name);
                    new Thread(() -> {
                        try {
                            parserWikiService.parseDir(name);
                        } catch (IOException e) {
                            message.append("\nException while trying to import the folder: ").append(e.getLocalizedMessage());
                            throw new RuntimeException(e);
                        }
                    }).start();
                }
                message.append("\n").append(status);
                break;
            case "file":
                if (file != null && !file.isEmpty()) {
                    try {
                        Path uploadPath = Paths.get(UPLOAD_PATH);
                        if (!Files.exists(uploadPath)) {
                            Files.createDirectories(uploadPath);
                        }
                        Path filePath = uploadPath.resolve(file.getOriginalFilename());
                        Files.copy(file.getInputStream(), filePath);
                        message.append("File ").append(file.getOriginalFilename()).append(" uploaded successfully to ").append(UPLOAD_PATH);
                        parserFileService.parseFile(filePath);
                    } catch (IOException e) {
                        message.append("Failed to upload file: ").append(e.getMessage());
                    }
                } else {
                    message.append("No file uploaded.");
                }
                break;
            case "web":
                message.append("Importing web page: ").append(name);
                break;
            default:
                message.append("Invalid source type.");
        }

        model.addAttribute("message", message);
        return "importer";
    }
}