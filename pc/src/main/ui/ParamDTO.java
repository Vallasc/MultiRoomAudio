package main.ui;

import com.fasterxml.jackson.annotation.*;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, 
                getterVisibility = JsonAutoDetect.Visibility.NONE, 
                setterVisibility = JsonAutoDetect.Visibility.NONE)
public class ParamDTO {
    public String data;
    public String type;

    public ParamDTO() {}

    public ParamDTO(String type) {
        this.type = type;
    }

    public Class<?> getType() throws TypeNotSupportedException{
        if(type.equals("int") || type.equals("Integer")) {
            return Integer.TYPE;
        } else if(type.equals("float") || type.equals("Float")) {
            return Float.TYPE;
        } else if(type.equals("boolean") || type.equals("Boolean")) {
            return Boolean.TYPE;
        } else if(type.equals("String")) {
            return String.class;
        } else {
            throw new TypeNotSupportedException();
        }
    }

    public Object getData() throws TypeNotSupportedException{
        if(type.equals("int") || type.equals("Integer")) {
            return Integer.valueOf(data);
        } else if(type.equals("float") || type.equals("Float")) {
            return Float.valueOf(data);
        } else if(type.equals("boolean") || type.equals("Boolean")) {
            return Boolean.valueOf(data);
        } else if(type.equals("String")) {
            return data;
        } else {
            throw new TypeNotSupportedException();
        }
    }

    public void setData(Object data) throws TypeNotSupportedException{
        this.data = String.valueOf(data);
    }
    
    public class TypeNotSupportedException extends Exception {}
}
