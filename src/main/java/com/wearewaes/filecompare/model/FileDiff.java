package com.wearewaes.filecompare.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Holds two arrays of bytes left and right to be compared to each other.
 *
 * @author tmarques
 *
 */
public class FileDiff {

    private byte[] left;
    private byte[] right;

    public void setLeft(final byte[] left) {
        this.left = left;
    }

    public void setRight(final byte[] right) {
        this.right = right;
    }

    /**
     * @return {@link DiffResult} between {@link FileDiff#left} and {@link FileDiff#right};
     * @throws IllegalArgumentException If {@link FileDiff#left} or {@link FileDiff#right} is null.
     */
    public DiffResult diff() {

        if (this.left == null || this.right == null)
            throw new IllegalArgumentException("Both left and right must be setted (not null) to run diff.");

        DiffResult diff = DiffResult.EQUALS;
        if (!Arrays.equals(left, right)) {
            if (left.length != right.length) {
                diff = DiffResult.DIFERENT_SIZE;
            } else {
                diff = DiffResult.SAME_SIZE_NOT_EQUALS;
            }
        }

        return diff;
    }

    /**
     * @return A map where the key is the corresponding offset that are different between both files and the value corresponding to the lenght of the offset
     * @throws IllegalArgumentException
     *             If the diff between both left and right is not equals to {@link DiffResult#SAME_SIZE_NOT_EQUALS}. Also if any of left or right files are not setted
     */
    public Map<Integer, Integer> result() {

        if (!DiffResult.SAME_SIZE_NOT_EQUALS.equals(this.diff()))
            throw new IllegalArgumentException("Files left and right must have the diff equals to " + DiffResult.SAME_SIZE_NOT_EQUALS);

        final Map<Integer, Integer> offsets = new HashMap<>();

        final AtomicBoolean isNextDiffOffset = new AtomicBoolean(true);
        final AtomicInteger currentOffset = new AtomicInteger();

        IntStream.range(0, left.length).forEach(i -> {
            if (left[i] != right[i]) {
                if (isNextDiffOffset.get()) {
                    currentOffset.set(i);
                    offsets.put(currentOffset.get(), 1);
                    isNextDiffOffset.set(false);
                } else {
                    int length = offsets.get(currentOffset.get());
                    offsets.put(currentOffset.get(), ++length);
                }
            } else if (!isNextDiffOffset.get()) {
                isNextDiffOffset.set(true);
            }
        });

        return offsets;
    }

    /**
     * <pre>
     * JSON representation of the diff of both left and right filef:
     *
     * When {@link DiffResult#DIFERENT_SIZE}
     *  {
     *      "message": "Files have different size."
     *  }
     *
     * When {@link DiffResult#EQUALS}
     *  {
     *      "message": "Files are the same."
     *  }
     *
     * When {@link DiffResult#SAME_SIZE_NOT_EQUALS}
     *  {
     *      "<offset>": <lenght>,
     *      "<offset>": <lenght>,
     *      ....
     *  }
     * </pre>
     *
     * @return
     */
    public JsonObject resultAsJson() {

        JsonObject jObj = new JsonObject();

        try {
            switch (this.diff()) {
            case DIFERENT_SIZE:
                jObj.addProperty("message", "Files have different size.");
                break;

            case EQUALS:
                jObj.addProperty("message", "Files are the same.");
                break;

            case SAME_SIZE_NOT_EQUALS:
                final Map<Integer, Integer> result = this.result();
                final Gson gson = new Gson();
                jObj = new JsonParser().parse(gson.toJson(result)).getAsJsonObject();
                break;

            default:
                break;
            }
        } catch (final IllegalArgumentException e) {
            jObj.addProperty("message", e.getMessage());
        }


        return jObj;
    }


}
