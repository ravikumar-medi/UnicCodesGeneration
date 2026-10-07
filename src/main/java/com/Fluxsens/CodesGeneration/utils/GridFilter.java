package com.Fluxsens.CodesGeneration.utils;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

/**
 * This class is specially written for preparing dynamic query for Extjs grid
 * based filter Some of this parameters passed depends upon the manually written
 * file overrides.js
 */
@Repository
public class GridFilter {

	/**
	 * Generating query based on the start and limit parameters as well as different
	 * types of datatypes passed from the hashtable
	 * 
	 * @param start
	 * @param limit
	 * @param filterInfo
	 * @return filtered string
	 */
	public String generateQuery(String start, String limit, Hashtable<String, Hashtable<String, String>> filterInfo) {
		StringBuffer whereCond = new StringBuffer("");
		if (filterInfo != null) {
			Enumeration<String> keys = filterInfo.keys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				String fieldName = key.split("#")[1];
				Hashtable<String, String> filterDetails = filterInfo.get(key);

				if ("numeric".equals(filterDetails.get("DATATYPE")) || "number".equals(filterDetails.get("DATATYPE"))) {
					whereCond.append(" AND " + prepareForNumber(fieldName, filterDetails));
				} else if ("string".equals(filterDetails.get("DATATYPE"))) {
					whereCond.append(" AND " + prepareForString(fieldName, filterDetails));
				} else if ("date".equals(filterDetails.get("DATATYPE"))) {
					whereCond.append(" AND " + prepareForDate("DATE(" + fieldName + ")", filterDetails));
				} else if ("boolean".equals(filterDetails.get("DATATYPE"))) {
					whereCond.append(" AND " + prepareForBoolean(fieldName, filterDetails));
				} else if ("list".equals(filterDetails.get("DATATYPE"))) {
					whereCond.append(" AND " + prepareForList(fieldName, filterDetails));
				}
				/*
				 * else if ("month".equals(filterDetails.get("DATATYPE"))) {
				 * whereCond.append(" AND " + prepareForMonth(fieldName, filterDetails)); } else
				 * if ("year".equals(filterDetails.get("DATATYPE"))) { whereCond.append(" AND "
				 * + prepareForYear(fieldName, filterDetails)); }
				 */
			}

			return whereCond.toString();
		}

