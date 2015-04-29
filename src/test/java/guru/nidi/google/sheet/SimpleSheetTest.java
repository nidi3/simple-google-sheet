package guru.nidi.google.sheet;

import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.ServiceException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeThat;

/**
 *
 */
public class SimpleSheetTest {
    private static SimpleSheet ss;

    private static String getEnv(String name) {
        final String env = System.getenv(name);
        assumeThat("Environment variable " + name + " is not set, skipping test", env, notNullValue());
        return env;
    }

    @BeforeClass
    public static void init() throws GeneralSecurityException, IOException, ServiceException {
        ss = new SimpleSheet(new SimpleCredential(
                getEnv("GOOGLE_SERVICE_ACCOUNT_ID"),
                new File(getEnv("GOOGLE_SERVICE_ACCOUNT_KEY"))));

        final SpreadsheetEntry sheet = ss.findSheet(ss.createSheet("__data"));
        final CellFeed cellFeed = ss.cellFeed(ss.findWorksheet(ss.worksheetFeed(sheet), 0));
        ss.updateCell(cellFeed, 1, 1, "name");
        ss.updateCell(cellFeed, 1, 2, "age");
        ss.updateCell(cellFeed, 2, 1, "A");
        ss.updateCell(cellFeed, 2, 2, "666");
        ss.updateCell(cellFeed, 3, 1, "B");
        ss.updateCell(cellFeed, 3, 2, "42");
    }

    @AfterClass
    public static void cleanup() throws IOException, ServiceException {
        for (SpreadsheetEntry sheet : ss.getSheetFeed().getEntries()) {
            if (sheet.getTitle().getPlainText().startsWith("__")) {
                ss.deleteSheet(sheet);
            }
        }
    }

    @Test
    public void createAndDeleteSheet() throws IOException, ServiceException {
        final int before = ss.getSheetFeed().getEntries().size();
        final com.google.api.services.drive.model.File
                sst1 = ss.createSheet("__sst1"),
                sst2 = ss.createSheet("__sst2");
        final int middle = ss.getSheetFeed().getEntries().size();
        final SpreadsheetEntry found = ss.findSheet("__sst1");
        ss.deleteSheet(found);
        ss.deleteSheet(sst2);
        final int after = ss.getSheetFeed().getEntries().size();

        assertEquals(2, middle - before);
        assertNotNull(found);
        assertEquals(before, after);
    }

    @Test
    public void addAndRemoveWorksheets() throws IOException, ServiceException {
        final SpreadsheetEntry sheet = ss.findSheet(ss.createSheet("__sheet"));
        final int before = sheet.getWorksheets().size();
        final WorksheetEntry worksheet = ss.addWorksheet(sheet, "worksheet");
        final int middle = sheet.getWorksheets().size();
        worksheet.delete();
        final int after = sheet.getWorksheets().size();

        assertEquals(1, middle - before);
        assertEquals(before, after);
    }

    @Test
    public void getCells() throws IOException, ServiceException {
        final SpreadsheetEntry sheet = ss.findSheet("__data");
        final CellFeed cellFeed = ss.cellFeed(ss.findWorksheet(ss.worksheetFeed(sheet), 0));

        assertEquals("name", ss.findCell(cellFeed, 1, 1).getPlainTextContent());
        assertEquals("age", ss.findCell(cellFeed, 1, 2).getPlainTextContent());
        assertNull(ss.findCell(cellFeed, 1, 3));
    }

    @Test
    public void getCellsQuery() throws IOException, ServiceException {
        final SpreadsheetEntry sheet = ss.findSheet("__data");
        final CellFeed cellFeed = ss.cellFeed(ss.findWorksheet(ss.worksheetFeed(sheet), 0),
                new CellFeedQuery().minCol(2).maxCol(2).minRow(2).maxRow(2));
        final Cell cell = cellFeed.getEntries().get(0).getCell();

        assertEquals(1, cellFeed.getEntries().size());
        assertEquals(2, cell.getRow());
        assertEquals(2, cell.getCol());
    }

    @Test
    public void getRows() throws IOException, ServiceException {
        final SpreadsheetEntry sheet = ss.findSheet("__data");
        final ListFeed listFeed = ss.listFeed(ss.findWorksheet(ss.worksheetFeed(sheet), 0));

        final CustomElementCollection firstRow = listFeed.getEntries().get(0).getCustomElements();
        final CustomElementCollection secondRow = listFeed.getEntries().get(1).getCustomElements();

        assertEquals("A", firstRow.getValue("name"));
        assertEquals("666", firstRow.getValue("age"));
        assertEquals("B", secondRow.getValue("name"));
        assertEquals("42", secondRow.getValue("age"));
    }

    @Test
    public void getRowsInverse() throws IOException, ServiceException {
        final SpreadsheetEntry sheet = ss.findSheet("__data");
        final ListFeed listFeed = ss.listFeed(ss.findWorksheet(ss.worksheetFeed(sheet), 0),
                new ListFeedQuery().inverseRowOrder().orderByColumn("name"));

        final CustomElementCollection firstRow = listFeed.getEntries().get(0).getCustomElements();
        final CustomElementCollection secondRow = listFeed.getEntries().get(1).getCustomElements();

        assertEquals("B", firstRow.getValue("name"));
        assertEquals("42", firstRow.getValue("age"));
        assertEquals("A", secondRow.getValue("name"));
        assertEquals("666", secondRow.getValue("age"));
    }

    @Test
    public void getRowsOrderBy() throws IOException, ServiceException {
        final SpreadsheetEntry sheet = ss.findSheet("__data");
        final ListFeed listFeed = ss.listFeed(ss.findWorksheet(ss.worksheetFeed(sheet), 0),
                new ListFeedQuery().orderByColumn("age"));

        final CustomElementCollection firstRow = listFeed.getEntries().get(0).getCustomElements();
        final CustomElementCollection secondRow = listFeed.getEntries().get(1).getCustomElements();

        assertEquals("B", firstRow.getValue("name"));
        assertEquals("42", firstRow.getValue("age"));
        assertEquals("A", secondRow.getValue("name"));
        assertEquals("666", secondRow.getValue("age"));
    }

    @Test
    public void getRowsQuery() throws IOException, ServiceException {
        final SpreadsheetEntry sheet = ss.findSheet("__data");
        final ListFeed listFeed = ss.listFeed(ss.findWorksheet(ss.worksheetFeed(sheet), 0),
                new ListFeedQuery().query("age>100"));

        final CustomElementCollection firstRow = listFeed.getEntries().get(0).getCustomElements();

        assertEquals(1, listFeed.getEntries().size());
        assertEquals("A", firstRow.getValue("name"));
        assertEquals("666", firstRow.getValue("age"));
    }
}
