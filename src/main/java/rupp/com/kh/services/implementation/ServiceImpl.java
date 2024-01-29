package rupp.com.kh.services.implementation;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.Collections;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.ArrayList;

import java.io.IOException;
import java.nio.file.Files; 
import java.io.FileReader;
import java.io.BufferedReader;
import java.nio.file.Path; 
import java.io.FileWriter;
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
import rupp.com.kh.storages.AppData;

@Service
public class ServiceImpl implements AppService {

    private final String databasePath = "src/main/java/rupp/com/kh/db/database.txt"; 
    /** 
    * MS_FILE_EXSTENTION store all files exstention that allowed. 
    * Final Keyword use for protect variable PATH can't be change. 
    */ 
    private final List<String> MS_FILE_EXSTENTIONS = Collections.unmodifiableList(new ArrayList<>(Arrays.asList("docx", "xlsx", "pptx", "pdf")));

    /**
     * @function
     * @param
     */
    @Override
    public List<User> getGoupMembers() {
        return Arrays.asList(
            new User("Sann Rayuth",Gender.MALE,"images/group4/student.webp","(ប្រធាន)"),
            new User("Ly Chyleng",Gender.MALE,"images/group4/chyleng.jpg","(អនុប្រធាន)"),
            new User("San Putchhay",Gender.MALE,"images/group4/chhay.JPG","(Write Book)"),
            new User("Ly Horleng",Gender.MALE,"images/group4/Horleng.JPG","(CODE)"),
            new User("Lim Dane",Gender.MALE,"images/group4/Dane.jpg","(CODE)"),
            new User("Rinn Rozajesmine",Gender.MALE,"images/group4/student.webp","(Design Book)"),
            new User("Yon Yii",Gender.MALE,"images/group4/yii.jpg","(Make Slide)"),
            new User("Huy Bochhy",Gender.MALE,"images/group4/bochy.jpg","(Make Slide)")
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
     * @function
     * @param
     */
    private Boolean isMicrosoftFile(String fileName) {
        return this.MS_FILE_EXSTENTIONS
                    .stream()
                    .anyMatch(exstention ->  fileName.toLowerCase().endsWith("." + exstention.toLowerCase()));
    }

    /**
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
     * @function
     * @param
     */
    private String getFormatDate(String dateString){
        LocalDateTime date = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME);
        date = date.plus(5, ChronoUnit.HOURS);
        // Create a custom date and time formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy, h:mm:ss a");
        // Format LocalDateTime to the desired string format
        return date.format(formatter);
    }

    @Override
    public Boolean handleDeleteFile(UUID id) throws Exception {
        for(FileDetailDTO file : AppData.listFiles) {
            if(id.equals(file.getId())) {
                try {
                    Files.delete(file.getFilePath());
                    AppData.listFiles.remove(file);
                    return true;
                } catch (IOException e) {
                    throw new Exception(e.getMessage());
                }
            }
        }
        return false;
    }

    @Override
    public Boolean handleSaveFiles() throws Exception {
        if(Objects.isNull(AppData.listFiles) || AppData.listFiles.isEmpty()) {
            throw new Exception("No file provided");
        }
        try(FileWriter writer = new FileWriter(databasePath)) {
            for(FileDetailDTO file : AppData.listFiles){
                writer.write(file.toString());
            }
            AppData.reset();
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    @Override
    public List<FileDetailDTO> getReport() throws Exception {
        String records = "";
        try {
            FileReader reader = new FileReader(databasePath);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                records += line;
            }
            bufferedReader.close();
            reader.close();
            if(records.isBlank() || records.isEmpty()) return null;
            return getFileList(records);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

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


    
}
