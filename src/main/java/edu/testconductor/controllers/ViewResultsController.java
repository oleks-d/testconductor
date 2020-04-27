package edu.testconductor.controllers;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Table;
import edu.testconductor.domain.Exam;
import edu.testconductor.domain.StudentGroup;
import edu.testconductor.domain.StudentSession;
import edu.testconductor.domain.User;
import edu.testconductor.repos.ExamsRepo;
import edu.testconductor.repos.GroupsRepo;
import edu.testconductor.repos.StudentSessionRepo;
import edu.testconductor.repos.UserRepo;
import edu.testconductor.services.EmailServiceImpl;
import javafx.scene.text.Font;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;


@Controller
public class ViewResultsController {

    private static Logger logger = LoggerFactory.getLogger(ViewResultsController.class);

    @Autowired
    private StudentSessionRepo sessionsRepo;

    @Autowired
    private ExamsRepo examsRepo;

    @Autowired
    private GroupsRepo groupsRepo;

    /*
     @RequestMapping (value = "/submit/id/{id}", method = RequestMethod.GET,
 produces="text/xml")
public String showLoginWindow(@PathVariable("id") String id,
                              @RequestParam(value = "logout", required = false) String logout,
                              @RequestParam("name") String username,
                              @RequestParam("password") String password,
                              @ModelAttribute("submitModel") SubmitModel model,
                              BindingResult errors) throws LoginException {...}

     */
    @PreAuthorize("hasAuthority(1)")
    @GetMapping(value = "/results")
    public ModelAndView viewResults(@RequestParam("examID") Optional<Long> examID, @RequestParam("userEmail") Optional<String> userEmail, @RequestParam("groupID") Optional<Long> groupID) {

        Iterable<Exam> exams = examsRepo.findAllByOrderByStartDateTimeDesc();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("exams", exams);
        if (examID.isPresent()) {
            Iterable<StudentSession> results = sessionsRepo.findAllByExamId(examID.get());
            params.put("selectedExamID", examID.get());
            params.put("results", results);

        }
        if (userEmail.isPresent()) {
            Iterable<StudentSession> results = sessionsRepo.findAllByEmail(userEmail.get());
            //params.put("selectedExamID", examID.get());
            params.put("results", results);

        }
        if (groupID.isPresent()) {
            String groupName = groupsRepo.getOne(groupID.get()).getGroupName();
            Iterable<StudentSession> results = sessionsRepo.findAllByGroupName(groupName);
            params.put("selectedGroup", groupName);
            params.put("results", results);

        }
        Iterable<StudentGroup> groups = groupsRepo.findAll();
        params.put("groups", groups);
        return new ModelAndView("results", params);
    }

    @PreAuthorize("hasAuthority(1)")
    @PostMapping(value = "/results")
    public ModelAndView sortResults(@RequestParam Long examID) {

        Iterable<StudentSession> results = sessionsRepo.findAllByExamId(examID);

        Iterable<Exam> exams = examsRepo.findAllByOrderByStartDateTimeDesc();
        Iterable<StudentGroup> groups = groupsRepo.findAll();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("selectedExamID", examID);
        params.put("groups", groups);
        params.put("results", results);
        params.put("exams", exams);
        return new ModelAndView("results", params);
    }


//    @RequestMapping(path = "/print_results", method = RequestMethod.POST)
//    public ResponseEntity<Resource> download(@RequestParam Map<String, String> allParameters) throws IOException {
//
//        String result = "Ім'я \t\t\t Група \t\t\t Тема \t\t\t Час здачі \t\t\t Результат <hr>";
//        for (String key : allParameters.keySet()) {
//            if (key.equals("_csrf"))
//                continue;
//            result = result + "\n\n" + allParameters.get(key).replace(" -- " , "\t\t\t");
//        }
//
//
//
//        ByteArrayResource resource = new ByteArrayResource(result.getBytes());
//
//        HttpHeaders headers = new HttpHeaders() ;
//        headers.add("Content-Type", "text/html; charset=utf-8");
//        headers.setContentType(new MediaType("text", "plain", StandardCharsets.UTF_8));
//        return ResponseEntity.ok()
//                .headers(headers)
//                //.contentLength(result.length())
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(resource);
//    }

