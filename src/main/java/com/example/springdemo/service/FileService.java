package com.example.springdemo.service;

import com.example.springdemo.exceptions.FileNotFoundException;
import com.example.springdemo.exceptions.FileStorageException;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileService {
    @Autowired
    private ServletContext servletContext;

    @Autowired
    CloudBlobContainer cloudBlobContainer;
    public URI uploadFileAzure(MultipartFile multipartFile){
        URI uri = null;
        CloudBlockBlob blob = null;
        try {

            String originalFilename = multipartFile.getOriginalFilename();
            String extension = multipartFile.getOriginalFilename().substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString().substring(0, 21) + extension;
            blob = cloudBlobContainer.getBlockBlobReference(fileName);
            blob.upload(multipartFile.getInputStream(), -1);
            uri = blob.getUri();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (StorageException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return uri;
    }

    public String uploadFile(MultipartFile file){
        String uploadDir = servletContext.getRealPath("resources" + File.separator + "uploads");
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        String fileName = UUID.randomUUID().toString().substring(0, 15) + extension;
        String fileNameUpload = uploadDir + File.separator + fileName;
        try {
            Path copyLocation = Paths.get(fileNameUpload);
            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
            throw new FileStorageException("Could not store file " + file.getOriginalFilename()
                    + ". Please try again!");
        }
        return fileName;
    }

    public Resource loadFileAsResource(String fileName){
        String uploadDir = servletContext.getRealPath("resources" + File.separator + "uploads");
        Path uploadPath = Paths.get(uploadDir);
        Path filePath = uploadPath.resolve(fileName).normalize();
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) return resource;
            else throw new FileNotFoundException("File not found " + fileName);
        } catch (MalformedURLException e) {
            throw new FileNotFoundException("File not found " + fileName, e);
        }
    }
}
