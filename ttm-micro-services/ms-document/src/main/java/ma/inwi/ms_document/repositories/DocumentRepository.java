package ma.inwi.ms_document.repositories;

import ma.inwi.ms_document.entities.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {

    @Query("SELECT d FROM Document d WHERE d.authorName = :authorName AND d.deleted = false")
    List<Document> findDocumentByAuthorName(@Param("authorName") String authorName);

}

