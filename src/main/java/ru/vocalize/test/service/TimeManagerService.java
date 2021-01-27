package ru.vocalize.test.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import ru.vocalize.test.model.Log;
import ru.vocalize.test.model.User;
import ru.vocalize.test.repository.WorkLogRepository;

@Service
public class TimeManagerService {
  private final WorkLogRepository workLogger;

  public TimeManagerService(WorkLogRepository logWork) {
    this.workLogger = logWork;
  }

  public void userIn(User user) {
    workLogger.save(Log.in(user));
  }

  public void userOut(User user) {
    workLogger.save(Log.out(user));
  }

  public Boolean isWork(User user) {
    Log log = workLogger.findFirstByUserOrderByTimePointDesc(user);
    return log != null ? log.getWorking() : false;
  }

  public String createReport(User user) {
    List<Log> log = workLogger.findByUserOrderByTimePoint(user);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH-mm-ss").withZone(ZoneId.of("+03:00"));
    String filename = String.format("%s-%s.xlsx", user.getLogin(), formatter.format(Instant.now()));
    createExcelReport(filename, log);
    //Удаляем лог
    workLogger.deleteAllByUser(user);
    return filename;
  }

  private void createExcelReport(String fileName, List<Log> log) {
    XSSFWorkbook workbook = new XSSFWorkbook();
    XSSFSheet sheet = workbook.createSheet();
    for (int rowNum = 0; rowNum < log.size(); rowNum += 1) {
      Row row = sheet.createRow(rowNum);
      row.createCell(0)
         .setCellValue(log.get(rowNum)
                          .getWorking() ? "Прибыл" : "Убыл");
      row.createCell(1)
         .setCellValue(log.get(rowNum)
                          .getTimePoint()
                          .toString());
    }
    Row row = sheet.createRow(log.size());
    row.createCell(0)
       .setCellValue(time2String(getTime(log)));
    try (FileOutputStream outputStream = new FileOutputStream(fileName)){
      workbook.write(outputStream);
      workbook.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private long getTime(List<Log> log) {
    Iterator<Log> it = log.iterator();
    long time = 0;
    boolean toWork = false;
    while (it.hasNext()) {
      Log en = it.next();
      if (en.getWorking() != toWork) {
        time += en.getTimePoint()
                  .getEpochSecond() * (toWork ? 1 : -1);
        toWork = !toWork;
      }
    }
    if (toWork) {
      time += Instant.now()
                     .getEpochSecond();
    }
    return time;
  }

  private String time2String(long sec) {
    int min = (int) sec / 60;
    int hour = min / 60;
    return String.format("Отработано: %d часов,\n %d минут, %d секунд.", hour, min % 60, sec % 60);
  }
}
