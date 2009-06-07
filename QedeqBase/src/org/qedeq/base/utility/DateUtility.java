/* $Id: DateUtility.java,v 1.1 2008/07/26 07:55:42 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2009,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.base.utility;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.commons.lang.time.FastDateFormat;

/**
 * Various methods for date and time handling.
 *
 * @version $Revision: 1.1 $
 * @author Michael Meyling
 */
public final class DateUtility {

    /** ISO 8601 date and time format. */
    public static final FastDateFormat ISO_8601_TIMESTAMP_FORMATTER = FastDateFormat.getInstance(
        "yyyy-MM-dd'T'HH:mm:ss.SSS");

    /** Date format YYYYMMDD HHmm. */
    public static final FastDateFormat NICE_TIMESTAMP_FORMATTER = FastDateFormat.getInstance(
        "yyyy-MM-dd' 'HH:mm:ss.SSS");

    /** GMT timezone. */
    private static final TimeZone GMT = TimeZone.getTimeZone("GMT");

    /**
     * Hidden constructor.
     */
    private DateUtility() {
        // nothing to do
    }

    /**
     * Current timestamp in ISO 8601 format (date and time).
     *
     * @return  Current timestamp.
     */
    public static final String getIsoTimestamp() {
        return ISO_8601_TIMESTAMP_FORMATTER.format(new Date());
    }

    /**
     * Current timestamp as ISO 8601 date and time separated by space.
     *
     * @return  Current timestamp.
     */
    public static final String getTimestamp() {
        return NICE_TIMESTAMP_FORMATTER.format(new Date());
    }

    /**
     * Current GMT timestamp as ISO 8601 date and time separated by space.
     *
     * @return  Current GMT timestamp.
     */
    public static final String getGmtTimestamp() {
        return NICE_TIMESTAMP_FORMATTER.format(getCurrentGmtDate());
    }

    /**
     * Returns a current GMT date.
     *
     * @return  Current GMT date.
     */
    public static final Date getCurrentGmtDate() {
        final Calendar cal = Calendar.getInstance(GMT);
        final Calendar gmtDate = new GregorianCalendar();
        gmtDate.set(Calendar.MONTH, cal.get(Calendar.MONTH));
        gmtDate.set(Calendar.YEAR, cal.get(Calendar.YEAR));
        gmtDate.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
        gmtDate.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
        gmtDate.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
        gmtDate.set(Calendar.SECOND, cal.get(Calendar.SECOND));
        return gmtDate.getTime();
    }

    /**
     * Convert millisecond duration into readable time amount.
     *
     * @param   milliseconds    Duration in milliseconds.
     * @return  Time in format "[d day[s] ]HH:mm:ss.SSS".
     */
    public static final String getDuration(final long milliseconds) {
        final StringBuffer buffer = new StringBuffer();
        long factor = 1000 * 60 * 60 * 24;
        long rest = milliseconds;
        long mod = 0;

        // days
        mod = rest / factor;
        rest = rest % factor;
        if (mod > 0) {
            buffer.append(mod);
            buffer.append(" day");
            if (mod > 1) {
                buffer.append("s");
            }
            buffer.append(" ");
        }

        // hours
        factor = factor / 24;
        mod = rest / factor;
        rest = rest % factor;
        buffer.append(StringUtility.format(mod, 2));

        buffer.append(":");

        // minutes
        factor = factor / 60;
        mod = rest / factor;
        rest = rest % factor;
        buffer.append(StringUtility.format(mod, 2));

        buffer.append(":");

        // seconds
        factor = factor / 60;
        mod = rest / factor;
        rest = rest % factor;
        buffer.append(StringUtility.format(mod, 2));

        buffer.append(".");

        // milliseconds
        factor = factor / 1000;
        mod = rest / factor;
        rest = rest % factor;
        buffer.append(StringUtility.format(mod, 3));

        return buffer.toString();
    }

}
