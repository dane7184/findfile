package rupp.com.kh.services;

import java.util.List;
import java.util.UUID;

import rupp.com.kh.dto.FileDetailDTO;
import rupp.com.kh.dto.User;

public interface AppService {

    List<User> getGoupMembers();
    List<FileDetailDTO> retrieveAllFiles(String pathStr) throws Exception;
    Boolean handleDeleteFile(UUID id)  throws Exception;
    Boolean handleSaveFiles()  throws Exception ;
    List<FileDetailDTO> getReport() throws Exception;
    
}
