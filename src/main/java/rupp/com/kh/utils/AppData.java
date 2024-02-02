package rupp.com.kh.utils;

import java.util.List;
import java.util.Objects;

import rupp.com.kh.dto.FileDetailDTO;

public class AppData {
    public static List<FileDetailDTO> listFiles;
    public static String errorMessage ;
    public static String currentPath ;

    public static void reset(){
        if(!Objects.isNull(listFiles)) listFiles.clear();
        errorMessage = null;
        currentPath = null;
    }

}
