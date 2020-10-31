package com.example.filedemo.service;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.*;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;
import com.example.filedemo.analyser.*;
import com.example.filedemo.exception.FileStorageException;
import com.example.filedemo.exception.MyFileNotFoundException;
import com.example.filedemo.property.FileStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            //open the file and operate on it
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            String type = "";

            try {
                type = analyse.analyses(String.valueOf(filePath));

                //creating
                try {
                    File myObj = new File(targetLocation + "result.txt");
                    if (myObj.createNewFile()) {
                        System.out.println("File created: " + myObj.getName());
                    } else {
                        System.out.println("File already exists.");
                    }
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }

                //writing to file
                try {
                    FileWriter myWriter = new FileWriter(targetLocation+"result.txt");
                    myWriter.write("Analysing the file type ...\n");
                    myWriter.append("The file was identified to be of type : " + type + "\n\n");
                    myWriter.append("\nExtracting METADATA...\nRendring METADATA...\n\n");

                    // yahan se code hai data write ka
                    File file1 = new File(String.valueOf(filePath));
                    //Parser method parameters
                    Parser parser = new AutoDetectParser();
                    BodyContentHandler handler = new BodyContentHandler();
                    Metadata metadata = new Metadata();
                    FileInputStream inputstream = new FileInputStream(file1);
                    ParseContext context = new ParseContext();

                    parser.parse(inputstream, handler, metadata, context);
                    System.out.println(handler.toString());

                    //getting the list of all meta data elements
                    String[] metadataNames = metadata.names();

                    for(String name : metadataNames) {
                        myWriter.append(name + ": " + metadata.get(name) + "\n");
                    }
                    myWriter.append("\n---end of transcript---");
                    myWriter.close();
                    //bas yahin tak tha writing operation

                    System.out.println("Successfully wrote to the file.");
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }
}
