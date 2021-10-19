package it.unibo.sca.multiroomaudio.ui;

import it.unibo.sca.multiroomaudio.ui.ParamDTO.TypeNotSupportedException;

public class MethodDTO {
    public String name;
    public ParamDTO[] params;

    public MethodDTO(){}

    public MethodDTO(String name, ParamDTO[] params) {
        this.name = name;
        this.params = params;
    }

    public Class<?>[] getParamsTypes() throws TypeNotSupportedException {
        Class<?> res[] = new Class[params.length];
        for(int i = 0; i < params.length; i++){
            res[i] = params[i].getType();
        }
        return res;
    }

    public Object[] getParams() throws TypeNotSupportedException {
        Object res[] = new Object[params.length];
        for(int i = 0; i < params.length; i++)
            res[i] = params[i].getData();
        return res;
    }

}
