package mochegov.accounting.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

@Slf4j
@Data
@Embeddable
public class Document {

    @Column(name = "DOCUMENT_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    @Column(name = "DOC_SERIES", columnDefinition = "VARCHAR(10) NOT NULL")
    private String series;

    @Column(name = "DOC_NUMBER", columnDefinition = "VARCHAR(20) NOT NULL")
    private String number;

    @Column(name = "DATE_ISSUE", columnDefinition = "DATE NOT NULL")
    private Date dateIssue;

    @Column(name = "WHO_ISSUED", columnDefinition = "VARCHAR(128) NOT NULL")
    private String whoIssued;

    public Document() {}

    public Document(DocumentType documentType, String series, String number, Date dateIssue, String whoIssued) {
        this.documentType = documentType;
        this.series = series;
        this.number = number;
        this.dateIssue = dateIssue;
        this.whoIssued = whoIssued;
    }
}