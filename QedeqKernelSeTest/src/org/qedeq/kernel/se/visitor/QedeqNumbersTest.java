/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2013,  Michael Meyling <mime@qedeq.org>.
 *
 * "Hilbert II" is free software; you can redistribute
 * it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 */

package org.qedeq.kernel.se.visitor;

import java.util.Random;

import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.kernel.se.visitor.QedeqNumbers;

/**
 * Test {@link QedeqNumbers} works correctly.
 *
 * @author Michael Meyling
 */
public class QedeqNumbersTest extends QedeqTestCase {

    private QedeqNumbers numbers;

    private double oldVisitPercentage;

    private QedeqNumbers old;

    /**
     * Constructor.
     */
    public QedeqNumbersTest() {
        super();
    }

    /**
     * Constructor.
     *
     * @param   name    Test case name.
     */
    public QedeqNumbersTest(String name) {
        super(name);
    }

    /**
     * Test constructor.
     */
    public void testConstructor() throws Exception {
        numbers = new QedeqNumbers(10, 21);
        assertEquals(0, numbers.getImportNumber());
        assertEquals(0, numbers.getAbsoluteChapterNumber());
        assertEquals(0, numbers.getChapterNumber());
        assertEquals(0, numbers.getAbsoluteSectionNumber());
        assertEquals(0, numbers.getSectionNumber());
        assertEquals(0, numbers.getSubsectionNumber());
        assertEquals(0, numbers.getNodeNumber());
        assertEquals(0, numbers.getAxiomNumber());
        assertEquals(0, numbers.getPropositionNumber());
        assertEquals(0, numbers.getPredicateDefinitionNumber());
        assertEquals(0, numbers.getFunctionDefinitionNumber());
        assertEquals(0, numbers.getRuleNumber());
        assertEquals((double) 0, numbers.getVisitPercentage(), 1e-10);
    }

    /**
     * Test hashCode and equals.
     */
    public void testHashCodeAndEquals() throws Exception {
        int remember = 0;
        numbers = new QedeqNumbers(11, 13);
        QedeqNumbers other = new QedeqNumbers(11, 13);
        assertEquals(numbers, other);
        assertEquals(numbers.hashCode(), other.hashCode());
        numbers.increaseImportNumber();
        assertFalse(numbers.equals(other));
        assertFalse(numbers.hashCode() == other.hashCode());
        other.increaseImportNumber();
        assertEquals(numbers, other);
        assertEquals(numbers.hashCode(), other.hashCode());
        for (int i = 0; i < 10; i++) {
            numbers.increaseImportNumber();
            other.increaseImportNumber();
        }
        assertEquals(numbers, other);
        assertEquals(numbers.hashCode(), other.hashCode());

        remember = numbers.getChapterNumber();
        numbers.increaseChapterNumber(4, true);
        assertEquals(remember + 1, numbers.getChapterNumber());
        assertFalse(numbers.equals(other));
        assertFalse(numbers.hashCode() == other.hashCode());
        other.increaseChapterNumber(4, true);
        assertEquals(numbers, other);
        assertEquals(numbers.hashCode(), other.hashCode());

        remember = numbers.getSectionNumber();
        numbers.increaseSectionNumber(8, true);
        assertEquals(remember + 1, numbers.getSectionNumber());
        assertFalse(numbers.equals(other));
        assertFalse(numbers.hashCode() == other.hashCode());
        other.increaseSectionNumber(8, true);
        assertEquals(numbers, other);
        assertEquals(numbers.hashCode(), other.hashCode());

        remember = numbers.getSubsectionNumber();
        numbers.increaseSubsectionNumber();
        assertEquals(remember + 1, numbers.getSubsectionNumber());
        assertFalse(numbers.equals(other));
        assertFalse(numbers.hashCode() == other.hashCode());
        other.increaseSubsectionNumber();
        assertEquals(numbers, other);
        assertEquals(numbers.hashCode(), other.hashCode());

        remember = numbers.getAxiomNumber();
        numbers.increaseAxiomNumber();
        assertEquals(remember + 1, numbers.getAxiomNumber());
        assertFalse(numbers.equals(other));
        assertFalse(numbers.hashCode() == other.hashCode());
        other.increaseAxiomNumber();
        assertEquals(numbers, other);
        assertEquals(numbers.hashCode(), other.hashCode());

        remember = numbers.getPredicateDefinitionNumber();
        numbers.increasePredicateDefinitionNumber();
        assertEquals(remember + 1, numbers.getPredicateDefinitionNumber());
        assertFalse(numbers.equals(other));
        assertFalse(numbers.hashCode() == other.hashCode());
        other.increasePredicateDefinitionNumber();
        assertEquals(numbers, other);
        assertEquals(numbers.hashCode(), other.hashCode());

        remember = numbers.getFunctionDefinitionNumber();
        numbers.increaseFunctionDefinitionNumber();
        assertEquals(remember + 1, numbers.getFunctionDefinitionNumber());
        assertFalse(numbers.equals(other));
        assertFalse(numbers.hashCode() == other.hashCode());
        other.increaseFunctionDefinitionNumber();
        assertEquals(numbers, other);
        assertEquals(numbers.hashCode(), other.hashCode());

        remember = numbers.getPropositionNumber();
        numbers.increasePropositionNumber();
        assertEquals(remember + 1, numbers.getPropositionNumber());
        assertFalse(numbers.equals(other));
        assertFalse(numbers.hashCode() == other.hashCode());
        other.increasePropositionNumber();
        assertEquals(numbers, other);
        assertEquals(numbers.hashCode(), other.hashCode());

        remember = numbers.getRuleNumber();
        numbers.increaseRuleNumber();
        assertEquals(remember + 1, numbers.getRuleNumber());
        assertFalse(numbers.equals(other));
        assertFalse(numbers.hashCode() == other.hashCode());
        other.increaseRuleNumber();
        assertEquals(numbers, other);
        assertEquals(numbers.hashCode(), other.hashCode());

    }

