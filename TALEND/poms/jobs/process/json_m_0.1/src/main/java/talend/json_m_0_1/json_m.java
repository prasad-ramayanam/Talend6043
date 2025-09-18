
package talend.json_m_0_1;

import routines.Numeric;
import routines.DataOperation;
import routines.TalendDataGenerator;
import routines.TalendStringUtil;
import routines.TalendString;
import routines.MDM;
import routines.StringHandling;
import routines.Relational;
import routines.TalendDate;
import routines.Mathematical;
import routines.SQLike;
import routines.system.*;
import routines.system.api.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.Comparator;

@SuppressWarnings("unused")

/**
 * Job: json_m Purpose: <br>
 * Description: <br>
 * 
 * @author R, lata
 * @version 8.0.1.20250730_0900-patch
 * @status
 */
public class json_m implements TalendJob {
	static {
		System.setProperty("TalendJob.log", "json_m.log");
	}

	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(json_m.class);

	static {
		String javaUtilLoggingConfigFile = System.getProperty("java.util.logging.config.file");
		if (javaUtilLoggingConfigFile == null) {
			setupDefaultJavaUtilLogging();
		}
	}

	/**
	 * This class replaces the default {@code System.err} stream used by Java Util
	 * Logging (JUL). You can use your own configuration through the
	 * {@code java.util.logging.config.file} system property, enabling you to
	 * specify an external logging configuration file for tailored logging setup.
	 */
	public static class StandardConsoleHandler extends java.util.logging.StreamHandler {
		public StandardConsoleHandler() {
			// Set System.out as default log output stream
			super(System.out, new java.util.logging.SimpleFormatter());
		}

		/**
		 * Publish a {@code LogRecord}. The logging request was made initially to a
		 * {@code Logger} object, which initialized the {@code LogRecord} and forwarded
		 * it here.
		 *
		 * @param record description of the log event. A null record is silently ignored
		 *               and is not published
		 */
		@Override
		public void publish(java.util.logging.LogRecord record) {
			super.publish(record);
			flush();
		}

		/**
		 * Override {@code StreamHandler.close} to do a flush but not to close the
		 * output stream. That is, we do <b>not</b> close {@code System.out}.
		 */
		@Override
		public void close() {
			flush();
		}
	}

	protected static void setupDefaultJavaUtilLogging() {
		java.util.logging.LogManager logManager = java.util.logging.LogManager.getLogManager();

		// Get the root logger
		java.util.logging.Logger rootLogger = logManager.getLogger("");

		// Remove existing handlers to set standard console handler only
		java.util.logging.Handler[] handlers = rootLogger.getHandlers();
		for (java.util.logging.Handler handler : handlers) {
			rootLogger.removeHandler(handler);
		}

		rootLogger.addHandler(new StandardConsoleHandler());
		rootLogger.setLevel(java.util.logging.Level.INFO);
	}

	protected static boolean isCBPClientPresent() {
		boolean isCBPClientPresent = false;
		try {
			Class.forName("org.talend.metrics.CBPClient");
			isCBPClientPresent = true;
		} catch (java.lang.ClassNotFoundException e) {
		}
		return isCBPClientPresent;
	}

	protected static void logIgnoredError(String message, Throwable cause) {
		log.error(message, cause);

	}

	public final Object obj = new Object();

	// for transmiting parameters purpose
	private Object valueObject = null;

	public Object getValueObject() {
		return this.valueObject;
	}

	public void setValueObject(Object valueObject) {
		this.valueObject = valueObject;
	}

	private final static String defaultCharset = java.nio.charset.Charset.defaultCharset().name();

	private final static String utf8Charset = "UTF-8";

	public static String taskExecutionId = null;

	public static String jobExecutionId = java.util.UUID.randomUUID().toString();;

	private final static boolean isCBPClientPresent = isCBPClientPresent();

	// contains type for every context property
	public class PropertiesWithType extends java.util.Properties {
		private static final long serialVersionUID = 1L;
		private java.util.Map<String, String> propertyTypes = new java.util.HashMap<>();

		public PropertiesWithType(java.util.Properties properties) {
			super(properties);
		}

		public PropertiesWithType() {
			super();
		}

		public void setContextType(String key, String type) {
			propertyTypes.put(key, type);
		}

		public String getContextType(String key) {
			return propertyTypes.get(key);
		}
	}

	// create and load default properties
	private java.util.Properties defaultProps = new java.util.Properties();

	// create application properties with default
	public class ContextProperties extends PropertiesWithType {

		private static final long serialVersionUID = 1L;

		public ContextProperties(java.util.Properties properties) {
			super(properties);
		}

		public ContextProperties() {
			super();
		}

		public void synchronizeContext() {

		}

		// if the stored or passed value is "<TALEND_NULL>" string, it mean null
		public String getStringValue(String key) {
			String origin_value = this.getProperty(key);
			if (NULL_VALUE_EXPRESSION_IN_COMMAND_STRING_FOR_CHILD_JOB_ONLY.equals(origin_value)) {
				return null;
			}
			return origin_value;
		}

	}

	protected ContextProperties context = new ContextProperties(); // will be instanciated by MS.

	public ContextProperties getContext() {
		return this.context;
	}

	protected java.util.Map<String, String> defaultProperties = new java.util.HashMap<String, String>();
	protected java.util.Map<String, String> additionalProperties = new java.util.HashMap<String, String>();

	public java.util.Map<String, String> getDefaultProperties() {
		return this.defaultProperties;
	}

	public java.util.Map<String, String> getAdditionalProperties() {
		return this.additionalProperties;
	}

	private final String jobVersion = "0.1";
	private final String jobName = "json_m";
	private final String projectName = "TALEND";
	public Integer errorCode = null;
	private String currentComponent = "";
	public static boolean isStandaloneMS = Boolean.valueOf("false");

	private void s(final String component) {
		try {
			org.talend.metrics.DataReadTracker.setCurrentComponent(jobName, component);
		} catch (Exception | NoClassDefFoundError e) {
			// ignore
		}
	}

	private void mdc(final String subJobName, final String subJobPidPrefix) {
		mdcInfo.forEach(org.slf4j.MDC::put);
		org.slf4j.MDC.put("_subJobName", subJobName);
		org.slf4j.MDC.put("_subJobPid", subJobPidPrefix + subJobPidCounter.getAndIncrement());
	}

	private void sh(final String componentId) {
		ok_Hash.put(componentId, false);
		start_Hash.put(componentId, System.currentTimeMillis());
	}

	{
		s("none");
	}

	private String cLabel = null;

	private final java.util.Map<String, Object> globalMap = new java.util.HashMap<String, Object>();
	private final static java.util.Map<String, Object> junitGlobalMap = new java.util.HashMap<String, Object>();

	private final java.util.Map<String, Long> start_Hash = new java.util.HashMap<String, Long>();
	private final java.util.Map<String, Long> end_Hash = new java.util.HashMap<String, Long>();
	private final java.util.Map<String, Boolean> ok_Hash = new java.util.HashMap<String, Boolean>();
	public final java.util.List<String[]> globalBuffer = new java.util.ArrayList<String[]>();

	private final JobStructureCatcherUtils talendJobLog = new JobStructureCatcherUtils(jobName,
			"_RPAwwJL1EfCnV4-ejo1b-g", "0.1");
	private org.talend.job.audit.JobAuditLogger auditLogger_talendJobLog = null;

	private RunStat runStat = new RunStat(talendJobLog, System.getProperty("audit.interval"));

	// OSGi DataSource
	private final static String KEY_DB_DATASOURCES = "KEY_DB_DATASOURCES";

	private final static String KEY_DB_DATASOURCES_RAW = "KEY_DB_DATASOURCES_RAW";

	public void setDataSources(java.util.Map<String, javax.sql.DataSource> dataSources) {
		java.util.Map<String, routines.system.TalendDataSource> talendDataSources = new java.util.HashMap<String, routines.system.TalendDataSource>();
		for (java.util.Map.Entry<String, javax.sql.DataSource> dataSourceEntry : dataSources.entrySet()) {
			talendDataSources.put(dataSourceEntry.getKey(),
					new routines.system.TalendDataSource(dataSourceEntry.getValue()));
		}
		globalMap.put(KEY_DB_DATASOURCES, talendDataSources);
		globalMap.put(KEY_DB_DATASOURCES_RAW, new java.util.HashMap<String, javax.sql.DataSource>(dataSources));
	}

	public void setDataSourceReferences(List serviceReferences) throws Exception {

		java.util.Map<String, routines.system.TalendDataSource> talendDataSources = new java.util.HashMap<String, routines.system.TalendDataSource>();
		java.util.Map<String, javax.sql.DataSource> dataSources = new java.util.HashMap<String, javax.sql.DataSource>();

		for (java.util.Map.Entry<String, javax.sql.DataSource> entry : BundleUtils
				.getServices(serviceReferences, javax.sql.DataSource.class).entrySet()) {
			dataSources.put(entry.getKey(), entry.getValue());
			talendDataSources.put(entry.getKey(), new routines.system.TalendDataSource(entry.getValue()));
		}

		globalMap.put(KEY_DB_DATASOURCES, talendDataSources);
		globalMap.put(KEY_DB_DATASOURCES_RAW, new java.util.HashMap<String, javax.sql.DataSource>(dataSources));
	}

	private final java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
	private final java.io.PrintStream errorMessagePS = new java.io.PrintStream(new java.io.BufferedOutputStream(baos));

	public String getExceptionStackTrace() {
		if ("failure".equals(this.getStatus())) {
			errorMessagePS.flush();
			return baos.toString();
		}
		return null;
	}

	private Exception exception;

	public Exception getException() {
		if ("failure".equals(this.getStatus())) {
			return this.exception;
		}
		return null;
	}

	private class TalendException extends Exception {

		private static final long serialVersionUID = 1L;

		private java.util.Map<String, Object> globalMap = null;
		private Exception e = null;

		private String currentComponent = null;
		private String cLabel = null;

		private String virtualComponentName = null;

		public void setVirtualComponentName(String virtualComponentName) {
			this.virtualComponentName = virtualComponentName;
		}

		private TalendException(Exception e, String errorComponent, final java.util.Map<String, Object> globalMap) {
			this.currentComponent = errorComponent;
			this.globalMap = globalMap;
			this.e = e;
		}

		private TalendException(Exception e, String errorComponent, String errorComponentLabel,
				final java.util.Map<String, Object> globalMap) {
			this(e, errorComponent, globalMap);
			this.cLabel = errorComponentLabel;
		}

		public Exception getException() {
			return this.e;
		}

		public String getCurrentComponent() {
			return this.currentComponent;
		}

		public String getExceptionCauseMessage(Exception e) {
			Throwable cause = e;
			String message = null;
			int i = 10;
			while (null != cause && 0 < i--) {
				message = cause.getMessage();
				if (null == message) {
					cause = cause.getCause();
				} else {
					break;
				}
			}
			if (null == message) {
				message = e.getClass().getName();
			}
			return message;
		}

		@Override
		public void printStackTrace() {
			if (!(e instanceof TalendException || e instanceof TDieException)) {
				if (virtualComponentName != null && currentComponent.indexOf(virtualComponentName + "_") == 0) {
					globalMap.put(virtualComponentName + "_ERROR_MESSAGE", getExceptionCauseMessage(e));
				}
				globalMap.put(currentComponent + "_ERROR_MESSAGE", getExceptionCauseMessage(e));
				System.err.println("Exception in component " + currentComponent + " (" + jobName + ")");
			}
			if (!(e instanceof TDieException)) {
				if (e instanceof TalendException) {
					e.printStackTrace();
				} else {
					e.printStackTrace();
					e.printStackTrace(errorMessagePS);
				}
			}
			if (!(e instanceof TalendException)) {
				json_m.this.exception = e;
			}
			if (!(e instanceof TalendException)) {
				try {
					for (java.lang.reflect.Method m : this.getClass().getEnclosingClass().getMethods()) {
						if (m.getName().compareTo(currentComponent + "_error") == 0) {
							m.invoke(json_m.this, new Object[] { e, currentComponent, globalMap });
							break;
						}
					}

					if (!(e instanceof TDieException)) {
						if (enableLogStash) {
							talendJobLog.addJobExceptionMessage(currentComponent, cLabel, null, e);
							talendJobLogProcess(globalMap);
						}
					}
				} catch (Exception e) {
					this.e.printStackTrace();
				}
			}
		}
	}

