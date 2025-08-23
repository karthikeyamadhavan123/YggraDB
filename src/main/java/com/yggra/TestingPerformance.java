package com.yggra;

import com.yggra.commands.SQLCommand;
import com.yggra.executor.SQLExecutor;
import com.yggra.parser.Lexer;
import com.yggra.parser.Parser;
import com.yggra.parser.Token;

import java.util.List;

public class TestingPerformance {

    // ⚔️ Forging the SQL Blade: Parse the raw SQL command into a battle-ready SQLCommand object

    private static SQLCommand parse(String sql) {
        Lexer lexer = new Lexer();
        List<Token> tokens = lexer.tokenize(sql);
        Parser parser = new Parser(tokens);
        return parser.parse();
    }

    // ⚔️ Wielding Mjölnir: Hammer out repeated CREATE TABLE commands to test the might of table creation

    private static void benchmarkCreateTable(SQLExecutor executor) {
        int runs = 50;
        long[] times = new long[runs];

        System.out.println("⚔️ Benchmark: CREATE TABLE");

        for (int i = 0; i < runs; i++) {
            String sql = "CREATE TABLE bench_table_" + i + " (id INT, name VARCHAR(255));";
            SQLCommand command = parse(sql);

            long startTime = System.nanoTime();
            executor.execute(command);
            long endTime = System.nanoTime();

            times[i] = endTime - startTime;
        }

        printStats(times);
    }

    // ⚔️ Filling the War Chest: Unleash a barrage of INSERT commands to measure the speed of data infusion

    private static void benchmarkInsert(SQLExecutor executor) {
        int runs = 1000;
        long[] times = new long[runs];

        System.out.println("⚔️ Benchmark: INSERT INTO");

        // Create table once for insert benchmark
        executor.execute(parse("CREATE TABLE bench_insert (id INT, name VARCHAR(255));"));

        for (int i = 0; i < runs; i++) {
            String sql = "INSERT INTO bench_insert (id, name) VALUES (" + i + ", 'User_" + i + "');";
            SQLCommand command = parse(sql);

            long startTime = System.nanoTime();
            executor.execute(command);
            long endTime = System.nanoTime();

            times[i] = endTime - startTime;
        }

        printStats(times);
    }

    // ⚔️ War Horn's Echo: Sound the performance statistics to reveal the battle's outcome

    private static void printStats(long[] times) {
        long sum = 0, min = Long.MAX_VALUE, max = Long.MIN_VALUE;
        for (long t : times) {
            sum += t;
            if (t < min) min = t;
            if (t > max) max = t;
        }
        double avgMs = (sum / (double) times.length) / 1_000_000.0;
        double minMs = min / 1_000_000.0;
        double maxMs = max / 1_000_000.0;

        System.out.println("Runs: " + times.length);
        System.out.printf("Average: %.4f ms\n", avgMs);
        System.out.printf("Min: %.4f ms\n", minMs);
        System.out.printf("Max: %.4f ms\n", maxMs);
        System.out.println();
    }

    // ⚔️ Surveying the Battlefield: Call forth the SHOW TABLES command to inspect the realm of tables

    private static void benchmarkShowTables(SQLExecutor executor) {
        int runs = 20;
        long[] times = new long[runs];

        System.out.println("⚔️ Benchmark: SHOW TABLES");

        // Create table once for insert benchmark
        executor.execute(parse("CREATE TABLE bench_insert (id INT, name VARCHAR(255));"));

        for (int i = 0; i < runs; i++) {
            String sql = "SHOW TABLES;";
            SQLCommand command = parse(sql);

            long startTime = System.nanoTime();
            executor.execute(command);
            long endTime = System.nanoTime();

            times[i] = endTime - startTime;
        }

        printStats(times);
    }

    // ⚔️ Reshaping the Arena: Wield the ALTER TABLE command to rename tables with the fury of Kratos

    private static void benchmarkAlterTable(SQLExecutor executor) {
        int runs = 20;
        long[] times = new long[runs];

        System.out.println("⚔️ Benchmark: ALTER TABLE_NAME");

        for (int i = 0; i < runs; i++) {
            String sql = "ALTER TABLE bench_table_" + i + " RENAME bench_new_table_" + i + ";";

            SQLCommand command = parse(sql);

            long startTime = System.nanoTime();
            executor.execute(command);
            long endTime = System.nanoTime();

            times[i] = endTime - startTime;
        }
        printStats(times);
    }

    // ⚔️ Cleansing the Realm: Unleash TRUNCATE TABLE to purge table contents with divine wrath

    private static void benchmarkTruncateTable(SQLExecutor executor) {
        int runs = 50;
        long[] times = new long[runs];

        System.out.println("⚔️ Benchmark: TRUNCATE TABLE");

        for (int i = 0; i < runs; i++) {
            String sql = "TRUNCATE TABLE bench_table_" + i + ";";

            SQLCommand command = parse(sql);

            long startTime = System.nanoTime();
            executor.execute(command);
            long endTime = System.nanoTime();

            times[i] = endTime - startTime;
        }
        printStats(times);
    }

    // ⚔️ Destroying the Stronghold: Cast down tables with the DROP TABLE command's unrelenting force

