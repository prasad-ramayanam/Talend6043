
package talend.file_to_db_0_1;

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
 * Job: file_to_db Purpose: <br>
 * Description:  <br>
 * @author R, lata
 * @version 8.0.1.20250730_0900-patch
 * @status 
 */
public class file_to_db implements TalendJob {
	static {System.setProperty("TalendJob.log", "file_to_db.log");}

	

	
	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(file_to_db.class);
	

static {
         String javaUtilLoggingConfigFile = System.getProperty("java.util.logging.config.file");
         if (javaUtilLoggingConfigFile == null) {
             setupDefaultJavaUtilLogging();
         }
}

/**
* This class replaces the default {@code System.err} stream used by Java Util Logging (JUL).
* You can use your own configuration through the
* {@code java.util.logging.config.file} system property, enabling you to specify an external
* logging configuration file for tailored logging setup.
*/
public static class StandardConsoleHandler extends java.util.logging.StreamHandler {
     public StandardConsoleHandler() {
         // Set System.out as default log output stream
         super(System.out, new java.util.logging.SimpleFormatter());
     }

     /**
      * Publish a {@code LogRecord}.
      * The logging request was made initially to a {@code Logger} object,
      * which initialized the {@code LogRecord} and forwarded it here.
      *
      * @param  record  description of the log event. A null record is
      *                 silently ignored and is not published
      */
      @Override
      public void publish(java.util.logging.LogRecord record) {
            super.publish(record);
            flush();
      }

