package com.zenika.zpresence;

import io.netty.handler.codec.http.QueryStringDecoder;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.http.impl.MimeMapping;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.mods.web.WebServerBase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import static org.vertx.java.core.http.HttpHeaders.CONTENT_TYPE;

public class RestVerticle extends WebServerBase {

    @Override
    protected RouteMatcher routeMatcher() {
        RouteMatcher matcher = new RouteMatcher();

        matcher.post("/upload/:event", request -> {
            final String event = QueryStringDecoder.decodeComponent(request.params().get("event"));

            request.expectMultiPart(true);
            request.uploadHandler(upload -> {
                final Buffer file = new Buffer(0);
                upload.exceptionHandler(uploadException -> request.response().setStatusCode(400).end("Upload failed"));
                upload.dataHandler(file::appendBuffer);
                upload.endHandler(voidHandler -> {
                    try {
                        eb.send("prevayler-store", new JsonObject().putString("action", "edit-people").putString("event", event).putArray("people", extractPeople(file)), (Message<JsonObject> message) -> {
                            if ("ok".equals(message.body().getString("status"))) {
                                request.response().end(message.body().getArray("people").toString());
                            } else {
                                request.response().setStatusCode(400).end(message.body().getString("message"));
                            }
                        });
                    } catch (Exception e) {
                        request.response().setStatusCode(400).end();
                    }
                });
            });

        });

        matcher.get("/export/:event", request -> {
            final String event = QueryStringDecoder.decodeComponent(request.params().get("event").replace(".xls", ""));

            eb.send("prevayler-store", new JsonObject().putString("action", "get-people").putString("event", event), (Message<JsonObject> message) -> {
                if ("ok".equals(message.body().getString("status"))) {
                    JsonArray people = message.body().getArray("people");

                    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                        Workbook wb = new HSSFWorkbook();
                        Sheet s = wb.createSheet();

                        AtomicInteger rowNum = new AtomicInteger(0);
                        Row header = s.createRow(rowNum.getAndIncrement());
                        header.createCell(0).setCellValue("Présence");
                        header.createCell(1).setCellValue("Prénom");
                        header.createCell(2).setCellValue("Nom");
                        people.forEach(person -> {
                            JsonObject p = ((JsonObject) person);
                            Row r = s.createRow(rowNum.getAndIncrement());
                            AtomicInteger colNum = new AtomicInteger(0);
                            r.createCell(colNum.getAndIncrement()).setCellValue(p.getBoolean("presence"));
                            r.createCell(colNum.getAndIncrement()).setCellValue(p.getString("firstname", ""));
                            r.createCell(colNum.getAndIncrement()).setCellValue(p.getString("lastname", ""));
                            p.getArray("attributes", new JsonArray()).forEach(attribute -> r.createCell(colNum.getAndIncrement()).setCellValue((String) attribute));
                        });
                        wb.write(out);
                        request.response().putHeader(CONTENT_TYPE, MimeMapping.getMimeTypeForExtension("xls"));
//                        String fileName = request.headers().get("user-agent").contains("MSIE") ? URLEncoder.encode(event, "utf-8") : event /* encode differently ? */;
//                        request.response().putHeader("Content-disposition", "attachment; filename=\"" + fileName + ".xls\"");
                        request.response().end(new Buffer(out.toByteArray()));
                    } catch (Exception e) {
                        logger.error("error", e);
                        //sendError(message, "fetch-directory", e);
                        request.response().setStatusCode(400).end();
                    }
                } else {
                    request.response().setStatusCode(400).end(message.body().getString("message"));
                }
            });
        });

        matcher.get("/exit", ignore -> container.exit());

        matcher.noMatch(staticHandler());
        return matcher;
    }

    JsonArray extractPeople(Buffer file) {
        JsonArray people = new JsonArray();

        try (InputStream fileInputStream = new ByteArrayInputStream(file.getBytes())) {
            Workbook wb = workbook(fileInputStream);
            Sheet firstSheet = wb.getSheetAt(0);
            logger.info("Reading " + firstSheet.getLastRowNum() + 1 + " rows from first sheet");
            firstSheet.forEach(row -> {
                logger.debug("Parse row #" + row.getRowNum());
                Iterator<Cell> cellIterator = row.cellIterator();
                JsonObject person = new JsonObject();
                person.putString("firstname", cellIterator.hasNext() ? string(cellIterator.next()) : null);
                person.putString("lastname", cellIterator.hasNext() ? string(cellIterator.next()) : null);
                JsonArray attributes = new JsonArray();
                cellIterator.forEachRemaining(cell -> attributes.add(string(cell)));
                people.addObject(person.putArray("attributes", attributes));
            });
        } catch (Exception e) {
            logger.error("error", e);
            //sendError(message, "fetch-directory", e);
        }

        return people;
    }

    String string(Cell cell) {
//        try {
//            return Long.toString(cell.getDateCellValue().getTime());
//        } catch (Exception e1) {
        try {
            return Integer.toString(Double.valueOf(cell.getNumericCellValue()).intValue());
        } catch (Exception e2) {
            try {
                return Boolean.toString(cell.getBooleanCellValue());
            } catch (Exception e3) {
                try {
                    return cell.getStringCellValue().trim().replaceAll("\\u00A0", "");
                } catch (Exception e4) {
                    logger.warn("Cannot read cell [" + cell.getRowIndex() + "," + cell.getColumnIndex() + "]");
                    return "";
                }
            }
        }
//        }
    }

    Workbook workbook(InputStream inputStream) {
        try {
            return new HSSFWorkbook(inputStream);
        } catch (Exception he) {
            logger.warn("Cannot open Excel file as HSSFWorkbook!");
            try {
                inputStream.reset();
                return new XSSFWorkbook(inputStream);
            } catch (Exception xe) {
                logger.error("Cannot open Excel file as XSSFWorkbook!");
                throw new RuntimeException("Cannot open Excel file!");
            }
        }
    }
}