    /**
     * Test that {@link QedeqNumbers#getVisitPercentage()} is nondecreasing.
     */
    public void testGetVisitPercentage() throws Exception {
        Random generator = new Random(19580427);
        iterateAndCheck(generator);
        iterateAndCheck(generator);
        iterateAndCheck(generator);
        iterateAndCheck(generator);
        iterateAndCheck(generator);
        iterateAndCheck(generator);
    }

    /**
     * @param generator
     * @throws Exception
     */
    private void iterateAndCheck(Random generator) throws Exception {
        final int imports = generator.nextInt(10);
        final int chapters = generator.nextInt(20);
        numbers = new QedeqNumbers(imports, chapters);
        old = null;
        oldVisitPercentage = 0;
        checkVarious();
        for (int i = 0; i < imports; i++) {
            assertEquals(i, numbers.getImportNumber());
            numbers.increaseImportNumber();
            checkVarious();
        }
        for (int i = 0; i < chapters; i++) {
            final int sections = generator.nextInt(30);
            final boolean chapterNumbering = generator.nextBoolean();
            assertEquals(i, numbers.getAbsoluteChapterNumber());
            numbers.increaseChapterNumber(sections, chapterNumbering);
            assertEquals(i + 1, numbers.getAbsoluteChapterNumber());
            assertEquals(chapterNumbering, numbers.isChapterNumbering());
            checkVarious();
            for (int j = 0; j < sections; j++) {
                final int subsections = generator.nextInt(30);
                final boolean sectionNumbering = generator.nextBoolean();
                assertEquals(j, numbers.getAbsoluteSectionNumber());
                numbers.increaseSectionNumber(subsections, sectionNumbering);
                assertEquals(j + 1, numbers.getAbsoluteSectionNumber());
                assertEquals(sectionNumbering, numbers.isSectionNumbering());
                checkVarious();
                for (int k = 0; k < subsections; k++) {
                    final int kind = generator.nextInt(6);
                    switch (kind) {
                    case 0:
                        numbers.increaseSubsectionNumber();
                        break;
                    case 1:
                        numbers.increaseNodeNumber();
                        checkVarious();
                        numbers.increaseAxiomNumber();
                        break;
                    case 2:
                        numbers.increaseNodeNumber();
                        checkVarious();
                        numbers.increasePredicateDefinitionNumber();
                        break;
                    case 3:
                        numbers.increaseNodeNumber();
                        checkVarious();
                        numbers.increaseFunctionDefinitionNumber();
                        break;
                    case 4:
                        numbers.increaseNodeNumber();
                        checkVarious();
                        numbers.increasePropositionNumber();
                        break;
                    case 5:
                        numbers.increaseNodeNumber();
                        checkVarious();
                        numbers.increaseRuleNumber();
                        break;
                    default:
                        throw new RuntimeException("unknown case");
                    }
                    checkVarious();
                    assertEquals(k + 1, numbers.getSubsectionNumber()
                        + numbers.getNodeNumber());
                }
            }
        }
        numbers.setFinished(true);
        assertTrue(numbers.isFinished());
        assertTrue(Math.abs(100 - numbers.getVisitPercentage()) < 1e10);
    }

    public void checkVarious() throws Exception {
        assertFalse(numbers.isFinished());
        assertFalse(numbers.equals(old));
        if (old != null) {
            assertFalse(numbers.hashCode() == old.hashCode());
        }
        final QedeqNumbers compare = new QedeqNumbers(numbers);
        assertEquals(numbers, compare);
        assertEquals(numbers.hashCode(), compare.hashCode());
        assertEquals(numbers.toString(), compare.toString());
        assertFalse(oldVisitPercentage > numbers.getVisitPercentage());
        assertTrue(numbers.getVisitPercentage() >= 0);
        assertTrue(numbers.getVisitPercentage() <= 100);
        old = new QedeqNumbers(numbers);
    }

}