		return "";
	}

	public String generateSearchQuery(Hashtable<String, Hashtable<String, String>> searchInfo) {
		StringBuffer whereCond = new StringBuffer("");
		if (searchInfo != null && searchInfo.size() > 0) {
			whereCond.append(" AND (");
			Enumeration<String> keys = searchInfo.keys();
			int index = 0;
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				String fieldName = key.split("#")[1];
				Hashtable<String, String> filterDetails = searchInfo.get(key);

				if (index != 0) {
					whereCond.append(" OR ");
				}
				index++;

				if ("numeric".equals(filterDetails.get("DATATYPE")) || "number".equals(filterDetails.get("DATATYPE"))) {
					whereCond.append(prepareForNumber(fieldName, filterDetails));
				} else if ("string".equals(filterDetails.get("DATATYPE"))) {
					whereCond.append(prepareForString(fieldName, filterDetails));
				} else if ("date".equals(filterDetails.get("DATATYPE"))) {
					whereCond.append(prepareForDate("DATE(" + fieldName + ")", filterDetails));
				} else if ("boolean".equals(filterDetails.get("DATATYPE"))) {
					whereCond.append(prepareForBoolean(fieldName, filterDetails));
				} else if ("list".equals(filterDetails.get("DATATYPE"))) {
					whereCond.append(prepareForList(fieldName, filterDetails));
				}
			}

			whereCond.append(")");
			return whereCond.toString();
		}

		return "";
	}

	/**
	 * preparing string by appending list of values from the hashtable
	 * 
	 * @param fieldName
	 * @param filterDetails
	 * @return string from the list
	 */
	private String prepareForList(String fieldName, Map<String, String> filterDetails) {
		// return fieldName + " REGEXP ('" + filterDetails.get("SEARCHVALUE") + "')";
		return fieldName + " in (" + filterDetails.get("SEARCHVALUE") + ")";
	}

	/**
	 * preparing simple string based on the hashtable searchvalue
	 * 
	 * @param fieldName
	 * @param filterDetails
	 * @return string for like operator
	 */
	private String prepareForString(String fieldName, Map<String, String> filterDetails) {
		if ("eq".equals(filterDetails.get("OPERATOR"))) {
			/**
			 * Handling Manual Store Filter
			 */
			return fieldName + "='" + filterDetails.get("SEARCHVALUE").replace("'", "''") + "'";
		}
		return fieldName + " LIKE '%" + filterDetails.get("SEARCHVALUE").replace("'", "''") + "%'";
	}

	/**
	 * preparing month string based on the month values passed from the hashtable
	 * 
	 * @param fieldName
	 * @param filterDetails
	 * @return month based string
	 */
	@SuppressWarnings("unused")
	private String prepareForMonth(String fieldName, Map<String, String> filterDetails) {
		return "MONTH(" + fieldName + ") = " + filterDetails.get("SEARCHVALUE");
	}

	/**
	 * preparing year string based on the year values passed from the hashtable
	 * 
	 * @param fieldName
	 * @param filterDetails
	 * @return year based string
	 */
	@SuppressWarnings("unused")
	private String prepareForYear(String fieldName, Map<String, String> filterDetails) {
		return "YEAR(" + fieldName + ") = " + filterDetails.get("SEARCHVALUE");
	}

	/**
	 * preparing boolean string based on the boolean parameters passed from the
	 * hashtable
	 * 
	 * @param fieldName
	 * @param filterDetails
	 * @return filtered string of boolean type
	 */
	private String prepareForBoolean(String fieldName, Map<String, String> filterDetails) {
		String value = filterDetails.get("SEARCHVALUE");
		if ("true".equals(value)) {
			return fieldName + " = 1";
		} else if ("false".equals(value)) {
			return "(" + fieldName + " = 0 OR " + fieldName + " IS NULL)";
		}
		return "";
	}

	/**
	 * preparing number string based on the numbers passed from the hashtable
	 * 
	 * @param fieldName
	 * @param filterDetails
	 * @return
	 */
	private String prepareForNumber(String fieldName, Map<String, String> filterDetails) {
		String operator = filterDetails.get("OPERATOR");
		if ("eq".equals(operator)) {
			return fieldName + " = " + filterDetails.get("SEARCHVALUE");
		} else if ("lt".equals(operator)) {
			return fieldName + " < " + filterDetails.get("SEARCHVALUE");
		} else if ("gt".equals(operator)) {
			return fieldName + " > " + filterDetails.get("SEARCHVALUE");
		}
		return "";
	}

	/**
	 * preparing date string based on the date fields passed from the hashtable
	 * 
	 * @param fieldName
	 * @param filterDetails
	 * @return string of correponding date format
	 */
	private String prepareForDate(String fieldName, Map<String, String> filterDetails) {
		String operator = filterDetails.get("OPERATOR");
		String dateFormat = filterDetails.get("DATEFORMAT");

		if ("eq".equals(operator)) {
			return fieldName + " = '" + DateUtils.getSqlDateFromString(filterDetails.get("SEARCHVALUE"), dateFormat)
					+ "'";
		} else if ("lt".equals(operator)) {
			return fieldName + " < '" + DateUtils.getSqlDateFromString(filterDetails.get("SEARCHVALUE"), dateFormat)
					+ "'";
		} else if ("gt".equals(operator)) {
			return fieldName + " > '" + DateUtils.getSqlDateFromString(filterDetails.get("SEARCHVALUE"), dateFormat)
					+ "'";
		} else if ("le".equals(operator)) {
			return fieldName + " <= '" + DateUtils.getSqlDateFromString(filterDetails.get("SEARCHVALUE"), dateFormat)
					+ "'";
		} else if ("ge".equals(operator)) {
			return fieldName + " >= '" + DateUtils.getSqlDateFromString(filterDetails.get("SEARCHVALUE"), dateFormat)
					+ "'";
		}
		return "";
	}

	/**
	 * This is used to set prepare the filter string based on the parameters passed
	 * from the UI
	 * 
	 * @param requestInfo
	 * @param tableName
	 * @return sql string
	 */
	@SuppressWarnings("unchecked")
	public String prepareFilterString(List<Object> requestInfo, String tableName) {
		String filterString = "";
		String sortColumn = "id";
		String sortDirection = "DESC";

		String sql = null;
		List<String> sortInfo = (List<String>) requestInfo.get(0);
		filterString = (String) requestInfo.get(1);

		if (sortInfo.size() > 0) {
			sortColumn = sortInfo.get(0);
			sortDirection = sortInfo.get(1);
		}
		sql = "from " + tableName + " where 1=1 " + filterString + " order by " + sortColumn + " " + sortDirection;
		return sql;
	}

}