      /**
      * Override {@code StreamHandler.close} to do a flush but not
      * to close the output stream.  That is, we do <b>not</b>
      * close {@code System.out}.
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

	//contains type for every context property
	public class PropertiesWithType extends java.util.Properties {
		private static final long serialVersionUID = 1L;
		private java.util.Map<String,String> propertyTypes = new java.util.HashMap<>();
		
		public PropertiesWithType(java.util.Properties properties){
			super(properties);
		}
		public PropertiesWithType(){
			super();
		}
		
		public void setContextType(String key, String type) {
			propertyTypes.put(key,type);
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

		public ContextProperties(java.util.Properties properties){
			super(properties);
		}
		public ContextProperties(){
			super();
		}

		public void synchronizeContext(){
			
		}
		
		//if the stored or passed value is "<TALEND_NULL>" string, it mean null
		public String getStringValue(String key) {
			String origin_value = this.getProperty(key);
			if(NULL_VALUE_EXPRESSION_IN_COMMAND_STRING_FOR_CHILD_JOB_ONLY.equals(origin_value)) {
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
	private final String jobName = "file_to_db";
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

	
	private String cLabel =  null;
	
		private final java.util.Map<String, Object> globalMap = new java.util.HashMap<String, Object>();
        private final static java.util.Map<String, Object> junitGlobalMap = new java.util.HashMap<String, Object>();
	
		private final java.util.Map<String, Long> start_Hash = new java.util.HashMap<String, Long>();
		private final java.util.Map<String, Long> end_Hash = new java.util.HashMap<String, Long>();
		private final java.util.Map<String, Boolean> ok_Hash = new java.util.HashMap<String, Boolean>();
		public  final java.util.List<String[]> globalBuffer = new java.util.ArrayList<String[]>();
	

private final JobStructureCatcherUtils talendJobLog = new JobStructureCatcherUtils(jobName, "_J_fWgI75EfC-J65at5wWMg", "0.1");
private org.talend.job.audit.JobAuditLogger auditLogger_talendJobLog = null;

private RunStat runStat = new RunStat(talendJobLog, System.getProperty("audit.interval"));

	// OSGi DataSource
	private final static String KEY_DB_DATASOURCES = "KEY_DB_DATASOURCES";
	
	private final static String KEY_DB_DATASOURCES_RAW = "KEY_DB_DATASOURCES_RAW";

	public void setDataSources(java.util.Map<String, javax.sql.DataSource> dataSources) {
		java.util.Map<String, routines.system.TalendDataSource> talendDataSources = new java.util.HashMap<String, routines.system.TalendDataSource>();
		for (java.util.Map.Entry<String, javax.sql.DataSource> dataSourceEntry : dataSources.entrySet()) {
			talendDataSources.put(dataSourceEntry.getKey(), new routines.system.TalendDataSource(dataSourceEntry.getValue()));
		}
		globalMap.put(KEY_DB_DATASOURCES, talendDataSources);
		globalMap.put(KEY_DB_DATASOURCES_RAW, new java.util.HashMap<String, javax.sql.DataSource>(dataSources));
	}
	
	public void setDataSourceReferences(List serviceReferences) throws Exception{
		
		java.util.Map<String, routines.system.TalendDataSource> talendDataSources = new java.util.HashMap<String, routines.system.TalendDataSource>();
		java.util.Map<String, javax.sql.DataSource> dataSources = new java.util.HashMap<String, javax.sql.DataSource>();
		
		for (java.util.Map.Entry<String, javax.sql.DataSource> entry : BundleUtils.getServices(serviceReferences,  javax.sql.DataSource.class).entrySet()) {
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
	private String cLabel =  null;
	
	private String virtualComponentName = null;
	
	public void setVirtualComponentName (String virtualComponentName){
		this.virtualComponentName = virtualComponentName;
	}

	private TalendException(Exception e, String errorComponent, final java.util.Map<String, Object> globalMap) {
		this.currentComponent= errorComponent;
		this.globalMap = globalMap;
		this.e = e;
	}
	
	private TalendException(Exception e, String errorComponent, String errorComponentLabel, final java.util.Map<String, Object> globalMap) {
		this(e, errorComponent, globalMap);
		this.cLabel = errorComponentLabel;
	}

	public Exception getException() {
		return this.e;
	}

	public String getCurrentComponent() {
		return this.currentComponent;
	}

	
    public String getExceptionCauseMessage(Exception e){
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
			if(virtualComponentName!=null && currentComponent.indexOf(virtualComponentName+"_")==0){
				globalMap.put(virtualComponentName+"_ERROR_MESSAGE",getExceptionCauseMessage(e));
			}
			globalMap.put(currentComponent+"_ERROR_MESSAGE",getExceptionCauseMessage(e));
			System.err.println("Exception in component " + currentComponent + " (" + jobName + ")");
		}
		if (!(e instanceof TDieException)) {
			if(e instanceof TalendException){
				e.printStackTrace();
			} else {
				e.printStackTrace();
				e.printStackTrace(errorMessagePS);
			}
		}
		if (!(e instanceof TalendException)) {
			file_to_db.this.exception = e;
		}
		if (!(e instanceof TalendException)) {
		try {
			for (java.lang.reflect.Method m : this.getClass().getEnclosingClass().getMethods()) {
				if (m.getName().compareTo(currentComponent + "_error") == 0) {
					m.invoke(file_to_db.this, new Object[] { e , currentComponent, globalMap});
					break;
				}
			}

			if(!(e instanceof TDieException)){
		if(enableLogStash) {
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

			public void tFileInputDelimited_1_error(Exception exception, String errorComponent, final java.util.Map<String, Object> globalMap) throws TalendException {
				
				end_Hash.put(errorComponent, System.currentTimeMillis());
				
				status = "failure";
				
					tFileInputDelimited_1_onSubJobError(exception, errorComponent, globalMap);
			}
			
			public void tLogRow_1_error(Exception exception, String errorComponent, final java.util.Map<String, Object> globalMap) throws TalendException {
				
				end_Hash.put(errorComponent, System.currentTimeMillis());
				
				status = "failure";
				
					tFileInputDelimited_1_onSubJobError(exception, errorComponent, globalMap);
			}
			
			public void tDBOutput_1_error(Exception exception, String errorComponent, final java.util.Map<String, Object> globalMap) throws TalendException {
				
				end_Hash.put(errorComponent, System.currentTimeMillis());
				
				status = "failure";
				
					tFileInputDelimited_1_onSubJobError(exception, errorComponent, globalMap);
			}
			
			public void talendJobLog_error(Exception exception, String errorComponent, final java.util.Map<String, Object> globalMap) throws TalendException {
				
				end_Hash.put(errorComponent, System.currentTimeMillis());
				
				status = "failure";
				
					talendJobLog_onSubJobError(exception, errorComponent, globalMap);
			}
			
			public void tFileInputDelimited_1_onSubJobError(Exception exception, String errorComponent, final java.util.Map<String, Object> globalMap) throws TalendException {

resumeUtil.addLog("SYSTEM_LOG", "NODE:"+ errorComponent, "", Thread.currentThread().getId()+ "", "FATAL", "", exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception),"");

			}
			public void talendJobLog_onSubJobError(Exception exception, String errorComponent, final java.util.Map<String, Object> globalMap) throws TalendException {

resumeUtil.addLog("SYSTEM_LOG", "NODE:"+ errorComponent, "", Thread.currentThread().getId()+ "", "FATAL", "", exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception),"");

			}
	



public static class row2Struct implements routines.system.IPersistableRow<row2Struct> {
    final static byte[] commonByteArrayLock_TALEND_file_to_db = new byte[0];
    static byte[] commonByteArray_TALEND_file_to_db = new byte[0];

	
			    public Integer EMPLOYEE_ID;

				public Integer getEMPLOYEE_ID () {
					return this.EMPLOYEE_ID;
				}

				public Boolean EMPLOYEE_IDIsNullable(){
				    return true;
				}
				public Boolean EMPLOYEE_IDIsKey(){
				    return false;
				}
				public Integer EMPLOYEE_IDLength(){
				    return null;
				}
				public Integer EMPLOYEE_IDPrecision(){
				    return null;
				}
				public String EMPLOYEE_IDDefault(){
				
					return null;
				
				}
				public String EMPLOYEE_IDComment(){
				
				    return "";
				
				}
				public String EMPLOYEE_IDPattern(){
				
					return "";
				
				}
				public String EMPLOYEE_IDOriginalDbColumnName(){
				
					return "EMPLOYEE_ID";
				
				}

				
			    public String FIRST_NAME;

				public String getFIRST_NAME () {
					return this.FIRST_NAME;
				}

				public Boolean FIRST_NAMEIsNullable(){
				    return true;
				}
				public Boolean FIRST_NAMEIsKey(){
				    return false;
				}
				public Integer FIRST_NAMELength(){
				    return null;
				}
				public Integer FIRST_NAMEPrecision(){
				    return null;
				}
				public String FIRST_NAMEDefault(){
				
					return null;
				
				}
				public String FIRST_NAMEComment(){
				
				    return "";
				
				}
				public String FIRST_NAMEPattern(){
				
					return "";
				
				}
				public String FIRST_NAMEOriginalDbColumnName(){
				
					return "FIRST_NAME";
				
				}

				
			    public String LAST_NAME;

				public String getLAST_NAME () {
					return this.LAST_NAME;
				}

				public Boolean LAST_NAMEIsNullable(){
				    return true;
				}
				public Boolean LAST_NAMEIsKey(){
				    return false;
				}
				public Integer LAST_NAMELength(){
				    return null;
				}
				public Integer LAST_NAMEPrecision(){
				    return null;
				}
				public String LAST_NAMEDefault(){
				
					return null;
				
				}
				public String LAST_NAMEComment(){
				
				    return "";
				
				}
				public String LAST_NAMEPattern(){
				
					return "";
				
				}
				public String LAST_NAMEOriginalDbColumnName(){
				
					return "LAST_NAME";
				
				}

				
			    public String EMAIL;

				public String getEMAIL () {
					return this.EMAIL;
				}

				public Boolean EMAILIsNullable(){
				    return true;
				}
				public Boolean EMAILIsKey(){
				    return false;
				}
				public Integer EMAILLength(){
				    return null;
				}
				public Integer EMAILPrecision(){
				    return null;
				}
				public String EMAILDefault(){
				
					return null;
				
				}
				public String EMAILComment(){
				
				    return "";
				
				}
				public String EMAILPattern(){
				
					return "";
				
				}
				public String EMAILOriginalDbColumnName(){
				
					return "EMAIL";
				
				}

				
			    public String PHONE_NUMBER;

				public String getPHONE_NUMBER () {
					return this.PHONE_NUMBER;
				}

				public Boolean PHONE_NUMBERIsNullable(){
				    return true;
				}
				public Boolean PHONE_NUMBERIsKey(){
				    return false;
				}
				public Integer PHONE_NUMBERLength(){
				    return null;
				}
				public Integer PHONE_NUMBERPrecision(){
				    return null;
				}
				public String PHONE_NUMBERDefault(){
				
					return null;
				
				}
				public String PHONE_NUMBERComment(){
				
				    return "";
				
				}
				public String PHONE_NUMBERPattern(){
				
					return "";
				
				}
				public String PHONE_NUMBEROriginalDbColumnName(){
				
					return "PHONE_NUMBER";
				
				}

				
			    public java.util.Date HIRE_DATE;

				public java.util.Date getHIRE_DATE () {
					return this.HIRE_DATE;
				}

				public Boolean HIRE_DATEIsNullable(){
				    return true;
				}
				public Boolean HIRE_DATEIsKey(){
				    return false;
				}
				public Integer HIRE_DATELength(){
				    return null;
				}
				public Integer HIRE_DATEPrecision(){
				    return null;
				}
				public String HIRE_DATEDefault(){
				
					return null;
				
				}
				public String HIRE_DATEComment(){
				
				    return "";
				
				}
				public String HIRE_DATEPattern(){
				
					return "dd-MM-yyyy";
				
				}
				public String HIRE_DATEOriginalDbColumnName(){
				
					return "HIRE_DATE";
				
				}

				
			    public String JOB_ID;

				public String getJOB_ID () {
					return this.JOB_ID;
				}

				public Boolean JOB_IDIsNullable(){
				    return true;
				}
				public Boolean JOB_IDIsKey(){
				    return false;
				}
				public Integer JOB_IDLength(){
				    return null;
				}
				public Integer JOB_IDPrecision(){
				    return null;
				}
				public String JOB_IDDefault(){
				
					return null;
				
				}
				public String JOB_IDComment(){
				
				    return "";
				
				}
				public String JOB_IDPattern(){
				
					return "";
				
				}
				public String JOB_IDOriginalDbColumnName(){
				
					return "JOB_ID";
				
				}

				
			    public Integer SALARY;

				public Integer getSALARY () {
					return this.SALARY;
				}

				public Boolean SALARYIsNullable(){
				    return true;
				}
				public Boolean SALARYIsKey(){
				    return false;
				}
				public Integer SALARYLength(){
				    return null;
				}
				public Integer SALARYPrecision(){
				    return null;
				}
				public String SALARYDefault(){
				
					return null;
				
				}
				public String SALARYComment(){
				
				    return "";
				
				}
				public String SALARYPattern(){
				
					return "";
				
				}
				public String SALARYOriginalDbColumnName(){
				
					return "SALARY";
				
				}

				
			    public String COMMISSION_PCT;

				public String getCOMMISSION_PCT () {
					return this.COMMISSION_PCT;
				}

				public Boolean COMMISSION_PCTIsNullable(){
				    return true;
				}
				public Boolean COMMISSION_PCTIsKey(){
				    return false;
				}
				public Integer COMMISSION_PCTLength(){
				    return null;
				}
				public Integer COMMISSION_PCTPrecision(){
				    return null;
				}
				public String COMMISSION_PCTDefault(){
				
					return null;
				
				}
				public String COMMISSION_PCTComment(){
				
				    return "";
				
				}
				public String COMMISSION_PCTPattern(){
				
					return "";
				
				}
				public String COMMISSION_PCTOriginalDbColumnName(){
				
					return "COMMISSION_PCT";
				
				}

				
			    public Integer MANAGER_ID;

				public Integer getMANAGER_ID () {
					return this.MANAGER_ID;
				}

				public Boolean MANAGER_IDIsNullable(){
				    return true;
				}
				public Boolean MANAGER_IDIsKey(){
				    return false;
				}
				public Integer MANAGER_IDLength(){
				    return null;
				}
				public Integer MANAGER_IDPrecision(){
				    return null;
				}
				public String MANAGER_IDDefault(){
				
					return null;
				
				}
				public String MANAGER_IDComment(){
				
				    return "";
				
				}
				public String MANAGER_IDPattern(){
				
					return "";
				
				}
				public String MANAGER_IDOriginalDbColumnName(){
				
					return "MANAGER_ID";
				
				}

				
			    public Integer DEPARTMENT_ID;

				public Integer getDEPARTMENT_ID () {
					return this.DEPARTMENT_ID;
				}

				public Boolean DEPARTMENT_IDIsNullable(){
				    return true;
				}
				public Boolean DEPARTMENT_IDIsKey(){
				    return false;
				}
				public Integer DEPARTMENT_IDLength(){
				    return null;
				}
				public Integer DEPARTMENT_IDPrecision(){
				    return null;
				}
				public String DEPARTMENT_IDDefault(){
				
					return null;
				
				}
				public String DEPARTMENT_IDComment(){
				
				    return "";
				
				}
				public String DEPARTMENT_IDPattern(){
				
					return "";
				
				}
				public String DEPARTMENT_IDOriginalDbColumnName(){
				
					return "DEPARTMENT_ID";
				
				}

				


	private Integer readInteger(ObjectInputStream dis) throws IOException{
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
	
	private Integer readInteger(org.jboss.marshalling.Unmarshaller dis) throws IOException{
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

	private void writeInteger(Integer intNum, ObjectOutputStream dos) throws IOException{
		if(intNum == null) {
            dos.writeByte(-1);
		} else {
			dos.writeByte(0);
	    	dos.writeInt(intNum);
    	}
	}
	
	private void writeInteger(Integer intNum, org.jboss.marshalling.Marshaller marshaller) throws IOException{
		if(intNum == null) {
			marshaller.writeByte(-1);
		} else {
			marshaller.writeByte(0);
			marshaller.writeInt(intNum);
    	}
	}

	private String readString(ObjectInputStream dis) throws IOException{
		String strReturn = null;
		int length = 0;
        length = dis.readInt();
		if (length == -1) {
			strReturn = null;
		} else {
			if(length > commonByteArray_TALEND_file_to_db.length) {
				if(length < 1024 && commonByteArray_TALEND_file_to_db.length == 0) {
   					commonByteArray_TALEND_file_to_db = new byte[1024];
				} else {
   					commonByteArray_TALEND_file_to_db = new byte[2 * length];
   				}
			}
			dis.readFully(commonByteArray_TALEND_file_to_db, 0, length);
			strReturn = new String(commonByteArray_TALEND_file_to_db, 0, length, utf8Charset);
		}
		return strReturn;
	}
	
	private String readString(org.jboss.marshalling.Unmarshaller unmarshaller) throws IOException{
		String strReturn = null;
		int length = 0;
        length = unmarshaller.readInt();
		if (length == -1) {
			strReturn = null;
		} else {
			if(length > commonByteArray_TALEND_file_to_db.length) {
				if(length < 1024 && commonByteArray_TALEND_file_to_db.length == 0) {
   					commonByteArray_TALEND_file_to_db = new byte[1024];
				} else {
   					commonByteArray_TALEND_file_to_db = new byte[2 * length];
   				}
			}
			unmarshaller.readFully(commonByteArray_TALEND_file_to_db, 0, length);
			strReturn = new String(commonByteArray_TALEND_file_to_db, 0, length, utf8Charset);
		}
		return strReturn;
	}

    private void writeString(String str, ObjectOutputStream dos) throws IOException{
		if(str == null) {
            dos.writeInt(-1);
		} else {
            byte[] byteArray = str.getBytes(utf8Charset);
	    	dos.writeInt(byteArray.length);
			dos.write(byteArray);
    	}
    }
    
    private void writeString(String str, org.jboss.marshalling.Marshaller marshaller) throws IOException{
		if(str == null) {
			marshaller.writeInt(-1);
		} else {
            byte[] byteArray = str.getBytes(utf8Charset);
            marshaller.writeInt(byteArray.length);
            marshaller.write(byteArray);
    	}
    }

	private java.util.Date readDate(ObjectInputStream dis) throws IOException{
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
	
	private java.util.Date readDate(org.jboss.marshalling.Unmarshaller unmarshaller) throws IOException{
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

    private void writeDate(java.util.Date date1, ObjectOutputStream dos) throws IOException{
		if(date1 == null) {
            dos.writeByte(-1);
		} else {
			dos.writeByte(0);
	    	dos.writeLong(date1.getTime());
    	}
    }
    
    private void writeDate(java.util.Date date1, org.jboss.marshalling.Marshaller marshaller) throws IOException{
		if(date1 == null) {
			marshaller.writeByte(-1);
		} else {
			marshaller.writeByte(0);
			marshaller.writeLong(date1.getTime());
    	}
    }

    public void readData(ObjectInputStream dis) {

		synchronized(commonByteArrayLock_TALEND_file_to_db) {

        	try {

        		int length = 0;
		
						this.EMPLOYEE_ID = readInteger(dis);
					
					this.FIRST_NAME = readString(dis);
					
					this.LAST_NAME = readString(dis);
					
					this.EMAIL = readString(dis);
					
					this.PHONE_NUMBER = readString(dis);
					
					this.HIRE_DATE = readDate(dis);
					
					this.JOB_ID = readString(dis);
					
						this.SALARY = readInteger(dis);
					
					this.COMMISSION_PCT = readString(dis);
					
						this.MANAGER_ID = readInteger(dis);
					
						this.DEPARTMENT_ID = readInteger(dis);
					
        	} catch (IOException e) {
	            throw new RuntimeException(e);

		

        }

		

      }


    }
    
    public void readData(org.jboss.marshalling.Unmarshaller dis) {

		synchronized(commonByteArrayLock_TALEND_file_to_db) {

        	try {

        		int length = 0;
		
						this.EMPLOYEE_ID = readInteger(dis);
					
					this.FIRST_NAME = readString(dis);
					
					this.LAST_NAME = readString(dis);
					
					this.EMAIL = readString(dis);
					
					this.PHONE_NUMBER = readString(dis);
					
					this.HIRE_DATE = readDate(dis);
					
					this.JOB_ID = readString(dis);
					
						this.SALARY = readInteger(dis);
					
					this.COMMISSION_PCT = readString(dis);
					
						this.MANAGER_ID = readInteger(dis);
					
						this.DEPARTMENT_ID = readInteger(dis);
					
        	} catch (IOException e) {
	            throw new RuntimeException(e);

		

        }

		

      }


    }

    public void writeData(ObjectOutputStream dos) {
        try {

		
					// Integer
				
						writeInteger(this.EMPLOYEE_ID,dos);
					
					// String
				
						writeString(this.FIRST_NAME,dos);
					
					// String
				
						writeString(this.LAST_NAME,dos);
					
					// String
				
						writeString(this.EMAIL,dos);
					
					// String
				
						writeString(this.PHONE_NUMBER,dos);
					
					// java.util.Date
				
						writeDate(this.HIRE_DATE,dos);
					
					// String
				
						writeString(this.JOB_ID,dos);
					
					// Integer
				
						writeInteger(this.SALARY,dos);
					
					// String
				
						writeString(this.COMMISSION_PCT,dos);
					
					// Integer
				
						writeInteger(this.MANAGER_ID,dos);
					
					// Integer
				
						writeInteger(this.DEPARTMENT_ID,dos);
					
        	} catch (IOException e) {
	            throw new RuntimeException(e);
        }


    }
    
    public void writeData(org.jboss.marshalling.Marshaller dos) {
        try {

		
					// Integer
				
						writeInteger(this.EMPLOYEE_ID,dos);
					
					// String
				
						writeString(this.FIRST_NAME,dos);
					
					// String
				
						writeString(this.LAST_NAME,dos);
					
					// String
				
						writeString(this.EMAIL,dos);
					
					// String
				
						writeString(this.PHONE_NUMBER,dos);
					
					// java.util.Date
				
						writeDate(this.HIRE_DATE,dos);
					
					// String
				
						writeString(this.JOB_ID,dos);
					
					// Integer
				
						writeInteger(this.SALARY,dos);
					
					// String
				
						writeString(this.COMMISSION_PCT,dos);
					
					// Integer
				
						writeInteger(this.MANAGER_ID,dos);
					
					// Integer
				
						writeInteger(this.DEPARTMENT_ID,dos);
					
        	} catch (IOException e) {
	            throw new RuntimeException(e);
        }


    }


    public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append("[");
		sb.append("EMPLOYEE_ID="+String.valueOf(EMPLOYEE_ID));
		sb.append(",FIRST_NAME="+FIRST_NAME);
		sb.append(",LAST_NAME="+LAST_NAME);
		sb.append(",EMAIL="+EMAIL);
		sb.append(",PHONE_NUMBER="+PHONE_NUMBER);
		sb.append(",HIRE_DATE="+String.valueOf(HIRE_DATE));
		sb.append(",JOB_ID="+JOB_ID);
		sb.append(",SALARY="+String.valueOf(SALARY));
		sb.append(",COMMISSION_PCT="+COMMISSION_PCT);
		sb.append(",MANAGER_ID="+String.valueOf(MANAGER_ID));
		sb.append(",DEPARTMENT_ID="+String.valueOf(DEPARTMENT_ID));
	    sb.append("]");

	    return sb.toString();
    }
        public String toLogString(){
        	StringBuilder sb = new StringBuilder();
        	
        				if(EMPLOYEE_ID == null){
        					sb.append("<null>");
        				}else{
            				sb.append(EMPLOYEE_ID);
            			}
            		
        			sb.append("|");
        		
        				if(FIRST_NAME == null){
        					sb.append("<null>");
        				}else{
            				sb.append(FIRST_NAME);
            			}
            		
        			sb.append("|");
        		
        				if(LAST_NAME == null){
        					sb.append("<null>");
        				}else{
            				sb.append(LAST_NAME);
            			}
            		
        			sb.append("|");
        		
        				if(EMAIL == null){
        					sb.append("<null>");
        				}else{
            				sb.append(EMAIL);
            			}
            		
        			sb.append("|");
        		
        				if(PHONE_NUMBER == null){
        					sb.append("<null>");
        				}else{
            				sb.append(PHONE_NUMBER);
            			}
            		
        			sb.append("|");
        		
        				if(HIRE_DATE == null){
        					sb.append("<null>");
        				}else{
            				sb.append(HIRE_DATE);
            			}
            		
        			sb.append("|");
        		
        				if(JOB_ID == null){
        					sb.append("<null>");
        				}else{
            				sb.append(JOB_ID);
            			}
            		
        			sb.append("|");
        		
        				if(SALARY == null){
        					sb.append("<null>");
        				}else{
            				sb.append(SALARY);
            			}
            		
        			sb.append("|");
        		
        				if(COMMISSION_PCT == null){
        					sb.append("<null>");
        				}else{
            				sb.append(COMMISSION_PCT);
            			}
            		
        			sb.append("|");
        		
        				if(MANAGER_ID == null){
        					sb.append("<null>");
        				}else{
            				sb.append(MANAGER_ID);
            			}
            		
        			sb.append("|");
        		
        				if(DEPARTMENT_ID == null){
        					sb.append("<null>");
        				}else{
            				sb.append(DEPARTMENT_ID);
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
    final static byte[] commonByteArrayLock_TALEND_file_to_db = new byte[0];
    static byte[] commonByteArray_TALEND_file_to_db = new byte[0];

	
			    public Integer EMPLOYEE_ID;

				public Integer getEMPLOYEE_ID () {
					return this.EMPLOYEE_ID;
				}

				public Boolean EMPLOYEE_IDIsNullable(){
				    return true;
				}
				public Boolean EMPLOYEE_IDIsKey(){
				    return false;
				}
				public Integer EMPLOYEE_IDLength(){
				    return null;
				}
				public Integer EMPLOYEE_IDPrecision(){
				    return null;
				}
				public String EMPLOYEE_IDDefault(){
				
					return null;
				
				}
				public String EMPLOYEE_IDComment(){
				
				    return "";
				
				}
				public String EMPLOYEE_IDPattern(){
				
					return "";
				
				}
				public String EMPLOYEE_IDOriginalDbColumnName(){
				
					return "EMPLOYEE_ID";
				
				}

				
			    public String FIRST_NAME;

				public String getFIRST_NAME () {
					return this.FIRST_NAME;
				}

				public Boolean FIRST_NAMEIsNullable(){
				    return true;
				}
				public Boolean FIRST_NAMEIsKey(){
				    return false;
				}
				public Integer FIRST_NAMELength(){
				    return null;
				}
				public Integer FIRST_NAMEPrecision(){
				    return null;
				}
				public String FIRST_NAMEDefault(){
				
					return null;
				
				}
				public String FIRST_NAMEComment(){
				
				    return "";
				
				}
				public String FIRST_NAMEPattern(){
				
					return "";
				
				}
				public String FIRST_NAMEOriginalDbColumnName(){
				
					return "FIRST_NAME";
				
				}

				
			    public String LAST_NAME;

				public String getLAST_NAME () {
					return this.LAST_NAME;
				}

				public Boolean LAST_NAMEIsNullable(){
				    return true;
				}
				public Boolean LAST_NAMEIsKey(){
				    return false;
				}
				public Integer LAST_NAMELength(){
				    return null;
				}
				public Integer LAST_NAMEPrecision(){
				    return null;
				}
				public String LAST_NAMEDefault(){
				
					return null;
				
				}
				public String LAST_NAMEComment(){
				
				    return "";
				
				}
				public String LAST_NAMEPattern(){
				
					return "";
				
				}
				public String LAST_NAMEOriginalDbColumnName(){
				
					return "LAST_NAME";
				
				}

				
			    public String EMAIL;

				public String getEMAIL () {
					return this.EMAIL;
				}

				public Boolean EMAILIsNullable(){
				    return true;
				}
				public Boolean EMAILIsKey(){
				    return false;
				}
				public Integer EMAILLength(){
				    return null;
				}
				public Integer EMAILPrecision(){
				    return null;
				}
				public String EMAILDefault(){
				
					return null;
				
				}
				public String EMAILComment(){
				
				    return "";
				
				}
				public String EMAILPattern(){
				
					return "";
				
				}
				public String EMAILOriginalDbColumnName(){
				
					return "EMAIL";
				
				}

				
			    public String PHONE_NUMBER;

				public String getPHONE_NUMBER () {
					return this.PHONE_NUMBER;
				}

				public Boolean PHONE_NUMBERIsNullable(){
				    return true;
				}
				public Boolean PHONE_NUMBERIsKey(){
				    return false;
				}
				public Integer PHONE_NUMBERLength(){
				    return null;
				}
				public Integer PHONE_NUMBERPrecision(){
				    return null;
				}
				public String PHONE_NUMBERDefault(){
				
					return null;
				
				}
				public String PHONE_NUMBERComment(){
				
				    return "";
				
				}
				public String PHONE_NUMBERPattern(){
				
					return "";
				
				}
				public String PHONE_NUMBEROriginalDbColumnName(){
				
					return "PHONE_NUMBER";
				
				}

				
			    public java.util.Date HIRE_DATE;

				public java.util.Date getHIRE_DATE () {
					return this.HIRE_DATE;
				}

				public Boolean HIRE_DATEIsNullable(){
				    return true;
				}
				public Boolean HIRE_DATEIsKey(){
				    return false;
				}
				public Integer HIRE_DATELength(){
				    return null;
				}
				public Integer HIRE_DATEPrecision(){
				    return null;
				}
				public String HIRE_DATEDefault(){
				
					return null;
				
				}
				public String HIRE_DATEComment(){
				
				    return "";
				
				}
				public String HIRE_DATEPattern(){
				
					return "dd-MM-yyyy";
				
				}
				public String HIRE_DATEOriginalDbColumnName(){
				
					return "HIRE_DATE";
				
				}

				
			    public String JOB_ID;

				public String getJOB_ID () {
					return this.JOB_ID;
				}

				public Boolean JOB_IDIsNullable(){
				    return true;
				}
				public Boolean JOB_IDIsKey(){
				    return false;
				}
				public Integer JOB_IDLength(){
				    return null;
				}
				public Integer JOB_IDPrecision(){
				    return null;
				}
				public String JOB_IDDefault(){
				
					return null;
				
				}
				public String JOB_IDComment(){
				
				    return "";
				
				}
				public String JOB_IDPattern(){
				
					return "";
				
				}
				public String JOB_IDOriginalDbColumnName(){
				
					return "JOB_ID";
				
				}

				
			    public Integer SALARY;

				public Integer getSALARY () {
					return this.SALARY;
				}

				public Boolean SALARYIsNullable(){
				    return true;
				}
				public Boolean SALARYIsKey(){
				    return false;
				}
				public Integer SALARYLength(){
				    return null;
				}
				public Integer SALARYPrecision(){
				    return null;
				}
				public String SALARYDefault(){
				
					return null;
				
				}
				public String SALARYComment(){
				
				    return "";
				
				}
				public String SALARYPattern(){
				
					return "";
				
				}
				public String SALARYOriginalDbColumnName(){
				
					return "SALARY";
				
				}

				
			    public String COMMISSION_PCT;

				public String getCOMMISSION_PCT () {
					return this.COMMISSION_PCT;
				}

				public Boolean COMMISSION_PCTIsNullable(){
				    return true;
				}
				public Boolean COMMISSION_PCTIsKey(){
				    return false;
				}
				public Integer COMMISSION_PCTLength(){
				    return null;
				}
				public Integer COMMISSION_PCTPrecision(){
				    return null;
				}
				public String COMMISSION_PCTDefault(){
				
					return null;
				
				}
				public String COMMISSION_PCTComment(){
				
				    return "";
				
				}
				public String COMMISSION_PCTPattern(){
				
					return "";
				
				}
				public String COMMISSION_PCTOriginalDbColumnName(){
				
					return "COMMISSION_PCT";
				
				}

				
			    public Integer MANAGER_ID;

				public Integer getMANAGER_ID () {
					return this.MANAGER_ID;
				}

				public Boolean MANAGER_IDIsNullable(){
				    return true;
				}
				public Boolean MANAGER_IDIsKey(){
				    return false;
				}
				public Integer MANAGER_IDLength(){
				    return null;
				}
				public Integer MANAGER_IDPrecision(){
				    return null;
				}
				public String MANAGER_IDDefault(){
				
					return null;
				
				}
				public String MANAGER_IDComment(){
				
				    return "";
				
				}
				public String MANAGER_IDPattern(){
				
					return "";
				
				}
				public String MANAGER_IDOriginalDbColumnName(){
				
					return "MANAGER_ID";
				
				}

				
			    public Integer DEPARTMENT_ID;

				public Integer getDEPARTMENT_ID () {
					return this.DEPARTMENT_ID;
				}

				public Boolean DEPARTMENT_IDIsNullable(){
				    return true;
				}
				public Boolean DEPARTMENT_IDIsKey(){
				    return false;
				}
				public Integer DEPARTMENT_IDLength(){
				    return null;
				}
				public Integer DEPARTMENT_IDPrecision(){
				    return null;
				}
				public String DEPARTMENT_IDDefault(){
				
					return null;
				
				}
				public String DEPARTMENT_IDComment(){
				
				    return "";
				
				}
				public String DEPARTMENT_IDPattern(){
				
					return "";
				
				}
				public String DEPARTMENT_IDOriginalDbColumnName(){
				
					return "DEPARTMENT_ID";
				
				}

				


	private Integer readInteger(ObjectInputStream dis) throws IOException{
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
	
	private Integer readInteger(org.jboss.marshalling.Unmarshaller dis) throws IOException{
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

	private void writeInteger(Integer intNum, ObjectOutputStream dos) throws IOException{
		if(intNum == null) {
            dos.writeByte(-1);
		} else {
			dos.writeByte(0);
	    	dos.writeInt(intNum);
    	}
	}
	
	private void writeInteger(Integer intNum, org.jboss.marshalling.Marshaller marshaller) throws IOException{
		if(intNum == null) {
			marshaller.writeByte(-1);
		} else {
			marshaller.writeByte(0);
			marshaller.writeInt(intNum);
    	}
	}

	private String readString(ObjectInputStream dis) throws IOException{
		String strReturn = null;
		int length = 0;
        length = dis.readInt();
		if (length == -1) {
			strReturn = null;
		} else {
			if(length > commonByteArray_TALEND_file_to_db.length) {
				if(length < 1024 && commonByteArray_TALEND_file_to_db.length == 0) {
   					commonByteArray_TALEND_file_to_db = new byte[1024];
				} else {
   					commonByteArray_TALEND_file_to_db = new byte[2 * length];
   				}
			}
			dis.readFully(commonByteArray_TALEND_file_to_db, 0, length);
			strReturn = new String(commonByteArray_TALEND_file_to_db, 0, length, utf8Charset);
		}
		return strReturn;
	}
	
	private String readString(org.jboss.marshalling.Unmarshaller unmarshaller) throws IOException{
		String strReturn = null;
		int length = 0;
        length = unmarshaller.readInt();
		if (length == -1) {
			strReturn = null;
		} else {
			if(length > commonByteArray_TALEND_file_to_db.length) {
				if(length < 1024 && commonByteArray_TALEND_file_to_db.length == 0) {
   					commonByteArray_TALEND_file_to_db = new byte[1024];
				} else {
   					commonByteArray_TALEND_file_to_db = new byte[2 * length];
   				}
			}
			unmarshaller.readFully(commonByteArray_TALEND_file_to_db, 0, length);
			strReturn = new String(commonByteArray_TALEND_file_to_db, 0, length, utf8Charset);
		}
		return strReturn;
	}

    private void writeString(String str, ObjectOutputStream dos) throws IOException{
		if(str == null) {
            dos.writeInt(-1);
		} else {
            byte[] byteArray = str.getBytes(utf8Charset);
	    	dos.writeInt(byteArray.length);
			dos.write(byteArray);
    	}
    }
    
    private void writeString(String str, org.jboss.marshalling.Marshaller marshaller) throws IOException{
		if(str == null) {
			marshaller.writeInt(-1);
		} else {
            byte[] byteArray = str.getBytes(utf8Charset);
            marshaller.writeInt(byteArray.length);
            marshaller.write(byteArray);
    	}
    }

	private java.util.Date readDate(ObjectInputStream dis) throws IOException{
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
	
	private java.util.Date readDate(org.jboss.marshalling.Unmarshaller unmarshaller) throws IOException{
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

    private void writeDate(java.util.Date date1, ObjectOutputStream dos) throws IOException{
		if(date1 == null) {
            dos.writeByte(-1);
		} else {
			dos.writeByte(0);
	    	dos.writeLong(date1.getTime());
    	}
    }
    
    private void writeDate(java.util.Date date1, org.jboss.marshalling.Marshaller marshaller) throws IOException{
		if(date1 == null) {
			marshaller.writeByte(-1);
		} else {
			marshaller.writeByte(0);
			marshaller.writeLong(date1.getTime());
    	}
    }

    public void readData(ObjectInputStream dis) {

		synchronized(commonByteArrayLock_TALEND_file_to_db) {

        	try {

        		int length = 0;
		
						this.EMPLOYEE_ID = readInteger(dis);
					
					this.FIRST_NAME = readString(dis);
					
					this.LAST_NAME = readString(dis);
					
					this.EMAIL = readString(dis);
					
					this.PHONE_NUMBER = readString(dis);
					
					this.HIRE_DATE = readDate(dis);
					
					this.JOB_ID = readString(dis);
					
						this.SALARY = readInteger(dis);
					
					this.COMMISSION_PCT = readString(dis);
					
						this.MANAGER_ID = readInteger(dis);
					
						this.DEPARTMENT_ID = readInteger(dis);
					
        	} catch (IOException e) {
	            throw new RuntimeException(e);

		

        }

		

      }


    }
    
    public void readData(org.jboss.marshalling.Unmarshaller dis) {

		synchronized(commonByteArrayLock_TALEND_file_to_db) {

        	try {

        		int length = 0;
		
						this.EMPLOYEE_ID = readInteger(dis);
					
					this.FIRST_NAME = readString(dis);
					
					this.LAST_NAME = readString(dis);
					
					this.EMAIL = readString(dis);
					
					this.PHONE_NUMBER = readString(dis);
					
					this.HIRE_DATE = readDate(dis);
					
					this.JOB_ID = readString(dis);
					
						this.SALARY = readInteger(dis);
					
					this.COMMISSION_PCT = readString(dis);
					
						this.MANAGER_ID = readInteger(dis);
					
						this.DEPARTMENT_ID = readInteger(dis);
					
        	} catch (IOException e) {
	            throw new RuntimeException(e);

		

        }

		

      }


    }

    public void writeData(ObjectOutputStream dos) {
        try {

		
					// Integer
				
						writeInteger(this.EMPLOYEE_ID,dos);
					
					// String
				
						writeString(this.FIRST_NAME,dos);
					
					// String
				
						writeString(this.LAST_NAME,dos);
					
					// String
				
						writeString(this.EMAIL,dos);
					
					// String
				
						writeString(this.PHONE_NUMBER,dos);
					
					// java.util.Date
				
						writeDate(this.HIRE_DATE,dos);
					
					// String
				
						writeString(this.JOB_ID,dos);
					
					// Integer
				
						writeInteger(this.SALARY,dos);
					
					// String
				
						writeString(this.COMMISSION_PCT,dos);
					
					// Integer
				
						writeInteger(this.MANAGER_ID,dos);
					
					// Integer
				
						writeInteger(this.DEPARTMENT_ID,dos);
					
        	} catch (IOException e) {
	            throw new RuntimeException(e);
        }


    }
    
    public void writeData(org.jboss.marshalling.Marshaller dos) {
        try {

		
					// Integer
				
						writeInteger(this.EMPLOYEE_ID,dos);
					
					// String
				
						writeString(this.FIRST_NAME,dos);
					
					// String
				
						writeString(this.LAST_NAME,dos);
					
					// String
				
						writeString(this.EMAIL,dos);
					
					// String
				
						writeString(this.PHONE_NUMBER,dos);
					
					// java.util.Date
				
						writeDate(this.HIRE_DATE,dos);
					
					// String
				
						writeString(this.JOB_ID,dos);
					
					// Integer
				
						writeInteger(this.SALARY,dos);
					
					// String
				
						writeString(this.COMMISSION_PCT,dos);
					
					// Integer
				
						writeInteger(this.MANAGER_ID,dos);
					
					// Integer
				
						writeInteger(this.DEPARTMENT_ID,dos);
					
        	} catch (IOException e) {
	            throw new RuntimeException(e);
        }


    }


    public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append("[");
		sb.append("EMPLOYEE_ID="+String.valueOf(EMPLOYEE_ID));
		sb.append(",FIRST_NAME="+FIRST_NAME);
		sb.append(",LAST_NAME="+LAST_NAME);
		sb.append(",EMAIL="+EMAIL);
		sb.append(",PHONE_NUMBER="+PHONE_NUMBER);
		sb.append(",HIRE_DATE="+String.valueOf(HIRE_DATE));
		sb.append(",JOB_ID="+JOB_ID);
		sb.append(",SALARY="+String.valueOf(SALARY));
		sb.append(",COMMISSION_PCT="+COMMISSION_PCT);
		sb.append(",MANAGER_ID="+String.valueOf(MANAGER_ID));
		sb.append(",DEPARTMENT_ID="+String.valueOf(DEPARTMENT_ID));
	    sb.append("]");

	    return sb.toString();
    }
        public String toLogString(){
        	StringBuilder sb = new StringBuilder();
        	
        				if(EMPLOYEE_ID == null){
        					sb.append("<null>");
        				}else{
            				sb.append(EMPLOYEE_ID);
            			}
            		
        			sb.append("|");
        		
        				if(FIRST_NAME == null){
        					sb.append("<null>");
        				}else{
            				sb.append(FIRST_NAME);
            			}
            		
        			sb.append("|");
        		
        				if(LAST_NAME == null){
        					sb.append("<null>");
        				}else{
            				sb.append(LAST_NAME);
            			}
            		
        			sb.append("|");
        		
        				if(EMAIL == null){
        					sb.append("<null>");
        				}else{
            				sb.append(EMAIL);
            			}
            		
        			sb.append("|");
        		
        				if(PHONE_NUMBER == null){
        					sb.append("<null>");
        				}else{
            				sb.append(PHONE_NUMBER);
            			}
            		
        			sb.append("|");
        		
        				if(HIRE_DATE == null){
        					sb.append("<null>");
        				}else{
            				sb.append(HIRE_DATE);
            			}
            		
        			sb.append("|");
        		
        				if(JOB_ID == null){
        					sb.append("<null>");
        				}else{
            				sb.append(JOB_ID);
            			}
            		
        			sb.append("|");
        		
        				if(SALARY == null){
        					sb.append("<null>");
        				}else{
            				sb.append(SALARY);
            			}
            		
        			sb.append("|");
        		
        				if(COMMISSION_PCT == null){
        					sb.append("<null>");
        				}else{
            				sb.append(COMMISSION_PCT);
            			}
            		
        			sb.append("|");
        		
        				if(MANAGER_ID == null){
        					sb.append("<null>");
        				}else{
            				sb.append(MANAGER_ID);
            			}
            		
        			sb.append("|");
        		
        				if(DEPARTMENT_ID == null){
        					sb.append("<null>");
        				}else{
            				sb.append(DEPARTMENT_ID);
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

public void tFileInputDelimited_1Process(final java.util.Map<String, Object> globalMap) throws TalendException {
	globalMap.put("tFileInputDelimited_1_SUBPROCESS_STATE", 0);

	final boolean execStat = this.execStat;

		mdc("tFileInputDelimited_1", "Dg8RPq_");

	
		String iterateId = "";
	
	
	String currentComponent = "";
	s("none");
	String cLabel =  null;
	java.util.Map<String, Object> resourceMap = new java.util.HashMap<String, Object>();

	try {
			// TDI-39566 avoid throwing an useless Exception
			boolean resumeIt = true;
			if (globalResumeTicket == false && resumeEntryMethodName != null) {
				String currentMethodName = new java.lang.Exception().getStackTrace()[0].getMethodName();
				resumeIt = resumeEntryMethodName.equals(currentMethodName);
			}
			if (resumeIt || globalResumeTicket) { //start the resume
				globalResumeTicket = true;



		row1Struct row1 = new row1Struct();
row1Struct row2 = row1;





	
	/**
	 * [tDBOutput_1 begin ] start
	 */

	

	
		
