package com.heliorodri.springwebfluxpoc.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@With
@Table("movie")
public class Movie {

    @Id
    private Integer id;
    @NotNull(message = "The name of the movie cannot be blank")
    private String name;

}