package com.vfortro.gestoreta.specification;

import com.vfortro.gestoreta.model.Event;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public class EventSpecifications {
    public static Specification<Event> isFromFalla(Long fallaId) {
        return (root, query, cb) ->
                fallaId == null ? null : cb.equal(root.get("eventTag").get("id"), fallaId);
    }

    public static Specification<Event> hasTitle(String title) {
        return (root, query, cb) ->
                title == null ? null : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Event> hasEventTag(Long tagId) {
        return (root, query, cb) ->
                tagId == null ? null : cb.equal(root.get("eventTag").get("id"), tagId);
    }

    public static Specification<Event> isPublic(Boolean publicField) {
        return (root, query, cb) ->
                publicField == null ? null : cb.equal(root.get("publicField"), publicField);
    }

    public static Specification<Event> isDone(Boolean done) {
        return (root, query, cb) ->
                done == null ? null : cb.equal(root.get("done"), done);
    }

    public static Specification<Event> hasPrice(Float price) {
        return (root, query, cb) ->
                price == null ? null : cb.equal(root.get("price"), price);
    }

    public static Specification<Event> isOnDate(Instant date) {
        return (root, query, cb) ->
                date == null ? null : cb.equal(root.get("date"), date);
    }
}

