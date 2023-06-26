package com.project.reactiveProgramming.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table(name = "contact_message")
// TODO learn about serialization and de-serialization
public class ContactMessage implements Serializable {

    //TODO check all generation types and strategies
    @Id
    private Integer id;

    @NotNull
    private String name;

    private String email;

    private String subject;

    private String message;

    // e.g. 2025-06-05
    // MM ==> month, mm ==> minute
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern= "yyyy-MM-dd")
    private LocalDate date;
}
