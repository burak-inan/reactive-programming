package com.project.reactiveProgramming.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ContactMessageRequest {

    @NotNull(message = "Please enter name")
    @Size(min = 4,max = 16,message = "Your name should be at least 4 characters")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+",message = "Your message must consist of the character .")
    private String name;

    @Email(message = "Please enter valid email")
    @Size(min = 5,max = 20,message = "Your email should be at least 5 characters")
    @NotNull(message = "Please enter your email")
    private String email;

    @NotNull(message = "Please enter subject")
    @Size(min = 4, max = 50, message = "Your subject should be at least 4 characters")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+" ,message="Your message must consist of the characters .")
    private String subject;

    @NotNull(message = "Please enter message ")
    @Size(min = 4, max = 50, message = "Your subject should be at least 16 characters")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+" ,message="Your message must consist of the characters .")
    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern= "yyyy-MM-dd")
    private LocalDate date;
}