		sh("tDBOutput_1");
		
	
	s(currentComponent="tDBOutput_1");
	
			
			
	
			runStat.updateStatAndLog(execStat,enableLogStash,resourceMap,iterateId,0,0,"row2");
			
		int tos_count_tDBOutput_1 = 0;
		
                if(log.isDebugEnabled())
            log.debug("tDBOutput_1 - "  + ("Start to work.") );
            if (log.isDebugEnabled()) {
                class BytesLimit65535_tDBOutput_1{
                    public void limitLog4jByte() throws Exception{
                    StringBuilder log4jParamters_tDBOutput_1 = new StringBuilder();
                    log4jParamters_tDBOutput_1.append("Parameters:");
                            log4jParamters_tDBOutput_1.append("DB_VERSION" + " = " + "MYSQL_8");
                        log4jParamters_tDBOutput_1.append(" | ");
                            log4jParamters_tDBOutput_1.append("USE_EXISTING_CONNECTION" + " = " + "false");
                        log4jParamters_tDBOutput_1.append(" | ");
                            log4jParamters_tDBOutput_1.append("HOST" + " = " + "\"127.0.0.1\"");
                        log4jParamters_tDBOutput_1.append(" | ");
                            log4jParamters_tDBOutput_1.append("PORT" + " = " + "\"3306\"");
                        log4jParamters_tDBOutput_1.append(" | ");
                            log4jParamters_tDBOutput_1.append("DBNAME" + " = " + "\"world\"");
                        log4jParamters_tDBOutput_1.append(" | ");
                            log4jParamters_tDBOutput_1.append("USER" + " = " + "\"root\"");
                        log4jParamters_tDBOutput_1.append(" | ");
                            log4jParamters_tDBOutput_1.append("PASS" + " = " + String.valueOf("enc:routine.encryption.key.v1:k/B/kP4U5s/eWJ0XLZ/C3o5/Y30VwwtCoyA6OBf60Jv9v+I=").substring(0, 4) + "...");     
                        log4jParamters_tDBOutput_1.append(" | ");
                            log4jParamters_tDBOutput_1.append("TABLE" + " = " + "\"emp\"");
                        log4jParamters_tDBOutput_1.append(" | ");
                            log4jParamters_tDBOutput_1.append("TABLE_ACTION" + " = " + "DROP_IF_EXISTS_AND_CREATE");
                        log4jParamters_tDBOutput_1.append(" | ");
                            log4jParamters_tDBOutput_1.append("DATA_ACTION" + " = " + "INSERT");
                        log4jParamters_tDBOutput_1.append(" | ");
                            log4jParamters_tDBOutput_1.append("SPECIFY_DATASOURCE_ALIAS" + " = " + "false");
                        log4jParamters_tDBOutput_1.append(" | ");
                            log4jParamters_tDBOutput_1.append("DIE_ON_ERROR" + " = " + "false");
                        log4jParamters_tDBOutput_1.append(" | ");
                            log4jParamters_tDBOutput_1.append("PROPERTIES" + " = " + "\"noDatetimeStringSync=true\"");
                        log4jParamters_tDBOutput_1.append(" | ");
                            log4jParamters_tDBOutput_1.append("USE_BATCH_SIZE" + " = " + "true");
                        log4jParamters_tDBOutput_1.append(" | ");
                            log4jParamters_tDBOutput_1.append("BATCH_SIZE" + " = " + "10000");
                        log4jParamters_tDBOutput_1.append(" | ");
                            log4jParamters_tDBOutput_1.append("COMMIT_EVERY" + " = " + "10000");
                        log4jParamters_tDBOutput_1.append(" | ");
                            log4jParamters_tDBOutput_1.append("ADD_COLS" + " = " + "[]");
                        log4jParamters_tDBOutput_1.append(" | ");
                            log4jParamters_tDBOutput_1.append("USE_FIELD_OPTIONS" + " = " + "false");
                        log4jParamters_tDBOutput_1.append(" | ");
                            log4jParamters_tDBOutput_1.append("USE_HINT_OPTIONS" + " = " + "false");
                        log4jParamters_tDBOutput_1.append(" | ");
                            log4jParamters_tDBOutput_1.append("ENABLE_DEBUG_MODE" + " = " + "false");
                        log4jParamters_tDBOutput_1.append(" | ");
                            log4jParamters_tDBOutput_1.append("ON_DUPLICATE_KEY_UPDATE" + " = " + "false");
                        log4jParamters_tDBOutput_1.append(" | ");
                            log4jParamters_tDBOutput_1.append("UNIFIED_COMPONENTS" + " = " + "tMysqlOutput");
                        log4jParamters_tDBOutput_1.append(" | ");
                if(log.isDebugEnabled())
            log.debug("tDBOutput_1 - "  + (log4jParamters_tDBOutput_1) );
                    } 
                } 
            new BytesLimit65535_tDBOutput_1().limitLog4jByte();
            }
			if(enableLogStash) {
				talendJobLog.addCM("tDBOutput_1", "tDBOutput_1", "tMysqlOutput");
				talendJobLogProcess(globalMap);
				s(currentComponent);
			}
			







int nb_line_tDBOutput_1 = 0;
int nb_line_update_tDBOutput_1 = 0;
int nb_line_inserted_tDBOutput_1 = 0;
int nb_line_deleted_tDBOutput_1 = 0;
int nb_line_rejected_tDBOutput_1 = 0;

int deletedCount_tDBOutput_1=0;
int updatedCount_tDBOutput_1=0;
int insertedCount_tDBOutput_1=0;
int rowsToCommitCount_tDBOutput_1=0;
int rejectedCount_tDBOutput_1=0;

String tableName_tDBOutput_1 = "emp";
boolean whetherReject_tDBOutput_1 = false;

java.util.Calendar calendar_tDBOutput_1 = java.util.Calendar.getInstance();
calendar_tDBOutput_1.set(1, 0, 1, 0, 0, 0);
long year1_tDBOutput_1 = calendar_tDBOutput_1.getTime().getTime();
calendar_tDBOutput_1.set(10000, 0, 1, 0, 0, 0);
long year10000_tDBOutput_1 = calendar_tDBOutput_1.getTime().getTime();
long date_tDBOutput_1;

java.sql.Connection conn_tDBOutput_1 = null;
		
			
        String properties_tDBOutput_1 = "noDatetimeStringSync=true";
        if (properties_tDBOutput_1 == null || properties_tDBOutput_1.trim().length() == 0) {
            properties_tDBOutput_1 = "rewriteBatchedStatements=true&allowLoadLocalInfile=true";
        }else {
            if (!properties_tDBOutput_1.contains("rewriteBatchedStatements=")) {
                properties_tDBOutput_1 += "&rewriteBatchedStatements=true";
            }

            if (!properties_tDBOutput_1.contains("allowLoadLocalInfile=")) {
                properties_tDBOutput_1 += "&allowLoadLocalInfile=true";
            }
        }