    @RequestMapping(path = "/print_results", method = RequestMethod.POST)
    public void download(@RequestParam Map<String, String> allParameters, HttpServletResponse response) throws IOException, URISyntaxException {

        List<List<String>> result = new ArrayList<>();
        ArrayList<String> title = new ArrayList<>();
        title.add("Ім'я");
        title.add("Група");
        title.add("Тема");
        title.add("Час здачі");
        title.add("Результат");
        result.add(title);

        for (String key : allParameters.keySet()) {
            if (key.equals("_csrf"))
                continue;
            String[] line = allParameters.get(key).split(" -- ");
            ArrayList<String> lineList = new ArrayList<>();
            lineList.addAll(Arrays.asList(line));
            result.add(lineList);
        }

        ByteArrayOutputStream pdfStream = createPDF(result);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", String.format("inline; filename=\"results.pdf\""));

        //InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(result.getBytes()));
        byte[] pdfData = pdfStream.toByteArray();
        InputStream inputStream = new ByteArrayInputStream(pdfData);
        FileCopyUtils.copy(inputStream, response.getOutputStream());


    }

    private ByteArrayOutputStream createPDF(List<List<String>> body) throws URISyntaxException, IOException {

        ByteArrayOutputStream result = new ByteArrayOutputStream();


        PdfWriter writer = new PdfWriter(result);

        PdfDocument pdfDoc = new PdfDocument(writer);

        pdfDoc.addNewPage();

        InputStream file = ViewResultsController.class.getClassLoader().getResourceAsStream("/static/vnmu-logo.png");

        //String file = "./src/main/resources/static/vnmu-logo.png";
        ImageData data = ImageDataFactory.create(IOUtils.toByteArray(file));
        Image img = new Image(data);

        img.scale(0.3f,0.3f);
        //img.setFixedPosition(300, PageSize.A4.getHeight()-img.getImageScaledHeight());

        float [] pointColumnWidths = {250F, 50F, 250F, 150F, 50F};
        Table table = new Table(pointColumnWidths);
        //table.setFixedPosition(1f, img.getImageScaledHeight(), PageSize.A4.getWidth());
        //String FONT_FILENAME =  IOUtils.toString(ViewResultsController.class.getClassLoader().getResourceAsStream("./static/fonts/arial.ttf"));//"./src/main/resources/static/fonts/arial.ttf";
        byte[] fontData = IOUtils.toByteArray(ViewResultsController.class.getClassLoader().getResourceAsStream("/static/fonts/arial.ttf"));
        PdfFont font = PdfFontFactory.createFont(fontData, PdfEncodings.IDENTITY_H);


        for(List<String> item : body){
            for(String cellText : item) {
                Cell cell = new Cell();
                cell.add(cellText);
                table.addCell(cell);
            }
        }


        Document document = new Document(pdfDoc);
        document.setFont(font);

        document.add(img);
        document.add(table);
        document.close();

        return result;
    }

//
//    private ByteArrayOutputStream createPDF(String body) throws IOException, URISyntaxException {
//        ByteArrayOutputStream result = new ByteArrayOutputStream();
//        PDDocument document = new PDDocument();
//
////        File file = new File("C:/PdfBox_Examples/my_doc.pdf");
////        PDDocument document = PDDocument.load(file);
//
//        document.addPage(new PDPage());
//        PDPage page = document.getPage(0);
//        PDPageContentStream contentStream = new PDPageContentStream(document, page);
//
//        String file = ViewResultsController.class.getClassLoader().getResource("./static/vnmu-logo.png").toURI().getPath();
//        PDImageXObject pdImage = PDImageXObject.createFromFile(file, document);
//
//        contentStream.drawImage(pdImage, 70, 250);
//
//        //Begin the Content stream
// //       contentStream.beginText();
//
//        //Setting the font to the Content stream
////        PDFont font = PDType0Font.load(document, new FileInputStream("c:/windows/fonts/arial.ttf"), false);
////        contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
//
//        //Setting the position for the line
// //       contentStream.newLineAtOffset(25, 500);
//
//        String text = "ВНМУ Результати";
//
//        //Adding text in the form of string
////        contentStream.showText(text);
////        contentStream.newLine();
////        contentStream.showText(body);
//
//        //Ending the content stream
////        contentStream.endText();
//
//        //Closing the content stream
//        contentStream.close();
//
//        document.save(result);
//        return result;
//    }

}