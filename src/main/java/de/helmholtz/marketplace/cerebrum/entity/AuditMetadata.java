package de.helmholtz.marketplace.cerebrum.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PROTECTED)
public class AuditMetadata
{
    @Schema(description = "Creation date of a Service", example = "2020-02-19")
    @CreatedDate
    private long createdDate;

    @Schema(description = "Date of last modification", example = "2020-03-24")
    @LastModifiedDate
    private long lastModifiedDate;
}
