/*
 * Copyright (C) 2015 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.google.sheet;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 */
public class SimpleSheet {
    private static final String APPLICATION_NAME = "guru.nidi-SimpleSheet-1";
    private static final URL SPREADSHEET_FEED_URL = url("https://spreadsheets.google.com/feeds/spreadsheets/private/full");

    private final SpreadsheetService service;
    private final Drive drive;

    public SimpleSheet(SimpleCredential credential) {
        service = new SpreadsheetService(APPLICATION_NAME);
        if (credential.credential != null) {
            service.setOAuth2Credentials(credential.credential);
        }

        drive = new Drive.Builder(credential.httpTransport, credential.jsonFactory, credential.credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private static URL url(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
    }

    public File createSheet(String title) throws IOException {
        final File file = new File();
        file.setTitle(title);
        return createSheet(file);
    }

    public File createSheet(File sheet) throws IOException {
        sheet.setMimeType("application/vnd.google-apps.spreadsheet");
        return drive.files().insert(sheet).execute();
    }

    public File shareWith(File file, String email, Role role) throws IOException {
        final Permission permission = new Permission();
        permission.setValue(email);
        permission.setType("user");
        permission.setRole(role.getName());
        drive.permissions().insert(file.getId(), permission).execute();
        return file;
    }

    public void deleteSheet(File sheet) throws IOException {
        drive.files().delete(sheet.getId()).execute();
    }

    public void deleteSheet(SpreadsheetEntry sheet) throws IOException {
        drive.files().delete(sheet.getKey()).execute();
    }

    public SpreadsheetFeed getSheetFeed() throws IOException, ServiceException {
        return service.getFeed(SPREADSHEET_FEED_URL, SpreadsheetFeed.class);
    }

    public SpreadsheetEntry findSheet(File file) throws IOException, ServiceException {
        return findSheet(file.getTitle());
    }

    public SpreadsheetEntry findSheet(String title) throws IOException, ServiceException {
        for (SpreadsheetEntry entry : getSheetFeed().getEntries()) {
            if (entry.getTitle().getPlainText().equals(title)) {
                return entry;
            }
        }
        return null;
    }

    public WorksheetFeed worksheetFeed(SpreadsheetEntry sheet) throws IOException, ServiceException {
        return service.getFeed(sheet.getWorksheetFeedUrl(), WorksheetFeed.class);
    }

    public WorksheetEntry findWorksheet(WorksheetFeed feed, int index) {
        return index < feed.getEntries().size() ? feed.getEntries().get(index) : null;
    }

    public WorksheetEntry findWorksheet(WorksheetFeed feed, String title) {
        for (WorksheetEntry worksheet : feed.getEntries()) {
            if (worksheet.getTitle().getPlainText().equals(title)) {
                return worksheet;
            }
        }
        return null;
    }

    public WorksheetEntry addWorksheet(SpreadsheetEntry sheet, String title) throws IOException, ServiceException {
        final WorksheetEntry worksheet = new WorksheetEntry(1000, 26);
        worksheet.setTitle(new PlainTextConstruct(title));
        return addWorksheet(sheet, worksheet);
    }

    public WorksheetEntry addWorksheet(SpreadsheetEntry sheet, WorksheetEntry worksheet) throws IOException, ServiceException {
        return service.insert(sheet.getWorksheetFeedUrl(), worksheet);
    }

    public ListFeed listFeed(WorksheetEntry worksheet) throws IOException, ServiceException {
        return service.getFeed(worksheet.getListFeedUrl(), ListFeed.class);
    }

    public ListFeed listFeed(WorksheetEntry worksheet, ListFeedQuery query) throws IOException, ServiceException {
        return service.getFeed(url(worksheet.getListFeedUrl().toString() + query.toQuery()), ListFeed.class);
    }

    public ListEntry addRow(WorksheetEntry worksheet, ListEntry row) throws IOException, ServiceException {
        return service.insert(worksheet.getListFeedUrl(), row);
    }

    public CellFeed cellFeed(WorksheetEntry worksheet) throws IOException, ServiceException {
        return service.getFeed(worksheet.getCellFeedUrl(), CellFeed.class);
    }

    public CellFeed cellFeed(WorksheetEntry worksheet, CellFeedQuery query) throws IOException, ServiceException {
        return service.getFeed(url(worksheet.getCellFeedUrl().toString() + query.toQuery()), CellFeed.class);
    }

    public CellEntry findCell(CellFeed feed, int row, int col) {
        //TODO use binary search
        for (CellEntry entry : feed.getEntries()) {
            final Cell cell = entry.getCell();
            if (cell.getRow() == row && cell.getCol() == col) {
                return entry;
            }
            if (cell.getRow() > row) {
                return null;
            }
        }
        return null;
    }

    public CellEntry getCell(CellFeed feed, int row, int col) throws IOException, ServiceException {
        CellEntry entry = findCell(feed, row, col);
        if (entry == null) {
            entry = feed.insert(new CellEntry(row, col, ""));
        }
        return entry;
    }

    public void updateCell(CellFeed feed, int row, int col, String value) throws IOException, ServiceException {
        final CellEntry entry = getCell(feed, row, col);
        entry.changeInputValueLocal(value);
        entry.update();
    }
}