        String url_tDBOutput_1 = "jdbc:mysql://" + "127.0.0.1" + ":" + "3306" + "/" + "world" + "?" + properties_tDBOutput_1;

			String driverClass_tDBOutput_1 = "com.mysql.cj.jdbc.Driver";
			
                if(log.isDebugEnabled())
            log.debug("tDBOutput_1 - "  + ("Driver ClassName: ")  + (driverClass_tDBOutput_1)  + (".") );
			String dbUser_tDBOutput_1 = "root";
			

			 
	final String decryptedPassword_tDBOutput_1 = routines.system.PasswordEncryptUtil.decryptPassword("enc:routine.encryption.key.v1:OL8qhMb3J2w6G8zvjygjIOv7J/45Am1RswPxHUDwtRkiQf8=");

			String dbPwd_tDBOutput_1 = decryptedPassword_tDBOutput_1;
			java.lang.Class.forName(driverClass_tDBOutput_1);
			
                if(log.isDebugEnabled())
            log.debug("tDBOutput_1 - "  + ("Connection attempts to '")  + (url_tDBOutput_1)  + ("' with the username '")  + (dbUser_tDBOutput_1)  + ("'.") );
			conn_tDBOutput_1 = java.sql.DriverManager.getConnection(url_tDBOutput_1, dbUser_tDBOutput_1, dbPwd_tDBOutput_1);
			
                if(log.isDebugEnabled())
            log.debug("tDBOutput_1 - "  + ("Connection to '")  + (url_tDBOutput_1)  + ("' has succeeded.") );
			
	resourceMap.put("conn_tDBOutput_1", conn_tDBOutput_1);
	
			conn_tDBOutput_1.setAutoCommit(false);
			int commitEvery_tDBOutput_1 = 10000;
			int commitCounter_tDBOutput_1 = 0;
			
                if(log.isDebugEnabled())
            log.debug("tDBOutput_1 - "  + ("Connection is set auto commit to '")  + (conn_tDBOutput_1.getAutoCommit())  + ("'.") );

		int count_tDBOutput_1=0;
		
				
                                java.sql.DatabaseMetaData dbMetaData_tDBOutput_1 = conn_tDBOutput_1.getMetaData();
                                    java.sql.ResultSet rsTable_tDBOutput_1 = dbMetaData_tDBOutput_1.getTables("world", null, null, new String[]{"TABLE"});
                                boolean whetherExist_tDBOutput_1 = false;
                                while(rsTable_tDBOutput_1.next()) {
                                    String table_tDBOutput_1 = rsTable_tDBOutput_1.getString("TABLE_NAME");
                                    if(table_tDBOutput_1.equalsIgnoreCase("emp")) {
                                        whetherExist_tDBOutput_1 = true;
                                        break;
                                    }
                                }
                                if(whetherExist_tDBOutput_1) {
                                    try (java.sql.Statement stmtDrop_tDBOutput_1 = conn_tDBOutput_1.createStatement()) {
                if(log.isDebugEnabled())
            log.debug("tDBOutput_1 - "  + ("Dropping")  + (" table '")  + (tableName_tDBOutput_1)  + ("'.") );
                                        stmtDrop_tDBOutput_1.execute("DROP TABLE `" + tableName_tDBOutput_1 + "`" );
                if(log.isDebugEnabled())
            log.debug("tDBOutput_1 - "  + ("Drop")  + (" table '")  + (tableName_tDBOutput_1)  + ("' has succeeded.") );
                                    }
                                }
                                try(java.sql.Statement stmtCreate_tDBOutput_1 = conn_tDBOutput_1.createStatement()) {
                if(log.isDebugEnabled())
            log.debug("tDBOutput_1 - "  + ("Creating")  + (" table '")  + (tableName_tDBOutput_1)  + ("'.") );
                                    stmtCreate_tDBOutput_1.execute("CREATE TABLE `" + tableName_tDBOutput_1 + "`(`EMPLOYEE_ID` INT(0)  ,`FIRST_NAME` VARCHAR(20)  ,`LAST_NAME` VARCHAR(20)  ,`EMAIL` VARCHAR(20)  ,`PHONE_NUMBER` VARCHAR(20)  ,`HIRE_DATE` DATETIME ,`JOB_ID` VARCHAR(20)  ,`SALARY` INT(0)  ,`COMMISSION_PCT` VARCHAR(20)  ,`MANAGER_ID` INT(0)  ,`DEPARTMENT_ID` INT(0)  )");
                if(log.isDebugEnabled())
            log.debug("tDBOutput_1 - "  + ("Create")  + (" table '")  + (tableName_tDBOutput_1)  + ("' has succeeded.") );
                                }
			

		
						String insert_tDBOutput_1 = "INSERT INTO `" + "emp" + "` (`EMPLOYEE_ID`,`FIRST_NAME`,`LAST_NAME`,`EMAIL`,`PHONE_NUMBER`,`HIRE_DATE`,`JOB_ID`,`SALARY`,`COMMISSION_PCT`,`MANAGER_ID`,`DEPARTMENT_ID`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
		
						int batchSize_tDBOutput_1 = 10000;
						int batchSizeCounter_tDBOutput_1=0;
						    
						java.sql.PreparedStatement pstmt_tDBOutput_1 = conn_tDBOutput_1.prepareStatement(insert_tDBOutput_1);
						resourceMap.put("pstmt_tDBOutput_1", pstmt_tDBOutput_1);


 



		

/**
 * [tDBOutput_1 begin ] stop
 */




	
	/**
	 * [tLogRow_1 begin ] start
	 */

	

	
		
		sh("tLogRow_1");
		
	
	s(currentComponent="tLogRow_1");
	
			
			
	
			runStat.updateStatAndLog(execStat,enableLogStash,resourceMap,iterateId,0,0,"row1");
			
		int tos_count_tLogRow_1 = 0;
		
                if(log.isDebugEnabled())
            log.debug("tLogRow_1 - "  + ("Start to work.") );
            if (log.isDebugEnabled()) {
                class BytesLimit65535_tLogRow_1{
                    public void limitLog4jByte() throws Exception{
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
                if(log.isDebugEnabled())
            log.debug("tLogRow_1 - "  + (log4jParamters_tLogRow_1) );
                    } 
                } 
            new BytesLimit65535_tLogRow_1().limitLog4jByte();
            }
			if(enableLogStash) {
				talendJobLog.addCM("tLogRow_1", "tLogRow_1", "tLogRow");
				talendJobLogProcess(globalMap);
				s(currentComponent);
			}
			

	///////////////////////
	
         class Util_tLogRow_1 {

        String[] des_top = { ".", ".", "-", "+" };

        String[] des_head = { "|=", "=|", "-", "+" };

        String[] des_bottom = { "'", "'", "-", "+" };

        String name="";

        java.util.List<String[]> list = new java.util.ArrayList<String[]>();

        int[] colLengths = new int[11];

