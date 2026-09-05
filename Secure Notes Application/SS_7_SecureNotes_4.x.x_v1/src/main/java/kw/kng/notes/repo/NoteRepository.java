package kw.kng.notes.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kw.kng.notes.entities.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByOwnerUsername(String ownerUsername);
}