/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.module;

import org.qedeq.kernel.se.common.ErrorCodes;

/**
 * Error codes and messages for module package.
 *
 * @author  Michael Meyling
 */
public interface ModuleErrors extends ErrorCodes {

    /** Error code. */
    public static final int QEDEQ_MODULE_NOT_LOADED_CODE = 90500;

    /** Error message. */
    public static final String QEDEQ_MODULE_NOT_LOADED_TEXT
        = "QEDEQ module couldn't be loaded.";

    /** Error (or warning) number for: Node reference not found for. */
    public static final int NODE_REFERENCE_NOT_FOUND_CODE = 610007;

    /** Error (or warning) text for: Node reference not found for. */
    public static final String NODE_REFERENCE_NOT_FOUND_TEXT
        = "node reference not found for: ";


    /** Error (or warning) number for: node reference has more than one dot. */
    public static final int NODE_REFERENCE_HAS_MORE_THAN_ONE_DOT_CODE = 610011;

    /** Error (or warning) text for: Node reference not found for. */
    public static final String NODE_REFERENCE_HAS_MORE_THAN_ONE_DOT_TEXT
        = "node reference has more than one dots: ";


    /** Error (or warning) number for: node reference must have only one sub reference. */
    public static final int NODE_REFERENCE_MUST_HAVE_ONLY_ONE_SUB_REFERENCE_CODE = 610013;

    /** Error (or warning) text for: node reference must have only one sub reference. */
    public static final String NODE_REFERENCE_MUST_HAVE_ONLY_ONE_SUB_REFERENCE_TEXT
        = "node reference must have only one sub reference: ";


    /** Error (or warning) number for: node reference must have only one proof line reference. */
    public static final int NODE_REFERENCE_MUST_HAVE_ONLY_ONE_PROOF_LINE_REFERENCE_CODE = 610013;

    /** Error (or warning) text for: node reference must have only one sub reference. */
    public static final String NODE_REFERENCE_MUST_HAVE_ONLY_ONE_PROOF_LINE_REFERENCE_TEXT
        = "node reference must have only one proof line reference: ";


    /** Error (or warning) number for: Module reference not found for. */
    public static final int MODULE_REFERENCE_NOT_FOUND_CODE = 6100017;

    /** Error (or warning) text for: Module reference not found for. */
    public static final String MODULE_REFERENCE_NOT_FOUND_TEXT
        = "module reference not found for: ";


    /** Error (or warning) number for: reference can not contain sub reference and proof line
     *  reference simultaneously. */
    public static final int REFERENCE_CAN_NOT_CONTAIN_SUB_AND_LINE_REFERENCE_CODE = 90512;

    /** Error (or warning) text for: reference can not contain sub reference and proof line
     *  reference simultaneously. */
    public static final String REFERENCE_CAN_NOT_CONTAIN_SUB_AND_LINE_REFERENCE_TEXT
        = "reference can not contain sub reference and proof line reference simultaneously: ";


    /** Error (or warning) number for: Id or label defined more than once. */
    public static final int LABEL_DEFINED_MORE_THAN_ONCE_CODE = 10002;

    /** Error (or warning) text for: Id or label defined more than once. */
    public static final String LABEL_DEFINED_MORE_THAN_ONCE_TEXT
        = "Id or label defined more than once: ";


}
