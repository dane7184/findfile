package rupp.com.kh.dto;

import java.nio.file.Path;
import java.util.UUID;

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
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCreateDate() {
        return createDate;
    }
    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
    public String getUpdateDate() {
        return updateDate;
    }
    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
    public Path getFilePath() {
        return filePath;
    }
    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }
    public void setId(UUID id){
        this.id = id;
    }
    public UUID getId(){
        return id;
    }
    public String getIcon(){
        return icon;
    }
    public void setIcon(String icon){
        this.icon = icon;
    }

    public String toString(){
        return String.format("id=%s&fileName=%s&size=%s&createDate=%s&updateDate=%s&&"
                ,id,fileName,size,createDate,updateDate);
    }
}
