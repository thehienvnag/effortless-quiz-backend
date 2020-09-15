package com.example.springdemo.service;

import com.example.springdemo.exceptions.FileNotFoundException;
import com.example.springdemo.exceptions.FileStorageException;
import com.example.springdemo.model.answer.Answer;
import com.example.springdemo.model.studentattempt.StudentAttempt;
import com.example.springdemo.model.studentquestion.StudentQuestion;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {
    @Autowired
    private ServletContext servletContext;

    @Autowired
    CloudBlobContainer cloudBlobContainer;

    public URI uploadFileAzure(MultipartFile multipartFile) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uri;
    }

    public String uploadFile(MultipartFile file) {
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

    public Resource loadFileAsResource(String fileName) {
        String uploadDir = servletContext.getRealPath("resources" + File.separator + "uploads");
        Path uploadPath = Paths.get(uploadDir);
        Path filePath = uploadPath.resolve(fileName).normalize();
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) return resource;
            else throw new FileNotFoundException("File not found " + fileName);
        } catch (MalformedURLException e) {
            throw new FileNotFoundException("File not found " + fileName, e);
        }
    }

    public String exportExcelFile(List<StudentAttempt> studentAttemptList) {

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("StudentAttempts");
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);

        Row header = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle redBackGround = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.RED.getIndex());

        CellStyle greenBackGround = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());

        CellStyle grayBackGround = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        headerStyle.setFont(font);

        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Student Name");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("Question no.");
        headerCell.setCellStyle(headerStyle);

        for (int i = 2; i < studentAttemptList.get(0).getStudentQuestionList().size(); i++) {
            headerCell = header.createCell(i);
            headerCell.setCellValue("Question " + (i + 1));
            headerCell.setCellStyle(headerStyle);
        }

        XSSFFont redFont = (XSSFFont) workbook.createFont();
        redFont.setColor(IndexedColors.RED.index);

        XSSFFont greenFont = (XSSFFont) workbook.createFont();
        greenFont.setColor(IndexedColors.GREEN.index);

        int countRow = 1;

        for (StudentAttempt studentAttempt : studentAttemptList) {
            Row row = sheet.createRow(countRow++);
            Cell rowCell = row.createCell(0);
            rowCell.setCellValue(studentAttempt.getNameOfUser());
            rowCell.setCellStyle(grayBackGround);
            int countCell = 1;
            for (StudentQuestion studentQuestion : studentAttempt.getStudentQuestionList()) {
                String correctChosen = "";
                String wrongChosen = "";
                rowCell = row.createCell(countCell++);
                for (Answer answer : studentQuestion.getQuestionInQuiz().getQuestion().getAnswerList()) {
                    if (studentQuestion.getChosenAnswerId() != null
                            && studentQuestion.getChosenAnswerId().contains(answer.getId().toString())) {
                        if (answer.getCorrect()) {
                            correctChosen += answer.getContent();
                        } else {
                            wrongChosen += answer.getContent();
                        }
                    }
                    String richContent = wrongChosen + " ; " + correctChosen;
//                    XSSFRichTextString richString = new XSSFRichTextString(richContent);
//                    int wrongLastIndex = 0;
//                    if (!wrongChosen.isEmpty()) {
//                        wrongLastIndex = wrongChosen.length() - 1;
//                    }
//
//                    int richContentLength = 0;
//                    if (richContent.length() > 0) {
//                        richContentLength = richContent.length() - 1;
//                    }
//                    richString.applyFont(0, wrongLastIndex, redFont);
//                    richString.applyFont(wrongLastIndex == 0 ? 0 : wrongLastIndex + 3, richContentLength, greenFont);

                    rowCell.setCellValue(richContent);
                    if (wrongChosen.length() > 0 || richContent.equals(" ; ")) {
                        rowCell.setCellStyle(redBackGround);
                    } else {
                        rowCell.setCellStyle(greenBackGround);
                    }
                }
            }
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            workbook.write(out);
            workbook.close();
            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
