/**
 * Java parser for the MRZ records, as specified by the ICAO organization.
 * Copyright (C) 2011 Innovatrics s.r.o.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.ingsoft.bancoapp.applicationForm.mrzscanner.mrz.types;

import com.ingsoft.bancoapp.applicationForm.mrzscanner.mrz.MrzParseException;
import com.ingsoft.bancoapp.applicationForm.mrzscanner.mrz.MrzRange;
import com.ingsoft.bancoapp.applicationForm.mrzscanner.mrz.MrzRecord;
import com.ingsoft.bancoapp.applicationForm.mrzscanner.mrz.records.*;

/**
 * Lists all supported MRZ formats. Note that the order of the enum constants are important, see for example {@link  #FRENCH_ID}.
 * @author Martin Vysny, Pierrick Martin
 */
public enum MrzFormat {

    /**
     * MRTD td1 format: A three line long, 30 characters per line format.
     */
    MRTD_TD1(3, 30, MrtdTd1.class);

    public final int rows;
    public final int columns;
    private final Class<? extends MrzRecord> recordClass;

    private MrzFormat(int rows, int columns, Class<? extends MrzRecord> recordClass) {
        this.rows = rows;
        this.columns = columns;
        this.recordClass = recordClass;
    }

    /**
     * Checks if this format is able to parse given serialized MRZ record.
     * @param mrzRows MRZ record, separated into rows.
     * @return true if given MRZ record is of this type, false otherwise.
     */
    public boolean isFormatOf(String[] mrzRows) {
        return rows == mrzRows.length && columns == mrzRows[0].length();
    }

    /**
     * Detects given MRZ format.
     * @param mrz the MRZ string.
     * @return the format, never null.
     */
    public static final MrzFormat get(String mrz) {
        final String[] rows = mrz.split("\n");
        final int cols = rows[0].length();
        for (int i = 1; i < rows.length; i++) {
            if (rows[i].length() != cols) {
                throw new MrzParseException("Different row lengths: 0: " + cols + " and " + i + ": " + rows[i].length(), mrz, new MrzRange(0, 0, 0), null);
            }
        }
        for (final MrzFormat f : values()) {
            if (f.isFormatOf(rows)) {
                return f;
            }
        }
        throw new MrzParseException("Unknown format / unsupported number of cols/rows: " + cols + "/" + rows.length, mrz, new MrzRange(0, 0, 0), null);
    }

    /**
     * Creates new record instance with this type.
     * @return never null record instance.
     */
    public final MrzRecord newRecord() {
        try {
            return recordClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
