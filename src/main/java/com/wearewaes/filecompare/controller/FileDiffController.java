package com.wearewaes.filecompare.controller;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wearewaes.filecompare.model.FileDiff;
import com.wearewaes.filecompare.service.FileDiffService;

@RestController
@RequestMapping("v1/diff")
public class FileDiffController {

    @Autowired
    private FileDiffService fileDiffService;

    /**
     * Service that adds a file on the left or right position.
     *
     * @param id File identificator, if doesn`t exist will create one otherwise update it.
     * @param side String "left" or "right". Will replace current one if exist.
     * @param base64file JSON base64 encoded binary data.
     * @return
     */
    @RequestMapping(value = { "/{id:\\d+}/{side:left}", "/{id:\\d+}/{side:right}" }, method = RequestMethod.POST, consumes = "application/json;charset=UTF-8")
    public ResponseEntity<String> addFile(@PathVariable("id") Long id, @PathVariable("side") String side, @RequestBody String base64file) {

        if ("left".equals(side)) {
            fileDiffService.addLeft(id, Base64.decodeBase64(base64file));
        } else if ("right".equals(side)) {
            fileDiffService.addRight(id, Base64.decodeBase64(base64file));
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * <pre>
     * This service will return a {@link ResponseEntity} with a JSON representation of the differences between both files in a given {@link FileDiff} id.
     *
     *
     *
     * If given id doesn't exist the return will be a {@link ResponseEntity} with {@link HttpStatus#NOT_FOUND}.
     * <pre>
     *
     * @param id {@link FileDiff} identificator
     */
    @RequestMapping(value = "/{id:\\d+}", method = RequestMethod.GET)
    public ResponseEntity<String> diff(@PathVariable("id") Long id) {

        ResponseEntity<String> response;
        final FileDiff fileDiff = this.fileDiffService.fileDiff(id);

        if (fileDiff == null) {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            response = new ResponseEntity<>(fileDiff.resultAsJson().toString(), HttpStatus.OK);
        }

        return response;
    }



}
