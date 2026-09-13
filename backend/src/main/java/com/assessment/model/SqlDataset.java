package com.assessment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "sql_datasets")
public class SqlDataset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnore
    private Question question;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String schemaDescription;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String schemaDdl;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String seedDataSql;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String referenceQuery;

    private boolean orderRequired = false;

    @Column(columnDefinition = "TEXT")
    private String sampleOutputJson;

    public SqlDataset() {}

    public SqlDataset(Question question, String schemaDescription, String schemaDdl, String seedDataSql, String referenceQuery, boolean orderRequired, String sampleOutputJson) {
        this.question = question;
        this.schemaDescription = schemaDescription;
        this.schemaDdl = schemaDdl;
        this.seedDataSql = seedDataSql;
        this.referenceQuery = referenceQuery;
        this.orderRequired = orderRequired;
        this.sampleOutputJson = sampleOutputJson;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public String getSchemaDescription() { return schemaDescription; }
    public void setSchemaDescription(String schemaDescription) { this.schemaDescription = schemaDescription; }

    public String getSchemaDdl() { return schemaDdl; }
    public void setSchemaDdl(String schemaDdl) { this.schemaDdl = schemaDdl; }

    public String getSeedDataSql() { return seedDataSql; }
    public void setSeedDataSql(String seedDataSql) { this.seedDataSql = seedDataSql; }

    public String getReferenceQuery() { return referenceQuery; }
    public void setReferenceQuery(String referenceQuery) { this.referenceQuery = referenceQuery; }

    public boolean isOrderRequired() { return orderRequired; }
    public void setOrderRequired(boolean orderRequired) { this.orderRequired = orderRequired; }

    public String getSampleOutputJson() { return sampleOutputJson; }
    public void setSampleOutputJson(String sampleOutputJson) { this.sampleOutputJson = sampleOutputJson; }
}
