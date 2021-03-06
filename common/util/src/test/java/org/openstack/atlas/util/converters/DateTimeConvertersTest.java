package org.openstack.atlas.util.converters;

import static org.openstack.atlas.util.converters.DateTimeConverters.calToiso;
import static org.openstack.atlas.util.converters.DateTimeConverters.isoTocal;
import org.openstack.atlas.util.common.exceptions.ConverterException;
import java.util.Calendar;
import java.util.TimeZone;
import org.junit.Before;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class DateTimeConvertersTest {

    private final static String standardIso = "1978-05-28T15:20-06:00";
    private final static String wrongDate = "2000-1-1T00:00Z";
    private final static String expectedIso = "1978-05-28T21:20:00Z";

    public DateTimeConvertersTest() {
    }

    public Calendar getExpectedCalendar() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.set(Calendar.MONTH, 4);
        cal.set(Calendar.DAY_OF_MONTH, 28);
        cal.set(Calendar.YEAR, 1978);
        cal.set(Calendar.HOUR_OF_DAY, 21);
        cal.set(Calendar.MINUTE, 20);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public Calendar getWrongCalendar() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.YEAR, 2000);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    @Test(expected = ConverterException.class)
    public void shouldThrowConverterExceptionWhenConvertingBogusGregorianDate() throws ConverterException {
        String wtf = "May 28, 1978";
        Calendar cal = isoTocal(wtf);
        nop();
    }

    @Test(expected = ConverterException.class)
    public void shouldThrowConverterExceptionWhenConvertingNullString() throws ConverterException {
        String wtf = null;
        Calendar cal = isoTocal(wtf);
        nop();
    }

    @Test
    public void shouldConvertCalendarto8601() throws ConverterException {
        Calendar cal = getExpectedCalendar();
        String isoStr = calToiso(cal);

        assertTrue(expectedIso.equals(isoStr));
        assertFalse(isoStr.equals(wrongDate));
    }

    @Test
    public void shouldConvert8601toCalendar() throws ConverterException {
        Calendar cal;

        cal = isoTocal(standardIso);
        assertTrue(0 == cal.compareTo(getExpectedCalendar()));

        assertFalse(0 == cal.compareTo(getWrongCalendar()));
        nop();
    }

    @Test
    public void shouldKeepNegativeTimezoneWhenConvertingToCalendar() throws ConverterException {
        String isoString = "2000-01-01T00:00:00-08:00";
        int offset;
        int expectedOffset = 0 - 1000 * 60 * 60 * 8; // - 8 hours
        Calendar cal;
        cal = isoTocal(isoString);
        offset = cal.getTimeZone().getRawOffset();
        assertEquals(expectedOffset, offset);
    }

    @Test
    public void shouldKeepPositiveTimezoneWhenConvertingToCalendar() throws ConverterException {
        String isoString = "2000-01-01T00:00:00+08:00";
        int offset;
        int expectedOffset = 1000 * 60 * 60 * 8; // + 8 hours
        Calendar cal;
        cal = isoTocal(isoString);
        offset = cal.getTimeZone().getRawOffset();
        assertEquals(expectedOffset, offset);
    }

    @Test
    public void shouldParseOffsetWithoutColonPlus() throws ConverterException {
        String isoString = "2000-01-01T00:00:00+0800";
        int offset;
        int expectedOffset = 1000 * 60 * 60 * 8; // + 8 hours
        Calendar cal;
        cal = isoTocal(isoString);
        offset = cal.getTimeZone().getRawOffset();
        assertEquals(expectedOffset, offset);
    }

    @Test
    public void shouldParseOffsetWithShortFormPlus() throws ConverterException {
        String isoString = "2018-10-19T08:07:08+08";
        int offset;
        int expectedOffset = 1000 * 60 * 60 * 8; // + 8 hours
        Calendar cal;
        cal = isoTocal(isoString);
        offset = cal.getTimeZone().getRawOffset();
        assertEquals(expectedOffset, offset);
    }

    @Test
    public void shouldParseOffsetWithShortFormMinus() throws ConverterException {
        String isoString = "2000-01-01T00:00:00-08";
        int offset;
        int expectedOffset = 0 - 1000 * 60 * 60 * 8; // - 8 hours
        Calendar cal;
        cal = isoTocal(isoString);
        offset = cal.getTimeZone().getRawOffset();
        assertEquals(expectedOffset, offset);
    }

    @Test
    public void shouldParseOffsetWithoutColonMinus() throws ConverterException {
        String isoString = "2000-01-01T00:00:00-0800";
        int offset;
        int expectedOffset = 0 - 1000 * 60 * 60 * 8; // - 8 hours
        Calendar cal;
        cal = isoTocal(isoString);
        offset = cal.getTimeZone().getRawOffset();
        assertEquals(expectedOffset, offset);
    }

    @Test
    public void shouldParseOffsetWithDateOnly() throws ConverterException {
        String isoString = "2000-01-01";
        int offset;
        int expectedOffset = 0 ;
        Calendar cal;
        cal = isoTocal(isoString);
        offset = cal.getTimeZone().getRawOffset();
        assertEquals(expectedOffset, offset);
    }

    @Test(expected = ConverterException.class)
    public void shouldFailToParseOffsetWithBrokenDateOnly() throws ConverterException {
        String isoString = "2000-01";
        isoTocal(isoString);
        nop();
    }

    @Test(expected = ConverterException.class)
    public void shouldFailToParseOffsetWithGibberish() throws ConverterException {
        String isoString = "2000-01-10Tasdfasdkf-8000";
        isoTocal(isoString);
        nop();
    }

    @Test
    public void shouldParseOffWithVariousDateFormats() throws ConverterException {
        String[] variousDates = {
                "2018-08-08T08:45:00-08",//short form of zone offset and it is same as either the month or the day
                "2018-12-10T08:45",
                "2018-12-10T08:45-01:30",
                "2018-12-10T08:45:30",
                "2018-12-10T08:45:30.999999999",
                "2018-12-10T08:45:30.999999999-0800",
                "2018-12-10T08:45:30.999999999+08:00",
                //"2018-12-10T08:45:30+01:00[Europe/Paris]",
                "2018-12-10T",//rare scenarios, but needed as these were accepted before Java 8
                "2018-12-10T08",
                "2018-12-10T08-01",
                "2018-12-10T08-0130",
        };
        Calendar calendar;
        for (String date:variousDates) {
            try {
                calendar = isoTocal(date);
            } catch (Exception ex) {
                calendar = null;
            }
            assertNotNull(date+ " does not follow ISO-8601 format, could not be parsed.", calendar);
        }
    }

    @Test
    public void shouldKeepUtcByDefault() throws ConverterException {
        String isoString = "2000-01-01T00:00:00";
        int offset;
        int expectedOffset = 0;
        Calendar cal;
        cal = isoTocal(isoString);
        offset = cal.getTimeZone().getRawOffset();
        assertEquals(expectedOffset, offset);
    }

    @Test
    public void shouldInterpretZtoRepresentUTC() throws ConverterException {
        String isoString = "2000-01-01T00:00:00Z";
        int offset;
        int expectedOffset = 0;
        Calendar cal;
        cal = isoTocal(isoString);
        offset = cal.getTimeZone().getRawOffset();
        assertEquals(expectedOffset, offset);
    }

    public void nop() {
    }
}
