package com.cyberai.entity;

public class DubboRequestBO {

    String className;
    String methodName;
    String paramsClassName;
    String params;
    String group;
    String version;
    String address;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getParamsClassName() {
        return paramsClassName;
    }

    public void setParamsClassName(String paramsClassName) {
        this.paramsClassName = paramsClassName;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "DubboRequestBO{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", paramsClassName='" + paramsClassName + '\'' +
                ", params='" + params + '\'' +
                ", group='" + group + '\'' +
                ", version='" + version + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
