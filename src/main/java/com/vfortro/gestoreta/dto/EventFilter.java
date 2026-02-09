package com.vfortro.gestoreta.dto;

import lombok.*;

import java.time.Instant;

public record EventFilter (
     String title,
     Long tagId,
     Boolean isPublic,
     Boolean isDone,
     Float price,
     Instant date
) {}
