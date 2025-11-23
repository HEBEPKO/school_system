//package school.controller;
//
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
////import school.service.GoogleSheetsService;
//
//@Controller
//@RequiredArgsConstructor
//public class JuornalController {
//    private final GoogleSheetsService googleSheetsService;
//
//    @GetMapping("/journal/{classId}")
//    public String journal(@PathVariable Long classId, Model model) {
//        model.addAttribute("classId", classId);
//
//        return "journal/view";
//    }
//
//    @GetMapping("/journal/gsheets/{classId}")
//    public String googleSheetsIntegration(@PathVariable Long classId, Model model) {
//        try {
//            String sheetUrl = googleSheetsService.initClassJournal(classId);
//            model.addAttribute("sheetUrl", sheetUrl);
//            return "/gsheets";
//        } catch (Exception e) {
//            model.addAttribute("error", "Ошибка интеграции с Google Sheets");
//            return "error";
//        }
//    }
//}
