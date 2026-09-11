package kw.kng.prerequisites.gnrlutils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public final class SqlUtil 
{
	private SqlUtil() 
	{

	}

	// ######################################################################################################################################################
											// SQL LOADING
	// ######################################################################################################################################################

	
	/*
	 * SQL LOADER -> LOAD SQL QUERY FROM FILE
	 *
	 * Purpose:
	 * Reads an external SQL file from the project's resources folder
	 * and returns its contents as a String.
	 *
	 * This allows SQL statements to be maintained separately from
	 * the repository code, making complex queries easier to read,
	 * maintain and reuse.
	 *
	 * Responsibilities:
	 *
	 * 1. Locate the SQL file from the classpath.
	 * 2. Read the file contents.
	 * 3. Convert the contents into a UTF-8 String.
	 * 4. Throw a RuntimeException if the file cannot be loaded.
	 *
	 * Typical Usage:
	 *
	 * String sql = loadQueryFromFile(
	 *      "sql/eclinic/blood/bds_ass_jdbc.sql");
	 *
	 * Benefits:
	 *
	 * - Keeps SQL separate from Java code.
	 * - Improves repository readability.
	 * - Allows SQL files to be reused across repository methods.
	 * - Makes large SQL queries easier to maintain.
	 *
	 * Method Applied in:
	 * -> BDS -> repo -> jdbc -> BdsStaffJdbcRepo.java
	 * -> BDS -> repo -> jdbc -> BdsBannedJdbcRepo.java
	 */
	 public static String loadQueryFromFile(String filePath) 
	 {
		 
		 try 
		 {
		        ClassPathResource resource = new ClassPathResource(filePath);
		        byte[] bytes = Files.readAllBytes(resource.getFile().toPath());
		        return new String(bytes, StandardCharsets.UTF_8);
		 } 
		 catch (IOException e) 
		 {
		        throw new RuntimeException("Unable to load SQL file: " + filePath, e);
		 }
		 
	 } 
	
	
	// ######################################################################################################################################################
											// SQL DEBUGGING
	// ######################################################################################################################################################

	/*
	 * SQL DEBUGGING -> FORMAT SQL FOR CONSOLE OUTPUT
	 *
	 * Purpose:
	 * Converts a single-line SQL statement into a readable multi-line format
	 * before printing it to the console.
	 *
	 * This method is intended ONLY for debugging purposes.
	 * It does NOT modify the SQL executed against the database.
	 *
	 * Example:
	 *
	 * Before:
	 * SELECT * FROM EMP WHERE ID=:id AND STATUS='Y'
	 *
	 * After:
	 * SELECT *
	 * FROM EMP
	 * WHERE
	 *     ID=:id
	 *     AND STATUS='Y'
	 */
	public static String formatSqlForConsole(String sql) 
	{
		return sql.replace(" SELECT ", "\nSELECT ").replace(" FROM ", "\nFROM ")
				.replace(" INNER JOIN ", "\nINNER JOIN ").replace(" LEFT JOIN ", "\nLEFT JOIN ")
				.replace(" RIGHT JOIN ", "\nRIGHT JOIN ").replace(" WHERE ", "\nWHERE ").replace(" AND ", "\n    AND ")
				.replace(" OR ", "\n    OR ").replace(" GROUP BY ", "\nGROUP BY ").replace(" HAVING ", "\nHAVING ")
				.replace(" ORDER BY ", "\nORDER BY ");
	}

	
	/*
	 * SQL DEBUGGING -> BUILD EXECUTABLE SQL
	 *
	 * Purpose:
	 * Replaces named parameters (e.g. :keyword, :staffId, :fromDate)
	 * with their actual runtime values.
	 *
	 * This produces an executable SQL statement that can be copied
	 * directly into DBeaver or SQL Developer for troubleshooting.
	 *
	 * This method is intended ONLY for debugging purposes.
	 * The SQL sent to Oracle still uses NamedParameterJdbcTemplate.
	 */
	public static String buildExecutableSql(String sql, MapSqlParameterSource params) 
	{
		String executableSql = sql;

		for (Map.Entry<String, Object> entry : params.getValues().entrySet()) {
			String key = ":" + entry.getKey();

			Object value = entry.getValue();

			if (value instanceof String) {
				executableSql = executableSql.replace(key, "'" + value + "'");
			} else if (value instanceof LocalDate) {
				executableSql = executableSql.replace(key, "'" + value.toString() + "'");
			} else {
				executableSql = executableSql.replace(key, String.valueOf(value));
			}
		}

		return executableSql;
	}


	/*
	 * SQL DEBUGGING -> PRINT COMPLETE SQL INFORMATION
	 *
	 * Purpose:
	 * Prints useful debugging information to the console including:
	 *
	 * 1. Generated SQL
	 * 2. Parameter values
	 * 3. Executable SQL
	 *
	 * This method should be called whenever complex dynamic SQL
	 * needs to be verified during development.
	 */
	public static void printSql(String title, String sql, MapSqlParameterSource params) 
	{
		System.out.println("\n==================== " + title + " ====================");
		System.out.println("\n==================== GENERATED SQL ====================");
		System.out.println(sql);
		System.out.println("\n==================== PARAMETERS =======================");
		System.out.println(params.getValues());
		System.out.println("\n==================== EXECUTABLE SQL ===================");
		System.out.println(formatSqlForConsole(buildExecutableSql(sql, params)));
		System.out.println("=======================================================\n");
	}
	
	// ######################################################################################################################################################
												// SQL PAGINATION
	// ######################################################################################################################################################	
	/*
	 * PAGINATION -> APPEND OFFSET/FETCH CLAUSE
	 *
	 * Purpose:
	 * Adds Oracle pagination syntax to the SQL query using
	 * Spring Pageable.
	 *
	 * Also stores the calculated offset and page size
	 * inside the parameter map.
	 *
	 * Example:
	 *
	 * OFFSET :offset ROWS
	 * FETCH NEXT :pageSize ROWS ONLY
	 *
	 * Reusable across all repository classes that support
	 * server-side pagination.
	 */
	public static void appendPagination(StringBuilder sql, MapSqlParameterSource params, Pageable pageable)
	{
		sql.append(" OFFSET :offset ROWS FETCH NEXT :pageSize ROWS ONLY ");
		params.addValue("offset", pageable.getOffset());
		params.addValue("pageSize", pageable.getPageSize());
	}
	
	/*
	 * GENERIC JDBC PAGINATION HELPER
	 *
	 * Purpose:
	 * Executes a paginated JDBC query and returns the result
	 * as a Spring Page<T>.
	 *
	 * Responsibilities:
	 *
	 * 1. Appends Oracle pagination clause.
	 * 2. Executes the main SQL query.
	 * 3. Executes the corresponding COUNT query.
	 * 4. Creates and returns PageImpl<T>.
	 *
	 * This method is completely generic and can be reused
	 * across any module regardless of the DTO or RowMapper.
	 *
	 * Example:
	 *
	 * Assistant Staff
	 * Donations
	 * Campaigns
	 * Electronic Archive
	 * Dashboard Statistics
	 *
	 * The repository remains responsible for:
	 * - Loading SQL from file
	 * - Preparing parameters
	 * - Creating the RowMapper
	 *
	 * This utility only executes the query and builds
	 * the Page<T> result.
	 * 
	 * Method Applied in:  
	 * -> BDS -> repo -> jdbc -> BdsAssistantJdbcRepo.java
	 */
	public static <T> Page<T> buildPage(NamedParameterJdbcTemplate jdbc, 
										StringBuilder sql, 
										String countSql, 
										Pageable pageable,
										MapSqlParameterSource params,
								        RowMapper<T> rowMapper)
	{
		// ------------------------------------------------------------
	    // Append Pagination
	    // ------------------------------------------------------------
	    appendPagination(sql, params, pageable);
	    // ------------------------------------------------------------
	    // Debug SQL
	    // ------------------------------------------------------------
	    printSql("DATA QUERY", sql.toString(), params);
	    printSql("COUNT QUERY", countSql, params);
	    
	    // ------------------------------------------------------------
	    // Execute Queries
	    // ------------------------------------------------------------
	    List<T> records = jdbc.query(sql.toString(), params, rowMapper);
		Long total = jdbc.queryForObject(countSql, params, Long.class);
		
	    // ------------------------------------------------------------
	    // Return Page
	    // ------------------------------------------------------------
		return new PageImpl<>(records, pageable, total == null ? 0L : total);
	}
	
	// ######################################################################################################################################################
															// SQL PARAMETER HELPERS
	// ######################################################################################################################################################
	
	public static boolean hasValue(String value) {
		return value != null && !value.trim().isEmpty();
	}

	public static void addLikeParameter(MapSqlParameterSource params, String key, String value) {
		params.addValue(key, "%" + value.trim() + "%");
	}

	public static void addIfNotNull(MapSqlParameterSource params, String parameterName, Object value) {
		if (value != null) {
			params.addValue(parameterName, value);
		}
	}

	// ######################################################################################################################################################
																// SQL CLAUSE HELPERS
	// ######################################################################################################################################################

	public static void appendEquals(StringBuilder sql, String columnName, String parameterName) {
		sql.append(" AND ").append(columnName).append(" = :").append(parameterName).append(" ");
	}
	
	
}
