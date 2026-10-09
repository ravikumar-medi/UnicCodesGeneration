package com.java.CodesGeneration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {

	@Value("${app.generated-by}")
	private String generatedBy;

	@Value("${app.version-name}")
	private String versionName;

	@Value("${app.version-code}")
	private String versionCode;

	@Value("${app.no-of-codes-for-lot}")
	private int noOfCodesForLot;

	@Value("${app.secondary.generated-by:c}")
	private String secondaryGeneratedBy;

	@Value("${app.secondary.version-name:v}")
	private String secondaryVersionName;

	@Value("${app.secondary.version-code:1}")
	private String secondaryVersionCode;

	@Value("${app.secondary.no-of-codes-for-lot:50}")
	private int secondaryNoOfCodesForLot;

	@Value("${app.success-message}")
	private String successMessage;

	@Value("${app.success-code}")
	private String successCode;

	@Value("${app.error-message}")
	private String errorMessage;

	@Value("${app.error-code}")
	private String errorCode;

	// Getters
	public String getGeneratedBy() {
		return generatedBy;
	}

	public String getVersionName() {
		return versionName;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public int getNoOfCodesForLot() {
		return noOfCodesForLot;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	public String getSuccessCode() {
		return successCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getSecondaryGeneratedBy() {
		return secondaryGeneratedBy;
	}

	public String getSecondaryVersionName() {
		return secondaryVersionName;
	}

	public String getSecondaryVersionCode() {
		return secondaryVersionCode;
	}

	public int getSecondaryNoOfCodesForLot() {
		return secondaryNoOfCodesForLot;
	}
}