package com.wearewaes.filecompare.model;

/**
 * Represent the expeted result from a {@link Differentiator} implementation.
 *
 * @author Tiago
 *
 */
public enum DiffResult {

    /**
     * When objects being compared are equals.
     */
    EQUALS,

    /**
     * When objects being compared have different size.
     */
    DIFERENT_SIZE,

    /**
     * When objects being compared have the same size but are not equals.
     */
    SAME_SIZE_NOT_EQUALS;
}
