package org.dol.database.utils;


import org.dol.database.schema.TableSchema;

/**
 * The Class NameUtil.
 */
public class NameUtil {

    /**
     * Gets the biz class name.
     *
     * @param tableSchema the table schema
     * @return the biz class name
     */
    public static String getBizClassName(TableSchema tableSchema) {
        return tableSchema.getModelName() + "Biz";
    }

    public static String getPageContextName(TableSchema tableSchema) {
        return tableSchema.getModelName() + "Context";
    }

    /**
     * Gets the biz var name.
     *
     * @param tableSchema the table schema
     * @return the biz var name
     */
    public static String getBizVarName(TableSchema tableSchema) {
        return Utils.uncapitalize(getBizClassName(tableSchema));
    }

    /**
     * Gets the controller class name.
     *
     * @param tableSchema the table schema
     * @return the controller class name
     */
    // ---
    public static String getControllerClassName(TableSchema tableSchema) {
        return tableSchema.getModelName() + "Controller";
    }

    /**
     * Gets the controller var name.
     *
     * @param tableSchema the table schema
     * @return the controller var name
     */
    public static String getControllerVarName(TableSchema tableSchema) {
        return Utils.uncapitalize(getControllerClassName(tableSchema));
    }

    /**
     * Gets the converter class name.
     *
     * @param tableSchema the table schema
     * @return the converter class name
     */
    public static String getConverterClassName(TableSchema tableSchema) {
        return tableSchema.getModelName() + "Converter";
    }

    /**
     * Gets the converter var name.
     *
     * @param tableSchema the table schema
     * @return the converter var name
     */
    public static String getConverterVarName(TableSchema tableSchema) {
        return Utils.uncapitalize(getConverterClassName(tableSchema));
    }


    /**
     * Gets the mapper class name.
     *
     * @param tableSchema the table schema
     * @return the mapper class name
     */
    public static String getMapperClassName(TableSchema tableSchema) {
        return tableSchema.getModelName() + "Mapper";
    }

    /**
     * Gets the master mapper name.
     *
     * @param tableSchema the table schema
     * @return the master mapper name
     */
    public static String getMasterMapperName(TableSchema tableSchema) {
        return "Master" + tableSchema.getModelName() + "Mapper";
    }

    public static String getMapperVarName(TableSchema tableSchema) {
        return Utils.uncapitalize(getMapperClassName(tableSchema));
    }

    public static String getVarName(String name) {
        return Utils.uncapitalize(name);
    }

    /**
     * Gets the master mapper var name.
     *
     * @param tableSchema the table schema
     * @return the master mapper var name
     */
    public static String getMasterMapperVarName(TableSchema tableSchema) {
        return Utils.uncapitalize(getMasterMapperName(tableSchema));
    }

    /**
     * Gets the message class name.
     *
     * @param tableSchema the table schema
     * @return the message class name
     */
    public static String getMessageClassName(TableSchema tableSchema) {
        return tableSchema.getModelName();
    }

    /**
     * Gets the message query class name.
     *
     * @param tableSchema the table schema
     * @return the message query class name
     */
    public static String getMessageQueryClassName(TableSchema tableSchema) {
        /* return tableSchema.getModelName() + "QueryInfo";*/
        return getModelQueryClassName(tableSchema);
    }

    /**
     * Gets the message query var name.
     *
     * @param tableSchema the table schema
     * @return the message query var name
     */
    public static String getMessageQueryVarName(TableSchema tableSchema) {
        /* return Utils.uncapitalize(getMessageQueryClassName(tableSchema));*/
        return getModelQueryVarName(tableSchema);
    }

    /**
     * Gets the message var name.
     *
     * @param tableSchema the table schema
     * @return the message var name
     */
    public static String getMessageVarName(TableSchema tableSchema) {
        return Utils.uncapitalize(getMessageClassName(tableSchema));
    }

    /**
     * Gets the model class name.
     *
     * @param tableSchema the table schema
     * @return the model class name
     */
    public static String getModelClassName(TableSchema tableSchema) {
        return tableSchema.getModelName();
    }

    /**
     * Gets the model query class name.
     *
     * @param tableSchema the table schema
     * @return the model query class name
     */
    public static String getModelQueryClassName(TableSchema tableSchema) {
        return tableSchema.getModelName() + "Query";
    }

    /**
     * Gets the model query var name.
     *
     * @param tableSchema the table schema
     * @return the model query var name
     */
    public static String getModelQueryVarName(TableSchema tableSchema) {
        return Utils.uncapitalize(getModelQueryClassName(tableSchema));
    }

    /**
     * Gets the model var name.
     *
     * @param tableSchema the table schema
     * @return the model var name
     */
    public static String getModelVarName(TableSchema tableSchema) {
        return Utils.uncapitalize(getModelClassName(tableSchema));
    }

    public static String getViewName(TableSchema tableSchema) {
        String name = tableSchema.nameWithoutPrefix();
        return name.toLowerCase().replaceAll("_", "-");
    }

    /**
     * Gets the service class name.
     *
     * @param tableSchema the table schema
     * @return the service class name
     */
    public static String getServiceClassName(TableSchema tableSchema) {
        return tableSchema.getModelName() + "Service";
    }

    /**
     * Gets the service impl class name.
     *
     * @param tableSchema the table schema
     * @return the service impl class name
     */
    public static String getServiceImplClassName(TableSchema tableSchema) {
        return getServiceClassName(tableSchema) + "Impl";
    }

    /**
     * Gets the service var name.
     *
     * @param tableSchema the table schema
     * @return the service var name
     */
    public static String getServiceVarName(TableSchema tableSchema) {
        return Utils.uncapitalize(getServiceClassName(tableSchema));
    }

    /**
     * Gets the slave mapper name.
     *
     * @param tableSchema the table schema
     * @return the slave mapper name
     */
    public static String getSlaveMapperName(TableSchema tableSchema) {
        return "Slave" + tableSchema.getModelName() + "Mapper";
    }

    /**
     * Gets the slave mapper var name.
     *
     * @param tableSchema the table schema
     * @return the slave mapper var name
     */
    public static String getSlaveMapperVarName(TableSchema tableSchema) {
        return Utils.uncapitalize(getSlaveMapperName(tableSchema));
    }

    /**
     * Gets the test class name.
     *
     * @param tableSchema the table schema
     * @return the test class name
     */
    // --
    public static String getTestClassName(TableSchema tableSchema) {
        return tableSchema.getModelName() + "Test";
    }

    /**
     * Gets the test var name.
     *
     * @param tableSchema the table schema
     * @return the test var name
     */
    public static String getTestVarName(TableSchema tableSchema) {
        return Utils.uncapitalize(getTestClassName(tableSchema));
    }

    public static String capitalize(String name) {
        return Utils.capitalize(name);
    }
}
