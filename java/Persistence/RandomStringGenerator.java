package Persistence;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class RandomStringGenerator {
    private static final Set<String> generatedStrings = ConcurrentHashMap.newKeySet();
    private static LocalDate lastDate = LocalDate.now();
    private static final int RANDOM_LENGTH = 7;
    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    // 新格式：年-月-日-小时:分钟
    private static final DateTimeFormatter READABLE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm");
    public static synchronized String generateUniqueString() {
        // 获取当前日期并格式化
        LocalDate today = LocalDate.now();
        String datePart = today.format(DateTimeFormatter.BASIC_ISO_DATE);

        // 日期变化时重置存储集合
        if (!today.isEqual(lastDate)) {
            generatedStrings.clear();
            lastDate = today;
        }

        // 生成唯一字符串
        String randomPart;
        String fullString;
        do {
            randomPart = generateRandomString();
            fullString = datePart + randomPart;
        } while (!generatedStrings.add(fullString));

        return fullString;
    }
    public static String getReadableCurrentTime() {
        return LocalDateTime.now().format(READABLE_TIME_FORMATTER);
    }

    private static String generateRandomString() {
        StringBuilder sb = new StringBuilder(RANDOM_LENGTH);
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < RANDOM_LENGTH; i++) {
            int index = random.nextInt(LETTERS.length());
            sb.append(LETTERS.charAt(index));
        }
        return sb.toString();
    }
}