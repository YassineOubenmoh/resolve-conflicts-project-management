package ma.inwi.msproject.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "project")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ownerUsername;

    private String ownerFullName;

    @Column(unique = true)
    private String title;

    private String description;
    private String marketType;
    private String projectType;
    private String ttmComitteeSubCategory;
    private String subcategoryCommercialCodir;
    private Boolean isConfidential;
    private Date dateStartTtm;

    //Files uploaded Names
    private String expressionOfNeed;

    //Uploaded File name
    private String briefCommunication;

    //Uploaded File name
    private String briefCDG;

    //Uploaded File name
    private String regulatoryBrief;

    //Files uploaded Names
    @ElementCollection
    @CollectionTable(name = "project-attachedDocuments", joinColumns = @JoinColumn(name = "project-id"))
    @Column(name = "attachedDocuments")
    private Set<String> attachedDocuments;

    @ElementCollection
    @CollectionTable(name = "project-comments", joinColumns = @JoinColumn(name = "project-id"))
    @Column(name = "comments")
    private Set<String> comments;

    private LocalDateTime dateCreation;

    @Column(nullable = false)
    private boolean deleted = false;

    /*
    @JsonManagedReference("project_moa")
    @OneToMany(mappedBy = "project", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private Set<Moa> moas;

     */

    @ElementCollection
    @CollectionTable(name = "project-moas", joinColumns = @JoinColumn(name = "project-id"))
    @Column(name = "moas")
    private Set<String> moas;

    private Long trackingId;

    @ElementCollection
    @CollectionTable(name = "project-departements", joinColumns = @JoinColumn(name = "project-id"))
    @Column(name = "departments")
    private List<String> departments;


    @JsonManagedReference("project_gateProject")
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private Set<GateProject> gateProjects;


}