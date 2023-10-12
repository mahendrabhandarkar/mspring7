package com.ks.mspring7.controller;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;

@RestController
@Controller
@RequestMapping
public class PDFController {

    @PostMapping("/createpdf")
    public void createPdf() {
        String title = "Learn here";
        String content = "Test Content";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15);
            Paragraph titlePara = new Paragraph(title);
            titlePara.setAlignment(Element.ALIGN_CENTER);
            document.add(titlePara);
        }catch(DocumentException de) {
            de.printStackTrace();
        }
    }
}