	public void tFileInputJSON_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tFileInputJSON_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tConvertType_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tFileInputJSON_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tLogRow_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tFileInputJSON_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void talendJobLog_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		talendJobLog_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tFileInputJSON_1_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void talendJobLog_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public static class row2Struct implements routines.system.IPersistableRow<row2Struct> {
		final static byte[] commonByteArrayLock_TALEND_json_m = new byte[0];
		static byte[] commonByteArray_TALEND_json_m = new byte[0];

		public Integer employee_id;

		public Integer getEmployee_id() {
			return this.employee_id;
		}

		public Boolean employee_idIsNullable() {
			return true;
		}

		public Boolean employee_idIsKey() {
			return false;
		}

		public Integer employee_idLength() {
			return 3;
		}

		public Integer employee_idPrecision() {
			return 0;
		}

		public String employee_idDefault() {

			return null;

		}

		public String employee_idComment() {

			return "";

		}

		public String employee_idPattern() {

			return "dd-MM-yyyy";

		}

		public String employee_idOriginalDbColumnName() {

			return "employee_id";

		}

		public String first_name;

		public String getFirst_name() {
			return this.first_name;
		}

		public Boolean first_nameIsNullable() {
			return true;
		}

		public Boolean first_nameIsKey() {
			return false;
		}

		public Integer first_nameLength() {
			return 11;
		}

		public Integer first_namePrecision() {
			return 0;
		}

		public String first_nameDefault() {

			return null;

		}

		public String first_nameComment() {

			return "";

		}

		public String first_namePattern() {

			return "dd-MM-yyyy";

		}

		public String first_nameOriginalDbColumnName() {

			return "first_name";

		}

		public String last_name;

		public String getLast_name() {
			return this.last_name;
		}

		public Boolean last_nameIsNullable() {
			return true;
		}

		public Boolean last_nameIsKey() {
			return false;
		}

		public Integer last_nameLength() {
			return 11;
		}

		public Integer last_namePrecision() {
			return 0;
		}

		public String last_nameDefault() {

			return null;

		}

		public String last_nameComment() {

			return "";

		}

		public String last_namePattern() {

			return "dd-MM-yyyy";

		}

		public String last_nameOriginalDbColumnName() {

			return "last_name";

		}

		public String email;

		public String getEmail() {
			return this.email;
		}

		public Boolean emailIsNullable() {
			return true;
		}

		public Boolean emailIsKey() {
			return false;
		}

		public Integer emailLength() {
			return 9;
		}

		public Integer emailPrecision() {
			return 0;
		}

		public String emailDefault() {

			return null;

		}

		public String emailComment() {

			return "";

		}

		public String emailPattern() {

			return "dd-MM-yyyy";

		}

		public String emailOriginalDbColumnName() {

			return "email";

		}

		public String phone_number;

		public String getPhone_number() {
			return this.phone_number;
		}

		public Boolean phone_numberIsNullable() {
			return true;
		}

		public Boolean phone_numberIsKey() {
			return false;
		}

		public Integer phone_numberLength() {
			return 14;
		}

		public Integer phone_numberPrecision() {
			return 0;
		}

		public String phone_numberDefault() {

			return null;

		}

		public String phone_numberComment() {

			return "";

		}

		public String phone_numberPattern() {

			return "dd-MM-yyyy";

		}

		public String phone_numberOriginalDbColumnName() {

			return "phone_number";

		}

		public String hire_date;

		public String getHire_date() {
			return this.hire_date;
		}

		public Boolean hire_dateIsNullable() {
			return true;
		}

		public Boolean hire_dateIsKey() {
			return false;
		}

		public Integer hire_dateLength() {
			return 20;
		}

		public Integer hire_datePrecision() {
			return 0;
		}

		public String hire_dateDefault() {

			return null;

		}

		public String hire_dateComment() {

			return "";

		}

		public String hire_datePattern() {

			return "dd-MM-yyyy";

		}

		public String hire_dateOriginalDbColumnName() {

			return "hire_date";

		}

		public String job_id;

		public String getJob_id() {
			return this.job_id;
		}

		public Boolean job_idIsNullable() {
			return true;
		}

		public Boolean job_idIsKey() {
			return false;
		}

		public Integer job_idLength() {
			return 10;
		}

		public Integer job_idPrecision() {
			return 0;
		}

		public String job_idDefault() {

			return null;

		}

		public String job_idComment() {

			return "";

		}

		public String job_idPattern() {

			return "dd-MM-yyyy";

		}

		public String job_idOriginalDbColumnName() {

			return "job_id";

		}

		public Integer salary;

		public Integer getSalary() {
			return this.salary;
		}

		public Boolean salaryIsNullable() {
			return true;
		}

		public Boolean salaryIsKey() {
			return false;
		}

		public Integer salaryLength() {
			return 5;
		}

		public Integer salaryPrecision() {
			return 0;
		}

		public String salaryDefault() {

			return null;

		}

		public String salaryComment() {

			return "";

		}

		public String salaryPattern() {

			return "dd-MM-yyyy";

		}

		public String salaryOriginalDbColumnName() {

			return "salary";

		}

		public String commission_pct;

		public String getCommission_pct() {
			return this.commission_pct;
		}

		public Boolean commission_pctIsNullable() {
			return true;
		}

		public Boolean commission_pctIsKey() {
			return false;
		}

		public Integer commission_pctLength() {
			return 4;
		}

		public Integer commission_pctPrecision() {
			return 0;
		}

		public String commission_pctDefault() {

			return null;

		}

		public String commission_pctComment() {

			return "";

		}

		public String commission_pctPattern() {

			return "dd-MM-yyyy";

		}

		public String commission_pctOriginalDbColumnName() {

			return "commission_pct";

		}

		public String manager_id;

		public String getManager_id() {
			return this.manager_id;
		}

		public Boolean manager_idIsNullable() {
			return true;
		}

		public Boolean manager_idIsKey() {
			return false;
		}

		public Integer manager_idLength() {
			return 3;
		}

		public Integer manager_idPrecision() {
			return 0;
		}

		public String manager_idDefault() {

			return null;

		}

		public String manager_idComment() {

			return "";

		}

		public String manager_idPattern() {

			return "dd-MM-yyyy";

		}

		public String manager_idOriginalDbColumnName() {

			return "manager_id";

		}

		public String department_id;

		public String getDepartment_id() {
			return this.department_id;
		}

		public Boolean department_idIsNullable() {
			return true;
		}

		public Boolean department_idIsKey() {
			return false;
		}

		public Integer department_idLength() {
			return 3;
		}

		public Integer department_idPrecision() {
			return 0;
		}

		public String department_idDefault() {

			return null;

		}

		public String department_idComment() {

			return "";

		}

		public String department_idPattern() {

			return "dd-MM-yyyy";

		}

		public String department_idOriginalDbColumnName() {

			return "department_id";

		}

		private Integer readInteger(ObjectInputStream dis) throws IOException {
			Integer intReturn;
			int length = 0;
			length = dis.readByte();
			if (length == -1) {
				intReturn = null;
			} else {
				intReturn = dis.readInt();
			}
			return intReturn;
		}

		private Integer readInteger(org.jboss.marshalling.Unmarshaller dis) throws IOException {
			Integer intReturn;
			int length = 0;
			length = dis.readByte();
			if (length == -1) {
				intReturn = null;
			} else {
				intReturn = dis.readInt();
			}
			return intReturn;
		}

		private void writeInteger(Integer intNum, ObjectOutputStream dos) throws IOException {
			if (intNum == null) {
				dos.writeByte(-1);
			} else {
				dos.writeByte(0);
				dos.writeInt(intNum);
			}
		}

		private void writeInteger(Integer intNum, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (intNum == null) {
				marshaller.writeByte(-1);
			} else {
				marshaller.writeByte(0);
				marshaller.writeInt(intNum);
			}
		}

		private String readString(ObjectInputStream dis) throws IOException {
			String strReturn = null;
			int length = 0;
			length = dis.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_TALEND_json_m.length) {
					if (length < 1024 && commonByteArray_TALEND_json_m.length == 0) {
						commonByteArray_TALEND_json_m = new byte[1024];
					} else {
						commonByteArray_TALEND_json_m = new byte[2 * length];
					}
				}
				dis.readFully(commonByteArray_TALEND_json_m, 0, length);
				strReturn = new String(commonByteArray_TALEND_json_m, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private String readString(org.jboss.marshalling.Unmarshaller unmarshaller) throws IOException {
			String strReturn = null;
			int length = 0;
			length = unmarshaller.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_TALEND_json_m.length) {
					if (length < 1024 && commonByteArray_TALEND_json_m.length == 0) {
						commonByteArray_TALEND_json_m = new byte[1024];
					} else {
						commonByteArray_TALEND_json_m = new byte[2 * length];
					}
				}
				unmarshaller.readFully(commonByteArray_TALEND_json_m, 0, length);
				strReturn = new String(commonByteArray_TALEND_json_m, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private void writeString(String str, ObjectOutputStream dos) throws IOException {
			if (str == null) {
				dos.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				dos.writeInt(byteArray.length);
				dos.write(byteArray);
			}
		}

		private void writeString(String str, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (str == null) {
				marshaller.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				marshaller.writeInt(byteArray.length);
				marshaller.write(byteArray);
			}
		}

		public void readData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock_TALEND_json_m) {

				try {

					int length = 0;

					this.employee_id = readInteger(dis);

					this.first_name = readString(dis);

					this.last_name = readString(dis);

					this.email = readString(dis);

					this.phone_number = readString(dis);

					this.hire_date = readString(dis);

					this.job_id = readString(dis);

					this.salary = readInteger(dis);

					this.commission_pct = readString(dis);

					this.manager_id = readString(dis);

					this.department_id = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void readData(org.jboss.marshalling.Unmarshaller dis) {

			synchronized (commonByteArrayLock_TALEND_json_m) {

				try {

					int length = 0;

					this.employee_id = readInteger(dis);

					this.first_name = readString(dis);

					this.last_name = readString(dis);

					this.email = readString(dis);

					this.phone_number = readString(dis);

					this.hire_date = readString(dis);

					this.job_id = readString(dis);

					this.salary = readInteger(dis);

					this.commission_pct = readString(dis);

					this.manager_id = readString(dis);

					this.department_id = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// Integer

				writeInteger(this.employee_id, dos);

				// String

				writeString(this.first_name, dos);

				// String

				writeString(this.last_name, dos);

				// String

				writeString(this.email, dos);

				// String

				writeString(this.phone_number, dos);

				// String

				writeString(this.hire_date, dos);

				// String

				writeString(this.job_id, dos);

				// Integer

				writeInteger(this.salary, dos);

				// String

				writeString(this.commission_pct, dos);

				// String

				writeString(this.manager_id, dos);

				// String

				writeString(this.department_id, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public void writeData(org.jboss.marshalling.Marshaller dos) {
			try {

				// Integer

				writeInteger(this.employee_id, dos);

				// String

				writeString(this.first_name, dos);

				// String

				writeString(this.last_name, dos);

				// String

				writeString(this.email, dos);

				// String

				writeString(this.phone_number, dos);

				// String

				writeString(this.hire_date, dos);

				// String

				writeString(this.job_id, dos);

				// Integer

				writeInteger(this.salary, dos);

				// String

				writeString(this.commission_pct, dos);

				// String

				writeString(this.manager_id, dos);

				// String

				writeString(this.department_id, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("employee_id=" + String.valueOf(employee_id));
			sb.append(",first_name=" + first_name);
			sb.append(",last_name=" + last_name);
			sb.append(",email=" + email);
			sb.append(",phone_number=" + phone_number);
			sb.append(",hire_date=" + hire_date);
			sb.append(",job_id=" + job_id);
			sb.append(",salary=" + String.valueOf(salary));
			sb.append(",commission_pct=" + commission_pct);
			sb.append(",manager_id=" + manager_id);
			sb.append(",department_id=" + department_id);
			sb.append("]");

			return sb.toString();
		}

		public String toLogString() {
			StringBuilder sb = new StringBuilder();

			if (employee_id == null) {
				sb.append("<null>");
			} else {
				sb.append(employee_id);
			}

			sb.append("|");

			if (first_name == null) {
				sb.append("<null>");
			} else {
				sb.append(first_name);
			}

			sb.append("|");

			if (last_name == null) {
				sb.append("<null>");
			} else {
				sb.append(last_name);
			}

			sb.append("|");

			if (email == null) {
				sb.append("<null>");
			} else {
				sb.append(email);
			}

			sb.append("|");

			if (phone_number == null) {
				sb.append("<null>");
			} else {
				sb.append(phone_number);
			}

			sb.append("|");

			if (hire_date == null) {
				sb.append("<null>");
			} else {
				sb.append(hire_date);
			}

			sb.append("|");

			if (job_id == null) {
				sb.append("<null>");
			} else {
				sb.append(job_id);
			}

			sb.append("|");

			if (salary == null) {
				sb.append("<null>");
			} else {
				sb.append(salary);
			}

			sb.append("|");

			if (commission_pct == null) {
				sb.append("<null>");
			} else {
				sb.append(commission_pct);
			}

			sb.append("|");

			if (manager_id == null) {
				sb.append("<null>");
			} else {
				sb.append(manager_id);
			}

			sb.append("|");

			if (department_id == null) {
				sb.append("<null>");
			} else {
				sb.append(department_id);
			}

			sb.append("|");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(row2Struct other) {

			int returnValue = -1;

			return returnValue;
		}

		private int checkNullsAndCompare(Object object1, Object object2) {
			int returnValue = 0;
			if (object1 instanceof Comparable && object2 instanceof Comparable) {
				returnValue = ((Comparable) object1).compareTo(object2);
			} else if (object1 != null && object2 != null) {
				returnValue = compareStrings(object1.toString(), object2.toString());
			} else if (object1 == null && object2 != null) {
				returnValue = 1;
			} else if (object1 != null && object2 == null) {
				returnValue = -1;
			} else {
				returnValue = 0;
			}

			return returnValue;
		}

		private int compareStrings(String string1, String string2) {
			return string1.compareTo(string2);
		}

	}

	public static class row1Struct implements routines.system.IPersistableRow<row1Struct> {
		final static byte[] commonByteArrayLock_TALEND_json_m = new byte[0];
		static byte[] commonByteArray_TALEND_json_m = new byte[0];

		public Integer employee_id;

		public Integer getEmployee_id() {
			return this.employee_id;
		}

		public Boolean employee_idIsNullable() {
			return true;
		}

		public Boolean employee_idIsKey() {
			return false;
		}

		public Integer employee_idLength() {
			return 3;
		}

		public Integer employee_idPrecision() {
			return 0;
		}

		public String employee_idDefault() {

			return null;

		}

		public String employee_idComment() {

			return "";

		}

		public String employee_idPattern() {

			return "dd-MM-yyyy";

		}

		public String employee_idOriginalDbColumnName() {

			return "employee_id";

		}

		public String first_name;

		public String getFirst_name() {
			return this.first_name;
		}

		public Boolean first_nameIsNullable() {
			return true;
		}

		public Boolean first_nameIsKey() {
			return false;
		}

		public Integer first_nameLength() {
			return 11;
		}

		public Integer first_namePrecision() {
			return 0;
		}

		public String first_nameDefault() {

			return null;

		}

		public String first_nameComment() {

			return "";

		}

		public String first_namePattern() {

			return "dd-MM-yyyy";

		}

		public String first_nameOriginalDbColumnName() {

			return "first_name";

		}

		public String last_name;

		public String getLast_name() {
			return this.last_name;
		}

		public Boolean last_nameIsNullable() {
			return true;
		}

		public Boolean last_nameIsKey() {
			return false;
		}

		public Integer last_nameLength() {
			return 11;
		}

		public Integer last_namePrecision() {
			return 0;
		}

		public String last_nameDefault() {

			return null;

		}

		public String last_nameComment() {

			return "";

		}

		public String last_namePattern() {

			return "dd-MM-yyyy";

		}

		public String last_nameOriginalDbColumnName() {

			return "last_name";

		}

		public String email;

		public String getEmail() {
			return this.email;
		}

		public Boolean emailIsNullable() {
			return true;
		}

		public Boolean emailIsKey() {
			return false;
		}

		public Integer emailLength() {
			return 9;
		}

		public Integer emailPrecision() {
			return 0;
		}

		public String emailDefault() {

			return null;

		}

		public String emailComment() {

			return "";

		}

		public String emailPattern() {

			return "dd-MM-yyyy";

		}

		public String emailOriginalDbColumnName() {

			return "email";

		}

		public String phone_number;

		public String getPhone_number() {
			return this.phone_number;
		}

		public Boolean phone_numberIsNullable() {
			return true;
		}

		public Boolean phone_numberIsKey() {
			return false;
		}

		public Integer phone_numberLength() {
			return 14;
		}

		public Integer phone_numberPrecision() {
			return 0;
		}

		public String phone_numberDefault() {

			return null;

		}

		public String phone_numberComment() {

			return "";

		}

		public String phone_numberPattern() {

			return "dd-MM-yyyy";

		}

		public String phone_numberOriginalDbColumnName() {

			return "phone_number";

		}

		public java.util.Date hire_date;

		public java.util.Date getHire_date() {
			return this.hire_date;
		}

		public Boolean hire_dateIsNullable() {
			return true;
		}

		public Boolean hire_dateIsKey() {
			return false;
		}

		public Integer hire_dateLength() {
			return 20;
		}

		public Integer hire_datePrecision() {
			return 0;
		}

		public String hire_dateDefault() {

			return null;

		}

		public String hire_dateComment() {

			return "";

		}

		public String hire_datePattern() {

			return "dd-MM-yyyy";

		}

		public String hire_dateOriginalDbColumnName() {

			return "hire_date";

		}

		public String job_id;

		public String getJob_id() {
			return this.job_id;
		}

		public Boolean job_idIsNullable() {
			return true;
		}

		public Boolean job_idIsKey() {
			return false;
		}

		public Integer job_idLength() {
			return 10;
		}

		public Integer job_idPrecision() {
			return 0;
		}

		public String job_idDefault() {

			return null;

		}

		public String job_idComment() {

			return "";

		}

		public String job_idPattern() {

			return "dd-MM-yyyy";

		}

		public String job_idOriginalDbColumnName() {

			return "job_id";

		}

		public Integer salary;

		public Integer getSalary() {
			return this.salary;
		}

		public Boolean salaryIsNullable() {
			return true;
		}

		public Boolean salaryIsKey() {
			return false;
		}

		public Integer salaryLength() {
			return 5;
		}

		public Integer salaryPrecision() {
			return 0;
		}

		public String salaryDefault() {

			return null;

		}

		public String salaryComment() {

			return "";

		}

		public String salaryPattern() {

			return "dd-MM-yyyy";

		}

		public String salaryOriginalDbColumnName() {

			return "salary";

		}

		public String commission_pct;

		public String getCommission_pct() {
			return this.commission_pct;
		}

		public Boolean commission_pctIsNullable() {
			return true;
		}

		public Boolean commission_pctIsKey() {
			return false;
		}

		public Integer commission_pctLength() {
			return 4;
		}

		public Integer commission_pctPrecision() {
			return 0;
		}

		public String commission_pctDefault() {

			return null;

		}

		public String commission_pctComment() {

			return "";

		}

		public String commission_pctPattern() {

			return "dd-MM-yyyy";

		}

		public String commission_pctOriginalDbColumnName() {

			return "commission_pct";

		}

		public String manager_id;

		public String getManager_id() {
			return this.manager_id;
		}

		public Boolean manager_idIsNullable() {
			return true;
		}

		public Boolean manager_idIsKey() {
			return false;
		}

		public Integer manager_idLength() {
			return 3;
		}

		public Integer manager_idPrecision() {
			return 0;
		}

		public String manager_idDefault() {

			return null;

		}

		public String manager_idComment() {

			return "";

		}

		public String manager_idPattern() {

			return "dd-MM-yyyy";

		}

		public String manager_idOriginalDbColumnName() {

			return "manager_id";

		}

		public String department_id;

		public String getDepartment_id() {
			return this.department_id;
		}

		public Boolean department_idIsNullable() {
			return true;
		}

		public Boolean department_idIsKey() {
			return false;
		}

		public Integer department_idLength() {
			return 3;
		}

		public Integer department_idPrecision() {
			return 0;
		}

		public String department_idDefault() {

			return null;

		}

		public String department_idComment() {

			return "";

		}

		public String department_idPattern() {

			return "dd-MM-yyyy";

		}

		public String department_idOriginalDbColumnName() {

			return "department_id";

		}

		private Integer readInteger(ObjectInputStream dis) throws IOException {
			Integer intReturn;
			int length = 0;
			length = dis.readByte();
			if (length == -1) {
				intReturn = null;
			} else {
				intReturn = dis.readInt();
			}
			return intReturn;
		}

		private Integer readInteger(org.jboss.marshalling.Unmarshaller dis) throws IOException {
			Integer intReturn;
			int length = 0;
			length = dis.readByte();
			if (length == -1) {
				intReturn = null;
			} else {
				intReturn = dis.readInt();
			}
			return intReturn;
		}

		private void writeInteger(Integer intNum, ObjectOutputStream dos) throws IOException {
			if (intNum == null) {
				dos.writeByte(-1);
			} else {
				dos.writeByte(0);
				dos.writeInt(intNum);
			}
		}

		private void writeInteger(Integer intNum, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (intNum == null) {
				marshaller.writeByte(-1);
			} else {
				marshaller.writeByte(0);
				marshaller.writeInt(intNum);
			}
		}

		private String readString(ObjectInputStream dis) throws IOException {
			String strReturn = null;
			int length = 0;
			length = dis.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_TALEND_json_m.length) {
					if (length < 1024 && commonByteArray_TALEND_json_m.length == 0) {
						commonByteArray_TALEND_json_m = new byte[1024];
					} else {
						commonByteArray_TALEND_json_m = new byte[2 * length];
					}
				}
				dis.readFully(commonByteArray_TALEND_json_m, 0, length);
				strReturn = new String(commonByteArray_TALEND_json_m, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private String readString(org.jboss.marshalling.Unmarshaller unmarshaller) throws IOException {
			String strReturn = null;
			int length = 0;
			length = unmarshaller.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_TALEND_json_m.length) {
					if (length < 1024 && commonByteArray_TALEND_json_m.length == 0) {
						commonByteArray_TALEND_json_m = new byte[1024];
					} else {
						commonByteArray_TALEND_json_m = new byte[2 * length];
					}
				}
				unmarshaller.readFully(commonByteArray_TALEND_json_m, 0, length);
				strReturn = new String(commonByteArray_TALEND_json_m, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private void writeString(String str, ObjectOutputStream dos) throws IOException {
			if (str == null) {
				dos.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				dos.writeInt(byteArray.length);
				dos.write(byteArray);
			}
		}

		private void writeString(String str, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (str == null) {
				marshaller.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				marshaller.writeInt(byteArray.length);
				marshaller.write(byteArray);
			}
		}

		private java.util.Date readDate(ObjectInputStream dis) throws IOException {
			java.util.Date dateReturn = null;
			int length = 0;
			length = dis.readByte();
			if (length == -1) {
				dateReturn = null;
			} else {
				dateReturn = new Date(dis.readLong());
			}
			return dateReturn;
		}

		private java.util.Date readDate(org.jboss.marshalling.Unmarshaller unmarshaller) throws IOException {
			java.util.Date dateReturn = null;
			int length = 0;
			length = unmarshaller.readByte();
			if (length == -1) {
				dateReturn = null;
			} else {
				dateReturn = new Date(unmarshaller.readLong());
			}
			return dateReturn;
		}

		private void writeDate(java.util.Date date1, ObjectOutputStream dos) throws IOException {
			if (date1 == null) {
				dos.writeByte(-1);
			} else {
				dos.writeByte(0);
				dos.writeLong(date1.getTime());
			}
		}

		private void writeDate(java.util.Date date1, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (date1 == null) {
				marshaller.writeByte(-1);
			} else {
				marshaller.writeByte(0);
				marshaller.writeLong(date1.getTime());
			}
		}

		public void readData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock_TALEND_json_m) {

				try {

					int length = 0;

					this.employee_id = readInteger(dis);

					this.first_name = readString(dis);

					this.last_name = readString(dis);

					this.email = readString(dis);

					this.phone_number = readString(dis);

					this.hire_date = readDate(dis);

					this.job_id = readString(dis);

					this.salary = readInteger(dis);

					this.commission_pct = readString(dis);

					this.manager_id = readString(dis);

					this.department_id = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void readData(org.jboss.marshalling.Unmarshaller dis) {

			synchronized (commonByteArrayLock_TALEND_json_m) {

				try {

					int length = 0;

					this.employee_id = readInteger(dis);

					this.first_name = readString(dis);

					this.last_name = readString(dis);

					this.email = readString(dis);

					this.phone_number = readString(dis);

					this.hire_date = readDate(dis);

					this.job_id = readString(dis);

					this.salary = readInteger(dis);

					this.commission_pct = readString(dis);

					this.manager_id = readString(dis);

					this.department_id = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// Integer

				writeInteger(this.employee_id, dos);

				// String

				writeString(this.first_name, dos);

				// String

				writeString(this.last_name, dos);

				// String

				writeString(this.email, dos);

				// String

				writeString(this.phone_number, dos);

				// java.util.Date

				writeDate(this.hire_date, dos);

				// String

				writeString(this.job_id, dos);

				// Integer

				writeInteger(this.salary, dos);

				// String

				writeString(this.commission_pct, dos);

				// String

				writeString(this.manager_id, dos);

				// String

				writeString(this.department_id, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public void writeData(org.jboss.marshalling.Marshaller dos) {
			try {

				// Integer

				writeInteger(this.employee_id, dos);

				// String

				writeString(this.first_name, dos);

				// String

				writeString(this.last_name, dos);

				// String

				writeString(this.email, dos);

				// String

				writeString(this.phone_number, dos);

				// java.util.Date

				writeDate(this.hire_date, dos);

				// String

				writeString(this.job_id, dos);

				// Integer

				writeInteger(this.salary, dos);

				// String

				writeString(this.commission_pct, dos);

				// String

				writeString(this.manager_id, dos);

				// String

				writeString(this.department_id, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("employee_id=" + String.valueOf(employee_id));
			sb.append(",first_name=" + first_name);
			sb.append(",last_name=" + last_name);
			sb.append(",email=" + email);
			sb.append(",phone_number=" + phone_number);
			sb.append(",hire_date=" + String.valueOf(hire_date));
			sb.append(",job_id=" + job_id);
			sb.append(",salary=" + String.valueOf(salary));
			sb.append(",commission_pct=" + commission_pct);
			sb.append(",manager_id=" + manager_id);
			sb.append(",department_id=" + department_id);
			sb.append("]");

			return sb.toString();
		}

		public String toLogString() {
			StringBuilder sb = new StringBuilder();

			if (employee_id == null) {
				sb.append("<null>");
			} else {
				sb.append(employee_id);
			}

			sb.append("|");

			if (first_name == null) {
				sb.append("<null>");
			} else {
				sb.append(first_name);
			}

			sb.append("|");

			if (last_name == null) {
				sb.append("<null>");
			} else {
				sb.append(last_name);
			}

			sb.append("|");

			if (email == null) {
				sb.append("<null>");
			} else {
				sb.append(email);
			}

			sb.append("|");

			if (phone_number == null) {
				sb.append("<null>");
			} else {
				sb.append(phone_number);
			}

			sb.append("|");

			if (hire_date == null) {
				sb.append("<null>");
			} else {
				sb.append(hire_date);
			}

			sb.append("|");

			if (job_id == null) {
				sb.append("<null>");
			} else {
				sb.append(job_id);
			}

			sb.append("|");

			if (salary == null) {
				sb.append("<null>");
			} else {
				sb.append(salary);
			}

			sb.append("|");

			if (commission_pct == null) {
				sb.append("<null>");
			} else {
				sb.append(commission_pct);
			}

			sb.append("|");

			if (manager_id == null) {
				sb.append("<null>");
			} else {
				sb.append(manager_id);
			}

			sb.append("|");

			if (department_id == null) {
				sb.append("<null>");
			} else {
				sb.append(department_id);
			}

			sb.append("|");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(row1Struct other) {

			int returnValue = -1;

			return returnValue;
		}

		private int checkNullsAndCompare(Object object1, Object object2) {
			int returnValue = 0;
			if (object1 instanceof Comparable && object2 instanceof Comparable) {
				returnValue = ((Comparable) object1).compareTo(object2);
			} else if (object1 != null && object2 != null) {
				returnValue = compareStrings(object1.toString(), object2.toString());
			} else if (object1 == null && object2 != null) {
				returnValue = 1;
			} else if (object1 != null && object2 == null) {
				returnValue = -1;
			} else {
				returnValue = 0;
			}

			return returnValue;
		}

		private int compareStrings(String string1, String string2) {
			return string1.compareTo(string2);
		}

	}

	public void tFileInputJSON_1Process(final java.util.Map<String, Object> globalMap) throws TalendException {
		globalMap.put("tFileInputJSON_1_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		mdc("tFileInputJSON_1", "4E26He_");

		String iterateId = "";

		String currentComponent = "";
		s("none");
		String cLabel = null;
		java.util.Map<String, Object> resourceMap = new java.util.HashMap<String, Object>();

		try {
			// TDI-39566 avoid throwing an useless Exception
			boolean resumeIt = true;
			if (globalResumeTicket == false && resumeEntryMethodName != null) {
				String currentMethodName = new java.lang.Exception().getStackTrace()[0].getMethodName();
				resumeIt = resumeEntryMethodName.equals(currentMethodName);
			}
			if (resumeIt || globalResumeTicket) { // start the resume
				globalResumeTicket = true;

				row1Struct row1 = new row1Struct();
				row2Struct row2 = new row2Struct();

				/**
				 * [tLogRow_1 begin ] start
				 */

				sh("tLogRow_1");

				s(currentComponent = "tLogRow_1");

				runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId, 0, 0, "row2");

				int tos_count_tLogRow_1 = 0;

				if (log.isDebugEnabled())
					log.debug("tLogRow_1 - " + ("Start to work."));
				if (log.isDebugEnabled()) {
					class BytesLimit65535_tLogRow_1 {
						public void limitLog4jByte() throws Exception {
							StringBuilder log4jParamters_tLogRow_1 = new StringBuilder();
							log4jParamters_tLogRow_1.append("Parameters:");
							log4jParamters_tLogRow_1.append("BASIC_MODE" + " = " + "false");
							log4jParamters_tLogRow_1.append(" | ");
							log4jParamters_tLogRow_1.append("TABLE_PRINT" + " = " + "true");
							log4jParamters_tLogRow_1.append(" | ");
							log4jParamters_tLogRow_1.append("VERTICAL" + " = " + "false");
							log4jParamters_tLogRow_1.append(" | ");
							log4jParamters_tLogRow_1.append("PRINT_CONTENT_WITH_LOG4J" + " = " + "true");
							log4jParamters_tLogRow_1.append(" | ");
							if (log.isDebugEnabled())
								log.debug("tLogRow_1 - " + (log4jParamters_tLogRow_1));
						}
					}
					new BytesLimit65535_tLogRow_1().limitLog4jByte();
				}
				if (enableLogStash) {
					talendJobLog.addCM("tLogRow_1", "tLogRow_1", "tLogRow");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				///////////////////////

				class Util_tLogRow_1 {

					String[] des_top = { ".", ".", "-", "+" };

					String[] des_head = { "|=", "=|", "-", "+" };

					String[] des_bottom = { "'", "'", "-", "+" };

					String name = "";

					java.util.List<String[]> list = new java.util.ArrayList<String[]>();

					int[] colLengths = new int[11];

					public void addRow(String[] row) {

						for (int i = 0; i < 11; i++) {
							if (row[i] != null) {
								colLengths[i] = Math.max(colLengths[i], row[i].length());
							}
						}
						list.add(row);
					}

					public void setTableName(String name) {

						this.name = name;
					}

					public StringBuilder format() {

						StringBuilder sb = new StringBuilder();

						sb.append(print(des_top));

						int totals = 0;
						for (int i = 0; i < colLengths.length; i++) {
							totals = totals + colLengths[i];
						}

						// name
						sb.append("|");
						int k = 0;
						for (k = 0; k < (totals + 10 - name.length()) / 2; k++) {
							sb.append(' ');
						}
						sb.append(name);
						for (int i = 0; i < totals + 10 - name.length() - k; i++) {
							sb.append(' ');
						}
						sb.append("|\n");

						// head and rows
						sb.append(print(des_head));
						for (int i = 0; i < list.size(); i++) {

							String[] row = list.get(i);

							java.util.Formatter formatter = new java.util.Formatter(new StringBuilder());

							StringBuilder sbformat = new StringBuilder();
							sbformat.append("|%1$-");
							sbformat.append(colLengths[0]);
							sbformat.append("s");

							sbformat.append("|%2$-");
							sbformat.append(colLengths[1]);
							sbformat.append("s");

							sbformat.append("|%3$-");
							sbformat.append(colLengths[2]);
							sbformat.append("s");

							sbformat.append("|%4$-");
							sbformat.append(colLengths[3]);
							sbformat.append("s");

							sbformat.append("|%5$-");
							sbformat.append(colLengths[4]);
							sbformat.append("s");

							sbformat.append("|%6$-");
							sbformat.append(colLengths[5]);
							sbformat.append("s");

							sbformat.append("|%7$-");
							sbformat.append(colLengths[6]);
							sbformat.append("s");

							sbformat.append("|%8$-");
							sbformat.append(colLengths[7]);
							sbformat.append("s");

							sbformat.append("|%9$-");
							sbformat.append(colLengths[8]);
							sbformat.append("s");

							sbformat.append("|%10$-");
							sbformat.append(colLengths[9]);
							sbformat.append("s");

							sbformat.append("|%11$-");
							sbformat.append(colLengths[10]);
							sbformat.append("s");

							sbformat.append("|\n");

							formatter.format(sbformat.toString(), (Object[]) row);

							sb.append(formatter.toString());
							if (i == 0)
								sb.append(print(des_head)); // print the head
						}

						// end
						sb.append(print(des_bottom));
						return sb;
					}

					private StringBuilder print(String[] fillChars) {
						StringBuilder sb = new StringBuilder();
						// first column
						sb.append(fillChars[0]);
						for (int i = 0; i < colLengths[0] - fillChars[0].length() + 1; i++) {
							sb.append(fillChars[2]);
						}
						sb.append(fillChars[3]);

						for (int i = 0; i < colLengths[1] - fillChars[3].length() + 1; i++) {
							sb.append(fillChars[2]);
						}
						sb.append(fillChars[3]);
						for (int i = 0; i < colLengths[2] - fillChars[3].length() + 1; i++) {
							sb.append(fillChars[2]);
						}
						sb.append(fillChars[3]);
						for (int i = 0; i < colLengths[3] - fillChars[3].length() + 1; i++) {
							sb.append(fillChars[2]);
						}
						sb.append(fillChars[3]);
						for (int i = 0; i < colLengths[4] - fillChars[3].length() + 1; i++) {
							sb.append(fillChars[2]);
						}
						sb.append(fillChars[3]);
						for (int i = 0; i < colLengths[5] - fillChars[3].length() + 1; i++) {
							sb.append(fillChars[2]);
						}
						sb.append(fillChars[3]);
						for (int i = 0; i < colLengths[6] - fillChars[3].length() + 1; i++) {
							sb.append(fillChars[2]);
						}
						sb.append(fillChars[3]);
						for (int i = 0; i < colLengths[7] - fillChars[3].length() + 1; i++) {
							sb.append(fillChars[2]);
						}
						sb.append(fillChars[3]);
						for (int i = 0; i < colLengths[8] - fillChars[3].length() + 1; i++) {
							sb.append(fillChars[2]);
						}
						sb.append(fillChars[3]);
						for (int i = 0; i < colLengths[9] - fillChars[3].length() + 1; i++) {
							sb.append(fillChars[2]);
						}
						sb.append(fillChars[3]);

						// last column
						for (int i = 0; i < colLengths[10] - fillChars[1].length() + 1; i++) {
							sb.append(fillChars[2]);
						}
						sb.append(fillChars[1]);
						sb.append("\n");
						return sb;
					}

					public boolean isTableEmpty() {
						if (list.size() > 1)
							return false;
						return true;
					}
				}
				Util_tLogRow_1 util_tLogRow_1 = new Util_tLogRow_1();
				util_tLogRow_1.setTableName("tLogRow_1");
				util_tLogRow_1.addRow(new String[] { "employee_id", "first_name", "last_name", "email", "phone_number",
						"hire_date", "job_id", "salary", "commission_pct", "manager_id", "department_id", });
				StringBuilder strBuffer_tLogRow_1 = null;
				int nb_line_tLogRow_1 = 0;
///////////////////////    			

				/**
				 * [tLogRow_1 begin ] stop
				 */

				/**
				 * [tConvertType_1 begin ] start
				 */

				sh("tConvertType_1");

				s(currentComponent = "tConvertType_1");

				runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId, 0, 0, "row1");

				int tos_count_tConvertType_1 = 0;

				if (enableLogStash) {
					talendJobLog.addCM("tConvertType_1", "tConvertType_1", "tConvertType");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				int nb_line_tConvertType_1 = 0;

				/**
				 * [tConvertType_1 begin ] stop
				 */

				/**
				 * [tFileInputJSON_1 begin ] start
				 */

				sh("tFileInputJSON_1");

				s(currentComponent = "tFileInputJSON_1");

				int tos_count_tFileInputJSON_1 = 0;

				if (log.isDebugEnabled())
					log.debug("tFileInputJSON_1 - " + ("Start to work."));
				if (log.isDebugEnabled()) {
					class BytesLimit65535_tFileInputJSON_1 {
						public void limitLog4jByte() throws Exception {
							StringBuilder log4jParamters_tFileInputJSON_1 = new StringBuilder();
							log4jParamters_tFileInputJSON_1.append("Parameters:");
							log4jParamters_tFileInputJSON_1.append("READ_BY" + " = " + "JSONPATH");
							log4jParamters_tFileInputJSON_1.append(" | ");
							log4jParamters_tFileInputJSON_1.append("JSON_PATH_VERSION" + " = " + "2_1_0");
							log4jParamters_tFileInputJSON_1.append(" | ");
							log4jParamters_tFileInputJSON_1.append("USEURL" + " = " + "false");
							log4jParamters_tFileInputJSON_1.append(" | ");
							log4jParamters_tFileInputJSON_1.append("FILENAME" + " = "
									+ "\"C:/Users/ramayanam/Desktop/Talend/New folder/export.json\"");
							log4jParamters_tFileInputJSON_1.append(" | ");
							log4jParamters_tFileInputJSON_1.append("JSON_LOOP_QUERY" + " = " + "\"$[*]\"");
							log4jParamters_tFileInputJSON_1.append(" | ");
							log4jParamters_tFileInputJSON_1.append("MAPPING_JSONPATH" + " = " + "[{QUERY="
									+ ("\"employee_id\"") + ", SCHEMA_COLUMN=" + ("employee_id") + "}, {QUERY="
									+ ("\"first_name\"") + ", SCHEMA_COLUMN=" + ("first_name") + "}, {QUERY="
									+ ("\"last_name\"") + ", SCHEMA_COLUMN=" + ("last_name") + "}, {QUERY="
									+ ("\"email\"") + ", SCHEMA_COLUMN=" + ("email") + "}, {QUERY="
									+ ("\"phone_number\"") + ", SCHEMA_COLUMN=" + ("phone_number") + "}, {QUERY="
									+ ("\"hire_date\"") + ", SCHEMA_COLUMN=" + ("hire_date") + "}, {QUERY="
									+ ("\"job_id\"") + ", SCHEMA_COLUMN=" + ("job_id") + "}, {QUERY=" + ("\"salary\"")
									+ ", SCHEMA_COLUMN=" + ("salary") + "}, {QUERY=" + ("\"commission_pct\"")
									+ ", SCHEMA_COLUMN=" + ("commission_pct") + "}, {QUERY=" + ("\"manager_id\"")
									+ ", SCHEMA_COLUMN=" + ("manager_id") + "}, {QUERY=" + ("\"department_id\"")
									+ ", SCHEMA_COLUMN=" + ("department_id") + "}]");
							log4jParamters_tFileInputJSON_1.append(" | ");
							log4jParamters_tFileInputJSON_1.append("DIE_ON_ERROR" + " = " + "false");
							log4jParamters_tFileInputJSON_1.append(" | ");
							log4jParamters_tFileInputJSON_1.append("ADVANCED_SEPARATOR" + " = " + "false");
							log4jParamters_tFileInputJSON_1.append(" | ");
							log4jParamters_tFileInputJSON_1.append("USE_LOOP_AS_ROOT" + " = " + "false");
							log4jParamters_tFileInputJSON_1.append(" | ");
							log4jParamters_tFileInputJSON_1.append("ENCODING" + " = " + "\"UTF-8\"");
							log4jParamters_tFileInputJSON_1.append(" | ");
							if (log.isDebugEnabled())
								log.debug("tFileInputJSON_1 - " + (log4jParamters_tFileInputJSON_1));
						}
					}
					new BytesLimit65535_tFileInputJSON_1().limitLog4jByte();
				}
				if (enableLogStash) {
					talendJobLog.addCM("tFileInputJSON_1", "tFileInputJSON_1", "tFileInputJSON");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				class JsonPathCache_tFileInputJSON_1 {
					final java.util.Map<String, com.jayway.jsonpath.JsonPath> jsonPathString2compiledJsonPath = new java.util.HashMap<String, com.jayway.jsonpath.JsonPath>();

					public com.jayway.jsonpath.JsonPath getCompiledJsonPath(String jsonPath) {
						if (jsonPathString2compiledJsonPath.containsKey(jsonPath)) {
							return jsonPathString2compiledJsonPath.get(jsonPath);
						} else {
							com.jayway.jsonpath.JsonPath compiledLoopPath = com.jayway.jsonpath.JsonPath
									.compile(jsonPath);
							jsonPathString2compiledJsonPath.put(jsonPath, compiledLoopPath);
							return compiledLoopPath;
						}
					}
				}

				int nb_line_tFileInputJSON_1 = 0;

				JsonPathCache_tFileInputJSON_1 jsonPathCache_tFileInputJSON_1 = new JsonPathCache_tFileInputJSON_1();

				String loopPath_tFileInputJSON_1 = "$[*]";
				java.util.List<Object> resultset_tFileInputJSON_1 = new java.util.ArrayList<Object>();

				java.io.InputStream is_tFileInputJSON_1 = null;
				com.jayway.jsonpath.ParseContext parseContext_tFileInputJSON_1 = com.jayway.jsonpath.JsonPath
						.using(com.jayway.jsonpath.Configuration.defaultConfiguration());
				Object filenameOrStream_tFileInputJSON_1 = null;
				try {
					filenameOrStream_tFileInputJSON_1 = "C:/Users/ramayanam/Desktop/Talend/New folder/export.json";
				} catch (java.lang.Exception e_tFileInputJSON_1) {
					globalMap.put("tFileInputJSON_1_ERROR_MESSAGE", e_tFileInputJSON_1.getMessage());

					log.error("tFileInputJSON_1 - " + e_tFileInputJSON_1.getMessage());

					globalMap.put("tFileInputJSON_1_ERROR_MESSAGE", e_tFileInputJSON_1.getMessage());
					System.err.println(e_tFileInputJSON_1.getMessage());
				}

				com.jayway.jsonpath.ReadContext document_tFileInputJSON_1 = null;
				try {
					if (filenameOrStream_tFileInputJSON_1 instanceof java.io.InputStream) {
						is_tFileInputJSON_1 = (java.io.InputStream) filenameOrStream_tFileInputJSON_1;
					} else {

						is_tFileInputJSON_1 = new java.io.FileInputStream((String) filenameOrStream_tFileInputJSON_1);

					}

					document_tFileInputJSON_1 = parseContext_tFileInputJSON_1.parse(is_tFileInputJSON_1, "UTF-8");
					com.jayway.jsonpath.JsonPath compiledLoopPath_tFileInputJSON_1 = jsonPathCache_tFileInputJSON_1
							.getCompiledJsonPath(loopPath_tFileInputJSON_1);
					Object result_tFileInputJSON_1 = document_tFileInputJSON_1.read(compiledLoopPath_tFileInputJSON_1,
							net.minidev.json.JSONObject.class);
					if (result_tFileInputJSON_1 instanceof net.minidev.json.JSONArray) {
						resultset_tFileInputJSON_1 = (net.minidev.json.JSONArray) result_tFileInputJSON_1;
					} else {
						resultset_tFileInputJSON_1.add(result_tFileInputJSON_1);
					}
				} catch (java.lang.Exception e_tFileInputJSON_1) {
					globalMap.put("tFileInputJSON_1_ERROR_MESSAGE", e_tFileInputJSON_1.getMessage());
					log.error("tFileInputJSON_1 - " + e_tFileInputJSON_1.getMessage());

					globalMap.put("tFileInputJSON_1_ERROR_MESSAGE", e_tFileInputJSON_1.getMessage());
					System.err.println(e_tFileInputJSON_1.getMessage());
				} finally {
					if (is_tFileInputJSON_1 != null) {
						is_tFileInputJSON_1.close();
					}
				}

				String jsonPath_tFileInputJSON_1 = null;
				com.jayway.jsonpath.JsonPath compiledJsonPath_tFileInputJSON_1 = null;

				Object value_tFileInputJSON_1 = null;
				log.info("tFileInputJSON_1 - Retrieving records from data.");
				Object root_tFileInputJSON_1 = null;
				for (Object row_tFileInputJSON_1 : resultset_tFileInputJSON_1) {
					nb_line_tFileInputJSON_1++;
					log.debug("tFileInputJSON_1 - Retrieving the record " + (nb_line_tFileInputJSON_1) + ".");

					row1 = null;
					boolean whetherReject_tFileInputJSON_1 = false;
					row1 = new row1Struct();

					try {
						jsonPath_tFileInputJSON_1 = "employee_id";
						compiledJsonPath_tFileInputJSON_1 = jsonPathCache_tFileInputJSON_1
								.getCompiledJsonPath(jsonPath_tFileInputJSON_1);

						try {

							if (jsonPath_tFileInputJSON_1.startsWith("$")) {
								if (root_tFileInputJSON_1 == null) {
									root_tFileInputJSON_1 = document_tFileInputJSON_1
											.read(jsonPathCache_tFileInputJSON_1.getCompiledJsonPath("$"));
								}
								value_tFileInputJSON_1 = compiledJsonPath_tFileInputJSON_1.read(root_tFileInputJSON_1);
							} else {
								value_tFileInputJSON_1 = compiledJsonPath_tFileInputJSON_1.read(row_tFileInputJSON_1);
							}
							if (value_tFileInputJSON_1 != null && !value_tFileInputJSON_1.toString().isEmpty()) {
								row1.employee_id = ParserUtils.parseTo_Integer(value_tFileInputJSON_1.toString());
							} else {
								row1.employee_id =

										null;
							}
						} catch (com.jayway.jsonpath.PathNotFoundException e_tFileInputJSON_1) {
							globalMap.put("tFileInputJSON_1_ERROR_MESSAGE", e_tFileInputJSON_1.getMessage());
							row1.employee_id =

									null;
						}
						jsonPath_tFileInputJSON_1 = "first_name";
						compiledJsonPath_tFileInputJSON_1 = jsonPathCache_tFileInputJSON_1
								.getCompiledJsonPath(jsonPath_tFileInputJSON_1);

						try {

							if (jsonPath_tFileInputJSON_1.startsWith("$")) {
								if (root_tFileInputJSON_1 == null) {
									root_tFileInputJSON_1 = document_tFileInputJSON_1
											.read(jsonPathCache_tFileInputJSON_1.getCompiledJsonPath("$"));
								}
								value_tFileInputJSON_1 = compiledJsonPath_tFileInputJSON_1.read(root_tFileInputJSON_1);
							} else {
								value_tFileInputJSON_1 = compiledJsonPath_tFileInputJSON_1.read(row_tFileInputJSON_1);
							}
							row1.first_name = value_tFileInputJSON_1 == null ?

									null : value_tFileInputJSON_1.toString();
						} catch (com.jayway.jsonpath.PathNotFoundException e_tFileInputJSON_1) {
							globalMap.put("tFileInputJSON_1_ERROR_MESSAGE", e_tFileInputJSON_1.getMessage());
							row1.first_name =

									null;
						}
						jsonPath_tFileInputJSON_1 = "last_name";
						compiledJsonPath_tFileInputJSON_1 = jsonPathCache_tFileInputJSON_1
								.getCompiledJsonPath(jsonPath_tFileInputJSON_1);

						try {

							if (jsonPath_tFileInputJSON_1.startsWith("$")) {
								if (root_tFileInputJSON_1 == null) {
									root_tFileInputJSON_1 = document_tFileInputJSON_1
											.read(jsonPathCache_tFileInputJSON_1.getCompiledJsonPath("$"));
								}
								value_tFileInputJSON_1 = compiledJsonPath_tFileInputJSON_1.read(root_tFileInputJSON_1);
							} else {
								value_tFileInputJSON_1 = compiledJsonPath_tFileInputJSON_1.read(row_tFileInputJSON_1);
							}
							row1.last_name = value_tFileInputJSON_1 == null ?

									null : value_tFileInputJSON_1.toString();
						} catch (com.jayway.jsonpath.PathNotFoundException e_tFileInputJSON_1) {
							globalMap.put("tFileInputJSON_1_ERROR_MESSAGE", e_tFileInputJSON_1.getMessage());
							row1.last_name =

									null;
						}
						jsonPath_tFileInputJSON_1 = "email";
						compiledJsonPath_tFileInputJSON_1 = jsonPathCache_tFileInputJSON_1
								.getCompiledJsonPath(jsonPath_tFileInputJSON_1);

						try {

							if (jsonPath_tFileInputJSON_1.startsWith("$")) {
								if (root_tFileInputJSON_1 == null) {
									root_tFileInputJSON_1 = document_tFileInputJSON_1
											.read(jsonPathCache_tFileInputJSON_1.getCompiledJsonPath("$"));
								}
								value_tFileInputJSON_1 = compiledJsonPath_tFileInputJSON_1.read(root_tFileInputJSON_1);
							} else {
								value_tFileInputJSON_1 = compiledJsonPath_tFileInputJSON_1.read(row_tFileInputJSON_1);
							}
							row1.email = value_tFileInputJSON_1 == null ?

									null : value_tFileInputJSON_1.toString();
						} catch (com.jayway.jsonpath.PathNotFoundException e_tFileInputJSON_1) {
							globalMap.put("tFileInputJSON_1_ERROR_MESSAGE", e_tFileInputJSON_1.getMessage());
							row1.email =

									null;
						}
						jsonPath_tFileInputJSON_1 = "phone_number";
						compiledJsonPath_tFileInputJSON_1 = jsonPathCache_tFileInputJSON_1
								.getCompiledJsonPath(jsonPath_tFileInputJSON_1);

						try {

							if (jsonPath_tFileInputJSON_1.startsWith("$")) {
								if (root_tFileInputJSON_1 == null) {
									root_tFileInputJSON_1 = document_tFileInputJSON_1
											.read(jsonPathCache_tFileInputJSON_1.getCompiledJsonPath("$"));
								}
								value_tFileInputJSON_1 = compiledJsonPath_tFileInputJSON_1.read(root_tFileInputJSON_1);
							} else {
								value_tFileInputJSON_1 = compiledJsonPath_tFileInputJSON_1.read(row_tFileInputJSON_1);
							}
							row1.phone_number = value_tFileInputJSON_1 == null ?

									null : value_tFileInputJSON_1.toString();
						} catch (com.jayway.jsonpath.PathNotFoundException e_tFileInputJSON_1) {
							globalMap.put("tFileInputJSON_1_ERROR_MESSAGE", e_tFileInputJSON_1.getMessage());
							row1.phone_number =

									null;
						}
						jsonPath_tFileInputJSON_1 = "hire_date";
						compiledJsonPath_tFileInputJSON_1 = jsonPathCache_tFileInputJSON_1
								.getCompiledJsonPath(jsonPath_tFileInputJSON_1);

						try {

							if (jsonPath_tFileInputJSON_1.startsWith("$")) {
								if (root_tFileInputJSON_1 == null) {
									root_tFileInputJSON_1 = document_tFileInputJSON_1
											.read(jsonPathCache_tFileInputJSON_1.getCompiledJsonPath("$"));
								}
								value_tFileInputJSON_1 = compiledJsonPath_tFileInputJSON_1.read(root_tFileInputJSON_1);
							} else {
								value_tFileInputJSON_1 = compiledJsonPath_tFileInputJSON_1.read(row_tFileInputJSON_1);
							}
							if (value_tFileInputJSON_1 != null && !value_tFileInputJSON_1.toString().isEmpty()) {
								row1.hire_date = ParserUtils.parseTo_Date(value_tFileInputJSON_1.toString(),
										"dd-MM-yyyy");
							} else {
								row1.hire_date =

										null;
							}
						} catch (com.jayway.jsonpath.PathNotFoundException e_tFileInputJSON_1) {
							globalMap.put("tFileInputJSON_1_ERROR_MESSAGE", e_tFileInputJSON_1.getMessage());
							row1.hire_date =

									null;
						}
						jsonPath_tFileInputJSON_1 = "job_id";
						compiledJsonPath_tFileInputJSON_1 = jsonPathCache_tFileInputJSON_1
								.getCompiledJsonPath(jsonPath_tFileInputJSON_1);

						try {

							if (jsonPath_tFileInputJSON_1.startsWith("$")) {
								if (root_tFileInputJSON_1 == null) {
									root_tFileInputJSON_1 = document_tFileInputJSON_1
											.read(jsonPathCache_tFileInputJSON_1.getCompiledJsonPath("$"));
								}
								value_tFileInputJSON_1 = compiledJsonPath_tFileInputJSON_1.read(root_tFileInputJSON_1);
							} else {
								value_tFileInputJSON_1 = compiledJsonPath_tFileInputJSON_1.read(row_tFileInputJSON_1);
							}
							row1.job_id = value_tFileInputJSON_1 == null ?

									null : value_tFileInputJSON_1.toString();
						} catch (com.jayway.jsonpath.PathNotFoundException e_tFileInputJSON_1) {
							globalMap.put("tFileInputJSON_1_ERROR_MESSAGE", e_tFileInputJSON_1.getMessage());
							row1.job_id =

									null;
						}
						jsonPath_tFileInputJSON_1 = "salary";
						compiledJsonPath_tFileInputJSON_1 = jsonPathCache_tFileInputJSON_1
								.getCompiledJsonPath(jsonPath_tFileInputJSON_1);

						try {

							if (jsonPath_tFileInputJSON_1.startsWith("$")) {
								if (root_tFileInputJSON_1 == null) {
									root_tFileInputJSON_1 = document_tFileInputJSON_1
											.read(jsonPathCache_tFileInputJSON_1.getCompiledJsonPath("$"));
								}
								value_tFileInputJSON_1 = compiledJsonPath_tFileInputJSON_1.read(root_tFileInputJSON_1);
							} else {
								value_tFileInputJSON_1 = compiledJsonPath_tFileInputJSON_1.read(row_tFileInputJSON_1);
							}
							if (value_tFileInputJSON_1 != null && !value_tFileInputJSON_1.toString().isEmpty()) {
								row1.salary = ParserUtils.parseTo_Integer(value_tFileInputJSON_1.toString());
							} else {
								row1.salary =

										null;
							}
						} catch (com.jayway.jsonpath.PathNotFoundException e_tFileInputJSON_1) {
							globalMap.put("tFileInputJSON_1_ERROR_MESSAGE", e_tFileInputJSON_1.getMessage());
							row1.salary =

									null;
						}
						jsonPath_tFileInputJSON_1 = "commission_pct";
						compiledJsonPath_tFileInputJSON_1 = jsonPathCache_tFileInputJSON_1
								.getCompiledJsonPath(jsonPath_tFileInputJSON_1);

						try {

							if (jsonPath_tFileInputJSON_1.startsWith("$")) {
								if (root_tFileInputJSON_1 == null) {
									root_tFileInputJSON_1 = document_tFileInputJSON_1
											.read(jsonPathCache_tFileInputJSON_1.getCompiledJsonPath("$"));
								}
								value_tFileInputJSON_1 = compiledJsonPath_tFileInputJSON_1.read(root_tFileInputJSON_1);
							} else {
								value_tFileInputJSON_1 = compiledJsonPath_tFileInputJSON_1.read(row_tFileInputJSON_1);
							}
							row1.commission_pct = value_tFileInputJSON_1 == null ?

									null : value_tFileInputJSON_1.toString();
						} catch (com.jayway.jsonpath.PathNotFoundException e_tFileInputJSON_1) {
							globalMap.put("tFileInputJSON_1_ERROR_MESSAGE", e_tFileInputJSON_1.getMessage());
							row1.commission_pct =

									null;
						}
						jsonPath_tFileInputJSON_1 = "manager_id";
						compiledJsonPath_tFileInputJSON_1 = jsonPathCache_tFileInputJSON_1
								.getCompiledJsonPath(jsonPath_tFileInputJSON_1);

						try {

							if (jsonPath_tFileInputJSON_1.startsWith("$")) {
								if (root_tFileInputJSON_1 == null) {
									root_tFileInputJSON_1 = document_tFileInputJSON_1
											.read(jsonPathCache_tFileInputJSON_1.getCompiledJsonPath("$"));
								}
								value_tFileInputJSON_1 = compiledJsonPath_tFileInputJSON_1.read(root_tFileInputJSON_1);
							} else {
								value_tFileInputJSON_1 = compiledJsonPath_tFileInputJSON_1.read(row_tFileInputJSON_1);
							}
							row1.manager_id = value_tFileInputJSON_1 == null ?

									null : value_tFileInputJSON_1.toString();
						} catch (com.jayway.jsonpath.PathNotFoundException e_tFileInputJSON_1) {
							globalMap.put("tFileInputJSON_1_ERROR_MESSAGE", e_tFileInputJSON_1.getMessage());
							row1.manager_id =

									null;
						}
						jsonPath_tFileInputJSON_1 = "department_id";
						compiledJsonPath_tFileInputJSON_1 = jsonPathCache_tFileInputJSON_1
								.getCompiledJsonPath(jsonPath_tFileInputJSON_1);

						try {

							if (jsonPath_tFileInputJSON_1.startsWith("$")) {
								if (root_tFileInputJSON_1 == null) {
									root_tFileInputJSON_1 = document_tFileInputJSON_1
											.read(jsonPathCache_tFileInputJSON_1.getCompiledJsonPath("$"));
								}
								value_tFileInputJSON_1 = compiledJsonPath_tFileInputJSON_1.read(root_tFileInputJSON_1);
							} else {
								value_tFileInputJSON_1 = compiledJsonPath_tFileInputJSON_1.read(row_tFileInputJSON_1);
							}
							row1.department_id = value_tFileInputJSON_1 == null ?

									null : value_tFileInputJSON_1.toString();
						} catch (com.jayway.jsonpath.PathNotFoundException e_tFileInputJSON_1) {
							globalMap.put("tFileInputJSON_1_ERROR_MESSAGE", e_tFileInputJSON_1.getMessage());
							row1.department_id =

									null;
						}
					} catch (java.lang.Exception e_tFileInputJSON_1) {
						globalMap.put("tFileInputJSON_1_ERROR_MESSAGE", e_tFileInputJSON_1.getMessage());
						whetherReject_tFileInputJSON_1 = true;
						log.error("tFileInputJSON_1 - " + e_tFileInputJSON_1.getMessage());

						System.err.println(e_tFileInputJSON_1.getMessage());
						row1 = null;
						globalMap.put("tFileInputJSON_1_ERROR_MESSAGE", e_tFileInputJSON_1.getMessage());
					}
//}

					/**
					 * [tFileInputJSON_1 begin ] stop
					 */

					/**
					 * [tFileInputJSON_1 main ] start
					 */

					s(currentComponent = "tFileInputJSON_1");

					tos_count_tFileInputJSON_1++;

					/**
					 * [tFileInputJSON_1 main ] stop
					 */

					/**
					 * [tFileInputJSON_1 process_data_begin ] start
					 */

					s(currentComponent = "tFileInputJSON_1");

					/**
					 * [tFileInputJSON_1 process_data_begin ] stop
					 */

// Start of branch "row1"
					if (row1 != null) {

						/**
						 * [tConvertType_1 main ] start
						 */

						s(currentComponent = "tConvertType_1");

						if (runStat.update(execStat, enableLogStash, iterateId, 1, 1

								, "row1", "tFileInputJSON_1", "tFileInputJSON_1", "tFileInputJSON", "tConvertType_1",
								"tConvertType_1", "tConvertType"

						)) {
							talendJobLogProcess(globalMap);
						}

						if (log.isTraceEnabled()) {
							log.trace("row1 - " + (row1 == null ? "" : row1.toLogString()));
						}

						row2 = new row2Struct();
						boolean bHasError_tConvertType_1 = false;
						if (bHasError_tConvertType_1) {
							row2 = null;
						}

						nb_line_tConvertType_1++;

						tos_count_tConvertType_1++;

						/**
						 * [tConvertType_1 main ] stop
						 */

						/**
						 * [tConvertType_1 process_data_begin ] start
						 */

						s(currentComponent = "tConvertType_1");

						/**
						 * [tConvertType_1 process_data_begin ] stop
						 */

// Start of branch "row2"
						if (row2 != null) {

							/**
							 * [tLogRow_1 main ] start
							 */

							s(currentComponent = "tLogRow_1");

							if (runStat.update(execStat, enableLogStash, iterateId, 1, 1

									, "row2", "tConvertType_1", "tConvertType_1", "tConvertType", "tLogRow_1",
									"tLogRow_1", "tLogRow"

							)) {
								talendJobLogProcess(globalMap);
							}

							if (log.isTraceEnabled()) {
								log.trace("row2 - " + (row2 == null ? "" : row2.toLogString()));
							}

///////////////////////		

							String[] row_tLogRow_1 = new String[11];

							if (row2.employee_id != null) { //
								row_tLogRow_1[0] = String.valueOf(row2.employee_id);

							} //

							if (row2.first_name != null) { //
								row_tLogRow_1[1] = String.valueOf(row2.first_name);

							} //

							if (row2.last_name != null) { //
								row_tLogRow_1[2] = String.valueOf(row2.last_name);

							} //

							if (row2.email != null) { //
								row_tLogRow_1[3] = String.valueOf(row2.email);

							} //

							if (row2.phone_number != null) { //
								row_tLogRow_1[4] = String.valueOf(row2.phone_number);

							} //

							if (row2.hire_date != null) { //
								row_tLogRow_1[5] = String.valueOf(row2.hire_date);

							} //

							if (row2.job_id != null) { //
								row_tLogRow_1[6] = String.valueOf(row2.job_id);

							} //

							if (row2.salary != null) { //
								row_tLogRow_1[7] = String.valueOf(row2.salary);

							} //

							if (row2.commission_pct != null) { //
								row_tLogRow_1[8] = String.valueOf(row2.commission_pct);

							} //

							if (row2.manager_id != null) { //
								row_tLogRow_1[9] = String.valueOf(row2.manager_id);

							} //

							if (row2.department_id != null) { //
								row_tLogRow_1[10] = String.valueOf(row2.department_id);

							} //

							util_tLogRow_1.addRow(row_tLogRow_1);
							nb_line_tLogRow_1++;
							log.info("tLogRow_1 - Content of row " + nb_line_tLogRow_1 + ": "
									+ TalendString.unionString("|", row_tLogRow_1));
//////

//////                    

///////////////////////    			

							tos_count_tLogRow_1++;

							/**
							 * [tLogRow_1 main ] stop
							 */

							/**
							 * [tLogRow_1 process_data_begin ] start
							 */

							s(currentComponent = "tLogRow_1");

							/**
							 * [tLogRow_1 process_data_begin ] stop
							 */

							/**
							 * [tLogRow_1 process_data_end ] start
							 */

							s(currentComponent = "tLogRow_1");

							/**
							 * [tLogRow_1 process_data_end ] stop
							 */

						} // End of branch "row2"

						/**
						 * [tConvertType_1 process_data_end ] start
						 */

						s(currentComponent = "tConvertType_1");

						/**
						 * [tConvertType_1 process_data_end ] stop
						 */

					} // End of branch "row1"

					/**
					 * [tFileInputJSON_1 process_data_end ] start
					 */

					s(currentComponent = "tFileInputJSON_1");

					/**
					 * [tFileInputJSON_1 process_data_end ] stop
					 */

					/**
					 * [tFileInputJSON_1 end ] start
					 */

					s(currentComponent = "tFileInputJSON_1");

				}
				globalMap.put("tFileInputJSON_1_NB_LINE", nb_line_tFileInputJSON_1);
				log.debug("tFileInputJSON_1 - Retrieved records count: " + nb_line_tFileInputJSON_1 + " .");

				if (log.isDebugEnabled())
					log.debug("tFileInputJSON_1 - " + ("Done."));

				ok_Hash.put("tFileInputJSON_1", true);
				end_Hash.put("tFileInputJSON_1", System.currentTimeMillis());

				/**
				 * [tFileInputJSON_1 end ] stop
				 */

				/**
				 * [tConvertType_1 end ] start
				 */

				s(currentComponent = "tConvertType_1");

				globalMap.put("tConvertType_1_NB_LINE", nb_line_tConvertType_1);
				if (runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId, "row1", 2, 0,
						"tFileInputJSON_1", "tFileInputJSON_1", "tFileInputJSON", "tConvertType_1", "tConvertType_1",
						"tConvertType", "output")) {
					talendJobLogProcess(globalMap);
				}

				ok_Hash.put("tConvertType_1", true);
				end_Hash.put("tConvertType_1", System.currentTimeMillis());

				/**
				 * [tConvertType_1 end ] stop
				 */

				/**
				 * [tLogRow_1 end ] start
				 */

				s(currentComponent = "tLogRow_1");

//////

				java.io.PrintStream consoleOut_tLogRow_1 = null;
				if (globalMap.get("tLogRow_CONSOLE") != null) {
					consoleOut_tLogRow_1 = (java.io.PrintStream) globalMap.get("tLogRow_CONSOLE");
				} else {
					consoleOut_tLogRow_1 = new java.io.PrintStream(new java.io.BufferedOutputStream(System.out));
					globalMap.put("tLogRow_CONSOLE", consoleOut_tLogRow_1);
				}

				consoleOut_tLogRow_1.println(util_tLogRow_1.format().toString());
				consoleOut_tLogRow_1.flush();
//////
				globalMap.put("tLogRow_1_NB_LINE", nb_line_tLogRow_1);
				if (log.isInfoEnabled())
					log.info("tLogRow_1 - " + ("Printed row count: ") + (nb_line_tLogRow_1) + ("."));

///////////////////////    			

				if (runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId, "row2", 2, 0,
						"tConvertType_1", "tConvertType_1", "tConvertType", "tLogRow_1", "tLogRow_1", "tLogRow",
						"output")) {
					talendJobLogProcess(globalMap);
				}

				if (log.isDebugEnabled())
					log.debug("tLogRow_1 - " + ("Done."));

				ok_Hash.put("tLogRow_1", true);
				end_Hash.put("tLogRow_1", System.currentTimeMillis());

				/**
				 * [tLogRow_1 end ] stop
				 */

			} // end the resume

		} catch (java.lang.Exception e) {

			if (!(e instanceof TalendException)) {
				log.fatal(currentComponent + " " + e.getMessage(), e);
			}

			TalendException te = new TalendException(e, currentComponent, cLabel, globalMap);

			throw te;
		} catch (java.lang.Error error) {

			runStat.stopThreadStat();

			throw error;
		} finally {

			try {

				/**
				 * [tFileInputJSON_1 finally ] start
				 */

				s(currentComponent = "tFileInputJSON_1");

				/**
				 * [tFileInputJSON_1 finally ] stop
				 */

				/**
				 * [tConvertType_1 finally ] start
				 */

				s(currentComponent = "tConvertType_1");

				/**
				 * [tConvertType_1 finally ] stop
				 */

				/**
				 * [tLogRow_1 finally ] start
				 */

				s(currentComponent = "tLogRow_1");

				/**
				 * [tLogRow_1 finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("tFileInputJSON_1_SUBPROCESS_STATE", 1);
	}

	public void talendJobLogProcess(final java.util.Map<String, Object> globalMap) throws TalendException {
		globalMap.put("talendJobLog_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		String iterateId = "";

		String currentComponent = "";
		s("none");
		String cLabel = null;
		java.util.Map<String, Object> resourceMap = new java.util.HashMap<String, Object>();

		try {
			// TDI-39566 avoid throwing an useless Exception
			boolean resumeIt = true;
			if (globalResumeTicket == false && resumeEntryMethodName != null) {
				String currentMethodName = new java.lang.Exception().getStackTrace()[0].getMethodName();
				resumeIt = resumeEntryMethodName.equals(currentMethodName);
			}
			if (resumeIt || globalResumeTicket) { // start the resume
				globalResumeTicket = true;

				/**
				 * [talendJobLog begin ] start
				 */

				sh("talendJobLog");

				s(currentComponent = "talendJobLog");

				int tos_count_talendJobLog = 0;

				for (JobStructureCatcherUtils.JobStructureCatcherMessage jcm : talendJobLog.getMessages()) {
					org.talend.job.audit.JobContextBuilder builder_talendJobLog = org.talend.job.audit.JobContextBuilder
							.create().jobName(jcm.job_name).jobId(jcm.job_id).jobVersion(jcm.job_version)
							.custom("process_id", jcm.pid).custom("thread_id", jcm.tid).custom("pid", pid)
							.custom("father_pid", fatherPid).custom("root_pid", rootPid);
					org.talend.logging.audit.Context log_context_talendJobLog = null;

					if (jcm.log_type == JobStructureCatcherUtils.LogType.PERFORMANCE) {
						long timeMS = jcm.end_time - jcm.start_time;
						String duration = String.valueOf(timeMS);

						log_context_talendJobLog = builder_talendJobLog.sourceId(jcm.sourceId)
								.sourceLabel(jcm.sourceLabel).sourceConnectorType(jcm.sourceComponentName)
								.targetId(jcm.targetId).targetLabel(jcm.targetLabel)
								.targetConnectorType(jcm.targetComponentName).connectionName(jcm.current_connector)
								.rows(jcm.row_count).duration(duration).build();
						auditLogger_talendJobLog.flowExecution(log_context_talendJobLog);
					} else if (jcm.log_type == JobStructureCatcherUtils.LogType.JOBSTART) {
						log_context_talendJobLog = builder_talendJobLog.timestamp(jcm.moment).build();
						auditLogger_talendJobLog.jobstart(log_context_talendJobLog);
					} else if (jcm.log_type == JobStructureCatcherUtils.LogType.JOBEND) {
						long timeMS = jcm.end_time - jcm.start_time;
						String duration = String.valueOf(timeMS);

						log_context_talendJobLog = builder_talendJobLog.timestamp(jcm.moment).duration(duration)
								.status(jcm.status).build();
						auditLogger_talendJobLog.jobstop(log_context_talendJobLog);
					} else if (jcm.log_type == JobStructureCatcherUtils.LogType.RUNCOMPONENT) {
						log_context_talendJobLog = builder_talendJobLog.timestamp(jcm.moment)
								.connectorType(jcm.component_name).connectorId(jcm.component_id)
								.connectorLabel(jcm.component_label).build();
						auditLogger_talendJobLog.runcomponent(log_context_talendJobLog);
					} else if (jcm.log_type == JobStructureCatcherUtils.LogType.FLOWINPUT) {// log current component
																							// input line
						long timeMS = jcm.end_time - jcm.start_time;
						String duration = String.valueOf(timeMS);

						log_context_talendJobLog = builder_talendJobLog.connectorType(jcm.component_name)
								.connectorId(jcm.component_id).connectorLabel(jcm.component_label)
								.connectionName(jcm.current_connector).connectionType(jcm.current_connector_type)
								.rows(jcm.total_row_number).duration(duration).build();
						auditLogger_talendJobLog.flowInput(log_context_talendJobLog);
					} else if (jcm.log_type == JobStructureCatcherUtils.LogType.FLOWOUTPUT) {// log current component
																								// output/reject line
						long timeMS = jcm.end_time - jcm.start_time;
						String duration = String.valueOf(timeMS);

						log_context_talendJobLog = builder_talendJobLog.connectorType(jcm.component_name)
								.connectorId(jcm.component_id).connectorLabel(jcm.component_label)
								.connectionName(jcm.current_connector).connectionType(jcm.current_connector_type)
								.rows(jcm.total_row_number).duration(duration).build();
						auditLogger_talendJobLog.flowOutput(log_context_talendJobLog);
					} else if (jcm.log_type == JobStructureCatcherUtils.LogType.JOBERROR) {
						java.lang.Exception e_talendJobLog = jcm.exception;
						if (e_talendJobLog != null) {
							try (java.io.StringWriter sw_talendJobLog = new java.io.StringWriter();
									java.io.PrintWriter pw_talendJobLog = new java.io.PrintWriter(sw_talendJobLog)) {
								e_talendJobLog.printStackTrace(pw_talendJobLog);
								builder_talendJobLog.custom("stacktrace", sw_talendJobLog.getBuffer().substring(0,
										java.lang.Math.min(sw_talendJobLog.getBuffer().length(), 512)));
							}
						}

						if (jcm.extra_info != null) {
							builder_talendJobLog.connectorId(jcm.component_id).custom("extra_info", jcm.extra_info);
						}

						log_context_talendJobLog = builder_talendJobLog
								.connectorType(jcm.component_id.substring(0, jcm.component_id.lastIndexOf('_')))
								.connectorId(jcm.component_id)
								.connectorLabel(jcm.component_label == null ? jcm.component_id : jcm.component_label)
								.build();

						auditLogger_talendJobLog.exception(log_context_talendJobLog);
					}

				}

				/**
				 * [talendJobLog begin ] stop
				 */

				/**
				 * [talendJobLog main ] start
				 */

				s(currentComponent = "talendJobLog");

				tos_count_talendJobLog++;

				/**
				 * [talendJobLog main ] stop
				 */

				/**
				 * [talendJobLog process_data_begin ] start
				 */

				s(currentComponent = "talendJobLog");

				/**
				 * [talendJobLog process_data_begin ] stop
				 */

				/**
				 * [talendJobLog process_data_end ] start
				 */

				s(currentComponent = "talendJobLog");

				/**
				 * [talendJobLog process_data_end ] stop
				 */

				/**
				 * [talendJobLog end ] start
				 */

				s(currentComponent = "talendJobLog");

				ok_Hash.put("talendJobLog", true);
				end_Hash.put("talendJobLog", System.currentTimeMillis());

				/**
				 * [talendJobLog end ] stop
				 */

			} // end the resume

		} catch (java.lang.Exception e) {

			if (!(e instanceof TalendException)) {
				log.fatal(currentComponent + " " + e.getMessage(), e);
			}

			TalendException te = new TalendException(e, currentComponent, cLabel, globalMap);

			throw te;
		} catch (java.lang.Error error) {

			runStat.stopThreadStat();

			throw error;
		} finally {

			try {

				/**
				 * [talendJobLog finally ] start
				 */

				s(currentComponent = "talendJobLog");

				/**
				 * [talendJobLog finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("talendJobLog_SUBPROCESS_STATE", 1);
	}

	public String resuming_logs_dir_path = null;
	public String resuming_checkpoint_path = null;
	public String parent_part_launcher = null;
	private String resumeEntryMethodName = null;
	private boolean globalResumeTicket = false;

	public boolean watch = false;
	// portStats is null, it means don't execute the statistics
	public Integer portStats = null;
	public int portTraces = 4334;
	public String clientHost;
	public String defaultClientHost = "localhost";
	public String contextStr = "Default";
	public boolean isDefaultContext = true;
	public String pid = "0";
	public String rootPid = null;
	public String fatherPid = null;
	public String fatherNode = null;
	public long startTime = 0;
	public boolean isChildJob = false;
	public String log4jLevel = "";

	private boolean enableLogStash;
	private boolean enableLineage;

	private boolean execStat = true;

	private ThreadLocal<java.util.Map<String, String>> threadLocal = new ThreadLocal<java.util.Map<String, String>>() {
		protected java.util.Map<String, String> initialValue() {
			java.util.Map<String, String> threadRunResultMap = new java.util.HashMap<String, String>();
			threadRunResultMap.put("errorCode", null);
			threadRunResultMap.put("status", "");
			return threadRunResultMap;
		};
	};

	protected PropertiesWithType context_param = new PropertiesWithType();
	public java.util.Map<String, Object> parentContextMap = new java.util.HashMap<String, Object>();

	public String status = "";

	private final static java.util.Properties jobInfo = new java.util.Properties();
	private final static java.util.Map<String, String> mdcInfo = new java.util.HashMap<>();
	private final static java.util.concurrent.atomic.AtomicLong subJobPidCounter = new java.util.concurrent.atomic.AtomicLong();

	public static void main(String[] args) {
		final json_m json_mClass = new json_m();

		int exitCode = json_mClass.runJobInTOS(args);
		if (exitCode == 0) {
			log.info("TalendJob: 'json_m' - Done.");
		}

		System.exit(exitCode);
	}

	private void getjobInfo() {
		final String TEMPLATE_PATH = "src/main/templates/jobInfo_template.properties";
		final String BUILD_PATH = "../jobInfo.properties";
		final String path = this.getClass().getResource("").getPath();
		if (path.lastIndexOf("target") > 0) {
			final java.io.File templateFile = new java.io.File(
					path.substring(0, path.lastIndexOf("target")).concat(TEMPLATE_PATH));
			if (templateFile.exists()) {
				readJobInfo(templateFile);
				return;
			}
		}
		readJobInfo(new java.io.File(BUILD_PATH));
	}

	private void readJobInfo(java.io.File jobInfoFile) {

		if (jobInfoFile.exists()) {
			try (java.io.InputStream is = new java.io.FileInputStream(jobInfoFile)) {
				jobInfo.load(is);
			} catch (IOException e) {

				log.debug("Read jobInfo.properties file fail: " + e.getMessage());

			}
		}
		log.info(String.format("Project name: %s\tJob name: %s\tGIT Commit ID: %s\tTalend Version: %s", projectName,
				jobName, jobInfo.getProperty("gitCommitId"), "8.0.1.20250730_0900-patch"));

	}

	public String[][] runJob(String[] args) {

		int exitCode = runJobInTOS(args);
		String[][] bufferValue = new String[][] { { Integer.toString(exitCode) } };

		return bufferValue;
	}

	public boolean hastBufferOutputComponent() {
		boolean hastBufferOutput = false;

		return hastBufferOutput;
	}

	public int runJobInTOS(String[] args) {
		// reset status
		status = "";

		String lastStr = "";
		for (String arg : args) {
			if (arg.equalsIgnoreCase("--context_param")) {
				lastStr = arg;
			} else if (lastStr.equals("")) {
				evalParam(arg);
			} else {
				evalParam(lastStr + " " + arg);
				lastStr = "";
			}
		}

		final boolean enableCBP = false;
		boolean inOSGi = routines.system.BundleUtils.inOSGi();

		if (!inOSGi && isCBPClientPresent) {
			if (org.talend.metrics.CBPClient.getInstanceForCurrentVM() == null) {
				try {
					org.talend.metrics.CBPClient.startListenIfNotStarted(enableCBP, true);
				} catch (java.lang.Exception e) {
					errorCode = 1;
					status = "failure";
					e.printStackTrace();
					return 1;
				}
			}
		}

		enableLogStash = "true".equalsIgnoreCase(System.getProperty("audit.enabled"));

		if (!"".equals(log4jLevel)) {

			if ("trace".equalsIgnoreCase(log4jLevel)) {
				org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(),
						org.apache.logging.log4j.Level.TRACE);
			} else if ("debug".equalsIgnoreCase(log4jLevel)) {
				org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(),
						org.apache.logging.log4j.Level.DEBUG);
			} else if ("info".equalsIgnoreCase(log4jLevel)) {
				org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(),
						org.apache.logging.log4j.Level.INFO);
			} else if ("warn".equalsIgnoreCase(log4jLevel)) {
				org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(),
						org.apache.logging.log4j.Level.WARN);
			} else if ("error".equalsIgnoreCase(log4jLevel)) {
				org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(),
						org.apache.logging.log4j.Level.ERROR);
			} else if ("fatal".equalsIgnoreCase(log4jLevel)) {
				org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(),
						org.apache.logging.log4j.Level.FATAL);
			} else if ("off".equalsIgnoreCase(log4jLevel)) {
				org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(),
						org.apache.logging.log4j.Level.OFF);
			}
			org.apache.logging.log4j.core.config.Configurator
					.setLevel(org.apache.logging.log4j.LogManager.getRootLogger().getName(), log.getLevel());

		}

		getjobInfo();
		log.info("TalendJob: 'json_m' - Start.");

		java.util.Set<Object> jobInfoKeys = jobInfo.keySet();
		for (Object jobInfoKey : jobInfoKeys) {
			org.slf4j.MDC.put("_" + jobInfoKey.toString(), jobInfo.get(jobInfoKey).toString());
		}
		org.slf4j.MDC.put("_pid", pid);
		org.slf4j.MDC.put("_rootPid", rootPid);
		org.slf4j.MDC.put("_fatherPid", fatherPid);
		org.slf4j.MDC.put("_projectName", projectName);
		org.slf4j.MDC.put("_startTimestamp", java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)
				.format(java.time.format.DateTimeFormatter.ISO_INSTANT));
		org.slf4j.MDC.put("_jobRepositoryId", "_RPAwwJL1EfCnV4-ejo1b-g");
		org.slf4j.MDC.put("_compiledAtTimestamp", "2025-09-16T18:49:05.605713700Z");

		java.lang.management.RuntimeMXBean mx = java.lang.management.ManagementFactory.getRuntimeMXBean();
		String[] mxNameTable = mx.getName().split("@"); //$NON-NLS-1$
		if (mxNameTable.length == 2) {
			org.slf4j.MDC.put("_systemPid", mxNameTable[0]);
		} else {
			org.slf4j.MDC.put("_systemPid", String.valueOf(java.lang.Thread.currentThread().getId()));
		}

		if (enableLogStash) {
			java.util.Properties properties_talendJobLog = new java.util.Properties();
			properties_talendJobLog.setProperty("root.logger", "audit");
			properties_talendJobLog.setProperty("encoding", "UTF-8");
			properties_talendJobLog.setProperty("application.name", "Talend Studio");
			properties_talendJobLog.setProperty("service.name", "Talend Studio Job");
			properties_talendJobLog.setProperty("instance.name", "Talend Studio Job Instance");
			properties_talendJobLog.setProperty("propagate.appender.exceptions", "none");
			properties_talendJobLog.setProperty("log.appender", "file");
			properties_talendJobLog.setProperty("appender.file.path", "audit.json");
			properties_talendJobLog.setProperty("appender.file.maxsize", "52428800");
			properties_talendJobLog.setProperty("appender.file.maxbackup", "20");
			properties_talendJobLog.setProperty("host", "false");

			System.getProperties().stringPropertyNames().stream().filter(it -> it.startsWith("audit.logger."))
					.forEach(key -> properties_talendJobLog.setProperty(key.substring("audit.logger.".length()),
							System.getProperty(key)));

			org.apache.logging.log4j.core.config.Configurator
					.setLevel(properties_talendJobLog.getProperty("root.logger"), org.apache.logging.log4j.Level.DEBUG);

			auditLogger_talendJobLog = org.talend.job.audit.JobEventAuditLoggerFactory
					.createJobAuditLogger(properties_talendJobLog);
		}

		if (clientHost == null) {
			clientHost = defaultClientHost;
		}

		if (pid == null || "0".equals(pid)) {
			pid = TalendString.getAsciiRandomString(6);
		}

		org.slf4j.MDC.put("_pid", pid);

		if (rootPid == null) {
			rootPid = pid;
		}

		org.slf4j.MDC.put("_rootPid", rootPid);

		if (fatherPid == null) {
			fatherPid = pid;
		} else {
			isChildJob = true;
		}
		org.slf4j.MDC.put("_fatherPid", fatherPid);

		if (portStats != null) {
			// portStats = -1; //for testing
			if (portStats < 0 || portStats > 65535) {
				// issue:10869, the portStats is invalid, so this client socket can't open
				System.err.println("The statistics socket port " + portStats + " is invalid.");
				execStat = false;
			}
		} else {
			execStat = false;
		}

		try {
			java.util.Dictionary<String, Object> jobProperties = null;
			if (inOSGi) {
				jobProperties = routines.system.BundleUtils.getJobProperties(jobName);

				if (jobProperties != null && jobProperties.get("context") != null) {
					contextStr = (String) jobProperties.get("context");
				}

				if (jobProperties != null && jobProperties.get("taskExecutionId") != null) {
					taskExecutionId = (String) jobProperties.get("taskExecutionId");
				}

				// extract ids from parent route
				if (null == taskExecutionId || taskExecutionId.isEmpty()) {
					for (String arg : args) {
						if (arg.startsWith("--context_param")
								&& (arg.contains("taskExecutionId") || arg.contains("jobExecutionId"))) {

							String keyValue = arg.replace("--context_param", "");
							String[] parts = keyValue.split("=");
							String[] cleanParts = java.util.Arrays.stream(parts).filter(s -> !s.isEmpty())
									.toArray(String[]::new);
							if (cleanParts.length == 2) {
								String key = cleanParts[0];
								String value = cleanParts[1];
								if ("taskExecutionId".equals(key.trim()) && null != value) {
									taskExecutionId = value.trim();
								} else if ("jobExecutionId".equals(key.trim()) && null != value) {
									jobExecutionId = value.trim();
								}
							}
						}
					}
				}
			}

			// first load default key-value pairs from application.properties
			if (isStandaloneMS) {
				context.putAll(this.getDefaultProperties());
			}
			// call job/subjob with an existing context, like: --context=production. if
			// without this parameter, there will use the default context instead.
			java.io.InputStream inContext = json_m.class.getClassLoader()
					.getResourceAsStream("talend/json_m_0_1/contexts/" + contextStr + ".properties");
			if (inContext == null) {
				inContext = json_m.class.getClassLoader()
						.getResourceAsStream("config/contexts/" + contextStr + ".properties");
			}
			if (inContext != null) {
				try {
					// defaultProps is in order to keep the original context value
					if (context != null && context.isEmpty()) {
						defaultProps.load(inContext);
						if (inOSGi && jobProperties != null) {
							java.util.Enumeration<String> keys = jobProperties.keys();
							while (keys.hasMoreElements()) {
								String propKey = keys.nextElement();
								if (defaultProps.containsKey(propKey)) {
									defaultProps.put(propKey, (String) jobProperties.get(propKey));
								}
							}
						}
						context = new ContextProperties(defaultProps);
					}
					if (isStandaloneMS) {
						// override context key-value pairs if provided using --context=contextName
						defaultProps.load(inContext);
						context.putAll(defaultProps);
					}
				} finally {
					inContext.close();
				}
			} else if (!isDefaultContext) {
				// print info and job continue to run, for case: context_param is not empty.
				System.err.println("Could not find the context " + contextStr);
			}
			// override key-value pairs if provided via --config.location=file1.file2 OR
			// --config.additional-location=file1,file2
			if (isStandaloneMS) {
				context.putAll(this.getAdditionalProperties());
			}

			// override key-value pairs if provide via command line like
			// --key1=value1,--key2=value2
			if (!context_param.isEmpty()) {
				context.putAll(context_param);
				// set types for params from parentJobs
				for (Object key : context_param.keySet()) {
					String context_key = key.toString();
					String context_type = context_param.getContextType(context_key);
					context.setContextType(context_key, context_type);

				}
			}
			class ContextProcessing {
				private void processContext_0() {
				}

				public void processAllContext() {
					processContext_0();
				}
			}

			new ContextProcessing().processAllContext();
		} catch (java.io.IOException ie) {
			System.err.println("Could not load context " + contextStr);
			ie.printStackTrace();
		}

		// get context value from parent directly
		if (parentContextMap != null && !parentContextMap.isEmpty()) {
		}

		// Resume: init the resumeUtil
		resumeEntryMethodName = ResumeUtil.getResumeEntryMethodName(resuming_checkpoint_path);
		resumeUtil = new ResumeUtil(resuming_logs_dir_path, isChildJob, rootPid);
		resumeUtil.initCommonInfo(pid, rootPid, fatherPid, projectName, jobName, contextStr, jobVersion);

		List<String> parametersToEncrypt = new java.util.ArrayList<String>();
		// Resume: jobStart
		resumeUtil.addLog("JOB_STARTED", "JOB:" + jobName, parent_part_launcher, Thread.currentThread().getId() + "",
				"", "", "", "", resumeUtil.convertToJsonText(context, ContextProperties.class, parametersToEncrypt));

		org.slf4j.MDC.put("_context", contextStr);
		log.info("TalendJob: 'json_m' - Started.");
		java.util.Optional.ofNullable(org.slf4j.MDC.getCopyOfContextMap()).ifPresent(mdcInfo::putAll);

		if (execStat) {
			try {
				runStat.openSocket(!isChildJob);
				runStat.setAllPID(rootPid, fatherPid, pid, jobName);
				runStat.startThreadStat(clientHost, portStats);
				runStat.updateStatOnJob(RunStat.JOBSTART, fatherNode);
			} catch (java.io.IOException ioException) {
				ioException.printStackTrace();
			}
		}

		java.util.concurrent.ConcurrentHashMap<Object, Object> concurrentHashMap = new java.util.concurrent.ConcurrentHashMap<Object, Object>();
		globalMap.put("concurrentHashMap", concurrentHashMap);

		long startUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long endUsedMemory = 0;
		long end = 0;

		startTime = System.currentTimeMillis();

		this.globalResumeTicket = true;// to run tPreJob

		if (enableLogStash) {
			talendJobLog.addJobStartMessage();
			try {
				talendJobLogProcess(globalMap);
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		}

		this.globalResumeTicket = false;// to run others jobs

		try {
			errorCode = null;
			tFileInputJSON_1Process(globalMap);
			if (!"failure".equals(status)) {
				status = "end";
			}
		} catch (TalendException e_tFileInputJSON_1) {
			globalMap.put("tFileInputJSON_1_SUBPROCESS_STATE", -1);

			e_tFileInputJSON_1.printStackTrace();

		}

		this.globalResumeTicket = true;// to run tPostJob

		end = System.currentTimeMillis();

		if (watch) {
			System.out.println((end - startTime) + " milliseconds");
		}

		endUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		if (false) {
			System.out.println((endUsedMemory - startUsedMemory) + " bytes memory increase when running : json_m");
		}
		if (enableLogStash) {
			talendJobLog.addJobEndMessage(startTime, end, status);
			try {
				talendJobLogProcess(globalMap);
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		}

		if (execStat) {
			runStat.updateStatOnJob(RunStat.JOBEND, fatherNode);
			runStat.stopThreadStat();
		}
		if (!inOSGi && isCBPClientPresent) {
			if (org.talend.metrics.CBPClient.getInstanceForCurrentVM() != null) {
				s("none");
				org.talend.metrics.CBPClient.getInstanceForCurrentVM().sendData();
			}
		}

		int returnCode = 0;

		if (errorCode == null) {
			returnCode = status != null && status.equals("failure") ? 1 : 0;
		} else {
			returnCode = errorCode.intValue();
		}
		resumeUtil.addLog("JOB_ENDED", "JOB:" + jobName, parent_part_launcher, Thread.currentThread().getId() + "", "",
				"" + returnCode, "", "", "");
		resumeUtil.flush();

		org.slf4j.MDC.remove("_subJobName");
		org.slf4j.MDC.remove("_subJobPid");
		org.slf4j.MDC.remove("_systemPid");
		log.info("TalendJob: 'json_m' - Finished - status: " + status + " returnCode: " + returnCode);

		return returnCode;

	}

	// only for OSGi env
	public void destroy() {
		// add CBP code for OSGI Executions
		if (null != taskExecutionId && !taskExecutionId.isEmpty()) {
			try {
				org.talend.metrics.DataReadTracker.setExecutionId(taskExecutionId, jobExecutionId, false);
				org.talend.metrics.DataReadTracker.sealCounter();
				org.talend.metrics.DataReadTracker.reset();
			} catch (Exception | NoClassDefFoundError e) {
				// ignore
			}
		}

	}

	private java.util.Map<String, Object> getSharedConnections4REST() {
		java.util.Map<String, Object> connections = new java.util.HashMap<String, Object>();

		return connections;
	}

	private void evalParam(String arg) {
		if (arg.startsWith("--resuming_logs_dir_path")) {
			resuming_logs_dir_path = arg.substring(25);
		} else if (arg.startsWith("--resuming_checkpoint_path")) {
			resuming_checkpoint_path = arg.substring(27);
		} else if (arg.startsWith("--parent_part_launcher")) {
			parent_part_launcher = arg.substring(23);
		} else if (arg.startsWith("--watch")) {
			watch = true;
		} else if (arg.startsWith("--stat_port=")) {
			String portStatsStr = arg.substring(12);
			if (portStatsStr != null && !portStatsStr.equals("null")) {
				portStats = Integer.parseInt(portStatsStr);
			}
		} else if (arg.startsWith("--trace_port=")) {
			portTraces = Integer.parseInt(arg.substring(13));
		} else if (arg.startsWith("--client_host=")) {
			clientHost = arg.substring(14);
		} else if (arg.startsWith("--context=")) {
			contextStr = arg.substring(10);
			isDefaultContext = false;
		} else if (arg.startsWith("--father_pid=")) {
			fatherPid = arg.substring(13);
		} else if (arg.startsWith("--root_pid=")) {
			rootPid = arg.substring(11);
		} else if (arg.startsWith("--father_node=")) {
			fatherNode = arg.substring(14);
		} else if (arg.startsWith("--pid=")) {
			pid = arg.substring(6);
		} else if (arg.startsWith("--context_type")) {
			String keyValue = arg.substring(15);
			int index = -1;
			if (keyValue != null && (index = keyValue.indexOf('=')) > -1) {
				if (fatherPid == null) {
					context_param.setContextType(keyValue.substring(0, index),
							replaceEscapeChars(keyValue.substring(index + 1)));
				} else { // the subjob won't escape the especial chars
					context_param.setContextType(keyValue.substring(0, index), keyValue.substring(index + 1));
				}

			}

		} else if (arg.startsWith("--context_param")) {
			String keyValue = arg.substring(16);
			int index = -1;
			if (keyValue != null && (index = keyValue.indexOf('=')) > -1) {
				if (fatherPid == null) {
					context_param.put(keyValue.substring(0, index), replaceEscapeChars(keyValue.substring(index + 1)));
				} else { // the subjob won't escape the especial chars
					context_param.put(keyValue.substring(0, index), keyValue.substring(index + 1));
				}
			}
		} else if (arg.startsWith("--context_file")) {
			String keyValue = arg.substring(15);
			String filePath = new String(java.util.Base64.getDecoder().decode(keyValue));
			java.nio.file.Path contextFile = java.nio.file.Paths.get(filePath);
			try (java.io.BufferedReader reader = java.nio.file.Files.newBufferedReader(contextFile)) {
				String line;
				while ((line = reader.readLine()) != null) {
					int index = -1;
					if ((index = line.indexOf('=')) > -1) {
						if (line.startsWith("--context_param")) {
							if ("id_Password".equals(context_param.getContextType(line.substring(16, index)))) {
								context_param.put(line.substring(16, index),
										routines.system.PasswordEncryptUtil.decryptPassword(line.substring(index + 1)));
							} else {
								context_param.put(line.substring(16, index), line.substring(index + 1));
							}
						} else {// --context_type
							context_param.setContextType(line.substring(15, index), line.substring(index + 1));
						}
					}
				}
			} catch (java.io.IOException e) {
				System.err.println("Could not load the context file: " + filePath);
				e.printStackTrace();
			}
		} else if (arg.startsWith("--log4jLevel=")) {
			log4jLevel = arg.substring(13);
		} else if (arg.startsWith("--audit.enabled") && arg.contains("=")) {// for trunjob call
			final int equal = arg.indexOf('=');
			final String key = arg.substring("--".length(), equal);
			System.setProperty(key, arg.substring(equal + 1));
		}
	}

	private static final String NULL_VALUE_EXPRESSION_IN_COMMAND_STRING_FOR_CHILD_JOB_ONLY = "<TALEND_NULL>";

	private final String[][] escapeChars = { { "\\\\", "\\" }, { "\\n", "\n" }, { "\\'", "\'" }, { "\\r", "\r" },
			{ "\\f", "\f" }, { "\\b", "\b" }, { "\\t", "\t" } };

	private String replaceEscapeChars(String keyValue) {

		if (keyValue == null || ("").equals(keyValue.trim())) {
			return keyValue;
		}

		StringBuilder result = new StringBuilder();
		int currIndex = 0;
		while (currIndex < keyValue.length()) {
			int index = -1;
			// judege if the left string includes escape chars
			for (String[] strArray : escapeChars) {
				index = keyValue.indexOf(strArray[0], currIndex);
				if (index >= 0) {

					result.append(keyValue.substring(currIndex, index + strArray[0].length()).replace(strArray[0],
							strArray[1]));
					currIndex = index + strArray[0].length();
					break;
				}
			}
			// if the left string doesn't include escape chars, append the left into the
			// result
			if (index < 0) {
				result.append(keyValue.substring(currIndex));
				currIndex = currIndex + keyValue.length();
			}
		}

		return result.toString();
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public String getStatus() {
		return status;
	}

	ResumeUtil resumeUtil = null;
}
/************************************************************************************************
 * 127656 characters generated by Talend Cloud Data Management Platform on the
 * September 17, 2025 at 12:19:05 AM IST
 ************************************************************************************************/