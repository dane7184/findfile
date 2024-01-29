package rupp.com.kh.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import rupp.com.kh.dto.FormDTO;
import rupp.com.kh.services.AppService;
import rupp.com.kh.storages.AppData;

import java.util.Objects;
import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;




@Controller
public class AppController {

    
    @Autowired AppService service;
    
    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("users", service.getGoupMembers());
        return "index";
    }
    

    @GetMapping("/folder")
    public String handleUploadFile() {
        AppData.reset();
        return "folder-upload";
    }

    @PostMapping("folder/upload")
    public String handleUploadFolder(@ModelAttribute("formDTO")FormDTO formDTO) throws Exception {
        AppData.listFiles = service.retrieveAllFiles(formDTO.getPath());
        AppData.currentPath = formDTO.getPath();
        return "redirect:/folder/preview-file";
    }

    @GetMapping("/folder/preview-file")
    public String handlePreviewFiles(Model model) {
        if(Objects.isNull(AppData.listFiles) || AppData.listFiles.size() == 0) {
            return "redirect:/folder";
        }else {
            model.addAttribute("listFiles", AppData.listFiles);
            model.addAttribute("currentPath","Path : " + AppData.currentPath);
            model.addAttribute("total", "Total :" +  String.valueOf(AppData.listFiles.size()));
            return "preview-file";
        }
    }
    
    @GetMapping("/error-occurred")
    public String handleError(Model model) {
        model.addAttribute("message", AppData.errorMessage);
        return "error-occurred";
    }

    @GetMapping("/folder/file/d/{id}")
    public String handleDeleteFile(@PathVariable String id) throws Exception{
        System.out.println(id);
        return service.handleDeleteFile(UUID.fromString(id)) ? "redirect:/folder/preview-file" : "redirect:/error-occurred";
    }

    @GetMapping("folder/save")
    public String handleSaveFiles() throws Exception {
        return service.handleSaveFiles() ? "redirect:/report" : "redirect:/error-occurred" ;       
    }
    @GetMapping("/report")
    public String handleViewReport(Model model) throws Exception{
        model.addAttribute("files",service.getReport());
        return "report";
    }

}
