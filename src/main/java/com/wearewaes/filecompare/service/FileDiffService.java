package com.wearewaes.filecompare.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.wearewaes.filecompare.model.FileDiff;

@Service
public class FileDiffService {

    private final Map<Long, FileDiff> filesDiff = new HashMap<>();

    /**
     * @return A new {@link FileDiff} if there is none on the given id position or the already existing one if there is.
     */
    private FileDiff createAndReturnFileDiff(final Long id) {

        FileDiff fileDiff = this.filesDiff.get(id);
        if (fileDiff == null) {
            fileDiff = new FileDiff();
        }
        return fileDiff;
    }

    /**
     * Create a {@link FileDiff} if doesn't exist yet and update the left file
     *
     * @param id {@link FileDiff} identificator
     * @param fileBytes
     */
    public void addLeft(final Long id, final byte[] fileBytes) {
        final FileDiff fileDiff = createAndReturnFileDiff(id);
        fileDiff.setLeft(fileBytes);
        this.filesDiff.put(id, fileDiff);
    }

    /**
     * Create a {@link FileDiff} if doesn't exist yet and update the right file
     *
     * @param id {@link FileDiff} identificator
     * @param fileBytes
     */
    public void addRight(final Long id, final byte[] fileBytes) {
        final FileDiff fileDiff = createAndReturnFileDiff(id);
        fileDiff.setRight(fileBytes);
        this.filesDiff.put(id, fileDiff);
    }

    /**
     * @param id
     * @return {@link FileDiff} with the given id. Null if doesn't exist.
     */
    public FileDiff fileDiff(Long id) {
        return this.filesDiff.get(id);
    }

}
