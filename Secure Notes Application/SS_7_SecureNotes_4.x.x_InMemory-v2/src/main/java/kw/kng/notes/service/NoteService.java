package kw.kng.notes.service;

import java.util.List;

import kw.kng.notes.entities.Note;

public interface NoteService {
    Note createNoteForUser(String username, String content);

    Note updateNoteForUser(Long noteId, String content, String username);

    void deleteNoteForUser(Long noteId, String username);

    List<Note> getNotesForUser(String username);
}
