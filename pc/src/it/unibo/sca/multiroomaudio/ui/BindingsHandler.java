package it.unibo.sca.multiroomaudio.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.cef.browser.CefBrowser;

import it.unibo.sca.multiroomaudio.ui.ParamDTO.TypeNotSupportedException;

public class BindingsHandler {

    private final Class<?> clazz;
    private Object classInstance;
    private final String interfaceName;

    public BindingsHandler(Object object, String interfaceName) {
        this.clazz = object.getClass();
        this.interfaceName = interfaceName;
        classInstance = object;
    }
    
    public ParamDTO callMethod(MethodDTO method){
        try {
            Method classMethod = clazz.getMethod(method.name, method.getParamsTypes());
            ParamDTO retPar = new ParamDTO(classMethod.getReturnType().getSimpleName());
            retPar.setData( classMethod.invoke(classInstance, method.getParams()));
            return retPar;
        } catch (IllegalAccessException | InvocationTargetException | TypeNotSupportedException |
                    NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            return new ParamDTO("Error");
        }
    }

    public void injectJavascriptInterface(CefBrowser browser){
        StringBuilder sb = new StringBuilder();

        sb.append("var " + interfaceName + " = {" +
        "parseReturn(data){" +
            "const obj = JSON.parse(data);" +
            "if(obj.type === \"int\" || obj.type === \"Integer\"){" +
                "return parseInt(obj.data);" +
            "} else if(obj.type === \"float\" || obj.type === \"Float\"){" +
                "return parseFloat(obj.data);" +
            "} else if(obj.type === \"boolean\" || obj.type === \"Boolean\"){" +
                "return obj.data === 'true';"+
            "} else if(obj.type === \"void\"){" +
                "return;"+
            "} else "+
                "return obj.data;"+
        "},");
        Method[] classMethods = (Method[]) Arrays.stream(clazz.getMethods())
                                        .filter(m -> m.getAnnotation(JavascriptInterface.class) != null).toArray(Method[]::new);

        for(int k=0; k<classMethods.length; k++){

            StringBuilder innerSb = new StringBuilder();

            innerSb.append( classMethods[k].getName() + "(");

            for(int i = 0; i < classMethods[k].getParameters().length; i++){
                innerSb.append( classMethods[k].getParameters()[i].getName() );
                if(i + 1 < classMethods[k].getParameters().length){
                    innerSb.append(",");
                }
            }

            innerSb.append(             "){" +
                                "return new Promise((resolve, reject) => {" +
                                    "cefQuery({" +
                                        "request: JSON.stringify({" +
                                            "name: \"" + classMethods[k].getName() + "\"," +
                                            "params: [");
            for(int i = 0; i < classMethods[k].getParameters().length; i++){
                innerSb.append("{ data: " + classMethods[k].getParameters()[i].getName() + "," +
                                "type: \"" + classMethods[k].getParameters()[i].getType().getSimpleName() + "\"}");
                if(i + 1 < classMethods[k].getParameters().length){
                    innerSb.append(",");
                }
            }
            innerSb.append(             "]})," +
                                        "onSuccess: (response) => resolve(" + interfaceName + ".parseReturn(response))," +
                                        "onFailure: (response) => reject(response)" +
                                    "})" +
                                "})" +
                            "}");
            if(k<(classMethods.length - 1)){
                innerSb.append(",");
            }
            sb.append(innerSb.toString());
        }
        sb.append("}");
        browser.executeJavaScript(sb.toString(), browser.getURL(), 0);
        //System.out.println(sb.toString());
    }

}
