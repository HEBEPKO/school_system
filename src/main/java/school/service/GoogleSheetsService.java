//package school.service;
//
//import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.services.sheets.v4.Sheets;
//import com.google.api.services.sheets.v4.SheetsScopes;
//import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
//import school.model.SchoolClass;
//import school.repository.ClassRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.security.GeneralSecurityException;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class GoogleSheetsService {
//
//    @Value("${google.credentials.file}")
//    private String credentialsFile;
//
//    private final ClassRepository classRepository;
//
//    public String initClassJournal(Long classId) throws IOException, GeneralSecurityException {
//        SchoolClass aClass = classRepository.findById(classId)
//                .orElseThrow(() -> new RuntimeException("Класс не найден"));
//
//        if (aClass.getGoogleSheetId() != null) {
//            return "https://docs.google.com/spreadsheets/d/"  + aClass.getGoogleSheetId();
//        }
//
//        Sheets sheetsService = createSheetsService();
//
//        // Создание нового Google Sheets
//        com.google.api.services.sheets.v4.model.Spreadsheet spreadsheet = new com.google.api.services.sheets.v4.model.Spreadsheet()
//                .setProperties(new SpreadsheetProperties()
//                        .setTitle("Журнал класса " + aClass.getName()));
//
//        com.google.api.services.sheets.v4.model.Spreadsheet created = sheetsService.spreadsheets().create(spreadsheet).execute();
//
//        aClass.setGoogleSheetId(created.getSpreadsheetId());
//        classRepository.save(aClass);
//
//        return "https://docs.google.com/spreadsheets/d/"  + created.getSpreadsheetId();
//    }
//
//    private Sheets createSheetsService() throws IOException, GeneralSecurityException {
//        NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//        JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
//
//        GoogleCredential credentials = GoogleCredential.fromStream(new FileInputStream(credentialsFile))
//                .createScoped(List.of(SheetsScopes.SPREADSHEETS));
//
//        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentials)
//                .setApplicationName("E-School Journal")
//                .build();
//    }
//}