        public void addRow(String[] row) {

            for (int i = 0; i < 11; i++) {
                if (row[i]!=null) {
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
       
                        formatter.format(sbformat.toString(), (Object[])row);	
                                
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
                //first column
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
                
                    //last column
                    for (int i = 0; i < colLengths[10] - fillChars[1].length() + 1; i++) {
                        sb.append(fillChars[2]);
                    }         
                sb.append(fillChars[1]);
                sb.append("\n");               
                return sb;
            }
            
            public boolean isTableEmpty(){
            	if (list.size() > 1)
            		return false;
            	return true;
            }
        }
        Util_tLogRow_1 util_tLogRow_1 = new Util_tLogRow_1();
        util_tLogRow_1.setTableName("tLogRow_1");
        util_tLogRow_1.addRow(new String[]{"EMPLOYEE_ID","FIRST_NAME","LAST_NAME","EMAIL","PHONE_NUMBER","HIRE_DATE","JOB_ID","SALARY","COMMISSION_PCT","MANAGER_ID","DEPARTMENT_ID",});        
 		StringBuilder strBuffer_tLogRow_1 = null;
		int nb_line_tLogRow_1 = 0;
///////////////////////    			



 



		

/**
 * [tLogRow_1 begin ] stop
 */




	
	/**
	 * [tFileInputDelimited_1 begin ] start
	 */

	

	
		
		sh("tFileInputDelimited_1");
		
	
	s(currentComponent="tFileInputDelimited_1");
	
			
			
	
		int tos_count_tFileInputDelimited_1 = 0;
		
                if(log.isDebugEnabled())
            log.debug("tFileInputDelimited_1 - "  + ("Start to work.") );
            if (log.isDebugEnabled()) {
                class BytesLimit65535_tFileInputDelimited_1{
                    public void limitLog4jByte() throws Exception{
                    StringBuilder log4jParamters_tFileInputDelimited_1 = new StringBuilder();
                    log4jParamters_tFileInputDelimited_1.append("Parameters:");
                            log4jParamters_tFileInputDelimited_1.append("USE_EXISTING_DYNAMIC" + " = " + "false");
                        log4jParamters_tFileInputDelimited_1.append(" | ");
                            log4jParamters_tFileInputDelimited_1.append("FILENAME" + " = " + "\"C:/Users/ramayanam/Desktop/Talend/inbound/emp.txt\"");
                        log4jParamters_tFileInputDelimited_1.append(" | ");
                            log4jParamters_tFileInputDelimited_1.append("CSV_OPTION" + " = " + "false");
                        log4jParamters_tFileInputDelimited_1.append(" | ");
                            log4jParamters_tFileInputDelimited_1.append("ROWSEPARATOR" + " = " + "\"\\n\"");
                        log4jParamters_tFileInputDelimited_1.append(" | ");
                            log4jParamters_tFileInputDelimited_1.append("FIELDSEPARATOR" + " = " + "\",\"");
                        log4jParamters_tFileInputDelimited_1.append(" | ");
                            log4jParamters_tFileInputDelimited_1.append("HEADER" + " = " + "1");
                        log4jParamters_tFileInputDelimited_1.append(" | ");
                            log4jParamters_tFileInputDelimited_1.append("FOOTER" + " = " + "0");
                        log4jParamters_tFileInputDelimited_1.append(" | ");
                            log4jParamters_tFileInputDelimited_1.append("LIMIT" + " = " + "");
                        log4jParamters_tFileInputDelimited_1.append(" | ");
                            log4jParamters_tFileInputDelimited_1.append("REMOVE_EMPTY_ROW" + " = " + "true");
                        log4jParamters_tFileInputDelimited_1.append(" | ");
                            log4jParamters_tFileInputDelimited_1.append("UNCOMPRESS" + " = " + "false");
                        log4jParamters_tFileInputDelimited_1.append(" | ");
                            log4jParamters_tFileInputDelimited_1.append("DIE_ON_ERROR" + " = " + "false");
                        log4jParamters_tFileInputDelimited_1.append(" | ");
                            log4jParamters_tFileInputDelimited_1.append("ADVANCED_SEPARATOR" + " = " + "false");
                        log4jParamters_tFileInputDelimited_1.append(" | ");
                            log4jParamters_tFileInputDelimited_1.append("RANDOM" + " = " + "false");
                        log4jParamters_tFileInputDelimited_1.append(" | ");
                            log4jParamters_tFileInputDelimited_1.append("TRIMALL" + " = " + "false");
                        log4jParamters_tFileInputDelimited_1.append(" | ");
                            log4jParamters_tFileInputDelimited_1.append("TRIMSELECT" + " = " + "[{TRIM="+("false")+", SCHEMA_COLUMN="+("EMPLOYEE_ID")+"}, {TRIM="+("false")+", SCHEMA_COLUMN="+("FIRST_NAME")+"}, {TRIM="+("false")+", SCHEMA_COLUMN="+("LAST_NAME")+"}, {TRIM="+("false")+", SCHEMA_COLUMN="+("EMAIL")+"}, {TRIM="+("false")+", SCHEMA_COLUMN="+("PHONE_NUMBER")+"}, {TRIM="+("false")+", SCHEMA_COLUMN="+("HIRE_DATE")+"}, {TRIM="+("false")+", SCHEMA_COLUMN="+("JOB_ID")+"}, {TRIM="+("false")+", SCHEMA_COLUMN="+("SALARY")+"}, {TRIM="+("false")+", SCHEMA_COLUMN="+("COMMISSION_PCT")+"}, {TRIM="+("false")+", SCHEMA_COLUMN="+("MANAGER_ID")+"}, {TRIM="+("false")+", SCHEMA_COLUMN="+("DEPARTMENT_ID")+"}]");
                        log4jParamters_tFileInputDelimited_1.append(" | ");
                            log4jParamters_tFileInputDelimited_1.append("CHECK_FIELDS_NUM" + " = " + "false");
                        log4jParamters_tFileInputDelimited_1.append(" | ");
                            log4jParamters_tFileInputDelimited_1.append("CHECK_DATE" + " = " + "false");
                        log4jParamters_tFileInputDelimited_1.append(" | ");
                            log4jParamters_tFileInputDelimited_1.append("ENCODING" + " = " + "\"ISO-8859-15\"");
                        log4jParamters_tFileInputDelimited_1.append(" | ");
                            log4jParamters_tFileInputDelimited_1.append("SPLITRECORD" + " = " + "false");
                        log4jParamters_tFileInputDelimited_1.append(" | ");
                            log4jParamters_tFileInputDelimited_1.append("ENABLE_DECODE" + " = " + "false");
                        log4jParamters_tFileInputDelimited_1.append(" | ");
                            log4jParamters_tFileInputDelimited_1.append("USE_HEADER_AS_IS" + " = " + "false");
                        log4jParamters_tFileInputDelimited_1.append(" | ");
                if(log.isDebugEnabled())
            log.debug("tFileInputDelimited_1 - "  + (log4jParamters_tFileInputDelimited_1) );
                    } 
                } 
            new BytesLimit65535_tFileInputDelimited_1().limitLog4jByte();
            }
			if(enableLogStash) {
				talendJobLog.addCM("tFileInputDelimited_1", "tFileInputDelimited_1", "tFileInputDelimited");
				talendJobLogProcess(globalMap);
				s(currentComponent);
			}
			
	
	
	
 
	
	
	final routines.system.RowState rowstate_tFileInputDelimited_1 = new routines.system.RowState();
	
	
				int nb_line_tFileInputDelimited_1 = 0;
				org.talend.fileprocess.FileInputDelimited fid_tFileInputDelimited_1 = null;
				int limit_tFileInputDelimited_1 = -1;
				try{
					
						Object filename_tFileInputDelimited_1 = "C:/Users/ramayanam/Desktop/Talend/inbound/emp.txt";
						if(filename_tFileInputDelimited_1 instanceof java.io.InputStream){
							
			int footer_value_tFileInputDelimited_1 = 0, random_value_tFileInputDelimited_1 = -1;
			if(footer_value_tFileInputDelimited_1 >0 || random_value_tFileInputDelimited_1 > 0){
				throw new java.lang.Exception("When the input source is a stream,footer and random shouldn't be bigger than 0.");				
			}
		
						}
						try {
							fid_tFileInputDelimited_1 = new org.talend.fileprocess.FileInputDelimited("C:/Users/ramayanam/Desktop/Talend/inbound/emp.txt", "ISO-8859-15",",","\n",true,1,0,
									limit_tFileInputDelimited_1
								,-1, false);
						} catch(java.lang.Exception e) {
globalMap.put("tFileInputDelimited_1_ERROR_MESSAGE",e.getMessage());
							
								
									log.error("tFileInputDelimited_1 - " +e.getMessage());
								
								System.err.println(e.getMessage());
							
						}
					
				    
				    	log.info("tFileInputDelimited_1 - Retrieving records from the datasource.");
				    
					while (fid_tFileInputDelimited_1!=null && fid_tFileInputDelimited_1.nextRecord()) {
						rowstate_tFileInputDelimited_1.reset();
						
			    						row1 = null;			
												
									boolean whetherReject_tFileInputDelimited_1 = false;
									row1 = new row1Struct();
									try {
										
				int columnIndexWithD_tFileInputDelimited_1 = 0;
				
					String temp = ""; 
				
					columnIndexWithD_tFileInputDelimited_1 = 0;
					
						temp = fid_tFileInputDelimited_1.get(columnIndexWithD_tFileInputDelimited_1);
						if(temp.length() > 0) {
							
								try {
								
    								row1.EMPLOYEE_ID = ParserUtils.parseTo_Integer(temp);
    							
    							} catch(java.lang.Exception ex_tFileInputDelimited_1) {
globalMap.put("tFileInputDelimited_1_ERROR_MESSAGE",ex_tFileInputDelimited_1.getMessage());
									rowstate_tFileInputDelimited_1.setException(new RuntimeException(String.format("Couldn't parse value for column '%s' in '%s', value is '%s'. Details: %s",
										"EMPLOYEE_ID", "row1", temp, ex_tFileInputDelimited_1), ex_tFileInputDelimited_1));
								}
    							
						} else {						
							
								
									row1.EMPLOYEE_ID = null;
								
							
						}
					
				
					columnIndexWithD_tFileInputDelimited_1 = 1;
					
							row1.FIRST_NAME = fid_tFileInputDelimited_1.get(columnIndexWithD_tFileInputDelimited_1);
						
				
					columnIndexWithD_tFileInputDelimited_1 = 2;
					
							row1.LAST_NAME = fid_tFileInputDelimited_1.get(columnIndexWithD_tFileInputDelimited_1);
						
				
					columnIndexWithD_tFileInputDelimited_1 = 3;
					
							row1.EMAIL = fid_tFileInputDelimited_1.get(columnIndexWithD_tFileInputDelimited_1);
						
				
					columnIndexWithD_tFileInputDelimited_1 = 4;
					
							row1.PHONE_NUMBER = fid_tFileInputDelimited_1.get(columnIndexWithD_tFileInputDelimited_1);
						
				
					columnIndexWithD_tFileInputDelimited_1 = 5;
					
						temp = fid_tFileInputDelimited_1.get(columnIndexWithD_tFileInputDelimited_1);
						if(temp.length() > 0) {
							
								try {
								
    									row1.HIRE_DATE = ParserUtils.parseTo_Date(temp, "dd-MM-yyyy");
    								
    							} catch(java.lang.Exception ex_tFileInputDelimited_1) {
globalMap.put("tFileInputDelimited_1_ERROR_MESSAGE",ex_tFileInputDelimited_1.getMessage());
									rowstate_tFileInputDelimited_1.setException(new RuntimeException(String.format("Couldn't parse value for column '%s' in '%s', value is '%s'. Details: %s",
										"HIRE_DATE", "row1", temp, ex_tFileInputDelimited_1), ex_tFileInputDelimited_1));
								}
    							
						} else {						
							
								
									row1.HIRE_DATE = null;
								
							
						}
					
				
					columnIndexWithD_tFileInputDelimited_1 = 6;
					
							row1.JOB_ID = fid_tFileInputDelimited_1.get(columnIndexWithD_tFileInputDelimited_1);
						
				
					columnIndexWithD_tFileInputDelimited_1 = 7;
					
						temp = fid_tFileInputDelimited_1.get(columnIndexWithD_tFileInputDelimited_1);
						if(temp.length() > 0) {
							
								try {
								
    								row1.SALARY = ParserUtils.parseTo_Integer(temp);
    							
    							} catch(java.lang.Exception ex_tFileInputDelimited_1) {
globalMap.put("tFileInputDelimited_1_ERROR_MESSAGE",ex_tFileInputDelimited_1.getMessage());
									rowstate_tFileInputDelimited_1.setException(new RuntimeException(String.format("Couldn't parse value for column '%s' in '%s', value is '%s'. Details: %s",
										"SALARY", "row1", temp, ex_tFileInputDelimited_1), ex_tFileInputDelimited_1));
								}
    							
						} else {						
							
								
									row1.SALARY = null;
								
							
						}
					
				
					columnIndexWithD_tFileInputDelimited_1 = 8;
					
							row1.COMMISSION_PCT = fid_tFileInputDelimited_1.get(columnIndexWithD_tFileInputDelimited_1);
						
				
					columnIndexWithD_tFileInputDelimited_1 = 9;
					
						temp = fid_tFileInputDelimited_1.get(columnIndexWithD_tFileInputDelimited_1);
						if(temp.length() > 0) {
							
								try {
								
    								row1.MANAGER_ID = ParserUtils.parseTo_Integer(temp);
    							
    							} catch(java.lang.Exception ex_tFileInputDelimited_1) {
globalMap.put("tFileInputDelimited_1_ERROR_MESSAGE",ex_tFileInputDelimited_1.getMessage());
									rowstate_tFileInputDelimited_1.setException(new RuntimeException(String.format("Couldn't parse value for column '%s' in '%s', value is '%s'. Details: %s",
										"MANAGER_ID", "row1", temp, ex_tFileInputDelimited_1), ex_tFileInputDelimited_1));
								}
    							
						} else {						
							
								
									row1.MANAGER_ID = null;
								
							
						}
					
				
					columnIndexWithD_tFileInputDelimited_1 = 10;
					
						temp = fid_tFileInputDelimited_1.get(columnIndexWithD_tFileInputDelimited_1);
						if(temp.length() > 0) {
							
								try {
								
    								row1.DEPARTMENT_ID = ParserUtils.parseTo_Integer(temp);
    							
    							} catch(java.lang.Exception ex_tFileInputDelimited_1) {
globalMap.put("tFileInputDelimited_1_ERROR_MESSAGE",ex_tFileInputDelimited_1.getMessage());
									rowstate_tFileInputDelimited_1.setException(new RuntimeException(String.format("Couldn't parse value for column '%s' in '%s', value is '%s'. Details: %s",
										"DEPARTMENT_ID", "row1", temp, ex_tFileInputDelimited_1), ex_tFileInputDelimited_1));
								}
    							
						} else {						
							
								
									row1.DEPARTMENT_ID = null;
								
							
						}
					
				
				
										
										if(rowstate_tFileInputDelimited_1.getException()!=null) {
											throw rowstate_tFileInputDelimited_1.getException();
										}
										
										
							
			    					} catch (java.lang.Exception e) {
globalMap.put("tFileInputDelimited_1_ERROR_MESSAGE",e.getMessage());
			        					whetherReject_tFileInputDelimited_1 = true;
			        					
												log.error("tFileInputDelimited_1 - " +e.getMessage());
											
			                					System.err.println(e.getMessage());
			                					row1 = null;
			                				
										
			    					}
								
			log.debug("tFileInputDelimited_1 - Retrieving the record " + fid_tFileInputDelimited_1.getRowNumber() + ".");
		

 



		

/**
 * [tFileInputDelimited_1 begin ] stop
 */

	
	/**
	 * [tFileInputDelimited_1 main ] start
	 */

	

	
	
	s(currentComponent="tFileInputDelimited_1");
	
			
			
	

 


	tos_count_tFileInputDelimited_1++;

		

/**
 * [tFileInputDelimited_1 main ] stop
 */

	
	/**
	 * [tFileInputDelimited_1 process_data_begin ] start
	 */

	

	
	
	s(currentComponent="tFileInputDelimited_1");
	
			
			
	

 



		

/**
 * [tFileInputDelimited_1 process_data_begin ] stop
 */

// Start of branch "row1"
if(row1 != null) { 



	
	/**
	 * [tLogRow_1 main ] start
	 */

	

	
	
	s(currentComponent="tLogRow_1");
	
			
			
	
			if(runStat.update(execStat,enableLogStash,iterateId,1,1
				
					,"row1","tFileInputDelimited_1","tFileInputDelimited_1","tFileInputDelimited","tLogRow_1","tLogRow_1","tLogRow"
				
			)) {
				talendJobLogProcess(globalMap);
			}
			
    			if(log.isTraceEnabled()){
    				log.trace("row1 - " + (row1==null? "": row1.toLogString()));
    			}
    		
///////////////////////		
						

				
				String[] row_tLogRow_1 = new String[11];
   				
	    		if(row1.EMPLOYEE_ID != null) { //              
                 row_tLogRow_1[0]=    						    
				                String.valueOf(row1.EMPLOYEE_ID)			
					          ;	
							
	    		} //			
    			   				
	    		if(row1.FIRST_NAME != null) { //              
                 row_tLogRow_1[1]=    						    
				                String.valueOf(row1.FIRST_NAME)			
					          ;	
							
	    		} //			
    			   				
	    		if(row1.LAST_NAME != null) { //              
                 row_tLogRow_1[2]=    						    
				                String.valueOf(row1.LAST_NAME)			
					          ;	
							
	    		} //			
    			   				
	    		if(row1.EMAIL != null) { //              
                 row_tLogRow_1[3]=    						    
				                String.valueOf(row1.EMAIL)			
					          ;	
							
	    		} //			
    			   				
	    		if(row1.PHONE_NUMBER != null) { //              
                 row_tLogRow_1[4]=    						    
				                String.valueOf(row1.PHONE_NUMBER)			
					          ;	
							
	    		} //			
    			   				
	    		if(row1.HIRE_DATE != null) { //              
                 row_tLogRow_1[5]=    						
								FormatterUtils.format_Date(row1.HIRE_DATE, "dd-MM-yyyy")
					          ;	
							
	    		} //			
    			   				
	    		if(row1.JOB_ID != null) { //              
                 row_tLogRow_1[6]=    						    
				                String.valueOf(row1.JOB_ID)			
					          ;	
							
	    		} //			
    			   				
	    		if(row1.SALARY != null) { //              
                 row_tLogRow_1[7]=    						    
				                String.valueOf(row1.SALARY)			
					          ;	
							
	    		} //			
    			   				
	    		if(row1.COMMISSION_PCT != null) { //              
                 row_tLogRow_1[8]=    						    
				                String.valueOf(row1.COMMISSION_PCT)			
					          ;	
							
	    		} //			
    			   				
	    		if(row1.MANAGER_ID != null) { //              
                 row_tLogRow_1[9]=    						    
				                String.valueOf(row1.MANAGER_ID)			
					          ;	
							
	    		} //			
    			   				
	    		if(row1.DEPARTMENT_ID != null) { //              
                 row_tLogRow_1[10]=    						    
				                String.valueOf(row1.DEPARTMENT_ID)			
					          ;	
							
	    		} //			
    			 

				util_tLogRow_1.addRow(row_tLogRow_1);	
				nb_line_tLogRow_1++;
                	log.info("tLogRow_1 - Content of row "+nb_line_tLogRow_1+": " + TalendString.unionString("|",row_tLogRow_1));
//////

//////                    
                    
///////////////////////    			

 
     row2 = row1;


	tos_count_tLogRow_1++;

		

/**
 * [tLogRow_1 main ] stop
 */

	
	/**
	 * [tLogRow_1 process_data_begin ] start
	 */

	

	
	
	s(currentComponent="tLogRow_1");
	
			
			
	

 



		

/**
 * [tLogRow_1 process_data_begin ] stop
 */


	
	/**
	 * [tDBOutput_1 main ] start
	 */

	

	
	
	s(currentComponent="tDBOutput_1");
	
			
			
	
			if(runStat.update(execStat,enableLogStash,iterateId,1,1
				
					,"row2","tLogRow_1","tLogRow_1","tLogRow","tDBOutput_1","tDBOutput_1","tMysqlOutput"
				
			)) {
				talendJobLogProcess(globalMap);
			}
			
    			if(log.isTraceEnabled()){
    				log.trace("row2 - " + (row2==null? "": row2.toLogString()));
    			}
    		



        whetherReject_tDBOutput_1 = false;
                            if(row2.EMPLOYEE_ID == null) {
pstmt_tDBOutput_1.setNull(1, java.sql.Types.INTEGER);
} else {pstmt_tDBOutput_1.setInt(1, row2.EMPLOYEE_ID);
}

                            if(row2.FIRST_NAME == null) {
pstmt_tDBOutput_1.setNull(2, java.sql.Types.VARCHAR);
} else {pstmt_tDBOutput_1.setString(2, row2.FIRST_NAME);
}

                            if(row2.LAST_NAME == null) {
pstmt_tDBOutput_1.setNull(3, java.sql.Types.VARCHAR);
} else {pstmt_tDBOutput_1.setString(3, row2.LAST_NAME);
}

                            if(row2.EMAIL == null) {
pstmt_tDBOutput_1.setNull(4, java.sql.Types.VARCHAR);
} else {pstmt_tDBOutput_1.setString(4, row2.EMAIL);
}

                            if(row2.PHONE_NUMBER == null) {
pstmt_tDBOutput_1.setNull(5, java.sql.Types.VARCHAR);
} else {pstmt_tDBOutput_1.setString(5, row2.PHONE_NUMBER);
}

                            if(row2.HIRE_DATE != null) {
date_tDBOutput_1 = row2.HIRE_DATE.getTime();
if(date_tDBOutput_1 < year1_tDBOutput_1 || date_tDBOutput_1 >= year10000_tDBOutput_1) {
pstmt_tDBOutput_1.setString(6, "0000-00-00 00:00:00");
} else {pstmt_tDBOutput_1.setTimestamp(6, new java.sql.Timestamp(date_tDBOutput_1));
}
} else {
pstmt_tDBOutput_1.setNull(6, java.sql.Types.DATE);
}

                            if(row2.JOB_ID == null) {
pstmt_tDBOutput_1.setNull(7, java.sql.Types.VARCHAR);
} else {pstmt_tDBOutput_1.setString(7, row2.JOB_ID);
}

                            if(row2.SALARY == null) {
pstmt_tDBOutput_1.setNull(8, java.sql.Types.INTEGER);
} else {pstmt_tDBOutput_1.setInt(8, row2.SALARY);
}

                            if(row2.COMMISSION_PCT == null) {
pstmt_tDBOutput_1.setNull(9, java.sql.Types.VARCHAR);
} else {pstmt_tDBOutput_1.setString(9, row2.COMMISSION_PCT);
}

                            if(row2.MANAGER_ID == null) {
pstmt_tDBOutput_1.setNull(10, java.sql.Types.INTEGER);
} else {pstmt_tDBOutput_1.setInt(10, row2.MANAGER_ID);
}

                            if(row2.DEPARTMENT_ID == null) {
pstmt_tDBOutput_1.setNull(11, java.sql.Types.INTEGER);
} else {pstmt_tDBOutput_1.setInt(11, row2.DEPARTMENT_ID);
}

                    pstmt_tDBOutput_1.addBatch();
                    nb_line_tDBOutput_1++;

                if(log.isDebugEnabled())
            log.debug("tDBOutput_1 - "  + ("Adding the record ")  + (nb_line_tDBOutput_1)  + (" to the ")  + ("INSERT")  + (" batch.") );
                      batchSizeCounter_tDBOutput_1++;
                if ( batchSize_tDBOutput_1 <= batchSizeCounter_tDBOutput_1) {
                try {
                        int countSum_tDBOutput_1 = 0;
                if(log.isDebugEnabled())
            log.debug("tDBOutput_1 - "  + ("Executing the ")  + ("INSERT")  + (" batch.") );
                        for(int countEach_tDBOutput_1: pstmt_tDBOutput_1.executeBatch()) {
                            countSum_tDBOutput_1 += (countEach_tDBOutput_1 == java.sql.Statement.EXECUTE_FAILED ? 0 : 1);
                        }
                        rowsToCommitCount_tDBOutput_1 += countSum_tDBOutput_1;
                if(log.isDebugEnabled())
            log.debug("tDBOutput_1 - "  + ("The ")  + ("INSERT")  + (" batch execution has succeeded.") );
                        insertedCount_tDBOutput_1 += countSum_tDBOutput_1;
                }catch (java.sql.BatchUpdateException e){
                    globalMap.put("tDBOutput_1_ERROR_MESSAGE",e.getMessage());
                    int countSum_tDBOutput_1 = 0;
                    for(int countEach_tDBOutput_1: e.getUpdateCounts()) {
                        countSum_tDBOutput_1 += (countEach_tDBOutput_1 < 0 ? 0 : countEach_tDBOutput_1);
                    }
                    rowsToCommitCount_tDBOutput_1 += countSum_tDBOutput_1;
                    insertedCount_tDBOutput_1 += countSum_tDBOutput_1;
                    System.err.println(e.getMessage());
            log.error("tDBOutput_1 - "  + (e.getMessage()) );
                }

                batchSizeCounter_tDBOutput_1 = 0;
            }
                commitCounter_tDBOutput_1++;

                if(commitEvery_tDBOutput_1 <= commitCounter_tDBOutput_1) {

                        try {
                                int countSum_tDBOutput_1 = 0;
                if(log.isDebugEnabled())
            log.debug("tDBOutput_1 - "  + ("Executing the ")  + ("INSERT")  + (" batch.") );
                                for(int countEach_tDBOutput_1: pstmt_tDBOutput_1.executeBatch()) {
                                    countSum_tDBOutput_1 += (countEach_tDBOutput_1 < 0 ? 0 : 1);
                                }
                                rowsToCommitCount_tDBOutput_1 += countSum_tDBOutput_1;
                if(log.isDebugEnabled())
            log.debug("tDBOutput_1 - "  + ("The ")  + ("INSERT")  + (" batch execution has succeeded.") );
                                insertedCount_tDBOutput_1 += countSum_tDBOutput_1;
                        }catch (java.sql.BatchUpdateException e){
                            globalMap.put("tDBOutput_1_ERROR_MESSAGE",e.getMessage());
                            int countSum_tDBOutput_1 = 0;
                            for(int countEach_tDBOutput_1: e.getUpdateCounts()) {
                                countSum_tDBOutput_1 += (countEach_tDBOutput_1 < 0 ? 0 : countEach_tDBOutput_1);
                            }
                            rowsToCommitCount_tDBOutput_1 += countSum_tDBOutput_1;
                            insertedCount_tDBOutput_1 += countSum_tDBOutput_1;
                            System.err.println(e.getMessage());
            log.error("tDBOutput_1 - "  + (e.getMessage()) );

                        }
                    if(rowsToCommitCount_tDBOutput_1 != 0){
                if(log.isDebugEnabled())
            log.debug("tDBOutput_1 - "  + ("Connection starting to commit ")  + (rowsToCommitCount_tDBOutput_1)  + (" record(s).") );
                    }
                    conn_tDBOutput_1.commit();
                    if(rowsToCommitCount_tDBOutput_1 != 0){
                if(log.isDebugEnabled())
            log.debug("tDBOutput_1 - "  + ("Connection commit has succeeded.") );
                        rowsToCommitCount_tDBOutput_1 = 0;
                    }
                    commitCounter_tDBOutput_1=0;
                }

 


	tos_count_tDBOutput_1++;

		

/**
 * [tDBOutput_1 main ] stop
 */

	
	/**
	 * [tDBOutput_1 process_data_begin ] start
	 */

	

	
	
	s(currentComponent="tDBOutput_1");
	
			
			
	

 



		

/**
 * [tDBOutput_1 process_data_begin ] stop
 */

	
	/**
	 * [tDBOutput_1 process_data_end ] start
	 */

	

	
	
	s(currentComponent="tDBOutput_1");
	
			
			
	

 



		

/**
 * [tDBOutput_1 process_data_end ] stop
 */




	
	/**
	 * [tLogRow_1 process_data_end ] start
	 */

	

	
	
	s(currentComponent="tLogRow_1");
	
			
			
	

 



		

/**
 * [tLogRow_1 process_data_end ] stop
 */


} // End of branch "row1"




	
	/**
	 * [tFileInputDelimited_1 process_data_end ] start
	 */

	

	
	
	s(currentComponent="tFileInputDelimited_1");
	
			
			
	

 



		

/**
 * [tFileInputDelimited_1 process_data_end ] stop
 */

	
	/**
	 * [tFileInputDelimited_1 end ] start
	 */

	

	
	
	s(currentComponent="tFileInputDelimited_1");
	
			
			
	



            }
            }finally{
                if(!((Object)("C:/Users/ramayanam/Desktop/Talend/inbound/emp.txt") instanceof java.io.InputStream)){
                	if(fid_tFileInputDelimited_1!=null){
                		fid_tFileInputDelimited_1.close();
                	}
                }
                if(fid_tFileInputDelimited_1!=null){
                	globalMap.put("tFileInputDelimited_1_NB_LINE", fid_tFileInputDelimited_1.getRowNumber());
					
						log.info("tFileInputDelimited_1 - Retrieved records count: "+ fid_tFileInputDelimited_1.getRowNumber() + ".");
					
                }
			}
			  

 
                if(log.isDebugEnabled())
            log.debug("tFileInputDelimited_1 - "  + ("Done.") );

ok_Hash.put("tFileInputDelimited_1", true);
end_Hash.put("tFileInputDelimited_1", System.currentTimeMillis());




		

/**
 * [tFileInputDelimited_1 end ] stop
 */


	
	/**
	 * [tLogRow_1 end ] start
	 */

	

	
	
	s(currentComponent="tLogRow_1");
	
			
			
	


//////

                    
                    java.io.PrintStream consoleOut_tLogRow_1 = null;
                    if (globalMap.get("tLogRow_CONSOLE")!=null)
                    {
                    	consoleOut_tLogRow_1 = (java.io.PrintStream) globalMap.get("tLogRow_CONSOLE");
                    }
                    else
                    {
                    	consoleOut_tLogRow_1 = new java.io.PrintStream(new java.io.BufferedOutputStream(System.out));
                    	globalMap.put("tLogRow_CONSOLE",consoleOut_tLogRow_1);
                    }
                    
                    consoleOut_tLogRow_1.println(util_tLogRow_1.format().toString());
                    consoleOut_tLogRow_1.flush();
//////
globalMap.put("tLogRow_1_NB_LINE",nb_line_tLogRow_1);
                if(log.isInfoEnabled())
            log.info("tLogRow_1 - "  + ("Printed row count: ")  + (nb_line_tLogRow_1)  + (".") );

///////////////////////    			

			 		if(runStat.updateStatAndLog(execStat,enableLogStash,resourceMap,iterateId,"row1",2,0,
			 			"tFileInputDelimited_1","tFileInputDelimited_1","tFileInputDelimited","tLogRow_1","tLogRow_1","tLogRow","output")) {
						talendJobLogProcess(globalMap);
					}
				
 
                if(log.isDebugEnabled())
            log.debug("tLogRow_1 - "  + ("Done.") );

ok_Hash.put("tLogRow_1", true);
end_Hash.put("tLogRow_1", System.currentTimeMillis());




		

/**
 * [tLogRow_1 end ] stop
 */


	
	/**
	 * [tDBOutput_1 end ] start
	 */

	

	
	
	s(currentComponent="tDBOutput_1");
	
			
			
	



				
					try {
						if (batchSizeCounter_tDBOutput_1 != 0) {
							int countSum_tDBOutput_1 = 0;
							
                if(log.isDebugEnabled())
            log.debug("tDBOutput_1 - "  + ("Executing the ")  + ("INSERT")  + (" batch.") );
							for(int countEach_tDBOutput_1: pstmt_tDBOutput_1.executeBatch()) {
								countSum_tDBOutput_1 += (countEach_tDBOutput_1 == java.sql.Statement.EXECUTE_FAILED ? 0 : 1);
							}
							rowsToCommitCount_tDBOutput_1 += countSum_tDBOutput_1;
							
                if(log.isDebugEnabled())
            log.debug("tDBOutput_1 - "  + ("The ")  + ("INSERT")  + (" batch execution has succeeded.") );
							
								insertedCount_tDBOutput_1 += countSum_tDBOutput_1;
							
						}
					}catch (java.sql.BatchUpdateException e){
						globalMap.put(currentComponent+"_ERROR_MESSAGE",e.getMessage());
						
							int countSum_tDBOutput_1 = 0;
							for(int countEach_tDBOutput_1: e.getUpdateCounts()) {
								countSum_tDBOutput_1 += (countEach_tDBOutput_1 < 0 ? 0 : countEach_tDBOutput_1);
							}
							rowsToCommitCount_tDBOutput_1 += countSum_tDBOutput_1;
							
								insertedCount_tDBOutput_1 += countSum_tDBOutput_1;
								
            log.error("tDBOutput_1 - "  + (e.getMessage()) );
							System.err.println(e.getMessage());
							
					}
					batchSizeCounter_tDBOutput_1 = 0;
					
			
		

		if(pstmt_tDBOutput_1 != null) {
			
				pstmt_tDBOutput_1.close();
				resourceMap.remove("pstmt_tDBOutput_1");
			
		}
		
	resourceMap.put("statementClosed_tDBOutput_1", true);
	
			if (commitCounter_tDBOutput_1 > 0 && rowsToCommitCount_tDBOutput_1 != 0) {
				
                if(log.isDebugEnabled())
            log.debug("tDBOutput_1 - "  + ("Connection starting to commit ")  + (rowsToCommitCount_tDBOutput_1)  + (" record(s).") );
			}
			conn_tDBOutput_1.commit();
			if (commitCounter_tDBOutput_1 > 0 && rowsToCommitCount_tDBOutput_1 != 0) {
				
                if(log.isDebugEnabled())
            log.debug("tDBOutput_1 - "  + ("Connection commit has succeeded.") );
				rowsToCommitCount_tDBOutput_1 = 0;
			}
			commitCounter_tDBOutput_1 = 0;
			
		
                if(log.isDebugEnabled())
            log.debug("tDBOutput_1 - "  + ("Closing the connection to the database.") );
		conn_tDBOutput_1 .close();
		
                if(log.isDebugEnabled())
            log.debug("tDBOutput_1 - "  + ("Connection to the database has closed.") );
		resourceMap.put("finish_tDBOutput_1", true);
	

	nb_line_deleted_tDBOutput_1=nb_line_deleted_tDBOutput_1+ deletedCount_tDBOutput_1;
	nb_line_update_tDBOutput_1=nb_line_update_tDBOutput_1 + updatedCount_tDBOutput_1;
	nb_line_inserted_tDBOutput_1=nb_line_inserted_tDBOutput_1 + insertedCount_tDBOutput_1;
	nb_line_rejected_tDBOutput_1=nb_line_rejected_tDBOutput_1 + rejectedCount_tDBOutput_1;
	
        globalMap.put("tDBOutput_1_NB_LINE",nb_line_tDBOutput_1);
        globalMap.put("tDBOutput_1_NB_LINE_UPDATED",nb_line_update_tDBOutput_1);
        globalMap.put("tDBOutput_1_NB_LINE_INSERTED",nb_line_inserted_tDBOutput_1);
        globalMap.put("tDBOutput_1_NB_LINE_DELETED",nb_line_deleted_tDBOutput_1);
        globalMap.put("tDBOutput_1_NB_LINE_REJECTED", nb_line_rejected_tDBOutput_1);
    

	
                if(log.isDebugEnabled())
            log.debug("tDBOutput_1 - "  + ("Has ")  + ("inserted")  + (" ")  + (nb_line_inserted_tDBOutput_1)  + (" record(s).") );

			 		if(runStat.updateStatAndLog(execStat,enableLogStash,resourceMap,iterateId,"row2",2,0,
			 			"tLogRow_1","tLogRow_1","tLogRow","tDBOutput_1","tDBOutput_1","tMysqlOutput","output")) {
						talendJobLogProcess(globalMap);
					}
				
 
                if(log.isDebugEnabled())
            log.debug("tDBOutput_1 - "  + ("Done.") );

ok_Hash.put("tDBOutput_1", true);
end_Hash.put("tDBOutput_1", System.currentTimeMillis());




		

/**
 * [tDBOutput_1 end ] stop
 */







				}//end the resume

				



	
			}catch(java.lang.Exception e){	
				
				    if(!(e instanceof TalendException)){
					   log.fatal(currentComponent + " " + e.getMessage(),e);
					}
				
				TalendException te = new TalendException(e, currentComponent, cLabel, globalMap);
				
				throw te;
			}catch(java.lang.Error error){	
				
					runStat.stopThreadStat();
				
				throw error;
			}finally{
				
				try{
					
	
	/**
	 * [tFileInputDelimited_1 finally ] start
	 */

	

	
	
	s(currentComponent="tFileInputDelimited_1");
	
			
			
	

 



		

/**
 * [tFileInputDelimited_1 finally ] stop
 */


	
	/**
	 * [tLogRow_1 finally ] start
	 */

	

	
	
	s(currentComponent="tLogRow_1");
	
			
			
	

 



		

/**
 * [tLogRow_1 finally ] stop
 */


	
	/**
	 * [tDBOutput_1 finally ] start
	 */

	

	
	
	s(currentComponent="tDBOutput_1");
	
			
			
	



    try {
    if (resourceMap.get("statementClosed_tDBOutput_1") == null) {
                java.sql.PreparedStatement pstmtToClose_tDBOutput_1 = null;
                if ((pstmtToClose_tDBOutput_1 = (java.sql.PreparedStatement) resourceMap.remove("pstmt_tDBOutput_1")) != null) {
                    pstmtToClose_tDBOutput_1.close();
                }
    }
    } finally {
        if(resourceMap.get("finish_tDBOutput_1") == null){
            java.sql.Connection ctn_tDBOutput_1 = null;
            if((ctn_tDBOutput_1 = (java.sql.Connection)resourceMap.get("conn_tDBOutput_1")) != null){
                try {
                if(log.isDebugEnabled())
            log.debug("tDBOutput_1 - "  + ("Closing the connection to the database.") );
                    ctn_tDBOutput_1.close();
                if(log.isDebugEnabled())
            log.debug("tDBOutput_1 - "  + ("Connection to the database has closed.") );
                } catch (java.sql.SQLException sqlEx_tDBOutput_1) {
                    String errorMessage_tDBOutput_1 = "failed to close the connection in tDBOutput_1 :" + sqlEx_tDBOutput_1.getMessage();
            log.error("tDBOutput_1 - "  + (errorMessage_tDBOutput_1) );
                    System.err.println(errorMessage_tDBOutput_1);
                }
            }
        }
    }
 



		

/**
 * [tDBOutput_1 finally ] stop
 */







				}catch(java.lang.Exception e){	
					//ignore
				}catch(java.lang.Error error){
					//ignore
				}
				resourceMap = null;
			}
		

