package rupp.com.kh.services.implementation;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.Collections;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.ArrayList;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors; 
import java.util.stream.Stream; 
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import rupp.com.kh.dto.FileDetailDTO;
import rupp.com.kh.dto.User;
import rupp.com.kh.enums.Gender;
import rupp.com.kh.services.AppService;
import rupp.com.kh.utils.AppData;

@Service
public class ServiceImpl implements AppService {

    private final String DATABASE_PATH = "src/main/java/rupp/com/kh/utils/database.txt"; 
    /** 
    * @MS_FILE_EXSTENTION store all files exstention that allowed.
    */ 
    private final List<String> MS_FILE_EXSTENTIONS = Collections.unmodifiableList
            (new ArrayList<>(Arrays.asList("docx", "xlsx", "pptx", "pdf")));

    /**
     * @function
     * @param
     */
    @Override
    public List<User> getGoupMembers() {
        return Arrays.asList(
            new User("Sann Rayuth",Gender.MALE,"images/group4/rayuth.png","Mazer"),
            new User("Ly Chyleng",Gender.MALE,"images/group4/chyleang.png","Mazer"),
            new User("San Putchhay",Gender.MALE,"images/group4/chhay.png","Write Book"),
            new User("Ly Horleng",Gender.MALE,"images/group4/horleng.png","Write Code"),
            new User("Lim Dane",Gender.MALE,"images/group4/dane.png","Write Code"),
            new User("Rinn Rozajesmine",Gender.FEMALE,"images/group4/jesmine.png","Design Book"),
            new User("Yon Yii",Gender.MALE,"images/group4/yii.png","Make Slide"),
            new User("Huy Bochhy",Gender.MALE,"images/group4/bochy.png","Make Slide")
        );
    }

    /**
     * @function 
     * @param
     */
    @Override
    public List<FileDetailDTO> retrieveAllFiles(String pathStr) throws Exception {

        Path path = Paths.get(pathStr);
        
        if (!Files.isDirectory(path))  
            throw new IllegalArgumentException("Path must be directory!\r\n");

        try(Stream<Path> walk = Files.walk(path)) {
            Stream<Path> data = walk.filter(p -> !Files.isDirectory(p)).filter(p -> isMicrosoftFile(p.getFileName().toString()));
            List<FileDetailDTO> fileListDetail = data 
                            .map(p -> getFileDetails(p)) 
                            .collect(Collectors.toList());
            return fileListDetail;
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * explain 3
     * @function
     * @param
     */
    private Boolean isMicrosoftFile(String fileName) {
        return this.MS_FILE_EXSTENTIONS
                    .stream()
                    .anyMatch(exstention ->  fileName.toLowerCase().endsWith("." + exstention.toLowerCase()));
    }

    /**
     * explain 2
     * @function
     * @param
     */
    private FileDetailDTO getFileDetails(Path path) {
        try {
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            return new FileDetailDTO(
                path.getFileName().toString(),
                String.valueOf(attributes.size()) + " KB",
                getFormatDate(attributes.creationTime().toString()),
                getFormatDate(attributes.lastModifiedTime().toString()),
                path,
                UUID.randomUUID()
            );

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * explain 1
     * @function
     * @param
     */
    private String getFormatDate(String dateString){
        LocalDateTime date = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME);
        date = date.plus(5, ChronoUnit.HOURS);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        return date.format(formatter);
    }

    /***
     * @handleSaveFiles after retrieveAllFiles want  to save to DB -> report
     * @throws Exception
     */
    @Override
    public void handleSaveFiles() throws Exception {
        if(Objects.isNull(AppData.listFiles) || AppData.listFiles.isEmpty()) {
            throw new Exception("No file provided");
        }
        try(FileWriter writer = new FileWriter(DATABASE_PATH)) {
            for(FileDetailDTO file : AppData.listFiles){
                writer.write(file.toString());
            }
            AppData.reset();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }
    @Override
    public void handleDeleteFile(UUID id) throws Exception {
        FileDetailDTO fileToDelete = findFilesById(AppData.listFiles, id);
        try {
            Files.delete(fileToDelete.getFilePath());
            AppData.listFiles.remove(fileToDelete);
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }

    /***
     * before expain @getReport need to know @getFileList and @readFile
     * @return
     * @throws Exception
     */
    @Override
    public List<FileDetailDTO> getReport() throws Exception {
        String records = readFile();
        if(records.isBlank() || records.isEmpty()) return null;
        return getFileList(records);
    }

    /***
     * before expain @handleDeleteReport need to know
     *                                                 @getFileList
     *                                                 @readFile
     *                                                 @findFilesById
     *
     * @param id
     * @throws Exception
     */
    @Override
    public void handleDeleteReport(UUID id) throws Exception {
        String records = readFile();
        if(records.isBlank() || records.isEmpty()) return ;
        List<FileDetailDTO> files = getFileList(records);

        FileDetailDTO fileToDelete = findFilesById(files,id);

        if(!Objects.isNull(fileToDelete)) files.remove(fileToDelete);
       try(FileWriter writer = new FileWriter(DATABASE_PATH)) {
           for(FileDetailDTO file : files){
               writer.write(file.toString());
           }
       } catch (Exception e) {
           throw new Exception(e.getMessage());
       }
    }

    /***
     * @getFileList for get record
     * @param records
     * @return
     */
    private List<FileDetailDTO> getFileList(String records){

        List<FileDetailDTO> files = new ArrayList<>();
        for(String record : records.split("&&")){
            String[] contents = record.split("&");
            UUID id = UUID.fromString(contents[0].split("=")[1]);
            String fileName = contents[1].split("=")[1];
            String size = contents[2].split("=")[1];
            String createDate = contents[3].split("=")[1];
            String updateDate = contents[4].split("=")[1];
            files.add(new FileDetailDTO(fileName,size,createDate,updateDate,null,id));
        }
        return files;
    }

    private String readFile() throws Exception {
        String records = "";
        try {
            FileReader reader = new FileReader(DATABASE_PATH);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                records += line;
            }
            bufferedReader.close();
            reader.close();
            return records;
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }

    private FileDetailDTO findFilesById(List<FileDetailDTO> files,UUID id){

        for(FileDetailDTO file : files) {
            if(file.getId().equals(id)) return file;
        }
        return null;
        
    }
}