    private static void benchmarkDropTable(SQLExecutor executor) {
        int runs = 50;
        long[] times = new long[runs];

        System.out.println("⚔️ Benchmark: DROP TABLE");

        for (int i = 0; i < runs; i++) {
            String sql = "DROP TABLE bench_table_" + i + ";";

            SQLCommand command = parse(sql);

            long startTime = System.nanoTime();
            executor.execute(command);
            long endTime = System.nanoTime();

            times[i] = endTime - startTime;
        }
        printStats(times);
    }

    // ⚔️ Forging New Pillars: Expand tables with the ADD COLUMN command to bolster their structure

    private static void benchmarkAddColumn(SQLExecutor executor) {
        int runs = 50;
        long[] times = new long[runs];

        System.out.println("⚔️ Benchmark: ADD COLUMN TO TABLE");

        for (int i = 0; i < runs; i++) {
            String sql = "ADD COLUMN (col" + i + " INT) TO TABLE bench_table_" + i + ";";
            SQLCommand command = parse(sql);
            long startTime = System.nanoTime();
            executor.execute(command);
            long endTime = System.nanoTime();
            times[i] = endTime - startTime;
        }

        printStats(times);
    }

    // ⚔️ Shattering the Pillars: Remove columns from tables with the precision of a Spartan strike

    private static void benchmarkRemoveColumn(SQLExecutor executor) {
        int runs = 50;
        long[] times = new long[runs];

        System.out.println("⚔️ Benchmark: REMOVE COLUMN FROM TABLE");

        for (int i = 0; i < runs; i++) {
            String sql = "REMOVE FROM TABLE bench_table_" + i + "(col" + i + ");";
            SQLCommand command = parse(sql);
            long startTime = System.nanoTime();
            executor.execute(command);
            long endTime = System.nanoTime();
            times[i] = endTime - startTime;
        }

        printStats(times);
    }

    // ⚔️ Renaming the Relics: Transform column names with the RENAME COLUMN command's ancient magic

    private static void benchmarkRenameColumn(SQLExecutor executor) {
        int runs = 50;
        long[] times = new long[runs];

        System.out.println("⚔️ Benchmark: RENAME COLUMN");

        for (int i = 0; i < runs; i++) {
            String sql = "RENAME COLUMN col" + i + " TO col_new_" + i + " IN TABLE bench_table_" + i + ";";
            long startTime = System.nanoTime();
            executor.execute(parse(sql));
            long endTime = System.nanoTime();
            times[i] = endTime - startTime;
        }
        printStats(times);
    }

    // ⚔️ Reforging the Blade: Alter column definitions with the MODIFY COLUMN command's divine craft

    private static void benchmarkModifyColumn(SQLExecutor executor) {
        int runs = 50;
        long[] times = new long[runs];

        System.out.println("⚔️ Benchmark: MODIFY COLUMN");

        for (int i = 0; i < runs; i++) {
            String sql = "MODIFY COLUMN (id VARCHAR(50),name INT) IN TABLE bench_table_" + i + ";";
            long startTime = System.nanoTime();
            executor.execute(parse(sql));
            long endTime = System.nanoTime();
            times[i] = endTime - startTime;
        }

        printStats(times);
    }

    // ⚔️ Setting the Standard: Bestow default values on columns with the SET DEFAULT command's authority

    public static void benchmarkSetDefault(SQLExecutor executor) {
        int runs = 50;
        long[] times = new long[runs];

        System.out.println("⚔️ Benchmark: SET DEFAULT COLUMN");

        for (int i = 0; i < runs; i++) {
            String sql = "SET COLUMN id IN TABLE bench_table_" + i + " DEFAULT " + i + ";";
            long startTime = System.nanoTime();
            executor.execute(parse(sql));
            long endTime = System.nanoTime();
            times[i] = endTime - startTime;
        }

        printStats(times);
    }

    // ⚔️ Breaking the Covenant: Remove default values from columns with the DROP DEFAULT command's resolve

    public static void benchmarkDropDefault(SQLExecutor executor) {
        int runs = 50;
        long[] times = new long[runs];

        System.out.println("⚔️ Benchmark: DROP DEFAULT COLUMN");

        for (int i = 0; i < runs; i++) {
            String sql = "DROP DEFAULT FOR COLUMN id IN TABLE bench_table_" + i + ";";
            long startTime = System.nanoTime();
            executor.execute(parse(sql));
            long endTime = System.nanoTime();
            times[i] = endTime - startTime;
        }

        printStats(times);
    }

    // ⚔️ Marching to War: Initialize the battlefield and launch the performance benchmarking campaign

    public static void main(String[] args) {
        SQLExecutor executor = new SQLExecutor();

        // ✅ Create and select benchmark database
        executor.execute(parse("CREATE DATABASE benchmark;"));
        executor.execute(parse("USE benchmark;"));

        // Run benchmarks
//        benchmarkCreateTable(executor);
//        benchmarkInsert(executor);
//        benchmarkShowTables(executor);
//        benchmarkAlterTable(executor);
//        benchmarkTruncateTable(executor);
//        benchmarkDropTable(executor);
//        benchmarkAddColumn(executor);
//        benchmarkRemoveColumn(executor);
//        benchmarkRenameColumn(executor);
//        benchmarkModifyColumn(executor);
//        benchmarkSetDefault(executor);
//        benchmarkDropDefault(executor);
    }
}