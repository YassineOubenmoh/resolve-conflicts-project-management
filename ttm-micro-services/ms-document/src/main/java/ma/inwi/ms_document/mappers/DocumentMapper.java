package ma.inwi.ms_document.mappers;

import ma.inwi.ms_document.dtos.DocumentDto;
import ma.inwi.ms_document.entities.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(target = "projectId", source = "projectId")
    @Mapping(target = "requiredActionId", source = "requiredActionId")
    DocumentDto documentToDocumentDto(Document document);

    @Mapping(target = "projectId", source = "projectId")
    @Mapping(target = "requiredActionId", source = "requiredActionId")
    Document documentDtoToDocument(DocumentDto documentDto);
}