		globalMap.put("tFileInputDelimited_1_SUBPROCESS_STATE", 1);
	}
	


public void talendJobLogProcess(final java.util.Map<String, Object> globalMap) throws TalendException {
	globalMap.put("talendJobLog_SUBPROCESS_STATE", 0);

	final boolean execStat = this.execStat;


	
		String iterateId = "";
	
	
	String currentComponent = "";
	s("none");
	String cLabel =  null;
	java.util.Map<String, Object> resourceMap = new java.util.HashMap<String, Object>();

	try {
			// TDI-39566 avoid throwing an useless Exception
			boolean resumeIt = true;
			if (globalResumeTicket == false && resumeEntryMethodName != null) {
				String currentMethodName = new java.lang.Exception().getStackTrace()[0].getMethodName();
				resumeIt = resumeEntryMethodName.equals(currentMethodName);
			}
			if (resumeIt || globalResumeTicket) { //start the resume
				globalResumeTicket = true;





	
	/**
	 * [talendJobLog begin ] start
	 */

	

	
		
		sh("talendJobLog");
		
	
	s(currentComponent="talendJobLog");
	
			
			
	
		int tos_count_talendJobLog = 0;
		

	for (JobStructureCatcherUtils.JobStructureCatcherMessage jcm : talendJobLog.getMessages()) {
		org.talend.job.audit.JobContextBuilder builder_talendJobLog = org.talend.job.audit.JobContextBuilder.create().jobName(jcm.job_name).jobId(jcm.job_id).jobVersion(jcm.job_version)
			.custom("process_id", jcm.pid).custom("thread_id", jcm.tid).custom("pid", pid).custom("father_pid", fatherPid).custom("root_pid", rootPid);
		org.talend.logging.audit.Context log_context_talendJobLog = null;
		
		
		if(jcm.log_type == JobStructureCatcherUtils.LogType.PERFORMANCE){
			long timeMS = jcm.end_time - jcm.start_time;
			String duration = String.valueOf(timeMS);
			
			log_context_talendJobLog = builder_talendJobLog
				.sourceId(jcm.sourceId).sourceLabel(jcm.sourceLabel).sourceConnectorType(jcm.sourceComponentName)
				.targetId(jcm.targetId).targetLabel(jcm.targetLabel).targetConnectorType(jcm.targetComponentName)
				.connectionName(jcm.current_connector).rows(jcm.row_count).duration(duration).build();
			auditLogger_talendJobLog.flowExecution(log_context_talendJobLog);
		} else if(jcm.log_type == JobStructureCatcherUtils.LogType.JOBSTART) {
			log_context_talendJobLog = builder_talendJobLog.timestamp(jcm.moment).build();
			auditLogger_talendJobLog.jobstart(log_context_talendJobLog);
		} else if(jcm.log_type == JobStructureCatcherUtils.LogType.JOBEND) {
			long timeMS = jcm.end_time - jcm.start_time;
			String duration = String.valueOf(timeMS);
		
			log_context_talendJobLog = builder_talendJobLog
				.timestamp(jcm.moment).duration(duration).status(jcm.status).build();
			auditLogger_talendJobLog.jobstop(log_context_talendJobLog);
		} else if(jcm.log_type == JobStructureCatcherUtils.LogType.RUNCOMPONENT) {
			log_context_talendJobLog = builder_talendJobLog.timestamp(jcm.moment)
				.connectorType(jcm.component_name).connectorId(jcm.component_id).connectorLabel(jcm.component_label).build();
			auditLogger_talendJobLog.runcomponent(log_context_talendJobLog);
		} else if(jcm.log_type == JobStructureCatcherUtils.LogType.FLOWINPUT) {//log current component input line
			long timeMS = jcm.end_time - jcm.start_time;
			String duration = String.valueOf(timeMS);
			
			log_context_talendJobLog = builder_talendJobLog
				.connectorType(jcm.component_name).connectorId(jcm.component_id).connectorLabel(jcm.component_label)
				.connectionName(jcm.current_connector).connectionType(jcm.current_connector_type)
				.rows(jcm.total_row_number).duration(duration).build();
			auditLogger_talendJobLog.flowInput(log_context_talendJobLog);
		} else if(jcm.log_type == JobStructureCatcherUtils.LogType.FLOWOUTPUT) {//log current component output/reject line
			long timeMS = jcm.end_time - jcm.start_time;
			String duration = String.valueOf(timeMS);
			
			log_context_talendJobLog = builder_talendJobLog
				.connectorType(jcm.component_name).connectorId(jcm.component_id).connectorLabel(jcm.component_label)
				.connectionName(jcm.current_connector).connectionType(jcm.current_connector_type)
				.rows(jcm.total_row_number).duration(duration).build();
			auditLogger_talendJobLog.flowOutput(log_context_talendJobLog);
		} else if(jcm.log_type == JobStructureCatcherUtils.LogType.JOBERROR) {
			java.lang.Exception e_talendJobLog = jcm.exception;
			if(e_talendJobLog!=null) {
				try(java.io.StringWriter sw_talendJobLog = new java.io.StringWriter();java.io.PrintWriter pw_talendJobLog = new java.io.PrintWriter(sw_talendJobLog)) {
					e_talendJobLog.printStackTrace(pw_talendJobLog);
					builder_talendJobLog.custom("stacktrace", sw_talendJobLog.getBuffer().substring(0,java.lang.Math.min(sw_talendJobLog.getBuffer().length(), 512)));
				}
			}

			if(jcm.extra_info!=null) {
				builder_talendJobLog.connectorId(jcm.component_id).custom("extra_info", jcm.extra_info);
			}
				
			log_context_talendJobLog = builder_talendJobLog
				.connectorType(jcm.component_id.substring(0, jcm.component_id.lastIndexOf('_')))
				.connectorId(jcm.component_id)
				.connectorLabel(jcm.component_label == null ? jcm.component_id : jcm.component_label).build();

			auditLogger_talendJobLog.exception(log_context_talendJobLog);
		}
		
		
		
	}

 



		

/**
 * [talendJobLog begin ] stop
 */

	
	/**
	 * [talendJobLog main ] start
	 */

	

	
	
	s(currentComponent="talendJobLog");
	
			
			
	

 


	tos_count_talendJobLog++;

		

/**
 * [talendJobLog main ] stop
 */

	
	/**
	 * [talendJobLog process_data_begin ] start
	 */

	

	
	
	s(currentComponent="talendJobLog");
	
			
			
	

 



		

/**
 * [talendJobLog process_data_begin ] stop
 */

	
	/**
	 * [talendJobLog process_data_end ] start
	 */

	

	
	
	s(currentComponent="talendJobLog");
	
			
			
	

 



		

/**
 * [talendJobLog process_data_end ] stop
 */

	
	/**
	 * [talendJobLog end ] start
	 */

	

	
	
	s(currentComponent="talendJobLog");
	
			
			
	

 

ok_Hash.put("talendJobLog", true);
end_Hash.put("talendJobLog", System.currentTimeMillis());




		

/**
 * [talendJobLog end ] stop
 */

				}//end the resume

				



	
			}catch(java.lang.Exception e){	
				
				    if(!(e instanceof TalendException)){
					   log.fatal(currentComponent + " " + e.getMessage(),e);
					}
				
				TalendException te = new TalendException(e, currentComponent, cLabel, globalMap);
				
				throw te;
			}catch(java.lang.Error error){	
				
					runStat.stopThreadStat();
				
				throw error;
			}finally{
				
				try{
					
	
	/**
	 * [talendJobLog finally ] start
	 */

	

	
	
	s(currentComponent="talendJobLog");
	
			
			
	

 



		

/**
 * [talendJobLog finally ] stop
 */

				}catch(java.lang.Exception e){	
					//ignore
				}catch(java.lang.Error error){
					//ignore
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
            java.util.Map<String,String> threadRunResultMap = new java.util.HashMap<String, String>();
            threadRunResultMap.put("errorCode", null);
            threadRunResultMap.put("status", "");
            return threadRunResultMap;
        };
    };


    protected PropertiesWithType context_param = new PropertiesWithType();
    public java.util.Map<String, Object> parentContextMap = new java.util.HashMap<String, Object>();

    public String status= "";
    
    
    private final static java.util.Properties jobInfo = new java.util.Properties();
    private final static java.util.Map<String,String> mdcInfo = new java.util.HashMap<>();
    private final static java.util.concurrent.atomic.AtomicLong subJobPidCounter = new java.util.concurrent.atomic.AtomicLong();


    public static void main(String[] args){
        final file_to_db file_to_dbClass = new file_to_db();

        int exitCode = file_to_dbClass.runJobInTOS(args);
	        if(exitCode==0){
		        log.info("TalendJob: 'file_to_db' - Done.");
	        }

        System.exit(exitCode);
    }
	

	
	
	private void getjobInfo() {
		final String TEMPLATE_PATH = "src/main/templates/jobInfo_template.properties";
		final String BUILD_PATH = "../jobInfo.properties";
		final String path = this.getClass().getResource("").getPath();
		if(path.lastIndexOf("target") > 0) {
			final java.io.File templateFile = new java.io.File(
					path.substring(0, path.lastIndexOf("target")).concat(TEMPLATE_PATH));
			if (templateFile.exists()) {
				readJobInfo(templateFile);
				return;
			}
		}
			readJobInfo(new java.io.File(BUILD_PATH));
	}

    private void readJobInfo(java.io.File jobInfoFile){
	
        if(jobInfoFile.exists()) {
            try (java.io.InputStream is = new java.io.FileInputStream(jobInfoFile)) {
            	jobInfo.load(is);
            } catch (IOException e) {
            	 
                log.debug("Read jobInfo.properties file fail: " + e.getMessage());
                
            }
        }
		log.info(String.format("Project name: %s\tJob name: %s\tGIT Commit ID: %s\tTalend Version: %s",
				projectName,jobName,jobInfo.getProperty("gitCommitId"), "8.0.1.20250730_0900-patch"));
		
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
        if(org.talend.metrics.CBPClient.getInstanceForCurrentVM() == null) {
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

	        if(!"".equals(log4jLevel)){
	        	
				
				
				if("trace".equalsIgnoreCase(log4jLevel)){
					org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(), org.apache.logging.log4j.Level.TRACE);
				}else if("debug".equalsIgnoreCase(log4jLevel)){
					org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(), org.apache.logging.log4j.Level.DEBUG);
				}else if("info".equalsIgnoreCase(log4jLevel)){
					org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(), org.apache.logging.log4j.Level.INFO);
				}else if("warn".equalsIgnoreCase(log4jLevel)){
					org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(), org.apache.logging.log4j.Level.WARN);
				}else if("error".equalsIgnoreCase(log4jLevel)){
					org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(), org.apache.logging.log4j.Level.ERROR);
				}else if("fatal".equalsIgnoreCase(log4jLevel)){
					org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(), org.apache.logging.log4j.Level.FATAL);
				}else if ("off".equalsIgnoreCase(log4jLevel)){
					org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(), org.apache.logging.log4j.Level.OFF);
				}
				org.apache.logging.log4j.core.config.Configurator.setLevel(org.apache.logging.log4j.LogManager.getRootLogger().getName(), log.getLevel());
				
			}

	        getjobInfo();
			log.info("TalendJob: 'file_to_db' - Start.");
		

                java.util.Set<Object> jobInfoKeys = jobInfo.keySet();
                for(Object jobInfoKey: jobInfoKeys) {
                    org.slf4j.MDC.put("_" + jobInfoKey.toString(), jobInfo.get(jobInfoKey).toString());
                }
                org.slf4j.MDC.put("_pid", pid);
                org.slf4j.MDC.put("_rootPid", rootPid);
                org.slf4j.MDC.put("_fatherPid", fatherPid);
                org.slf4j.MDC.put("_projectName", projectName);
                org.slf4j.MDC.put("_startTimestamp",java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC ).format( java.time.format.DateTimeFormatter.ISO_INSTANT ));
                org.slf4j.MDC.put("_jobRepositoryId","_J_fWgI75EfC-J65at5wWMg");
                org.slf4j.MDC.put("_compiledAtTimestamp","2025-09-11T10:43:45.051110400Z");

                java.lang.management.RuntimeMXBean mx = java.lang.management.ManagementFactory.getRuntimeMXBean();
                String[] mxNameTable = mx.getName().split("@"); //$NON-NLS-1$
                if (mxNameTable.length == 2) {
                    org.slf4j.MDC.put("_systemPid", mxNameTable[0]);
                } else {
                    org.slf4j.MDC.put("_systemPid", String.valueOf(java.lang.Thread.currentThread().getId()));
                }

		
		
			if(enableLogStash) {
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

				System.getProperties().stringPropertyNames().stream()
					.filter(it -> it.startsWith("audit.logger."))
					.forEach(key -> properties_talendJobLog.setProperty(key.substring("audit.logger.".length()), System.getProperty(key)));

				
				
				
				org.apache.logging.log4j.core.config.Configurator.setLevel(properties_talendJobLog.getProperty("root.logger"), org.apache.logging.log4j.Level.DEBUG);
				
				auditLogger_talendJobLog = org.talend.job.audit.JobEventAuditLoggerFactory.createJobAuditLogger(properties_talendJobLog);
			}
		

        if(clientHost == null) {
            clientHost = defaultClientHost;
        }

        if(pid == null || "0".equals(pid)) {
            pid = TalendString.getAsciiRandomString(6);
        }

            org.slf4j.MDC.put("_pid", pid);

        if (rootPid==null) {
            rootPid = pid;
        }

            org.slf4j.MDC.put("_rootPid", rootPid);

        if (fatherPid==null) {
            fatherPid = pid;
        }else{
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
                    contextStr = (String)jobProperties.get("context");
                }

                if (jobProperties != null && jobProperties.get("taskExecutionId") != null) {
                    taskExecutionId = (String)jobProperties.get("taskExecutionId");
                }

                // extract ids from parent route
                if(null == taskExecutionId || taskExecutionId.isEmpty()){
                    for(String arg : args) {
                        if(arg.startsWith("--context_param")
                                && (arg.contains("taskExecutionId") || arg.contains("jobExecutionId"))){

                            String keyValue = arg.replace("--context_param", "");
                            String[] parts = keyValue.split("=");
                            String[] cleanParts = java.util.Arrays.stream(parts)
                                    .filter(s -> !s.isEmpty())
                                    .toArray(String[]::new);
                            if (cleanParts.length == 2) {
                                String key = cleanParts[0];
                                String value = cleanParts[1];
                                if ("taskExecutionId".equals(key.trim()) && null != value) {
                                    taskExecutionId = value.trim();
                                }else if ("jobExecutionId".equals(key.trim()) && null != value) {
                                    jobExecutionId = value.trim();
                                }
                            }
                        }
                    }
                }
            }

            // first load default key-value pairs from application.properties
            if(isStandaloneMS) {
                context.putAll(this.getDefaultProperties());
            }
            //call job/subjob with an existing context, like: --context=production. if without this parameter, there will use the default context instead.
            java.io.InputStream inContext = file_to_db.class.getClassLoader().getResourceAsStream("talend/file_to_db_0_1/contexts/" + contextStr + ".properties");
            if (inContext == null) {
                inContext = file_to_db.class.getClassLoader().getResourceAsStream("config/contexts/" + contextStr + ".properties");
            }
            if (inContext != null) {
                try {
                    //defaultProps is in order to keep the original context value
                    if(context != null && context.isEmpty()) {
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
                    if(isStandaloneMS) {
                        // override context key-value pairs if provided using --context=contextName
                        defaultProps.load(inContext);
                        context.putAll(defaultProps);
                    }
                } finally {
                    inContext.close();
                }
            } else if (!isDefaultContext) {
                //print info and job continue to run, for case: context_param is not empty.
                System.err.println("Could not find the context " + contextStr);
            }
            // override key-value pairs if provided via --config.location=file1.file2 OR --config.additional-location=file1,file2
            if(isStandaloneMS) {
                context.putAll(this.getAdditionalProperties());
            }
            
            // override key-value pairs if provide via command line like --key1=value1,--key2=value2
            if(!context_param.isEmpty()) {
                context.putAll(context_param);
				//set types for params from parentJobs
				for (Object key: context_param.keySet()){
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
            System.err.println("Could not load context "+contextStr);
            ie.printStackTrace();
        }

        // get context value from parent directly
        if (parentContextMap != null && !parentContextMap.isEmpty()) {
        }

        //Resume: init the resumeUtil
        resumeEntryMethodName = ResumeUtil.getResumeEntryMethodName(resuming_checkpoint_path);
        resumeUtil = new ResumeUtil(resuming_logs_dir_path, isChildJob, rootPid);
        resumeUtil.initCommonInfo(pid, rootPid, fatherPid, projectName, jobName, contextStr, jobVersion);

		List<String> parametersToEncrypt = new java.util.ArrayList<String>();
        //Resume: jobStart
        resumeUtil.addLog("JOB_STARTED", "JOB:" + jobName, parent_part_launcher, Thread.currentThread().getId() + "", "","","","",resumeUtil.convertToJsonText(context,ContextProperties.class,parametersToEncrypt));

            org.slf4j.MDC.put("_context", contextStr);
            log.info("TalendJob: 'file_to_db' - Started.");
            java.util.Optional.ofNullable(org.slf4j.MDC.getCopyOfContextMap()).ifPresent(mdcInfo::putAll);

if(execStat) {
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


this.globalResumeTicket = true;//to run tPreJob




		if(enableLogStash) {
	        talendJobLog.addJobStartMessage();
	        try {
	            talendJobLogProcess(globalMap);
	        } catch (java.lang.Exception e) {
	            e.printStackTrace();
	        }
        }

this.globalResumeTicket = false;//to run others jobs

try {
errorCode = null;tFileInputDelimited_1Process(globalMap);
if(!"failure".equals(status)) { status = "end"; }
}catch (TalendException e_tFileInputDelimited_1) {
globalMap.put("tFileInputDelimited_1_SUBPROCESS_STATE", -1);

e_tFileInputDelimited_1.printStackTrace();

}

this.globalResumeTicket = true;//to run tPostJob




        end = System.currentTimeMillis();

        if (watch) {
            System.out.println((end-startTime)+" milliseconds");
        }

        endUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        if (false) {
            System.out.println((endUsedMemory - startUsedMemory) + " bytes memory increase when running : file_to_db");
        }
		if(enableLogStash) {
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
    if(org.talend.metrics.CBPClient.getInstanceForCurrentVM() != null) {
        s("none");
        org.talend.metrics.CBPClient.getInstanceForCurrentVM().sendData();
    }
    }
    

    int returnCode = 0;


    if(errorCode == null) {
         returnCode = status != null && status.equals("failure") ? 1 : 0;
    } else {
         returnCode = errorCode.intValue();
    }
    resumeUtil.addLog("JOB_ENDED", "JOB:" + jobName, parent_part_launcher, Thread.currentThread().getId() + "", "","" + returnCode,"","","");
    resumeUtil.flush();


        org.slf4j.MDC.remove("_subJobName");
        org.slf4j.MDC.remove("_subJobPid");
        org.slf4j.MDC.remove("_systemPid");
        log.info("TalendJob: 'file_to_db' - Finished - status: " + status + " returnCode: " + returnCode );

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
                if (fatherPid==null) {
                    context_param.setContextType(keyValue.substring(0, index), replaceEscapeChars(keyValue.substring(index + 1)));
                } else { // the subjob won't escape the especial chars
                    context_param.setContextType(keyValue.substring(0, index), keyValue.substring(index + 1) );
                }

            }

		} else if (arg.startsWith("--context_param")) {
            String keyValue = arg.substring(16);
            int index = -1;
            if (keyValue != null && (index = keyValue.indexOf('=')) > -1) {
                if (fatherPid==null) {
                    context_param.put(keyValue.substring(0, index), replaceEscapeChars(keyValue.substring(index + 1)));
                } else { // the subjob won't escape the especial chars
                    context_param.put(keyValue.substring(0, index), keyValue.substring(index + 1) );
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
                    if ( (index = line.indexOf('=')) > -1) {
							if (line.startsWith("--context_param")) {
								if ("id_Password".equals(context_param.getContextType(line.substring(16, index)))) {
									context_param.put(line.substring(16, index), routines.system.PasswordEncryptUtil.decryptPassword(
											line.substring(index + 1)));
								} else {
									context_param.put(line.substring(16, index), line.substring(index + 1));
								}
							}else {//--context_type
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
		} else if (arg.startsWith("--audit.enabled") && arg.contains("=")) {//for trunjob call
		    final int equal = arg.indexOf('=');
			final String key = arg.substring("--".length(), equal);
			System.setProperty(key, arg.substring(equal + 1));
		}
    }
    
    private static final String NULL_VALUE_EXPRESSION_IN_COMMAND_STRING_FOR_CHILD_JOB_ONLY = "<TALEND_NULL>";

    private final String[][] escapeChars = {
        {"\\\\","\\"},{"\\n","\n"},{"\\'","\'"},{"\\r","\r"},
        {"\\f","\f"},{"\\b","\b"},{"\\t","\t"}
        };
    private String replaceEscapeChars (String keyValue) {

		if (keyValue == null || ("").equals(keyValue.trim())) {
			return keyValue;
		}

		StringBuilder result = new StringBuilder();
		int currIndex = 0;
		while (currIndex < keyValue.length()) {
			int index = -1;
			// judege if the left string includes escape chars
			for (String[] strArray : escapeChars) {
				index = keyValue.indexOf(strArray[0],currIndex);
				if (index>=0) {

					result.append(keyValue.substring(currIndex, index + strArray[0].length()).replace(strArray[0], strArray[1]));
					currIndex = index + strArray[0].length();
					break;
				}
			}
			// if the left string doesn't include escape chars, append the left into the result
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
 *     145425 characters generated by Talend Cloud Data Management Platform 
 *     on the September 11, 2025 at 4:13:45 PM IST
 ************************************************************************************************/