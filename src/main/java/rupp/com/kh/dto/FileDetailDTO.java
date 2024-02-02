package rupp.com.kh.dto;

import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.util.UUID;

@Setter
@Getter
public class FileDetailDTO {
    private String fileName;
    private String size;
    private String createDate;
    private String updateDate;
    private Path filePath;
    private UUID id;
    private String icon;

    public FileDetailDTO(){};

    public FileDetailDTO(String fileName, String size, String createDate, String updateDate, Path filePath,UUID id) {
        this.fileName = fileName;
        this.size = size;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.filePath = filePath;
        this.id = id;
        String[] arrOfFileNames = fileName.split("\\.");
        this.icon = "images/"  + arrOfFileNames[arrOfFileNames.length-1] + ".png";
    }

    public String toString(){
        return String.format("id=%s&\tfileName=%s\t\t\t\t\t\t&size=%s\t\t\t&createDate=%s\t\t&updateDate=%s\t&&\n"
                ,id,fileName,size,createDate,updateDate);
    }
}
